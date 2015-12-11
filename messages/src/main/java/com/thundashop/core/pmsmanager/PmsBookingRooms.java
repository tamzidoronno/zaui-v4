/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.data.Booking;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author boggi
 */
public class PmsBookingRooms {
    public static class PriceType {
        public static Integer daily = 1;
        public static Integer monthly = 2;
        public static Integer weekly = 3;
        public static Integer minutly = 4;
        public static Integer hourly = 5;
        public static Integer secondly = 6;
    }
    
    public String bookingItemTypeId = "";
    public String pmsBookingRoomId = UUID.randomUUID().toString();
    public List<PmsGuests> guests = new ArrayList();
    public PmsBookingDateRange date = new PmsBookingDateRange();
    public Integer numberOfGuests = 0;
    public double count = 1;
    public double price = 108;
    public double taxes = 8;
    public Integer priceType = 1;
    public String bookingId;
    
    /**
     * Finalized entries
     */
    @Transient
    public Booking booking;
}
