/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.bookingengine;

import com.thundashop.core.bookingengine.BookingItemAssignerOptimal;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.common.BookingEngineException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author ktonder
 */
public class BookingEngineItemAssignerOptimal {
    private BookingItemType type1;
    private BookingItem item1;
    private BookingItem item2;
    private BookingItem item3;
    
    
    @Before
    public void setup() {
        type1 = new BookingItemType();
        type1.name = "type1";
        type1.id = UUID.randomUUID().toString();
        
        item1 = getBookingItem();
        item2 = getBookingItem();
        item3 = getBookingItem();
    }
    /**
     * Testing that this simple case is assigned
     * Booking1: |----------|
     * Booking2:            |---------|
     * 
     * Assigned to the first available room.
     */
    @Test
    public void assignBookings() {
        ArrayList<Booking> bookings = new ArrayList();
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 09:00", null));
        bookings.add(getBooking("2015-01-05 09:00", "2015-01-05 10:00", null));
        
        runTest(bookings);
        
        Assert.assertEquals(item1.id, bookings.get(0).bookingItemId); 
        Assert.assertEquals(item1.id, bookings.get(1).bookingItemId); 
        
        Assert.assertEquals(2, item1.bookingIds.size()); 
    }
    
    
    /**
     * Testing that this simple case is assigned
     * 
     * x already assigned to item1
     * y already assigned to item1
     * 
     * Booking1: |----x-----|
     * Booking2:            |----y----|
     * 
     * Assigned to the first available room.
     */
    @Test
    public void assignBookings_checkAvailableResult() {
        ArrayList<Booking> bookings = new ArrayList();
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 09:00", item1));
        bookings.add(getBooking("2015-01-05 09:00", "2015-01-05 10:00", item1));
        
        List<String> res = getAvailableResult(bookings);
        Assert.assertEquals(2, res.size()); 
        Assert.assertEquals(item2.id, res.get(0)); 
        Assert.assertEquals(item3.id, res.get(1)); 
    }
    
    /**
     * Testing that this simple case is assigned
     * 
     * Booking1: |----------|
     * Booking2:            |---------|
     * 
     * Assigned to the first available room.
     */
    @Test
    public void assignBookings_checkAvailableResult2() {
        ArrayList<Booking> bookings = new ArrayList();
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 09:00", null));
        bookings.add(getBooking("2015-01-05 09:00", "2015-01-05 10:00", null));
        
        List<String> res = getAvailableResult(bookings);
        Assert.assertEquals(3, res.size()); 
        Assert.assertEquals(item1.id, res.get(0)); 
        Assert.assertEquals(item2.id, res.get(1)); 
        Assert.assertEquals(item3.id, res.get(2)); 
    }
    
    /**
     * Testing that this simple case is assigned
     * 
     * x = assigned to item2
     * 
     * Booking1: |----------|
     * Booking2:            |-----x---|
     * 
     * Assigned to the first available room.
     */
    @Test
    public void assignBookings_checkAvailableResult3() {
        ArrayList<Booking> bookings = new ArrayList();
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 09:00", null));
        bookings.add(getBooking("2015-01-05 09:00", "2015-01-05 10:00", item2));
        
        List<String> res = getAvailableResult(bookings);
        Assert.assertEquals(2, res.size()); 
        Assert.assertEquals(item1.id, res.get(0)); 
        Assert.assertEquals(item3.id, res.get(1)); 
    }
    
    /**
     * Testing that this simple case is assigned
     * Booking1: |----------|
     * Booking2:            |---------|
     * Booking3:   |----------------|
     * 
     * Assigned to the first available room.
     */
    @Test
    public void assignBookings_1() {
        ArrayList<Booking> bookings = new ArrayList();
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 09:00", null));
        bookings.add(getBooking("2015-01-05 09:00", "2015-01-05 10:00", null));
        bookings.add(getBooking("2015-01-05 08:30", "2015-01-05 09:30", null));
        
        runTest(bookings);
        
        Assert.assertEquals(item1.id, bookings.get(0).bookingItemId); 
        Assert.assertEquals(item1.id, bookings.get(1).bookingItemId); 
        Assert.assertEquals(item2.id, bookings.get(2).bookingItemId); 
        
        Assert.assertEquals(2, item1.bookingIds.size()); 
        Assert.assertEquals(1, item2.bookingIds.size()); 
    }
    
