/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine;

import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingTimeLine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Add bookings to this class and it will flatten the bookings on a timeline
 * with a count for each timeline.
 * 
 * @author ktonder
 */
public class BookingTimeLineFlatten {
    public Map<String, BookingTimeLine> timeLines = new HashMap();
    
    private final int totalAvailableSpots;

    public BookingTimeLineFlatten(int totalAvailableSpots) {
        this.totalAvailableSpots = totalAvailableSpots;
    }
    
    public void add(Booking booking) {
        List<BookingTimeLine> overlappingLines = getLinesOverlapping(booking);
        if (overlappingLines.size() > 0) {
            split(overlappingLines, booking);
        } else {
            addBookingTimeLine(booking);
        }
    } 

    public List<BookingTimeLine> getTimelines() {
        List<BookingTimeLine> lines = new ArrayList(timeLines.values());
        Collections.sort(lines);
        return lines;
    }

    private List<BookingTimeLine> getLinesOverlapping(Booking booking) {
        ArrayList<BookingTimeLine> timelines = new ArrayList();
        
        for (BookingTimeLine line : timeLines.values()) {
            if (line.intercepts(booking)) {
                timelines.add(line);
            }
        }
        
        return timelines;
    }

    private void addBookingTimeLine(Booking booking) {
        BookingTimeLine line = new BookingTimeLine(totalAvailableSpots);
        line.bookingIds.add(booking.id);
        line.start = booking.startDate;
        line.end = booking.endDate;
        line.count = 1;
        timeLines.put(line.id, line);
    }

    private void split(List<BookingTimeLine> overlappingLines, Booking booking) {
        for (BookingTimeLine timeLine : overlappingLines) {
            timeLines.remove(timeLine.id);
            List<BookingTimeLine> splittedLines = timeLine.split(booking);
            for (BookingTimeLine splitted : splittedLines) {
                timeLines.put(splitted.id, splitted);
            }
        }
    }

    public boolean canAdd(Booking booking) {
        for (BookingTimeLine itemLine : timeLines.values()) {
            if (itemLine.intercepts(booking) && itemLine.getAvailableSpots() < 1) {
                return false;
            }
        }
        
        return true;
    }
    
}
