/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine.data;

import com.thundashop.core.common.BookingEngineException;
import com.thundashop.core.common.GetShopLogHandler;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Add bookings to this class and it will flatten the bookings on a timeline
 * with a count for each timeline.
 *
 * @author ktonder
 */
public class BookingTimeLineFlatten implements Serializable {
    private List<Booking> bookings = new ArrayList();
    private HashMap<String, Booking> bookingMap = new HashMap();
    private final int totalAvailableSpots;
    private final String bookingItemTypeId;
    public String bookingItemId = "";
    public Date end;
    public Date start;
    public boolean overFlow = false;

    public BookingTimeLineFlatten(int totalAvailableSpots, String bookingItemTypeId) {
        this.totalAvailableSpots = totalAvailableSpots;
        this.bookingItemTypeId = bookingItemTypeId;
    }

    public void add(Booking booking) {
        if (booking.bookingItemTypeId.equals(bookingItemTypeId) || bookingItemTypeId.equals("all")) {
            bookings.add(booking);
            bookingMap.put(booking.id, booking);
        }
    }

    public List<Booking> getBookings() {
        return bookings;
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
    
    public List<BookingTimeLine> getTimelinesIncludedNulls() {
        sortBookings();
        List<Date> markers = getMarkers();

        List<BookingTimeLine> timeLines = new ArrayList();
        Date prev = null;
        for (Date marker : markers) {
            if (prev != null) {
                BookingTimeLine timeLine = createTimeLine(prev, marker);
                timeLines.add(timeLine);
            }
            prev = marker;
        }

        return timeLines;
    }

    public boolean canAdd(Booking booking) {
        for (BookingTimeLine itemLine : getTimelines()) {
            if (itemLine.intercepts(booking) && itemLine.getAvailableSpots() < 1) {
                GetShopLogHandler.logPrintStatic("Booking is full between: " + itemLine.start + " and " + itemLine.end, null);
                return false;
            }
        }

        return true;
    }

    private void sortBookings() {
        Collections.sort(bookings, new Comparator<Booking>(){
            @Override
            public int compare(Booking o1, Booking o2) {
                if(o1 == null || o2 == null || o1.startDate == null || o2.startDate == null) {
                    GetShopLogHandler.logPrintStatic("null date", null);
                    return 1;
                }
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
    public List<BookingTimeLine> getTimelines(Integer interval, Integer append) {
        LinkedList<BookingTimeLine> result = new LinkedList();
        List<BookingTimeLine> allLines = getTimelines();
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        while(true) {
            Date startTime = cal.getTime();
            cal.add(Calendar.SECOND, interval);
            Date endTime = cal.getTime();
            cal.add(Calendar.SECOND, append);

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

        Collections.sort(result, new Comparator<BookingTimeLine>(){
            public int compare(BookingTimeLine o1, BookingTimeLine o2){
                return o1.start.compareTo(o2.start);
            }
        });

        return result;
    }

    public boolean containsBooking(Booking booking) {
        return bookings.contains(booking);
    }

    /**
     * Will return the best possible combo of booking lines. Also forces that
     * the timelines with bookings that is assigned gets their place.
     *
     * @return
     */
    public List<Booking> getBestCombo() {
        List<BookingTimeLine> timeLines = getTimelines();

        // Remove items if there is any fixed places that needs to be fixed position
        List<Booking> bookingIdsToCheck = bookings.stream()
                .filter(b -> b.bookingItemId != null && !b.bookingItemId.isEmpty())
                .collect(Collectors.toList());
       
        List<String> bookingIdsRemoved = new ArrayList();
        
        bookingIdsToCheck.stream().forEach(booking -> {
            timeLines.stream().forEach(t -> {
                if (t.bookingIds.contains(booking.id)) {
                    bookingIdsRemoved.addAll(t.bookingIds);
                    t.bookingIds.clear();
                    t.bookingIds.add(booking.id);
                    bookingIdsRemoved.remove(booking.id);
                }
            });
        });
        
        bookingIdsRemoved.stream().forEach(bookingId -> {
            timeLines.stream().forEach(t -> {
                t.bookingIds.remove(bookingId);
            });
        });
        
        // End cleaning up static content.
        
        List<String> bookingIds = bruteForceCombination(timeLines);

        return bookingIds
                .stream()
                .map(id -> bookingMap.get(id))
                .collect(Collectors.toList());
    }

    private List<String> bruteForceCombination(List<BookingTimeLine> lines) {

        if (lines == null || lines.isEmpty()) {
            return new ArrayList();
        }
        
        List<String> bookingIdsToIgnore = new ArrayList();
        
        List<String> combos = new ArrayList();
        String concattedLine = makeOneLine(lines.get(0), "", "", 0, lines, combos, bookingIdsToIgnore);    
        
        List<String> args = Arrays.asList(concattedLine.split(" | "));
        args = args.stream()
                .filter(s -> !s.equals("|"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        
        return args;
    }
    

    void GeneratePermutations(List<List<String>> Lists, List<String> result, int depth, String current) {
        if (depth == Lists.size()) {
            result.add(current);
            return;
        }

        for (int i = 0; i < Lists.get(depth).size(); ++i) {
            GeneratePermutations(Lists, result, depth + 1, current + Lists.get(depth).get(i));
        }
    }

    private String makeOneLine(BookingTimeLine currentLine, String currentBookingId, String concattedLine, int depth, List<BookingTimeLine> allLines, List<String> combos, List<String> dropBookingIds) {
        
        if (currentLine == null) {
            return concattedLine;
        }
        
        depth++;

        BookingTimeLine nextLine = null;
        
        if (allLines.size() > depth) {
            nextLine = allLines.get(depth);
        }
        
        
        if (currentLine.bookingIds.contains(currentBookingId)) {
            concattedLine += " | " + makeOneLine(nextLine, currentBookingId, concattedLine, depth, allLines, combos, dropBookingIds);
            return concattedLine;
        }
        
        String nextBookingId = "";
        
        for (String bookingId : currentLine.bookingIds) {
            if (dropBookingIds.contains(bookingId)) {
                continue;
            }
            
            boolean prevMatched = false;
            
            for (int i=1; i<depth; i++) {
                if (allLines.size() > i) {
                    if (allLines.get((i-1)).bookingIds.stream().anyMatch(s -> s.equals(bookingId))) {
                        prevMatched = true;
                    }
                }
            }
            
            if (prevMatched) {
                continue;
            }
            
            nextBookingId = bookingId;
            break;
        }
        
        if (!nextBookingId.isEmpty()) {
            String nextId = makeOneLine(nextLine, nextBookingId, concattedLine, depth, allLines, combos, dropBookingIds);

            concattedLine += nextBookingId + " | " + nextId;
            return concattedLine;
        }
        
        if (allLines.size() <= depth)
            return concattedLine;
        
        nextLine = allLines.get(depth);
        
        return makeOneLine(nextLine, nextBookingId, concattedLine, depth, allLines, combos, dropBookingIds);
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    /**
     * Will return all the possible combinations of bookings between start and end date.
     * @return 
     */
    public List<String> getAllPossibleCombos() {
 
        List<BookingTimeLine> timeLines = getTimelines();
        
        makeDistinct(timeLines);
       
        if (timeLines.isEmpty())
            return new ArrayList();
        
        List<String> result = new ArrayList();
        
        for (String bookingId : timeLines.get(0).bookingIds) {
            getNextCombination(0, bookingId, timeLines, bookingId, result);
        }
        
        return result;
    }
    

    private boolean getNextCombination(int counter, String currentBookingId, List<BookingTimeLine> timeLines, String prevResult, List<String> result) {
        
        BookingTimeLine nextLine = timeLines.get(counter);
        
        int nextCounter = counter + 1;
        if (timeLines.size() <= nextCounter) {
            result.add(prevResult);
            if (result.size() > 100000) {
                result.clear();
                throw new BookingEngineException("Overflow");
            }
            return false;
        }
        
        if (nextLine.bookingIds.contains(currentBookingId)) {
            getNextCombination(nextCounter, currentBookingId, timeLines, ""+prevResult, result);
            return true;
        }
        
        boolean found = false;
        int i = 0;
        for (String bookingId : nextLine.bookingIds) {
            String nextResult = prevResult;
            
            if (i > 4) {
                break;
            }
            
            if (isInPrevTimeLine(bookingId, timeLines, counter))
                continue;
            
            if (nextResult.isEmpty()) {
                nextResult += bookingId;
            } else {
                nextResult += "," + bookingId;
            }
            
            getNextCombination(nextCounter, bookingId, timeLines, ""+nextResult, result);
            
            found = true;
            
            i++;
        }
        
        if (!found)
            return getNextCombination(nextCounter, currentBookingId, timeLines, prevResult, result);
        else
            return false;
    }

    private boolean isInPrevTimeLine(String bookingId, List<BookingTimeLine> timeLines, int count) {
        if (count < 1)
            return false;
        
        BookingTimeLine line = timeLines.get((count-1));
        return line.bookingIds.contains(bookingId);
        
    }

    private void makeDistinct(List<BookingTimeLine> timeLines) {
        for (BookingTimeLine line : timeLines) {
            String bookingIdOnlyInGroup = null;
            
            List<String> canBeRemoved = new ArrayList();
            
            for (String id : line.bookingIds) {
                if (isOnlyInTimeLine(line, timeLines, id)) {
                    if (bookingIdOnlyInGroup == null) {
                        bookingIdOnlyInGroup = id;
                    } else {
                        canBeRemoved.add(id);
                    }
                }
            }
            
            line.bookingIds.removeAll(canBeRemoved);
        }
    }

    private boolean isOnlyInTimeLine(BookingTimeLine line, List<BookingTimeLine> timeLines, String id) {
        for (BookingTimeLine iLine : timeLines) {
            if (iLine.equals(line)) {
                continue;
            }
            
            if (iLine.bookingIds.contains(id)) {
                return false;
            }
        }
        
        return true;
    }

    public int getMaxCount() {
        int max = Integer.MAX_VALUE;
        
        List<BookingTimeLine> timeLines = getTimelines();

        for (BookingTimeLine timeline : timeLines) {
            int freeSpots = timeline.getAvailableSpots();
            if (freeSpots < max) {
                max = freeSpots;
            }
        }
        
        long distinctItemsAssigned = bookings.stream()
                .filter(b -> b.isAssigned())
                .map(b -> b.bookingItemId)
                .distinct()
                .count();
        
        long maxDueToAssigned = totalAvailableSpots - distinctItemsAssigned;
        
        if (maxDueToAssigned < max) {
            return (int)maxDueToAssigned;
        }
        
      
        return max;
    }

    public String getBookingItemTypeId() {
        return bookingItemTypeId;
    }
}
