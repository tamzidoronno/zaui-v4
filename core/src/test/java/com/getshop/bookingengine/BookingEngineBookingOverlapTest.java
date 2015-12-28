/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.bookingengine;

import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.BookingEngineAbstract;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.BookingEngineException;
import com.thundashop.core.databasemanager.data.Credentials;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author ktonder
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/All.xml")
public class BookingEngineBookingOverlapTest extends TestCommon {

    @InjectMocks
    @Spy
    BookingEngineAbstract abstractEngine;

    @InjectMocks
    BookingEngine bookingEngine;
    
    @Autowired
    private BookingEngineTestHelper helper;
    
    private BookingItemType type1;
    private BookingItemType type2;
    
    @Before
    public void setup() {
        abstractEngine.setCredentials(new Credentials(BookingEngine.class));
        bookingEngine.setCredentials(new Credentials(BookingEngine.class));
        bookingEngine.setConfirmationRequired(true);
        
        type1 = bookingEngine.createABookingItemType("type1");
        type2 =  bookingEngine.createABookingItemType("type1");
        
        createBookintItem(type1);
        createBookintItem(type2);
    }
    
    /**
     * Overlapping ---- = type 1, **** = type2
     * 
     * |----------------| (already added)
     * |----------------|
     * 
     */
    @Test(expected = BookingEngineException.class)
    public void throwsExceptionIfOverlapping_bookingalreadyexists() {
        List<Booking> bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-03 08:00"), type1, null));
        bookingEngine.addBookings(bookings);
        
        bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-03 08:00"), type1, null));
        bookingEngine.addBookings(bookings);
    }
    
    /**
     * Overlapping ---- = type 1, **** = type2
     * 
     * |----------------| (already added)
     *          |----------------|
     * 
     */
    @Test(expected = BookingEngineException.class)
    public void throwsExceptionIfOverlapping_2() {
        List<Booking> bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-03 08:00"), type1, null));
        bookingEngine.addBookings(bookings);
        
        bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 16:00"), helper.getDate("2014-01-03 16:00"), type1, null));
        bookingEngine.addBookings(bookings);
    }
    
    /**
     * Overlapping ---- = type 1, **** = type2
     * 
     *               |----------------| (already added)
     *  |----------------|
     * 
     */
    @Test(expected = BookingEngineException.class)
    public void throwsExceptionIfOverlapping_3() {
        List<Booking> bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 16:00"), helper.getDate("2014-01-03 16:00"), type1, null));
        bookingEngine.addBookings(bookings);
        
        bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-02 23:00"), type1, null));
        bookingEngine.addBookings(bookings);
    }
    
    /*
     * Overlapping ---- = type 1, **** = type2
     * 
     *               |----------------| (already added)
     *  |------------------------------------------|
     * 
     */
    @Test(expected = BookingEngineException.class)
    public void throwsExceptionIfOverlapping_4() {
        List<Booking> bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 16:00"), helper.getDate("2014-01-03 16:00"), type1, null));
        bookingEngine.addBookings(bookings);
        
        bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-04 23:00"), type1, null));
        bookingEngine.addBookings(bookings);
    }    
    
    /*
     * Overlapping ---- = type 1, **** = type2
     * 
     *  |-----------------------------| (already added)
     *       |--------------|
     * 
     */
    @Test(expected = BookingEngineException.class)
    public void throwsExceptionIfOverlapping_5() {
        List<Booking> bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-04 23:00"), type1, null));
        bookingEngine.addBookings(bookings);
        
        bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 16:00"), helper.getDate("2014-01-03 16:00"), type1, null));
        bookingEngine.addBookings(bookings);
    }
    
    /**
     * Overlapping ---- = type 1, **** = type2
     * 
     * |----------------| 
     * |----------------|
     * 
     */
    @Test(expected = BookingEngineException.class)
    public void throwsExceptionIfOverlapping_6() {
        List<Booking> bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-03 08:00"), type1, null));
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-03 08:00"), type1, null));
        bookingEngine.addBookings(bookings);
    }
    
    /**
     * Overlapping ---- = type 1, **** = type2
     * 
     * |----------------|
     *          |----------------|
     * 
     */
    @Test(expected = BookingEngineException.class)
    public void throwsExceptionIfOverlapping_7() {
        List<Booking> bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-03 08:00"), type1, null));
        bookings.add(getBooking(helper.getDate("2014-01-02 16:00"), helper.getDate("2014-01-03 16:00"), type1, null));
        bookingEngine.addBookings(bookings);
    }
    
    /**
     * Overlapping ---- = type 1, **** = type2
     * 
     *               |----------------| 
     *  |----------------|
     * 
     */
    @Test(expected = BookingEngineException.class)
    public void throwsExceptionIfOverlapping_8() {
        List<Booking> bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 16:00"), helper.getDate("2014-01-03 16:00"), type1, null));
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-02 23:00"), type1, null));
        bookingEngine.addBookings(bookings);
    }
    
