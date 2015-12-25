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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 *
 * @author ktonder
 */
public class BookingTimeLineFlattenTest {
    
    @Rule public TestName name = new TestName();
    
    @Before
    public void testName() {
//        System.out.println(" ");
//        System.out.println(name.getMethodName());
    }
    
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
     * Splitting
     * |-------1--------|
     *                  |-----2-------|
     *                                          |-------3-------|
     *                     |--------------4---------------|
     */
    @Test
    public void allInRow_1() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 08:00")); // 1
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-06 08:00")); // 2
        flattenTimeline.add(getBooking("2015-01-08 08:00", "2015-01-11 08:00")); // 3
        flattenTimeline.add(getBooking("2015-01-05 16:00", "2015-01-10 08:00")); // 4
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        printTimeLines(timeLines);
        Assert.assertEquals(6, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-04 08:00"), getDate("2015-01-05 08:00"), 1);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-05 08:00"), getDate("2015-01-05 16:00"), 1);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-05 16:00"), getDate("2015-01-06 08:00"), 2);
        
        assertDateAndCount(timeLines.get(3), getDate("2015-01-06 08:00"), getDate("2015-01-08 08:00"), 1);
        assertDateAndCount(timeLines.get(4), getDate("2015-01-08 08:00"), getDate("2015-01-10 08:00"), 2);
        assertDateAndCount(timeLines.get(5), getDate("2015-01-10 08:00"), getDate("2015-01-11 08:00"), 1);
    }
    
    /**
     * Splitting
     *                     |--------------4---------------|
     * |-------1--------|
     *                  |-----2-------|
     *                                          |-------3-------|
     *                     |--------------4---------------|
     */
    @Test
    public void allInRow_2() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-05 16:00", "2015-01-10 08:00")); // 4
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 08:00")); // 1
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-06 08:00")); // 2
        flattenTimeline.add(getBooking("2015-01-08 08:00", "2015-01-11 08:00")); // 3
        flattenTimeline.add(getBooking("2015-01-05 16:00", "2015-01-10 08:00")); // 4
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        printTimeLines(timeLines);
        Assert.assertEquals(6, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-04 08:00"), getDate("2015-01-05 08:00"), 1);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-05 08:00"), getDate("2015-01-05 16:00"), 1);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-05 16:00"), getDate("2015-01-06 08:00"), 3);
        
        assertDateAndCount(timeLines.get(3), getDate("2015-01-06 08:00"), getDate("2015-01-08 08:00"), 2);
        assertDateAndCount(timeLines.get(4), getDate("2015-01-08 08:00"), getDate("2015-01-10 08:00"), 3);
        assertDateAndCount(timeLines.get(5), getDate("2015-01-10 08:00"), getDate("2015-01-11 08:00"), 1);
    }
    
    
    /**
     * Splitting
     *    |----------------|
     *                     |--------------|
     *                                          |---------------|
     * 
     * |--------------------------------------------------------------------------|
     */
    @Test
    public void allInRowWithALongDate() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2115-01-05 08:00"));
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 08:00"));
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-06 08:00"));
        flattenTimeline.add(getBooking("2015-01-08 08:00", "2015-01-10 08:00"));

        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        
        Assert.assertEquals(6, timeLines.size());
        assertDateAndCount(timeLines.get(0), getDate("2015-01-03 08:00"), getDate("2015-01-04 08:00"), 1);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-04 08:00"), getDate("2015-01-05 08:00"), 2);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-05 08:00"), getDate("2015-01-06 08:00"), 2);
        assertDateAndCount(timeLines.get(3), getDate("2015-01-06 08:00"), getDate("2015-01-08 08:00"), 1);
        assertDateAndCount(timeLines.get(4), getDate("2015-01-08 08:00"), getDate("2015-01-10 08:00"), 2);
        assertDateAndCount(timeLines.get(5), getDate("2015-01-10 08:00"), getDate("2115-01-05 08:00"), 1);
    }
    
    /**
     * Splitting
     * |---------------------------0----------------------------------------------|
     *    |--------1-------|
     *                     |-------2------|
     *                                          |-------3------|
     */
    @Test
    public void allInRowWithALongDate_inEnd() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2115-01-05 08:00")); // 0
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 08:00")); // 1
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-06 08:00")); // 2
        flattenTimeline.add(getBooking("2015-01-08 08:00", "2015-01-10 08:00")); // 3

        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(6, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-03 08:00"), getDate("2015-01-04 08:00"), 1);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-04 08:00"), getDate("2015-01-05 08:00"), 2);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-05 08:00"), getDate("2015-01-06 08:00"), 2);
        assertDateAndCount(timeLines.get(3), getDate("2015-01-06 08:00"), getDate("2015-01-08 08:00"), 1);
        assertDateAndCount(timeLines.get(4), getDate("2015-01-08 08:00"), getDate("2015-01-10 08:00"), 2);
        assertDateAndCount(timeLines.get(5), getDate("2015-01-10 08:00"), getDate("2115-01-05 08:00"), 1);
    }
    
    /**
     * Splitting
     * |---------------------------0----------------------------------------------|
     *    |--------1-------|
     *                     |-------2------|
     *                                          |-------3------|
     * |---------------------------0----------------------------------------------|
     */
    @Test
    public void allInRowWithALongDate_inEnd_2() {
        
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2115-01-05 08:00")); // 0
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 08:00")); // 1
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-06 08:00")); // 2
        flattenTimeline.add(getBooking("2015-01-08 08:00", "2015-01-10 08:00")); // 3
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2115-01-05 08:00")); // 0
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(6, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-03 08:00"), getDate("2015-01-04 08:00"), 2);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-04 08:00"), getDate("2015-01-05 08:00"), 3);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-05 08:00"), getDate("2015-01-06 08:00"), 3);
        assertDateAndCount(timeLines.get(3), getDate("2015-01-06 08:00"), getDate("2015-01-08 08:00"), 2);
        assertDateAndCount(timeLines.get(4), getDate("2015-01-08 08:00"), getDate("2015-01-10 08:00"), 3);
        assertDateAndCount(timeLines.get(5), getDate("2015-01-10 08:00"), getDate("2115-01-05 08:00"), 2);
        
    }
    
    /**
     * Splitting
     * |---------------------------0------------------|
     * |--------1-------|
     *                  |-------2------|
     *                                 |-------3------|
     */
    @Test
    public void allInRowWithALongDate_3() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2115-01-05 08:00")); // 0
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2015-01-05 08:00")); // 1
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-08 08:00")); // 2
        flattenTimeline.add(getBooking("2015-01-08 08:00", "2115-01-05 08:00")); // 3

        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(3, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-03 08:00"), getDate("2015-01-05 08:00"), 2);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-05 08:00"), getDate("2015-01-08 08:00"), 2);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-08 08:00"), getDate("2115-01-05 08:00"), 2);
    }
    
    /**
     * Splitting
     * 
     * |--------0-------|
     *                  |-------1------|
     *                                 |-------2------|
     * |---------------------------3------------------|
     */
    @Test
    public void allInRowWithALongDate_4() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2015-01-05 08:00")); // 0
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-08 08:00")); // 1
        flattenTimeline.add(getBooking("2015-01-08 08:00", "2115-01-05 08:00")); // 2
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2115-01-05 08:00")); // 3
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(3, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-03 08:00"), getDate("2015-01-05 08:00"), 2);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-05 08:00"), getDate("2015-01-08 08:00"), 2);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-08 08:00"), getDate("2115-01-05 08:00"), 2);
    }
    
    /**
     * Splitting
     * 
     * |---------------------------0------------------|
     * |--------1-------|
     *                  |-------2------|
     *                                 |-------3------|
     * |---------------------------4------------------|
     */
    @Test
    public void allInRowWithALongDate_5() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2115-01-05 08:00")); // 0
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2015-01-05 08:00")); // 1
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-08 08:00")); // 2
        flattenTimeline.add(getBooking("2015-01-08 08:00", "2115-01-05 08:00")); // 3
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2115-01-05 08:00")); // 4
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(3, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-03 08:00"), getDate("2015-01-05 08:00"), 3);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-05 08:00"), getDate("2015-01-08 08:00"), 3);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-08 08:00"), getDate("2115-01-05 08:00"), 3);
    }
    
    /**
     * Splitting
     * 
     * |---------------------------0------------------|
     *  |-------1-------|
     *                  |-------2------|
     *                                 |------3------|
     * |---------------------------4------------------|
     */
    @Test
    public void allInRowWithALongDate_6() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2115-01-05 08:00")); // 0
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 08:00")); // 1
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-08 08:00")); // 2
        flattenTimeline.add(getBooking("2015-01-08 08:00", "2114-01-05 08:00")); // 3
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2115-01-05 08:00")); // 4
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(5, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-03 08:00"), getDate("2015-01-04 08:00"), 2);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-04 08:00"), getDate("2015-01-05 08:00"), 3);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-05 08:00"), getDate("2015-01-08 08:00"), 3);
        assertDateAndCount(timeLines.get(3), getDate("2015-01-08 08:00"), getDate("2114-01-05 08:00"), 3);
        assertDateAndCount(timeLines.get(4), getDate("2114-01-05 08:00"), getDate("2115-01-05 08:00"), 2);
    }
    
    /**
     * Splitting
     * 
     * |---------------------------0------------------|
     *  |-------1-------|
     *                      |---2---|
     *                                 |------3------|
     * |---------------------------4------------------|
     */
    @Test
    public void allInRowWithALongDate_7() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2115-01-05 08:00")); // 0
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 08:00")); // 1
        flattenTimeline.add(getBooking("2015-01-06 08:00", "2015-01-07 08:00")); // 2
        flattenTimeline.add(getBooking("2015-01-08 08:00", "2114-01-05 08:00")); // 3
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2115-01-05 08:00")); // 4
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(7, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-03 08:00"), getDate("2015-01-04 08:00"), 2);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-04 08:00"), getDate("2015-01-05 08:00"), 3);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-05 08:00"), getDate("2015-01-06 08:00"), 2);
        assertDateAndCount(timeLines.get(3), getDate("2015-01-06 08:00"), getDate("2015-01-07 08:00"), 3);
        assertDateAndCount(timeLines.get(4), getDate("2015-01-07 08:00"), getDate("2015-01-08 08:00"), 2);
        assertDateAndCount(timeLines.get(5), getDate("2015-01-08 08:00"), getDate("2114-01-05 08:00"), 3);
        assertDateAndCount(timeLines.get(6), getDate("2114-01-05 08:00"), getDate("2115-01-05 08:00"), 2);
    }
    
    
    /**
     * Splitting
     * 
     * |---------------------------0------------------|
     *  |-------1-------|
     *                      |---2---|
     *                                 |------3------|
     * |---------------------------4------------------|
     *                                                |-----5-----|
     */
    @Test
    public void allInRowWithALongDate_8() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2115-01-05 08:00")); // 0
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 08:00")); // 1
        flattenTimeline.add(getBooking("2015-01-06 08:00", "2015-01-07 08:00")); // 2
        flattenTimeline.add(getBooking("2015-01-08 08:00", "2114-01-05 08:00")); // 3
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2115-01-05 08:00")); // 4
        flattenTimeline.add(getBooking("2115-01-05 08:00", "2117-01-05 08:00")); // 5
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(8, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-03 08:00"), getDate("2015-01-04 08:00"), 2);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-04 08:00"), getDate("2015-01-05 08:00"), 3);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-05 08:00"), getDate("2015-01-06 08:00"), 2);
        assertDateAndCount(timeLines.get(3), getDate("2015-01-06 08:00"), getDate("2015-01-07 08:00"), 3);
        assertDateAndCount(timeLines.get(4), getDate("2015-01-07 08:00"), getDate("2015-01-08 08:00"), 2);
        assertDateAndCount(timeLines.get(5), getDate("2015-01-08 08:00"), getDate("2114-01-05 08:00"), 3);
        assertDateAndCount(timeLines.get(6), getDate("2114-01-05 08:00"), getDate("2115-01-05 08:00"), 2);
        assertDateAndCount(timeLines.get(7), getDate("2115-01-05 08:00"), getDate("2117-01-05 08:00"), 1);
    }
    
    /**
     * Splitting
     * 
     * |--------0-------|
     *                  |-------1------|
     *                                 |-------2------|
     *    |-------------------3------------------|
     */
    @Test
    public void allInRowWithALongDate_9() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2015-01-05 08:00")); // 0
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-08 08:00")); // 1
        flattenTimeline.add(getBooking("2015-01-08 08:00", "2015-01-10 08:00")); // 2
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-09 08:00")); // 3
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(5, timeLines.size());
        
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-03 08:00"), getDate("2015-01-04 08:00"), 1);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-04 08:00"), getDate("2015-01-05 08:00"), 2);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-05 08:00"), getDate("2015-01-08 08:00"), 2);
        assertDateAndCount(timeLines.get(3), getDate("2015-01-08 08:00"), getDate("2015-01-09 08:00"), 2);
        assertDateAndCount(timeLines.get(4), getDate("2015-01-09 08:00"), getDate("2015-01-10 08:00"), 1);
    }
    
    /**
     * Splitting
     * 
     * |--------0-------|
     *                  |-------1------|
     *                                 |-------2-----|
     *    |-------------------3------------------|---4---|
     */
    @Test
    public void allInRowWithALongDate_10() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2015-01-05 08:00")); // 0
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-08 08:00")); // 1
        flattenTimeline.add(getBooking("2015-01-08 08:00", "2015-01-10 08:00")); // 2
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-09 08:00")); // 3
        flattenTimeline.add(getBooking("2015-01-09 08:00", "2015-01-12 08:00")); // 4
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(6, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-03 08:00"), getDate("2015-01-04 08:00"), 1);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-04 08:00"), getDate("2015-01-05 08:00"), 2);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-05 08:00"), getDate("2015-01-08 08:00"), 2);
        assertDateAndCount(timeLines.get(3), getDate("2015-01-08 08:00"), getDate("2015-01-09 08:00"), 2);
        assertDateAndCount(timeLines.get(4), getDate("2015-01-09 08:00"), getDate("2015-01-10 08:00"), 2);
        assertDateAndCount(timeLines.get(5), getDate("2015-01-10 08:00"), getDate("2015-01-12 08:00"), 1);
    }
    
    /**
     * Splitting
     * 
     * |--------0-------|
     *                  |-------1------|
     *    |-------------------3.-----------------|
     *                                 |-------2-----|
     *                                           |---4---|
     */
    @Test
    public void allInRowWithALongDate_10_2() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2015-01-05 08:00")); // 0
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-08 08:00")); // 1
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-09 08:00")); // 3
        flattenTimeline.add(getBooking("2015-01-08 08:00", "2015-01-10 08:00")); // 2
        flattenTimeline.add(getBooking("2015-01-09 08:00", "2015-01-12 08:00")); // 4
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(6, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-03 08:00"), getDate("2015-01-04 08:00"), 1);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-04 08:00"), getDate("2015-01-05 08:00"), 2);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-05 08:00"), getDate("2015-01-08 08:00"), 2);
        assertDateAndCount(timeLines.get(3), getDate("2015-01-08 08:00"), getDate("2015-01-09 08:00"), 2);
        assertDateAndCount(timeLines.get(4), getDate("2015-01-09 08:00"), getDate("2015-01-10 08:00"), 2);
        assertDateAndCount(timeLines.get(5), getDate("2015-01-10 08:00"), getDate("2015-01-12 08:00"), 1);
    }
    
    /**
     * Splitting
     *    |-------------------3.-----------------|
     * |--------0-------|
     *                  |-------1------|
     *                                 |-------2-----|
     *                                           |---4---|
     */
    @Test
    public void allInRowWithALongDate_10_3() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-09 08:00")); // 3
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2015-01-05 08:00")); // 0
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-08 08:00")); // 1
        flattenTimeline.add(getBooking("2015-01-08 08:00", "2015-01-10 08:00")); // 2
        flattenTimeline.add(getBooking("2015-01-09 08:00", "2015-01-12 08:00")); // 4
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(6, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-03 08:00"), getDate("2015-01-04 08:00"), 1);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-04 08:00"), getDate("2015-01-05 08:00"), 2);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-05 08:00"), getDate("2015-01-08 08:00"), 2);
        assertDateAndCount(timeLines.get(3), getDate("2015-01-08 08:00"), getDate("2015-01-09 08:00"), 2);
        assertDateAndCount(timeLines.get(4), getDate("2015-01-09 08:00"), getDate("2015-01-10 08:00"), 2);
        assertDateAndCount(timeLines.get(5), getDate("2015-01-10 08:00"), getDate("2015-01-12 08:00"), 1);
    }
    
    /**
     * Splitting
     *    
     * |--------0-------|
     *     |-------------------3.-----------------|
     *                  |-------1------|
     *                                 |-------2-----|
     *                                           |---4---|
     */
    @Test
    public void allInRowWithALongDate_10_4() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2015-01-05 08:00")); // 0
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-09 08:00")); // 3
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-08 08:00")); // 1
        flattenTimeline.add(getBooking("2015-01-08 08:00", "2015-01-10 08:00")); // 2
        flattenTimeline.add(getBooking("2015-01-09 08:00", "2015-01-12 08:00")); // 4
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(6, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-03 08:00"), getDate("2015-01-04 08:00"), 1);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-04 08:00"), getDate("2015-01-05 08:00"), 2);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-05 08:00"), getDate("2015-01-08 08:00"), 2);
        assertDateAndCount(timeLines.get(3), getDate("2015-01-08 08:00"), getDate("2015-01-09 08:00"), 2);
        assertDateAndCount(timeLines.get(4), getDate("2015-01-09 08:00"), getDate("2015-01-10 08:00"), 2);
        assertDateAndCount(timeLines.get(5), getDate("2015-01-10 08:00"), getDate("2015-01-12 08:00"), 1);
    }
    
    /**
     * Splitting
     *    
     * |--------0-------|
     *     |-------------------3.-----------------|
     *                  |-------1------|
     *                                 |-------2-----|
     *                                           |---4---|
     *                  |-------1------|
     */
    @Test
    public void allInRowWithALongDate_10_5() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2015-01-05 08:00")); // 0
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-09 08:00")); // 3
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-08 08:00")); // 1
        flattenTimeline.add(getBooking("2015-01-08 08:00", "2015-01-10 08:00")); // 2
        flattenTimeline.add(getBooking("2015-01-09 08:00", "2015-01-12 08:00")); // 4
        flattenTimeline.add(getBooking("2015-01-05 08:00", "2015-01-08 08:00")); // 1
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(6, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-03 08:00"), getDate("2015-01-04 08:00"), 1);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-04 08:00"), getDate("2015-01-05 08:00"), 2);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-05 08:00"), getDate("2015-01-08 08:00"), 3);
        assertDateAndCount(timeLines.get(3), getDate("2015-01-08 08:00"), getDate("2015-01-09 08:00"), 2);
        assertDateAndCount(timeLines.get(4), getDate("2015-01-09 08:00"), getDate("2015-01-10 08:00"), 2);
        assertDateAndCount(timeLines.get(5), getDate("2015-01-10 08:00"), getDate("2015-01-12 08:00"), 1);
    }
    
    /*
     * Splitting
     * 
     *         |--0--|
     *    |---1---|---2---|
     */
    @Test
    public void allInRowWithALongDate_11() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2015-01-05 08:00")); // 0
        flattenTimeline.add(getBooking("2015-01-02 08:00", "2015-01-04 08:00")); // 1
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-06 08:00")); // 2
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(4, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-02 08:00"), getDate("2015-01-03 08:00"), 1);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-03 08:00"), getDate("2015-01-04 08:00"), 2);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-04 08:00"), getDate("2015-01-05 08:00"), 2);
        assertDateAndCount(timeLines.get(3), getDate("2015-01-05 08:00"), getDate("2015-01-06 08:00"), 1);
    }
    
    /*
     * Splitting
     * 
     *    |---1---|---2---|
     *         |--0--|
     */
    @Test
    public void allInRowWithALongDate_12() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-02 08:00", "2015-01-04 08:00")); // 1
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-06 08:00")); // 2
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2015-01-05 08:00")); // 0
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(4, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-02 08:00"), getDate("2015-01-03 08:00"), 1);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-03 08:00"), getDate("2015-01-04 08:00"), 2);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-04 08:00"), getDate("2015-01-05 08:00"), 2);
        assertDateAndCount(timeLines.get(3), getDate("2015-01-05 08:00"), getDate("2015-01-06 08:00"), 1);
    }
    
    /*
     * Splitting
     * 
     *    |---1---|---2---|
     *         |--0--|           
     *            |---4---|
     */    
    @Test
    public void allInRowWithALongDate_13() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-02 08:00", "2015-01-04 08:00")); // 1
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-06 08:00")); // 2
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2015-01-05 08:00")); // 0
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-06 08:00")); // 4
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        printTimeLines(timeLines);
        Assert.assertEquals(4, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-02 08:00"), getDate("2015-01-03 08:00"), 1);
        assertDateAndCount(timeLines.get(1), getDate("2015-01-03 08:00"), getDate("2015-01-04 08:00"), 2);
        assertDateAndCount(timeLines.get(2), getDate("2015-01-04 08:00"), getDate("2015-01-05 08:00"), 3);
        assertDateAndCount(timeLines.get(3), getDate("2015-01-05 08:00"), getDate("2015-01-06 08:00"), 2);
    }
    
    /**
     * Splitting
     * |--------------------------------------------------------------------------|
     *                     |--------------|
     */
    @Test
    public void allInRowWithALongDate_inEnd_3() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2115-01-05 08:00"));
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 08:00"));

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
    
    /**
     * Splitting:
     * 
     * |------------------| |------------------|  
     * |------------------| |------------------|  
     * |------------------| |------------------|  
     */
    @Test
    public void testExact_2() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 12:00"));
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 12:00"));
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 12:00"));
        
        flattenTimeline.add(getBooking("2015-01-14 08:00", "2015-01-15 12:00"));
        flattenTimeline.add(getBooking("2015-01-14 08:00", "2015-01-15 12:00"));
        flattenTimeline.add(getBooking("2015-01-14 08:00", "2015-01-15 12:00"));
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        Assert.assertEquals(2, timeLines.size());
        Assert.assertEquals(3, timeLines.get(0).count);   
        Assert.assertEquals(3, timeLines.get(1).count);   
        Assert.assertEquals(7, timeLines.get(0).getAvailableSpots());   
        Assert.assertEquals(7, timeLines.get(1).getAvailableSpots());   
    }
    
    
    /**
     * Splitting:
     * 
     * |------------------| |------------------|  
     * |------------------| |------------------|  
     * |------------------| |------------------|  
     * |---------------------------------------|
     */
    @Test
    public void testExact_3() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 12:00"));
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 12:00"));
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 12:00"));
        
        flattenTimeline.add(getBooking("2015-01-14 08:00", "2015-01-15 12:00"));
        flattenTimeline.add(getBooking("2015-01-14 08:00", "2015-01-15 12:00"));
        flattenTimeline.add(getBooking("2015-01-14 08:00", "2015-01-15 12:00"));
        
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-15 12:00"));
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        printTimeLines(timeLines);
        Assert.assertEquals(3, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-04 08:00"), getDate("2015-01-05 12:00"), 4); 
        assertDateAndCount(timeLines.get(1), getDate("2015-01-05 12:00"), getDate("2015-01-14 08:00"), 1); 
        assertDateAndCount(timeLines.get(2), getDate("2015-01-14 08:00"), getDate("2015-01-15 12:00"), 4); 
    }
    
    /**
     * Splitting:
     * 
     * |--------------------------------------------|
     *   |------------------| |------------------|  
     *   |------------------| |------------------|  
     *   |------------------| |------------------|  
     */
    @Test
    public void testExact_4() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2015-01-14 12:00")); 
        
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 12:00"));
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 12:00"));
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 12:00"));
        
        flattenTimeline.add(getBooking("2015-01-07 08:00", "2015-01-08 12:00"));
        flattenTimeline.add(getBooking("2015-01-07 08:00", "2015-01-08 12:00"));
        flattenTimeline.add(getBooking("2015-01-07 08:00", "2015-01-08 12:00"));
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        printTimeLines(timeLines);
        Assert.assertEquals(5, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-03 08:00"), getDate("2015-01-04 08:00"), 1); 
        assertDateAndCount(timeLines.get(1), getDate("2015-01-04 08:00"), getDate("2015-01-05 12:00"), 4); 
        assertDateAndCount(timeLines.get(2), getDate("2015-01-05 12:00"), getDate("2015-01-07 08:00"), 1); 
        assertDateAndCount(timeLines.get(3), getDate("2015-01-07 08:00"), getDate("2015-01-08 12:00"), 4); 
        assertDateAndCount(timeLines.get(4), getDate("2015-01-08 12:00"), getDate("2015-01-14 12:00"), 1); 
    }
    
    /**
     * Splitting:
     * 
     *   |------------------| |------------------|  
     *   |------------------| |------------------|  
     *   |------------------| |------------------|  
     * |--------------------------------------------|
     */
    @Test
    public void testExact_5() {
        BookingTimeLineFlatten flattenTimeline = new BookingTimeLineFlatten(10, "bookingTypeId");
        
        
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 12:00"));
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 12:00"));
        flattenTimeline.add(getBooking("2015-01-04 08:00", "2015-01-05 12:00"));
        
        flattenTimeline.add(getBooking("2015-01-07 08:00", "2015-01-08 12:00"));
        flattenTimeline.add(getBooking("2015-01-07 08:00", "2015-01-08 12:00"));
        flattenTimeline.add(getBooking("2015-01-07 08:00", "2015-01-08 12:00"));
        
        flattenTimeline.add(getBooking("2015-01-03 08:00", "2015-01-14 12:00")); 
        
        List<BookingTimeLine> timeLines = flattenTimeline.getTimelines();
        printTimeLines(timeLines);
        Assert.assertEquals(5, timeLines.size());
        
        assertDateAndCount(timeLines.get(0), getDate("2015-01-03 08:00"), getDate("2015-01-04 08:00"), 1); 
        assertDateAndCount(timeLines.get(1), getDate("2015-01-04 08:00"), getDate("2015-01-05 12:00"), 4); 
        assertDateAndCount(timeLines.get(2), getDate("2015-01-05 12:00"), getDate("2015-01-07 08:00"), 1); 
        assertDateAndCount(timeLines.get(3), getDate("2015-01-07 08:00"), getDate("2015-01-08 12:00"), 4); 
        assertDateAndCount(timeLines.get(4), getDate("2015-01-08 12:00"), getDate("2015-01-14 12:00"), 1); 
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

    private void assertDateAndCount(BookingTimeLine timeLine, Date start, Date end, int count) {
        Assert.assertEquals(count, timeLine.count);
        Assert.assertEquals(start, timeLine.start);
        Assert.assertEquals(end, timeLine.end);
    }

    private void printTimeLines(List<BookingTimeLine> timeLines) {
        System.out.println("Result:");
        for (BookingTimeLine timeLine : timeLines) {
            System.out.println(timeLine);
        }
    }

    private void printMarker(String string) {
        System.out.println("Mark:" + string);
    }

}
