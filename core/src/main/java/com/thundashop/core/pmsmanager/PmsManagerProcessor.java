package com.thundashop.core.pmsmanager;

import com.ibm.icu.util.Calendar;
import com.thundashop.core.arx.AccessCategory;
import com.thundashop.core.arx.Card;
import com.thundashop.core.arx.Person;
import com.thundashop.core.bookingengine.BookingEngine;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class PmsManagerProcessor {
    private final PmsManager manager;

    PmsManagerProcessor(PmsManager manager) {
        this.manager = manager;
    }
    
    public void doProcessing() {
        processStarting(0, 24*1);
        processStarting(24, 24*2);
        processStarting(48, 24*3);
        processEndings(0, 24*1);
        processEndings(24, 24*2);
        processEndings(48, 24*3);
        processAutoAssigning();
        if(manager.configuration.arxHostname != null && !manager.configuration.arxHostname.isEmpty()) {
            processArx();
        }
    }

    private void processStarting(int hoursAhead, int maxAhead) {
        List<PmsBooking> bookings = manager.getAllBookings(null);
        for(PmsBooking booking : bookings) {
            if(!booking.confirmed) {
                continue;
            }
            
            boolean save = false;
            for(PmsBookingRooms room : booking.rooms) {
                if(!isBetween(room.date.start, hoursAhead-24, maxAhead-24)) {
                    continue;
                }
                if(room.isEnded()) {
                    continue;
                }
                
                String key = "room_starting_" + hoursAhead + "_hours";
                if(room.notificationsSent.contains(key)) {
                    continue;
                }
                save = true;
                manager.doNotification(key, booking, room);
                room.notificationsSent.add(key);
                
                
            }
            if(save) {
               manager.saveBooking(booking);
            }
        }
    }

    private boolean pushToArx(PmsBookingRooms room, boolean deleted) {        
        room.code = generateCode(room.code);
        Person person = new Person();
        person.firstName = room.guests.get(0).name.split(" ")[0];
        person.lastName = room.guests.get(0).name.split(" ")[1];
        
        Card card = new Card();
        card.format = "kode";
        card.cardid = room.code;
        person.cards.add(card);
        person.id = room.pmsBookingRoomId;
        person.deleted = deleted;
        
        AccessCategory category = new AccessCategory();
        category.name = manager.bookingEngine.getBookingItem(room.bookingItemId).bookingItemName;
        person.accessCategories.add(category);
        
        try {
            manager.arxManager.overrideCredentials(manager.configuration.arxHostname,
                    manager.configuration.arxUsername,
                    manager.configuration.arxPassword);
                    
            manager.arxManager.updatePerson(person);
        }catch(Exception e) {
            e.printStackTrace();
            manager.warnArxDown();
            return false;
        }
        return true;
    }

    private void processEndings(int hoursAhead, int maxAhead) {
        List<PmsBooking> bookings = manager.getAllBookings(null);
        for(PmsBooking booking : bookings) {
            if(!booking.confirmed) {
                continue;
            }
            boolean save = false;
            for(PmsBookingRooms room : booking.rooms) {
                if(!isBetween(room.date.end, (maxAhead*-1), (hoursAhead*-1))) {
                    continue;
                }
                if(!room.isEnded()) {
                    continue;
                }
                
                String key = "room_ended_" + hoursAhead + "_hours";
                if(room.notificationsSent.contains(key)) {
                    continue;
                }
                save = true;
                manager.doNotification(key, booking, room);
                room.notificationsSent.add(key);
            }
            if(save) {
                manager.saveBooking(booking);
            }
        }
    }

    private boolean isBetween(Date date, int hoursAhead, int maxAhead) {
        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(new Date());
        nowCal.add(Calendar.HOUR_OF_DAY, hoursAhead);
        Date now = nowCal.getTime();
        
        nowCal.setTime(new Date());
        nowCal.add(Calendar.HOUR_OF_DAY, maxAhead);
        Date max = nowCal.getTime();
        
        if(date.before(now)) {
            return false;
        }
        
        if(date.after(max)) {
            return false;
        }
        
        return true;
    }

    private void processArx() {
        List<PmsBooking> bookings = manager.getAllBookings(null);
        for(PmsBooking booking : bookings) {
            
            if(booking.id.equals("fbee75cb-cd15-4ab6-8076-ed19c0cd466a")) {
                System.out.println("this is it");
            }
            if(!booking.confirmed) {
                continue;
            }
            boolean save = false;
            for(PmsBookingRooms room : booking.rooms) {
                if(!manager.isClean(room.bookingItemId) && manager.configuration.cleaningInterval > 0) {
                    continue;
                }
                
                if(room.isStarted() && !room.addedToArx && !room.isEnded()) {
                    if(pushToArx(room, false)) {
                        room.addedToArx = true;
                        save = true;
                        manager.doNotification("room_added_to_arx", booking, room);
                    }
                }
                
                if(room.isEnded() && room.addedToArx) {
                    if(pushToArx(room, true)) {
                        room.addedToArx = false;
                        save = true;
                        manager.doNotification("room_removed_from_arx", booking, room);
                    }
                }
            }
            if(save) {
                manager.saveBooking(booking);
            }
        }
    }

    private String generateCode(String code) {
        if(code != null && !code.isEmpty()) {
            return code;
        }
        
        for(int i = 0; i < 100000; i++) {
            Integer newcode = new Random().nextInt(9999-1000)+1000;
            if(!codeExist(newcode)) {
                return newcode.toString();
            }
        }
        System.out.println("Tried 100 000 times to generate a code without success");
        return null;
    }

    private boolean codeExist(int newcode) {
        List<PmsBooking> bookings = manager.getAllBookings(null);
        for(PmsBooking booking : bookings) {
            for(PmsBookingRooms room : booking.rooms) {
                if(room.code.equals(newcode) && room.addedToArx) {
                    return true;
                }
            }
        }
        return false;
    }

    private void processAutoAssigning() {
        List<PmsBooking> bookings = manager.getAllBookings(null);
        for(PmsBooking booking : bookings) {
            
            if(booking.id.equals("fbee75cb-cd15-4ab6-8076-ed19c0cd466a")) {
                System.out.println("this is it");
            }
            if(!booking.confirmed) {
                continue;
            }
            
            boolean save = false;
            for(PmsBookingRooms room : booking.rooms) {
                if(!room.isStarted()) {
                    continue;
                }
                if(room.isEnded()) {
                    continue;
                }
                
                if(room.bookingItemId == null || room.bookingItemId.isEmpty()) {
                    manager.bookingEngine.autoAssignItem(room.bookingId);
                }
            }
        }
    }
    
}
