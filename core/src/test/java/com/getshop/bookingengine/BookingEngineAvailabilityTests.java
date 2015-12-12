/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.bookingengine;

import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.BookingEngineAbstract;
import com.thundashop.core.bookingengine.data.Availability;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.common.BookingEngineException;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.Credentials;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
public class BookingEngineAvailabilityTests extends TestCommon {
    @InjectMocks
    @Spy
    BookingEngineAbstract abstractEngine;

    @InjectMocks
    BookingEngine bookingEngine;
    
    @Autowired
    private BookingEngineTestHelper helper;
    
    @After
    public void setup() {
        abstractEngine.setCredentials(new Credentials(BookingEngine.class));
        bookingEngine.setCredentials(new Credentials(BookingEngine.class));
    }
    
    /** 
     * Test add availability
     */
    @Test
    public void addAvailability() {
        BookingItem item = helper.createAValidBookingItemAndResetDatabaseSaver(bookingEngine, databaseSaver);
        helper.getAValidAvailability(item, bookingEngine);
        
        item = bookingEngine.getBookingItem(item.id);
        assertEquals(1, item.availabilitieIds.size());
        assertEquals(1, item.availabilities.size());
        verify(databaseSaver, times(2)).saveObject(any(DataCommon.class), any(Credentials.class));
    }
    
    
    /**
     * Checks that an availability is not added if the bookingitem does not exists.
     */
    @Test(expected = BookingEngineException.class)
    public void testAvailabilityThrowsExceptionIfItemDoesNotExsits() {
        Availability availability = new Availability();
        bookingEngine.addAvailability("doest not extists", availability);
    }
    
    /**
     * Checks that a availability without dates is not allowed. 
     */
    @Test(expected = BookingEngineException.class)
    public void failsIfDatesNotSpecifiedInAvailbility() {
        BookingItem item = helper.createAValidBookingItemAndResetDatabaseSaver(bookingEngine, databaseSaver);
        Availability availability = helper.getAValidAvailability(item, bookingEngine);
        availability.startDate = null;
        availability.endDate = null;
        
        bookingEngine.addAvailability(item.id, availability);
    }
    
    /**
     * Availibilites can not overlap within a bookingitem, this ensures that the
     * avaiblility that overlaps in the beginning of another availability is stopped
     * 
     * Availbility 1: 08:00 - 09:00
     * Availbility 2: 07:30 - 08:30
     * 
     * @throws ParseException 
     */
    @Test(expected = BookingEngineException.class)
    public void testThrowsExceptionIfBookingAvailiblityOverlapsInBeginning() throws ParseException {
        BookingItem item = helper.createAValidBookingItemAndResetDatabaseSaver(bookingEngine, databaseSaver);
        Availability availability = helper.getAValidAvailability(item, bookingEngine);
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Availability availability2 = helper.getAValidAvailability(item, bookingEngine);
        availability2.startDate = formatter.parse("2014-01-01 07:30");
        availability2.endDate = formatter.parse("2014-01-01 08:30");;

        bookingEngine.addAvailability(item.id, availability);
        bookingEngine.addAvailability(item.id, availability2);
    }
    
    /**
     * Availibilites can not overlap within a bookingitem, this ensures that the
     * avaiblility that overlaps in the end of another availability is stopped
     * 
     * Availbility 1: 08:00 - 09:00
     * Availbility 2: 08:30 - 09:30
     * 
     * @throws ParseException 
     */
    @Test(expected = BookingEngineException.class)
    public void testThrowsExceptionIfBookingAvailiblityOverlapsInEnd() throws ParseException {
        BookingItem item = helper.createAValidBookingItemAndResetDatabaseSaver(bookingEngine, databaseSaver);
        Availability availability = helper.getAValidAvailability(item, bookingEngine);
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Availability availability2 = new Availability();
        availability2.startDate = formatter.parse("2014-01-01 08:30");
        availability2.endDate = formatter.parse("2014-01-01 09:30");;

        bookingEngine.addAvailability(item.id, availability);
        bookingEngine.addAvailability(item.id, availability2);
    }
    
    
    /**
     * Availibilites can not overlap within a bookingitem, this ensures that the
     * avaiblility that overlaps a whole periode
     * 
     * Availbility 1: 08:00 - 09:00
     * Availbility 2: 07:00 - 10:00
     * 
     * @throws ParseException 
     */
    @Test(expected = BookingEngineException.class)
    public void testThrowsExceptionIfBookingAvailiblityOverall() throws ParseException {
        BookingItem item = helper.createAValidBookingItemAndResetDatabaseSaver(bookingEngine, databaseSaver);
        Availability availability = helper.getAValidAvailability(item, bookingEngine);
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Availability availability2 = new Availability();
        availability2.startDate = formatter.parse("2014-01-01 07:00");
        availability2.endDate = formatter.parse("2014-01-01 10:00");;

        bookingEngine.addAvailability(item.id, availability);
        bookingEngine.addAvailability(item.id, availability2);
    }
   
    /**
     * Availibilites can not overlap within a bookingitem, this ensures that the
     * avaiblility that overlaps exactly the same time periode
     * 
     * Availbility 1: 08:00 - 09:00
     * Availbility 2: 08:00 - 09:00
     * 
     * @throws ParseException 
     */
    @Test(expected = BookingEngineException.class)
    public void testThrowsExceptionIfBookingAvailiblityExact() throws ParseException {
        BookingItem item = helper.createAValidBookingItemAndResetDatabaseSaver(bookingEngine, databaseSaver);
        helper.getAValidAvailability(item, bookingEngine);
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Availability availability2 = new Availability();
        availability2.startDate = formatter.parse("2014-01-01 08:00");
        availability2.endDate = formatter.parse("2014-01-01 09:00");;

        bookingEngine.addAvailability(item.id, availability2);
    }
   
    /**
     * Making sure that that we can add availabilites after eachoter and in between of two other avaiabilites.
     * 
     * Availbility 1: 08:00 - 09:00
     * Availbility 2: 08:30 - 09:30
     * 
     * @throws ParseException 
     */
    @Test
    public void addThreeAvailbilitesInARow() throws ParseException {
        BookingItem item = helper.createAValidBookingItemAndResetDatabaseSaver(bookingEngine, databaseSaver);
        helper.getAValidAvailability(item, bookingEngine);
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Availability availability2 = new Availability();
        availability2.startDate = formatter.parse("2014-01-01 11:00");
        availability2.endDate = formatter.parse("2014-01-01 12:00");;

        Availability availbility3 = new Availability();
        availbility3.startDate = formatter.parse("2014-01-01 10:00");
        availbility3.endDate = formatter.parse("2014-01-01 11:00");;

        bookingEngine.addAvailability(item.id, availability2);
        bookingEngine.addAvailability(item.id, availbility3);
    }
    
    @Test
    public void testAvailbilityFromDatabaseUsed() {
        Availability availability = new Availability();
        availability.id = "TESTME";
        feedDataFromDatabase(bookingEngine, availability);
        
        Availability fromDb = bookingEngine.getAvailbility("TESTME");
        
        assertEquals(availability.id, fromDb.id);
    }
    
}
