package com.thundashop.services.bookingitemtypeservice;

import java.util.List;
import java.util.stream.Collectors;

import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.repository.bookingitemtyperepository.IBookingItemTypeRepository;
import com.thundashop.repository.utils.SessionInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingItemTypeService implements IBookingItemTypeService {

    private final IBookingItemTypeRepository bookingItemTypeRepository;

    @Autowired
    public BookingItemTypeService(IBookingItemTypeRepository bookingItemTypeRepository){
        this.bookingItemTypeRepository = bookingItemTypeRepository;
    }

    @Override
    public List<String> getBookingItemTypesIds(SessionInfo sessionInfo) {
        List<BookingItemType> allTypes = bookingItemTypeRepository.getAll(sessionInfo);
        return allTypes
                .stream()
                .map(type -> type.id)
                .collect(Collectors.toList());
    }

    @Override
    public BookingItemType getBookingItemTypeById(String id, SessionInfo sessionInfo) {        
        return bookingItemTypeRepository.getById(id, sessionInfo);
    }

    @Override
    public List<BookingItemType> getAllBookingItemTypes(SessionInfo sessionInfo) {
        return bookingItemTypeRepository.getAll(sessionInfo);
    }
    
}
