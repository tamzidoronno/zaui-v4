
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
    boolean forcedOpen = false;
    boolean forcedOpenCompleted = false;
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
    Date forcedOpenDate;
    boolean forcedOpenNeedClosing = false;
    
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
        if(date.end == null || date.start == null) {
            return false;
        }
        
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
        return isEnded(now);
    }

    boolean isEnded(Date day) {
        boolean result = day.after(date.end);
        return result;
    }

    boolean isStarted() {
        return isStarted(new Date());
    }
    
    boolean isStarted(Date when) {
        return date.start.before(when);
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
        return isEndingToday(new Date());
    }

    boolean isEndingToday(Date toDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(toDate);
        
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(date.end);
        String startString = startCal.get(Calendar.DAY_OF_YEAR) + "-" + startCal.get(Calendar.YEAR);
        String nowString = cal.get(Calendar.DAY_OF_YEAR) + "-" + cal.get(Calendar.YEAR);
        
        if(startString.equals(nowString)) {
            return true;
        }
        
        return false;
    }

    boolean isSame(PmsBookingRooms alreadyAdded) {
        if(!alreadyAdded.bookingItemTypeId.equals(bookingItemTypeId)) {
            return false;
        }
        if(alreadyAdded.date.start.equals(date.start) && alreadyAdded.date.end.equals(date.end)) {
            return true;
        }
        return false;
    }

    boolean needInvoicing(NewOrderFilter filter) {
       
        if(filter.forceInvoicing) {
            return true;
        }
        
        if(filter.onlyEnded && date.end.after(filter.endInvoiceAt)) {
            return false;
        }
        
        if(filter.onlyEnded && sameDay(new Date(), date.end)) {
            return false;
        }
        
        if(filter.prepayment && invoicedTo != null) {
            //If invoiced into the future. 
            Date toInvoiceFrom = invoicedTo;
            if(filter.prepaymentDaysAhead >= 0) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(invoicedTo);
                cal.add(Calendar.DAY_OF_YEAR, filter.prepaymentDaysAhead*-1);
                toInvoiceFrom = cal.getTime();
            }
            if(toInvoiceFrom.after(new Date())) {
                return false;
            }
        }
        
        if(invoicedTo == null) {
            return true;
        }
        
        if(date.end.before(invoicedTo) || sameDay(date.end, invoicedTo)) {
            return false;
        }
        
        if(invoicedTo.before(filter.endInvoiceAt) && !sameDay(invoicedTo, filter.endInvoiceAt)) {
            return true;
        }
        
        return false;
    }
    

    Date getInvoiceStartDate(NewOrderFilter filter) {
        if(date.start.after(filter.startInvoiceFrom)) {
            return date.start;
        }
        
        if(invoicedTo != null) {
            return invoicedTo;
        }
        
        return filter.startInvoiceFrom;
    }

    Date getInvoiceEndDate(NewOrderFilter filter, PmsBooking booking) {
        if(filter.increaseUnits > 0) {
            filter.endInvoiceAt = addTimeUnits(filter.increaseUnits, booking, filter.startInvoiceFrom);
        }
        
        if(date.end.before(filter.endInvoiceAt)) {
            return date.end;
        }
        
        return filter.endInvoiceAt;
    }

    private Date addTimeUnits(Integer increaseUnits, PmsBooking booking, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if(booking.priceType.equals(PmsBooking.PriceType.monthly)) {
            cal.add(Calendar.MONTH, increaseUnits);
        }
        if(booking.priceType.equals(PmsBooking.PriceType.daily)) {
            cal.add(Calendar.DAY_OF_YEAR, increaseUnits);
        }
        if(booking.priceType.equals(PmsBooking.PriceType.weekly)) {
            cal.add(Calendar.WEEK_OF_YEAR, increaseUnits);
        }
        if(booking.priceType.equals(PmsBooking.PriceType.hourly)) {
            cal.add(Calendar.HOUR_OF_DAY, increaseUnits);
        }
        return cal.getTime();
            
    }

    private boolean sameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                  cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
