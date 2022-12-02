package com.thundashop.core.zauiactivity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.services.octoapiservice.IOctoApiService;
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
    private IOctoApiService octoApiService;
    
    @Autowired
    private IZauiActivityService zauiActivityService;

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
    public List<BookingReserve> reserveBooking(Integer supplierId, BookingReserveRequest bookingReserveRequest) {
        return octoApiService.reserveBooking(supplierId, bookingReserveRequest);
    }

    @Override
    public List<BookingConfirm> confirmBooking(Integer supplierId, String bookingId, BookingConfirmRequest bookingConfirmRequest) {
        return octoApiService.confirmBooking(supplierId, bookingId, bookingConfirmRequest);
    }

    @Override
    public List<BookingConfirm> cancelBooking(Integer supplierId, String bookingId) {
        return octoApiService.cancelBooking(supplierId, bookingId);
    }

    @Override
    public void fetchZauiActivities(Integer supplierId) throws ZauiException {
        zauiActivityService.fetchZauiActivities(supplierId, getSessionInfo());
    }
}
