package com.thundashop.core.zauiactivity;

import java.io.IOException;
import java.util.List;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.zauiactivity.dto.*;

/**
 * The ZauiActivityManager is responsible for handling all zaui do
 * activities.<br>
 */
@GetShopApi
@GetShopSession
public interface IZauiActivityManager {
    ZauiActivityConfig getActivityConfig();

    void updateActivityConfig(ZauiActivityConfig newActivityConfig);

    List<OctoSupplier> getAllSuppliers();

    List<OctoProduct> getActivities(Integer supplierId) throws IOException;

    List<OctoProductAvailability> getAvailability(Integer supplierId, OctoProductAvailabilityRequestDto availabilityRequest);

    List<BookingReserve> reserveBooking(Integer supplierId, BookingReserveRequest bookingReserveRequest);

    List<BookingConfirm> confirmBooking(String bookingId, BookingConfirmRequest bookingConfirmRequest);

    List<BookingConfirm> confirmBooking(Integer supplierId, String bookingId, BookingConfirmRequest bookingConfirmRequest);

    List<BookingConfirm> cancelBooking(Integer supplierId, String bookingId);

    void fetchZauiActivities(Integer supplierId) throws ZauiException;
}
