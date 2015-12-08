package com.thundashop.core.bookingengine;

import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import java.util.List;


/**
 * Booking engine management system.<br>
 */

@GetShopApi
@GetShopMultiLayerSession
public interface IBookingEngine {
    @Administrator
    public BookingItemType createABookingItemType(String name);
    
    @Administrator
    public List<BookingItemType> getBookingItemTypes();
    
    @Administrator
    public void deleteABookingItemType(String id);
    
    @Administrator
    public BookingItemType getABookingItemType(String id);
    
    @Administrator
    public BookingItemType saveABookingItemType(BookingItemType type);
    
    @Administrator
    public BookingItem saveBookingItem(BookingItem item);
    
    @Administrator
    public BookingItem getBookingItem(String id);
    
    @Administrator
    public List<BookingItem> getBookingItems();
    
    @Administrator
    public List<BookingItem> deleteBookingItem(String id);
}
