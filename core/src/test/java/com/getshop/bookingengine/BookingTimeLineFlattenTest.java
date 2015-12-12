/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.bookingengine;

import com.thundashop.core.bookingengine.BookingTimeLineFlatten;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingTimeLine;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author ktonder
 */
public class BookingTimeLineFlattenTest {
    
    /**
     * Splitting
     * |----------------|
     *                  |--------------|
     *                                          |---------------|
     */
    @Test
    public void allInRow() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 08:00"));
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-06 08:00"));
        flattenTimeline.add(getBooking("2015-01-08 08:00", "2015-01-10 08:00"));
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(3, timeLines.size());
    }
    
    
    /**
     * Splitting:
     * 
     * |--------------------|
     *              |---------------------|
     */
    
    @Test
    public void twoLinesOverlapping() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 12:00"));
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-06 08:00"));
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(3, timeLines.size());
        
        BookingTimeLine firstTimeLine = timeLines.get(0);
        Assert.assertEquals(1, firstTimeLine.count);
        Assert.assertEquals(getDate("2015-01-04 08:00"), firstTimeLine.start);
        Assert.assertEquals(getDate("2015-01-05 08:00"), firstTimeLine.end);
        
        
        BookingTimeLine secondTimeLine = timeLines.get(1);
        Assert.assertEquals(2, secondTimeLine.count);
        Assert.assertEquals(getDate("2015-01-05 08:00"), secondTimeLine.start);
        Assert.assertEquals(getDate("2015-01-05 12:00"), secondTimeLine.end);
        
        BookingTimeLine thirdTimeLine = timeLines.get(2);
        Assert.assertEquals(1, thirdTimeLine.count);
        Assert.assertEquals(getDate("2015-01-05 12:00"), thirdTimeLine.start);
        Assert.assertEquals(getDate("2015-01-06 08:00"), thirdTimeLine.end);
    }
    
    /**
     * Splitting:
     *      
     *              |---------------------|
     * |--------------------|
     */
    
    @Test
    public void twoLinesOverlapping_2() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-06 08:00"));
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 12:00"));
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(3, timeLines.size());
        
        BookingTimeLine firstTimeLine = timeLines.get(0);
        Assert.assertEquals(1, firstTimeLine.count);
        Assert.assertEquals(getDate("2015-01-04 08:00"), firstTimeLine.start);
        Assert.assertEquals(getDate("2015-01-05 08:00"), firstTimeLine.end);
        
        
        BookingTimeLine secondTimeLine = timeLines.get(1);
        Assert.assertEquals(2, secondTimeLine.count);
        Assert.assertEquals(getDate("2015-01-05 08:00"), secondTimeLine.start);
        Assert.assertEquals(getDate("2015-01-05 12:00"), secondTimeLine.end);
        
        BookingTimeLine thirdTimeLine = timeLines.get(2);
        Assert.assertEquals(1, thirdTimeLine.count);
        Assert.assertEquals(getDate("2015-01-05 12:00"), thirdTimeLine.start);
        Assert.assertEquals(getDate("2015-01-06 08:00"), thirdTimeLine.end);
    }
    
    
    /**
     * Splitting:
     *      
     *              |---------------------|
     * |--------------------|
     *                              |---------------------|
     */
    
    @Test
    public void twoLinesOverlapping_3() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-06 08:00"));
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 12:00"));
        flattenTimeline.add(getBooking("2015-01-06 06:00", "2015-01-08 12:00"));
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(5, timeLines.size());
        
        BookingTimeLine firstTimeLine = timeLines.get(0);
        Assert.assertEquals(1, firstTimeLine.count);
        Assert.assertEquals(getDate("2015-01-04 08:00"), firstTimeLine.start);
        Assert.assertEquals(getDate("2015-01-05 08:00"), firstTimeLine.end);
        
        
        BookingTimeLine secondTimeLine = timeLines.get(1);
        Assert.assertEquals(2, secondTimeLine.count);
        Assert.assertEquals(getDate("2015-01-05 08:00"), secondTimeLine.start);
        Assert.assertEquals(getDate("2015-01-05 12:00"), secondTimeLine.end);
        
        BookingTimeLine thirdTimeLine = timeLines.get(2);
        Assert.assertEquals(1, thirdTimeLine.count);
        Assert.assertEquals(getDate("2015-01-05 12:00"), thirdTimeLine.start);
        Assert.assertEquals(getDate("2015-01-06 06:00"), thirdTimeLine.end);
        
        BookingTimeLine fourTimeLine = timeLines.get(3);
        Assert.assertEquals(2, fourTimeLine.count);
        Assert.assertEquals(getDate("2015-01-06 06:00"), fourTimeLine.start);
        Assert.assertEquals(getDate("2015-01-06 08:00"), fourTimeLine.end);
        
        BookingTimeLine fifthTimeLine = timeLines.get(4);
        Assert.assertEquals(1, fifthTimeLine.count);
        Assert.assertEquals(getDate("2015-01-06 08:00"), fifthTimeLine.start);
        Assert.assertEquals(getDate("2015-01-08 12:00"), fifthTimeLine.end);
    }
    
    /**
     * Splitting:
     *      
     *              |---------------------|
     * |--------------------|
     *                              |---------------------|
     *                      |-------|
     */
    
    @Test
    public void twoLinesOverlapping_4() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-06 08:00"));
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 12:00"));
        flattenTimeline.add(getBooking("2015-01-06 06:00", "2015-01-08 12:00"));
        flattenTimeline.add(getBooking("2015-01-05 12:00", "2015-01-06 06:00"));
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(5, timeLines.size());
        
        BookingTimeLine firstTimeLine = timeLines.get(0);
        Assert.assertEquals(1, firstTimeLine.count);
        Assert.assertEquals(getDate("2015-01-04 08:00"), firstTimeLine.start);
        Assert.assertEquals(getDate("2015-01-05 08:00"), firstTimeLine.end);
        
        
        BookingTimeLine secondTimeLine = timeLines.get(1);
        Assert.assertEquals(2, secondTimeLine.count);
        Assert.assertEquals(getDate("2015-01-05 08:00"), secondTimeLine.start);
        Assert.assertEquals(getDate("2015-01-05 12:00"), secondTimeLine.end);
        
        BookingTimeLine thirdTimeLine = timeLines.get(2);
        Assert.assertEquals(2, thirdTimeLine.count);
        Assert.assertEquals(getDate("2015-01-05 12:00"), thirdTimeLine.start);
        Assert.assertEquals(getDate("2015-01-06 06:00"), thirdTimeLine.end);
        
        BookingTimeLine fourTimeLine = timeLines.get(3);
        Assert.assertEquals(2, fourTimeLine.count);
        Assert.assertEquals(getDate("2015-01-06 06:00"), fourTimeLine.start);
        Assert.assertEquals(getDate("2015-01-06 08:00"), fourTimeLine.end);
        
        BookingTimeLine fifthTimeLine = timeLines.get(4);
        Assert.assertEquals(1, fifthTimeLine.count);
        Assert.assertEquals(getDate("2015-01-06 08:00"), fifthTimeLine.start);
        Assert.assertEquals(getDate("2015-01-08 12:00"), fifthTimeLine.end);
    }
    
    /**
     * Splitting:
     * 
     * |------------------|
     * |------------------|
     * |------------------|
     */
    @Test
    public void testExact() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 12:00"));
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 12:00"));
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 12:00"));
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(1, timeLines.size());
        Assert.assertEquals(3, timeLines.get(0).count);   
        Assert.assertEquals(7, timeLines.get(0).getAvailableSpots());   
        
    }
    
    private Booking getBooking(String startDate, String endDate) {        
        Booking booking = new Booking();
        booking.id = UUID.randomUUID().toString();
        booking.startDate = getDate(startDate);
        booking.endDate = getDate(endDate);
        booking.bookingItemTypeId = "bookingTypeId";

        return booking;
    }
    
    private Date getDate(String startDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return formatter.parse(startDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        
        return null;
    }

}
