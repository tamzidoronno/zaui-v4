package com.thundashop.core.zauiactivity;

import java.util.List;

import com.thundashop.core.pmsbookingprocess.BookerInformation;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.services.zauiactivityservice.IZauiActivityService;
import com.thundashop.zauiactivity.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.services.octoapiservice.IOctoApiService;
import com.thundashop.zauiactivity.dto.OctoProduct;
import com.thundashop.zauiactivity.dto.OctoProductAvailability;
import com.thundashop.zauiactivity.dto.OctoProductAvailabilityRequestDto;
import com.thundashop.zauiactivity.dto.OctoSupplier;
import com.thundashop.zauiactivity.dto.ZauiActivityConfig;

@Component
@GetShopSession
@Slf4j
public class ZauiActivityManager extends GetShopSessionBeanNamed implements IZauiActivityManager {
    @Autowired
    private IOctoApiService octoApiService;
    @Autowired
    private IZauiActivityService zauiActivityService;
    @Autowired
    PmsManager pmsManager;

    @Autowired
    StoreManager storeManager;

    @Autowired
    UserManager userManager;

    @Override
    public ZauiActivityConfig getActivityConfig() {
        return zauiActivityService.getZauiActivityConfig(getSessionInfo());
    }

    @Override
    public ZauiActivityConfig updateActivityConfig(ZauiActivityConfig newActivityConfig) {
       // return zauiActivityService.setZauiActivityConfig(newActivityConfig, getSessionInfo());
        saveObject(newActivityConfig);
        return newActivityConfig;
    }

    @Override
    public List<OctoSupplier> getAllSuppliers() throws ZauiException {
        return octoApiService.getAllSuppliers();
    }

    @Override
    public List<OctoProduct> getOctoProducts(Integer supplierId) throws ZauiException {
        return octoApiService.getOctoProducts(supplierId);
    }

    @Override
    public List<OctoProductAvailability> getZauiActivityAvailability(Integer supplierId,
            OctoProductAvailabilityRequestDto availabilityRequest) throws ZauiException {
        return octoApiService.getOctoProductAvailability(supplierId, availabilityRequest);
    }

    @Override
    public OctoBooking reserveBooking(Integer supplierId, OctoBookingReserveRequest OctoBookingReserveRequest) throws ZauiException {
        return octoApiService.reserveBooking(supplierId, OctoBookingReserveRequest);
    }

    @Override
    public OctoBooking confirmBooking(Integer supplierId, String bookingId, OctoBookingConfirmRequest octoBookingConfirmRequest) throws ZauiException {
        return octoApiService.confirmBooking(supplierId, bookingId, octoBookingConfirmRequest);
    }


    @Override
    public List<ZauiActivity> getZauiActivities() throws ZauiException {
        return zauiActivityService.getZauiActivities(getSessionInfo());
    }

    @Override
    public void fetchZauiActivities() throws ZauiException {
        String currency = storeManager.getStoreSettingsApplicationKey("currencycode");
        zauiActivityService.fetchZauiActivities(getSessionInfo(),currency);
    }

    @Override
    public void addActivityToBooking(BookingZauiActivityItem activityItem, String pmsBookingId) throws ZauiException {
        PmsBooking booking = pmsManager.getBooking(pmsBookingId);
        User booker = userManager.getUserById(booking.userId);
        booking = zauiActivityService.addActivityToBooking(activityItem,booking,booker);
        pmsManager.saveBooking(booking);
    }
    @Override
    public void cancelActivity(String pmsBookingId, String octoBookingId) throws ZauiException {
        PmsBooking booking = pmsManager.getBooking(pmsBookingId);
        BookingZauiActivityItem activityItem = booking.bookingZauiActivityItems.stream()
                .filter(item -> item.getOctoBooking().getId().equals(octoBookingId))
                .findFirst()
                .orElse(null);
        zauiActivityService.cancelActivityFromBooking(activityItem);
        pmsManager.saveBooking(booking);
    }
}
