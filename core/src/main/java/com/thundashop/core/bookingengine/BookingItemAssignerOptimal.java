/*
 * This booking assinger is built for maximum optimal booking style. 
 * 
 * It will try to optimally assign all bookings.
 */
package com.thundashop.core.bookingengine;

import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.BookingEngineException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class BookingItemAssignerOptimal {
    private final BookingItemType type;
    private List<Booking> bookings;
    private List<BookingItem> items;
    private boolean dryRun = false;

    public BookingItemAssignerOptimal(BookingItemType type, List<Booking> bookings, List<BookingItem> items) {
        this.type = type;
        this.bookings = bookings;
        this.items = items;
    }

    /**
     * If this runs without throwing any exceptions
     * it is considered to be ok.
     */
    public void canAssign() {
        List<List<Booking>> bookingLines = preCheck();
        dryRun = true;
        assignBookings(bookingLines);
    }
    
    public void assign() {
        List<List<Booking>> bookingLines = preCheck();
        dryRun = false;
        assignBookings(bookingLines);
    }
    
    private List<List<Booking>> preCheck() {
        List<List<Booking>> bookingLines = makeOptimalTimeLines();
        long maximumNumberOfLines = items.stream().mapToInt(o -> o.bookingSize).sum();
        
        if (bookingLines.size() > maximumNumberOfLines) {
            printBookingLines(bookingLines);
            throw new BookingEngineException("The setup of bookings can not be fitted into the booking, you have more bookings than you have items of this type");
        }
        
        return bookingLines;
    }
    
    /**
     * This code takes all the bookings and sort them the best possible way so they
     * dont overlap eachother and are still grouped together. 
     * 
     * Example:
     *  |----|
     *     |-----|
     *       |-----|
     * 
     * Returns: 
     *  |----||----|
     *     |----|
     * 
     * @return 
     */
    private List<List<Booking>> makeOptimalTimeLines() {
        List<Booking> bookingsToAssign = new ArrayList(bookings);
        Collections.sort(bookingsToAssign);
        
        List<List<Booking>> bookingLines = new ArrayList();
        while(bookingsToAssign.size() > 0) {
            List<Booking> bookingLine = new ArrayList();
            Booking currentBooking = bookingsToAssign.get(0);
            bookingLine.add(currentBooking);
            String currentBookingItemId = currentBooking.bookingItemId;
            
            for (Booking booking : bookingsToAssign) {
                if (booking.equals(currentBooking)) {
                    continue;
                }
                
                if (currentBooking.endDate.getTime() <= booking.startDate.getTime()) {
                    if (!canBeOnTheSameLine(booking, currentBookingItemId)) {
                        continue;
                    }
                    
                    if (booking.bookingItemId != null && !booking.bookingItemId.isEmpty()) {
                        currentBookingItemId = booking.bookingItemId;
                    }
                    
                    bookingLine.add(booking);
                }
            }
            
            bookingsToAssign.removeAll(bookingLine);
            bookingLines.add(bookingLine);
        }
        
        return bookingLines;
    }

    private void assignBookings(List<List<Booking>> bookingLines) {
        List<String> availableBookingItems = getAvailableBookingItems(bookingLines);
        assignLeftovers(bookingLines, availableBookingItems);
    }

    private List<String> getAvailableBookingItems(List<List<Booking>> bookingLines) throws BookingEngineException {
        List<String> bookingItemsFlatten = getBookingItemsFlatten();
        // First processes all that needs to be processed because they are
        // already assigned
        List<List<Booking>> processed = assignAllLinesThatAlreadyHasElementsWithBookingItemId(bookingLines, bookingItemsFlatten);
        bookingLines.removeAll(processed);
        return bookingItemsFlatten;
    }

    private void assignLeftovers(List<List<Booking>> bookingLines, List<String> bookingItemsFlatten) throws BookingEngineException {
        // Do the rest of the timelines
        for (List<Booking> bookingLine : bookingLines) {
            if (bookingItemsFlatten.isEmpty()) {
                throw new BookingEngineException("Not enough bookingitems to make all timelines, no more space left.");
            }
            
            String bookingItem = bookingItemsFlatten.remove(0);
            
            BookingItem item = getBookingItem(bookingItem);
            if (item == null) {
                throw new BookingEngineException("Did not find the booking item with id (it possible has been deleted): " + bookingItem);
            }
            
            assignBookingsToItem(bookingLine, item);
        }
    }

    private List<List<Booking>> assignAllLinesThatAlreadyHasElementsWithBookingItemId(List<List<Booking>> bookingLines, List<String> bookingItemsFlatten) throws BookingEngineException {
        List<List<Booking>> bookingLinesProcessed = new ArrayList();
        
        for (List<Booking> bookingLine : bookingLines) {
            Booking bookingWithItem = bookingLine.stream()
                    .filter(o -> o.bookingItemId != null && !o.bookingItemId.isEmpty())
                    .findFirst().orElse(null);
            
            if (bookingWithItem == null) {
                continue;
            }
            
            if (!removeIfExists(bookingWithItem.bookingItemId, bookingItemsFlatten)) {
                /*
                1. Already in use
                2. Does not exits
                3. Not enouygh available rooms
                */
                throw new BookingEngineException("Could not complete the assignment, already in use. : " + bookingWithItem.getHumanReadableDates() + " item: " + bookingWithItem.bookingItemId);
            }
            
            BookingItem item = getBookingItem(bookingWithItem.bookingItemId);
            if (item == null) {
                throw new BookingEngineException("Did not find the booking item for booking: " + bookingWithItem.getInformation());
            }
            
            assignBookingsToItem(bookingLine, item);
            bookingLinesProcessed.add(bookingLine);
        }
        
        return bookingLinesProcessed;
    }

    private BookingItem getBookingItem(String bookingItemId) {
        BookingItem item = items.stream()
                .filter(o -> o.id.equals(bookingItemId))
                .findFirst()
                .orElse(null);
        
        return item;
    }

    private boolean canBeOnTheSameLine(Booking booking, String currentBookingItemId) {
        if (currentBookingItemId == null || currentBookingItemId.isEmpty()) {
            return true;
        }
        
        if (booking.bookingItemId == null || booking.bookingItemId.isEmpty()) {
            return true;
        }
        
        if (booking.bookingItemId.equals(currentBookingItemId)) {
            return true;
        }
        
        return false;
    }

    /**
     * Returns a list of booking items based on count.
     * 
     * @return 
     */
    private List<String> getBookingItemsFlatten() {
        List<String> flattenList = new ArrayList();
        
        for (BookingItem item : items) {
            for (int i=0; i<item.bookingSize; i++) {
                flattenList.add(item.id);
            }
        }
        
        return flattenList;
    }

    private void assignBookingsToItem(List<Booking> bookings, BookingItem item) {
        if (dryRun) {
            return;
        }
        
        for (Booking booking : bookings) {
            booking.bookingItemId = item.id;
            item.bookingIds.add(booking.id);
        }
    }

    private boolean removeIfExists(String bookingItemId, List<String> bookingItemsFlatten) {
        int i = 0;
        int found = -1;
        for (String ibookingItemId : bookingItemsFlatten) {
            if (ibookingItemId.equals(bookingItemId)) {
                found = i;
            }
            i++;
        }
        
        if (found > -1) {
            bookingItemsFlatten.remove(found);
            return true;
        }
        
        return false;
    }

    /**
     * Will return a set
     *  of items ids that can be used for assigning items to.
     * @return 
     */
    public List<String> getAvailableItems() {
        List<List<Booking>> bookingLines = preCheck();
        dryRun = true;
        List<String> availableBookingItems = getAvailableBookingItems(bookingLines);
        return availableBookingItems;
    }

    private void printBookingLines(List<List<Booking>> bookingLines) {
        int i = 1;
        for (List<Booking> bookings : bookingLines) {
            System.out.println("Line " + i);
            for (Booking booking : bookings) {
                System.out.println("Booking id: " + booking.id + " - Times: " + booking.getHumanReadableDates() + " type: " + booking.bookingItemTypeId);
            }
            i++;
        }
    }

}
