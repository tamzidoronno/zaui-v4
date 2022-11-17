package com.thundashop.core.zauiactivity;

import java.io.IOException;
import java.util.List;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.zauiactivity.dto.ActivityConfig;
import com.thundashop.core.zauiactivity.dto.Availability;
import com.thundashop.core.zauiactivity.dto.AvailabilityRequest;
import com.thundashop.core.zauiactivity.dto.BookingConfirm;
import com.thundashop.core.zauiactivity.dto.BookingConfirmRequest;
import com.thundashop.core.zauiactivity.dto.BookingReserve;
import com.thundashop.core.zauiactivity.dto.BookingReserveRequest;
import com.thundashop.core.zauiactivity.dto.OctoProduct;

/**
 * The ZauiActivityManager is responsible for handling all zaui do
 * activities.<br>
 */
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
