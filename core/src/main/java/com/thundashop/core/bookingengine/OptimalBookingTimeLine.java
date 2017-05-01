/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine;

import com.thundashop.core.bookingengine.data.Booking;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class OptimalBookingTimeLine {
    public String uuid = UUID.randomUUID().toString();
    public List<Booking> bookings = new ArrayList();

    long getDistanceBetweenBookings(Booking booking) {
        long shortestDistance = Long.MAX_VALUE;
        
        for (Booking ibooking : bookings) {
            long startTimeBookingInLine = ibooking.startDate.getTime();
            long endTimeBookingInLine = ibooking.startDate.getTime();
            
            long timeBetweenNext = booking.startDate.getTime() - endTimeBookingInLine;
            long timeBetweenPrev = startTimeBookingInLine - booking.endDate.getTime();
            
            if (timeBetweenNext < 0 && timeBetweenPrev > 0) {
                shortestDistance = timeBetweenPrev;
            }
            
            if (timeBetweenPrev < 0 && timeBetweenNext > 0) {
                shortestDistance = timeBetweenNext;
            }
            
        }
        
        return shortestDistance;
    }
}
