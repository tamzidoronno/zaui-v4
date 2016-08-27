
package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class PmsBookingSimpleFilter {

    private final PmsManager manager;

    PmsBookingSimpleFilter(PmsManager aThis) {
        this.manager = aThis;
    }

    LinkedList<PmsRoomSimple> filterRooms(PmsBookingFilter filter) {
        LinkedList<PmsRoomSimple> result = new LinkedList();
        List<PmsBooking> bookings = manager.getAllBookings(filter);
        for(PmsBooking booking : bookings) {
            List<PmsBookingRooms> rooms = booking.getActiveRooms();
            if(filter.includeDeleted) {
                rooms = booking.getAllRoomsIncInactive();
            }
            for(PmsBookingRooms room : rooms) {
                if(inFilter(room, filter, booking)) {
                    result.add(convertRoom(room, booking));
                }
            }
        }
        
        Collections.sort(result, new Comparator<PmsRoomSimple>(){
            public int compare(PmsRoomSimple o1, PmsRoomSimple o2){
                return o2.regDate.compareTo(o1.regDate);
            }
        });
        return result;
    }

    private PmsRoomSimple convertRoom(PmsBookingRooms room, PmsBooking booking) {
        PmsRoomSimple simple = new PmsRoomSimple();
        simple.start = room.date.start.getTime();
        simple.end = room.date.end.getTime();
        simple.bookingItemId = room.bookingItemId;
        simple.addons = room.addons;
        simple.price = room.price;
        simple.checkedIn = false;
        simple.checkedOut = false;
        simple.regDate = booking.rowCreatedDate;
        simple.keyIsReturned = room.keyIsReturned;
        simple.wubookreservationid = booking.wubookreservationid;
        simple.testReservation = booking.testReservation;
        simple.channel = booking.channel;
        
        if(manager.getConfiguration().hasLockSystem()) {
            simple.code = room.code;
        }
        simple.pmsRoomId = room.pmsBookingRoomId;
        simple.bookingId = booking.id;
        User user = manager.userManager.getUserById(booking.userId);
        if(user != null) {
            simple.owner = user.fullName;
        }
        simple.guest = room.guests;
        if(room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
            simple.room = manager.bookingEngine.getBookingItem(room.bookingItemId).bookingItemName;
        }
        if(room.bookingItemTypeId != null) {
            simple.roomType = manager.bookingEngine.getBookingItemType(room.bookingItemTypeId).name;
        }
        
        simple.paidFor = booking.payedFor;
        if(room.isDeleted() || booking.isDeleted) {
            simple.progressState = "deleted";
        } else if(room.isStarted() && !room.isEnded()) {
            if(manager.getConfiguration().hasLockSystem() && !room.addedToArx) {
                simple.progressState = "waitingforlock";
            } else {
                simple.progressState = "active";
            }
        } else if(!booking.confirmed) {
            simple.progressState = "unconfirmed";
        } else if(room.isEnded()) {
            simple.progressState = "ended";
        } else if(booking.confirmed) {
            simple.progressState = "confirmed";
        }
        
        if(!booking.payedFor && 
                manager.getConfigurationSecure().requirePayments && 
                !simple.progressState.equals("deleted")) {
            simple.progressState = "notpaid";
        }
        
        if(booking.testReservation) {
            simple.progressState = "test";
        }
        
        simple.checkedIn = room.checkedin;
        simple.checkedOut = room.checkedout;
        
        simple.numberOfGuests = room.numberOfGuests;
        simple.transferredToArx = room.addedToArx;
        return simple;
    }


    private boolean inFilter(PmsBookingRooms room, PmsBookingFilter filter, PmsBooking booking) {
        if (filter.searchWord != null && !filter.searchWord.isEmpty()) {
            User user = manager.userManager.getUserById(booking.userId);
            if (user != null && user.fullName != null && user.fullName.toLowerCase().contains(filter.searchWord.toLowerCase())) {
                return true;
            } else if (room.containsSearchWord(filter.searchWord)) {
                return true;
            }

            if (room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
                BookingItem item = manager.bookingEngine.getBookingItem(room.bookingItemId);
                if (item.bookingItemName.contains(filter.searchWord)) {
                    return true;
                }
            }
        } else if (filter.filterType == null || filter.filterType.equals("registered")) {
            if (filter.startDate == null || (booking.rowCreatedDate.after(filter.startDate) && booking.rowCreatedDate.before(filter.endDate))) {
                return true;
            }
        } else if (filter.filterType.equals("inhouse")) {
            if(room.checkedin && !room.checkedout) {
                return true;
            }
        } else if (filter.filterType.equals("active")) {
            if (room.isActiveInPeriode(filter.startDate, filter.endDate)) {
                 return true;
            }
        } else if (filter.filterType.equals("uncofirmed")) {
            if (!booking.confirmed) {
                 return true;
            }
        } else if (filter.filterType.equals("checkin")) {
            if (room.checkingInBetween(filter.startDate, filter.endDate)) {
                return true;
            }
        } else if (filter.filterType.equals("checkout")) {
            if (room.checkingOutBetween(filter.startDate, filter.endDate)) {
                return true;
            }
        } else if (filter.filterType.equals("deleted")) {
            if (booking.isDeleted) {
               return true;
            }
        }
        return false;
    }
    
}
