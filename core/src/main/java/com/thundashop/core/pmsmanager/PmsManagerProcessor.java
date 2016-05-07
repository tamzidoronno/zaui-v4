package com.thundashop.core.pmsmanager;

import com.ibm.icu.util.Calendar;
import com.thundashop.core.arx.AccessCategory;
import com.thundashop.core.arx.Card;
import com.thundashop.core.arx.Person;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;

public class PmsManagerProcessor {

    private final PmsManager manager;
    private Date lastProcessed;

    PmsManagerProcessor(PmsManager manager) {
        this.manager = manager;
    }

    public void doProcessing() {
        try { runAutoPayWithCard(); }catch(Exception e) { e.printStackTrace(); }
        try { confirmWhenPaid(); }catch(Exception e) { e.printStackTrace(); }
        try { processAutoAssigning(); }catch(Exception e) { e.printStackTrace(); }
        try { processAutoExtend(); }catch(Exception e) { e.printStackTrace(); }
        try { processStarting(-4, 0, false); }catch(Exception e) { e.printStackTrace(); }
        try { processStarting(0, 4, false); }catch(Exception e) { e.printStackTrace(); }
        try { processStarting(4, 12, false); }catch(Exception e) { e.printStackTrace(); }
        try { processStarting(12, 12 * 2, false); }catch(Exception e) { e.printStackTrace(); }
        try { processStarting(24, 24 * 2, false); }catch(Exception e) { e.printStackTrace(); }
        try { processStarting(48, 24 * 3, false); }catch(Exception e) { e.printStackTrace(); }
        try { processStarting(0, 12, true); }catch(Exception e) { e.printStackTrace(); }
        try { processStarting(12, 12 * 2, true); }catch(Exception e) { e.printStackTrace(); }
        try { processStarting(24, 24 * 2, true); }catch(Exception e) { e.printStackTrace(); }
        try { processStarting(48, 24 * 3, true); }catch(Exception e) { e.printStackTrace(); }
        try { processEndings(0, 24 * 1); }catch(Exception e) { e.printStackTrace(); }
        try { processEndings(24, 24 * 2); }catch(Exception e) { e.printStackTrace(); }
        try { processEndings(48, 24 * 3); }catch(Exception e) { e.printStackTrace(); }
        try { processIntervalCleaning(false); }catch(Exception e) { e.printStackTrace(); }
        try { processIntervalCleaning(true); }catch(Exception e) { e.printStackTrace(); }
        try { processArx(); }catch(Exception e) { e.printStackTrace(); }

        try { createPeriodeInvoices(); }catch(Exception e) { e.printStackTrace(); }
        try { makeSureCleaningsAreOkey(); }catch(Exception e) { e.printStackTrace(); }
        try { checkForIncosistentBookings(); }catch(Exception e) { e.printStackTrace(); }
    }

    private void processStarting(int hoursAhead, int maxAhead, boolean started) {
        int hoursAheadCheck = hoursAhead;
        if(hoursAheadCheck == 0) {
            //Send sms even though the event has started, 12 hours later.
            hoursAheadCheck = -12;
        }
        
        int maxAheadCheck = maxAhead;
        if(manager.getConfigurationSecure().ignoreTimeIntervalsOnNotification && !started) {
            hoursAheadCheck = -12;
        }
        
        List<PmsBooking> bookings = getAllConfirmedNotDeleted();
        for (PmsBooking booking : bookings) {

            boolean save = false;
            for (PmsBookingRooms room : booking.rooms) {
                int start = hoursAheadCheck;
                int end = maxAheadCheck;
                if(started) {
                    start = end * -1;
                    end = end * -1;
                }
                if (!isBetween(room.date.start, start, end)) {
                    continue;
                }
                if (room.isEnded()) {
                    continue;
                }
                booking = manager.finalize(booking);
                String key = "room_starting_" + maxAhead + "_hours";
                if(started) {
                    key = "room_started_" + maxAhead + "_hours";
                }
                if (room.notificationsSent.contains(key)) {
                    continue;
                }
                save = true;
                manager.doNotification(key, booking, room);
                room.notificationsSent.add(key);
                if(hoursAhead == 0) {
                    manager.markRoomAsDirty(room.bookingItemId);
                }
            }
            if (save) {
                manager.saveBooking(booking);
            }
        }
    }

    private boolean pushToLock(PmsBookingRooms room, boolean deleted) {
        PmsConfiguration config = manager.getConfigurationSecure();
        
        
        if(!manager.frameworkConfig.productionMode) {
            return true;
        }
        
        if (config.locktype.isEmpty() || config.locktype.equals("arx")) {
            return pushToArx(room, deleted);
        } else {
            return pushToGetShop(room, deleted);
        }
    }

