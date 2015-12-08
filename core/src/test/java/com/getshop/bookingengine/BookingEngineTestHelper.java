/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.bookingengine;

import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.Availability;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DatabaseSaver;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.mockito.Mockito.reset;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class BookingEngineTestHelper {
    
    public BookingItem createAValidBookingItem(String bookingItemTypeId) {
        BookingItem bookingItem = new BookingItem();
        bookingItem.bookingItemTypeId = bookingItemTypeId;
        
        return bookingItem;
    }
    
    /**
     * An availbilty that is added to booking engine and has 
     * a timerange between 08:00 to 09:00
     * 
     * @param item
     * @param bookingEngine
     * @return 
     */
    public Availability getAValidAvailability(BookingItem item, BookingEngine bookingEngine) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        Availability availability = new Availability();
        try {
            availability.startDate = formatter.parse("2014-01-01 08:00");
            availability.endDate = formatter.parse("2014-01-01 09:00");
        } catch (ParseException ex) {
            Logger.getLogger(BookingEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        bookingEngine.addAvailability(item.id, availability);

        
        return availability;
    }
    
    
    public BookingItem createAValidBookingItemAndResetDatabaseSaver(BookingEngine bookingEngine, DatabaseSaver databaseSaver) {
        BookingItemType type = bookingEngine.createABookingItemType("Booking Item Test");
        BookingItem item = createAValidBookingItem(type.id);
        item = bookingEngine.saveBookingItem(item);
        
        reset(databaseSaver);
        return item;
    }

    Booking getValidBooking(int i, BookingEngine bookingEngine) {
        return getValidBooking(i, bookingEngine, null);
    }
    
    /**
     * booking 0: 2014-01-01 08:00 - 2014-01-02 10:00
     * booking 1: 2014-01-01 08:00 - 2014-01-02 08:00
     * booking 2: 2014-01-02 08:00 - 2014-01-03 08:00
     * booking 3: 2014-01-02 06:00 - 2014-01-03 08:00
     * booking 4: 2014-01-01 06:00 - 2014-01-02 12:00
     */
    Booking getValidBooking(int i, BookingEngine bookingEngine, BookingItem item) {
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        List<BookingItemType> types = bookingEngine.getBookingItemTypes();
        BookingItemType type = null;
        if (types.isEmpty()) {
            type = bookingEngine.createABookingItemType("Type");
        } else {
            type = types.get(0);
        }
        
        if (item == null) {
            item = createAValidBookingItem(type.id);
            item.bookingSize = 1;
            item = bookingEngine.saveBookingItem(item);
        }
        
        
        
        Booking booking = new Booking();
        booking.bookingItemTypeId = type.id;
        booking.bookingItemId = item.id;
        
        try {
            if (i == 0) {
                booking.startDate = formatter.parse("2014-01-01 08:00");
                booking.endDate = formatter.parse("2014-01-02 10:00");
            }
            if (i == 1) {
                booking.startDate = formatter.parse("2014-01-01 08:00");
                booking.endDate = formatter.parse("2014-01-02 08:00");
            }
            if (i == 2) {
                booking.startDate = formatter.parse("2014-01-02 08:00");
                booking.endDate = formatter.parse("2014-01-03 08:00");
            }
            if (i == 3) {
                booking.startDate = formatter.parse("2014-01-02 06:00");
                booking.endDate = formatter.parse("2014-01-03 08:00");
            }
            if (i == 4) {
                booking.startDate = formatter.parse("2014-01-02 06:00");
                booking.endDate = formatter.parse("2014-01-03 08:00");
            }
            if (i == 5) {
                booking.startDate = formatter.parse("2014-01-03 08:00");
                booking.endDate = formatter.parse("2014-01-04 08:00");
            }
            if (i == 6) {
                booking.startDate = formatter.parse("2014-01-01 12:00");
                booking.endDate = formatter.parse("2014-01-01 16:00");
            }
        } catch (ParseException ex) {
            Logger.getLogger(BookingEngineTestHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return booking;
    }
}
