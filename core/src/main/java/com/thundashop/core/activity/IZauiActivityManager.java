package com.thundashop.core.activity;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.activity.dto.*;
import com.thundashop.core.common.GetShopApi;

import java.io.IOException;
import java.util.List;

@GetShopApi
@GetShopSession
public interface IZauiActivityManager {
    ActivityConfig getActivityConfig();

    void updateActivityConfig(ActivityConfig newActivityConfig);

    List<OctoProduct> getActivities() throws IOException;

    List<Availability> getAvailability(AvailabilityRequest availabilityRequest);

    List<BookingReserve> reserveBooking(BookingReserveRequest bookingReserveRequest);

    List<BookingConfirm> confirmBooking(String bookingId, BookingConfirmRequest bookingConfirmRequest);

    String getSupplierName();

    List<BookingConfirm> cancelBooking(String bookingId);
}
