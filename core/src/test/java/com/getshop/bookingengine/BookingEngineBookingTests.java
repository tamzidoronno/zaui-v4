/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.bookingengine;

import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingGroup;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.BookingRequiredConfirmationList;
import com.thundashop.core.common.BookingEngineException;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.Credentials;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author ktonder
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/All.xml")
public class BookingEngineBookingTests extends TestCommon {
    @InjectMocks
    BookingEngine bookingEngine;
    
    @Autowired
    private BookingEngineTestHelper helper;
    
    @After
    public void setup() {
        bookingEngine.setCredentials(new Credentials(BookingEngine.class));
    }
    
    @Test
    public void addBooking() {
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine));
        bookings.add(helper.getValidBooking(2, bookingEngine));
        
        reset(databaseSaver);
        BookingGroup bookingGroup = bookingEngine.addBookings(bookings);
        verify(databaseSaver, times(5)).saveObject(any(DataCommon.class), any(Credentials.class));
        Assert.assertNotNull(bookingGroup);
    }
    
    /**
     * Making sure that userid is set if user is 
     * logged in while creating booking groups.
     */
    @Test
    public void userIdSetToBookingGroupIfUserLoggedIn() {
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine));
        bookings.add(helper.getValidBooking(2, bookingEngine));
        
        setUserLoggedIn(bookingEngine);
        BookingGroup bookingGroup = bookingEngine.addBookings(bookings);
        Assert.assertNotNull(bookingGroup.userCreatedByUserId);
    }
    
    /**
     * If a booking is added and the bookingItemType does not exists,
     * it will throw an expection.
     */
    @Test(expected = BookingEngineException.class)
    public void addBookingThrowsExceptionIfBookingItemDoesNotExists() {
        List<Booking> bookings = new ArrayList();
        Booking booking = helper.getValidBooking(1, bookingEngine);
        booking.bookingItemTypeId = "not_exists";
        bookings.add(booking);
        bookingEngine.addBookings(bookings);
    }
    
    /**
     * Check that the booking item id is missing.
     * 
     * confirmationRequired = false
     * bookingItemId missing
     */
    @Test(expected = BookingEngineException.class)
    public void throwsExcpetionIfBookingItemIdIsNotSet() {
        List<Booking> bookings = new ArrayList();
        Booking booking = helper.getValidBooking(1, bookingEngine);
        booking.bookingItemId = "not exists";
        bookings.add(booking);
        bookingEngine.setConfirmationRequired(false);
        bookingEngine.addBookings(bookings);
    }
    
    /**
     * Check that its allowed to create booking if id
     * does not exists.
     * 
     * confirmationRequired = true
     * bookingItemId missing
     */
    @Test
    public void testAllowToAddWithoutBookingItemIdIfRequiredIsFalse() {
        List<Booking> bookings = new ArrayList();
        Booking booking = helper.getValidBooking(1, bookingEngine);
        booking.bookingItemId = null;
        bookings.add(booking);
        bookingEngine.setConfirmationRequired(true);
        bookingEngine.addBookings(bookings);
    }
 
    /**
     * Checking that data from database is used for booking item
     */
    @Test
    public void testBookingFromDatabaseIsUsed() {
        String id = UUID.randomUUID().toString();
        Booking booking = new Booking();
        booking.id = id;
        feedDataFromDatabase(bookingEngine, booking);
        booking = bookingEngine.getBooking(id);
        Assert.assertNotNull(booking);
    }
    
    /**
     * Feeding confirmationlist from database.
     */
    @Test
    public void testRequiredListIsAddedFromDatabase() {
        BookingRequiredConfirmationList req = new BookingRequiredConfirmationList();
        req.id = UUID.randomUUID().toString();
        req.bookingItemTypeId = "thisone";
        
        feedDataFromDatabase(bookingEngine, req);
        BookingRequiredConfirmationList fetchedOne = bookingEngine.getConfirmationList("thisone");
        Assert.assertEquals(req.id, fetchedOne.id);
    }
    
    /**
     * If a booking is added and confirmation is required, is it then added to the list and
     * not directly to the booking item?
     */
    @Test
    public void isBookingAddedToListIfConfirmationRequired() {
        bookingEngine.setConfirmationRequired(false);
        
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine));
        bookingEngine.setConfirmationRequired(true);
        
        reset(databaseSaver);
        bookingEngine.addBookings(bookings);
        verify(databaseSaver, times(3)).saveObject(any(DataCommon.class), any(Credentials.class));
        
        BookingRequiredConfirmationList requireConfirmation = bookingEngine.getConfirmationList(bookings.get(0).bookingItemTypeId);
        Assert.assertEquals(1, requireConfirmation.bookings.size());
        Assert.assertEquals(0, bookingEngine.getBookingItem(bookings.get(0).bookingItemId).bookingIds.size());
    }
 
    /**
     * If BookingEngine is not set to require confirmation
     * check that its not added to requirelist but directly to bookingitem.
     */
    @Test
    public void isBookingAddedToBookingIfConfirmationIsNotRequired() {
        bookingEngine.setConfirmationRequired(false);
        
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine));
        bookingEngine.setConfirmationRequired(false);
        
        reset(databaseSaver);
        bookingEngine.addBookings(bookings);
        verify(databaseSaver, times(3)).saveObject(any(DataCommon.class), any(Credentials.class));
        
        BookingRequiredConfirmationList requireConfirmation = bookingEngine.getConfirmationList(bookings.get(0).bookingItemTypeId);
        Assert.assertEquals(0, requireConfirmation.bookings.size());
        Assert.assertEquals(1, bookingEngine.getBookingItem(bookings.get(0).bookingItemId).bookingIds.size());
    }
 
    /**
     * Make sure that addBookings is not used for updating bookings.
     */
    @Test(expected = BookingEngineException.class)
    public void testThatThrowsExceptionIfBookingIdIsSet() {
        List<Booking> bookings = new ArrayList();
        Booking booking = helper.getValidBooking(1, bookingEngine);
        booking.id = UUID.randomUUID().toString();
        bookings.add(booking);
        
        bookingEngine.addBookings(bookings);
    }
    
    /**
     * Booking throws expection if there is not enough free spots.
     */
    
    @Test
    public void addBooking_expectionThrownWhenFull() {
        List<Booking> bookings = new ArrayList();
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItem item = helper.createAValidBookingItem(type.id);
        item.bookingSize = 1;
        bookingEngine.saveBookingItem(item);
        
        bookings.add(helper.getValidBooking(1, bookingEngine, item));
        bookings.add(helper.getValidBooking(1, bookingEngine, item));
        
        BookingGroup bookingGroup = bookingEngine.addBookings(bookings);
    }
    
}