/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine.data;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class BookingTimeLine implements Comparable<BookingTimeLine>, Serializable {
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
        
        long StartDate1 = booking.startDate.getTime();
        long StartDate2 = start.getTime();
        long EndDate1 = booking.endDate.getTime();
        long EndDate2 = end.getTime();
        return (StartDate1 < EndDate2) && (StartDate2 < EndDate1);
    }

    public List<BookingTimeLine> split(Booking booking, boolean returnSelf) {
        ArrayList<BookingTimeLine> splitted = new ArrayList();
    
        /**
         * case(1) 
         * Timeline: |-----------|
         * Booking:  |---------------------------
         * 
         * or
         * case(2) 
         * Timeline:   |-----------|
         * Booking:  |---------------------------
         * 
         * or
         * case(3) 
         * Timeline:                |-----------|
         * Booking:  ---------------------------|
         * 
         * or
         * case(4) 
         * Timeline:               |-----------|
         * Booking:  ---------------------------|
         */
        if (
                (booking.startDate.equals(start) && booking.endDate.after(end)) // case 1
                ||
                (booking.startDate.before(start) && booking.endDate.after(end)) // case 2
                ||
                (booking.startDate.before(start) && booking.endDate.equals(end)) // case 3
                ||
                (booking.startDate.before(start) && booking.endDate.after(end)) // case 4
            ) {
            count++;
            splitted.add(this);
            return splitted;
        }
        
        
        
        /**
         * This is: 
         * Timeline: |--------------------------------------------|
         * Booking:          |------------------------|
         */
        if (booking.startDate.after(start) && booking.endDate.before(end)) {
            BookingTimeLine one = cloneMe();
            BookingTimeLine two = cloneMe();
            BookingTimeLine three = cloneMe();
            
            two.count++;
            
            one.start = start;
            one.end = booking.startDate;
            
            two.start = booking.startDate;
            two.end = booking.endDate;
            
            three.start = booking.endDate;
            three.end = end;
            
            splitted.add(one);
            splitted.add(two);
            splitted.add(three);
            return splitted;
        }

        /**
         * This is: 
         * Timeline: |--------------------|
         * Booking:          |------------------------
         * 
         */
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
        
        /**
         * This is: 
         * Timeline:      |--------------------|
         * Booking : ----------------|
         */
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
        
        /**
         * This is: 
         * Timeline: |--------------------|
         * Booking:  |--------------------|
         */
        if (booking.endDate.equals(end) && booking.startDate.equals(start)) {
            count++;
            splitted.add(this);
        }
        
        if (splitted.isEmpty() && returnSelf) {
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

    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM-yyyy HH:mm:ss");
        return "From: " + dateFormat.format(start) + " to: " + dateFormat.format(end) + " count: " + count;
    }

    public List<BookingTimeLine> partialSplit(Booking booking, boolean firstInRow, boolean lastInRow, boolean isFullWidthOverlap)  {
        
        List<BookingTimeLine> partSplitts = new ArrayList();
        
        System.out.println(booking.getInformation());
        
        /*
         * Timeline: |----------| 
         * Booking :      |-------------|
         *
         */
//        if (firstInRow) {
        if (booking.startDate.after(start) || booking.startDate.equals(start)) {
            BookingTimeLine one = cloneMe();
            BookingTimeLine two = cloneMe();
            
            one.start = start;
            one.end = booking.startDate;
            
            two.start = booking.startDate;
            two.end = end;
            if (firstInRow)
                two.count++;
            
            if (lastInRow)
                two.count--;
            
            
            partSplitts.add(one);
            partSplitts.add(two);
        }
        
        

        
        /*
         * Timeline:       |---------|
         * Booking : |----------|
         */
        if (booking.endDate.before(end) || booking.endDate.equals(end)) {
            BookingTimeLine one = cloneMe();
            BookingTimeLine two = cloneMe();
            
            one.start = start;
            one.end = booking.endDate;
            one.count++;
            
            
            two.start = booking.endDate;
            two.end = end;
            
            
            partSplitts.add(one);
            partSplitts.add(two);
        }
        
        return partSplitts;
    }

    
}
