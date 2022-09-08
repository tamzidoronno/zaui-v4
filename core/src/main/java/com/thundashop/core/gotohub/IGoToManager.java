package com.thundashop.core.gotohub;

import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.gotohub.dto.*;

import java.util.Date;

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
    public GoToApiResponse getPriceAndAllotmentWithDate(Date from, Date to);

    /**
     * Inventory allotment of the hotel (1 month)
     *
     * @return GoToApiResponse
     */
    public GoToApiResponse getPriceAndAllotment();

    /**
     * Save new Goto Booking
     *
     * @param booking data
     * @return GoToApiResponse
     */
    public GoToApiResponse saveBooking(Booking booking);

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
     * @param incrementBookingId: pms booking incremental id
     */
    public void sendEmailForCancelledBooking(String reservationId, int incrementBookingId);
}
