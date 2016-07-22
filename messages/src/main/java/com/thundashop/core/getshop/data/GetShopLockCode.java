
package com.thundashop.core.getshop.data;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class GetShopLockCode implements Serializable {
    String code;
    Boolean used = false;
    Date codeRefreshed = null;
    Date addedToLock = null;
    Date needToBeRemoved = null;
    Integer slot = 0;
    
    public void refreshCode() {
        Integer newcode = new Random().nextInt(799999) + 200000;
        code = newcode + "";
        resetOnLock();
        used = false;
    }

    public boolean needWarnAboutNotUpdated() {
        if(!needUpdate()) {
            return false;
        }
        if(codeRefreshed==null) {
            return false;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, -6);
        Date past = cal.getTime();
        if(past.after(codeRefreshed)) {
            return true;
        }
        return false;
    }
    
    public boolean needUpdate() {
        if(addedToLock == null) {
            return true;
        }
        if(needToBeRemoved != null) {
            return true;
        }
        return false;
    }

    public void setCode(String code) {
        this.code = code;
        doUpdate();
    }

    private void doUpdate() {
        addedToLock = null;
        needToBeRemoved = null;
    }

    public boolean canUse() {
        if(used) {
            return false;
        }
        if(!needUpdate()) {
            return true;
        }
        return false;
    }

    public String fetchCode() {
        used = true;
        return code;
    }

    public String fetchCodeToAddToLock() {
        return code;

    }

    public void setAddedToLock() {
        addedToLock = new Date();
    }
    public boolean isAddedToLock() {
        return addedToLock != null;
    }

    public void resetOnLock() {
        codeRefreshed = new Date();
        addedToLock = null;
        needToBeRemoved = null;
    }
}
