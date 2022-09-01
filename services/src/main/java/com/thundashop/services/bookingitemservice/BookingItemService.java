package com.thundashop.services.bookingitemservice;

import java.util.List;

import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.repository.bookingitemrepository.IBookingItemRepository;
import com.thundashop.repository.utils.SessionInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingItemService implements IBookingItemService {
    private final IBookingItemRepository bookingItemRepository;

    @Autowired
    public BookingItemService(IBookingItemRepository bookingItemTypeRepository){
        this.bookingItemRepository = bookingItemTypeRepository;
    }

    @Override
    public List<BookingItem> getAllBookingItems(SessionInfo sessionInfo) {        
        return bookingItemRepository.getAll(sessionInfo);
    }

    @Override
    public BookingItem getBookingItemById(String id, SessionInfo sessionInfo) {       
        return bookingItemRepository.getById(id, sessionInfo);
    }
    
}
