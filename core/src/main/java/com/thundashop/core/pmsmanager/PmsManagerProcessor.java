package com.thundashop.core.pmsmanager;

import com.ibm.icu.util.Calendar;
import com.thundashop.core.arx.AccessCategory;
import com.thundashop.core.arx.Card;
import com.thundashop.core.arx.Person;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
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
        processOrdersToCreate();
    }

    private void processStarting(int hoursAhead, int maxAhead) {
        List<PmsBooking> bookings = getAllConfirmedNotDeleted();
        for(PmsBooking booking : bookings) {
            
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
        if(room.guests.get(0).name.split(" ").length > 1) {
            person.lastName = room.guests.get(0).name.split(" ")[1];
        }
        
        if(manager.configuration.arxCardFormat == null || manager.configuration.arxCardFormat.isEmpty()) {
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
        String alias = manager.bookingEngine.getBookingItem(room.bookingItemId).bookingItemAlias;
        category.name = manager.bookingEngine.getBookingItem(room.bookingItemId).bookingItemName;
        if(alias != null && !alias.isEmpty()) {
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
        }catch(Exception e) {
            e.printStackTrace();
            manager.warnArxDown();
            return false;
        }
        return true;
    }

    private void processEndings(int hoursAhead, int maxAhead) {
        List<PmsBooking> bookings = getAllConfirmedNotDeleted();
        for(PmsBooking booking : bookings) {
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
        List<PmsBooking> bookings = getAllConfirmedNotDeleted();
        for(PmsBooking booking : bookings) {
            boolean save = false;
            for(PmsBookingRooms room : booking.rooms) {
                if(!manager.isClean(room.bookingItemId) && manager.configuration.cleaningInterval > 0) {
                    continue;
                }
                
                if(room.guests.isEmpty()) {
                    PmsGuests guest = new PmsGuests();
                    User user = manager.userManager.getUserById(booking.userId);
                    guest.name = user.fullName;
                    room.guests.add(guest);
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
            Integer newcode = new Random().nextInt(999999-100000)+100000;
            if(!codeExist(newcode)) {
                return newcode.toString();
            }
        }
        System.out.println("Tried 100 000 times to generate a code without success");
        return null;
    }

    private boolean codeExist(int newcode) {
        List<PmsBooking> bookings = getAllConfirmedNotDeleted();
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
        List<PmsBooking> bookings = getAllConfirmedNotDeleted();
        for(PmsBooking booking : bookings) {
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

    private void processOrdersToCreate() {
        List<PmsBooking> bookings = getAllConfirmedNotDeleted();
        for(PmsBooking booking : bookings) {
            if(booking.isEndedOverTwoMonthsAgo()) {
                continue;
            }
            
            if(booking.isEnded() && !manager.configuration.prepayment) {
                createEndingOrder(booking);
            } else {
                createPeriodeInvoices(booking);
            }
        }
    }

    private void createPeriodeInvoices(PmsBooking booking) {
        int type = booking.priceType;
        
        NewOrderFilter filter = new NewOrderFilter();
        filter.numberOfMonths = 1;
        if(type == 0) {
            return;
        }
        if(type != PmsBooking.PriceType.monthly) {
//            System.out.println("Not yet supporting other payment periods than months");
            return;
        }
        System.out.println("\t" + booking.invoicedTo);
        if(booking.invoicedTo.before(new Date())) {
            System.out.println("this one should be");
        }
        
        if(type == PmsBooking.PriceType.monthly) {
            filter.startInvoiceFrom = null;
            if(isAfterOrToday(booking.invoicedTo)) {
                if(manager.configuration.prepayment) {
                    filter.startInvoiceFrom = booking.invoicedTo;
                } else {
                    filter.startInvoiceFrom = substractOneMonth(booking.invoicedTo);
                }
                manager.createOrder(booking.id, filter);
                booking.invoicedTo = addOneMonth(booking.invoicedTo);
                manager.saveBooking(booking);
            }
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

    private Date substractOneMonth(Date invoicedTo) {
        Calendar toCheckCal = Calendar.getInstance();
        toCheckCal.setTime(invoicedTo);
        toCheckCal.add(Calendar.MONTH, -1);
        return toCheckCal.getTime();
    }

    private void createEndingOrder(PmsBooking booking) {
        Date endDate = booking.getEndDate();
        if(isSameDay(endDate, booking.invoicedTo)) {
            return;
        }
        
        NewOrderFilter filter = new NewOrderFilter();
        filter.numberOfMonths = 1;
        filter.startInvoiceFrom = booking.invoicedTo;
        manager.createOrder(booking.id, filter);
        booking.invoicedTo = endDate;
        manager.saveBooking(booking);
    }

    private List<PmsBooking> getAllConfirmedNotDeleted() {
        List<PmsBooking> res = manager.getAllBookings(null);
        List<PmsBooking> toRemove = new ArrayList();
        for(PmsBooking booking : res) {
            if(!booking.confirmed) {
//                toRemove.add(booking);
            }
            if(booking.isDeleted) {
//                toRemove.add(booking);
            }
        }
        res.removeAll(toRemove);
        return res;
    }
    
}
