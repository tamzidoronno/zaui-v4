/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine;

import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingTimeLine;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Add bookings to this class and it will flatten the bookings on a timeline
 * with a count for each timeline.
 * 
 * @author ktonder
 */
public class BookingTimeLineFlatten implements Serializable { 
    private List<Booking> bookings = new ArrayList();
    private final int totalAvailableSpots;
    private final String bookingItemTypeId;
    Date end;
    Date start;

    public BookingTimeLineFlatten(int totalAvailableSpots, String bookingItemTypeId) {
        this.totalAvailableSpots = totalAvailableSpots;
        this.bookingItemTypeId = bookingItemTypeId;
    }
    
    public void add(Booking booking) {
        if (booking.bookingItemTypeId.equals(bookingItemTypeId)) {
            bookings.add(booking);
        }
    } 

    public List<BookingTimeLine> getTimelines() {
        sortBookings();
        List<Date> markers = getMarkers();

        List<BookingTimeLine> timeLines = new ArrayList();
        Date prev = null;
        for (Date marker : markers) {
            if (prev != null) {
                BookingTimeLine timeLine = createTimeLine(prev, marker);
                if (timeLine.count > 0) {
                    timeLines.add(timeLine);
                }
            }
            prev = marker;
        }
        
        return timeLines;
    }

    boolean canAdd(Booking booking) {
        for (BookingTimeLine itemLine : getTimelines()) {
            if (itemLine.intercepts(booking) && itemLine.getAvailableSpots() < 1) {
                System.out.println("Booking is full between: " + itemLine.start + " and " + itemLine.end);
                return false;
            }
        }
        
        return true;
    }

    private void sortBookings() {
        Collections.sort(bookings, new Comparator<Booking>(){
            @Override
            public int compare(Booking o1, Booking o2) {
                return o1.startDate.compareTo(o2.startDate);
            }
       });
    }

    private List<Date> getMarkers() {
        Set<Date> treeSet = new TreeSet();
        for (Booking booking : bookings) {
            treeSet.add(booking.startDate);
            treeSet.add(booking.endDate);
        }
        
        return new ArrayList(treeSet);
    }

    private BookingTimeLine createTimeLine(Date prev, Date marker) {
        List<String> bookingIds = new ArrayList();
        
        bookings.stream().filter(o -> o.interCepts(prev, marker))
                .forEach(o -> bookingIds.add(o.id));
        
        BookingTimeLine timeLine = createNewTimeLine(prev, marker, bookingIds);
        return timeLine;
    }

    private BookingTimeLine createNewTimeLine(Date prev, Date end, List<String> bookingIds) {
        BookingTimeLine timeLine = new BookingTimeLine(totalAvailableSpots);
        timeLine.start = prev;
        timeLine.end = end;
        timeLine.count = bookingIds.size();
        timeLine.bookingIds = bookingIds;
        return timeLine;
    }

    /**
     * @todo Make unit tests.
     * @param interval Number of seconds intervals.
     * @return 
     */
    public List<BookingTimeLine> getTimelines(Integer interval) {
        LinkedList<BookingTimeLine> result = new LinkedList();
        List<BookingTimeLine> allLines = getTimelines();
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        while(true) {
            Date startTime = cal.getTime();
            cal.add(Calendar.SECOND, interval);
            Date endTime = cal.getTime();
            
            BookingTimeLine tmpLine = createNewTimeLine(startTime, endTime, new ArrayList());
            BookingTimeLine bookingsFound = allLines.stream()
                    .filter(o -> o.withIn(startTime, endTime))
                    .max(new Comparator<BookingTimeLine>() {
                        @Override
                        public int compare(BookingTimeLine o1, BookingTimeLine o2) {
                            return o1.count.compareTo(o2.count);
                        }
                    }).orElse(tmpLine);
            
            tmpLine.count = bookingsFound.count;
            tmpLine.bookingIds = bookingsFound.bookingIds;
            result.add(tmpLine);
            
            if(endTime.after(end)) {
                break;
            }
        }
        
        return result;
    }
}