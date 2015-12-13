/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.bookingengine;

import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.BookingEngineAbstract;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.BookingEngineException;
import com.thundashop.core.databasemanager.data.Credentials;
import java.util.UUID;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.*;
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
public class BookingEngineTest extends TestCommon {
   
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
     * We need to make sure that we can create a BookingEngineType and 
     * that its stored to the database.
     * 
     *  - Creating a booking item.
     *  - Checking that its stored to database
     *  - Making sure that its retrieveable
     */
    @Test 
    public void testCreateBookingItemType() {
        BookingItemType type = bookingEngine.createABookingItemType("Booking Item Test");
        BookingItemType type2 = bookingEngine.getBookingItemType(type.id);
        
        verify(databaseSaver, times(1)).saveObject(any(BookingItemType.class), any(Credentials.class));
        assertNotNull(type2);
    }
    
    @Test(expected = BookingEngineException.class)
    public void throwExecptionIfUpdatingANoneExsistingType() {
        BookingItemType type = new BookingItemType();
        type.id = UUID.randomUUID().toString();
        bookingEngine.updateBookingItemType(type);
    }
    
    @Test
    public void canUpdateBookingItemType() {
        BookingItemType type = bookingEngine.createABookingItemType("Booking Item Test");
        type.name = "New Booking Item Name";
        
        reset(databaseSaver);
        bookingEngine.updateBookingItemType(type);
        String newName = bookingEngine.getBookingItemType(type.id).name;
        verify(databaseSaver, times(1)).saveObject(any(BookingItemType.class), any(Credentials.class));
        
        Assert.assertEquals("New Booking Item Name", newName);
    }
    
    /**
     * We need to make sure that we can create a BookingItem and
     * that its stored to the database
     */
    @Test
    public void testSaveBookingItem() {
        BookingItemType type = bookingEngine.createABookingItemType("Booking Item Test");
        BookingItem item = helper.createAValidBookingItem(type.id);
        BookingItem savedItem = bookingEngine.saveBookingItem(item);
        verify(databaseSaver, times(2)).saveObject(any(BookingItem.class), any(Credentials.class));
        
        BookingItem item2 = bookingEngine.getBookingItem(item.id);
        assertNotNull(item2);
    }
  
    /**
     * Making sure that bookingitems can not be saved if itemtype does not exists
     */
    @Test(expected = BookingEngineException.class)
    public void testThrowsExceptionIfBookingItemTypeDoesNotExists() {
        BookingItem item = helper.createAValidBookingItem("item_id_does_not_exists");
        bookingEngine.saveBookingItem(item);
    }
    
    /**
     * Testing that the BookingItem is retreived from database and stored in memory
     */
    @Test
    public void testUsingDataFromDatabase() {
        BookingItem item = new BookingItem();
        item.id = "TESTME";
        feedDataFromDatabase(abstractEngine, item);
        
        Assert.assertEquals(item.id, bookingEngine.getBookingItem("TESTME").id);
    }
    
    /**
     * Testing that the BookingItemType is retreived from database and stored in memory
     */
    @Test
    public void testUsingDataFromDatabaseBookingItemType() {
        BookingItemType itemType = new BookingItemType();
        itemType.id = "TESTME";
        feedDataFromDatabase(abstractEngine, itemType);
        
        Assert.assertEquals(itemType.id, bookingEngine.getBookingItemType("TESTME").id);
    }  
    
}