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
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class Event extends DataCommon {
    public String bookingItemId = "";
    
    public List<Day> days = new ArrayList();
    
    @Transient
    public BookingItem bookingItem;
    
    @Transient
    public BookingItemType bookingItemType;
    
    
}
