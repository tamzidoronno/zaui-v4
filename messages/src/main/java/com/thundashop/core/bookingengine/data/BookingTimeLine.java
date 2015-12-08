/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class BookingTimeLine implements Comparable<BookingTimeLine> {
    public String id = UUID.randomUUID().toString();
    public List<String> bookingIds = new ArrayList();
    public int count;
    public Date start;
    public Date end;
    private final int totalAvailableSpots;

    public BookingTimeLine(int totalAvailableSpots) {
        this.totalAvailableSpots = totalAvailableSpots;
    }

    public boolean intercepts(Booking booking) {
        if (booking.startDate.before(end) && booking.startDate.after(start)) {
            return true;
        }
        
        if (booking.endDate.before(end) && booking.endDate.after(start)) {
            return true;
        }
        
        if (booking.endDate.equals(end) && booking.startDate.equals(start)) {
            return true;
        }
        
        return false;
    }

    public List<BookingTimeLine> split(Booking booking) {
        ArrayList<BookingTimeLine> splitted = new ArrayList();
        
        if (booking.startDate.before(end) && booking.startDate.after(start)) {
            BookingTimeLine one = cloneMe();
            BookingTimeLine two = cloneMe();
            BookingTimeLine three = cloneMe();
            
            two.count++;
            
            one.end = booking.startDate;
            two.start = booking.startDate;
            three.start = two.end;
            three.end = booking.endDate;
            
            splitted.add(one);
            splitted.add(two);
            splitted.add(three);
        }
        
        if (booking.endDate.before(end) && booking.endDate.after(start)) {
            BookingTimeLine one = cloneMe();
            BookingTimeLine two = cloneMe();
            BookingTimeLine three = cloneMe();
            
            two.count++;
            
            one.start = booking.startDate;
            one.end = start;
            two.end = booking.endDate;
            three.start = booking.endDate;
            
            splitted.add(one);
            splitted.add(two);
            splitted.add(three);
        }
        
        if (booking.endDate.equals(end) && booking.startDate.equals(start)) {
            count++;
            splitted.add(this);
        }
        
        return splitted;
    }

    private BookingTimeLine cloneMe() {
        BookingTimeLine line = new BookingTimeLine(totalAvailableSpots);
        line.count = count;
        line.start = start;
        line.end = end;
        return line;
    }

    @Override
    public int compareTo(BookingTimeLine o) {
        return start.compareTo(o.start);
    }
    
    public int getAvailableSpots() {
        return totalAvailableSpots - count;
    }
}
