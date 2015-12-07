package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsManager extends GetShopSessionBeanNamed implements IPmsManager {

    @Autowired
    BookingEngine bookingEngine;
    
    @Override
    public Integer addRoom(String name) throws Exception {
        List<BookingItemType> types = bookingEngine.getBookingItemTypes();
        for(BookingItemType type : types) {
            if(type.name.equalsIgnoreCase(name)) {
                return -1;
            }
        }
        
        bookingEngine.createABookingItemType(name);
        return 0;
    }

    @Override
    public List<BookingItemType> getAllRooms() throws Exception {
        return bookingEngine.getBookingItemTypes();
    }
    
}
