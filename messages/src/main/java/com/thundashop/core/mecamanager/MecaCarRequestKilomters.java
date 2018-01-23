/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mecamanager;

import com.thundashop.core.messagemanager.SmsLogEntry;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class MecaCarRequestKilomters implements Serializable {
    
    private Date lastReceivedKilomters = new Date(0);
    private Date nextRequestDate = null;
    private int requestCount = 0;
    
    private List<MessageLog> logs = new ArrayList();
    
    public void reset() {
    }
    
    public void markReceivedKilomters() {
        lastReceivedKilomters = new Date();
    }

    public void setLastReceivedKilomters(Date lastReceivedKilomters) {
        this.lastReceivedKilomters = lastReceivedKilomters;
    }

    /**
     * Returns true if kilometers has not been received 7 days
     * after push notifications has been sent to request kilometers.
     * 
     * @return 
     */
    public boolean canSendNotification() {
        Date today = new Date();
        
        if (today.after(nextRequestDate)) {
            return true;
        }
        
        return false;
    }

    public void markAsSentSmsNotification(MecaFleet fleet, String textMessage, String number) {
        String message = "Sms: " + textMessage + ", Telefonnr: " + number;
        String type = "sms";
        addLogEntry(message, type, fleet.naggingInterval);
        calculateNextRequest(fleet);
    }
    
    public void markAsSentPushNotification(MecaFleet fleet) {
        addLogEntry("Push notification sendt", "push", fleet.naggingInterval);
    }

    private void addLogEntry(String message, String type, String naggingInterval) {
        MessageLog entry = new MessageLog();
        entry.message = message;
        entry.type = type + "("+naggingInterval+")";
        logs.add(entry);
    }
    
    private void calculateNextRequest(MecaFleet fleet) {
        requestCount++;
        
        if (fleet.naggingInterval.equals("frequency_none")) {
            setNextMainRequest();
        } else if (fleet.naggingInterval.equals("frequency_daily")) {
            handleDailyRequest();
        } else if (fleet.naggingInterval.equals("frequency_weekly")) {
            handleWeeklyRequest();
        } else {
            setNextMainRequest();
        }
    }

    private void setNextMainRequest() {
        requestCount = 0;
        nextRequestDate = getNextMainRequest();
    }

    private Date getNextMainRequest() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 7);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void handleDailyRequest() {
        if (requestCount <= 2) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(nextRequestDate);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            nextRequestDate = cal.getTime();
        } else {
            setNextMainRequest();
        }
    }

    private void handleWeeklyRequest() {
        if (requestCount <= 2) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(nextRequestDate);
            cal.add(Calendar.DAY_OF_MONTH, 7);
            nextRequestDate = cal.getTime();
        } else {
            setNextMainRequest();
        }
    }

    public boolean setNextIfNull() {
        if (nextRequestDate == null) {
            nextRequestDate = getNextMainRequest();
            return true;
        }
        
        return false;
    }
}
