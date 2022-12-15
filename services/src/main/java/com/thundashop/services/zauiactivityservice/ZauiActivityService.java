package com.thundashop.services.zauiactivityservice;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thundashop.core.pmsmanager.PmsBooking;
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
import com.thundashop.zauiactivity.dto.BookingZauiActivityItem;
import com.thundashop.zauiactivity.dto.OctoBooking;
import com.thundashop.zauiactivity.dto.OctoBookingConfirmRequest;
import com.thundashop.zauiactivity.dto.OctoBookingReserveRequest;
import com.thundashop.zauiactivity.dto.OctoConfirmContact;
import com.thundashop.zauiactivity.dto.OctoProduct;
import com.thundashop.zauiactivity.dto.UnitItemReserveRequest;
import com.thundashop.zauiactivity.dto.ZauiActivity;
import com.thundashop.zauiactivity.dto.ZauiActivityConfig;

import lombok.extern.slf4j.Slf4j;

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

    public void fetchZauiActivities(SessionInfo sessionInfo, ZauiActivityConfig zauiActivityConfig, String currency) {
        if (zauiActivityConfig == null || zauiActivityConfig.connectedSuppliers == null
                || zauiActivityConfig.connectedSuppliers.size() < 1) {
            return;
        }
        zauiActivityConfig.connectedSuppliers.forEach(supplier -> {
            try {
                octoApiService.getOctoProducts(supplier.getId()).forEach(
                        octoProduct -> {
                            try {
                                zauiActivityRepository.save(
                                        mapOctoToZauiActivity(octoProduct, supplier.getId(), currency), sessionInfo);
                            } catch (Exception ex) {
                                log.error(
                                        "Failed to insert octo product into database. Octo product id: {}, supplier id: {}. Reason: {}, Actual error: {}",
                                        octoProduct.getId(), supplier.getId(), ex.getMessage(), ex);
                            }
                        });
            } catch (Exception e) {
                log.error("Failed to fetch octo products for supplier {}. Reason: {}, Actual error: {}",
                        supplier.getId(), e.getMessage(), e);
            }
        });

    }

    @Override
    public PmsBooking addActivityToBooking(BookingZauiActivityItem activityItem, PmsBooking booking, User booker)
            throws ZauiException {
        if (activityItem.units == null)
            throw new ZauiException(ZauiStatusCodes.MISSING_PARAMS);
        OctoBooking octoReservedBooking = reserveOctoBooking(activityItem, booking);
        activityItem.setOctoBooking(octoReservedBooking);
        OctoBooking octoConfirmedBooking = confirmOctoBooking(activityItem, booking, booker, octoReservedBooking);
        activityItem.setOctoBooking(octoConfirmedBooking);
        booking.bookingZauiActivityItems.add(activityItem);
        return booking;
    }

    private OctoBooking reserveOctoBooking(BookingZauiActivityItem activityItem, PmsBooking booking)
            throws ZauiException {
        OctoBookingReserveRequest bookingReserveRequest = new OctoBookingReserveRequest()
                .setProductId(activityItem.octoProductId)
                .setOptionId(activityItem.optionId)
                .setAvailabilityId(activityItem.availabilityId)
                .setNotes(ZauiConstants.ZAUI_STAY_TAG)
                .setUnitItems(activityItem.units.stream().map(o -> new UnitItemReserveRequest(o.id))
                        .collect(Collectors.toList()));
        return octoApiService.reserveBooking(activityItem.supplierId, bookingReserveRequest);
    }

    private OctoBooking confirmOctoBooking(BookingZauiActivityItem activityItem, PmsBooking booking, User booker,
            OctoBooking octoReservedBooking) throws ZauiException {
        OctoConfirmContact contact = new OctoConfirmContact()
                .setFullName(booker.fullName)
                .setEmailAddress(booker.emailAddress)
                .setPhoneNumber(booker.prefix + booker.cellPhone)
                .setCountry(booking.countryCode);
        OctoBookingConfirmRequest confirmRequest = new OctoBookingConfirmRequest()
                .setContact(contact)
                .setEmailConfirmation(true);
        return octoApiService.confirmBooking(activityItem.supplierId, octoReservedBooking.getId(), confirmRequest);
    }

    @Override
    public void cancelActivityFromBooking(BookingZauiActivityItem activityItem) throws ZauiException {
        OctoBooking octoCancelledBooking = octoApiService.cancelBooking(activityItem.supplierId,
                activityItem.getOctoBooking().getId());
        activityItem.setOctoBooking(octoCancelledBooking);
    }

    private ZauiActivity mapOctoToZauiActivity(OctoProduct octoProduct, Integer supplierId, String currency) {
        ZauiActivity zauiActivity = new ZauiActivity();
        zauiActivity.name = octoProduct.getInternalName();
        zauiActivity.productId = octoProduct.getId();
        zauiActivity.supplierId = supplierId;
        zauiActivity.shortDescription = octoProduct.getShortDescription();
        zauiActivity.description = octoProduct.getPrimaryDescription();
        zauiActivity.activityOptionList = octoProduct.getOptions();
        zauiActivity.mainImage = octoProduct.getCoverImage();
        zauiActivity.tag = ZauiConstants.ZAUI_ACTIVITY_TAG;
        zauiActivity.currency = currency;
        return zauiActivity;
    }

    @Override
    public Optional<BookingZauiActivityItem> getBookingZauiActivityItemByAddonId(String addonId,
            SessionInfo sessionInfo) {
        PmsBooking booking = pmsBookingRepository.getPmsBookingByAddonId(addonId, sessionInfo);
        BookingZauiActivityItem bookingZauiActivityItem = booking.bookingZauiActivityItems.stream()
                .filter(item -> item.addonId.equals(addonId)).findFirst().get();
        return Optional.of(bookingZauiActivityItem);
    }
}
