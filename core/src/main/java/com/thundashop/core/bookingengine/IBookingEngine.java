package com.thundashop.core.bookingengine;

import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingEngineConfiguration;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import java.util.Date;
import java.util.List;


/**
 * Booking engine management system.<br>
 */

@GetShopApi
@GetShopMultiLayerSession
public interface IBookingEngine {
    @Administrator
    public BookingItemType createABookingItemType(String name);
    
    public List<BookingItemType> getBookingItemTypes();
    
    @Administrator
    public void deleteBookingItemType(String id);
    
    @Administrator
    public BookingItemType getBookingItemType(String id);
    
    @Administrator
    public BookingItemType updateBookingItemType(BookingItemType type);
    
    @Administrator
    public BookingItem saveBookingItem(BookingItem item);
    
    @Administrator
    public BookingItem getBookingItem(String id);
    
    @Administrator
    public List<BookingItem> getBookingItems();
    
    @Administrator
    public void deleteBookingItem(String id);
    
    @Administrator
    public void setConfirmationRequired(boolean confirmationRequired);
    
    @Administrator
    public BookingEngineConfiguration getConfig();
    
    @Administrator
    public List<Booking> getAllBookings();
    
    @Administrator
    public void changeTypeOnBooking(String bookingId, String itemTypeId);
    
    @Administrator
    public void changeDatesOnBooking(String bookingId, Date start, Date end);
    
    @Administrator
    public void changeBookingItemOnBooking(String booking, String item);
}
