/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.eventbooking;

import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class Event extends DataCommon {
    public String bookingItemId = "";
    
    public String subLocationId = "";
    
    public List<Day> days = new ArrayList();
    
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

    void setMainDates() {
        if (days.size() > 0) {
            Comparator<Day> byDay = (Day o1, Day o2)->o1.startDate.compareTo(o2.startDate);
             Collections.sort(days, byDay);
            mainStartDate = days.get(0).startDate;
            mainEndDate = days.get(0).endDate;
        }
    }
    
}