    private void processEndings(int hoursAhead, int maxAhead) {
        List<PmsBooking> bookings = getAllConfirmedNotDeleted();
        for (PmsBooking booking : bookings) {
            boolean save = false;
            for (PmsBookingRooms room : booking.rooms) {
                if (!isBetween(room.date.end, (maxAhead * -1), (hoursAhead * -1))) {
                    continue;
                }
                if (!room.isEnded()) {
                    continue;
                }
                booking = manager.finalize(booking);
                String key = "room_ended_" + hoursAhead + "_hours";
                if (room.notificationsSent.contains(key)) {
                    continue;
                }
                save = true;
                manager.doNotification(key, booking, room);
                room.notificationsSent.add(key);
            }
            if (save) {
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

        if (date.before(now)) {
            return false;
        }

        if (date.after(max)) {
            return false;
        }

        return true;
    }

    private void processArx() {
        if (manager.getConfigurationSecure().arxHostname == null || manager.getConfigurationSecure().arxHostname.isEmpty()) { 
            return;
        }
        
        List<PmsBooking> bookings = getAllConfirmedNotDeleted();
        for (PmsBooking booking : bookings) {
            boolean save = false;
            for (PmsBookingRooms room : booking.rooms) {
                if (!manager.isClean(room.bookingItemId) && manager.getConfigurationSecure().cleaningInterval > 0) {
                    continue;
                }

                if (room.guests.isEmpty() || room.guests.get(0).name == null) {
                    room.guests.clear();
                    PmsGuests guest = new PmsGuests();
                    User user = manager.userManager.getUserById(booking.userId);
                    if (user != null) {
                        guest.name = user.fullName;
                        room.guests.add(guest);
                    }
                }

                if (room.isStarted() && !room.addedToArx && !room.isEnded()) {
                    if (pushToLock(room, false)) {
                        room.addedToArx = true;
                        save = true;
                        manager.doNotification("room_added_to_arx", booking, room);
                    }
                }

                if (room.isEnded() && room.addedToArx) {
                    if (pushToLock(room, true)) {
                        room.addedToArx = false;
                        save = true;
                        manager.doNotification("room_removed_from_arx", booking, room);
                    }
                }
            }
            if (save) {
                manager.saveBooking(booking);
            }
        }
    }

    private void processAutoAssigning() {
        List<PmsBooking> bookings = getAllConfirmedNotDeleted();
        for (PmsBooking booking : bookings) {
            boolean save = false;
            for (PmsBookingRooms room : booking.rooms) {
                if (!room.isStartingToday() && !room.isStarted()) {
                    continue;
                }
                if (room.isEnded()) {
                    continue;
                }
                if(room.bookingItemId == null || room.bookingItemId.isEmpty()) {
                    booking = manager.finalize(booking);
                    if (room.bookingItemId == null || room.bookingItemId.isEmpty()) {
                        manager.autoAssignItem(room);
                        save = true;
                    }
                }
            }
            if (save) {
                manager.finalize(booking);
                manager.saveBooking(booking);
            }
        }
    }

    private void createPeriodeInvoices() {
        if(!manager.getConfigurationSecure().autoCreateInvoices) { 
            return;
        }
        
        if (manager.lastOrderProcessed != null && isSameDay(manager.lastOrderProcessed, new Date())) {
            return;
        }
        
        NewOrderFilter filter = new NewOrderFilter();
        filter.prepaymentDaysAhead = manager.getConfigurationSecure().prepaymentDaysAhead;
        filter.increaseUnits = manager.getConfigurationSecure().increaseUnits;
        filter.autoGeneration = true;
        
        if (!manager.getConfigurationSecure().prepayment) {
            filter.prepayment = false;
            filter.endInvoiceAt = beginningOfMonth(0);
            manager.createOrder(null, filter);

            filter.onlyEnded = true;
            filter.endInvoiceAt = new Date();
            manager.createOrder(null, filter);
        } else {
            filter.prepayment = true;
            filter.endInvoiceAt = beginningOfMonth(1);
            manager.createOrder(null, filter);
        }
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        boolean sameYear = calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR);
        boolean sameMonth = calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH);
        boolean sameDay = calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
        return (sameDay && sameMonth && sameYear);
    }

