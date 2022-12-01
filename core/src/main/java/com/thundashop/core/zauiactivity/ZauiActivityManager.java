package com.thundashop.core.zauiactivity;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.services.octoapiservice.OctoApiService;
import com.thundashop.services.zauiactivityservice.IZauiActivityService;
import com.thundashop.zauiactivity.dto.BookingConfirm;
import com.thundashop.zauiactivity.dto.BookingConfirmRequest;
import com.thundashop.zauiactivity.dto.BookingReserve;
import com.thundashop.zauiactivity.dto.BookingReserveRequest;
import com.thundashop.zauiactivity.dto.OctoProduct;
import com.thundashop.zauiactivity.dto.OctoProductAvailability;
import com.thundashop.zauiactivity.dto.OctoProductAvailabilityRequestDto;
import com.thundashop.zauiactivity.dto.OctoSupplier;
import com.thundashop.zauiactivity.dto.ZauiActivityConfig;

@Component
@GetShopSession
public class ZauiActivityManager extends GetShopSessionBeanNamed implements IZauiActivityManager {
    @Autowired
    private OctoApiService octoApiService;
    
    @Autowired
    private IZauiActivityService zauiActivityService;

    @Override
    public ZauiActivityConfig getActivityConfig() {
        return zauiActivityService.getZauiActivityConfig(getSessionInfo());
    }

    @Override
    public void updateActivityConfig(ZauiActivityConfig newActivityConfig) {
        zauiActivityService.setZauiActivityConfig(newActivityConfig, getSessionInfo());
    }

    @Override
    public List<OctoSupplier> getAllSuppliers() {
        return octoApiService.getAllSuppliers();
    }

    @Override
    public List<OctoProduct> getActivities(Integer supplierId) throws IOException {
        return octoApiService.getProducts(supplierId);
    }

    @Override
    public List<OctoProductAvailability> getAvailability(Integer supplierId,
            OctoProductAvailabilityRequestDto availabilityRequest) {
        return octoApiService.getAvailability(supplierId, availabilityRequest);
    }

    @Override
    public List<BookingReserve> reserveBooking(Integer supplierId, BookingReserveRequest bookingReserveRequest) {
        return octoApiService.reserveBooking(supplierId, bookingReserveRequest);
    }

    @Override
    public List<BookingConfirm> confirmBooking(Integer supplierId, String bookingId,
            BookingConfirmRequest bookingConfirmRequest) {
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
        zauiActivityService.fetchZauiActivities(supplierId, getSessionInfo());
    }
}
