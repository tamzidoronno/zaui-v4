/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine;

import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author ktonder
 */
public class BookingItemTimeline {
    public String bookingItemId = "";
    public HashMap<String, BookingDate> dates = new HashMap();
    public String bookingItemTypeId;
    public String optimalTimeLineId ;

    public BookingItemTimeline(String id, String typeId) {
        this.bookingItemId = id;
        this.bookingItemTypeId = typeId;
    }
    
    public boolean isAvailable(Date start, Date end) {
        if (start == null || end == null) {
            return true;
        }
        
        for (String bookingId : dates.keySet()) {
            Date startDate = dates.get(bookingId).startDate;
            Date endDate = dates.get(bookingId).endDate;
            
            long StartDate1 = start.getTime();
            long StartDate2 = startDate.getTime();
            
            long EndDate1 = end.getTime();
            long EndDate2 = endDate.getTime();
   
            if ((StartDate1 < EndDate2) && (EndDate1 > StartDate2) ) {
                return false;
            }
        }
        
        return true;
    }
    
    public void add(String bookingId, Date start, Date end) {
        if (start == null || end == null) {
            return;
        }
        
        BookingDate date = new BookingDate();
        date.bookingId = bookingId;
        date.startDate = start;
        date.endDate = end;
        
        dates.put(bookingId, date);
    }

    public boolean notInUseAtAll() {
        return dates.isEmpty();
    }

    void setOptimalBookingTimeLineId(String uuid) {
        this.optimalTimeLineId = uuid;
    }

    void remove(String id) {
        dates.remove(id);
    }
    
}
