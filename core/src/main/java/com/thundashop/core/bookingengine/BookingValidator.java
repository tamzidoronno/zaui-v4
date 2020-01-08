/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine;

import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingTimeLine;
import com.thundashop.core.bookingengine.data.BookingTimeLineFlatten;
import com.thundashop.core.common.BookingEngineException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author ktonder
 */
public class BookingValidator {
    private Map<String, BookingItem> items = new HashMap();
    private HashMap<String, Booking> savedBookings = new HashMap();
    private HashMap<String, List<Booking>> groupedBookings = new HashMap();
    private List<Booking> unsavedBookings = new ArrayList();
    private List<Booking> allBookings = new ArrayList();

    public BookingValidator(Map<String, BookingItem> items) {
        this.items = items;
    }

    public void add(Booking booking) {
        if (booking == null)
            return;
        
        if (booking.endedSevenDaysAgoOrMore()) {
            return;
        }
        
        if (booking.id == null || booking.id.isEmpty()) {
            unsavedBookings.add(booking);
        } else {
            savedBookings.put(booking.id, booking);
        }
        
        allBookings.add(booking);
        
        List<Booking> toUse = groupedBookings.get(booking.bookingItemTypeId);
        if (toUse == null) {
            toUse = new ArrayList();
            groupedBookings.put(booking.bookingItemTypeId, toUse);
        }
        
        if (booking.id != null && !booking.id.isEmpty()) {
            toUse.removeIf(o -> o.id.equals(booking.id));
        }
        
        toUse.add(booking);
    }
    
    public void validate() {
        allBookings.clear();
        allBookings.addAll(savedBookings.values());
        allBookings.addAll(unsavedBookings);
        checkForOverbookedByType();
        checkForOverbookedByItemId();
    }

    private void checkForOverbookedByType() {
        for (String typeId : groupedBookings.keySet()) {
            int totalSpotsForType = getTotalSpotsForCategory(typeId);
            BookingTimeLineFlatten ret = new BookingTimeLineFlatten(totalSpotsForType, typeId);
            groupedBookings.get(typeId).stream().forEach(booking -> ret.add(booking));
            List<BookingTimeLine> lines = ret.getTimelines();
            
            if (totalSpotsForType == 0) {
                String bookingId = lines.get(0).bookingIds.get(0);
                Booking b = allBookings
                        .stream()
                        .filter(o -> o.id.equals(bookingId))
                        .findAny()
                        .orElse(null);

                continue;
            }
            
            lines.forEach(line -> {
                if (line.count > totalSpotsForType) {
                    line.bookingIds.stream()
                            .forEach(id -> {
                                Booking b = savedBookings.get(id);
                            });
                    throw new BookingEngineException("Overbooking detected for category at " + line.start + " - " + line.end);
                }
            });
        }
    }

    private int getTotalSpotsForCategory(String typeId) {
        return items.values().stream()
                .filter(item -> item.bookingItemTypeId.equals(typeId))
                .mapToInt(item -> item.bookingSize)
                .sum();
    }

    private void checkForOverbookedByItemId() {
        items.values().stream()
                .forEach(item -> {
                    BookingTimeLineFlatten ret = new BookingTimeLineFlatten(item.bookingSize, item.bookingItemTypeId);
                    
                    allBookings.stream()
                            .filter(b -> b.bookingItemId != null && b.bookingItemId.equals(item.id))
                            .forEach(b -> ret.add(b));
                    
                    int maxCount = ret.getMaxCount();
                    if (maxCount < 0) {
                        throw new BookingEngineException("Overbooking detected by room.");
                    }
                });
    }

    
}
