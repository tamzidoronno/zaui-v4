package com.thundashop.core.bcomratemanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.DataCommon;

public class RateManagerConfig extends DataCommon {
//    public String hotelId = "44126423";
    public String hotelId = "";
    public String hotelName;
    public String hotelChainCode;
    
    @Administrator
    public String username = "";
    
    @Administrator
    public String password = "";
}
