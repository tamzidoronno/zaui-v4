/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.eventbooking;

import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.calendarmanager.data.EntryComment;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.Editor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class Event extends DataCommon {
    public String bookingItemId = "";
    
    public String subLocationId = "";

    public String eventHelderUserId = "";
    
    public String freeTextEventHelder = "";
    
    public List<Day> days = new ArrayList();
    
    public boolean markedAsReady = false;
    
    public List<Reminder> reminders = new ArrayList();
    
    public boolean isCanceled = false;
    
    public boolean smsReminderSent;
    
    public boolean mailReminderSent;
    
    public boolean questBackSent = false;
    
    public boolean isLocked = false;
    
    public boolean isHidden = false;
    
    public String extraInformation = "";
    
    public List<EntryComment> eventComments = new ArrayList();
    
    @Editor
    public HashMap<String, byte[]> encryptedPersonalIds = new HashMap();
    
    @Editor
    public HashMap<String, List<UserComment>> comments = new HashMap();
    
    @Editor
    public HashMap<String, String> participationStatus = new HashMap();
    
    /**
     * Key = userid
     * Value = invoicegroupid
     */
    @Editor
    public HashMap<String, String> groupInvoiceStatus = new HashMap();
    
    @Transient
    public BookingItem bookingItem;
    
    @Transient
    public BookingItemType bookingItemType;
    
    @Transient
    public Location location;
    
    @Transient
    public SubLocation subLocation;
    
    @Transient
    public Date mainStartDate;
    
    @Transient
    public Date mainEndDate;
    
    @Transient
    public String eventPage = "";
    
    @Transient
    public boolean canBook = false;
    
    @Transient
    public boolean canBookWaitingList = false;
    
    @Transient
    public Double price = -1D;
    
    @Transient
    boolean isInFuture;

    void setMainDates() {
        if (days.size() > 0) {
            Comparator<Day> byDay = (Day o1, Day o2)->o1.startDate.compareTo(o2.startDate);
             Collections.sort(days, byDay);
            mainStartDate = days.get(0).startDate;
            mainEndDate = days.get(0).endDate;
        }
    }

    public String getBookingItemId() {
        if (bookingItemId == null) {
            return "";
        }
        
        return bookingItemId;
    }

    public String getEventHelderUserId() {
        return eventHelderUserId != null ? eventHelderUserId : "";
    }
    
    public String getSubLocationId() {
        return subLocationId != null ? subLocationId : "";
    }

    boolean sameDates(Event event2) {
        if (days.size() != event2.days.size()) {
            return false;
        }
        
        int i = 0;
        for (Day day : days) {
            Day day2 = event2.days.get(i);
            if (day.startDate != null && day2.startDate != null && !day2.startDate.equals(day.startDate)) {
                return false;
            }
            
            if (day.endDate != null && day2.endDate != null && !day2.endDate.equals(day.endDate)) {
                return false;
            }
            i++;
        }
        
        return true;
    }
    
    public boolean isInFuture() {
        boolean isInfure = false;
        for (Day day : days) {
            if (day.isInFuture()) {
                isInfure = true;
            }
        }
        
        return isInfure;    
    }

    public Date getLastDate() {
        Date higestDate = null;
        
        for (Day day : days) {
            if (higestDate == null) {
                higestDate = day.endDate;
                continue;
            }
            
            if (day.endDate.after(higestDate))
                higestDate = day.endDate;
        }
        
        return higestDate;
    }
}