package com.thundashop.core.bcomratemanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.DataCommon;
import java.util.HashMap;

public class RateManagerConfig extends DataCommon {
//    public String hotelId = "44126423";
    public String hotelId = "";
    public String hotelName;
    public String hotelChainCode;
    
    //<GetShopRoomTypeId, bookingComRateManagerId>
    public HashMap<String, String> roomTypeIdMap = new HashMap();
    
    @Administrator
    public String username = "";
    
    @Administrator
    public String password = "";
}
