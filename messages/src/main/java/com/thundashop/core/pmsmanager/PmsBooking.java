
package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PmsBooking extends DataCommon {

    public static class PriceType {
        public static Integer daily = 1;
        public static Integer monthly = 2;
        public static Integer weekly = 3;
        public static Integer minutly = 4;
        public static Integer hourly = 5;
        public static Integer secondly = 6;
    }
       
    public static class BookingStates {
        public static Integer STARTED = 0;
        public static Integer COMPLETED = 1;
        public static Integer DELETED = 2;
    }
    
    public List<PmsBookingRooms> rooms = new ArrayList();
    public String sessionId;
    public List<PmsBookingAddonItem> addons = new ArrayList();
    public BookingContactData contactData = new BookingContactData();
    public String language = "nb_NO";
    public String userId = "";
    public Integer state = 0;
    public List<String> orderIds = new ArrayList();
    public Date invoicedTo = null;
    public Integer priceType = 1;
    public boolean confirmed = false;

    void attachBookingItems(List<Booking> bookingsToAdd) {
        for(PmsBookingRooms room : rooms) {
            for(Booking booking : bookingsToAdd) {
                if(room.pmsBookingRoomId.equals(booking.externalReference)) {
                    room.bookingId = booking.id;
                }
            }
        }
    }
    
    PmsBookingRooms findRoom(String roomId) {
        for(PmsBookingRooms room : rooms) {
            if(room.pmsBookingRoomId.equals(roomId)) {
                return room;
            }
        }
        return null;
    }
}
