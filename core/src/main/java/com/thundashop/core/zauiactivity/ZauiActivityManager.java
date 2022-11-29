package com.thundashop.core.zauiactivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.services.zauiactivityservice.IZauiActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;

import com.thundashop.services.octoapiservice.OctoApiService;
import com.thundashop.zauiactivity.dto.BookingConfirm;
import com.thundashop.zauiactivity.dto.BookingConfirmRequest;
import com.thundashop.zauiactivity.dto.BookingReserve;
import com.thundashop.zauiactivity.dto.BookingReserveRequest;
import com.thundashop.zauiactivity.dto.OctoProduct;
import com.thundashop.zauiactivity.dto.OctoProductAvailability;
import com.thundashop.zauiactivity.dto.OctoProductAvailabilityRequestDto;
import com.thundashop.zauiactivity.dto.ZauiActivityConfig;

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
    public String getSupplierName() {
        if (getZauiActivityConfig().getSupplierId() == null) {
            return "";
        }
        return octoApiService.getSupplierName(getZauiActivityConfig().getSupplierId());
    }

    @Override
    public List<OctoProduct> getActivities() throws IOException {
        if (getZauiActivityConfig().getSupplierId() == null) {
            return new ArrayList<>();
        }
        return octoApiService.getProducts(getZauiActivityConfig().getSupplierId());
    }

    @Override
    public List<OctoProductAvailability> getAvailability(OctoProductAvailabilityRequestDto availabilityRequest) {
        if (getZauiActivityConfig().getSupplierId() == null) {
            return new ArrayList<>();
        }
        return octoApiService.getAvailability(getZauiActivityConfig().getSupplierId(), availabilityRequest);
    }

    @Override
    public List<BookingReserve> reserveBooking(BookingReserveRequest bookingReserveRequest) {
        if (getZauiActivityConfig().getSupplierId() == null) {
            return new ArrayList<>();
        }
        return octoApiService.reserveBooking(getZauiActivityConfig().getSupplierId(), bookingReserveRequest);
    }

    @Override
    public List<BookingConfirm> confirmBooking(String bookingId, BookingConfirmRequest bookingConfirmRequest) {
        if (getZauiActivityConfig().getSupplierId() == null) {
            return new ArrayList<>();
        }
        return octoApiService.confirmBooking(getZauiActivityConfig().getSupplierId(), bookingId, bookingConfirmRequest);
    }

    @Override
    public List<BookingConfirm> cancelBooking(String bookingId) {
        if (getZauiActivityConfig().getSupplierId() == null) {
            return new ArrayList<>();
        }
        return octoApiService.cancelBooking(getZauiActivityConfig().getSupplierId(), bookingId);
    }

    private ZauiActivityConfig getZauiActivityConfig() {
        return zauiActivityService.getZauiActivityConfig(getSessionInfo());
    }

    @Override
    public void fetchZauiActivities(Integer supplierId) throws ZauiException {
        zauiActivityService.importZauiActivities(supplierId, getSessionInfo());
    }
}
