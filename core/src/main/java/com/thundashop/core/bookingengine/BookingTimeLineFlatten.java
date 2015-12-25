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
import java.util.Collection;
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
public class BookingTimeLineFlatten implements Serializable { 
    public Map<String, BookingTimeLine> timeLines = new HashMap();
    
    private final int totalAvailableSpots;
    private final String bookingItemTypeId;

    public BookingTimeLineFlatten(int totalAvailableSpots, String bookingItemTypeId) {
        this.totalAvailableSpots = totalAvailableSpots;
        this.bookingItemTypeId = bookingItemTypeId;
    }
    
    public void add(Booking booking) {
        if (!booking.bookingItemTypeId.equals(bookingItemTypeId)) {
            return;
        }
        
        if (bookingOverlapFullWidth(booking)) {
            return;
        }
        
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
            
            if (line.intercepts(booking) ) {
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
        
        if (overlappingLines.size() > 1) {
            for (BookingTimeLine timeLine : overlappingLines) {
                timeLines.remove(timeLine.id);
                for (BookingTimeLine partLine : timeLine.partialSplit(booking, true, false)) {
                    timeLines.put(partLine.id, partLine);
                }
            }
        } else {
            for (BookingTimeLine timeLine : overlappingLines) {
                timeLines.remove(timeLine.id);
                List<BookingTimeLine> splittedLines = timeLine.split(booking, true);
                for (BookingTimeLine splitted : splittedLines) {
                    timeLines.put(splitted.id, splitted);
                }
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
    
    private boolean timeLineWithinBooking(BookingTimeLine timeLine, Booking booking) {
        /*
         * Timeline:     |---------------|
         * Booking:  |------------------------|
         */
        if (timeLine.start.after(booking.startDate) && timeLine.end.before(booking.endDate)) {
            return true;
        }
        
        /**
         * Timeline:       |------------------|
         * Booking:  |------------------------|      
         */
        if (timeLine.start.after(booking.startDate) && timeLine.end.equals(booking.endDate)) {
            return true;
        }

        /**
         * Timeline: |------------------|
         * Booking:  |------------------------|      
         */
        if (timeLine.start.equals(booking.startDate) && timeLine.end.before(booking.endDate)) {
            return true;
        }
        
        return false;
    }
    
    private boolean bookingWithinBooking(BookingTimeLine timeLine, Booking booking) {
        /**
         * Timeline: |------------------------|
         * Booking:     |------------------|
         */
        if (timeLine.start.before(booking.startDate) && timeLine.end.after(booking.endDate)) {
            return true;
        }
        
        /**
         * Timeline: |------------------------|
         * Booking:  |------------------|
         */
        if (timeLine.start.equals(booking.startDate) && timeLine.end.after(booking.endDate)) {
            return true;
        }
        
        /**
         * Timeline: |------------------------|
         * Booking:        |------------------|
         */
        if (timeLine.start.before(booking.startDate) && timeLine.end.equals(booking.endDate)) {
            return true;
        }

        return false;
    }
    
    private boolean isAFullWidthOverlap(BookingTimeLine timeLine, Booking booking) {
                   
        if (timeLineWithinBooking(timeLine, booking))
            return true;
        
        if (bookingWithinBooking(timeLine, booking))
            return true;

        return false;
    }

    private boolean bookingOverlapFullWidth(Booking booking) {
        System.out.println("Booking: " + booking.getInformation());
        List<BookingTimeLine> timeLinesOverlaping = new ArrayList();
        
        for (BookingTimeLine timeLine : timeLines.values()) {
            if (isAFullWidthOverlap(timeLine, booking)) {
                timeLinesOverlaping.add(timeLine);
            }
        }
        
        Collections.sort(timeLinesOverlaping);
        List<BookingTimeLine> linesToAdd = new ArrayList();
        
        for (BookingTimeLine timeLine : timeLinesOverlaping) {
            List<BookingTimeLine> splittedLines = timeLine.split(booking, false);
            if (!splittedLines.isEmpty()) {
                this.timeLines.remove(timeLine.id);
                linesToAdd.addAll(splittedLines);
            }
        } 
        
        if (!timeLinesOverlaping.isEmpty()) {
            doPartialSplit(booking, linesToAdd);
        } 
        
        addAll(linesToAdd);
        
        return !timeLinesOverlaping.isEmpty();
    }

    private void doPartialSplit(Booking booking, List<BookingTimeLine> linesToAdd) {
        List<BookingTimeLine> allTimeLines = new ArrayList();
        allTimeLines.addAll(timeLines.values());
        allTimeLines.addAll(linesToAdd);
        
        List<BookingTimeLine> allTimeLines2 = new ArrayList();
        allTimeLines2.addAll(timeLines.values());
        
        Collections.sort(allTimeLines);

        int count = 0;
        for (BookingTimeLine line : linesToAdd) {
            if (booking.within(line.start, line.end)) {
                count++;
            }
        }
        
        String specialPartial = "";
        if (count == 1 && linesToAdd.size() == 1) {
            allTimeLines2.addAll(linesToAdd);
            specialPartial = linesToAdd.get(0).id;
        }
        
        
        boolean needToAddExtra = getLinesOverlapping(booking).size() == 1;
         
        for (BookingTimeLine timeLine : allTimeLines2) {
            if (booking.within(timeLine.start, timeLine.end)) {
                timeLines.remove(timeLine.id);
                
                linesToAdd.removeIf(o -> o.id.equals(timeLine.id));
                linesToAdd.addAll(timeLine.partialSplit(booking, timeLine.id.equals(specialPartial),needToAddExtra));    
            }
        }
    }
    
    private void addAll(List<BookingTimeLine> splittedLines) {
        for (BookingTimeLine timeLine : splittedLines) {
            if (timeLine.start.equals(timeLine.end)) {
                continue;
            }
            timeLines.put(timeLine.id, timeLine);
        }
    }
    
}
