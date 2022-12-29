package com.thundashop.services.zauiactivityservice;

import com.thundashop.core.gotohub.constant.GotoConstants;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsOrderCreateRow;
import com.thundashop.core.pmsmanager.PmsOrderCreateRowItemLine;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.repository.exceptions.NotUniqueDataException;
import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.repository.pmsbookingrepository.IPmsBookingRepository;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.repository.utils.ZauiStatusCodes;
import com.thundashop.repository.zauiactivityrepository.IZauiActivityConfigRepository;
import com.thundashop.repository.zauiactivityrepository.IZauiActivityRepository;
import com.thundashop.services.octoapiservice.IOctoApiService;
import com.thundashop.zauiactivity.constant.ZauiConstants;
import com.thundashop.zauiactivity.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
public class ZauiActivityService implements IZauiActivityService {
    @Autowired
    private IZauiActivityConfigRepository zauiActivityConfigRepository;
    @Autowired
    private IZauiActivityRepository zauiActivityRepository;
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

    public List<ZauiActivity> getZauiActivities(SessionInfo sessionInfo) {
        return zauiActivityRepository.getAll(sessionInfo);
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
        if (zauiActivityConfig == null || zauiActivityConfig.connectedSuppliers == null
                || zauiActivityConfig.connectedSuppliers.size() < 1) {
            return;
        }
        zauiActivityConfig.connectedSuppliers.forEach(supplier -> {
            try {
                List<OctoProduct> octoProducts = octoApiService.getOctoProducts(supplier.getId());
                List<Integer> octoProductIds = octoProducts.stream().map(OctoProduct::getId)
                        .collect(Collectors.toList());
                List<String> removingActivityIds = getZauiActivities(sessionInfo).stream()
                        .filter(activity -> activity.getSupplierId() == supplier.getId()
                                && !octoProductIds.contains(activity.getProductId()))
                        .map(activity -> activity.id).collect(Collectors.toList());
                zauiActivityRepository.markDeleted(removingActivityIds, sessionInfo);
                syncZauiActivities(octoProducts, supplier, currency, sessionInfo);
            } catch (Exception e) {
                log.error("Failed to fetch octo products for supplier {}. Reason: {}, Actual error: {}",
                        supplier.getId(), e.getMessage(), e);
            }
        });
    }

    @Override
    public PmsBooking addActivityToBooking(BookingZauiActivityItem activityItem, PmsBooking booking, User booker)
            throws ZauiException {
        if (activityItem.getUnits() == null || activityItem.getUnits().isEmpty())
            throw new ZauiException(ZauiStatusCodes.MISSING_PARAMS);
        OctoBooking octoReservedBooking = reserveOctoBooking(activityItem);
        activityItem.setOctoBooking(octoReservedBooking);
        OctoBooking octoConfirmedBooking = confirmOctoBooking(activityItem, booking, booker);
        booking = addActivityToBooking(activityItem, octoConfirmedBooking, booking);
        return booking;
    }

    @Override
    public PmsBooking addActivityToBooking(BookingZauiActivityItem activityItem, OctoBooking octoBooking,
            PmsBooking booking) throws ZauiException {
        if (activityItem.getUnits() == null || activityItem.getUnits().isEmpty())
            throw new ZauiException(ZauiStatusCodes.MISSING_PARAMS);
        activityItem.setOctoBooking(octoBooking);
        activityItem.price = getPricingFromOctoTaxObject(activityItem.getOctoBooking().getPricing()).getTotal();
        activityItem.priceExTaxes = getPricingFromOctoTaxObject(activityItem.getOctoBooking().getPricing())
                .getSubtotal();
        activityItem.setUnpaidAmount(activityItem.price);
        booking.bookingZauiActivityItems.add(activityItem);
        log.info("activity added to booking {}", activityItem);
        return booking;
    }

