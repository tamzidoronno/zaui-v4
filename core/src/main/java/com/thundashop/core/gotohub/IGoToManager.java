package com.thundashop.core.gotohub;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.gotohub.dto.*;

import java.util.List;


@GetShopApi
public interface IGoToManager {

    /**
     * Get hotel information
     * @return
     */
    public Hotel getHotelInformation();

    /**
     * Get all {@link com.thundashop.core.gotohub.dto.RatePlan} of all types of room using virtual room methodology!
     * TODO need parameter for Date range on future
     * @return {@link List} of {@link RoomType}
     * @throws Exception
     */
    public List<RoomType> getRoomTypeDetails() throws Exception;

    /**
     * Inventory allotment within date of the hotel
     * TODO need parameter for Date range on future
     * @return {@link List} of {@link PriceAllotment}
     * @throws Exception
     */
    public List<PriceAllotment> getPriceAndAllotment() throws Exception;
    public FinalResponse saveBooking(Booking booking);
    public FinalResponse confirmBooking(String reservationId);
    public FinalResponse cancelBooking(String reservationId);
}
