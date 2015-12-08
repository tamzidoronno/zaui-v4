package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsManager extends GetShopSessionBeanNamed implements IPmsManager {

    public HashMap<String, PmsBooking> bookings = new HashMap();
    
    @Autowired
    BookingEngine bookingEngine;

    @Override
    public List<Room> getAllRoomTypes(long start, long end) {
        List<Room> result = new ArrayList();
        List<BookingItemType> allGroups = bookingEngine.getBookingItemTypes();
        for(BookingItemType type : allGroups) {
            Room room = new Room();
            room.type = type;
            room.price = 1.0;
            result.add(room);
        }
        return result;
    }

    @Override
    public void setBooking(PmsBooking booking) throws Exception {
        PmsBooking result = bookings.get(getSession().id);
        if(result != null) {
            if(!result.id.equals(booking.id)) {
                throw new Exception("Invalid booking update");
            }
        }
        
        booking.sessionId = getSession().id;
        saveObject(booking);
        bookings.put(booking.sessionId, booking);
    }

    @Override
    public PmsBooking getCurrentBooking() {
        if(getSession() == null) {
            System.out.println("Warning, no session set yet");
        }
        PmsBooking result = bookings.get(getSession().id);
        if(result == null) {
            return startBooking();
        }
        return result;
    }

    @Override
    public PmsBooking startBooking() {
        PmsBooking currentBooking = bookings.get(getSession().id);
        
        bookings.remove(getSession().id);
        if(currentBooking != null) {
            deleteObject(currentBooking);
        }
        
        PmsBooking booking = new PmsBooking();
        
        PmsBookingDateRange range = new PmsBookingDateRange();
        range.start = new Date();
        booking.dates.add(range);
        
        try {
            setBooking(booking);
        } catch (Exception ex) {
            Logger.getLogger(PmsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return booking;
    }
    
}
