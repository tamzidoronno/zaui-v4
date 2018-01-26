/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine;

import com.thundashop.core.bookingengine.data.Booking;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class OptimalBookingTimeLine {
    public String uuid = UUID.randomUUID().toString();
    public List<Booking> bookings = new ArrayList();
    public String bookingItemId = "";

    long getLongestDistanceBetweenBookings(Booking booking) {
        long retVal = 0;
        
        for (Booking ibooking : bookings) {
            long startTimeBookingInLine = ibooking.startDate.getTime();
            long endTimeBookingInLine = ibooking.startDate.getTime();
            
            long timeBetweenNext = booking.startDate.getTime() - endTimeBookingInLine;
            long timeBetweenPrev = startTimeBookingInLine - booking.endDate.getTime();
            
            if (timeBetweenNext > retVal) {
                retVal = timeBetweenNext;
            }
            
            if (timeBetweenPrev > retVal) {
                retVal = retVal;
            }
            
            if (ibooking.interCepts(booking.startDate, booking.endDate)) {
                return 0;
            }
        }
        
        return retVal;
    }
    
    long getDistanceBetweenBookings(Booking booking) {
        long shortestDistance = Long.MAX_VALUE;
        
        Collections.sort(bookings, Booking.sortByStartDate());
        
        for (Booking ibooking : bookings) {
            long endTimeBookingInLine = ibooking.endDate.getTime();
            
            long timeBetweenNext = booking.startDate.getTime() - endTimeBookingInLine;
            if (timeBetweenNext < shortestDistance) {
                shortestDistance  = timeBetweenNext;
            }

            if (shortestDistance < 0 ) {
                continue;
            }
            
            if (ibooking.interCepts(booking.startDate, booking.endDate)) {
                return Long.MAX_VALUE;
            }
            
        }
        
        return shortestDistance;
    }

    public boolean canAddBooking(Booking booking) {
        for (Booking ibooking : bookings) {
            if (ibooking.bookingItemTypeId != null && !ibooking.bookingItemTypeId.equals(booking.bookingItemTypeId)) {
                return false;
            }
        }
        
        if (booking.bookingItemId != null && !booking.bookingItemId.isEmpty()) {
            for (Booking ibooking : bookings) {
                if (ibooking.bookingItemId != null && !ibooking.bookingItemId.isEmpty() && !ibooking.bookingItemId.equals(booking.bookingItemId)) {
                    return false;
                }
            }
        }
        
        for (Booking ibooking : bookings) {
            if (ibooking.interCepts(booking.startDate, booking.endDate)) {
                return false;
            }
        }
        
        return true;
    }
}
