package com.thundashop.core.gotohub;

import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.gotohub.dto.*;

import java.util.Date;
import java.util.List;


@GetShopApi
public interface IGoToManager {
    /**
     * Get hotel information
     *
     * @return
     */
    public FinalResponse getHotelInformation();

    /**
     * Get all {@link com.thundashop.core.gotohub.dto.RatePlan} of all types of room using virtual room methodology!
     * TODO need parameter for Date range on future
     *
     * @return {@link List} of {@link RoomType}
     * @throws Exception
     */
    public FinalResponse getRoomTypeDetails() throws Exception;

    /**
     * Inventory allotment within date of the hotel
     * TODO need parameter for Date range on future
     *
     * @return {@link List} of {@link PriceAllotment}
     * @throws Exception
     */
    public FinalResponse getPriceAndAllotment(Date from, Date to) throws Exception;

    public FinalResponse saveBooking(Booking booking);
    public FinalResponse confirmBooking(String reservationId);
    public FinalResponse cancelBooking(String reservationId);

    /**
     * Get GoTo related configurations
     * @return {@link GoToManager}
     */
    //@Administrator
    public GoToConfiguration getConfiguration();

    /**
     * Save goto related configuration
     * @param configuration
     * @return <code>true</code> if saved, <code>false</code> if failed
     */
    //@Administrator
    public boolean saveConfiguration(GoToConfiguration configuration);
}
