/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mecamanager;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class MecaCarRequestKilomters implements Serializable {
    private Date requestedLastTime;
    
    private Date requestedLastTimeSms;
    
    private Date lastReceivedKilomters = new Date(0);
    
    private boolean requestActive = false;
    
    public void reset() {
        requestedLastTime = null;
        requestedLastTimeSms = null;
    }
    
    public boolean canSendPushNotification() {
        if (requestedLastTime == null || lastReceivedKilomters == null)
            return true;
        
        Date date1 = getDateInFuture(lastReceivedKilomters, 1, 0);
        Date date2 = getDateInFuture(requestedLastTime, 1, 0);
        Date date3 = getDateInFuture(requestedLastTime, 0, 1);
        Date toDay = new Date();
        
        if (date1.before(toDay) && date2.before(toDay))
            return true;
        
        if (date1.before(toDay) && date3.before(toDay))
            return true;
        
        return false;
    }

    public void markReceivedKilomters() {
        requestActive = false;
        requestedLastTime = new Date();
        lastReceivedKilomters = new Date();
    }

    public void setRequestedLastTime(Date requestedLastTime) {
        this.requestedLastTime = requestedLastTime;
    }

    public void setLastReceivedKilomters(Date lastReceivedKilomters) {
        this.lastReceivedKilomters = lastReceivedKilomters;
    }

    private Date getDateInFuture(Date lastReceivedKilomters, int month, int extraDays) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(lastReceivedKilomters);
        
        cal.add(Calendar.MONTH, month);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.add(Calendar.DAY_OF_MONTH, extraDays);
        
        return cal.getTime();
    }

    /**
     * Returns true if kilometers has not been received 7 days
     * after push notifications has been sent to request kilometers.
     * 
     * @return 
     */
    public boolean canSendSmsNotification() {
        
        Date date1 = getDateInFuture(lastReceivedKilomters, 1, 7);
        
        Date toDay = new Date();
        
        if (date1.before(toDay)) {
            
            if (requestedLastTime == null) 
                return true;
            
            Date date2 = getDateInFuture(requestedLastTime, 0, 7);
            
            if (date2.before(toDay) && requestedLastTimeSms == null) {
                return true;
            }
        }
        
        return false;
    }

    public void markAsSentSmsNotification() {
        requestActive = true;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, 1);
        requestedLastTimeSms = cal.getTime();
    }
    
    public void markAsSentPushNotification() {
        requestActive = true;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, 1);
        requestedLastTime = cal.getTime();
    }
    
}
