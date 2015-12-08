/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine;

import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.BookingEngineException;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class BookingEngineVerifier {
    public void checkIfBookingItemIdExists(Booking booking, Map<String, BookingItemType> types) {
        BookingItemType type = types.get(booking.bookingItemTypeId);
        if (type == null) {
            throw new BookingEngineException("Booking must have a valid booking item type");
        }
    }

    
    void throwExceptionIfBookingItemIdMissing(Booking booking, Map<String, BookingItem> items) {
        String errorMessage = "The booking item id is missing, itemid is required when configuration on the bookingengine config require is false";
        
        if (booking.bookingItemId == null || items.get(booking.bookingItemId) == null) {
            throw new BookingEngineException(errorMessage);
        }
    }
}
