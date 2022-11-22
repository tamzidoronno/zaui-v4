package com.thundashop.core.zauiactivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;
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

    ZauiActivityConfig activityConfig = new ZauiActivityConfig();

    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof ZauiActivityConfig) {
                activityConfig = (ZauiActivityConfig) dataCommon;
            }
        }
    }

    @Override
    public ZauiActivityConfig getActivityConfig() {
        return activityConfig;
    }

    @Override
    public void updateActivityConfig(ZauiActivityConfig newActivityConfig) {
        activityConfig.setActivityConfig(newActivityConfig);
        saveObject(activityConfig);
    }

    @Override
    public String getSupplierName() {
        if (activityConfig.getSupplierId() == null) {
            return "";
        }
        return octoApiService.getSupplierName(activityConfig.getSupplierId());
    }

    @Override
    public List<OctoProduct> getActivities() throws IOException {
        if (activityConfig.getSupplierId() == null) {
            return new ArrayList<>();
        }
        return octoApiService.getProducts(activityConfig.getSupplierId());
    }

    @Override
    public List<OctoProductAvailability> getAvailability(OctoProductAvailabilityRequestDto availabilityRequest) {
        if (activityConfig.getSupplierId() == null) {
            return new ArrayList<>();
        }
        return octoApiService.getAvailability(activityConfig.getSupplierId(), availabilityRequest);
    }

    @Override
    public List<BookingReserve> reserveBooking(BookingReserveRequest bookingReserveRequest) {
        if (activityConfig.getSupplierId() == null) {
            return new ArrayList<>();
        }
        return octoApiService.reserveBooking(activityConfig.getSupplierId(), bookingReserveRequest);
    }

    @Override
    public List<BookingConfirm> confirmBooking(String bookingId, BookingConfirmRequest bookingConfirmRequest) {
        if (activityConfig.getSupplierId() == null) {
            return new ArrayList<>();
        }
        return octoApiService.confirmBooking(activityConfig.getSupplierId(), bookingId, bookingConfirmRequest);
    }

    @Override
    public List<BookingConfirm> cancelBooking(String bookingId) {
        if (activityConfig.getSupplierId() == null) {
            return new ArrayList<>();
        }
        return octoApiService.cancelBooking(activityConfig.getSupplierId(), bookingId);
    }
}
