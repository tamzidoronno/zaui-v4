/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.bookingengine;

import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.BookingEngineAbstract;
import com.thundashop.core.bookingengine.data.BookingTimeLineFlatten;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingGroup;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.BookingEngineException;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.Credentials;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

/**
 *
 * @author ktonder
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BookingEngineTestContext.class})
public class BookingEngineBookingTests extends TestCommon {
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
    
    @Test
    public void addBooking() {
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine));
        bookings.add(helper.getValidBooking(2, bookingEngine));
        
        reset(database);
        BookingGroup bookingGroup = bookingEngine.addBookings(bookings);
        verify(database, times(5)).save(any(DataCommon.class), any(Credentials.class));
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
        
        setUserLoggedIn(abstractEngine);
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
        feedDataFromDatabase(abstractEngine, booking);
        booking = bookingEngine.getBooking(id);
        Assert.assertNotNull(booking);
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
        
        reset(database);
        bookingEngine.addBookings(bookings);
        verify(database, times(3)).save(any(DataCommon.class), any(Credentials.class));
        
        List<Booking> required = bookingEngine.getConfirmationList(bookings.get(0).bookingItemTypeId);
        Assert.assertEquals(1, required.size());
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
        
        reset(database);
        bookingEngine.addBookings(bookings);
        verify(database, times(3)).save(any(DataCommon.class), any(Credentials.class));
        
        List<Booking> required = bookingEngine.getConfirmationList(bookings.get(0).bookingItemTypeId);
        Assert.assertEquals(0, required.size());
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
    @Test(expected = BookingEngineException.class)
    public void addBooking_expectionThrownWhenFull() {
        List<Booking> bookings = new ArrayList();
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItem item = helper.createAValidBookingItem(type.id);
        item.bookingSize = 1;
        bookingEngine.saveBookingItem(item);
        
        bookings.add(helper.getValidBooking(1, bookingEngine, item));
        bookings.add(helper.getValidBooking(1, bookingEngine, item));
        
        bookingEngine.addBookings(bookings);
    }
    
    @Test
    public void testGetTimeLineFunction() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItem item = helper.createAValidBookingItem(type.id);
        item.bookingSize = 1;
        bookingEngine.saveBookingItem(item);
        
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine, item));
        bookings.add(helper.getValidBooking(2, bookingEngine, item));
        
        BookingGroup bookingGroup = bookingEngine.addBookings(bookings);
        BookingTimeLineFlatten timeLine = bookingEngine.getTimelines(type.id, bookings.get(0).startDate, bookings.get(1).endDate);
        
        Assert.assertEquals(2, timeLine.getTimelines().size());
    }
    
    @Test
    public void testCanAdd() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItem item = helper.createAValidBookingItem(type.id);
        item.bookingSize = 1;
        bookingEngine.saveBookingItem(item);
        
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine, item));
        bookings.add(helper.getValidBooking(2, bookingEngine, item));
        
        boolean canAdd = bookingEngine.canAdd(bookings);
        Assert.assertEquals(true, canAdd);
        
        bookings.add(helper.getValidBooking(2, bookingEngine, item));
        canAdd = bookingEngine.canAdd(bookings);
        Assert.assertEquals(false, canAdd);
    }
    
    @Test
    public void testDeleteBooking() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItem item = helper.createAValidBookingItem(type.id);
        bookingEngine.saveBookingItem(item);
        
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine, item));
        BookingGroup group = bookingEngine.addBookings(bookings);
        
        
        reset(database);
        bookingEngine.deleteBooking(group.bookingIds.get(0));
        verify(database, times(1)).delete(any(Booking.class), any(Credentials.class));
       
        Booking deletedBooking = bookingEngine.getBooking(group.bookingIds.get(0));
        Assert.assertNull(deletedBooking);
    }
    
    @Test(expected = BookingEngineException.class)
    public void testDeleteBookingItem_failsIfBookingNotExists() {
        bookingEngine.deleteBookingItem("not exists");
    }
   
    @Test
    public void testDeleteBookingItem() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItem item = helper.createAValidBookingItem(type.id);
        bookingEngine.saveBookingItem(item);
        
        reset(database);
        bookingEngine.deleteBookingItem(item.id);
        verify(database, times(1)).delete(any(Booking.class), any(Credentials.class));
    }
    
    @Test(expected = BookingEngineException.class)
    public void testDeleteBookingItem_ifBookingItemHasBookings() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItem item = helper.createAValidBookingItem(type.id);
        BookingItem savedItem = bookingEngine.saveBookingItem(item);
        
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine, item));
        BookingGroup group = bookingEngine.addBookings(bookings);
        
        bookingEngine.deleteBookingItem(savedItem.id);
    }
    
    @Test
    public void testChangeBookingType() {
        bookingEngine.setConfirmationRequired(true);
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItemType type2 = bookingEngine.createABookingItemType("Type2");
        
        BookingItem item = helper.createAValidBookingItem(type.id);
        BookingItem item2 = helper.createAValidBookingItem(type2.id);
        BookingItem savedItem = bookingEngine.saveBookingItem(item);
        BookingItem savedItem2 = bookingEngine.saveBookingItem(item2);
        
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine, savedItem));
        bookings.get(0).bookingItemId = null;
        bookings.get(0).bookingItemTypeId = type.id;
        
        BookingGroup bookingGroup = bookingEngine.addBookings(bookings);
        String bookingId = bookingGroup.bookingIds.get(0);
        
        reset(database);
        bookingEngine.changeTypeOnBooking(bookingId, type2.id);
        verify(database, times(1)).save(any(Booking.class), any(Credentials.class));
    }
    
    @Test(expected = BookingEngineException.class)
    public void testChangeBookingType_throwsException_bookingItemTypeExists() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItem item = helper.createAValidBookingItem(type.id);
        BookingItem savedItem = bookingEngine.saveBookingItem(item);
        
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine, savedItem));
        BookingGroup bookingGroup = bookingEngine.addBookings(bookings);
        
        bookingEngine.changeTypeOnBooking(bookingGroup.bookingIds.get(0), "not_exists");
    }
    
    @Test(expected = BookingEngineException.class)
    public void testChangeBookingType_throwsException_ifBookingItemAlreadySetToBooking() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItem item = helper.createAValidBookingItem(type.id);
        BookingItem savedItem = bookingEngine.saveBookingItem(item);
 
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine, savedItem));
 
        BookingItemType type2 = bookingEngine.createABookingItemType("Type2");
        BookingItem item2 = helper.createAValidBookingItem(type2.id);
        BookingItem savedItem2 = bookingEngine.saveBookingItem(item2);
        
        bookings.get(0).bookingItemTypeId = type.id;
        
        Assert.assertNotNull(bookings.get(0).bookingItemId);
        BookingGroup bookingGroup = bookingEngine.addBookings(bookings);
        String bookingId = bookingGroup.bookingIds.get(0);
        
        bookingEngine.changeTypeOnBooking(bookingId, type2.id);
    }
    
    public void testChangeBookingType_throwsException_bookingNotExists() {
        bookingEngine.changeTypeOnBooking("not exists", "not_exists");
    }
       
    @Test(expected = BookingEngineException.class)
    public void testChangeBookingType_throwsException_notAvailableSpots() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItem item = helper.createAValidBookingItem(type.id);
        BookingItem savedItem = bookingEngine.saveBookingItem(item);
        
        BookingItemType type2 = bookingEngine.createABookingItemType("Type2");
        BookingItem item2 = helper.createAValidBookingItem(type2.id);
        BookingItem savedItem2 = bookingEngine.saveBookingItem(item2);
        
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine, savedItem));
        bookings.add(helper.getValidBooking(1, bookingEngine, savedItem2));
        
        bookings.get(0).bookingItemTypeId = type.id;
        bookings.get(1).bookingItemTypeId = type2.id;
        
        BookingGroup bookingGroup = bookingEngine.addBookings(bookings);
        
        bookingEngine.changeTypeOnBooking(bookingGroup.bookingIds.get(1), type.id);
    }
    
    @Test
    public void testCanAddBookingsIfParalellByItemTypeIds() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItem item = helper.createAValidBookingItem(type.id);
        BookingItem savedItem = bookingEngine.saveBookingItem(item);
        
        BookingItemType type2 = bookingEngine.createABookingItemType("Type2");
        BookingItem item2 = helper.createAValidBookingItem(type2.id);
        BookingItem savedItem2 = bookingEngine.saveBookingItem(item2);
        
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine, savedItem));
        bookings.add(helper.getValidBooking(1, bookingEngine, savedItem2));
        
        bookings.get(0).bookingItemTypeId = type.id;
        bookings.get(1).bookingItemTypeId = type2.id;
        
        bookingEngine.addBookings(bookings);
    }
    
    @Test
    public void testChangeBookingDate() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItem item = helper.createAValidBookingItem(type.id);
        BookingItem savedItem = bookingEngine.saveBookingItem(item);
        
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine, savedItem));
        
        BookingGroup group = bookingEngine.addBookings(bookings);
        String bookingId = group.bookingIds.get(0);
        
        reset(database);
        bookingEngine.changeDatesOnBooking(bookingId, helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-03 08:00"));
        verify(database, times(1)).save(any(DataCommon.class), any(Credentials.class));
        
        Booking booking = bookingEngine.getBooking(bookingId);
        Assert.assertEquals(helper.getDate("2014-01-02 08:00"), booking.startDate);
        Assert.assertEquals(helper.getDate("2014-01-03 08:00"), booking.endDate);
    }
    
    @Test(expected = BookingEngineException.class)
    public void testChangeBookingDate_throwsExceptionIfFull() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItem item = helper.createAValidBookingItem(type.id);
        BookingItem savedItem = bookingEngine.saveBookingItem(item);
        
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine, savedItem));
        bookings.add(helper.getValidBooking(2, bookingEngine, savedItem));
        
        bookings.get(0).bookingItemTypeId = type.id;
        bookings.get(1).bookingItemTypeId = type.id;
        
        BookingGroup group = bookingEngine.addBookings(bookings);
        String bookingId = group.bookingIds.stream().filter(o -> bookingEngine.getBooking(o).startDate.equals(helper.getDate("2014-01-01 08:00"))).findFirst().get();
        
        bookingEngine.changeDatesOnBooking(bookingId, helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-03 08:00"));
    }
    
    @Test(expected = BookingEngineException.class)
    public void testChangeBookingDate_throwsIfBookingDontExists() {
        bookingEngine.changeDatesOnBooking("dont_exists", helper.getDate("2014-01-02 08:00"), helper.getDate("2014-01-02 08:00"));
    }
    
    @Test 
    public void testChangeBookingItemOnBooking() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItem item = helper.createAValidBookingItem(type.id);
        BookingItem item2 = helper.createAValidBookingItem(type.id);
        BookingItem savedItem = bookingEngine.saveBookingItem(item);
        BookingItem savedItem2 = bookingEngine.saveBookingItem(item2);
        
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine, savedItem));
        BookingGroup bookingGroup = bookingEngine.addBookings(bookings);
        
        reset(database);
        bookingEngine.changeBookingItemOnBooking(bookingGroup.bookingIds.get(0), savedItem2.id);
        verify(database, times(1)).save(any(DataCommon.class), any(Credentials.class));
        
        Booking savedBooking = bookingEngine.getBooking(bookingGroup.bookingIds.get(0));
        Assert.assertEquals(savedItem2.id, savedBooking.bookingItemId);
    }
    
    @Test
    public void testChangeBookingItemOnBooking_automaticallyChangesBookingItemTypeIfChangedToADifferentBookingItemType() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItemType type2 = bookingEngine.createABookingItemType("Type2");
        
        BookingItem item = helper.createAValidBookingItem(type.id);
        BookingItem item2 = helper.createAValidBookingItem(type2.id);
        BookingItem savedItem = bookingEngine.saveBookingItem(item);
        BookingItem savedItem2 = bookingEngine.saveBookingItem(item2);
        
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine, savedItem));
        bookings.get(0).bookingItemTypeId = type.id;
        BookingGroup bookingGroup = bookingEngine.addBookings(bookings);
        
        bookingEngine.changeBookingItemOnBooking(bookingGroup.bookingIds.get(0), savedItem2.id);
        
        Booking savedBooking = bookingEngine.getBooking(bookingGroup.bookingIds.get(0));
        Assert.assertEquals(savedItem2.id, savedBooking.bookingItemId);
        Assert.assertEquals(type2.id, savedBooking.bookingItemTypeId);
    }
    
  
    @Test(expected = BookingEngineException.class)
    public void testChangeBookingItemOnBooking_throwsexception_bookingDoesNotExists() {
        bookingEngine.changeBookingItemOnBooking("dost not exist", "does not exists");
    }

    @Test(expected = BookingEngineException.class)
    public void testChangeBookingItemOnBooking_throwsexception_bookingItemIdDoesNotExists() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItem item = helper.createAValidBookingItem(type.id);
        BookingItem savedItem = bookingEngine.saveBookingItem(item);
        
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine, savedItem));
        BookingGroup bookingGroup = bookingEngine.addBookings(bookings);
        
        bookingEngine.changeBookingItemOnBooking(bookingGroup.bookingIds.get(0), "does not exists");
    }
    
    @Test(expected = BookingEngineException.class)
    public void testChangeBookingItemOnBooking_ThrowsExceptionIfMovedToFull() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItemType type2 = bookingEngine.createABookingItemType("Type2");
        BookingItem item = helper.createAValidBookingItem(type.id);
        BookingItem item2 = helper.createAValidBookingItem(type2.id);
        BookingItem savedItem = bookingEngine.saveBookingItem(item);
        BookingItem savedItem2 = bookingEngine.saveBookingItem(item2);
        
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine, savedItem));
        bookings.add(helper.getValidBooking(1, bookingEngine, savedItem2));
        
        bookings.get(0).bookingItemTypeId = type.id;
        bookings.get(1).bookingItemTypeId = type2.id;
        BookingGroup bookingGroup = bookingEngine.addBookings(bookings);

        String bookingId = bookingGroup.bookingIds.stream().filter(o -> bookingEngine.getBooking(o).bookingItemTypeId.equals(type.id)).findFirst().get();
        bookingEngine.changeBookingItemOnBooking(bookingId, savedItem2.id);
    }
    
    @Test
    public void testDeleteBookingItemType() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        
        reset(database);
        bookingEngine.deleteBookingItemType(type.id);
        verify(database, times(1)).delete(any(BookingItemType.class), any(Credentials.class));
        
        BookingItemType bookingType = bookingEngine.getBookingItemType(type.id);
        Assert.assertNull(bookingType);
    }
    
    @Test(expected = BookingEngineException.class)
    public void testDeleteBookingItemType_typeAlreadyHasBookingItems() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItem item = helper.createAValidBookingItem(type.id);
        BookingItem savedItem = bookingEngine.saveBookingItem(item);
        
        bookingEngine.deleteBookingItemType(type.id);
    }
    
    @Test
    public void testGetAvailableItemsReturnsAsnwer() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItem item = helper.createAValidBookingItem(type.id);
        bookingEngine.saveBookingItem(item);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date now = cal.getTime();
        
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date toMorrow = cal.getTime();
        
        List<BookingItem> items = bookingEngine.getAvailbleItems(type.id, now, toMorrow);
        Assert.assertEquals(1, items.size());
    }
    
    @Test
    public void testChangeBookingItemTypeOnBookingItem() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItemType type2 = bookingEngine.createABookingItemType("Type2");
        
        BookingItem item = helper.createAValidBookingItem(type.id);
        bookingEngine.saveBookingItem(item);
        
        List<Booking> bookings = new ArrayList();
        bookings.add(helper.getValidBooking(1, bookingEngine, item));
        
        BookingGroup savedBookings = bookingEngine.addBookings(bookings);
        
        item.bookingItemTypeId = type2.id;
        bookingEngine.saveBookingItem(item);
        
        Booking savedBooking = bookingEngine.getBooking(savedBookings.bookingIds.get(0));
        
        Assert.assertEquals(type2.id, savedBooking.bookingItemTypeId);
    }
    
    @Test
    public void testGetAvailableSpotsWhenNoBookingsAdded() {
        BookingItemType type = bookingEngine.createABookingItemType("Type");
        BookingItem item = helper.createAValidBookingItem(type.id);
        bookingEngine.saveBookingItem(item);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date now = cal.getTime();
        
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date toMorrow = cal.getTime();
        
        int avil = bookingEngine.getNumberOfAvailable(type.id, now, toMorrow);
        Assert.assertEquals(1, avil);
        
    }
}