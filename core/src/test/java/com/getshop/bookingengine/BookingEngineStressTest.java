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
import com.thundashop.core.databasemanager.data.Credentials;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author ktonder
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/All.xml")
public class BookingEngineStressTest extends TestCommon {

    @InjectMocks
    @Spy
    BookingEngineAbstract abstractEngine;

    @InjectMocks
    BookingEngine bookingEngine;
    private BookingItemType type1;
    
    @Before
    public void setup() {
        abstractEngine.setCredentials(new Credentials(BookingEngine.class));
        bookingEngine.setCredentials(new Credentials(BookingEngine.class));
        bookingEngine.setConfirmationRequired(true);
        
        type1 = bookingEngine.createABookingItemType("type1");
        createBookintItem(type1);
    }   
    
    
    /* Test that the booking engine is fast enough 
     *
     * Adding 5000 bookings to the engine.
     *
     */
    @Test(timeout = 5000)
    public void test() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        for (int i=0;i<5000;i++) {
            
            Date start = cal.getTime();
            cal.add(Calendar.DAY_OF_YEAR, 1);
            Date endDate = cal.getTime();
            Booking booking = getBooking(start, endDate, type1, null);
            ArrayList<Booking> bookings = new ArrayList();
            bookings.add(booking);
            bookingEngine.addBookings(bookings);
        }
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
