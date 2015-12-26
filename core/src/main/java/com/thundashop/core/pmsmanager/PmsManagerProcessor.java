package com.thundashop.core.pmsmanager;

import com.ibm.icu.util.Calendar;
import com.thundashop.core.arx.AccessCategory;
import com.thundashop.core.arx.Card;
import com.thundashop.core.arx.Person;
import java.util.Date;
import java.util.List;

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
    }

    private void processStarting(int hoursAhead, int maxAhead) {
        List<PmsBooking> bookings = manager.getAllBookings(null);
        for(PmsBooking booking : bookings) {
            if(!booking.confirmed) {
                continue;
            }
            
            boolean save = false;
            for(PmsBookingRooms room : booking.rooms) {
                if(room.guests.get(0).name.equals("rwar dsa")) {
                    System.out.println("found");
                }
                if(!isBetween(room.date.start, hoursAhead-24, maxAhead-24)) {
                    continue;
                }
                if(room.isEnded()) {
                    continue;
                }
                
                if(hoursAhead == 0) {
                    if(!room.isStarted()) {
                        continue;
                    }
                    if(pushToArx(room, booking.isDeleted)) {
                        save = true;
                        if(booking.isDeleted) {
                            room.ended = true;
                        } else {
                            room.started = true;
                        }
                    } else {
                        continue;
                    }
                }
                
                String key = "room_starting_" + hoursAhead + "_hours";
                if(room.notificationsSent.contains(key)) {
                    continue;
                }
                save = true;
                manager.doNotification(key, booking, room, null);
                room.notificationsSent.add(key);
                
                
            }
            if(save) {
               manager.saveBooking(booking);
            }
        }
    }

    private boolean pushToArx(PmsBookingRooms room, boolean deleted) {
        if(manager.configuration.arxHostname.isEmpty()) {
            return true;
        }
        if(deleted && room.ended) {
            return false;
        }
        if(!deleted && room.started) {
            return false;
        }
        
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
            logonToArx();
            manager.arxManager.updatePerson(person);
        }catch(Exception e) {
            e.printStackTrace();
            manager.warnArxDown();
            return false;
        }
        return true;
    }

    private void logonToArx() {
        manager.arxManager.logonToArx(manager.configuration.arxHostname, 
                manager.configuration.arxUsername, 
                manager.configuration.arxPassword);
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
                
                if(hoursAhead == 0) {
                    if(!room.isEnded() || room.ended) {
                        continue;
                    }
                    if(pushToArx(room, true)) {
                        room.ended = true;
                    } else {
                        continue;
                    }
                }
                
                String key = "room_ended_" + hoursAhead + "_hours";
                if(room.notificationsSent.contains(key)) {
                    continue;
                }
                save = true;
                manager.doNotification(key, booking, room, null);
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

    private boolean isAfter(int hoursAhead, Date date) {
        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(new Date());
        nowCal.add(Calendar.HOUR_OF_DAY, hoursAhead);
        Date now = nowCal.getTime();
        
        return now.after(date);
    }
    
}
