/*
 * This booking assinger is built for maximum optimal booking style. 
 * 
 * It will try to optimally assign all bookings.
 */
package com.thundashop.core.bookingengine;

import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.BookingTimeLineFlatten;
import com.thundashop.core.common.BookingEngineException;
import com.thundashop.core.common.GetShopLogHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final String storeId;
    private List<OptimalBookingTimeLine> failedRows = new ArrayList();
    private List<OptimalBookingTimeLine> linesToBruteforce;
    private ArrayList bookingsToBruteForce;

    public BookingItemAssignerOptimal(BookingItemType type, List<Booking> bookings, List<BookingItem> items, Boolean throwException, String storeId) {
        this.type = type;
        this.bookings = bookings;
        this.items = items;
        this.storeId = storeId;
        
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
        
        if (bookingLines.size() > getBookingItemsFlatten().size()) {
            tryToMerge(bookingLines, unassignedBookings);
        }
        
        setItemIdsToLines(bookingLines);
        
        return bookingLines;
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
            if (timeLine.isAvailable(booking.getStartDateTranslated(), booking.getEndDateTranslated())) {
                timeLine.add(booking.id, booking.getStartDateTranslated(), booking.getEndDateTranslated());
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
            booking.setStartDate(start);
            booking.setEndDate(end);
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
            booking.setStartDate(start);
            booking.setEndDate(end);
            
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
    
    public void printBookingLines(List<OptimalBookingTimeLine> bookingLines) {
        int i = 1;
        for (OptimalBookingTimeLine bookings : bookingLines) {
            GetShopLogHandler.logPrintStatic("Line " + i, null);
            Collections.sort(bookings.bookings, Booking.sortByStartDate());
            for (Booking booking : bookings.bookings) {
                GetShopLogHandler.logPrintStatic("Booking id: " + booking.id + ",created : " + booking.rowCreatedDate + " - Times: " + booking.getHumanReadableDates() + " type: " + booking.bookingItemTypeId + " Item id: " + booking.bookingItemId, null);
            }
            i++;
        }
    }

    private boolean overlappingBooking(Booking booking, List<Booking> bookingLine) {
        for (Booking ibooking : bookingLine) {
            if (ibooking.interCepts(booking.getStartDateTranslated(), booking.getEndDateTranslated())) {
                return true;
            }
        }
        
        return false;
    }

    private void printBookingsInterceptingWithItem(Booking bookingWithItem) {
        
        for (Booking booking : bookings) {
            if (booking.bookingItemId != null && booking.bookingItemId.equals(bookingWithItem.bookingItemId) && booking.interCepts(bookingWithItem.getStartDateTranslated(), bookingWithItem.getEndDateTranslated())) {
                GetShopLogHandler.logPrintStatic(booking.getInformation(), null);
            }
        }
    }

    private BookingItemTimeline isThereAFreeItemForBookings(Booking booking, List<BookingItemTimeline> bookingItemsFlatten, OptimalBookingTimeLine bookingLine) {
        for (BookingItemTimeline timeLine : bookingItemsFlatten) {
            if (timeLine.bookingItemTypeId.equals(booking.bookingItemTypeId) && timeLine.isAvailable(booking.getStartDateTranslated(), booking.getEndDateTranslated()) && timeLine.optimalTimeLineId != null && timeLine.optimalTimeLineId.equals(bookingLine.uuid)) {
                timeLine.add(booking.id, booking.getStartDateTranslated(), booking.getEndDateTranslated());
                return timeLine;
            }
        }
        
        for (BookingItemTimeline timeLine : bookingItemsFlatten) {
            if (timeLine.bookingItemTypeId.equals(booking.bookingItemTypeId) && timeLine.isAvailable(booking.getStartDateTranslated(), booking.getEndDateTranslated())) {
                timeLine.add(booking.id, booking.getStartDateTranslated(), booking.getEndDateTranslated());
                return timeLine;
            }
        }
 
        return null;
    }

    private void removeBookingsThatHasNullDates() {
        bookings.removeIf(o -> o.getStartDateTranslated() == null || o.getEndDateTranslated() == null);
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
                if (currentBooking.getEndDateTranslated().getTime() <= booking.getStartDateTranslated().getTime()) {
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
        while(!unassignedBookings.isEmpty()) {
            long closestDistance = Long.MAX_VALUE;
            OptimalBookingTimeLine timeLineToUse = null;
            Booking bookingToUse = null;
            boolean found = false;
            
            for (OptimalBookingTimeLine timeLine : bookingLines) {
                for (Booking booking : unassignedBookings) {
                    long closestDistanceInBooking = timeLine.getDistanceBetweenBookings(booking);
                    if (closestDistanceInBooking < closestDistance && !overlappingBooking(booking, timeLine.bookings)) {
                        closestDistance = closestDistanceInBooking;
                        timeLineToUse = timeLine;
                        bookingToUse = booking;
                        found = true;
                    }
                }
            }
            
            if (timeLineToUse != null) {
                unassignedBookings.remove(bookingToUse);
                timeLineToUse.bookings.add(bookingToUse);
            }
            
            if (!unassignedBookings.isEmpty() && !found) {
                List<Booking> newBookings = unassignedBookings.subList(0, 1);
                List<OptimalBookingTimeLine> newLine = makeLinesOfAssignedBookings(newBookings);
                unassignedBookings.removeAll(newBookings);
                bookingLines.addAll(newLine);
            }
        }
    }
    
    private void addUnassignedBookingsToLine2(List<OptimalBookingTimeLine> bookingLines, List<Booking> unassignedBookings) {
        
        for (OptimalBookingTimeLine timeLine : bookingLines) {
            BookingTimeLineFlatten flattenTimeLine = new BookingTimeLineFlatten(Integer.MAX_VALUE, "all");
            timeLine.bookings.stream().forEach(o -> flattenTimeLine.add(o));
            unassignedBookings.stream().forEach(o -> flattenTimeLine.add(o));
            List<Booking> bestCombo = flattenTimeLine.getBestCombo();
            
            for (Booking ibooking : bestCombo) {
                if (!timeLine.bookings.contains(ibooking)) {
                    timeLine.bookings.add(ibooking);
                }
            }
            
            unassignedBookings.removeAll(bestCombo);
        }
        
        while(!unassignedBookings.isEmpty()) {
            BookingTimeLineFlatten flattenTimeLine = new BookingTimeLineFlatten(Integer.MAX_VALUE, "all");
            unassignedBookings.stream().forEach(o -> {
                if (o.id == null || o.id.isEmpty()) {
                    o.id = "blank_"+UUID.randomUUID().toString();
                }
                flattenTimeLine.add(o);
            });
            
            List<Booking> bookingsToUse = flattenTimeLine.getBestCombo();
            
            bookingsToUse.stream().forEach(o -> {
                if (o.id != null && o.id.startsWith("blank_")) {
                    o.id = "";
                }
            });
            
            OptimalBookingTimeLine bookingLine = new OptimalBookingTimeLine();
            bookingLine.bookings.addAll(bookingsToUse);
            unassignedBookings.removeAll(bookingsToUse);
            bookingLines.add(bookingLine);
        }
    }
    
    private Booking getPrevBooking(Booking booking, List<Booking> unassignedBookings) {
        Booking prevBooking = null;
        long shortesTimeBetween = Long.MAX_VALUE;
        long startDate = booking.getStartDateTranslated().getTime();
        
        for (Booking ibook : unassignedBookings) {
            long timeBetween = startDate - ibook.getEndDateTranslated().getTime();
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
            String idToUse = "";
            for (Booking booking : line.bookings) {
                if (booking.bookingItemId != null && !booking.bookingItemId.isEmpty()) {
                    idToUse = booking.bookingItemId;
                }
            }
            
            if (!idToUse.isEmpty()) {
                line.bookingItemId = idToUse;
                itemIdsUsed.add(idToUse);
            }
        }
        
        for (OptimalBookingTimeLine line : bookingLines) {
            if (line.bookingItemId.isEmpty()) {
                BookingItemTimeline itemToUse = getNextAvailableItem(line, bookingLines, itemIdsUsed);
                
                if (itemToUse == null && !throwException) {
                    failedRows.add(line);
                }
                
                if (itemToUse == null && throwException) {
                    throw new BookingEngineException("Not enough space for this booking");
                } else {
                    if (itemToUse != null) {
                        line.bookingItemId = itemToUse.bookingItemId;
                    } else {
                        List<BookingItemTimeline> flatten = getBookingItemsFlatten();
                    }
                }
            }
        }
        
        Map<String, List<OptimalBookingTimeLine>> groupedBookings = bookingLines.stream()
                .collect(Collectors.groupingBy(o -> o.bookingItemId, Collectors.toList()));
        
        if (throwException) {
            for (String itemId : groupedBookings.keySet()) {
                BookingItem item2 = getBookingItem(itemId);
                if (item2 == null || item2.bookingSize < groupedBookings.get(itemId).size()) {
                    throw new BookingEngineException("Not enough space for this booking");
                }
            }
        }
    }
    
    public List<OptimalBookingTimeLine> getOptimalAssigned() {
        dryRun = true;
        return preCheck();
    }

    private BookingItemTimeline getNextAvailableItem(OptimalBookingTimeLine line, List<OptimalBookingTimeLine> bookingLines, List<String> itemIdsUsed) {
        List<BookingItemTimeline> flatten = getBookingItemsFlatten();
        
        for (String id : itemIdsUsed) {
            BookingItemTimeline toRemove = null;
            for (BookingItemTimeline flat : flatten) {
                if (flat.bookingItemId.equals(id)) {
                    toRemove = flat;
                }
            }
            
            if (toRemove != null) {
                flatten.remove(toRemove);
            }
        }
        
        if (flatten.size() > 0) {
            BookingItemTimeline toUse = flatten.get(0);
            itemIdsUsed.add(toUse.bookingItemId);
            return toUse;
        }
        
        return null;
    }

    private void tryToMerge(List<OptimalBookingTimeLine> bookingLines, List<Booking> unassignedBookings) {
        OptimalBookingTimeLine line = getLineWithFewestBooking(bookingLines);
        
        if (linesToBruteforce == null) {
            bookingsToBruteForce = new ArrayList(line.bookings);
            linesToBruteforce = bookingLines.stream()
                    .filter(l -> !l.equals(line))
                    .collect(Collectors.toList());
        }
        
        if (bookingsToBruteForce == null) {
            return;
        }
        
        System.out.println(bookingsToBruteForce);
    }

    void disableErrorCheck() {
        throwException = false;
    }

    public List<OptimalBookingTimeLine> getOverbookedLines() {
        return failedRows;
    }

    private OptimalBookingTimeLine getLineWithFewestBooking(List<OptimalBookingTimeLine> bookingLines) {
        List<OptimalBookingTimeLine> optLines = new ArrayList(bookingLines);
        
        Collections.sort(optLines, (OptimalBookingTimeLine l1, OptimalBookingTimeLine l2) -> {
            Integer i1 = new Integer(l1.bookings.size());
            Integer i2 = new Integer(l2.bookings.size());
            return i1.compareTo(i2);
        });
        
        return optLines.get(0);
    }

}
