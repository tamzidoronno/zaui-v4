package com.thundashop.core.zauiactivity;

import java.util.List;

import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.zauiactivity.dto.*;

/**
 * The ZauiActivityManager is responsible for handling all zaui do
 * activities.<br>
 */
@GetShopApi
@GetShopMultiLayerSession
public interface IZauiActivityManager {
    ZauiActivityConfig getActivityConfig();

    ZauiActivityConfig updateActivityConfig(ZauiActivityConfig newActivityConfig);

    List<OctoSupplier> getAllSuppliers() throws ZauiException;

    List<OctoProduct> getOctoProducts(Integer supplierId) throws ZauiException;

    List<OctoProductAvailability> getZauiActivityAvailability(Integer supplierId, OctoProductAvailabilityRequestDto availabilityRequest) throws ZauiException;

    OctoBooking reserveBooking(Integer supplierId, OctoBookingReserveRequest bookingReserveRequest) throws ZauiException;

    OctoBooking confirmBooking(Integer supplierId, String bookingId, OctoBookingConfirmRequest octoBookingConfirmRequest) throws ZauiException;

    OctoBooking cancelBooking(Integer supplierId, String bookingId) throws ZauiException;

    List<ZauiActivity> getZauiActivities() throws ZauiException;

    void fetchZauiActivities() throws ZauiException;

    void addActivityToBooking(BookingZauiActivityItem activityItem, String pmsBookingId) throws ZauiException;
}
