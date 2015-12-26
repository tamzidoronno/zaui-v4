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
        processStarting(0);
        processStarting(24);
        processStarting(48);
        processEndings(0);
        processEndings(24);
        processEndings(48);
    }

    private void processStarting(int hoursAhead) {
        List<PmsBooking> bookings = manager.getAllBookings(null);
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.add(Calendar.HOUR_OF_DAY, hoursAhead);
        Date now = nowCalendar.getTime();
        
        for(PmsBooking booking : bookings) {
            boolean save = false;
            for(PmsBookingRooms room : booking.rooms) {
                
                if(!room.date.start.after(now)) {
                    continue;
                }
                if(!room.date.end.after(now)) {
                    continue;
                }
                
                if(hoursAhead == 0) {
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
        
        logonToArx();
        try {
            manager.arxManager.updatePerson(person);
        }catch(Exception e) {
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

    private void processEndings(int hoursAhead) {
        List<PmsBooking> bookings = manager.getAllBookings(null);
        Calendar endFuture = Calendar.getInstance();
        endFuture.add(Calendar.HOUR_OF_DAY, 6+hoursAhead);
        Date end = endFuture.getTime();
        
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.add(Calendar.HOUR_OF_DAY, hoursAhead);
        Date now = nowCalendar.getTime();
        
        for(PmsBooking booking : bookings) {
            boolean save = false;
            for(PmsBookingRooms room : booking.rooms) {
                if(!room.date.end.after(now)) {
                    //Not yet ended
                    continue;
                }
                if(room.date.end.after(end)) {
                    //Ending sometimes in the future.
                    continue;
                }
                
                if(room.ended) {
                    //Already ended.
                    continue;
                }
                
                if(pushToArx(room, true)) {
                    room.ended = true;
                    manager.doNotification("room_ended", booking, room, null);
                    save = true;
                } else {
                    continue;
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
    
}
