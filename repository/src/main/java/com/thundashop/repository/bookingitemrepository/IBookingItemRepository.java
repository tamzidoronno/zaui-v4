package com.thundashop.repository.bookingitemrepository;

import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.repository.baserepository.IRepository;
import com.thundashop.repository.utils.SessionInfo;

public interface IBookingItemRepository extends IRepository<BookingItem>{
    BookingItem getById(String id, SessionInfo sessionInfo);     
}