    /**
     * Testing that this simple case is assigned
     * 
     * x = already assigned to item1
     * 
     * Booking1: |----------|
     * Booking2:            |----x----|
     * Booking3:   |----------------|
     * 
     * Assigned to the first available room.
     */
    @Test
    public void assignBookings_2() {
        ArrayList<Booking> bookings = new ArrayList();
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 09:00", null));
        bookings.add(getBooking("2015-01-05 09:00", "2015-01-05 10:00", item1));
        bookings.add(getBooking("2015-01-05 08:30", "2015-01-05 09:30", null));
        
        runTest(bookings);
        
        Assert.assertEquals(item1.id, bookings.get(0).bookingItemId); 
        Assert.assertEquals(item1.id, bookings.get(1).bookingItemId); 
        Assert.assertEquals(item2.id, bookings.get(2).bookingItemId); 
        
        Assert.assertEquals(2, item1.bookingIds.size()); 
        Assert.assertEquals(1, item2.bookingIds.size()); 
    }
    
    /**
     * Testing that this simple case is assigned
     * 
     * x = already assigned to item1
     * y = already assigned to item2
     * 
     * Booking1: |----x-----|
     * Booking2:            |----y----|
     * Booking3:   |----------------|
     * 
     * Assigned to the first available room.
     */
    @Test
    public void assignBookings_3() {
        ArrayList<Booking> bookings = new ArrayList();
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 09:00", item1));
        bookings.add(getBooking("2015-01-05 09:00", "2015-01-05 10:00", item2));
        bookings.add(getBooking("2015-01-05 08:30", "2015-01-05 09:30", null));
        
        runTest(bookings);
        
        Assert.assertEquals(item1.id, bookings.get(0).bookingItemId); 
        Assert.assertEquals(item2.id, bookings.get(1).bookingItemId); 
        Assert.assertEquals(item3.id, bookings.get(2).bookingItemId); 
        
        Assert.assertEquals(1, item1.bookingIds.size()); 
        Assert.assertEquals(1, item1.bookingIds.size()); 
        Assert.assertEquals(1, item2.bookingIds.size()); 
    }
    
    /**
     * Testing that this simple case is assigned
     * 
     * x = already assigned to item1
     * y = already assigned to item2
     * 
     * Booking1: |----x-----|
     * Booking2:            |----y----|
     * Booking3:   |------x---------|
     * 
     * Assigned to the first available room.
     */
    @Test(expected = BookingEngineException.class)
    public void assignBookings_4() {
        ArrayList<Booking> bookings = new ArrayList();
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 09:00", item1));
        bookings.add(getBooking("2015-01-05 09:00", "2015-01-05 10:00", item2));
        bookings.add(getBooking("2015-01-05 08:30", "2015-01-05 09:30", item1));
        
        runTest(bookings);
    }
    
    /**
     * Testing that this simple case is assigned
     * 
     * x = already assigned to item1
     * y = already assigned to item2
     * 
     * Booking1: |---x-----|
     * Booking2:           |----y----|
     * Booking3: |-------------------|
     * Booking4: |-------------------|
     * 
     * Assigned to the first available room.
     */
    @Test(expected = BookingEngineException.class)
    public void assignBookings_5() {
        ArrayList<Booking> bookings = new ArrayList();
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 09:00", item1));
        bookings.add(getBooking("2015-01-05 09:00", "2015-01-05 10:00", item2));
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 10:00", null));
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 10:00", null));
        
        runTest(bookings);
    }
    
    /**
     * Testing that this simple case is assigned
     * 
     * x = already assigned to item1
     * y = already assigned to item2
     * 
     * Booking1: |---------|
     * Booking2:           |---------|
     * Booking3: |-------------------|
     * Booking4: |-------------------|
     * 
     * Assigned to the first available room.
     */
    @Test
    public void assignBookings_7() {
        ArrayList<Booking> bookings = new ArrayList();
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 09:00", null));
        bookings.add(getBooking("2015-01-05 09:00", "2015-01-05 10:00", null));
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 10:00", null));
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 10:00", null));
        
        runTest(bookings);
        
        Assert.assertEquals(item1.id, bookings.get(0).bookingItemId); 
        Assert.assertEquals(item1.id, bookings.get(1).bookingItemId); 
        Assert.assertEquals(item2.id, bookings.get(2).bookingItemId); 
        Assert.assertEquals(item3.id, bookings.get(3).bookingItemId); 
    }
    
