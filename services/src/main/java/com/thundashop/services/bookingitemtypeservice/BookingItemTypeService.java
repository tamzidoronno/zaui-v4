package com.thundashop.services.bookingitemtypeservice;

import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.BookingEngineException;
import com.thundashop.repository.bookingitemtyperepository.IBookingItemTypeRepository;
import com.thundashop.repository.utils.SessionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BookingItemTypeService implements IBookingItemTypeService {
    private final IBookingItemTypeRepository bookingItemTypeRepository;
    private final Map<String, BookingItemType> types = new HashMap<>();


    @Autowired
    public BookingItemTypeService(IBookingItemTypeRepository bookingItemTypeRepository){
        this.bookingItemTypeRepository = bookingItemTypeRepository;
    }

    @Override
    public List<String> getBookingItemTypeIds(SessionInfo sessionInfo) {
        if(types.size() < 1){
            this.getAllBookingItemTypes(sessionInfo);
        }
        return new ArrayList<>(types.keySet());
    }

    @Override
    public BookingItemType getBookingItemTypeById(String id, SessionInfo sessionInfo) {
        if(types.size() > 0 && types.containsKey(id) && types.get(id) != null){
            return types.get(id);
        }
        return bookingItemTypeRepository.getById(id, sessionInfo);
    }

    @Override
    public BookingItemType updateBookingItemType(BookingItemType type, SessionInfo sessionInfo) {
        BookingItemType savedItem = this.getBookingItemTypeById(type.id, sessionInfo);

        if (savedItem == null) {
            if (type.id != null && !type.id.isEmpty()) {
                bookingItemTypeRepository.save(type, sessionInfo);
                savedItem = type;
            } else {
                throw new BookingEngineException("Could not update itemType, it does not exists. Use createBookingItemType to make a new one");
            }
        }

        savedItem.size = type.size;
        savedItem.name = type.name;
        savedItem.pageId = type.pageId;
        savedItem.productId = type.productId;
        savedItem.visibleForBooking = type.visibleForBooking;
        savedItem.autoConfirm = type.autoConfirm;
        savedItem.addon = type.addon;
        savedItem.group = type.group;
        savedItem.rules = type.rules;
        savedItem.order = type.order;
        savedItem.capacity = type.capacity;
        savedItem.orderAvailability = type.orderAvailability;
        savedItem.nameTranslations = type.nameTranslations;
        savedItem.descriptionTranslations = type.descriptionTranslations;
        savedItem.description = type.description;
        savedItem.eventItemGroup = type.eventItemGroup;
        savedItem.minStay = type.minStay;
        savedItem.systemCategory = type.systemCategory;
        savedItem.historicalProductIds = type.historicalProductIds;
        savedItem.setTranslationStrings(type.getTranslations());
        bookingItemTypeRepository.save(savedItem, sessionInfo);
        types.put(savedItem.id, savedItem);
        return savedItem;
    }

    @Override
    public void saveBookingItemType(BookingItemType type, SessionInfo sessionInfo) {
        bookingItemTypeRepository.save(type, sessionInfo);
        types.put(type.id, type);
    }


    @Override
    public List<BookingItemType> getAllBookingItemTypes(SessionInfo sessionInfo) {
        List<BookingItemType> bookingItemTypeList = bookingItemTypeRepository.getAll(sessionInfo);
        if(types.size() < 1){
            for(BookingItemType type: bookingItemTypeList){
                types.put(type.id, type);
            }
        }
        return bookingItemTypeList;
    }

    @Override
    public boolean deleteBookingItemType(BookingItemType data, SessionInfo sessionInfo) {
        if(data.deepFreeze){
            return false;
        }
        if (sessionInfo != null && sessionInfo.getCurrentUserId() != null) {
            data.gsDeletedBy = sessionInfo.getCurrentUserId();
        }
        data.deleted = new Date();
        bookingItemTypeRepository.save(data, sessionInfo);
        types.clear();
        return true;
    }
    
}
