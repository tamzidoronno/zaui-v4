/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.pmsmanager.PmsBooking.PriceType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author boggi
 */
public class PmsBookingRooms implements Serializable {
    public String bookingItemTypeId = "";
    public String bookingItemId = "";
    public String pmsBookingRoomId = UUID.randomUUID().toString();
    public List<PmsGuests> guests = new ArrayList();
    public PmsBookingDateRange date = new PmsBookingDateRange();
    public Integer numberOfGuests = 0;
    public double count = 1;
    public double price = 0;
    public double taxes = 8;
    public String bookingId;
    String code = "";
    
    /**
     * Finalized entries
     */
    @Transient
    public Booking booking;

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
        
        throw new UnsupportedOperationException("Not implented yet");
    }
}
