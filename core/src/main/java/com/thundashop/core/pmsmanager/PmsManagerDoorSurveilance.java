/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.arx.AccessLog;
import com.thundashop.core.arx.ArxConnection;
import com.thundashop.core.arx.Door;
import com.thundashop.core.bookingengine.data.BookingItem;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author boggi
 */
public class PmsManagerDoorSurveilance {
    
    PmsManager manager;

    PmsManagerDoorSurveilance(PmsManager aThis) {
        this.manager = aThis;
    }
    
    public void checkStatus() throws Exception {
        closeForTheDay();
        processKeepDoorOpenClosed();
    }
    
    private PmsBooking getActiveRoomWithCard(String card) throws Exception {
        if (card == null) {
            return null;
        }

        List<PmsBooking> bookings = getAllConfirmedNotDeleted();
        for (PmsBooking booking : bookings) {
            for (PmsBookingRooms room : booking.rooms) {
                if (room.isEnded()) {
                    continue;
                }
                if (!room.isStarted()) {
                    continue;
                }

                if (room.code.equals(card)) {
                    return booking;
                }
            }
        }
        return null;
    }
    
    private void closeForTheDay() throws Exception {
        
        if (!manager.getConfigurationSecure().keepDoorOpenWhenCodeIsPressed) {
            return;
        }
        
        String closeAtEnd = manager.getConfigurationSecure().closeAllDoorsAfterTime;
        String[] time = closeAtEnd.split(":");
        int hour = new Integer(time[0]);
        int minute = new Integer(time[1]);
        
        String arxHostname = manager.getConfigurationSecure().arxHostname;
        String arxUsername = manager.getConfigurationSecure().arxUsername;
        String arxPassword = manager.getConfigurationSecure().arxPassword;
        
        
        com.ibm.icu.util.Calendar cal = com.ibm.icu.util.Calendar.getInstance();
        if(cal.get(com.ibm.icu.util.Calendar.HOUR_OF_DAY) > hour) {
            manager.arxManager.closeAllForTheDay();
        } else if(cal.get(com.ibm.icu.util.Calendar.HOUR_OF_DAY) == hour && cal.get(com.ibm.icu.util.Calendar.MINUTE) >= minute) {
            manager.arxManager.closeAllForTheDay();
        } else {
            manager.arxManager.clearCloseForToday();
        }
    }
    
    public void handleDoorControl(String doorId, List<AccessLog> accessLogs) throws Exception {
        ArxConnection connection = new ArxConnection();
        PmsConfiguration config = manager.getConfiguration();

        if (!config.keepDoorOpenWhenCodeIsPressed) {
            return;
        }
        
        for (AccessLog logEntry : accessLogs) {
            if (logEntry.card != null) {
                PmsBooking book = getActiveRoomWithCard(logEntry.card);
                if (book != null) {
                    for (PmsBookingRooms room : book.rooms) {
                        manager.logEntry("Card / code used " + logEntry.card + " has been entered", book.id, room.bookingItemId);
                        if (room.code.equals(logEntry.card)) {
                            if(!room.forcedOpen) {
                                manager.logEntry("Forced open door: " + logEntry.door, book.id, room.bookingItemId);
                                connection.doorAction(config.arxHostname, config.arxUsername, config.arxPassword, doorId, "forceOpen", true);
                                room.forcedOpen = true;
                                room.forcedOpenCompleted = false;
                                manager.saveBooking(book);
                            } else {
                                manager.logEntry("Door need closing: " + logEntry.door, book.id, room.bookingItemId);
                                room.forcedOpenNeedClosing = true;
                                room.forcedOpen = false;
                                manager.saveBooking(book);
                                processKeepDoorOpenClosed();
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
    
    private List<PmsBooking> getAllConfirmedNotDeleted() {
        List<PmsBooking> res = new ArrayList(manager.bookings.values());
        List<PmsBooking> toRemove = new ArrayList();
        for (PmsBooking booking : res) {
            if (booking.rooms == null) {
                toRemove.add(booking);
            }
            if (booking.isDeleted) {
                toRemove.add(booking);
            }
            if (booking.sessionId != null && !booking.sessionId.isEmpty()) {
                toRemove.add(booking);
            }
            if (!booking.confirmed) {
                toRemove.add(booking);
            }
        }
        res.removeAll(toRemove);
        return res;
    }
    
    private void processKeepDoorOpenClosed() throws Exception {
        if (!manager.getConfigurationSecure().keepDoorOpenWhenCodeIsPressed) {
            return;
        }
        
        List<String> avoidClosing = new ArrayList();
        List<String> mightNeedClosing = new ArrayList();

        List<PmsBooking> bookings = getAllConfirmedNotDeleted();
        for (PmsBooking booking : bookings) {
            for (PmsBookingRooms room : booking.rooms) {
                if (!room.isEnded() && room.isStarted() && room.forcedOpen) {
                    if(room.forcedOpenNeedClosing) {
                        continue;
                    }
                    avoidClosing.add(room.bookingItemId);
                }
            }
        }
        for (PmsBooking booking : bookings) {
            for (PmsBookingRooms room : booking.rooms) {
                if(room.isEnded() || room.forcedOpenNeedClosing) {
                    if(room.forcedOpenCompleted) {
                        continue;
                    }
                    mightNeedClosing.add(room.pmsBookingRoomId);
                }
            }
        }
        
        if(!avoidClosing.isEmpty()) {
            return;
        }
        
        for (String itemToClose : mightNeedClosing) {
            for (PmsBooking booking : bookings) {
                boolean needSaving = false;
                for (PmsBookingRooms room : booking.rooms) {
                    if (avoidClosing.contains(room.bookingItemId)) {
                        continue;
                    }
                    
                    List<String> closed = new ArrayList();
                    
                    if (room.pmsBookingRoomId.equals(itemToClose)) {
                        BookingItem item = manager.bookingEngine.getBookingItem(room.bookingItemId);
                        String itemName = "";
                        if(item != null) {
                            itemName = item.bookingItemName + "(" + item.bookingItemAlias + ")";
                        }
                        if(!closed.contains(room.bookingItemId)) {
                            manager.logEntry("Closing door: " + itemName, booking.id, room.bookingItemId);
                            closeRoom(room.bookingItemId, booking.id);
                            closed.add(room.bookingItemId);
                        }
                        room.forcedOpenCompleted = true;
                        room.forcedOpenNeedClosing = false;
                        needSaving = true;
                    }
                }
                if (needSaving) {
                    manager.saveBooking(booking);
                }
            }
        }
    }

    private void closeRoom(String itemToClose, String bookingId) throws Exception {
        BookingItem item = manager.bookingEngine.getBookingItem(itemToClose);
        if(item == null) {
            System.out.println("Could not find item when closing door, id: "  + itemToClose);
            return;
        }
        ArxConnection con = new ArxConnection();
        List<Door> doors = manager.arxManager.getAllDoors();
        for (Door door : doors) {
            if (door.name.equals(item.bookingItemName) || door.name.equals(item.bookingItemAlias) || door.name.equals(item.doorId)) {
                con.doorAction(manager.getConfigurationSecure().arxHostname, manager.getConfigurationSecure().arxUsername, manager.getConfigurationSecure().arxPassword, door.externalId, "forceOpen", false);
                manager.logEntry("Ran close on : " + door.externalId, bookingId, itemToClose);
            }
        }
    }
}
