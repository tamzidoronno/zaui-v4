package com.thundashop.services.zauiactivityservice;

import com.thundashop.core.gotohub.constant.GotoConstants;
import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsOrderCreateRow;
import com.thundashop.core.pmsmanager.PmsOrderCreateRowItemLine;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.repository.exceptions.NotUniqueDataException;
import com.thundashop.core.common.ZauiException;
import com.thundashop.repository.pmsbookingrepository.IPmsBookingRepository;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.core.common.ZauiStatusCodes;
import com.thundashop.repository.zauiactivityrepository.IZauiActivityConfigRepository;
import com.thundashop.repository.zauiactivityrepository.IZauiActivityRepository;
import com.thundashop.services.octoapiservice.IOctoApiService;
import com.thundashop.services.validatorservice.IZauiActivityValidationService;
import com.thundashop.zauiactivity.constant.ZauiConstants;
import com.thundashop.zauiactivity.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
public class ZauiActivityService implements IZauiActivityService {
    @Autowired
    private IZauiActivityConfigRepository zauiActivityConfigRepository;
    @Autowired
    private IZauiActivityRepository zauiActivityRepository;
    @Autowired
    private IZauiActivityValidationService zauiActivityValidationService;
    @Autowired
    private IPmsBookingRepository pmsBookingRepository;
    @Autowired
    private IOctoApiService octoApiService;