    private List<PmsBooking> getAllConfirmedNotDeleted() {
        List<PmsBooking> res = new ArrayList(manager.getBookingMap().values());
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
            if (!booking.payedFor) {
                toRemove.add(booking);
            }
        }
        res.removeAll(toRemove);
        return res;
    }

    private Date beginningOfMonth(int monthsToAdd) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.add(Calendar.MONTH, monthsToAdd);

        return cal.getTime();
    }

    private boolean pushToArx(PmsBookingRooms room, boolean deleted) {
        Person person = new Person();
        if (!room.guests.isEmpty() && room.guests.get(0).name != null && room.guests.get(0).name.contains(" ")) {
            person.firstName = room.guests.get(0).name.split(" ")[0];
            if (room.guests.get(0).name.split(" ").length > 1) {
                person.lastName = room.guests.get(0).name.split(" ")[1];
            }
        } else {
            person.lastName = "Name";
            person.firstName = "Unknown";
        }

        if (manager.getConfigurationSecure().arxCardFormat == null || manager.getConfigurationSecure().arxCardFormat.isEmpty()) {
            System.out.println("Card format not set yet");
            return false;
        }

        Card card = new Card();
        card.format = manager.getConfigurationSecure().arxCardFormat;
        card.cardid = room.code;

        person.cards.add(card);
        person.id = room.pmsBookingRoomId;
        person.deleted = deleted;

        AccessCategory category = new AccessCategory();
        BookingItem item = manager.bookingEngine.getBookingItem(room.bookingItemId);
        if (item == null) {
            System.out.println("Not able to push to arx, item does not exists");
            return false;
        }
        String alias = item.bookingItemAlias;
        category.name = item.bookingItemName;
        if (alias != null && !alias.isEmpty()) {
            category.name = alias;
        }
        category.startDate = room.date.start;
        category.endDate = room.date.end;

        person.accessCategories.add(category);

        try {
            manager.arxManager.updatePerson(person);
        } catch (Exception e) {
            e.printStackTrace();
            manager.warnArxDown();
            return false;
        }
        return true;
    }

    private boolean pushToGetShop(PmsBookingRooms room, boolean deleted) {
        BookingItem item = manager.bookingEngine.getBookingItem(room.bookingItemId);
        if (item == null) {
            return false;
        }
        String roomName = item.bookingItemName;
        if (item.bookingItemAlias != null && !item.bookingItemAlias.isEmpty()) {
            roomName = item.bookingItemAlias;
        }
        String result = "";
        try {
            PmsConfiguration config = manager.getConfigurationSecure();
            manager.getShopLockManager.setCredentials(config.arxUsername, config.arxPassword, config.arxHostname);
            if (deleted) {
                result = manager.getShopLockManager.removeCode(room.pmsBookingRoomId);
            } else {
                result = manager.getShopLockManager.pushCode(room.pmsBookingRoomId, roomName, room.code, room.date.start, room.date.end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.equals("OK");

    }

    private void processAutoExtend() {
        if (manager.getConfigurationSecure().autoExtend) {
            List<PmsBooking> bookings = getAllConfirmedNotDeleted();
            for (PmsBooking booking : bookings) {
                boolean needSaving = false;
                for (PmsBookingRooms room : booking.rooms) {
                    if (room.isEnded() && !room.keyIsReturned) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(room.date.end);
                        cal.add(Calendar.DAY_OF_YEAR, 1);
                        room.date.end = cal.getTime();
                        BookingItem item = manager.bookingEngine.getBookingItem(room.bookingItemId);
                        Date start = room.date.start;
                        Date end = room.date.end;
                        if (item != null) {
                            String text = "Autoextending room " + item.bookingItemName;
                            PmsBookingRooms res = manager.changeDates(room.pmsBookingRoomId, booking.id, start, end);
                            if(res == null && (room.warnedAboutAutoExtend == null || room.warnedAboutAutoExtend.equals(room.ended))) {
                                text = "Not able to extend stay for room: " + item.bookingItemName;
                                manager.warnAboutUnableToAutoExtend(item.bookingItemName,"Not able to extend");
                                room.warnedAboutAutoExtend = room.date.end;
                                needSaving = true;
                            }

                            text += " (" + start + " to " + end + ")";
                            manager.logEntry(text, booking.id, room.bookingItemId);
                        }
                        needSaving = true;
                    }
                }
                if (needSaving) {
                    try {
                        booking = manager.finalize(booking);
                        manager.saveBooking(booking);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void processIntervalCleaning(boolean isCheckoutCleaning) {
        int maxNum = manager.getConfigurationSecure().numberOfIntervalCleaning;
        if (isCheckoutCleaning) {
            maxNum = manager.getConfigurationSecure().numberOfCheckoutCleanings;
        }
        if (maxNum == 0) {
            return;
        }

        List<PmsBooking> bookings = getAllConfirmedNotDeleted();

        DateTime time = new DateTime();
        for (Integer i = 0; i < 10; i++) {
            int maxNumAtDay = 0;
            int numberOfInterval = 0;
            int weekOfDay = time.getDayOfWeek();
            if (manager.getConfigurationSecure().cleaningDays.containsKey(weekOfDay)) {
                boolean check = manager.getConfigurationSecure().cleaningDays.get(weekOfDay);
                if (check) {
                    maxNumAtDay = maxNum;
                }
            }
            for (PmsBooking booking : bookings) {
                boolean needSaving = false;
                for (PmsBookingRooms room : booking.rooms) {

                    boolean needUpdate = false;
                    if (isCheckoutCleaning) {
                        needUpdate = manager.needCheckOutCleaning(room, time.toDate());
                    } else {
                        needUpdate = manager.needIntervalCleaning(room, time.toDate());
                    }

                    if (needUpdate) {
                        numberOfInterval++;
                        if (numberOfInterval > maxNumAtDay) {
                            moveToDifferentInterval(room, isCheckoutCleaning);
                            needSaving = true;
                        }
                    }
                }
                if (needSaving) {
                    manager.saveBooking(booking);
                }
            }
            time = time.plusDays(1);
        }
    }

    private void moveToDifferentInterval(PmsBookingRooms room, boolean checkoutCleaning) {
        Calendar dateToMove = Calendar.getInstance();
        if (checkoutCleaning) {
            dateToMove.setTime(room.date.exitCleaningDate);
            dateToMove.add(Calendar.DAY_OF_YEAR, 1);
            room.date.exitCleaningDate = dateToMove.getTime();
        } else {
            dateToMove.setTime(room.date.cleaningDate);
            dateToMove.add(Calendar.DAY_OF_YEAR, -1);
            room.date.cleaningDate = dateToMove.getTime();
        }
    }

    private void makeSureCleaningsAreOkey() {
        manager.makeSureCleaningsAreOkay();
    }


    private void checkForIncosistentBookings() {
        List<Booking> allBookings = manager.bookingEngine.getAllBookings();
        List<String> allBookingIds = new ArrayList();
        for(PmsBooking booking : manager.getBookingMap().values()) {
            if(booking.isDeleted) {
                continue;
            }
            if(booking.rooms != null) {
                for(PmsBookingRooms room : booking.rooms) {
                    if(room.bookingId != null) {
                        allBookingIds.add(room.bookingId);
                    }
                }
            }
        }
        
        for(Booking test : allBookings) {
            if(!allBookingIds.contains(test.id)) {
                manager.messageManager.sendErrorNotification(test.id + " this is missing on the bookingengine, the booking engine and the pms manager is out of sync: " + test.startDate + " - " + test.endDate + ", created: " + test.rowCreatedDate, null);
                manager.bookingEngine.deleteBooking(test.id);
            }
        }
    }

    private void confirmWhenPaid() {
        for(PmsBooking booking : manager.getBookingMap().values()) {
            if(booking.sessionId != null && !booking.sessionId.isEmpty()) {
                continue;
            }
            
            if(!manager.getConfigurationSecure().requirePayments) {
                if(!booking.payedFor) {
                    booking.payedFor = true;
                }
                continue;
            }
            
            
            if((booking.orderIds == null || booking.orderIds.isEmpty()) && !booking.payedFor) {
                booking.payedFor = true;
                manager.logEntry("Automarking booking as paid for, since no orders has been added", booking.id, null);
                manager.saveBooking(booking);
                continue;
            }
            boolean hasordersnotpaidfor = false;
            boolean needCapture = false;
            for(String orderId : booking.orderIds) {
                Order order = manager.orderManager.getOrderSecure(orderId);
                if(order == null) {
                    continue;
                }
                if(order.payment != null && order.payment.paymentType != null && order.payment.paymentType.toLowerCase().contains("invoice")) {
                    continue;
                }
                if(!order.captured) {
                    needCapture = true;
                }
                
                if(order.status != Order.Status.PAYMENT_COMPLETED) {
                    hasordersnotpaidfor = true;
                }
            }
            
            boolean needSaving = false;
            if(booking.needCapture != needCapture) {
                booking.needCapture = needCapture;
                needSaving = true;
            }
            
            if(!hasordersnotpaidfor && !booking.payedFor) {
                needSaving = true;
                booking.payedFor = true;
                manager.logEntry("Automarking booking as paid for", booking.id, null);
            } else if(hasordersnotpaidfor && booking.payedFor) {
                needSaving = true;
                booking.payedFor = false;
                manager.logEntry("This booking has orders not paid for yet.", booking.id, null);
            }
            if(needSaving) {
                manager.saveBooking(booking);
            }
        }
    }

    private void runAutoPayWithCard() {
        if(!manager.getConfigurationSecure().runAutoPayWithCard) {
            return;
        }
        manager.orderManager.checkForOrdersToAutoPay();
    }

}
