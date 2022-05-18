package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

public class PmsAdditionalItemInformation extends DataCommon {
    private Date lastUsed = null;
    private Date lastCleaned = null;
    private List<Date> cleaningDates = new ArrayList();
    public HashMap<Long, String> markedDirtyDatesLog = new HashMap();
    public HashMap<Long, String> markedDirtyDatesLogUser = new HashMap();
    public HashMap<String, PmsInventory> inventory = new HashMap();
    public HashMap<Long, String> cleanedByUser = new HashMap();
    public String itemId = "";
    public Integer squareMetres = 0;
    public boolean hideFromCleaningProgram = false;
    
    //This is a variable that can be set when sending messages.
    public String textMessageDescription = "";
    
    @Transient
    private boolean isClean = false;
    
    
    @Transient
    private boolean isCleanNotToday = false;
    
    @Transient
    boolean inUse;
    
    @Transient
    boolean inUseByCleaning;
    public boolean closed = false;
    public boolean closedByCleaningProgram = false;

    Boolean isClean() {
        isCleanNotToday = isClean(false);
        return isClean(true);
    }
    
    public void markCleaned(String userId) {
        Date cleaned = new Date();
        lastCleaned = cleaned;
        cleaningDates.add(cleaned);
        cleanedByUser.put(cleaned.getTime(), userId);
    }
    
    public void markCleanedWithoutLogging() {
        lastCleaned = new Date();
        isClean = true;
    }

    public void markDirty(String pmsBookingRoomId, String storeId) {
        this.markDirty(pmsBookingRoomId, storeId, null);
    }
    public void markDirty(String pmsBookingRoomId, String storeId, String userId) {
        if(storeId.equals("fd2fecef-1ca1-4231-86a6-0ec445fbac83")) {
            if(!isClean()) { return; }
        }
        lastUsed = new Date();
        long time = new Date().getTime();
        markedDirtyDatesLog.put(time, pmsBookingRoomId);
        if(userId != null) markedDirtyDatesLogUser.put(time, userId);
    }

    private boolean isCleanedToday() {
        if(lastCleaned != null) {
            if(isToday(lastCleaned.getTime())) {
                return true;
            }
        } else {
            for(Date date : cleaningDates) {
                if(isToday(date.getTime())) {
                    return true;
                }
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
        lastCleaned = new Date();
    }

    public void addCleaningDate(Date date) {
        cleaningDates.add(date);
        if(lastCleaned == null || lastCleaned.before(date) && date != null) {
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
        } else if(checkToday && isCleanedToday()) {
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
        HashMap<Long, Date> test = new HashMap();
        for(Date cleaning : cleaningDates) {
            test.put(cleaning.getTime(), cleaning);
        }
        return new ArrayList(test.values());
    }

    public void setLastUsed(Date lastMarkedAsDirty) {
        lastUsed = lastMarkedAsDirty;
    }

    Boolean isUsedToday() {
        if(lastUsed == null) {
            return false;
        }
        
        Calendar now = Calendar.getInstance();
        Calendar timeToCheck = Calendar.getInstance();
        timeToCheck.setTime(lastUsed);
        int hourOfDay = timeToCheck.get(Calendar.HOUR_OF_DAY);
        if(hourOfDay < 6) {
            return false;
        }
        return (now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == timeToCheck.get(Calendar.DAY_OF_YEAR));
    }

    void forceMarkDirty() {
        isClean = false;
        lastCleaned = null;
        lastUsed = null;
        
        Calendar cal1 = Calendar.getInstance();
        
        String todayDate = cal1.get(Calendar.YEAR) + "_"  + cal1.get(Calendar.DAY_OF_YEAR);
        
        List<Date> toRemove = new ArrayList();
        for(Date cleaningDate : cleaningDates) {
            cal1.setTime(cleaningDate);
            String other = cal1.get(Calendar.YEAR) + "_"  + cal1.get(Calendar.DAY_OF_YEAR);
            if(other.equals(todayDate)) {
                toRemove.add(cleaningDate);
            }
        }
        
        cleaningDates.removeAll(toRemove);
    }

    Date getLastUsed() {
        return lastUsed;
   }

    void setLastCleaned(Date latestCleaningBeforeThat) {
        lastCleaned = latestCleaningBeforeThat;
    }

    void removeCleaning(Date latestCleaning) {
        cleanedByUser.remove(latestCleaning.getTime());
        isClean = false;
    }
}
