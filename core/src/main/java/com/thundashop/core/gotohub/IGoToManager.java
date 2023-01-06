package com.thundashop.core.gotohub;

import java.util.Date;

import com.thundashop.core.common.ForceAsync;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.gotohub.dto.GoToApiResponse;
import com.thundashop.core.gotohub.dto.GoToConfiguration;
import com.thundashop.core.gotohub.dto.GotoBookingRequest;
import com.thundashop.core.gotohub.dto.GotoConfirmBookingRequest;
import com.thundashop.core.pmsmanager.PmsBookingRooms;

/**
 * Goto management system.<br>
 */

@GetShopApi
@GetShopMultiLayerSession
public interface IGoToManager {
    /**
     * Get hotel information
     *
     * @return GoToApiResponse
     */
    GoToApiResponse getHotelInformation();

    /**
     * Get all {@link com.thundashop.core.gotohub.dto.RatePlan} of all types of room using virtual room methodology!
     *
     * @return GoToApiResponse
     */
    GoToApiResponse getRoomTypeDetails();

    /**
     * Inventory allotment within date of the hotel
     *
     * @param from, to
     * @return GoToApiResponse
     */
    @ForceAsync
    GoToApiResponse getPriceAndAllotmentWithDate(Date from, Date to);

    /**
     * Inventory allotment of the hotel (1 month)
     *
     * @return GoToApiResponse
     */
    @ForceAsync
    GoToApiResponse getPriceAndAllotment();

    /**
     * Save new Goto Booking
     *
     * @param booking data
     * @return GoToApiResponse
     */
    GoToApiResponse saveBooking(GotoBookingRequest booking);

    /**
     * Confirm Goto Booking
     *
     * @param reservationId: booking Id
     * @return GoToApiResponse
     */
    GoToApiResponse confirmBooking(String reservationId);

    /**
     * Confirm Goto Booking
     *
     * @param reservationId: booking Id
     * @param confirmBookingReq: Goto Confirmed Booking Body
     * @return GoToApiResponse
     */
    GoToApiResponse confirmBookingWithActivities(String reservationId, GotoConfirmBookingRequest confirmBookingReq);

    /**
     * Cancel Goto Booking
     *
     * @param reservationId: booking Id
     * @return GoToApiResponse
     */
    GoToApiResponse cancelBooking(String reservationId);

    /**
     * Cancel Unpaid Goto Bookings
     *
     */
    void cancelUnpaidBookings();

    /**
     * Get GoTo related configurations
     *
     * @return GoToApiResponse
     */
    //@Administrator
    GoToConfiguration getConfiguration();

    /**
     * Save goto related configuration
     *
     * @param configuration: GoTo configuration for hotel
     * @return <code>true</code> if saved, <code>false</code> if failed
     */
    //@Administrator
    boolean saveConfiguration(GoToConfiguration configuration);

    /**
     * Save goto related configuration
     *
     * @param reservationId: GoTo booking reservation id
     * @param room: Cancelled PmsBookingRooms
     */
    void sendEmailForCancelledRooms(String reservationId, String channel, PmsBookingRooms room);
}
