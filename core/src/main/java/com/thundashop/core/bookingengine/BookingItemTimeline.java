/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine;

import java.util.Date;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 *
 * @author ktonder
 */
public class BookingItemTimeline {
    public String bookingItemId = "";
    public HashMap<Date, Date> dates = new HashMap();
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
        
        for (Date startDate : dates.keySet()) {
            Date endDate = dates.get(startDate);
            
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
    
    public void add(Date start, Date end) {
        if (start == null || end == null) {
            return;
        }
        
        dates.put(start, end);
    }

    public boolean notInUseAtAll() {
        return dates.isEmpty();
    }

    void setOptimalBookingTimeLineId(String uuid) {
        this.optimalTimeLineId = uuid;
    }
    
}
