package com.thundashop.services.bookingitemtypeservice;

import java.util.List;

import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.repository.utils.SessionInfo;

public interface IBookingItemTypeService {
    List<String> getBookingItemTypeIds(SessionInfo sessionInfo);
    List<BookingItemType> getAllBookingItemTypes(SessionInfo sessionInfo);    
    BookingItemType getBookingItemTypeById(String id, SessionInfo sessionInfo);
    BookingItemType updateBookingItemType(BookingItemType type, SessionInfo sessionInfo);
    void saveBookingItemType(BookingItemType type, SessionInfo sessionInfo);
    boolean deleteBookingItemType(BookingItemType data, SessionInfo sessionInfo);
}
