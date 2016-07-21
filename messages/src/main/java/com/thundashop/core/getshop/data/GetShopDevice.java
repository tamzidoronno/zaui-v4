package com.thundashop.core.getshop.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetShopDevice extends DataCommon {
    public String name;
    public Integer maxNumberOfCodes = 14;
    public Integer batteryStatus = 0;
    public Integer zwaveid = -1;
    public String type;
    HashMap<Integer, GetShopLockCode> codes = new HashMap();

    public GetShopDevice(ZWaveDevice device) {
        zwaveid = device.id;
        type = device.data.deviceTypeString.value + "";
    }
    
    public boolean isLock() {
        return type.equals("Secure Keypad");
    }
}
