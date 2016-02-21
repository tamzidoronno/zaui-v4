/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.data.RegistrationRules;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.pmsmanager.PmsBooking.PriceType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.mongodb.morphia.annotations.Transient;

public class PmsBookingRooms implements Serializable {
    public String bookingItemTypeId = "";
    public String bookingItemId = "";
    public String pmsBookingRoomId = UUID.randomUUID().toString();
    public List<PmsGuests> guests = new ArrayList();
    public PmsBookingDateRange date = new PmsBookingDateRange();
    public Integer numberOfGuests = 0;
    public double count = 1;
    public Double price = 0.0;
    public double taxes = 8;
    public String bookingId;
    String code = "";
    public boolean isClean = true;
    public Integer intervalCleaning = null;
    public boolean addedByRepeater = false;
    public Date invoicedTo = null;
    public boolean keyIsReturned = false;
    
    //Processor stuff.
    public boolean ended = false;
    public List<String> notificationsSent = new ArrayList();
    boolean addedToArx = false;
    boolean canBeAdded = true;
    boolean isAddon = false;
    
    /**
     * Finalized entries
     */
    @Transient
    public Booking booking;
    @Transient
    public BookingItem item;
    @Transient
    public BookingItemType type;

    boolean isActiveOnDay(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.set(Calendar.HOUR_OF_DAY, 17);
        if(cal.getTime().after(date.start) && cal.getTime().before(date.end)) {
            return true;
        }
        
        return false;
    }
    
    public Double getDailyPrice(Integer type, Calendar cal) {
        int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        if(type.equals(PriceType.monthly)) {
            return price / days;
        }
        
        if(type.equals(PriceType.daily)) {
            return price;
        }
        
        if(type.equals(PriceType.interval)) {
            return price;
        }
        
        if(type.equals(PriceType.progressive)) {
            return price;
        }
        
        throw new UnsupportedOperationException("Not implented yet");
    }

    boolean isEnded() {
        Date now = new Date();
        boolean result = now.after(date.end);
        return result;
    }

    boolean isStarted() {
        return date.start.before(new Date());
    }

    boolean isStartingToday() {
        if(date.start == null) {
            return false;
        }
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        if(cal.get(Calendar.HOUR_OF_DAY) < 6) {
            return false;
        }
        
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(date.start);
        String startString = startCal.get(Calendar.DAY_OF_YEAR) + "-" + startCal.get(Calendar.YEAR);
        String nowString = cal.get(Calendar.DAY_OF_YEAR) + "-" + cal.get(Calendar.YEAR);
        
        if(startString.equals(nowString)) {
            return true;
        }
        
        return false;
    }

    boolean isEndingToday() {
        if(date.end == null) {
            return false;
        }
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(date.end);
        String startString = startCal.get(Calendar.DAY_OF_YEAR) + "-" + startCal.get(Calendar.YEAR);
        String nowString = cal.get(Calendar.DAY_OF_YEAR) + "-" + cal.get(Calendar.YEAR);
        
        if(startString.equals(nowString)) {
            return true;
        }
        
        return false;
    }
}
