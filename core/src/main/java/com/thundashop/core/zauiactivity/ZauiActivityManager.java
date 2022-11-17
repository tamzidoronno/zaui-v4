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
import com.thundashop.zauiactivity.dto.ActivityConfig;
import com.thundashop.zauiactivity.dto.Availability;
import com.thundashop.zauiactivity.dto.AvailabilityRequest;
import com.thundashop.zauiactivity.dto.BookingConfirm;
import com.thundashop.zauiactivity.dto.BookingConfirmRequest;
import com.thundashop.zauiactivity.dto.BookingReserve;
import com.thundashop.zauiactivity.dto.BookingReserveRequest;
import com.thundashop.zauiactivity.dto.OctoProduct;
import com.thundashop.services.octoapiservice.OctoApiService;

@Component
@GetShopSession
public class ZauiActivityManager extends GetShopSessionBeanNamed implements IZauiActivityManager {
    @Autowired
    OctoApiService octoApiService;

    ActivityConfig activityConfig = new ActivityConfig();

    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof ActivityConfig) {
                activityConfig = (ActivityConfig) dataCommon;
            }
        }
    }

    @Override
    public ActivityConfig getActivityConfig() {
        return activityConfig;
    }

    @Override
    public void updateActivityConfig(ActivityConfig newActivityConfig) {
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
    public List<Availability> getAvailability(AvailabilityRequest availabilityRequest) {
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
