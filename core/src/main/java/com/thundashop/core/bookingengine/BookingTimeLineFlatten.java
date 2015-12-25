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
        for (BookingTimeLine timeLine : overlappingLines) {
            timeLines.remove(timeLine.id);
            List<BookingTimeLine> splittedLines = timeLine.split(booking, true);
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
    
    private boolean isAFullWidthOverlap(BookingTimeLine timeLine, Booking booking) {
                   
        /*
         * Timeline:     |---------------|
         * Booking:  |------------------------|
         */
        if (timeLine.start.after(booking.startDate) && timeLine.end.before(booking.endDate)) {
            return true;
        }

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

    private boolean bookingOverlapFullWidth(Booking booking) {
        List<BookingTimeLine> timeLinesOverlaping = new ArrayList();
        
        for (BookingTimeLine timeLine : timeLines.values()) {
            if (isAFullWidthOverlap(timeLine, booking)) {
                timeLinesOverlaping.add(timeLine);
            }
        }
        
        Collections.sort(timeLinesOverlaping);
        
        List<BookingTimeLine> linesToAdd = new ArrayList();
        int i = 1;
        for (BookingTimeLine timeLine : timeLinesOverlaping) {
            this.timeLines.remove(timeLine.id);
            List<BookingTimeLine> splittedLines = timeLine.split(booking, true);
            linesToAdd.addAll(splittedLines);
        }
        
        List<String> toRemove = new ArrayList();
        List<BookingTimeLine> timeLinesToTest = new ArrayList();
        timeLinesToTest.addAll(timeLines.values());
        
        if (!timeLinesOverlaping.isEmpty()) {
            for (BookingTimeLine timeLine : timeLinesToTest) {
                boolean firstInRow = isLastOrFirstInList(timeLine, timeLinesToTest).equals("FIRST");
                boolean lastInRow = isLastOrFirstInList(timeLine, timeLinesToTest).equals("LAST");
                boolean isFullWidthOverlap = isAFullWidthOverlap(timeLine, booking);
                
                if ((firstInRow || lastInRow) && timeLine.intercepts(booking)) {
                    toRemove.add(timeLine.id);
                    linesToAdd.addAll(timeLine.partialSplit(booking, firstInRow, lastInRow, isFullWidthOverlap));
                }
            }
        }
        
        for (String removeId : toRemove) {
            timeLines.remove(removeId);
        }
        
        addAll(linesToAdd);
        
        return !timeLinesOverlaping.isEmpty();
    }

    private void addAll(List<BookingTimeLine> splittedLines) {
        for (BookingTimeLine timeLine : splittedLines) {
            if (timeLine.start.equals(timeLine.end)) {
                continue;
            }
            timeLines.put(timeLine.id, timeLine);
        }
    }

    private String isLastOrFirstInList(BookingTimeLine timeLine, List<BookingTimeLine> timeLinesToTest) {
        List<BookingTimeLine> timeLinesToCheck = new ArrayList(timeLinesToTest);
        Collections.sort(timeLinesToCheck);
        
        if (timeLinesToCheck.size() == 1) {
            return "";
        }
        
        int i = 1;
        for (BookingTimeLine itimeLine : timeLinesToCheck) {
            if (itimeLine.equals(timeLine) && i == 1) {
                return "FIRST";
            }
            
            if (itimeLine.equals(timeLine) && i == timeLinesToCheck.size()) {
                return "LAST";
            }
            
            i++;
        }
        
        return "";
    }
    
}
