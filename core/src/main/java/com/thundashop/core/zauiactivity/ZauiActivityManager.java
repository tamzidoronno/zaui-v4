package com.thundashop.core.zauiactivity;

import java.io.IOException;
import java.util.List;

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
    public List<BookingReserve> reserveBooking(Integer supplierId, BookingReserveRequest bookingReserveRequest) {
        return octoApiService.reserveBooking(supplierId, bookingReserveRequest);
    }

    @Override
    public List<BookingConfirm> confirmBooking(String bookingId, BookingConfirmRequest bookingConfirmRequest) {
        return null;
    }

    @Override
    public List<BookingConfirm> confirmBooking(Integer supplierId, String bookingId, BookingConfirmRequest bookingConfirmRequest) {
        return octoApiService.confirmBooking(supplierId, bookingId, bookingConfirmRequest);
    }

    @Override
    public List<BookingConfirm> cancelBooking(Integer supplierId, String bookingId) {
        return octoApiService.cancelBooking(supplierId, bookingId);
    }

    private ZauiActivityConfig getZauiActivityConfig() {
        return zauiActivityService.getZauiActivityConfig(getSessionInfo());
    }

    @Override
    public void fetchZauiActivities(Integer supplierId) throws ZauiException {
        zauiActivityService.importZauiActivities(supplierId, getSessionInfo());
    }
}
