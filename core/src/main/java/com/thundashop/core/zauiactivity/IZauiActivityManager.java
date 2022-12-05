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

    OctoBookingReserve reserveBooking(Integer supplierId, OctoBookingReserveRequest bookingReserveRequest);

    OctoBookingConfirm confirmBooking(Integer supplierId, String bookingId, OctoBookingConfirmRequest octoBookingConfirmRequest);

    OctoBookingConfirm cancelBooking(Integer supplierId, String bookingId);

    void fetchZauiActivities(Integer supplierId) throws ZauiException;

    void addActivityToBooking(BookingZauiActivityItem activityItem, String pmsBookingId) throws ZauiException;


    void testActivity(BookingZauiActivityItem activityItem, String pmsBookingId) throws ZauiException;
}
