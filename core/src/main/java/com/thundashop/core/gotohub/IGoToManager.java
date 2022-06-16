package com.thundashop.core.gotohub;

import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.gotohub.dto.Hotel;
import com.thundashop.core.gotohub.dto.PriceAllotment;
import com.thundashop.core.gotohub.dto.RoomType;

import java.util.List;


@GetShopApi
public interface IGoToManager {

    /**
     * To change to access token internally
     * @param newToken
     * @return <code>true</code> if modfied, <code>false/code> if failed to change
     */
    public boolean changeToken(String newToken);

    /**
     * Check access token is working or not
     * @return
     * @throws Exception
     */
    public String testConnection();

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
}
