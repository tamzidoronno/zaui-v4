package com.thundashop.core.zauiactivity;

import java.util.List;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.pmsbookingprocess.GuestAddonsSummary;
import com.thundashop.repository.exceptions.NotUniqueDataException;
import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.zauiactivity.dto.*;

/**
 * The ZauiActivityManager is responsible for handling all zaui do
 * activities.<br>
 */
@GetShopApi
@GetShopMultiLayerSession
public interface IZauiActivityManager {
    ZauiActivityConfig getActivityConfig() throws NotUniqueDataException;

    ZauiActivityConfig updateActivityConfig(ZauiActivityConfig newActivityConfig);

    List<OctoSupplier> getAllSuppliers() throws ZauiException;

    List<OctoProduct> getOctoProducts(Integer supplierId) throws ZauiException;

    List<OctoProductAvailability> getZauiActivityAvailability(Integer supplierId, OctoProductAvailabilityRequestDto availabilityRequest) throws ZauiException;

    OctoBooking reserveBooking(Integer supplierId, OctoBookingReserveRequest bookingReserveRequest) throws ZauiException;

    OctoBooking confirmBooking(Integer supplierId, String bookingId, OctoBookingConfirmRequest octoBookingConfirmRequest) throws ZauiException;

    void addActivityToBooking(BookingZauiActivityItem activityItem, String pmsBookingId) throws ZauiException;

    GuestAddonsSummary addActivityToWebBooking(AddZauiActivityToWebBookingDto activity) throws ZauiException;

    GuestAddonsSummary removeActivityFromWebBooking(AddZauiActivityToWebBookingDto activity) throws ZauiException;

    void cancelActivity(String pmsBookingId, String octoBookingId) throws ZauiException;

    List<ZauiActivity> getZauiActivities() throws ZauiException;

    void fetchZauiActivities() throws NotUniqueDataException;

    List<CartItem> getZauiActivityCartItems(String productId, String addonId) throws ErrorException;
}
