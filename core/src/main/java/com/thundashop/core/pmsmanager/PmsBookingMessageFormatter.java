package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.RegistrationRulesField;
import com.thundashop.core.usermanager.data.User;
import java.text.SimpleDateFormat;
import java.util.Date;

class PmsBookingMessageFormatter { 
    public String formatRoomData(String message, PmsBookingRooms room, BookingEngine bookingEngine) {
        if(message == null) {
            return "";
        }
         String startMinute = new SimpleDateFormat("m").format(room.date.start).toString();
        if (startMinute.length() < 2) {
            startMinute = "0" + startMinute;
        }
        String endMinute = new SimpleDateFormat("m").format(room.date.end).toString();
        if (endMinute.length() < 2) {
            endMinute = "0" + endMinute;
        }
        
        if(room.code != null) {
            message = message.replace("{code}", room.code);
        }
        if(room.date != null && room.date.start != null) {
            message = message.replace("{checkin_date}", formatDate(room.date.start));
        }
        if(room.date != null && room.date.end != null) {
            message = message.replace("{checkout_date}", formatDate(room.date.end));
        }
        if(room.date != null && room.date.start != null) {
            message = message.replace("{checkin_time}", new SimpleDateFormat("H:").format(room.date.start) + startMinute);
        }
        if(room.date != null && room.date.end != null) {
            message = message.replace("{checkout_time}", new SimpleDateFormat("H:").format(room.date.end) + endMinute);
        }
        if(room.date != null && room.date.end != null) {
            message = message.replace("{roomType}", new SimpleDateFormat("H:").format(room.date.end) + endMinute);
        }
        
        if(room.bookingItemTypeId != null && !room.bookingItemTypeId.isEmpty()) {
             BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
             if(type != null) {
                message = message.replace("{roomType}", type.name);
             } else if(type != null) {
                message = message.replace("{roomType}", "");
             } else {
                 
             }
        }
        
        if(room.bookingItemId != null && !room.bookingItemId.trim().isEmpty()) {
            message = message.replace("{roomName}", bookingEngine.getBookingItem(room.bookingItemId).bookingItemName);
        } else {
            message = message.replace("{roomName}", "unknown");
        }
        
        return message;
    }
    
    public String formatContactData(String message, User user, PmsGuests guest, PmsBooking booking) {
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
        if(booking.registrationData.resultAdded.containsKey("user_fullName")) {
            message = message.replace("{name}", booking.registrationData.resultAdded.get("user_fullName"));
        }
        if(booking.registrationData.resultAdded.containsKey("user_cellPhone")) {
            message = message.replace("{phone}", booking.registrationData.resultAdded.get("user_cellPhone"));
        }
        if(booking.registrationData.resultAdded.containsKey("user_emailAddress")) {
            message = message.replace("{email}", booking.registrationData.resultAdded.get("user_emailAddress"));
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
            if(room.bookingItemTypeId != null && !room.bookingItemTypeId.isEmpty()) {
                bookingData += bookingEngine.getBookingItemType(room.bookingItemTypeId).name + " ";
            }
            long diff = 365*60*60*100;
            if(room.date.end != null && room.date.start != null) {
                diff = (room.date.end.getTime() - room.date.start.getTime()) / 1000;
            }
            if(diff > (365*60*60)) {
                bookingData += formatDate(room.date.start);
            } else {
                bookingData += formatDate(room.date.start) + " - " + formatDate(room.date.end) + " ";
            }
            if(room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
                BookingItem item = bookingEngine.getBookingItem(room.bookingItemId);
                if(item != null) {
                    bookingData += "(" + item.bookingItemName + ")";
                }
            } else if(room.bookingItemTypeId != null && !room.bookingItemTypeId.isEmpty()) {
                BookingItemType item = bookingEngine.getBookingItemType(room.bookingItemTypeId);
                if(item != null) {
                    bookingData += "(" + item.name + ")";
                }

            }
            for(PmsGuests guest : room.guests) {
                bookingData += "<br>";
                bookingData += guest.name + " " + guest.prefix + guest.phone + " " + guest.email + "<br>";
            }
            bookingData += "<br>";
        }
        
        String bookinginfo = "<table>";
        for(String key : booking.registrationData.resultAdded.keySet()) {
            if("agreetoterms".equals(key)) {
                continue;
            }
            String title = getTitle(key, booking);
            
            String text = booking.registrationData.resultAdded.get(key);
            if(key.equals("choosetyperadio")) {
                title = "Reg type";
                if(text.equals("registration_company")) {
                    text = "Organisasjon";
                } else {
                    text ="Privat";
                }
            }

            if(text.trim().isEmpty() || title.trim().isEmpty()) {
                continue;
            }
            bookinginfo += "<tr><td valign='top'>" + title + "</td><td>" + text + "</td></tr>";
        }
        bookinginfo += "</table>";
        
        message = message.replace("{rooms}", bookingData);
        message = message.replace("{roomlist}", bookingData);
        message = message.replace("{bookingid}", booking.id);
        message = message.replace("{bookinginformation}", bookinginfo);
        
        return message;
    }
    
    
    private String formatDate(Date startDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return sdf.format(startDate);
    }

    private String getTitle(String key, PmsBooking booking) {
        if(key.equals("openforpublic")) {
            return "Åpent for publikum";
        }
        for(RegistrationRulesField data : booking.registrationData.data.values()) {
            if(data.name.equals(key)) {
                String title = data.title;
                return title;
            }
        }
        return "";
    }

}
