package com.thundashop.services.bookingitemservice;

import java.util.List;

import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.repository.utils.SessionInfo;

public interface IBookingItemService {
    List<BookingItem> getAllBookingItems(SessionInfo sessionInfo);    
    BookingItem getBookingItemById(String id, SessionInfo sessionInfo); 
    
}