    @Override
    public PmsBooking addActivityToBooking(AddZauiActivityToWebBookingDto activity, PmsBooking booking,
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
                bookingReserveRequest);
        BookingZauiActivityItem activityItem = mapActivityToBookingZauiActivityItem(octoReserveBooking, sessionInfo);
        activityItem.setUnits(activity.getUnits());
        booking = addActivityToBooking(activityItem, octoReserveBooking, booking);
        return booking;
    }

    @Override
    public PmsBooking removeActivityFromWebBooking(AddZauiActivityToWebBookingDto activity, PmsBooking booking,
            SessionInfo sessionInfo) {
        booking.bookingZauiActivityItems.removeIf(item -> item.getAvailabilityId().equals(activity.getAvailabilityId())
                && item.getOptionId().equals(activity.getOptionId()));
        log.info("activity removed from booking {}", activity);
        return booking;
    }

    private OctoBooking reserveOctoBooking(BookingZauiActivityItem activityItem) throws ZauiException {
        OctoBookingReserveRequest bookingReserveRequest = new OctoBookingReserveRequest()
                .setProductId(activityItem.getOctoProductId())
                .setOptionId(activityItem.getOptionId())
                .setAvailabilityId(activityItem.getAvailabilityId())
                .setNotes(ZauiConstants.ZAUI_STAY_TAG)
                .setUnitItems(mapUnitsForBooking(activityItem.getUnits()));
        return octoApiService.reserveBooking(activityItem.getSupplierId(), bookingReserveRequest);
    }

    @Override
    public OctoBooking confirmOctoBooking(BookingZauiActivityItem activityItem, PmsBooking booking, User booker)
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
                confirmRequest);
    }

    @Override
    public void cancelActivityFromBooking(BookingZauiActivityItem activityItem) throws ZauiException {
        OctoBooking octoCancelledBooking = octoApiService.cancelBooking(activityItem.getSupplierId(),
                activityItem.getOctoBooking().getId());
        activityItem.setOctoBooking(octoCancelledBooking);
        // need clarification why is this needed
        double unpaidAmount = activityItem.getUnpaidAmount() != 0 ? 0 : -activityItem.price;
        activityItem.setUnpaidAmount(unpaidAmount);
    }

    @Override
    public void cancelAllActivitiesFromBooking(PmsBooking booking) {
        if (isNotBlank(booking.channel) && booking.channel.equals(GotoConstants.GOTO_BOOKING_CHANNEL_NAME)) {
            return;
        }
        // need filter for confirmation?
        for (BookingZauiActivityItem activityItem : booking.bookingZauiActivityItems) {
            try {
                cancelActivityFromBooking(activityItem);
            } catch (ZauiException e) {
                log.error(
                        "Failed to cancel octoBooking. OctoBookingId: {}. PmsBookingId {}. Reason: {}, Actual Error: {}",
                        activityItem.getOctoBooking().getId(), booking.id, e.getMessage(), e);
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
                    zauiActivityRepository.save(mapOctoToZauiActivity(octoProduct, supplier, currency, null),
                            sessionInfo);
                } else {
                    zauiActivityRepository.update(mapOctoToZauiActivity(octoProduct, supplier, currency, zauiActivity),
                            sessionInfo);
                }

            } catch (Exception ex) {
                log.error(
                        "Failed to save or update octo product into database. Octo product id: {}, supplier id: {}. Reason: {}, Actual error: {}",
                        octoProduct.getId(), supplier.getId(), ex.getMessage(), ex);
            }
        }
    }

    private ZauiActivity mapOctoToZauiActivity(OctoProduct octoProduct, ZauiConnectedSupplier supplier, String currency,
            ZauiActivity zauiActivity) {
        if (zauiActivity == null) {
            zauiActivity = new ZauiActivity();
        }
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
            UnitItemReserveRequest item = null;
            for (int i = 0; i < unit.quantity; i++) {
                item = new UnitItemReserveRequest();
                item.setUnitId(unit.getId());
            }
            unitItems.add(item);
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
}
