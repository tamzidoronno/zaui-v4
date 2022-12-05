package com.thundashop.core.zauiactivity;

import java.util.List;

import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.services.zauiactivityservice.IZauiActivityService;
import com.thundashop.zauiactivity.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.services.octoapiservice.IOctoApiService;
import com.thundashop.services.zauiactivityservice.IZauiActivityService;
import com.thundashop.zauiactivity.dto.BookingReserveRequest;
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

    @Override
    public ZauiActivityConfig getActivityConfig() {
        return zauiActivityService.getZauiActivityConfig(getSessionInfo());
    }

    @Override
    public ZauiActivityConfig updateActivityConfig(ZauiActivityConfig newActivityConfig) {
        return zauiActivityService.setZauiActivityConfig(newActivityConfig, getSessionInfo());
    }

    @Override
    public List<OctoSupplier> getAllSuppliers() {
        return octoApiService.getAllSuppliers();
    }

    @Override
    public List<OctoProduct> getZauiActivities(Integer supplierId) {
        return octoApiService.getOctoProducts(supplierId);
    }

    @Override
    public List<OctoProductAvailability> getZauiActivityAvailability(Integer supplierId,
            OctoProductAvailabilityRequestDto availabilityRequest) {
        return octoApiService.getOctoProductAvailability(supplierId, availabilityRequest);
    }

    @Override
    public OctoBookingReserve reserveBooking(Integer supplierId, OctoBookingReserveRequest OctoBookingReserveRequest) {
        return octoApiService.reserveBooking(supplierId, OctoBookingReserveRequest);
    }

    @Override
    public OctoBookingConfirm confirmBooking(Integer supplierId, String bookingId, OctoBookingConfirmRequest octoBookingConfirmRequest) {
        return octoApiService.confirmBooking(supplierId, bookingId, octoBookingConfirmRequest);
    }

    @Override
    public OctoBookingConfirm cancelBooking(Integer supplierId, String bookingId) {
        return octoApiService.cancelBooking(supplierId, bookingId);
    }

    @Override
    public void fetchZauiActivities(Integer supplierId) throws ZauiException {
        zauiActivityService.fetchZauiActivities(supplierId, getSessionInfo());
    }

    @Override
    public void addActivityToBooking(BookingZauiActivityItem activityItem, String pmsBookingId) throws ZauiException {
        PmsBooking booking = pmsManager.getBooking(pmsBookingId);
        zauiActivityService.addActivityToBooking(activityItem,booking);
        pmsManager.saveBooking(booking);
    }

    @Override
    public void testActivity(BookingZauiActivityItem activityItem, String pmsBookingId) throws ZauiException {
        addActivityToBooking(activityItem,pmsBookingId);
    }
}
