
package com.thundashop.core.pmsmanager;

import com.google.gson.Gson;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.RegistrationRules;
import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PmsBooking extends DataCommon {

    boolean containsSearchWord(String searchWord) {
        searchWord = searchWord.toLowerCase();
        for(PmsBookingRooms room : rooms) {
            for(PmsGuests guest :room.guests) {
                if(guest.email != null && guest.email.toLowerCase().contains(searchWord)) {
                    return true;
                }
                if(guest.phone != null && guest.phone.toLowerCase().contains(searchWord)) {
                    return true;
                }
                if(guest.name != null && guest.name.toLowerCase().contains(searchWord)) {
                    return true;
                }
            }
        }
        return false;
    }

    PmsBooking copy() {
        Gson res = new Gson();
        String copytext = res.toJson(this);
        return res.fromJson(copytext, PmsBooking.class);
    }

    PmsBooking copyUnsecure() {
        PmsBooking result = new PmsBooking();
        PmsBooking currentCopy = copy();
        for(PmsBookingRooms room : currentCopy.rooms) {
            PmsBookingRooms roomCopied = new PmsBookingRooms();
            roomCopied.date = room.date;
            roomCopied.bookingItemId = room.bookingItemId;
            roomCopied.bookingItemTypeId = room.bookingItemTypeId;
            result.confirmed = currentCopy.confirmed;
            
            result.rooms.add(roomCopied);
        }
        
        return result;
    }

    boolean hasStayAfter(Date startInvoiceFrom) {
        for(PmsBookingRooms room : rooms) {
            if(room.date.end.after(startInvoiceFrom)) {
                return true;
            }
        }
        return false;
    }

    boolean isEnded() {
        for(PmsBookingRooms room : rooms) {
            if(room.date.end.after(new Date())) {
                return false;
            }
        }
        return true;
    }

    Date getEndDate() {
        Date endDate = null;
        for(PmsBookingRooms room : rooms) {
            if(endDate == null || room.date.end.after(endDate)) {
                endDate = room.date.end;
            }
        }
        return endDate;
    }

    boolean isEndedOverTwoMonthsAgo() {
        Date ended = getEndDate();
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MONTH, -2);
        if(now.getTime().after(ended)) {
            return true;
        }
        return false;
    }

    public static class PriceType {
        public static Integer daily = 1;
        public static Integer monthly = 2;
        public static Integer weekly = 3;
        public static Integer minutly = 4;
        public static Integer hourly = 5;
        public static Integer secondly = 6;
        public static Integer progressive = 7;
    }
       
    public static class BookingStates {
        public static Integer STARTED = 0;
        public static Integer COMPLETED = 1;
        public static Integer DELETED = 2;
    }
    
    public List<PmsBookingRooms> rooms = new ArrayList();
    public String sessionId;
    public List<PmsBookingAddonItem> addons = new ArrayList();
    public RegistrationRules registrationData = new RegistrationRules();
    public String language = "nb_NO";
    public String userId = "";
    public Integer state = 0;
    public List<String> orderIds = new ArrayList();
    public Date invoicedTo = null;
    public Integer priceType = 1;
    public boolean confirmed = false;
    public boolean isDeleted = false;
    public PmsRepeatingData lastRepeatingData = null;

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

    boolean isActiveInPeriode(Date startDate, Date endDate) {
        for(PmsBookingRooms room : rooms) {
            if(room.date.start.before(endDate) && room.date.end.after(startDate)) {
                return true;
            }
        }
        return false;
    }    

    boolean checkingInBetween(Date startDate, Date endDate) {
        for(PmsBookingRooms room : rooms) {
            if(room.date.start.after(startDate) && room.date.start.before(endDate)) {
                return true;
            }
        }
        return false;
    }

    boolean checkingOutBetween(Date startDate, Date endDate) {
        for(PmsBookingRooms room : rooms) {
            if(room.date.end.after(startDate) && room.date.end.before(endDate)) {
                return true;
            }
        }
        return false;
    }
}
