package com.thundashop.core.zauiactivity;

import java.util.List;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.ZauiException;
import com.thundashop.core.pmsbookingprocess.GuestAddonsSummary;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.zauiactivity.dto.AddZauiActivityToWebBookingDto;
import com.thundashop.zauiactivity.dto.BookingZauiActivityItem;
import com.thundashop.zauiactivity.dto.OctoBooking;
import com.thundashop.zauiactivity.dto.OctoBookingConfirmRequest;
import com.thundashop.zauiactivity.dto.OctoBookingReserveRequest;
import com.thundashop.zauiactivity.dto.OctoProduct;
import com.thundashop.zauiactivity.dto.OctoProductAvailability;
import com.thundashop.zauiactivity.dto.OctoProductAvailabilityRequestDto;
import com.thundashop.zauiactivity.dto.OctoSupplier;
import com.thundashop.zauiactivity.dto.ZauiActivity;
import com.thundashop.zauiactivity.dto.ZauiActivityConfig;

/**
 * The ZauiActivityManager is responsible for handling all zaui do
 * activities.<br>
 */
@GetShopApi
public interface IZauiActivityManager {
    ZauiActivityConfig getActivityConfig() throws ZauiException;

    ZauiActivityConfig updateActivityConfig(ZauiActivityConfig newActivityConfig) throws ZauiException;

    List<OctoSupplier> getAllSuppliers() throws ZauiException;

    List<OctoProduct> getOctoProducts(Integer supplierId) throws ZauiException;

    List<OctoProductAvailability> getZauiActivityAvailability(Integer supplierId, OctoProductAvailabilityRequestDto availabilityRequest) throws ZauiException;

    OctoBooking reserveBooking(Integer supplierId, OctoBookingReserveRequest bookingReserveRequest) throws ZauiException;

    OctoBooking confirmBooking(Integer supplierId, String bookingId, OctoBookingConfirmRequest octoBookingConfirmRequest) throws ZauiException;

    void addActivityToBooking(BookingZauiActivityItem activityItem, String pmsBookingId) throws ZauiException;

    GuestAddonsSummary addActivityToWebBooking(AddZauiActivityToWebBookingDto activity) throws ZauiException;

    GuestAddonsSummary removeActivityFromBooking(String activity, String pmsBookingId);

    void cancelActivity(String pmsBookingId, String octoBookingId) throws ZauiException;

    void cancelAllActivitiesFromBooking(PmsBooking booking);

    List<ZauiActivity> getZauiActivities() throws ZauiException;

    void fetchZauiActivities() throws ZauiException;

    List<CartItem> getZauiActivityCartItems(String productId, String addonId, double price) throws ErrorException;

    ZauiActivity getZauiActivity(String productId);
}
