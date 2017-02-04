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
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerSubBase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author ktonder
 */
public class BookingItemAssignerOptimal {
    private final BookingItemType type;
    private List<Booking> bookings;
    private List<BookingItem> items;
    private boolean dryRun = false;
    private boolean throwException = true;
    private HashMap<Booking, BookingItem> assigned = new HashMap();

    public BookingItemAssignerOptimal(BookingItemType type, List<Booking> bookings, List<BookingItem> items, Boolean throwException) {
        this.type = type;
        this.bookings = bookings;
        this.items = items;
        
        removeBookingsThatHasNullDates();
        
        if (throwException != null) {
            this.throwException = throwException;
        }
    }

    /**
     * If this runs without throwing any exceptions
     * it is considered to be ok.
     */
    public void canAssign() {
        List<OptimalBookingTimeLine> bookingLines = preCheck();
        dryRun = true;
        assignBookings(bookingLines);
    }
    
    public void assign() {
        List<OptimalBookingTimeLine> bookingLines = preCheck();
        dryRun = false;
        assignBookings(bookingLines);
    }
    
    private List<OptimalBookingTimeLine> preCheck() {
        List<OptimalBookingTimeLine> bookingLines = makeOptimalTimeLines();
        long maximumNumberOfLines = items.stream().mapToInt(o -> o.bookingSize).sum();
        
        if (bookingLines.size() > maximumNumberOfLines) {
            printBookingLines(bookingLines);
            if (throwException) {
                throw new BookingEngineException("The setup of bookings can not be fitted into the booking, you have more bookings than you have items of this type");
            }
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
    private List<OptimalBookingTimeLine> makeOptimalTimeLines() {
        List<Booking> bookingsToAssign = new ArrayList(bookings);
        
        Collections.sort(bookingsToAssign, (Booking o1, Booking o2) -> {
            
            if (o1.isUnassigned() && !o2.isUnassigned()) {
                return 1;
            }

            if (!o1.isUnassigned() && o2.isUnassigned()) {
                return -1;
            }

            if (o1.startDate.equals(o2.startDate)) {
                return o2.endDate.compareTo(o1.endDate);
            }

            return o1.startDate.compareTo(o2.startDate);
        });
        
        List<OptimalBookingTimeLine> bookingLines = new ArrayList();
        while(bookingsToAssign.size() > 0) {
            OptimalBookingTimeLine bookingLine = new OptimalBookingTimeLine();
            Booking currentBooking = bookingsToAssign.remove(0);
            bookingLine.bookings.add(currentBooking);
            String currentBookingItemId = currentBooking.bookingItemId;
            
            for (Booking booking : bookingsToAssign) {
                if (currentBooking.endDate.getTime() <= booking.startDate.getTime()) {
                    if (!canBeOnTheSameLine(booking, currentBookingItemId)) {
                        continue;
                    }
                    
                    if (overlappingBooking(booking, bookingLine.bookings)) {
                        continue;
                    }
                    
                    if (booking.bookingItemId != null && !booking.bookingItemId.isEmpty()) {
                        currentBookingItemId = booking.bookingItemId;
                    }
                    
                    bookingLine.bookings.add(booking);
                }
                
                if (booking.bookingItemId == null || booking.bookingItemId.isEmpty()) {
                    if (!overlappingBooking(booking, bookingLine.bookings)) {
                        bookingLine.bookings.add(booking);
                    }
                }
            }
            
            bookingsToAssign.removeAll(bookingLine.bookings);
            bookingLines.add(bookingLine);
        }
        
        return bookingLines;
    }

    private void assignBookings(List<OptimalBookingTimeLine> bookingLines) {
        List<BookingItemTimeline> availableBookingItems = getAvailableBookingItems(bookingLines);
        assignLeftovers(bookingLines, availableBookingItems);
    }

    private List<BookingItemTimeline> getAvailableBookingItems(List<OptimalBookingTimeLine> bookingLines) throws BookingEngineException {
        List<BookingItemTimeline> bookingItemsFlatten = getBookingItemsFlatten();
        // First processes all that needs to be processed because they are
        // already assigned
        List<Booking> processed = assignAllLinesThatAlreadyHasElementsWithBookingItemId(bookingLines, bookingItemsFlatten);
        bookingLines.stream().forEach((ibookings) -> {
            ibookings.bookings.removeAll(processed);
        });
        
        bookingLines.removeAll(processed);
        return bookingItemsFlatten;
    }

    private void assignLeftovers(List<OptimalBookingTimeLine> bookingLines, List<BookingItemTimeline> bookingItemsFlatten) throws BookingEngineException {
        // Do the rest of the timelines
        
        for (OptimalBookingTimeLine bookingLine : bookingLines) {
            
            for (Booking booking : bookingLine.bookings) {
                BookingItemTimeline timeLineUsed = isThereAFreeItemForBookings(booking, bookingItemsFlatten, bookingLine);

                if (timeLineUsed == null) {
                    throw new BookingEngineException("Did not find an available bookingitem for booking: " + booking.getHumanReadableDates());
                }

                BookingItem item = getBookingItem(timeLineUsed.bookingItemId);
                if (item == null) {
                    throw new BookingEngineException("Did not find the booking item with id (it possible has been deleted): " + timeLineUsed.bookingItemId);
                }

                assignBookingsToItem(booking, item);
            }
        }
        
        removeBookingsThatCanBeAssignedToDifferentRooms(bookingLines, bookingItemsFlatten);
    }

    private List<Booking> assignAllLinesThatAlreadyHasElementsWithBookingItemId(List<OptimalBookingTimeLine> bookingLines, List<BookingItemTimeline> bookingItemsFlatten) throws BookingEngineException {
        List<Booking> processedBookings = new ArrayList();
        
        for (OptimalBookingTimeLine bookingLine : bookingLines) {
            
            for (Booking bookingWithItem : bookingLine.bookings) {
                if (bookingWithItem == null || bookingWithItem.bookingItemId == null || bookingWithItem.bookingItemId.isEmpty() ) {
                    continue;
                }

                BookingItemTimeline timeLine = getBookingItemTimeline(bookingWithItem, bookingItemsFlatten, bookingLine);
                
                if (timeLine == null) {
                    /*
                    1. Already in use
                    2. Does not exits
                    3. Not enouygh available rooms
                     */
                    BookingItem item = getBookingItem(bookingWithItem.bookingItemId);

                    if (throwException) {
                        printBookingsInterceptingWithItem(bookingWithItem);
                        throw new BookingEngineException("Could not complete the assignment, already in use. : " + bookingWithItem.getHumanReadableDates() + " item: " + item.bookingItemName);
                    }
                }

                BookingItem item = getBookingItem(bookingWithItem.bookingItemId);
                if (item == null) {
                    throw new BookingEngineException("Did not find the booking item for booking: " + bookingWithItem.getInformation());
                }

                assignBookingsToItem(bookingWithItem, item);
                
                processedBookings.add(bookingWithItem);
            }

        }
        
        return processedBookings;
    }

    private BookingItem getBookingItem(String bookingItemId) {
        BookingItem item = items.stream()
                .filter(o -> o.id.equals(bookingItemId))
                .findFirst()
                .orElse(null);
        
        return item;
    }

    /*
    * Checking weather the booking can be on the same booking line, 
    * basically if the item is the same or not set.
    */
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
    private List<BookingItemTimeline> getBookingItemsFlatten() {
        List<BookingItemTimeline> flattenList = new ArrayList();
        
        for (BookingItem item : items) {
            for (int i=0; i<item.bookingSize; i++) {
                flattenList.add(new BookingItemTimeline(item.id, item.bookingItemTypeId));
            }
        }
        
        return flattenList;
    }

    private void assignBookingsToItem(Booking booking, BookingItem item) {
        assigned.put(booking, item);
        if (dryRun) {
            return;
        }
        
        booking.bookingItemId = item.id;
        item.bookingIds.add(booking.id);
    }

    private BookingItemTimeline getBookingItemTimeline(Booking booking, List<BookingItemTimeline> bookingItemsFlatten, OptimalBookingTimeLine optimalTimeLine) {
        List<BookingItemTimeline> timeLines = bookingItemsFlatten.stream().filter(o -> o.bookingItemId.equals(booking.bookingItemId)).collect(Collectors.toList());

        if (timeLines.isEmpty() && throwException) {
            throw new BookingEngineException("Is there a booking assigned to an item that does not exists?");
        }
        
        for (BookingItemTimeline timeLine : timeLines) {
            if (timeLine.isAvailable(booking.startDate, booking.endDate)) {
                timeLine.add(booking.id, booking.startDate, booking.endDate);
                timeLine.setOptimalBookingTimeLineId(optimalTimeLine.uuid);
                return timeLine;
            }
        }
        
        return null;
    }

    /**
     * Will return a set
     *  of items ids that can be used for assigning items to.
     * @return 
     */
    public List<String> getAvailableItems(String bookingToConsider, Date start, Date end) {
        List<OptimalBookingTimeLine> bookingLines = preCheck();
        dryRun = true;
        List<BookingItemTimeline> availableBookingItems = getAvailableBookingItems(bookingLines);
        assignLeftovers(bookingLines, availableBookingItems);
        
        List<String> availableItems = availableBookingItems
                .stream()
                .filter(o -> ( start != null && end != null && o.isAvailable(start, end) || (o.notInUseAtAll()) ))
                .map(o -> o.bookingItemId)
                .collect(Collectors.toList());
        
        addItemIfConcideredBookingItemIsAssigned(bookingToConsider, availableBookingItems, availableItems);
        
        return availableItems;
    }

    private void addItemIfConcideredBookingItemIsAssigned(String bookingToConsider, List<BookingItemTimeline> availableBookingItems, List<String> availableItems) {
        if (bookingToConsider != null && !bookingToConsider.isEmpty()) {
            for (Booking booking : assigned.keySet()) {
                if (booking.id.equals(bookingToConsider)) {
                    BookingItem item = assigned.get(booking);
                    if (!availableBookingItems.contains(item.id)) {
                        availableItems.add(item.id);
                    }
                }
            }
        }
    }

    private void printBookingLines(List<OptimalBookingTimeLine> bookingLines) {
        int i = 1;
        for (OptimalBookingTimeLine bookings : bookingLines) {
            GetShopLogHandler.logPrintStatic("Line " + i, null);
            for (Booking booking : bookings.bookings) {
                GetShopLogHandler.logPrintStatic("Booking id: " + booking.id + ",created : " + booking.rowCreatedDate + " - Times: " + booking.getHumanReadableDates() + " type: " + booking.bookingItemTypeId + " Item id: " + booking.bookingItemId, null);
            }
            i++;
        }
    }

    private boolean overlappingBooking(Booking booking, List<Booking> bookingLine) {
        for (Booking ibooking : bookingLine) {
            if (ibooking.interCepts(booking.startDate, booking.endDate)) {
                return true;
            }
        }
        
        return false;
    }

    private void printBookingsInterceptingWithItem(Booking bookingWithItem) {
        
        for (Booking booking : bookings) {
            if (booking.bookingItemId != null && booking.bookingItemId.equals(bookingWithItem.bookingItemId) && booking.interCepts(bookingWithItem.startDate, bookingWithItem.endDate)) {
                GetShopLogHandler.logPrintStatic(booking.getInformation(), null);
            }
        }
    }

    private BookingItemTimeline isThereAFreeItemForBookings(Booking booking, List<BookingItemTimeline> bookingItemsFlatten, OptimalBookingTimeLine bookingLine) {
        for (BookingItemTimeline timeLine : bookingItemsFlatten) {
            if (timeLine.bookingItemTypeId.equals(booking.bookingItemTypeId) && timeLine.isAvailable(booking.startDate, booking.endDate) && timeLine.optimalTimeLineId != null && timeLine.optimalTimeLineId.equals(bookingLine.uuid)) {
                timeLine.add(booking.id, booking.startDate, booking.endDate);
                return timeLine;
            }
        }
        
        for (BookingItemTimeline timeLine : bookingItemsFlatten) {
            if (timeLine.bookingItemTypeId.equals(booking.bookingItemTypeId) && timeLine.isAvailable(booking.startDate, booking.endDate)) {
                timeLine.add(booking.id, booking.startDate, booking.endDate);
                return timeLine;
            }
        }
 
        return null;
    }

    private void removeBookingsThatHasNullDates() {
        bookings.removeIf(o -> o.startDate == null || o.endDate == null);
    }

    public List<Booking> getBookingThatUseItem(String bookingItemId) {
        List<Booking> bookingsToReturn = new ArrayList();
        
        for (Booking booking : assigned.keySet()) {
            BookingItem item = assigned.get(booking);
            if (item.id.equals(bookingItemId)) {
                bookingsToReturn.add(booking);
            }
        }
        
        return bookingsToReturn;
    }

    boolean isAssigned(Booking booking) {
        return assigned.get(booking) != null;
    }

    private void removeBookingsThatCanBeAssignedToDifferentRooms(List<OptimalBookingTimeLine> bookingLines, List<BookingItemTimeline> bookingItemsFlatten) {
    
        for (OptimalBookingTimeLine bookingLine : bookingLines) {   
            for (Booking booking : bookingLine.bookings) {
                BookingItemTimeline timeLineUsed = isThereAFreeItemForBookings(booking, bookingItemsFlatten, bookingLine);

                if (timeLineUsed != null && booking.bookingItemId.isEmpty()) {
                    assigned.remove(booking);
                    for (BookingItemTimeline timeLine : bookingItemsFlatten) {
                        timeLine.remove(booking.id);
                    }
                }
            }
        }
    }

}
