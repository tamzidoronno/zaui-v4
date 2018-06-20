/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine;

import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingTimeLineFlatten;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author ktonder
 */
public class BookingsBetweenCalculator {
    private final List<List<Booking>> possibleCombos = new ArrayList();
    private final Map<String, Booking> bookingsToCheck;
    private final Date start;
    private final Date end;
    private List<Booking> unassignedBookings;
    private List<Booking> bestCombo = new ArrayList();
    private long bestComboLength = -1;
    private final OptimalBookingTimeLine timeLine;
    private final long totalLength;
    private double coveragePercent;

    public BookingsBetweenCalculator(Date start, Date end, List<Booking> unassignedBookings, OptimalBookingTimeLine timeLine) {
        this.bookingsToCheck = new HashMap();
        this.start = start;
        this.end = end;
        this.totalLength = end.getTime() - start.getTime();
        this.unassignedBookings = unassignedBookings;
        this.timeLine = timeLine;
    }
    
    public void process() {
        possibleCombos.clear();
        bookingsToCheck.clear();
        bestCombo.clear();
        long start = System.currentTimeMillis();
        filterBookingsThatCanBeUsed();
        findAllCombos();
        findBestCombo();
        setCoveragePercent();
        printBestCombo();
    }

    public void setUnassignedBookings(List<Booking> unassignedBookings) {
        this.unassignedBookings = unassignedBookings;
    }

    private void filterBookingsThatCanBeUsed() {
        unassignedBookings.stream()
                .filter(o -> o.bookingIsWithinOrEqual(start, end))
                .forEach(o -> bookingsToCheck.put(o.id, o));
    }

    private void findAllCombos() {
        BookingTimeLineFlatten flatten = new BookingTimeLineFlatten(Integer.MAX_VALUE, "all");
        flatten.setStart(start);
        flatten.setEnd(end);
        bookingsToCheck.values().stream()
                .forEach(b -> flatten.add(b));
        
        List<String> bookingIds = flatten.getAllPossibleCombos();
        for (String bookingIdString : bookingIds) {
            String[] bookingIdsToUse = bookingIdString.split(",");
            List<Booking> bookingsCombo = new ArrayList();
            for (String id : bookingIdsToUse) {
                Booking booking = bookingsToCheck.get(id);
                bookingsCombo.add(booking);
            }
            possibleCombos.add(bookingsCombo);
        }
    }

    private void findBestCombo() {
        long highest = 0;
        
        for (List<Booking> bookings : possibleCombos) {
            long stayLength = getStayLength(bookings);
            if (stayLength > highest) {
                bestCombo = bookings;
                bestComboLength = stayLength;
                highest = stayLength;
            }
        }
        
    }

    private long getStayLength(List<Booking> bookings) {
        if (bookings == null)
            return -1;
        
        return bookings.stream()
                .mapToLong(o -> o.getStayLength())
                .sum();
    }

    private void printBestCombo() {
//        if (bestCombo.size() > 0 )
//            System.out.println(" ========== " + start + " -> " + end);
//        for (Booking booking: bestCombo) {
//            System.out.println(booking.getHumanReadableDates());
//        }
    }

    public double getCoveragePercent() {
        return coveragePercent;
    }
    
    public List<Booking> getBestCombo() {
        return bestCombo;
    }

    public OptimalBookingTimeLine getTimeLine() {
        return timeLine;
    }

    private void setCoveragePercent() {
        if (bestComboLength == -1) {
            coveragePercent = 0;
            return;
        }
        
        coveragePercent = (double)bestComboLength / (double)totalLength;
    }
    }
