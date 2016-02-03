/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.pmsmanager.TimeRepeaterData;
import java.util.ArrayList;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class BookingItem extends DataCommon {
    public String bookingItemTypeId; 
    
    public String bookingItemName = "";
    
    public String bookingItemAlias = "";
    
    public String description = "";
    
    /**
     * How many bookings is allowed to be on this bookingitem.
     */
    public int bookingSize = 1;
    
    /**
     * If a booking an event is full the candidates can sign up 
     * for a waiting list if this is set to higher then 0.
     */
    public int waitingListSize = 0;
    
    /**
     * This bookingitem will be full if this counter is hit.
     * if 0 then the bookingSize variable is the one that is used.
     */
    public int fullWhenCountHit = 1;
     
    /**
     * A bookingitem has a list of availabilities, 
     * this availibilites can not overlap eachother within an BookingItem.
     */
    public List<String> availabilitieIds = new ArrayList();
    
    /**
     * A set of id that is bookings that has been added to this booking item.
     */
    public List<String> bookingIds = new ArrayList<String>();
    
    /**
     * A set of id that is bookings that has been added to this booking item.
     */
    public List<String> waitingListBookingIds = new ArrayList<String>();
    
    /**
     * This is calculated if the booking is full or not.
     */
    @Transient
    public boolean isFull = false;
    
    /**
     * This value is calculated based on bookings and availability count.
     */
    @Transient
    int freeSpots = 0;
    
    @Transient
    public List<Availability> availabilities = new ArrayList();
    
    /**
     * Who owns the bookingItem. 
     */
    public String bookingOwnerUserId = "";
    
    public RegistrationRules rules = null;
    
    /**
     * Timelines for when this item is open.
     */
    public TimeRepeaterData openingHours;
}
