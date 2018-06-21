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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 * @author ktonder
 */
public class BookingTimeLine implements Comparable<BookingTimeLine>, Serializable {
    public String id = UUID.randomUUID().toString();
    public String originalSplitId = "";
    public List<String> bookingIds = new ArrayList();
    public Integer count;
    public Date start;
    public Date end;
    public int totalAvailableSpots;

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

    public boolean withIn(Date start, Date end) {
        long StartDate1 = start.getTime();
        long StartDate2 = this.start.getTime();
        long EndDate1 = end.getTime();
        long EndDate2 = this.end.getTime();
        return (StartDate1 <= EndDate2) && (StartDate2 <= EndDate1);
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
        return "From: " + dateFormat.format(start) + " to: " + dateFormat.format(end) + " count: " + count + " : " + id;
    }   
}