package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

public class PmsAdditionalItemInformation extends DataCommon {
    private Date lastUsed = null;
    private Date lastCleaned = null;
    private List<Date> cleaningDates = new ArrayList();
    public String itemId = "";
    
    @Transient
    private boolean isClean = false;
    
    @Transient
    boolean inUse;

    Boolean isClean() {
        return isClean(true);
    }
    
    public void markCleaned() {
        Date cleaned = new Date();
        lastCleaned = cleaned;
        cleaningDates.add(cleaned);
    }
    
    public void markDirty() {
        lastUsed = new Date();
    }

    private boolean isCleanedToday() {
        for(Date date : cleaningDates) {
            if(isToday(date.getTime())) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isToday(long timestamp) {
        Calendar now = Calendar.getInstance();
        Calendar timeToCheck = Calendar.getInstance();
        timeToCheck.setTimeInMillis(timestamp);
        return (now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == timeToCheck.get(Calendar.DAY_OF_YEAR));
    }

    void addCleaningDate() {
        cleaningDates.add(new Date());
    }

    public void addCleaningDate(Date date) {
        cleaningDates.add(date);
        if(lastCleaned == null || lastCleaned.before(date)) {
            lastCleaned = date;
        }
    }

    public Boolean isClean(boolean checkToday) {
        isClean = false;
        if(lastCleaned == null) {
            return false;
        }
        
        if(lastUsed == null) {
            isClean = true;
        } else if(isCleanedToday() && checkToday) {
            isClean = true;
        } else {
            isClean = lastCleaned.after(lastUsed);
        }
        
        return isClean;
    }

    boolean unsetMarkedDirtyPastThirtyMinutes() {
        Date toRemove = null;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -30);
        Date now = cal.getTime();
        if(lastUsed != null && lastUsed.after(now)) {
            lastUsed = null;
            return true;
        }
        return false;
    }

    List<Date> getAllCleaningDates() {
        return cleaningDates;
    }

}
