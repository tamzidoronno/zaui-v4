package com.thundashop.repository.bookingitemtyperepository;

import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.repository.baserepository.IRepository;
import com.thundashop.repository.utils.SessionInfo;

public interface IBookingItemTypeRepository extends IRepository<BookingItemType> {
    BookingItemType getById(String id, SessionInfo sessionInfo);    
}
