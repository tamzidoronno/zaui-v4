package com.thundashop.core.pmsmanager;

import com.ibm.icu.util.Calendar;
import com.thundashop.core.arx.AccessCategory;
import com.thundashop.core.arx.AccessLog;
import com.thundashop.core.arx.ArxManager;
import com.thundashop.core.arx.Card;
import com.thundashop.core.arx.Door;
import com.thundashop.core.arx.Person;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

public class PmsManagerProcessor {

    private final PmsManager manager;
    private Date lastProcessed;

    PmsManagerProcessor(PmsManager manager) {
        this.manager = manager;
    }

    public void doProcessing() {
        try { processAutoAssigning(); }catch(Exception e) { e.printStackTrace(); }
        try { processAutoExtend(); }catch(Exception e) { e.printStackTrace(); }
        try { processStarting(0, 24 * 1); }catch(Exception e) { e.printStackTrace(); }
        try { processStarting(24, 24 * 2); }catch(Exception e) { e.printStackTrace(); }
        try { processStarting(48, 24 * 3); }catch(Exception e) { e.printStackTrace(); }
        try { processEndings(0, 24 * 1); }catch(Exception e) { e.printStackTrace(); }
        try { processEndings(24, 24 * 2); }catch(Exception e) { e.printStackTrace(); }
        try { processEndings(48, 24 * 3); }catch(Exception e) { e.printStackTrace(); }
        try { processIntervalCleaning(false); }catch(Exception e) { e.printStackTrace(); }
        try { processIntervalCleaning(true); }catch(Exception e) { e.printStackTrace(); }
        try {
            closeForTheDay();
            processKeepDoorOpen();
            processKeepDoorOpenClosed();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try { processArx(); }catch(Exception e) { e.printStackTrace(); }
        try { processOrdersToCreate(); }catch(Exception e) { e.printStackTrace(); }
        try { makeSureCleaningsAreOkey(); }catch(Exception e) { e.printStackTrace(); }
        try { checkForIncosistentBookings(); }catch(Exception e) { e.printStackTrace(); }
    }

    private void processStarting(int hoursAhead, int maxAhead) {
        List<PmsBooking> bookings = getAllConfirmedNotDeleted();
        for (PmsBooking booking : bookings) {

            boolean save = false;
            for (PmsBookingRooms room : booking.rooms) {
                if (!isBetween(room.date.start, hoursAhead - 24, maxAhead - 24)) {
                    continue;
                }
                if (room.isEnded()) {
                    continue;
                }
                booking = manager.finalize(booking);
                String key = "room_starting_" + hoursAhead + "_hours";
                if (room.notificationsSent.contains(key)) {
                    continue;
                }
                save = true;
                manager.doNotification(key, booking, room);
                room.notificationsSent.add(key);
                manager.markRoomAsDirty(room.bookingItemId);

            }
            if (save) {
                manager.saveBooking(booking);
            }
        }
    }

    private boolean pushToLock(PmsBookingRooms room, boolean deleted) {
        if (manager.configuration.locktype.isEmpty() || manager.configuration.locktype.equals("arx")) {
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
        if (manager.configuration.arxHostname == null || manager.configuration.arxHostname.isEmpty()) { 
            return;
        }
        
        List<PmsBooking> bookings = getAllConfirmedNotDeleted();
        for (PmsBooking booking : bookings) {
            boolean save = false;
            for (PmsBookingRooms room : booking.rooms) {
                if (!manager.isClean(room.bookingItemId) && manager.configuration.cleaningInterval > 0) {
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

    private String generateCode(String code) {
        if (code != null && !code.isEmpty()) {
            return code;
        }

        for (int i = 0; i < 100000; i++) {
            int start = 1;
            int end = 10;
            for (int j = 0; j < manager.configuration.codeSize - 1; j++) {
                start *= 10;
                end *= 10;
            }
            end = end - 1;

            Integer newcode = new Random().nextInt(end - start) + start;
            if (!codeExist(newcode)) {
                return newcode.toString();
            }
        }
        System.out.println("Tried 100 000 times to generate a code without success");
        return null;
    }

    private boolean codeExist(int newcode) {
        List<PmsBooking> bookings = getAllConfirmedNotDeleted();
        for (PmsBooking booking : bookings) {
            for (PmsBookingRooms room : booking.rooms) {
                if (room.code.equals(newcode) && room.addedToArx) {
                    return true;
                }
            }
        }
        return false;
    }

    private void processAutoAssigning() {
        List<PmsBooking> bookings = getAllConfirmedNotDeleted();
        for (PmsBooking booking : bookings) {
            boolean save = false;
            for (PmsBookingRooms room : booking.rooms) {
                if (!room.isStartingToday()) {
                    continue;
                }
                if (room.isEnded()) {
                    continue;
                }
                booking = manager.finalize(booking);
                if (room.bookingItemId == null || room.bookingItemId.isEmpty()) {
                    manager.autoAssignItem(room);
                    save = true;
                }
            }
            if (save) {
                manager.finalize(booking);
                manager.saveBooking(booking);
            }
        }
    }

    private void processOrdersToCreate() {
        
        if(!manager.configuration.autoCreateInvoices) { 
            return;
        }
        
        if (manager.lastOrderProcessed != null && isSameDay(manager.lastOrderProcessed, new Date())) {
            return;
        }

        List<PmsBooking> bookings = getAllConfirmedNotDeleted();
        for (PmsBooking booking : bookings) {
            if (booking.isEndedOverTwoMonthsAgo()) {
                continue;
            }

            createPeriodeInvoices(booking);
        }
        manager.lastOrderProcessed = new Date();
    }

    private void createPeriodeInvoices(PmsBooking booking) {
        NewOrderFilter filter = new NewOrderFilter();
        filter.prepaymentDaysAhead = manager.configuration.prepaymentDaysAhead;
        filter.increaseUnits = manager.configuration.increaseUnits;
        
        if (!manager.configuration.prepayment) {
            filter.prepayment = false;
            filter.startInvoiceFrom = beginningOfMonth(-1);
            filter.endInvoiceAt = beginningOfMonth(0);
            manager.createOrder(booking.id, filter);

            filter.onlyEnded = true;
            filter.endInvoiceAt = new Date();
            manager.createOrder(booking.id, filter);

        } else {
            filter.prepayment = true;
            filter.startInvoiceFrom = beginningOfMonth(0);
            filter.endInvoiceAt = beginningOfMonth(1);
            manager.createOrder(booking.id, filter);
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

    private Date addOneMonth(Date invoicedTo) {
        Calendar toCheckCal = Calendar.getInstance();
        toCheckCal.setTime(invoicedTo);
        toCheckCal.add(Calendar.MONTH, 1);
        return toCheckCal.getTime();
    }

    private boolean isAfterOrToday(Date invoicedTo) {
        return (invoicedTo.before(new Date()) || isSameDay(new Date(), invoicedTo));
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

    private Date beginningOfMonth(int monthsToAdd) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.add(Calendar.MONTH, monthsToAdd);

        return cal.getTime();
    }

    private boolean pushToArx(PmsBookingRooms room, boolean deleted) {
        room.code = generateCode(room.code);
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

        if (manager.configuration.arxCardFormat == null || manager.configuration.arxCardFormat.isEmpty()) {
            System.out.println("Card format not set yet");
            return false;
        }

        Card card = new Card();
        card.format = manager.configuration.arxCardFormat;
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
            manager.arxManager.overrideCredentials(manager.configuration.arxHostname,
                    manager.configuration.arxUsername,
                    manager.configuration.arxPassword);

            manager.arxManager.updatePerson(person);
            manager.arxManager.clearOverRideCredentials();
        } catch (Exception e) {
            e.printStackTrace();
            manager.warnArxDown();
            return false;
        }
        return true;
    }

    private boolean pushToGetShop(PmsBookingRooms room, boolean deleted) {
        room.code = generateCode(room.code);
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
            PmsConfiguration config = manager.configuration;
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
        if (manager.configuration.autoExtend) {
            List<PmsBooking> bookings = getAllConfirmedNotDeleted();
            for (PmsBooking booking : bookings) {
                boolean needSaving = false;
                for (PmsBookingRooms room : booking.rooms) {
                    if (room.isEnded() && room.isEndingToday() && !room.keyIsReturned) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(room.date.end);
                        cal.add(Calendar.DAY_OF_YEAR, 1);
                        room.date.end = cal.getTime();
                        BookingItem item = manager.bookingEngine.getBookingItem(room.bookingItemId);
                        if (item != null) {
                            String text = "Autoextending room " + item.bookingItemName;
                            PmsBookingRooms res = manager.changeDates(room.pmsBookingRoomId, booking.id, room.date.start, room.date.end);
                            if(res != null) {
                                manager.logEntry(text, booking.id, room.bookingItemId);
                            } else {
                                manager.logEntry("Not able to extend stay for room: " + item.bookingItemName, booking.id, room.bookingItemId);
                                manager.warnAboutUnableToAutoExtend(item.bookingItemName,"Not able to extend");
                            }
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
        int maxNum = manager.configuration.numberOfIntervalCleaning;
        if (isCheckoutCleaning) {
            maxNum = manager.configuration.numberOfCheckoutCleanings;
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
            if (manager.configuration.cleaningDays.containsKey(weekOfDay)) {
                boolean check = manager.configuration.cleaningDays.get(weekOfDay);
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

    private void processKeepDoorOpen() throws Exception {
        if (!manager.configuration.keepDoorOpenWhenCodeIsPressed) {
            return;
        }

        String arxHostname = manager.configuration.arxHostname;
        String arxUsername = manager.configuration.arxUsername;
        String arxPassword = manager.configuration.arxPassword;

        manager.arxManager.overrideCredentials(arxHostname, arxUsername, arxPassword);
       
        manager.arxManager.startFetcherThread();
        
        int minute = 60 * 1000;

        HashMap<String, List<AccessLog>> log = manager.arxManager.getLogForAllDoor((System.currentTimeMillis() - (minute * 2)), System.currentTimeMillis());
        for (String doorId : log.keySet()) {
            List<AccessLog> accessLogs = log.get(doorId);
            accessLogs = makeLatestAccessLog(accessLogs);
            for (AccessLog logEntry : accessLogs) {
                if (logEntry.card != null) {
                    PmsBooking book = getActiveRoomWithCard(logEntry.card);
                    if (book != null) {
                        for (PmsBookingRooms room : book.rooms) {
                            if (room.code.equals(logEntry.card)) {
                                if(room.forcedOpenDate != null && room.forcedOpenDate.getTime() == logEntry.timestamp) {
                                    continue;
                                }
                                room.forcedOpenDate = new Date();
                                room.forcedOpenDate.setTime(logEntry.timestamp);
                                if(!room.forcedOpen) {
                                    manager.logEntry("Forced open door: " + logEntry.door, book.id, room.bookingItemId);
                                    manager.arxManager.doorAction(doorId, "forceOpen", true);
                                    room.forcedOpen = true;
                                    room.forcedOpenCompleted = false;
                                    manager.saveBooking(book);
                                } else {
                                    manager.logEntry("Door need closing: " + logEntry.door, book.id, room.bookingItemId);
                                    room.forcedOpenNeedClosing = true;
                                    room.forcedOpen = false;
                                    manager.saveBooking(book);
                                }
                            }
                        }
                    }
                }
            }
        }
        manager.arxManager.clearOverRideCredentials();
    }

    private PmsBooking getActiveRoomWithCard(String card) {
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

    private void processKeepDoorOpenClosed() throws Exception {
        if (!manager.configuration.keepDoorOpenWhenCodeIsPressed) {
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
                    
                    if (room.pmsBookingRoomId.equals(itemToClose)) {
                        BookingItem item = manager.bookingEngine.getBookingItem(room.bookingItemId);
                        String itemName = "";
                        if(item != null) {
                            itemName = item.bookingItemName + "(" + item.bookingItemAlias + ")";
                        }
                        manager.logEntry("Closing door: " + itemName, booking.id, room.bookingItemId);
                        closeRoom(room.bookingItemId, booking.id);
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
        manager.arxManager.overrideCredentials(manager.configuration.arxHostname, manager.configuration.arxUsername, manager.configuration.arxPassword);
        List<Door> doors = manager.arxManager.getAllDoors();
        for (Door door : doors) {
            if (door.name.equals(item.bookingItemName) || door.name.equals(item.bookingItemAlias) || door.name.equals(item.doorId)) {
                manager.arxManager.doorAction(door.externalId, "forceOpen", false);
                manager.logEntry("Ran close on : " + door.externalId, bookingId, itemToClose);
                manager.arxManager.clearOverRideCredentials();
            }
        }
    }

    private void closeForTheDay() throws Exception {
        
        if (!manager.configuration.keepDoorOpenWhenCodeIsPressed) {
            return;
        }
        
        String closeAtEnd = manager.configuration.closeAllDoorsAfterTime;
        String[] time = closeAtEnd.split(":");
        int hour = new Integer(time[0]);
        int minute = new Integer(time[1]);
        
        String arxHostname = manager.configuration.arxHostname;
        String arxUsername = manager.configuration.arxUsername;
        String arxPassword = manager.configuration.arxPassword;
        
        
        Calendar cal = Calendar.getInstance();
        if(cal.get(Calendar.HOUR_OF_DAY) > hour) {
            manager.arxManager.overrideCredentials(arxHostname, arxUsername, arxPassword);
            manager.arxManager.closeAllForTheDay();
            manager.arxManager.clearOverRideCredentials();
        } else if(cal.get(Calendar.HOUR_OF_DAY) == hour && cal.get(Calendar.MINUTE) >= minute) {
            manager.arxManager.overrideCredentials(arxHostname, arxUsername, arxPassword);
            manager.arxManager.closeAllForTheDay();
            manager.arxManager.clearOverRideCredentials();
        } else {
            manager.arxManager.clearCloseForToday();
        }
    }

    private void makeSureCleaningsAreOkey() {
        manager.makeSureCleaningsAreOkay();
    }

    private List<AccessLog> makeLatestAccessLog(List<AccessLog> accessLogs) {
        HashMap<String, AccessLog> result = new HashMap();
        
        for(AccessLog log : accessLogs) {
            if(log.card != null && log.card.isEmpty()) {
                AccessLog current = result.get(log.card);
                if(current == null) {
                    current = log;
                    result.put(log.card, current);
                }
                
                if(log.timestamp > current.timestamp) {
                    result.put(log.card, log);
                }
            }
        }
        
        return new ArrayList(result.values());
    }

    private void checkForIncosistentBookings() {
        List<Booking> allBookings = manager.bookingEngine.getAllBookings();
        List<String> allBookingIds = new ArrayList();
        for(PmsBooking booking : manager.bookings.values()) {
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

}
