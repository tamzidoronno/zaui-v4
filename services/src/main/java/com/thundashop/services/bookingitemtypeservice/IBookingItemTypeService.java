package com.thundashop.services.bookingitemtypeservice;

import java.util.List;

import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.repository.utils.SessionInfo;

public interface IBookingItemTypeService {
    List<String> getBookingItemTypesIds(SessionInfo sessionInfo);    
    List<BookingItemType> getAllBookingItemTypes(SessionInfo sessionInfo);    
    BookingItemType getBookingItemTypeById(String id, SessionInfo sessionInfo);    
}
