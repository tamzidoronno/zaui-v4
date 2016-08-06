package com.thundashop.core.getshop.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GetShopDevice extends DataCommon {
    public boolean needSaving = false;
    public boolean beingUpdated = false;
    public boolean isFailed = false;
    public boolean isAwake = false;
    public String name;
    public Integer maxNumberOfCodes = 20;
    public Integer batteryStatus = 0;
    public Integer zwaveid = -1;
    public String type;
    public HashMap<Integer, GetShopLockCode> codes = new HashMap();
    public Date lastTriedUpdate = null;

    public void setDevice(ZWaveDevice device) {
        zwaveid = device.id;
        type = device.data.deviceTypeString.value + "";
        name = device.data.givenName.value + "";
        if(device.data != null && device.data.isFailed != null && device.data.isFailed.value != null) {
            isFailed = new Boolean(device.data.isFailed.value + "");
        }
        if(device.data != null && device.data.isAwake != null && device.data.isAwake.value != null) {
            isAwake = new Boolean(device.data.isAwake.value + "");
        }
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
        if(lastTriedUpdate != null) {
            //Wait an hour before trying again on this one.
            Calendar cal = Calendar.getInstance();
            cal.setTime(lastTriedUpdate);
            cal.add(Calendar.MINUTE, -5);
            if(cal.getTime().before(new Date())) {
                return false;
            }
        }
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
