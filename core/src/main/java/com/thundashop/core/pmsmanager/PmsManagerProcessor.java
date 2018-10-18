package com.thundashop.core.pmsmanager;

import com.ibm.icu.util.Calendar;
import com.thundashop.core.arx.AccessCategory;
import com.thundashop.core.arx.Card;
import com.thundashop.core.arx.Person;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.getshop.data.GetShopDevice;
import com.thundashop.core.getshop.data.GetShopLockCode;
import com.thundashop.core.getshoplocksystem.LockCode;
import com.thundashop.core.getshoplocksystem.LockGroup;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;

public class PmsManagerProcessor {
    private final PmsManager manager;
    private Date lastProcessed;
    private List<PmsBooking> cachedResult;
    private List<PmsBooking> cachedResult_includepaidfor;
    private long start;

    PmsManagerProcessor(PmsManager manager) {
        this.manager = manager;
    }

    public void doProcessing() {
        start = System.currentTimeMillis();
        clearCachedObject();
        checkTimer("Cleared cache");
        try { runAutoPayWithCard(); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("Autopay cards");
        clearCachedObject();
        checkTimer("Clear cache 2");
        try { autoMarkBookingsAsPaid(); }catch(Exception e) {manager.logPrintException(e); }
        checkTimer("autoMarkBookingsAsPaid");
        clearCachedObject(); 
        checkTimer("Clear cache 2");
        try { pingServers(); } catch(Exception e) {}
        checkTimer("pingServers");
        try { processAutoAssigning(); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("processAutoAssigning");
        try { processAutoDeletion(); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("processAutoDeletion");
        try { processLockSystem(); }catch(Exception e) {manager.logPrintException(e); }
        checkTimer("processLockSystem");
        try { sendPaymentLinkOnUnpaidBookings(); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("sendPaymentLinkOnUnpaidBookings");
        try { sendRecieptsOnCompletedPayments(); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("sendRecieptsOnCompletedPayments");
    }
    
    public void hourlyProcessor() {
        start = System.currentTimeMillis();
        try { processAutoExtend(); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("processAutoExtend");
        try { processIntervalCleaning(false); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("processIntervalCleaning(false)");
        try { processIntervalCleaning(true); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("processIntervalCleaning(true)");
        try { makeSureCleaningsAreOkey(); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("makeSureCleaningsAreOkey");
        try { checkForIncosistentBookings(); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("checkForIncosistentBookings");
        try { checkForRoomToClose(); }catch(Exception e) {manager.logPrintException(e); }
        checkTimer("checkForRoomToClose");
        try { updateInvoices(); }catch(Exception e) {manager.logPrintException(e); }
        checkTimer("updateInvoices");
        try { checkForDeadCodes(); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("checkForDeadCodes");
        try { warnOrderNotPaidFor(); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("warnOrderNotPaidFor");
    }
    
    public void processStartEndings() {
        try { processGreetingMessage(); }catch(Exception e) { manager.logPrintException(e); }
        
        checkTimer("processAutoAssigning");
        try { processStarting(-4, 0, false); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("processStarting (-4,0)");
        try { processStarting(0, 4, false); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("processStarting (0,4)");
        try { processStarting(4, 12, false); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("processStarting (4,12)");
        try { processStarting(12, 12 * 2, false); }catch(Exception e) { manager.logPrintException(e);  }
        checkTimer("processStarting (12,24)");
        try { processStarting(24, 24 * 2, false); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("processStarting (24,48)");
        try { processStarting(48, 24 * 3, false); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("processStarting (48,72)");
        try { processStarting(0, 12, true); }catch(Exception e) { manager.logPrintException(e);  }
        checkTimer("processStarting (0,12,true)");
        try { processStarting(12, 12 * 2, true); }catch(Exception e) { manager.logPrintException(e);  }
        checkTimer("processStarting (12,24,true)");
        try { processStarting(24, 24 * 2, true); }catch(Exception e) { manager.logPrintException(e);  }
        checkTimer("processStarting (24,48,true)");
        try { processStarting(48, 24 * 3, true); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("processStarting (48,72,true)");
        try { processEndings(0, 24 * 1); }catch(Exception e) { manager.logPrintException(e);  }
        checkTimer("processEndings (0,24,true)");
        try { processEndings(24, 24 * 2); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("processEndings (24,48,true)");
        try { processEndings(48, 24 * 3); }catch(Exception e) { manager.logPrintException(e); }
        checkTimer("processEndings (48,72,true)");
    }
    

    private void processStarting(int hoursAhead, int maxAhead, boolean started) {
        int hoursAheadCheck = hoursAhead;
        if(hoursAheadCheck == 0) {
            //Send sms even though the event has started, 12 hours later.
            hoursAheadCheck = -12;
        }
        
        int maxAheadCheck = maxAhead;
        if(manager.getConfigurationSecure().ignoreTimeIntervalsOnNotification && !started) {
            hoursAheadCheck = -24;
        }
        List<PmsBooking> bookings = null;
        if(manager.getConfigurationSecure().sendMessagesRegardlessOfPayments) {
            bookings = getAllConfirmedNotDeleted(true);
        } else {
            bookings = getAllConfirmedNotDeleted(false);
        }
        for (PmsBooking booking : bookings) {
            if(booking.isEnded()) {
                continue;
            }
            boolean save = false;
            
            for (PmsBookingRooms room : booking.getActiveRooms()) {
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
                String key = "room_starting_" + maxAhead + "_hours";
                if(started) {
                    key = "room_started_" + maxAhead + "_hours";
                }
                if (room.notificationsSent.contains(key)) {
                    continue;
                }
                booking = manager.finalize(booking);
                save = true;
                manager.doNotificationFromProcessor(key, booking, room);
                room.notificationsSent.add(key);
            }
            if (save) {
                manager.saveBooking(booking);
            }

        }
    }

    private boolean pushToLock(PmsBookingRooms room, boolean deleted) {
        PmsConfiguration config = manager.getConfigurationSecure();
        try {
            PmsBooking booking = manager.getBookingFromRoomSecure(room.pmsBookingRoomId);
            if(deleted) {
                String isExpired = room.isEnded() ? "yes" : "no";
                String isStarted = room.isStarted() ? "yes" : "no";
                String blocked = room.blocked ? "yes" : "no";
                String deletedText = room.isDeleted() ? "yes" : "no";
                String startEndText = "";
                if(room.date != null && room.date.start != null && room.date.end != null) {
                    startEndText = room.date.start + " - " + room.date.end;
                }
                manager.logEntry("Removing code from lock, code (" + room.code + "), expired: <b>" + isExpired + "</b> started: <b>" + isStarted + "</b> blocked: <b>" + blocked + "</b> deleted: <b>" + deletedText+"</b> startend: <b>"+startEndText + "</b>", booking.id, room.bookingItemId);
            } else {
                manager.logEntry("Getting code from lock.", booking.id , room.bookingItemId);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        
        if (isApacSolutionActivated()) {
            return handleGetShopLockSystemCodes(room, deleted);
        } 
        
        if (config.getDefaultLockServer().locktype.isEmpty() || config.getDefaultLockServer().locktype.equals("arx")) {
            if(!deleted) {
                //Make sure everything is 100% updated.
                pushToArx(room, true);
            }
            return pushToArx(room, deleted);
        } else if(config.isGetShopHotelLock()) {
            return fetchFromGetshopHotelLock(room, deleted);
        } else {
            return pushToGetShop(room, deleted);
        }
    }

    private void processEndings(int hoursAhead, int maxAhead) {
        List<PmsBooking> bookings = null;
        if(manager.getConfigurationSecure().sendMessagesRegardlessOfPayments) {
            bookings = getAllConfirmedNotDeleted(true);
        } else {
            bookings = getAllConfirmedNotDeleted(false);
        }
        for (PmsBooking booking : bookings) {
            boolean save = false;
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                if (!isBetween(room.date.end, (maxAhead * -1), (hoursAhead * -1))) {
                    continue;
                }
                if (!room.isEnded()) {
                    continue;
                }
                String key = "room_ended_" + hoursAhead + "_hours";
                if (room.notificationsSent.contains(key)) {
                    continue;
                }
                booking = manager.finalize(booking);
                save = true;
                manager.doNotificationFromProcessor(key, booking, room);
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

    private void processLockSystem() {
        boolean oldLockSystemActive = manager.getConfigurationSecure().getDefaultLockServer().arxHostname != null && !manager.getConfigurationSecure().getDefaultLockServer().arxHostname.isEmpty();
        
        if (!oldLockSystemActive && !isApacSolutionActivated()) { 
            return;
        }
        try {
            manager.checkDoorStatusControl();
        }catch(Exception e) {
            manager.logPrintException(e);
        }
        
        List<PmsBooking> bookings = getAllConfirmedNotDeleted(true);
        if(manager.storeManager.getStoreId().equals("7f2c47a4-7ec9-41e2-a070-1e9e8fcf4e38")) {
            bookings = getBookingsNeedsToBeChecked(bookings);
        }
        for (PmsBooking booking : bookings) {
            if(!booking.confirmed) {
                continue;
            }
            boolean save = false;
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                if(!room.forceUpdateLocks) {
                    if(room.addedToArx) {
                        continue;
                    }
                    if (!manager.isClean(room.bookingItemId) && manager.getConfigurationSecure().cleaningInterval > 0) {
                        continue;
                    }
                    if(room.blocked) {
                        continue;
                    }
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
                
                
                //If it is possible to let customers check in earlier than specified, do it.
                int hourNow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                int boardingHour = manager.getConfigurationSecure().getBoardingHour();
                if(boardingHour >= 0) {
                    boolean boardingStarted = (hourNow >= boardingHour);
                    if(!room.isStarted() && boardingStarted && room.isStartingToday()) {
                        Calendar startCal = Calendar.getInstance();
                        startCal.setTime(room.date.start);
                        if(startCal.get(Calendar.HOUR_OF_DAY) > boardingHour) {
                            try {
                                manager.bookingEngine.changeDatesOnBooking(room.bookingId, new Date(), room.date.end);
                                manager.finalize(booking);
                            }catch(Exception e) {

                            }
                        }
                    }
                }
                 
               if (room.isStarted() && !room.isEnded()) {
                    boolean payedfor = manager.pmsInvoiceManager.isRoomPaidFor(room.pmsBookingRoomId);
                    boolean grantEven = manager.getConfigurationSecure().grantAccessEvenWhenNotPaid;
                    if(!payedfor && (grantEven && booking.isBookedAfterOpeningHours()) || room.forceAccess) {
                        payedfor = true;
                    }
                    if(payedfor && !room.deleted) {
                        if (pushToLock(room, false)) {
                            room.addedToArx = true;
                            room.forceAccess = false;
                            manager.markRoomAsDirty(room.bookingItemId);
                            save = true;
                            room.forceUpdateLocks = false;
                            if(notifyRoomAddedToArx(room.cardformat)) {
                                manager.doNotificationFromProcessor("room_added_to_arx", booking, room);
                            }
                        }
                    }
                }
            }
            
            //Also deleted rooms needs to be removed from arx.
            for (PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
                if(!room.addedToArx) {
                    continue;
                }
                boolean isRenaTreningNotPaid = (manager.storeManager.getStoreId().equals("cd94ea1c-01a1-49aa-8a24-836a87a67d3b") && !manager.pmsInvoiceManager.isRoomPaidFor(room.pmsBookingRoomId));
                if (room.isEnded() || !room.isStarted() || room.deleted || isRenaTreningNotPaid || room.blocked) {
                    if (pushToLock(room, true)) {
                        room.addedToArx = false;
                        save = true;
                        manager.doNotificationFromProcessor("room_removed_from_arx", booking, room);
                    }
                }
            }
            
            if (save) {
                manager.saveBooking(booking);
            }
        }
    }

    private void processAutoAssigning() {
        List<PmsBooking> bookings = getAllConfirmedNotDeleted(true);
        List<PmsBookingRooms> roomsToAssing = new ArrayList();
        for (PmsBooking booking : bookings) {
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                if (!room.isStartingToday() && !room.isStarted()) {
                    continue;
                }
                if (room.isEnded()) {
                    continue;
                }
                if (room.bookingItemId == null || room.bookingItemId.isEmpty()) {
                    roomsToAssing.add(room);
                }
            }
        }
        
        Collections.sort(roomsToAssing, new Comparator<PmsBookingRooms>(){
            public int compare(PmsBookingRooms o1, PmsBookingRooms o2){
                if(o1.date.start == null || o2.date.start == null)
                    return 0;
                return o1.date.start.compareTo(o2.date.start);
            }
       });
        
        for(PmsBookingRooms room : roomsToAssing) {
            if(room.recentlyChangedBookingItem()) {
                continue;
            }
            PmsBooking booking = manager.getBookingFromRoomSecure(room.pmsBookingRoomId);
            manager.autoAssignItem(room);
            manager.finalize(booking);
            manager.saveBooking(booking);
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

    private List<PmsBooking> getAllConfirmedNotDeleted(boolean includeNotPaidFor) {
        if(includeNotPaidFor && cachedResult_includepaidfor != null) {
            return cachedResult_includepaidfor;
        }
        if(!includeNotPaidFor && cachedResult != null) {
            return cachedResult;
        }
        
        List<PmsBooking> res = new ArrayList(manager.getBookingMap().values());
        List<PmsBooking> toRemove = new ArrayList();
        for (PmsBooking booking : res) {
            boolean ignoreNotPaidFor = checkIgnorePaidFor(booking);
            if (booking.getActiveRooms() == null) {
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
            if (!booking.payedFor && !includeNotPaidFor) {
                if(!ignoreNotPaidFor) {
                    toRemove.add(booking);
                }
            }
        }
        res.removeAll(toRemove);
        
        if(includeNotPaidFor) {
            cachedResult_includepaidfor = res;
        } else {
            cachedResult = res;
        }
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

        if (manager.getConfigurationSecure().getDefaultLockServer().arxCardFormat == null || manager.getConfigurationSecure().getDefaultLockServer().arxCardFormat.isEmpty()) {
            manager.logPrint("Card format not set yet");
            return false;
        }

        Card card = new Card();
        if(room.cardformat != null && !room.cardformat.isEmpty()) {
            card.format = room.cardformat;
        } else {
            room.cardformat = manager.getConfigurationSecure().getDefaultLockServer().arxCardFormat;
        }
        card.cardid = room.code;

        person.cards.add(card);
        person.id = room.pmsBookingRoomId;
        person.deleted = deleted;

        AccessCategory category = new AccessCategory();
        BookingItem item = manager.bookingEngine.getBookingItem(room.bookingItemId);
        if (item == null) {
            manager.logPrint("Not able to push to arx, item does not exists");
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
            manager.doorManager.updatePerson(person);
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
            List<PmsBooking> bookings = getAllConfirmedNotDeleted(true);
            for (PmsBooking booking : bookings) {
                boolean needSaving = false;
                for (PmsBookingRooms room : booking.getActiveRooms()) {
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
                            if(res == null && (room.warnedAboutAutoExtend == null || !room.isSameDay(room.warnedAboutAutoExtend, new Date()))) {
                                text = "Not able to extend stay for room: " + item.bookingItemName;
                                manager.warnAboutUnableToAutoExtend(room, item,"Not able to extend");
                                room.warnedAboutAutoExtend = new Date();
                                needSaving = true;
                                
                                text += " (" + start + " to " + end + ")";
                                manager.logEntry(text, booking.id, room.bookingItemId);
                                continue;
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

        List<PmsBooking> bookings = getAllConfirmedNotDeleted(false);

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
                if(booking.isEnded()) {
                    continue;
                }
                
                boolean needSaving = false;
                for (PmsBookingRooms room : booking.getActiveRooms()) {

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
        if(manager.getConfigurationSecure().cleaningInterval == 0) {
            return;
        }
        manager.makeSureCleaningsAreOkay();
    }


    private void checkForIncosistentBookings() {
        List<Booking> allBookings = manager.bookingEngine.getAllBookings();
        List<String> allBookingIds = new ArrayList();
        for(PmsBooking booking : manager.getBookingMap().values()) {
            if(booking.isDeleted) {
                continue;
            }
            if(booking.getActiveRooms() != null) {
                for(PmsBookingRooms room : booking.getActiveRooms()) {
                    if(room.bookingId != null) {
                        allBookingIds.add(room.bookingId);
                    }
                }
            }
        }
        
        for(Booking test : allBookings) {
            if(test.source != null && !test.source.isEmpty()) {
                continue;
            }
            if(!allBookingIds.contains(test.id)) {
                manager.messageManager.sendErrorNotification(test.id + "where found in the booking engine but not in the pms manager, the booking engine and the pms manager is out of sync: " + test.startDate + " - " + test.endDate + ", created: " + test.rowCreatedDate, null);
                manager.bookingEngine.deleteBooking(test.id);
            }
        }
    }

    private void autoMarkBookingsAsPaid() {
        List<PmsBooking> all = getAllConfirmedNotDeleted(true);
        PmsConfiguration config = manager.getConfiguration();
        for(PmsBooking booking : all) {
            if(booking.sessionId != null && !booking.sessionId.isEmpty()) {
                continue;
            }
            
            if(booking.isEndedOverTwoMonthsAgo()) {
                //Ended bookings are not relevant anymore.
                continue;
            }

            if(booking.orderCreatedAfterStay != null) {
                continue;
            }
            
            if(booking.isDeleted || booking.getActiveRooms().isEmpty()) {
                continue;
            }
            
            if(config.getRequirePayments() && booking.createOrderAfterStay && booking.isEnded()) {
                NewOrderFilter filter = new NewOrderFilter();
                filter.createNewOrder = true;
                filter.endInvoiceAt = booking.getEndDate();
                manager.pmsInvoiceManager.createOrder(booking.id, filter);
                booking.orderCreatedAfterStay = new Date();
                manager.saveBooking(booking);
            }
            boolean needSaving = false;
            boolean payedfor = true; 
            boolean firstDate = true;
            if(config.getRequirePayments()) {
                boolean needCapture = false;
                for(String orderId : booking.orderIds) {
                    Order order = manager.orderManager.getOrderSecure(orderId);
                    if(order == null) {
                        continue;
                    }
                    if(order.payment != null && order.payment.paymentType != null && 
                            order.payment.paymentType.toLowerCase().contains("invoice")) {
                        manager.pmsInvoiceManager.autoSendInvoice(order, booking.id);
                        continue;
                    }
                    if(order.payment != null && order.payment.paymentType != null && 
                            order.payment.paymentType.toLowerCase().contains("samlefaktura")) {
                        manager.pmsInvoiceManager.autoSendInvoice(order, booking.id);
                        continue;
                    }
                    if(!order.captured && order.status == Order.Status.PAYMENT_COMPLETED) {
                        needCapture = true;
                    }
                    
                    double total = manager.orderManager.getTotalAmount(order);
                    if(total <= 0.0 && !order.hasFreezeItem()) {
                        continue;
                    }
                    if(order.status != Order.Status.PAYMENT_COMPLETED || order.hasFreezeItem()) {
                        for(CartItem item : order.cart.getItems()) {
                            if(!firstDate && item.startDate != null && item.startDate.after(new Date())) {
                                //Only set payedfor=false when order is started.
                                continue;
                            }
                            if(!firstDate && item.endDate != null && item.endDate.before(new Date())) {
                                //Only set payedfor=false when order has not been ended.
                                continue;
                            }
                            firstDate = false;
                            payedfor = false;
                        }
                    }
                }

                if(booking.needCapture != needCapture) {
                    booking.needCapture = needCapture;
                    needSaving = true;
                }
                if((config.getMarkBookingsWithNoOrderAsUnpaid() && config.getPrepayment() && booking.orderIds.isEmpty())) {
                    payedfor = false;
                }
                if(booking.createOrderAfterStay) {
                    payedfor = true;
                }
                if(booking.testReservation) {
                    payedfor = true;
                }
                
                try {
                    booking.calculateTotalCost();
                    double totalPrice = booking.getTotalPrice();
                    if(totalPrice == 0.0) {
                        payedfor = true;
                    }
                }catch(Exception e) {}

            }
            boolean isBookingExternalChannel = (booking.channel != null && !booking.channel.isEmpty());
            if(isBookingExternalChannel && booking.channel.equals("terminal")) {
                isBookingExternalChannel = false;
            }
            boolean forceSend = isBookingExternalChannel;
            if(!manager.getConfigurationSecure().autoDeleteUnpaidBookings && !manager.storeManager.isPikStore()) {
                forceSend = true;
            }
            if(manager.getConfigurationSecure().ignorePaymentWindowDaysAheadOfStay > 0) {
                Date start = booking.getStartDate();
                int daysBetween = (int)( (start.getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24));
                if(daysBetween >= manager.getConfigurationSecure().ignorePaymentWindowDaysAheadOfStay) {
                    forceSend = true;
                }
            }
            
            if(booking.payedFor != payedfor) {
                booking.payedFor = payedfor;
                needSaving = true;
            }
            if(booking.isRegisteredToday() && !booking.hasSentNotification("booking_completed")) {
                if((payedfor == true || forceSend) && (booking.orderIds.size() == 1 || booking.createOrderAfterStay)) {
                    if(!booking.isSynxis()) {
                        manager.doNotificationFromProcessor("booking_completed", booking, null);
                        needSaving = true;
                    }
                }
            }
            if(booking.payedFor && !booking.avoidAutoDelete) {
                booking.avoidAutoDelete = true;
                needSaving = true;
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
        int daysToWarn = manager.getConfigurationSecure().warnWhenOrderNotPaidInDays;
        manager.orderManager.checkForOrdersToAutoPay(manager.getConfigurationSecure().numberOfDaysToTryToPayWithCardAfterStayOrderHasBeenCreated);
        List<PmsBooking> bookings = getAllConfirmedNotDeleted(true);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, daysToWarn);
        Date threeDaysAhead = cal.getTime();
        for(PmsBooking booking : bookings) {
            if(booking.isEnded()) {
                continue;
            }
            boolean needSaving = false;
            for(String orderId : booking.orderIds) {
                if(booking.orderIds.size() <= 1) {
                    //No need to warning first order.
                    continue;
                }
                Order order = manager.orderManager.getOrderSecure(orderId);
                if(order.status == Order.Status.PAYMENT_COMPLETED) {
                    continue;
                }
                manager.setOrderIdToSend(order.id);
                for(CartItem item : order.cart.getItems()) {
                    if(item.startDate == null) {
                        continue;
                    }
                    if(threeDaysAhead.after(item.startDate)) {
                        String key = order.id + "_order_unabletopaywithsavecardwarning";
                        if(!booking.notificationsSent.contains(key)) {
                            manager.doNotificationFromProcessor("order_unabletopaywithsavecardwarning", booking, null);
                            needSaving = true;
                            booking.notificationsSent.add(key);
                            manager.saveBooking(booking);
                        }
                    }
                    if(new Date().after(item.startDate)) {
                        String key = order.id + "_order_unabletopaywithsavecard";
                        if(!booking.notificationsSent.contains(key)) {
                            manager.doNotificationFromProcessor("order_unabletopaywithsavecard", booking, null);
                            needSaving = true;
                            booking.notificationsSent.add(key);
                            manager.saveBooking(booking);
                        }
                    }
                }
            }
            if(needSaving) {
                manager.saveBooking(booking);
            }
        }
    }

    private void clearCachedObject() {
        cachedResult_includepaidfor = null;
        cachedResult = null;
    }

    private boolean fetchFromGetshopHotelLock(PmsBookingRooms room, boolean deleted) {
        BookingItem item = manager.bookingEngine.getBookingItem(room.bookingItemId);
        if (item == null) {
            manager.logPrint("Not able to fetch code from getshop hotel lock, no lock is connected to the room");
            return false;
        }
        
        if(deleted) {
            manager.getShopLockManager.removeCodeOnLock(item.bookingItemAlias, room);
        } else {
            room.code = "";
            room.code = manager.getShopLockManager.getCodeForLock(item.bookingItemAlias);
            room.addedToArx = true;
            PmsBooking booking = manager.getBookingFromRoomSecure(room.pmsBookingRoomId);
            manager.saveBooking(booking);
        }
        manager.getShopLockManager.checkAndUpdateSubLocks();
        
        return true;
    }

    private boolean notifyRoomAddedToArx(String format) {
        if(manager.getConfigurationSecure().getDefaultLockServer().arxCardFormatsAvailable != null && !manager.getConfigurationSecure().getDefaultLockServer().arxCardFormatsAvailable.isEmpty()) {
            String[] splitted = manager.getConfigurationSecure().getDefaultLockServer().arxCardFormatsAvailable.split(";");
            if(splitted == null || splitted.length == 1) {
                return true;
            }
            if(splitted[0].equals(format)) {
                return true;
            }
            return false;
        }
        return true;
    }

    private boolean checkIgnorePaidFor(PmsBooking booking) {
        if(booking.hasForcedAccessedRooms()) {
            return true;
        }
        
        if(booking.channel == null || booking.channel.isEmpty()) {
            return false;
        }
        
        if(!booking.isBookedAfterOpeningHours()) {
            return false;
        }
        
        PmsChannelConfig config = manager.getConfigurationSecure().getChannelConfiguration(booking.channel);
        if(config == null) {
            return false;
        }
        
        if(config.ignoreUnpaidForAccess) {
            return true;
        }
        
        return false;
    }

    private void checkForRoomToClose() {
        manager.checkForRoomsToClose();
    }

    private void processAutoDeletion() {
        long start = System.currentTimeMillis();
        PmsConfiguration configuration = manager.getConfigurationSecure();
        if(!configuration.autoDeleteUnpaidBookings) {
            return;
        }
        
        List<PmsBooking> allNotDeleted = getAllConfirmedNotDeleted(true);
        for(PmsBooking booking : allNotDeleted) {
            if(booking.id.equals("66a3109d-4fb5-4ec7-89ab-e782b6efb5a7")) {
                System.out.println("check");
            }
            if(booking.payedFor) {
                continue;
            }
            if(booking.channel != null && !booking.channel.isEmpty()) {
                continue;
            }
            if(booking.bookedByUserId != null && !booking.bookedByUserId.isEmpty()) {
                continue;
            }
            if(booking.orderIds.size() > 1) {
                continue;
            }
            if(!booking.isOld(90)) {
                continue;
            }
            if(booking.isOld(100)) {
                continue;
            }
            if(booking.transferredToLock()) {
                continue;
            }
            if(booking.getActiveRooms().isEmpty()) {
                continue;
            }
            
            if(configuration.ignorePaymentWindowDaysAheadOfStay > 0) {
                Date startDate = booking.getStartDate();
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, configuration.ignorePaymentWindowDaysAheadOfStay);
                if(cal.getTime().before(startDate)) {
                    continue;
                }
            }
            
            manager.removeAllUnclosedOrders(booking.id);
            booking = manager.getBooking(booking.id);
            if(!booking.orderIds.isEmpty()) {
                return;
            }
            
            manager.logEntry("Autodeleted because it has expired.", booking.id, null);
            manager.deleteBooking(booking.id);
        }
    }

    private void sendPaymentLinkOnUnpaidBookings() {
        if(!manager.getConfigurationSecure().autoSendPaymentReminder) {
            return;
        }
        
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        
        int days = manager.getConfigurationSecure().numberOfDaysToSendPaymentLinkAheadOfStay;
        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.filterType = "checkin";
        filter.startDate = cal.getTime();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, days);
        filter.endDate = cal.getTime();
        filter.normalizeStartEndDateByBeginningEndOfDay();
        
        List<PmsBooking> bookingsCheckingIn = manager.getAllBookings(filter);
        List<PmsBooking> nonRefBookings = getLatestNonRefBookings();
        checkTimer("Get all bookings to check for non paid");
        bookingsCheckingIn.addAll(nonRefBookings);
        
        for(PmsBooking book : bookingsCheckingIn) {
            if(hour < 10 && !book.isRegisteredToday()) {
                continue;
            }            
            if(book.payedFor) {
                continue;
            }
            if(book.isDeleted) {
                continue;
            }
            if(book.getActiveRooms().isEmpty()) {
                continue;
            }
            
            if(book.isRegisteredToday() && (book.channel == null || book.channel.isEmpty()) && manager.getConfigurationSecure().autoDeleteUnpaidBookings) {
                continue;
            }
            
            if(book.isRegisteredToday() && (book.channel == null || book.channel.isEmpty()) && !manager.getConfigurationSecure().autoDeleteUnpaidBookings) {
                if(!book.isOld(30)) {
                    continue;
                }
            }
            
            if(book.hasForcedAccessedRooms()) {
                continue;
            }
            
            for(String orderId : book.orderIds) {
                Order order = manager.orderManager.getOrder(orderId);
                if(order.avoidAutoSending) {
                    continue;
                }
                if(order.status == Order.Status.PAYMENT_COMPLETED) {
                   continue; 
                }
                if(order.payment != null && order.payment.paymentType != null && 
                        (!order.payment.paymentType.toLowerCase().contains("dibs") &&
                        !order.payment.paymentType.toLowerCase().contains("netaxept") &&
                        !order.payment.paymentType.toLowerCase().contains("epay"))) {
                    continue;
                }
                
                String key = "autosendmissingpayment_" + book.id;
                if(order.attachedToRoom != null && !order.attachedToRoom.isEmpty()) {
                    PmsBookingRooms room = book.getRoom(order.attachedToRoom);
                    if(!room.date.start.before(cal.getTime())) {
                        continue;
                    }
                    
                    if(order.recieptEmail == null || order.recieptEmail.isEmpty()
                            && room.guests != null && !room.guests.isEmpty()) {
                        //Make sure it asks for the correct person to send email to.
                        order.recieptEmail = room.guests.get(0).email;
                        manager.orderManager.saveOrder(order);
                    }
                    
                    key = key + "_" + order.id;
                }
                
                if(order.payment.paymentType.toLowerCase().contains("dibsautocollect")) {
                    if(order.tryAutoPayWithDibs == null) {
                        order.tryAutoPayWithDibs = new Date();
                        manager.orderManager.saveOrder(order);
                        manager.getShop.addToDibsAutoCollect(order.id, manager.storeManager.getStoreId());
                    }
                    continue;
                }
                
                if(book.notificationsSent.contains(key)) {
                    continue;
                }

                manager.sendMissingPayment(orderId, book.id);
                book.notificationsSent.add(key);
                manager.saveBooking(book);
            }
        }
    }

    private void updateInvoices() {
        PmsConfiguration config = manager.getConfigurationSecure();
        if(!config.autoMarkCreditNotesAsPaidFor && !config.automarkInvoicesAsPaid) {
            return;
        }
        
        List<Order> orders = manager.orderManager.getOrders(null, null, null);
        for(Order ord : orders) {
            if(ord.createdOnDay(new Date())) {
                continue;
            }
            if(ord.status != Order.Status.PAYMENT_COMPLETED && config.autoMarkCreditNotesAsPaidFor && manager.orderManager.getTotalAmount(ord) < 0) {
                ord.status = Order.Status.PAYMENT_COMPLETED;
                ord.captured = true;
                ord.payment.captured = true;
                manager.orderManager.saveOrder(ord);
            }
            if(ord.isInvoice() && ord.status != Order.Status.PAYMENT_COMPLETED && config.automarkInvoicesAsPaid) {
                ord.status = Order.Status.PAYMENT_COMPLETED;
                ord.captured = true;
                ord.payment.captured = true;
                manager.orderManager.saveOrder(ord);
            }
        }
    }

    private void checkForDeadCodes() {
        
        if(manager.getStoreId().equals("7f2c47a4-7ec9-41e2-a070-1e9e8fcf4e38")) {
            return;
        }
        
        if (isApacSolutionActivated() && manager.getStoreId().equals("a152b5bd-80b6-417b-b661-c7c522ccf305") && manager.getBookingMap().size() > 1000) {
            manager.checkForDeadCodesApac();
            return;
        }
        
        if(!manager.getConfigurationSecure().isGetShopHotelLock()) {
            return;
        }
        
        List<PmsBooking> bookings = manager.getAllBookingsFlat();
        List<GetShopDevice> allLocks = manager.getShopLockManager.getAllLocks(null);
        for(GetShopDevice lock : allLocks) {
            if(!lock.isLock()) {
                continue;
            }
            boolean lockNeedUpdate = false;
            for(GetShopLockCode code : lock.codes.values()) {
                if(code.inUse() && code.usedBySource.isEmpty()) {
                    boolean found = false;
                    String codeToCheck = code.fetchCode();
                    for(PmsBooking booking : bookings) {
                        if(booking.isDeleted) { continue; }
                        for(PmsBookingRooms room : booking.rooms) {
                            if(room.isEnded() || room.isDeleted()) {
                                continue;
                            }
                            if(room.code.equals(codeToCheck)) {
                                found = true;
                            }
                        }
                    }
                    if(!found) {
                        manager.logPrint("Dead code found for device: " + lock.name + " - " + lock.zwaveid + " - " + code.fetchCodeToAddToLock() + " slot: " + code.slot);
                        code.refreshCode();
                        lockNeedUpdate = true;
                    }
                }
            }
            if(lockNeedUpdate) {
                manager.getShopLockManager.saveLock(lock);
            }
        }
    }

    private List<PmsBooking> getLatestNonRefBookings() {
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.filterType = "registered";
        filter.endDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, -6);
        filter.startDate = cal.getTime();
        
        List<PmsBooking> latestBookings = manager.getAllBookings(filter);
        List<PmsBooking> result = new ArrayList();
        
        for(PmsBooking booking : latestBookings) {
            if(booking.hasNoRefRooms()) {
                result.add(booking);
            }
        }
        return result;
    }

    private void pingServers() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -240);
        Date anHourAgo = cal.getTime();
        
        PmsConfiguration config = manager.getConfigurationSecure();
        for(String name : config.lockServerConfigs.keySet()) {
            PmsLockServer server = config.lockServerConfigs.get(name);
            if(server.isGetShopHotelLock() || server.isGetShopLockBox()) {
                PingServerThread pthread = new PingServerThread(server);
                pthread.start();
            } else {
                server.lastPing = new Date();
            }
            if(manager.recentlyStarter()) {
                continue;
            }
            if(anHourAgo.after(server.lastPing)) {
                if(!server.beenWarned) {
                    server.beenWarned = true;
                    manager.messageManager.sendErrorNotification("Lost connection with server: " + server.arxHostname + " lost connection at: " + server.lastPing + ") name: " + name, null);
                    manager.saveConfiguration(config);
                }
            } else {
                if(server.beenWarned) {
                    server.beenWarned = false;
                    manager.messageManager.sendErrorNotification("Connection to server : " + server.arxHostname + " reestablished, name: " + name + ", where down since: " + server.lastPing, null);
                    manager.saveConfiguration(config);
                }
            }
        }
    }

    private void warnOrderNotPaidFor() {
        PmsConfiguration config = manager.getConfigurationSecure();
        if(config.warnIfOrderNotPaidFirstTimeInHours <= 0) {
            return;
        }
        
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.filterType = "registered";
        filter.endDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, -36);
        filter.startDate = cal.getTime();
        
        
        List<PmsBooking> activeBookings = manager.getAllBookings(filter);
        cal = Calendar.getInstance();
        Calendar toCheck = Calendar.getInstance();
        toCheck.add(Calendar.HOUR_OF_DAY, config.warnIfOrderNotPaidFirstTimeInHours * -1);
        Date checkDate = toCheck.getTime();
        
        for(PmsBooking booking : activeBookings) {
            if(booking.orderIds.size() > 1) {
                continue;
            }
            if(booking.hasForcedAccessedRooms()) {
                continue;
            }
            for(String orderId : booking.orderIds) {
                Order order = manager.orderManager.getOrder(orderId);
                if(order.warnedNotPaid) {
                    continue;
                }
                cal.setTime(order.rowCreatedDate);
                if(order.status == Order.Status.PAYMENT_COMPLETED) {
                    continue;
                }
                if(order.isInvoice()) {
                    continue;
                }
                if(checkDate.after(order.rowCreatedDate)) {
                    order.warnedNotPaid = true;
                    manager.orderManager.saveOrder(order);
                    manager.doNotificationFromProcessor("warnfirstordernotpaid", booking, null);
                }
            }
        }
    }

    private void checkTimer(String text) {
        long diff = System.currentTimeMillis() - start;
        if(diff > 50) {
//            manager.logPrint("Processor:" + diff + " : " + text);
        }
        start = System.currentTimeMillis();
    }
    
    private void checkTimerInner(String text) {
        long diff = System.currentTimeMillis() - start;
//        manager.logPrint("\t Processor inner:" + diff + " : " + text);
        start = System.currentTimeMillis();
    }

    private boolean handleGetShopLockSystemCodes(PmsBookingRooms room, boolean deleted) {
        if (room.bookingItemId == null || room.bookingItemId.isEmpty()) {
            return false;
        }
        
        BookingItem item = manager.bookingEngine.getBookingItem(room.bookingItemId);
        
        if (item.lockGroupId == null || item.lockGroupId.isEmpty()) {
            return false;
        }
        
        boolean updated = false;
        if(deleted) {
            if (room.codeObject != null) {
                manager.getShopLockSystemManager.renewCodeForSlot(item.lockGroupId, room.codeObject.slotId);
            }
            room.removeCode();    
            updated = true;
        } else {        
            LockCode nextUnusedCode = manager.getShopLockSystemManager.getNextUnusedCode(item.lockGroupId, room.pmsBookingRoomId, getClass().getSimpleName(), "Automatically assigned by PMS processor");
            if (nextUnusedCode != null) {
                room.code = ""+nextUnusedCode.pinCode;
                room.addedToArx = true;
                room.codeObject = nextUnusedCode;
                LockCode clonedCode = nextUnusedCode.clone();
                if (clonedCode != null) {
                    room.codeObjectHistory.add(clonedCode);
                }
                updated = true;
            }
        }
        
        PmsBooking booking = manager.getBookingFromRoomSecure(room.pmsBookingRoomId);
        manager.saveBooking(booking);
        
        return updated;
    }

    private boolean isApacSolutionActivated() {
        return manager.getShopLockSystemManager.isActivated();
    }

    private void processGreetingMessage() {
        Calendar cal = Calendar.getInstance();
        int hourofday = cal.get(Calendar.HOUR_OF_DAY);
        if(hourofday != 7) {
            return;
        }
        
        List<PmsBooking> bookings = getAllConfirmedNotDeleted(true);
        for (PmsBooking booking : bookings) {
            if(booking.isEnded()) {
                continue;
            }
            boolean save = false;
            String key = "room_morning_message";
            
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                String greetingMessage = "room_morning_message";
                if(room.isStartingToday() && !room.notificationsSent.contains(greetingMessage)) {
                    manager.doNotificationFromProcessor(key, booking, room);
                    room.notificationsSent.add(key);
                    save = true;
                }
            }
            if(save) {
                manager.saveBooking(booking);
            }
        }
    }

    private List<PmsBooking> getBookingsNeedsToBeChecked(List<PmsBooking> bookings) {
        List<PmsBooking> toCheck = new ArrayList();
        
        java.util.Calendar end = java.util.Calendar.getInstance();
        java.util.Calendar start = java.util.Calendar.getInstance();
        start.add(Calendar.HOUR_OF_DAY, -30);
        end.add(Calendar.HOUR_OF_DAY, 30);
        for(PmsBooking booking : bookings) {
            boolean found = false;
            for(PmsBookingRooms r : booking.getAllRoomsIncInactive()) {
                if(found) { continue; }
                if(r.addedToArx) {
                    toCheck.add(booking);
                    found = true;
                } else if(r.startingBetween(start.getTime(), end.getTime())) {
                    toCheck.add(booking);
                    found = true;
                }
            }
        }
        return toCheck;
    }

    private void sendRecieptsOnCompletedPayments() {
        if(!manager.isActive() || manager.hasNoBookings()) {
            return;
        }
//        
//        List<String> ordersToAutosend = manager.orderManager.getOrdersToAutoSend();
//        for(String orderId : ordersToAutosend) {
//            PmsBooking booking = manager.getBookingWithOrderId(orderId);
//            if(booking != null) {
//                Order order = manager.orderManager.getOrder(orderId);
//                manager.pmsInvoiceManager.sendRecieptOnOrder(order, booking.id);
//            }
//        }
    }



}
