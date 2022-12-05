package com.thundashop.core.zauiactivity;

import java.io.IOException;
import java.util.List;

import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.services.zauiactivityservice.IZauiActivityService;
import com.thundashop.zauiactivity.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;

import com.thundashop.services.octoapiservice.OctoApiService;

@Component
@GetShopSession
public class ZauiActivityManager extends GetShopSessionBeanNamed implements IZauiActivityManager {
    @Autowired
    OctoApiService octoApiService;
    @Autowired
    IZauiActivityService zauiActivityService;

    @Autowired
    PmsManager pmsManager;

    @Override
    public ZauiActivityConfig getActivityConfig() {
        return getZauiActivityConfig();
    }

    @Override
    public void updateActivityConfig(ZauiActivityConfig newActivityConfig) {
        zauiActivityService.setZauiActivityConfig(newActivityConfig,getSessionInfo());
    }

    @Override
    public List<OctoSupplier> getAllSuppliers(){
        return octoApiService.getAllSuppliers();
    }


    @Override
    public List<OctoProduct> getActivities(Integer supplierId) throws IOException {
        return octoApiService.getProducts(supplierId);
    }

    @Override
    public List<OctoProductAvailability> getAvailability(Integer supplierId, OctoProductAvailabilityRequestDto availabilityRequest) {
        return octoApiService.getAvailability(supplierId, availabilityRequest);
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

    private ZauiActivityConfig getZauiActivityConfig() {
        return zauiActivityService.getZauiActivityConfig(getSessionInfo());
    }

    @Override
    public void fetchZauiActivities(Integer supplierId) throws ZauiException {
        zauiActivityService.importZauiActivities(supplierId, getSessionInfo());
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
