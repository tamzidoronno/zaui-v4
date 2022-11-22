package com.thundashop.core.zauiactivity;

import java.io.IOException;
import java.util.List;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.zauiactivity.dto.BookingConfirm;
import com.thundashop.zauiactivity.dto.BookingConfirmRequest;
import com.thundashop.zauiactivity.dto.BookingReserve;
import com.thundashop.zauiactivity.dto.BookingReserveRequest;
import com.thundashop.zauiactivity.dto.OctoProduct;
import com.thundashop.zauiactivity.dto.OctoProductAvailability;
import com.thundashop.zauiactivity.dto.OctoProductAvailabilityRequestDto;
import com.thundashop.zauiactivity.dto.ZauiActivityConfig;

/**
 * The ZauiActivityManager is responsible for handling all zaui do
 * activities.<br>
 */
@GetShopApi
@GetShopSession
public interface IZauiActivityManager {
    ZauiActivityConfig getActivityConfig();

    void updateActivityConfig(ZauiActivityConfig newActivityConfig);

    List<OctoProduct> getActivities() throws IOException;

    List<OctoProductAvailability> getAvailability(OctoProductAvailabilityRequestDto availabilityRequest);

    List<BookingReserve> reserveBooking(BookingReserveRequest bookingReserveRequest);

    List<BookingConfirm> confirmBooking(String bookingId, BookingConfirmRequest bookingConfirmRequest);

    String getSupplierName();

    List<BookingConfirm> cancelBooking(String bookingId);
}