    /**
     * Testing that this simple case is assigned
     * 
     * x = already assigned to item1
     * y = already assigned to item2
     * 
     * Booking1: |----x----|
     * Booking2:           |----y----|
     * Booking3: |-------------------|
     * Booking4: |-------------------|
     * 
     * Assigned to the first available room.
     */
    @Test(expected = BookingEngineException.class)
    public void assignBookings_8() {
        ArrayList<Booking> bookings = new ArrayList();
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 09:00", item1));
        bookings.add(getBooking("2015-01-05 09:00", "2015-01-05 10:00", item2));
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 10:00", null));
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 10:00", null));
        
        runTest(bookings);
        
        Assert.assertEquals(item1.id, bookings.get(0).bookingItemId); 
        Assert.assertEquals(item1.id, bookings.get(1).bookingItemId); 
        Assert.assertEquals(item2.id, bookings.get(2).bookingItemId); 
        Assert.assertEquals(item3.id, bookings.get(3).bookingItemId); 
    }
    
    /**
     * Testing that this simple case is assigned
     * 
     * x = already assigned to item1
     * y = already assigned to item2
     * 
     * Booking1: |----------|
     * Booking2:            |----y----|
     * Booking3:   |----------------|
     * Booking4: |----------------|
     * 
     * Assigned to the first available room.
     */
    @Test
    public void assignBookings_6() {
        ArrayList<Booking> bookings = new ArrayList();
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 09:00", null));
        bookings.add(getBooking("2015-01-05 09:00", "2015-01-05 10:00", item2));
        bookings.add(getBooking("2015-01-05 08:30", "2015-01-05 09:30", null));
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 09:30", null));
        
        runTest(bookings);
        
        Assert.assertEquals(item2.id, bookings.get(0).bookingItemId); 
        Assert.assertEquals(item2.id, bookings.get(1).bookingItemId); 
        Assert.assertEquals(item3.id, bookings.get(2).bookingItemId); 
        Assert.assertEquals(item1.id, bookings.get(3).bookingItemId); 
    }
    
    /**
     * Testing that this simple case is assigned
     * 
     * x = already assigned to item1
     * y = already assigned to item2
     * 
     * Booking1: |----x-----|
     * Booking2:            |----x----|
     * Booking3:                      |----x----|
     * Booking4:                           |----x----|
     * 
     * Expected:
     *           |------------------------------|
     *                                     |---------|
     * Assigned to the first available room.
     */
    @Test(expected = BookingEngineException.class)
    public void assignBookings_200() {
        ArrayList<Booking> bookings = new ArrayList();
        bookings.add(getBooking("2015-01-05 08:00", "2015-01-05 09:00", item1));
        bookings.add(getBooking("2015-01-05 09:00", "2015-01-05 10:00", item1));
        bookings.add(getBooking("2015-01-05 10:00", "2015-01-05 11:00", item1));
        bookings.add(getBooking("2015-01-05 10:30", "2015-01-05 12:30", item1));
        
        runTest(bookings);
    }
    
    public Booking getBooking(String start, String end, BookingItem item) {
        Booking booking = new Booking();
        booking.startDate = getDate(start);
        booking.endDate = getDate(end);
        if (item != null) {
            booking.bookingItemId = item.id;
        }
        
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

    private BookingItem getBookingItem() {
        BookingItem item = new BookingItem();
        item.bookingSize = 1;
        item.id = UUID.randomUUID().toString();
        item.bookingItemTypeId  = type1.id;
        return item;
    }

    private void runTest(ArrayList<Booking> unassignedBookings) {
        List<BookingItem> items = new ArrayList();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        
        BookingItemAssignerOptimal assigner = new BookingItemAssignerOptimal(type1, unassignedBookings, items);
        assigner.assign();
    }
    
    private List<String> getAvailableResult(ArrayList<Booking> unassignedBookings) {
        List<BookingItem> items = new ArrayList();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        
        BookingItemAssignerOptimal assigner = new BookingItemAssignerOptimal(type1, unassignedBookings, items);
        return assigner.getAvailableItems();
    }
}
