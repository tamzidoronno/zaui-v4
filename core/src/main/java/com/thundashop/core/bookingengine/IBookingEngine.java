package com.thundashop.core.bookingengine;

import com.thundashop.core.bookingengine.data.BookingTimeLineFlatten;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingEngineConfiguration;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.RegistrationRules;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.pmsmanager.TimeRepeaterData;
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
    public BookingTimeLineFlatten getTimelines(String id, Date startDate, Date endDate);
    
    @Administrator
    public BookingItemType updateBookingItemType(BookingItemType type);
    
    @Administrator
    public BookingItem saveBookingItem(BookingItem item);
    
    @Administrator
    public BookingItem getBookingItem(String id);
    
    public List<BookingItem> getBookingItems();
    
    @Administrator
    public void deleteBookingItem(String id);
    
    @Administrator
    public void setConfirmationRequired(boolean confirmationRequired);
    
    @Administrator
    public BookingEngineConfiguration getConfig();
    
    @Administrator
    public List<Booking> getAllBookings();
    
    public Integer getNumberOfAvailable(String itemType, Date start, Date end);
    
    @Administrator
    public void changeTypeOnBooking(String bookingId, String itemTypeId);
    
    @Administrator
    public void changeDatesOnBooking(String bookingId, Date start, Date end);
    
    @Administrator
    public void changeBookingItemOnBooking(String booking, String item);
    
    public RegistrationRules getDefaultRegistrationRules();
    
    @Administrator
    public void saveDefaultRegistrationRules(RegistrationRules rules);
    
    @Administrator
    public void saveOpeningHours(TimeRepeaterData time, String itemId);
    
    @Administrator
    public void deleteOpeningHours(String repeaterId);
    
    @Editor
    List<BookingItem> getAllAvailbleItems(Date start, Date end);
    
    @Administrator
    public List<BookingItem> getAvailbleItems(String typeId, Date start, Date end);    
    
    @Administrator
    public List<BookingItem> getAvailbleItemsWithBookingConsidered(String typeId, Date start, Date end, String bookingId);
    
    @Administrator
    public List<BookingItem> getAllAvailbleItemsWithBookingConsidered(Date start, Date end, String bookingid);
    
    public List<TimeRepeaterData> getOpeningHours(String itemId);
    
    @Administrator
    public List<Booking> getAllBookingsByBookingItem(String bookingItemId);
    
    public void checkConsistency();
}
