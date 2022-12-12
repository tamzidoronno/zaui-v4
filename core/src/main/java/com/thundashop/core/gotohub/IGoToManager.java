package com.thundashop.core.gotohub;

import java.util.Date;

import com.thundashop.core.common.ForceAsync;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.gotohub.dto.GoToApiResponse;
import com.thundashop.core.gotohub.dto.GoToConfiguration;
import com.thundashop.core.gotohub.dto.GotoBookingRequest;
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
    public GoToApiResponse getHotelInformation();

    /**
     * Get all {@link com.thundashop.core.gotohub.dto.RatePlan} of all types of room using virtual room methodology!
     *
     * @return GoToApiResponse
     */
    public GoToApiResponse getRoomTypeDetails();

    /**
     * Inventory allotment within date of the hotel
     *
     * @param from, to
     * @return GoToApiResponse
     */
    @ForceAsync
    public GoToApiResponse getPriceAndAllotmentWithDate(Date from, Date to);

    /**
     * Inventory allotment of the hotel (1 month)
     *
     * @return GoToApiResponse
     */
    @ForceAsync
    public GoToApiResponse getPriceAndAllotment();

    /**
     * Save new Goto Booking
     *
     * @param booking data
     * @return GoToApiResponse
     */
    public GoToApiResponse saveBooking(GotoBookingRequest booking);

    /**
     * Confirm Goto Booking
     *
     * @param reservationId: booking Id
     * @return GoToApiResponse
     */
    public GoToApiResponse confirmBooking(String reservationId);

    /**
     * Cancel Goto Booking
     *
     * @param reservationId: booking Id
     * @return GoToApiResponse
     */
    public GoToApiResponse cancelBooking(String reservationId);

    /**
     * Cancel Unpaid Goto Bookings
     *
     */
    public void cancelUnpaidBookings();

    /**
     * Get GoTo related configurations
     *
     * @return GoToApiResponse
     */
    //@Administrator
    public GoToConfiguration getConfiguration();

    /**
     * Save goto related configuration
     *
     * @param configuration: GoTo configuration for hotel
     * @return <code>true</code> if saved, <code>false</code> if failed
     */
    //@Administrator
    public boolean saveConfiguration(GoToConfiguration configuration);

    /**
     * Save goto related configuration
     *
     * @param reservationId: GoTo booking reservation id
     * @param room: Cancelled PmsBookingRooms
     */
    void sendEmailForCancelledRooms(String reservationId, String channel, PmsBookingRooms room);

    void sendCancelBookingAcknowledgement(String reservationId) throws Exception;
}
