package com.thundashop.core.getshop.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.pmsmanager.TimeRepeaterData;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
    public TimeRepeaterData openingHoursData = null;
    public List<String> masterLocks = new ArrayList();
    public String openingType = "";
    public String lockState = "unkown"; //unkown, locked, open.
    
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
        return type.equals("Secure Keypad") || type.equals("getshop_lock");
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
            cal.add(Calendar.MINUTE, 5);
            if(cal.getTime().after(new Date())) {
                return false;
            }
        }
        
        if(isLocked() && !needForceRemove()) {
            return false;
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

    public boolean isSameSource(String domain) {
        String source = serverSource;
        if(source == null || source.trim().isEmpty()) {
            source = "default";
        }
        
        if(domain == null || domain.trim().isEmpty()) {
            domain = "default";
        }
        
        return domain.equals(source);
    }

    public boolean isLocked() {
        return lockState != null && lockState.equals("locked");
    }

    public boolean isOpen() {
        return lockState != null && lockState.equals("open");
    }

    public boolean needForceRemove() {
        for(GetShopLockCode code : codes.values()) {
            if(code.needForceRemove()) {
                return true;
            }
        }
        
        return false;
    }

    public boolean hasCode(String curCode) {
        for(GetShopLockCode code : codes.values()) {
            if(code != null && code.fetchCodeToAddToLock().equals(curCode)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSubLock() {
        if(masterLocks == null) {
            return false;
        }
        return !masterLocks.isEmpty();
    }

    public GetShopLockCode getNextAvailableCode() {
        finalizeCodes();
        return codes.values().stream()
                .filter(code -> code.canUseForGuests())
                .findFirst()
                .orElse(null);
    }

    public void markInUse(Integer slot, String source) {
        finalizeCodes();
        GetShopLockCode code = codes.get(slot);
        if (code != null) {
            code.setInUse(true);
            code.usedBySource = source;
        }
    }

    private void finalizeCodes() {
        for (Integer slot : codes.keySet()) {
            codes.get(slot).slot = slot;
        }
    }

    public void removeCodeBySlotId(Integer slot) {
        GetShopLockCode code = codes.get(slot);
        if (code != null) {
            code.needToBeRemoved = new Date();
        }
    }
}