    /*
     * Overlapping ---- = type 1, **** = type2
     * 
     *               |----------------| 
     *  |------------------------------------------|
     * 
     */
    @Test(expected = BookingEngineException.class)
    public void throwsExceptionIfOverlapping_9() {
        List<Booking> bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 16:00"), helper.getDate("2014-01-03 16:00"), type1, null));
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-04 23:00"), type1, null));
        bookingEngine.addBookings(bookings);
    }    
    
    /*
     * Overlapping ---- = type 1, **** = type2
     * 
     *  |-----------------------------| 
     *       |--------------|
     * 
     */
    @Test(expected = BookingEngineException.class)
    public void throwsExceptionIfOverlapping_10() {
        List<Booking> bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-04 23:00"), type1, null));
        bookings.add(getBooking(helper.getDate("2014-01-02 16:00"), helper.getDate("2014-01-03 16:00"), type1, null));
        bookingEngine.addBookings(bookings);
    }
    
    /*
     * Overlapping ---- = type 1, **** = type2
     * 
     *  |------------| 
     *               |------------|
     * 
     */
    @Test()
    public void allowedAdded_1() {
        List<Booking> bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-02 16:00"), type1, null));
        bookings.add(getBooking(helper.getDate("2014-01-02 16:00"), helper.getDate("2014-01-03 16:00"), type1, null));
        bookingEngine.addBookings(bookings);
    }
    
    /*
     * Overlapping ---- = type 1, **** = type2
     * 
     *  |------------| (already added)
     *               |------------|
     * 
     */
    @Test()
    public void allowedAdded_2() {
        List<Booking> bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-02 16:00"), type1, null));
        bookingEngine.addBookings(bookings);
        
        bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 16:00"), helper.getDate("2014-01-03 16:00"), type1, null));
        bookingEngine.addBookings(bookings);
    }
    
    /*
     * Overlapping ---- = type 1, **** = type2
     * 
     *  |------------| (already added)
     *  |************|
     * 
     */
    @Test()
    public void allowedAdded_3() {
        List<Booking> bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-02 16:00"), type1, null));
        bookingEngine.addBookings(bookings);
        
        bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 16:00"), helper.getDate("2014-01-03 16:00"), type2, null));
        bookingEngine.addBookings(bookings);
    }
    
    /*
     * Overlapping ---- = type 1, **** = type2
     * 
     *  |------------| (already added)
     *  |************|-----------|
     * 
     */
    @Test
    public void allowedAdded_4() {
        List<Booking> bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-02 16:00"), type1, null));
        bookingEngine.addBookings(bookings);
        
        bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-02 16:00"), type2, null));
        bookings.add(getBooking(helper.getDate("2014-01-02 16:00"), helper.getDate("2014-01-03 16:00"), type1, null));
        bookingEngine.addBookings(bookings);
    }
    
    /*
     * Overlapping ---- = type 1, **** = type2
     * 
     *  |------------| (already added)
     *  |************| (already added)
     *         |************|
     * 
     */
    @Test(expected = BookingEngineException.class)
    public void throwsExceptionIfOverlapping_11() {
        List<Booking> bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-02 16:00"), type1, null));
        bookingEngine.addBookings(bookings);
        
        bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-02 16:00"), type2, null));
        bookingEngine.addBookings(bookings);
        
        bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 14:00"), helper.getDate("2014-01-03 16:00"), type2, null));
        bookingEngine.addBookings(bookings);
    }
    
    /*
     * Overlapping ---- = type 1, **** = type2
     * 
     *  |------------|
     *  |************| 
     *         |************|
     * 
     */
    @Test(expected = BookingEngineException.class)
    public void throwsExceptionIfOverlapping_12() {
        List<Booking> bookings = new ArrayList();
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-02 16:00"), type1, null));
        bookings.add(getBooking(helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-02 16:00"), type2, null));
        bookings.add(getBooking(helper.getDate("2014-01-02 14:00"), helper.getDate("2014-01-03 16:00"), type2, null));
        bookingEngine.addBookings(bookings);
    }
    
    private BookingItem createBookintItem(BookingItemType type) {
        BookingItem bookingItem = new BookingItem();
        bookingItem.bookingItemTypeId = type.id;
        bookingItem.bookingSize = 1;
        bookingEngine.saveBookingItem(bookingItem);
        return bookingItem;
    }

    private Booking getBooking(Date startDate, Date endDate, BookingItemType type, BookingItem item) {
        Booking booking = new Booking();
        booking.bookingItemTypeId = type.id;
        if (item != null) {
            booking.bookingItemId = item.id;
        }
        
        booking.startDate = startDate;
        booking.endDate = endDate;
        return booking;
    }
}
