package com.thundashop.core.gotohub;

import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.gotohub.dto.Hotel;
import com.thundashop.core.gotohub.dto.PriceAllotment;
import com.thundashop.core.gotohub.dto.RoomType;

import java.util.List;


@GetShopApi
public interface IGoToManager {
    public boolean changeToken(String newToken);
    public String testConnection() throws Exception;
    public Hotel getHotelInformation();
    public List<RoomType> getRoomTypeDetails() throws Exception;
    public List<PriceAllotment> getPriceAndAllotment() throws Exception;

}
