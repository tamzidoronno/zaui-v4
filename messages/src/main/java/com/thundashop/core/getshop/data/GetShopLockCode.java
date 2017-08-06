
package com.thundashop.core.getshop.data;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class GetShopLockCode implements Serializable {
    public String code;
    public Boolean used = false;
    public Date lastUsed = null;
    public Date codeRefreshed = null;
    public Date addedToLock = null;
    public Date needToBeRemoved = null;
    public Integer slot = 0;
    private boolean forceRemove = false;
    public String usedBySource = "";
    
    public Integer getSlot() {
        return slot;
    }
    
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
        
        if(forceRemove) {
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

    public boolean canUseForGuests() {
        if (isMasterCode()) {
            return false;
        }
        
        return canUse();
    }
    
    public String fetchCode() {
        used = true;
        lastUsed = new Date();
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
        used = false;
    }

    public boolean needToBeRemoved() {
        if(needToBeRemoved != null) {
            return true;
        }
        return false;
    }

    public boolean inUse() {
        return used;
    }

    public boolean needForceRemove() {
        return forceRemove;
    }
    
    public void forceRemove() {
        forceRemove = true;
    }

    public void unsetForceRemove() {
        forceRemove = false;
    }

    public void setInUse(boolean inUse) {
        this.used = inUse;
    }

    // This is not good enough, need to implement proper mastercode handling.
    private boolean isMasterCode() {
        return slot < 5;
    }
}
