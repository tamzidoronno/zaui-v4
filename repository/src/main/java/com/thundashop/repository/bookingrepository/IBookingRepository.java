package com.thundashop.repository.bookingrepository;

import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.repository.baserepository.IRepository;
import com.thundashop.repository.utils.SessionInfo;

public interface IBookingRepository extends IRepository<Booking>{
    Booking getById(String id, SessionInfo sessionInfo);    
}
