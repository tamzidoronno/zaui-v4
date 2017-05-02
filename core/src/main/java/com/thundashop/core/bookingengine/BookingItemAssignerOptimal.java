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
        List<Booking> unassignedBookings = new ArrayList(getAllUnassignedBookings());
        List<Booking> assignedBookings = new ArrayList(getAllAssginedBookings());
        
        Collections.sort(unassignedBookings, Booking.sortByStartDate());
        Collections.sort(assignedBookings, Booking.sortByStartDate());
        
        List<OptimalBookingTimeLine> bookingLines = makeLinesOfAssignedBookings(assignedBookings);
        addUnassignedBookingsToLine(bookingLines, unassignedBookings);
        
        setItemIdsToLines(bookingLines);
        
        return bookingLines;
    }

    private void assignBookings(List<OptimalBookingTimeLine> bookingLines) {
        List<BookingItemTimeline> availableBookingItems = getAvailableBookingItems(bookingLines);
        assignLeftovers(bookingLines, availableBookingItems, true);
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

    private void assignLeftovers(List<OptimalBookingTimeLine> bookingLines, List<BookingItemTimeline> bookingItemsFlatten, boolean removeRoomsIfTheyCanBeUsedElsewhere) throws BookingEngineException {
        // Do the rest of the timelines
        
        for (OptimalBookingTimeLine bookingLine : bookingLines) {
            
            for (Booking booking : bookingLine.bookings) {
                BookingItemTimeline timeLineUsed = isThereAFreeItemForBookings(booking, bookingItemsFlatten, bookingLine);

                if (timeLineUsed == null && throwException) {
                    throw new BookingEngineException("Did not find an available bookingitem for booking: " + booking.getHumanReadableDates());
                }

                BookingItem item = getBookingItem(timeLineUsed.bookingItemId);
                if (item == null && throwException) {
                    throw new BookingEngineException("Did not find the booking item with id (it possible has been deleted): " + timeLineUsed.bookingItemId);
                }

                assignBookingsToItem(booking, item);
            }
        }
        
        if (removeRoomsIfTheyCanBeUsedElsewhere) {
            removeBookingsThatCanBeAssignedToDifferentRooms(bookingLines, bookingItemsFlatten);
        }
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
        dryRun = true;
        
        if (start == null) {
            start = new Date(Long.MIN_VALUE);   
        }
        
        if (end == null) {
            end = new Date(Long.MAX_VALUE);
        }
        
        Booking booking = bookings.stream().filter(book -> book.id.equals(bookingToConsider)).findFirst().orElse(null);
        
        if (booking == null) {
            booking = new Booking();
            booking.startDate = start;
            booking.endDate = end;
            booking.bookingItemTypeId = type.id;
            booking.id = "TMP_BOOKING_TO_TEST_AVAILABILITY";
            bookings.add(booking);
        } else {
            try {
                booking = (Booking)booking.clone();
            } catch (CloneNotSupportedException ex) {
                ex.printStackTrace();
                return new ArrayList();
            }

            bookings.removeIf(book -> book.id.equals(bookingToConsider));
            bookings.add(booking);    
        }
        
        List<String> retItems = new ArrayList();
        for (BookingItem item : items) {
            booking.bookingItemId = item.id;
            try {
                canAssign();
                retItems.add(item.id);
            } catch (BookingEngineException ex) {
                continue;
            }
        }
        
        return retItems;
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

    public List<String> getBookingsFromTimeLine(String bookingItemId) {
        List<OptimalBookingTimeLine> availableBookingItems = preCheck();
       
        for (OptimalBookingTimeLine timeLine : availableBookingItems) {
            if (timeLine.bookingItemId.equals(bookingItemId)) {
                return timeLine.bookings.stream().map(b -> b.id).collect(Collectors.toList());
            }
        }
        
        return new ArrayList();
    }

    private List<Booking> getAllUnassignedBookings() {
        return bookings.stream()
                .filter(book -> book.bookingItemId == null || book.bookingItemId.isEmpty())
                .collect(Collectors.toList());
    }

    private List<Booking>  getAllAssginedBookings() {
        return bookings.stream()
                .filter(book -> book.bookingItemId != null && !book.bookingItemId.isEmpty())
                .collect(Collectors.toList());
    }

    private List<OptimalBookingTimeLine> makeLinesOfAssignedBookings(List<Booking> assignedBookings) {
        List<OptimalBookingTimeLine> bookingLines = new ArrayList();
        
        while(assignedBookings.size() > 0) {
            OptimalBookingTimeLine bookingLine = new OptimalBookingTimeLine();
            Booking currentBooking = assignedBookings.remove(0);
            bookingLine.bookings.add(currentBooking);
            String currentBookingItemId = currentBooking.bookingItemId;
            
            for (Booking booking : assignedBookings) {
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
            
            assignedBookings.removeAll(bookingLine.bookings);
            bookingLines.add(bookingLine);
        }
        
        return bookingLines;
    }

    private void addUnassignedBookingsToLine(List<OptimalBookingTimeLine> bookingLines, List<Booking> unassignedBookings) {
        
        
        while(true) {
            long closestDistance = Long.MAX_VALUE;
            OptimalBookingTimeLine timeLineToUse = null;
            Booking bookingToUse = null;
            
            for (OptimalBookingTimeLine timeLine : bookingLines) {
                for (Booking booking : unassignedBookings) {
                    long closestDistanceInBooking = timeLine.getDistanceBetweenBookings(booking);
                    if (closestDistanceInBooking < closestDistance && !overlappingBooking(booking, timeLine.bookings)) {
                        closestDistance = closestDistanceInBooking;
                        timeLineToUse = timeLine;
                        bookingToUse = booking;
                    }
                }
            }
            
            if (bookingToUse == null || timeLineToUse == null || closestDistance == Long.MAX_VALUE)
                break;
            
            unassignedBookings.remove(bookingToUse);
            timeLineToUse.bookings.add(bookingToUse);
        }

        List<OptimalBookingTimeLine> rest = makeLinesOfAssignedBookings(unassignedBookings);
        bookingLines.addAll(rest);
    }

    private Booking getPrevBooking(Booking booking, List<Booking> unassignedBookings) {
        Booking prevBooking = null;
        long shortesTimeBetween = Long.MAX_VALUE;
        long startDate = booking.startDate.getTime();
        
        for (Booking ibook : unassignedBookings) {
            long timeBetween = startDate - ibook.endDate.getTime();
            if (timeBetween < 0)
                continue;
            
            if (timeBetween < shortesTimeBetween) {
                shortesTimeBetween = timeBetween;
                prevBooking = ibook;
            }
        }
        
        return prevBooking;
    }

    private void setItemIdsToLines(List<OptimalBookingTimeLine> bookingLines) {
        List<String> itemIdsUsed = new ArrayList();
        
        for (OptimalBookingTimeLine line : bookingLines) {
            for (Booking booking : line.bookings) {
                if (!booking.bookingItemId.isEmpty()) {
                    line.bookingItemId = booking.bookingItemId;
                    itemIdsUsed.add(line.bookingItemId);
                    break;
                }
            }
        }
        
        for (OptimalBookingTimeLine line : bookingLines) {
            if (line.bookingItemId.isEmpty()) {
                BookingItemTimeline itemToUse = getNextAvailableItem(line, bookingLines, itemIdsUsed);
                if (itemToUse == null) {
                    throw new BookingEngineException("Not enough space for this booking");
                }
                line.bookingItemId = itemToUse.bookingItemId;
            }
        }
        
        bookingLines.stream().forEach(o -> {
            Collections.sort(o.bookings, Booking.sortByStartDate());
            System.out.println(o.bookingItemId);
            o.bookings.stream().forEach(b -> {
                System.out.println(b.id + " | " + b.getHumanReadableDates());
            });
        });
    }
    
    public List<OptimalBookingTimeLine> getOptimalAssigned() {
        dryRun = true;
        return preCheck();
    }

    private BookingItemTimeline getNextAvailableItem(OptimalBookingTimeLine line, List<OptimalBookingTimeLine> bookingLines, List<String> itemIdsUsed) {
        List<BookingItemTimeline> flatten = getBookingItemsFlatten();
        
        for (String id : itemIdsUsed) {
            for (BookingItemTimeline flat : flatten) {
                if (flat.bookingItemId.equals(id)) {
                    flatten.remove(flat);
                    break;
                }
            }
        }
        
        if (flatten.size() > 0) {
            return flatten.get(0);
        }
        
        return null;
    }
}