    public ZauiActivityConfig getZauiActivityConfig(SessionInfo sessionInfo) throws NotUniqueDataException {
        try {
            return zauiActivityConfigRepository.getZauiActivityConfig(sessionInfo).orElse(new ZauiActivityConfig());
        } catch (Exception ex) {
            log.error("Failed to get zaui activity config. Reason: {}, Actual Exception: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    public ZauiActivityConfig setZauiActivityConfig(ZauiActivityConfig zauiActivityConfig, SessionInfo sessionInfo) {
        return zauiActivityConfigRepository.save(zauiActivityConfig, sessionInfo);
    }

    public List<ZauiActivity> getAllZauiActivities(SessionInfo sessionInfo) {
        return zauiActivityRepository.getAll(sessionInfo);
    }

    public List<ZauiActivity> getZauiActivities(SessionInfo sessionInfo) throws ZauiException {
        try {
            List<Integer> supplierIds = getZauiActivityConfig(sessionInfo).getConnectedSuppliers().stream()
                    .map(ZauiConnectedSupplier::getId).collect(Collectors.toList());
            return zauiActivityRepository.getAll(sessionInfo).stream()
                    .filter(activity -> supplierIds.contains(activity.getSupplierId())).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get zaui activities. Reason: {}. Actual error: {}", e.getMessage(), e);
            throw new ZauiException(ZauiStatusCodes.ACTIVITY_NOT_FOUND);
        }
    }

    @Override
    public Optional<ZauiActivity> getZauiActivityById(String Id, SessionInfo sessionInfo) {
        return zauiActivityRepository.getById(Id, sessionInfo);
    }

    @Override
    public ZauiActivity getZauiActivityByOptionId(String optionId, SessionInfo sessionInfo) {
        return zauiActivityRepository.getByOptionId(optionId, sessionInfo);
    }

    public void fetchZauiActivities(SessionInfo sessionInfo, ZauiActivityConfig zauiActivityConfig, String currency) {
        if (zauiActivityConfig == null || zauiActivityConfig.getConnectedSuppliers() == null
                || zauiActivityConfig.getConnectedSuppliers().size() < 1) {
            log.info("ZauiActivitySyncLog: Zaui activity feature enabled but no supplier is connected.");
            return;
        }
        log.info("ZauiActivitySyncLog: Octo products sync process starting...");
        zauiActivityConfig.getConnectedSuppliers().forEach(supplier -> {
            try {
                log.info("ZauiActivitySyncLog: Octo products sync process starting for supplier {}", supplier.getId());
                List<OctoProduct> octoProducts = octoApiService.getOctoProducts(supplier.getId());

                List<Integer> octoProductIds = octoProducts.stream().map(OctoProduct::getId)
                        .collect(Collectors.toList());
                List<String> removingActivityIds = getAllZauiActivities(sessionInfo).stream()
                        .filter(activity -> activity.getSupplierId() == supplier.getId()
                                && !octoProductIds.contains(activity.getProductId()))
                        .map(activity -> activity.id).collect(Collectors.toList());
                zauiActivityRepository.markDeleted(removingActivityIds, sessionInfo);
                syncZauiActivities(octoProducts, supplier, currency, sessionInfo);
                log.info("ZauiActivitySyncLog: Octo products sync process completed for supplier {}", supplier.getId());
            } catch (Exception e) {
                log.error(
                        "ZauiActivitySyncLog: Octo products sync process failed for supplier {}. Reason: {}, Actual error: {}",
                        supplier.getId(), e.getMessage(), e);
            }
        });
        log.info("ZauiActivitySyncLog: Octo products sync process completed...");
    }

    @Override
    public PmsBooking addActivityToBooking(BookingZauiActivityItem activityItem, PmsBooking booking, User booker, ZauiActivityConfig config,
            SessionInfo sessionInfo)
            throws ZauiException {
        if (activityItem.getUnits() == null || activityItem.getUnits().isEmpty())
            throw new ZauiException(ZauiStatusCodes.MISSING_PARAMS);
        if (activityItem.getOctoBooking() == null) {
            OctoBooking octoReservedBooking = reserveOctoBooking(activityItem, config);
            activityItem.setOctoBooking(octoReservedBooking);
        }
        OctoBooking octoConfirmedBooking = confirmOctoBooking(activityItem, booking, booker, config);
        booking = addActivityToPmsBooking(activityItem, octoConfirmedBooking, booking, sessionInfo);
        return booking;
    }

    @Override
    public PmsBooking addActivityToPmsBooking(BookingZauiActivityItem activityItem, OctoBooking octoBooking,
            PmsBooking booking, SessionInfo sessionInfo) throws ZauiException {
        if (activityItem.getUnits() == null || activityItem.getUnits().isEmpty())
            throw new ZauiException(ZauiStatusCodes.MISSING_PARAMS);
        activityItem.setOctoBooking(octoBooking);
        activityItem.price = getPricingFromOctoTaxObject(activityItem.getOctoBooking().getPricing()).getTotal();
        activityItem.priceExTaxes = getPricingFromOctoTaxObject(activityItem.getOctoBooking().getPricing())
                .getSubtotal();
        activityItem.setUnpaidAmount(activityItem.price);
        try {
            List<Double> taxRate = octoBooking.getPricing().getIncludedTaxes().stream()
                    .map(taxData -> new Double(taxData.getRate())).collect(Collectors.toList());
            zauiActivityValidationService.validateTaxRates(activityItem.getSupplierId(), taxRate, sessionInfo);
        } catch (GotoException e) {
            log.error("Tax Validation Failed, error message: {}, actual error: {}", e.getMessage(), e);
            throw new ZauiException(ZauiStatusCodes.ACCOUNTING_ERROR);
        }

        int itemIndex = IntStream.range(0, booking.bookingZauiActivityItems.size())
                .filter(i -> booking.bookingZauiActivityItems.get(i).getId().equals(activityItem.getId()))
                .findFirst()
                .orElse(-1);

        if (itemIndex == -1) {
            booking.bookingZauiActivityItems.add(activityItem);
            log.info("activity added to booking {}", activityItem);
        } else {
            booking.bookingZauiActivityItems.set(itemIndex, activityItem);
            log.info("activity confirmed to booking {}", activityItem);
        }
        return booking;
    }

    @Override
    public PmsBooking addActivityToWebBooking(AddZauiActivityToWebBookingDto activity, PmsBooking booking, ZauiActivityConfig config,
            SessionInfo sessionInfo) throws ZauiException {
        ZauiActivity zauiActivity = getZauiActivityByOptionId(activity.getOptionId(), sessionInfo);
        ActivityOption bookedOption = zauiActivity.getActivityOptionList().stream()
                .filter(option -> option.getId().equals(activity.getOptionId()))
                .findFirst().orElse(null);
        if (bookedOption == null) {
            throw new ZauiException(ZauiStatusCodes.ACTIVITY_NOT_FOUND);
        }
        OctoBookingReserveRequest bookingReserveRequest = new OctoBookingReserveRequest()
                .setProductId(zauiActivity.getProductId())
                .setOptionId(bookedOption.getId())
                .setAvailabilityId(activity.getAvailabilityId())
                .setNotes(ZauiConstants.ZAUI_STAY_TAG)
                .setUnitItems(mapUnitsForBooking(activity.getUnits()));
        OctoBooking octoReserveBooking = octoApiService.reserveBooking(zauiActivity.getSupplierId(),
                bookingReserveRequest, config);
        BookingZauiActivityItem activityItem = mapActivityToBookingZauiActivityItem(octoReserveBooking, sessionInfo);
        activityItem.setUnits(activity.getUnits());
        booking = addActivityToPmsBooking(activityItem, octoReserveBooking, booking, sessionInfo);
        return booking;
    }

    @Override
    public PmsBooking removeActivityFromBooking(String activityItemId, PmsBooking booking) {
        booking.bookingZauiActivityItems.removeIf(item -> item.getId().equals(activityItemId));
        log.info("activity {} removed from booking {}", activityItemId, booking);
        return booking;
    }

    private OctoBooking reserveOctoBooking(BookingZauiActivityItem activityItem, ZauiActivityConfig config) throws ZauiException {
        OctoBookingReserveRequest bookingReserveRequest = new OctoBookingReserveRequest()
                .setProductId(activityItem.getOctoProductId())
                .setOptionId(activityItem.getOptionId())
                .setAvailabilityId(activityItem.getAvailabilityId())
                .setNotes(ZauiConstants.ZAUI_STAY_TAG)
                .setUnitItems(mapUnitsForBooking(activityItem.getUnits()));
        return octoApiService.reserveBooking(activityItem.getSupplierId(), bookingReserveRequest, config);
    }

    @Override
    public OctoBooking confirmOctoBooking(BookingZauiActivityItem activityItem, PmsBooking booking, User booker, ZauiActivityConfig config)
            throws ZauiException {
        OctoConfirmContact contact = new OctoConfirmContact()
                .setFullName(booker.fullName)
                .setEmailAddress(booker.emailAddress)
                .setPhoneNumber(booker.prefix + booker.cellPhone)
                .setCountry(booking.countryCode);
        OctoBookingConfirmRequest confirmRequest = new OctoBookingConfirmRequest()
                .setContact(contact)
                .setEmailConfirmation(true);
        return octoApiService.confirmBooking(activityItem.getSupplierId(), activityItem.getOctoBooking().getId(),
                confirmRequest, config);
    }

    @Override
    public void cancelActivityFromBooking(BookingZauiActivityItem activityItem, ZauiActivityConfig config) throws ZauiException {
        OctoBooking octoCancelledBooking = octoApiService.cancelBooking(activityItem.getSupplierId(),
                activityItem.getOctoBooking().getId(), config);

        // prevented pricing to be updated from cancelBooking as they get zeros
        Pricing pricingBeforeCancellation = activityItem.getOctoBooking().getPricing();
        octoCancelledBooking.setPricing(pricingBeforeCancellation);
        activityItem.setOctoBooking(octoCancelledBooking);
        // unpaid amount gets negative for cancelling paid activities, and zero for
        // unpaid ones
        double unpaidAmount = activityItem.getUnpaidAmount() != 0 ? 0 : -activityItem.price;
        activityItem.setUnpaidAmount(unpaidAmount);
    }

    @Override
    public void cancelAllActivitiesFromBooking(PmsBooking booking, ZauiActivityConfig config) {
        for (BookingZauiActivityItem activityItem : booking.bookingZauiActivityItems) {
            if (activityItem.getOctoBooking().getStatus().equals(ZauiConstants.OCTO_CONFIRMED_STATUS)) {
                try {
                    cancelActivityFromBooking(activityItem, config);
                } catch (ZauiException e) {
                    log.error(
                            "Failed to cancel octoBooking: {}. PmsBookingId {}. Reason: {}, Actual Error: {}",
                            activityItem, booking.id, e.getMessage(), e);
                }
            } else {
                removeActivityFromBooking(activityItem.getZauiActivityId(), booking);
            }
        }
    }

    private void syncZauiActivities(List<OctoProduct> octoProducts, ZauiConnectedSupplier supplier, String currency,
            SessionInfo sessionInfo) {
        for (OctoProduct octoProduct : octoProducts) {
            try {
                ZauiActivity zauiActivity = zauiActivityRepository.getBySupplierAndProductId(supplier.getId(),
                        octoProduct.getId(), sessionInfo);
                if (zauiActivity == null) {
                    zauiActivity = new ZauiActivity();
                }
                zauiActivityRepository.save(mapOctoToZauiActivity(octoProduct, supplier, currency, zauiActivity),
                        sessionInfo);

            } catch (Exception ex) {
                log.error(
                        "Failed to save or update octo product into database. Octo product id: {}, supplier id: {}. Reason: {}, Actual error: {}",
                        octoProduct.getId(), supplier.getId(), ex.getMessage(), ex);
            }
        }
    }

    private ZauiActivity mapOctoToZauiActivity(OctoProduct octoProduct, ZauiConnectedSupplier supplier, String currency,
            ZauiActivity zauiActivity) {
        zauiActivity.name = octoProduct.getInternalName();
        zauiActivity.setProductId(octoProduct.getId());
        zauiActivity.setSupplierId(supplier.getId());
        zauiActivity.setSupplierName(supplier.getName());
        zauiActivity.shortDescription = octoProduct.getShortDescription();
        zauiActivity.description = octoProduct.getPrimaryDescription();
        zauiActivity.setActivityOptionList(octoProduct.getOptions());
        zauiActivity.mainImage = octoProduct.getCoverImage();
        zauiActivity.tag = ZauiConstants.ZAUI_ACTIVITY_TAG;
        zauiActivity.setCurrency(currency);
        return zauiActivity;
    }

    @Override
    public Optional<BookingZauiActivityItem> getBookingZauiActivityItemByAddonId(String addonId,
            SessionInfo sessionInfo) {
        PmsBooking booking = pmsBookingRepository.getPmsBookingByAddonId(addonId, sessionInfo);
        return booking.bookingZauiActivityItems.stream()
                .filter(item -> item.addonId.equals(addonId)).findFirst();
    }

    private Pricing getPricingFromOctoTaxObject(Pricing pricingObj) {
        Pricing pricing = new Pricing();
        pricing.setTax(getPrecisedPrice(pricingObj.getIncludedTaxes().stream().mapToDouble(TaxData::getTaxAmount).sum(),
                pricingObj.getCurrencyPrecision()));
        pricing.setSubtotal(getPrecisedPrice(
                pricingObj.getIncludedTaxes().stream().mapToDouble(TaxData::getPriceExcludingTax).sum(),
                pricingObj.getCurrencyPrecision()));
        pricing.setTotal(pricing.getTax() + pricing.getSubtotal());
        return pricing;

    }

    @Override
    public double getPrecisedPrice(double price, Integer precision) {
        double amountDivider = Math.pow(10, precision);
        return price / amountDivider;
    }

    @Override
    public List<UnitItemReserveRequest> mapUnitsForBooking(List<Unit> units) {
        List<UnitItemReserveRequest> unitItems = new ArrayList<>();
        units.stream().filter(unit -> unit.quantity > 0).forEach(unit -> {
            for (int i = 0; i < unit.quantity; i++) {
                UnitItemReserveRequest item = new UnitItemReserveRequest();
                item.setUnitId(unit.getId());
                unitItems.add(item);
            }
        });
        return unitItems;
    }

    @Override
    public BookingZauiActivityItem mapActivityToBookingZauiActivityItem(OctoBooking octoBooking,
            SessionInfo sessionInfo) throws ZauiException {
        BookingZauiActivityItem activityItem = new BookingZauiActivityItem();
        ZauiActivity zauiActivity = getZauiActivityByOptionId(octoBooking.getOptionId(), sessionInfo);
        ActivityOption bookedOption = zauiActivity.getActivityOptionList().stream()
                .filter(option -> option.getId().equals(octoBooking.getOptionId()))
                .findFirst().orElse(null);
        if (bookedOption == null) {
            throw new ZauiException(ZauiStatusCodes.ACTIVITY_NOT_FOUND);
        }

        activityItem.setZauiActivityId(zauiActivity.id);
        activityItem.setOctoProductId(zauiActivity.getProductId());
        activityItem.setName(zauiActivity.name);
        activityItem.setOptionTitle(bookedOption.getInternalName());
        activityItem.setOptionId(bookedOption.getId());
        activityItem.setAvailabilityId(octoBooking.getAvailabilityId());
        activityItem.setUnits(getUnitFromOctoBookingUnitItems(octoBooking.getUnitItems()));
        activityItem.setNotes(octoBooking.getNotes());
        activityItem.setLocalDateTimeStart(octoBooking.getAvailability().getLocalDateTimeStart());
        activityItem.setLocalDateTimeEnd(octoBooking.getAvailability().getLocalDateTimeEnd());
        activityItem.setSupplierId(zauiActivity.getSupplierId());
        activityItem.setSupplierName(zauiActivity.getSupplierName());
        activityItem.setOctoBooking(octoBooking);
        return activityItem;
    }

    private List<Unit> getUnitFromOctoBookingUnitItems(List<UnitItemOnBooking> unitItems) {
        Map<String, Integer> unitQuantity = new HashMap<>();
        for (UnitItemOnBooking unitItem : unitItems) {
            int count = 0;
            if (unitQuantity.containsKey(unitItem.getUnitId())) {
                count = unitQuantity.get(unitItem.getUnitId());
            }
            count++;
            unitQuantity.put(unitItem.getUnitId(), count);
        }

        return unitQuantity.keySet().stream()
                .map(unitId -> {
                    Unit unit = new Unit();
                    unit.setId(unitId);
                    unit.setQuantity(unitQuantity.get(unitId));
                    return unit;
                }).collect(Collectors.toList());
    }

    @Override
    public PmsOrderCreateRow createOrderCreateRowForZauiActivities(List<BookingZauiActivityItem> activityItems) {
        if (activityItems.isEmpty())
            return null;
        PmsOrderCreateRow orderCreateRow = new PmsOrderCreateRow();
        orderCreateRow.roomId = "virtual";
        orderCreateRow.items = activityItems.stream().map(activity -> {
            PmsOrderCreateRowItemLine row = new PmsOrderCreateRowItemLine();
            row.createOrderOnProductId = activity.getZauiActivityId();
            row.count = 1;
            row.price = activity.price;
            row.orderItemType = ZauiConstants.ZAUI_ACTIVITY_TAG;
            row.setAddonId(activity.addonId);
            row.setTextOnOrder(activity.getName());
            return row;
        }).collect(Collectors.toList());
        return orderCreateRow;
    }

    @Override
    public void restrictGoToBookingWithActivities(PmsBooking booking) throws ZauiException {
        if (isNotBlank(booking.channel) && booking.channel.equals(GotoConstants.GOTO_BOOKING_CHANNEL_NAME)) {
            throw new ZauiException(ZauiStatusCodes.GOTO_CANCELLATION_DENIED);
        }
    }

    @Override
    public boolean isAllActivityCancelled(List<BookingZauiActivityItem> activities) {
        if (activities == null || activities.isEmpty())
            return true;
        return activities.stream().allMatch(activity -> activity.getOctoBooking().getStatus().equals("CANCELLED"));
    }

}
