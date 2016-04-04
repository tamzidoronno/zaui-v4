/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author boggi
 */
public class PmsManagerDoorSurveilance {
    
    PmsManager manager;

    PmsManagerDoorSurveilance(PmsManager aThis) {
        this.manager = aThis;
    }
    
    public void checkStatus() throws Exception {
        closeForTheDay();
        checkForSendCloseSignal();
    }
    
    private void closeForTheDay() throws Exception {
        
        if (!manager.getConfigurationSecure().keepDoorOpenWhenCodeIsPressed) {
            return;
        }
        
        String closeAtEnd = manager.getConfigurationSecure().closeAllDoorsAfterTime;
        String[] time = closeAtEnd.split(":");
        int hour = new Integer(time[0]);
        int minute = new Integer(time[1]);
        
        Calendar cal = Calendar.getInstance();
        if(cal.get(com.ibm.icu.util.Calendar.HOUR_OF_DAY) > hour) {
            manager.arxManager.closeAllForTheDay();
        } else if(cal.get(com.ibm.icu.util.Calendar.HOUR_OF_DAY) == hour && cal.get(com.ibm.icu.util.Calendar.MINUTE) >= minute) {
            manager.arxManager.closeAllForTheDay();
        } else {
            manager.arxManager.clearCloseForToday();
        }
    }
    
    private List<PmsBooking> getAllConfirmedNotDeleted() {
        List<PmsBooking> res = new ArrayList(manager.bookings.values());
        List<PmsBooking> toRemove = new ArrayList();
        for (PmsBooking booking : res) {
            if (booking.rooms == null) {
                toRemove.add(booking);
            }
            if (booking.isDeleted) {
                toRemove.add(booking);
            }
            if (booking.sessionId != null && !booking.sessionId.isEmpty()) {
                toRemove.add(booking);
            }
            if (!booking.confirmed) {
                toRemove.add(booking);
            }
        }
        res.removeAll(toRemove);
        return res;
    }

    private void checkForSendCloseSignal() throws Exception {
        if(!manager.getConfiguration().keepDoorOpenWhenCodeIsPressed) {
            return;
        }
        
        for(PmsBooking booking : getAllConfirmedNotDeleted()) {
            boolean needSaving = false;
            for(PmsBookingRooms room : booking.rooms) {
                if(room.isEnded() && !room.sentCloseSignal) {
                    room.sentCloseSignal = true;
                    manager.arxManager.pmsDoorAction(room.code, "close");
                    manager.logEntry("Ran close on door", booking.id, room.bookingItemId);
                    needSaving = true;
                }
            }
            if(needSaving) {
                manager.saveBooking(booking);
            }
        }
    }
}
