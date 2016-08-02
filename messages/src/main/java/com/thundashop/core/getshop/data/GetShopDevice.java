package com.thundashop.core.getshop.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GetShopDevice extends DataCommon {
    public boolean needSaving = false;
    public boolean beingUpdated = false;
    public String name;
    public Integer maxNumberOfCodes = 20;
    public Integer batteryStatus = 0;
    public Integer zwaveid = -1;
    public String type;
    public HashMap<Integer, GetShopLockCode> codes = new HashMap();

    public void setDevice(ZWaveDevice device) {
        zwaveid = device.id;
        type = device.data.deviceTypeString.value + "";
    }
    
    public boolean isLock() {
        return type.equals("Secure Keypad");
    }

    public void removeCode(String code) {
        for(GetShopLockCode c : codes.values()) {
            if(c.code.equals(code)) {
                c.needToBeRemoved = new Date();
            }
        }
    }

    public boolean needUpdate() {
        for(GetShopLockCode code : codes.values()) {
            if(code.needUpdate()) {
                return true;
            }
        }
        
        return false;
    }

    public boolean warnAboutCodeNotSet() {
        for(GetShopLockCode code : codes.values()) {
            if(code.needWarnAboutNotUpdated()) {
                return true;
            }
        }
        return false;
    }
}
