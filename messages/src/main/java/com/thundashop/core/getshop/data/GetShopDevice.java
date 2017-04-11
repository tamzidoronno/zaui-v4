package com.thundashop.core.getshop.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.pmsmanager.TimeRepeaterData;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class GetShopDevice extends DataCommon {
    public boolean needSaving = false;
    public boolean beingUpdated = false;
    public boolean isFailed = false;
    public boolean isAwake = false;
    public boolean needPriority = false;
    public String name;
    public Integer maxNumberOfCodes = 20;
    public Integer batteryStatus = 0;
    public Integer zwaveid = -1;
    public String type;
    public HashMap<Integer, GetShopLockCode> codes = new HashMap();
    public Date lastTriedUpdate = null;
    public LinkedList<Date> accessLog = new LinkedList();
    public HashMap<String, Object> instances;
    public Date batteryLastUpdated;
    public String serverSource = "";
    TimeRepeaterData openingHoursData = null;
 
    public void setDevice(ZWaveDevice device) {
        zwaveid = device.id;
        type = device.data.deviceTypeString.value + "";
        name = device.data.givenName.value + "";
        if(device.data != null && device.data.isFailed != null && device.data.isFailed.value != null) {
            isFailed = new Boolean(device.data.isFailed.value + "");
        }
        if(device.instances != null && device.instances.get("0") != null &&
                device.instances.get("0").commandClasses.get("128") != null &&
                device.instances.get("0").commandClasses.get("128").data != null &&
                device.instances.get("0").commandClasses.get("128").data.last != null &&
                device.instances.get("0").commandClasses.get("128").data.last.value != null)
        {
            batteryStatus = new Double(device.instances.get("0").commandClasses.get("128").data.last.value + "").intValue();
            Calendar lastupdate = Calendar.getInstance();
            lastupdate.setTimeInMillis(device.instances.get("0").commandClasses.get("128").data.last.updateTime * 1000);
            batteryLastUpdated = lastupdate.getTime();
        } else {
            batteryStatus = -1;
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
            cal.add(Calendar.MINUTE, 15);
            if(cal.getTime().after(new Date())) {
                return false;
            }
        }
        for(GetShopLockCode code : codes.values()) {
            if(code.needUpdate()) {
                return true;
            }
        }
        
        return oldBatteryStatus();
    }

    public boolean warnAboutCodeNotSet() {
        for(GetShopLockCode code : codes.values()) {
            if(code.needWarnAboutNotUpdated()) {
                return true;
            }
        }
        return false;
    }

    public boolean oldBatteryStatus() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if(hour > 10) {
            return false;
        }

            
        if(batteryLastUpdated == null) {
            return true;
        }
        cal.setTime(batteryLastUpdated);
        cal.add(Calendar.DAY_OF_YEAR, 2);
        if(cal.getTime().before(new Date())) {
            return true;
        }
        return false;
    }

    public int numberOfCodesNeedsUpdate() {
        int count = 0;
        for(GetShopLockCode code : codes.values()) {
            if(code.needUpdate()) {
                count++;
            }
        }
        return count;
    }
}
