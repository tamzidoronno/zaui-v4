package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.usermanager.data.User;
import java.text.SimpleDateFormat;
import java.util.Date;

class PmsBookingMessageFormatter {
    public String formatRoomData(String message, PmsBookingRooms room, BookingEngine bookingEngine) {
         String startMinute = new SimpleDateFormat("m").format(room.date.start).toString();
        if (startMinute.length() < 2) {
            startMinute = "0" + startMinute;
        }
        String endMinute = new SimpleDateFormat("m").format(room.date.end).toString();
        if (endMinute.length() < 2) {
            endMinute = "0" + endMinute;
        }
        
        message = message.replace("{code}", room.code);
        message = message.replace("{checkin_date}", formatDate(room.date.start));
        message = message.replace("{checkout_date}", formatDate(room.date.end));
        message = message.replace("{checkin_time}", new SimpleDateFormat("H:").format(room.date.start) + startMinute);
        message = message.replace("{checkout_time}", new SimpleDateFormat("H:").format(room.date.end) + endMinute);
        message = message.replace("{roomType}", new SimpleDateFormat("H:").format(room.date.end) + endMinute);
        
        if(room.booking != null && room.booking.bookingItemTypeId != null) {
            message = message.replace("{roomType}", bookingEngine.getBookingItemType(room.booking.bookingItemTypeId).name);
        }
        
        if(room.booking != null && room.booking.bookingItemId != null) {
            message = message.replace("{roomName}", bookingEngine.getBookingItem(room.booking.bookingItemId).bookingItemName);
        } else {
            message = message.replace("{roomName}", "unknown");
        }
        
        return message;
    }
    
    public String formatContactData(String message, User user, PmsGuests guest) {
        if(guest != null) {
            if(guest.name != null) { message = message.replace("{name}", guest.name); }
            if(guest.email != null) { message = message.replace("{email}", guest.email); } 
            if(guest.prefix != null) { message = message.replace("{prefix}", guest.prefix); }
            if(guest.phone != null) { message = message.replace("{phone}", guest.phone); }
        } else if(user != null) {
            if(user.fullName != null) { message = message.replace("{name}", user.fullName); }
            if(user.emailAddress != null) { message = message.replace("{email}", user.emailAddress); }
            if(user.prefix != null) { message = message.replace("{prefix}", user.prefix); }
            if(user.cellPhone != null) { message = message.replace("{phone}", user.cellPhone); }
        }
        
        if(user != null) {
            if(user.address != null) {
                if(user.address.address != null) { message = message.replace("{address}", user.address.address); }
                if(user.address.postCode != null) { message = message.replace("{postCode}", user.address.postCode); }
                if(user.address.city != null) { message = message.replace("{city}", user.address.city); }
            }
            
            if(user.fullName != null) { message = message.replace("{contact_name}", user.fullName); }
            if(user.prefix != null) { message = message.replace("{contact_prefix}", user.prefix); }
            if(user.cellPhone != null) { message = message.replace("{contact_phone}", user.cellPhone); }
            if(user.emailAddress != null) { message = message.replace("{contact_email}", user.emailAddress); }
        }
        
        return message;
    }

    String formatBookingData(String message, PmsBooking booking, BookingEngine bookingEngine) {
        String bookingData = "";
        for(PmsBookingRooms room : booking.rooms) {
            if(room.booking != null && room.booking.bookingItemTypeId != null) {
                bookingData += bookingEngine.getBookingItemType(room.bookingItemTypeId).name + "<br>";
            }
            bookingData += formatDate(room.date.start) + " - " + formatDate(room.date.end);
            if(room.booking != null && room.booking.bookingItemId != null) {
                BookingItem item = bookingEngine.getBookingItem(room.booking.bookingItemId);
                if(item != null) {
                    bookingData += item.bookingItemName + "<br>";
                }
            }
            for(PmsGuests guest : room.guests) {
                bookingData += guest.name + " " + guest.prefix + guest.phone + " " + guest.email + "<br>";
            }
        }
        
        message = message.replace("{rooms}", bookingData);
        message = message.replace("{bookingid}", booking.id);
        
        return message;
    }
    
    
    private String formatDate(Date startDate) {
        String startday = new SimpleDateFormat("d").format(startDate);
        String startmonth = new SimpleDateFormat("M").format(startDate);
        String startyear = new SimpleDateFormat("y").format(startDate);
        if(startday.length() == 1) { startday = "0" + startday; }
        if(startmonth.length() == 1) { startmonth = "0" + startmonth; }
        if(startyear.length() == 1) { startyear = "0" + startyear; }
        return startday + "-" + startmonth + "-" + startyear;
    }

}
