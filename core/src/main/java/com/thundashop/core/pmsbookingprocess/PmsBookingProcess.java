/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsbookingprocess;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.pmsmanager.PmsAdditionalTypeInformation;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingAddonItem;
import com.thundashop.core.pmsmanager.PmsBookingDateRange;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsGuests;
import com.thundashop.core.pmsmanager.PmsInvoiceManager;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@GetShopSession
public class PmsBookingProcess extends GetShopSessionBeanNamed implements IPmsBookingProcess {

    @Autowired
    BookingEngine bookingEngine;
    
    @Autowired
    PmsManager pmsManager;
    
    @Autowired
    PmsInvoiceManager pmsInvoiceManager;
    
    @Autowired
    ProductManager productManager;
    
    @Autowired
    UserManager userManager;
    
    @Override
    public StartBookingResult startBooking(StartBooking arg) {
        
        if(arg.adults < arg.rooms) {
            return null;
        }
        PmsBooking booking = pmsManager.startBooking();
        booking.couponCode = arg.discountCode;
        try {
            pmsManager.setBooking(booking);
        }catch(Exception e) {
            logPrintException(e);
        }
        
        arg.start = pmsInvoiceManager.normalizeDate(arg.start, true);
        arg.end = pmsInvoiceManager.normalizeDate(arg.end, false);
        
        StartBookingResult result = new StartBookingResult();
        List<BookingItemType> types = bookingEngine.getBookingItemTypes();
        
        Collections.sort(types, new Comparator<BookingItemType>() {
            public int compare(BookingItemType o1, BookingItemType o2) {
                return o1.order.compareTo(o2.order);
            }
        });

        
        PmsBooking existing = pmsManager.getCurrentBooking();
        
        boolean isAdministrator = false;
        
        if(getSession() != null && getSession().currentUser != null && getSession().currentUser.isAdministrator()) {
            isAdministrator = true;
        }
        
        for(BookingItemType type : types) {
            if(!type.visibleForBooking && !isAdministrator) {
                continue;
            }
            BookingProcessRooms room = new BookingProcessRooms();
            room.description = type.description;
            room.availableRooms = bookingEngine.getNumberOfAvailable(type.id, arg.start, arg.end);
            room.id = type.id;
            room.visibleForBooker = type.visibleForBooking;
            result.totalRooms += room.availableRooms;
            try {
                PmsAdditionalTypeInformation typeInfo = pmsManager.getAdditionalTypeInformationById(type.id);
                room.images.addAll(typeInfo.images);
                room.name = type.name;
                room.maxGuests = type.size;
                
                for(int i = 1; i <= type.size;i++) {
                    int count = 0;
                    for(PmsBookingRooms existingRoom : existing.rooms) {
                        if(existingRoom.bookingItemTypeId.equals(type.id) && existingRoom.numberOfGuests == i) {
                            count++;
                        }
                    }
                    room.roomsSelectedByGuests.put(i, count);
                    Double price = pmsInvoiceManager.calculatePrice(type.id, arg.start, arg.end, true, existing);
                    price += pmsInvoiceManager.getDerivedPrice(existing, type.id, i);
                    room.pricesByGuests.put(i, price);
                }
            } catch (Exception ex) {
                Logger.getLogger(PmsBookingProcess.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            result.rooms.add(room);
        }
        
        selectMostSuitableRooms(result, arg);
        
        result.totalAmount = existing.getTotalPrice();
        
        return result;
    }

    private void selectMostSuitableRooms(StartBookingResult result, StartBooking arg) {
        System.out.println("Need to find: " + arg.rooms + " rooms for :" + arg.adults);
        
        List<PmsBookingProcessorCalculator> toUse = new ArrayList();
        
        PmsBooking booking = pmsManager.getCurrentBooking();
        
        for(BookingProcessRooms room : result.rooms) {
            if(!room.visibleForBooker) {
                continue;
            }
            for(Integer guest : room.pricesByGuests.keySet()) {
                Double price = room.pricesByGuests.get(guest);
                PmsBookingProcessorCalculator res = new PmsBookingProcessorCalculator();
                res.room = room;
                res.guests = guest;
                res.price = price;
                res.guestPrice = price/guest;
                res.maxRooms = room.availableRooms;
                toUse.add(res);
                
            }
        }
        
        Collections.sort(toUse, new Comparator<PmsBookingProcessorCalculator>(){
            public int compare(PmsBookingProcessorCalculator o1, PmsBookingProcessorCalculator o2){
                if(o1.guestPrice == o2.guestPrice)
                    return 0;
                return o1.guestPrice < o2.guestPrice ? -1 : 1;
            }
        });
       
        List<PmsBookingProcessorCalculator> listOfRooms = new ArrayList();
        int guestLeft = arg.adults;
        int roomsLeft = arg.rooms;
        int breaker = 0;
        while(true) {
            for(Integer roomIdx = 0; roomIdx < arg.rooms; roomIdx++) {
                for(PmsBookingProcessorCalculator lowest : toUse) {
                    if(lowest.maxRooms == 0) {
                        continue;
                    }
                    if(lowest.guests <= guestLeft) {
                        if(lowest.guests == guestLeft && roomsLeft > 1) {
                            continue;
                        }
                        lowest.maxRooms--;
                        listOfRooms.add(lowest);
                        guestLeft -= lowest.guests;
                        roomsLeft--;
                        break;
                    }
                }
                if(guestLeft <= 0) {
                    break;
                }
            }
            if(guestLeft <= 0) {
                break;
            }
            boolean roomsleft = false;
            for(PmsBookingProcessorCalculator lowest : toUse) {
                if(lowest.maxRooms > 0) {
                    roomsleft = true;
                }
            }
            if(!roomsleft) {
                break;
            }
            
            breaker++;
            if(breaker >= 1000) {
                break;
            }
        }
        
        if(guestLeft > 0) {
            logPrint("################ WARNING ################");
            logPrint("Not all guests where assigned a room");
            logPrint("################ WARNING ################");
        }
        if(roomsLeft > 0) {
            logPrint("################ WARNING ################");
            logPrint("Not all rooms where assigned");
            logPrint("################ WARNING ################");
        }
        
        logPrint("---------------: " + guestLeft + "-----------");
        for(PmsBookingProcessorCalculator check : listOfRooms) {
                logPrint(check.guests + " : "+ check.room.availableRooms + " : " + check.price + " : " + check.room.name);
                Integer current = check.room.roomsSelectedByGuests.get(check.guests);
                if(current == null) {
                    current = 0;
                }
                current++;
                check.room.roomsSelectedByGuests.put(check.guests, current);
                
                PmsBookingRooms toAddToCurrentBooking = new PmsBookingRooms();
                toAddToCurrentBooking.bookingItemTypeId = check.room.id;
                toAddToCurrentBooking.numberOfGuests = check.guests;
                toAddToCurrentBooking.date = new PmsBookingDateRange();
                toAddToCurrentBooking.date.start = normalizeDate(arg.start, true);
                toAddToCurrentBooking.date.end = normalizeDate(arg.end, false);
                result.roomsSelected++;
                booking.addRoom(toAddToCurrentBooking);
        }
        try {
            pmsManager.setBooking(booking);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public GuestAddonsSummary getAddonsSummary(List<RoomsSelected> arg) {
        updateRoomsOnCurrentBooking(arg);
        return generateSummary();
    }

    private void updateRoomsOnCurrentBooking(List<RoomsSelected> result) {
        PmsBooking booking = pmsManager.getCurrentBooking();
        HashMap<String, Integer> typesCount = new HashMap();
        HashMap<String, Integer> currentBookingTypesCount = new HashMap();
        
        for(RoomsSelected room : result) {
            for(Integer guests : room.roomsSelectedByGuests.keySet()) {
                if(room.roomsSelectedByGuests.get(guests) > 0) {
                    Integer count = typesCount.get(room.id);
                    if(count == null) {
                        count = 0;
                    }
                    count++;
                    typesCount.put(room.id, count);
                }
            }
        }
        
        for(PmsBookingRooms room : booking.rooms) {
            Integer count = currentBookingTypesCount.get(room.bookingItemTypeId);
            if(count == null) {
                count = 0;
            }
            count++;
            currentBookingTypesCount.put(room.bookingItemTypeId, count);
        }
    }

    private Date normalizeDate(Date date, boolean isStart) {
        String[] defaultStart = pmsManager.getConfigurationSecure().defaultStart.split(":");
        String[] defaultEnd = pmsManager.getConfigurationSecure().defaultEnd.split(":");
        if(isStart) {
            Calendar calStart = Calendar.getInstance();
            calStart.setTime(date);
            calStart.set(Calendar.HOUR_OF_DAY, new Integer(defaultStart[0]));
            calStart.set(Calendar.MINUTE, new Integer(defaultStart[1]));
            date = calStart.getTime();
        } else {
            Calendar calEnd = Calendar.getInstance();
            calEnd.setTime(date);
            calEnd.set(Calendar.HOUR_OF_DAY, new Integer(defaultEnd[0]));
            calEnd.set(Calendar.MINUTE, new Integer(defaultEnd[1]));
            date = calEnd.getTime();
        }
        return date;
    }

    private void addRoomSummary(GuestAddonsSummary result) {
        PmsBooking booking = pmsManager.getCurrentBooking();
        result.fields = booking.registrationData.resultAdded;
        result.profileType = booking.registrationData.profileType;
        for(PmsBookingRooms room : booking.rooms) {
            RoomInfo returnroom = new RoomInfo();
            returnroom.start = room.date.start;
            returnroom.end = room.date.end;
            returnroom.guestCount = room.numberOfGuests;
            returnroom.roomId = room.pmsBookingRoomId;
            returnroom.roomName = bookingEngine.getBookingItemType(room.bookingItemTypeId).name;
            returnroom.maxGuests = bookingEngine.getBookingItemType(room.bookingItemTypeId).size;
            
            for(PmsGuests guest : room.guests) {
                GuestInfo info = new GuestInfo();
                info.name = guest.name;
                info.email = guest.email;
                info.prefix = guest.prefix;
                info.phone = guest.phone;
                returnroom.guestInfo.add(info);
            }
            List<PmsBookingAddonItem> addons = pmsManager.getAddonsWithDiscount(room.pmsBookingRoomId);
            
            List<PmsBookingAddonItem> res = addons.stream()
                .filter(add -> add.isActive || add.isAvailableForBooking)
                .collect(Collectors.toList());
            
            returnroom.addonsAvailable.clear();
            for(PmsBookingAddonItem item : res) {
                AddonItem toAddAddon = new AddonItem();
                toAddAddon.setAddon(item);
                toAddAddon.name = productManager.getProduct(item.productId).name;
                toAddAddon.icon = item.bookingicon;
                checkIsAddedToRoom(toAddAddon, room, item);
                if(!item.displayInBookingProcess.isEmpty() && !item.displayInBookingProcess.contains(room.bookingItemTypeId)) {
                    continue;
                }
                returnroom.addonsAvailable.put(toAddAddon.productId, toAddAddon);
            }
            
            result.rooms.add(returnroom);
        }
    }

    private void addItemSupported(GuestAddonsSummary result) {
        HashMap<String, AddonItem> itemsMap = new HashMap();
        for(RoomInfo r : result.rooms) {
            for(AddonItem item : r.addonsAvailable.values()) {
                itemsMap.put(item.productId, item);
            }
        }
        result.items = new ArrayList(itemsMap.values());
    }

    private void addTextualSummary(GuestAddonsSummary result) {
        PmsBooking booking = pmsManager.getCurrentBooking();
        int numberOfNights = 0;
        int numberOfRooms = 0;
        int numberOfGuests = 0;
        for(PmsBookingRooms room : booking.getActiveRooms()) {
            numberOfGuests += room.numberOfGuests;
        }
        result.textualSummary.add(numberOfGuests + " x guests");
        result.textualSummary.add(booking.getActiveRooms().size() + " x rooms");
        
        for(AddonItem item : result.items) {
            int added = 0;
            for(PmsBookingRooms room : booking.getActiveRooms()) {
                for(PmsBookingAddonItem tmp : room.addons) {
                    if(tmp.productId.equals(item.productId)) {
                        added += tmp.count;
                    }
                }
            }
            result.textualSummary.add(added + " x " + productManager.getProduct(item.productId).name);
        }
        
        result.textualSummary.add("Total price : " + booking.getTotalPrice());
        
    }

    @Override
    public GuestAddonsSummary addAddons(AddAddons arg) {
        if(arg.roomId == null || arg.roomId.isEmpty()) {
            PmsBooking booking = null;
            booking = pmsManager.getCurrentBooking();
            for(PmsBookingRooms room : booking.getActiveRooms()) {
                pmsManager.addProductToRoomDefaultCount(arg.productId, room.pmsBookingRoomId);
            }
        } else {
            pmsManager.addProductToRoomDefaultCount(arg.productId, arg.roomId);
        }
        
        return generateSummary();
    }

    @Override
    public GuestAddonsSummary removeAddons(AddAddons arg) {
        List<String> roomsToRemoveFrom = new ArrayList();
        PmsBooking booking = pmsManager.getCurrentBooking();
        if(arg.roomId == null || arg.roomId.isEmpty()) {
            for(PmsBookingRooms r : booking.getActiveRooms()) {
                roomsToRemoveFrom.add(r.pmsBookingRoomId);
            }
        } else {
            roomsToRemoveFrom.add(arg.roomId);
        }
        
        for(PmsBookingRooms r : booking.getActiveRooms()) {
            if(!roomsToRemoveFrom.contains(r.pmsBookingRoomId)) {
                continue;
            }
            
            List<PmsBookingAddonItem> remove = new ArrayList();
            for(PmsBookingAddonItem item : r.addons) {
                if(item.productId.equals(arg.productId)) {
                    remove.add(item);
                }
            }
            r.addons.removeAll(remove);
        }
        
        try {
            pmsManager.setBooking(booking);
        }catch(Exception e) {
            logPrintException(e);
        }
        
        return generateSummary();
    }

    @Override
    public GuestAddonsSummary saveGuestInformation(List<RoomInformation> arg) {
        PmsBooking booking = pmsManager.getCurrentBooking();
        for(RoomInformation info : arg) {
            PmsBookingRooms room = booking.getRoom(info.roomId);
            room.numberOfGuests = info.numberOfGuests;
            List<PmsGuests> updatedGuestInfo = new ArrayList();
            for(GuestInfo ginfo : info.guests) {
                PmsGuests newGuest = new PmsGuests();
                newGuest.email = ginfo.email;
                newGuest.phone = ginfo.phone;
                newGuest.prefix = ginfo.prefix;
                newGuest.name = ginfo.name;
                updatedGuestInfo.add(newGuest);
            }
            room.guests = updatedGuestInfo;
            pmsInvoiceManager.updatePriceMatrix(booking, room, booking.priceType);
        }
        try {
            pmsManager.setBooking(booking);
        }catch(Exception e) {
            logPrintException(e);
        }
        
        return generateSummary();
    }

    @Override
    public GuestAddonsSummary removeRoom(String roomId) {
        return generateSummary();
    }

    private GuestAddonsSummary generateSummary() {
        GuestAddonsSummary result = new GuestAddonsSummary();
        addRoomSummary(result);
        addItemSupported(result);
        addTextualSummary(result);
        addLoggedOnInformation(result);
        validateFields(result);
        return result;
    }

    private void isAddedToBooking(AddonItem toAddAddon, PmsBooking booking, PmsBookingAddonItem item) {
        int addedCount = 0;
        for(PmsBookingRooms room : booking.getActiveRooms()) {
            for(PmsBookingAddonItem tmp : room.addons) {
                if(tmp.productId.equals(item.productId)) {
                    addedCount++;
                }
            }
        }
        if(addedCount > 0) {
            toAddAddon.isAdded = true;
            toAddAddon.addedCount = addedCount;
        }
    }

    private void checkIsAddedToRoom(AddonItem toAddAddon, PmsBookingRooms room, PmsBookingAddonItem item) {
        int addedCount = 0;
        for(PmsBookingAddonItem tmp : room.addons) {
            if(tmp.productId.equals(item.productId)) {
                addedCount++;
            }
        }
        if(addedCount > 0) {
            toAddAddon.isAdded = true;
            toAddAddon.addedCount = addedCount;
        }
    }

    @Override
    public GuestAddonsSummary setGuestInformation(BookerInformation bookerInfo) {
        PmsBooking booking = pmsManager.getCurrentBooking();
        booking.registrationData.resultAdded = bookerInfo.fields;
        booking.registrationData.profileType = bookerInfo.profileType;
        booking.agreedToTermsAndConditions = bookerInfo.agreeToTerms;
        booking.invoiceNote = bookerInfo.ordertext;
        
        try {
            pmsManager.setBooking(booking);
        }catch(Exception e) {
            logPrint(e);
        }
        return generateSummary();
    }

    @Override
    public BookingResult completeBooking() {
        User loggedOn = userManager.getLoggedOnUser();
        PmsBooking booking = null;
        if(loggedOn != null) {
            booking = pmsManager.getCurrentBooking();
            if(booking.userId == null || booking.userId.trim().isEmpty()) {
                booking.userId = loggedOn.id;
                try {
                    pmsManager.setBooking(booking);
                }catch(Exception e) {
                    logPrint(e);
                }
            }
        }
        
        booking = pmsManager.completeCurrentBooking();
        BookingResult res = new BookingResult();
        res.success = 1;
        if(res == null) {
            res.success = 0;
        } else {
            res.bookingid = booking.id;
            if(booking.orderIds.size() == 1) {
                res.continuetopayment = 1;
                res.orderid = booking.orderIds.get(0);
            } else {
                res.continuetopayment = 0;
            }
        }
        return res;
    }

    private void validateFields(GuestAddonsSummary result) {
        PmsBooking booking = pmsManager.getCurrentBooking();
        result.isValid = true;
        if(!booking.agreedToTermsAndConditions) {
            result.fieldsValidation.put("agreeterms", "Need to agree to terms and contiditions");
            result.isValid = false;
        }
        
        for(PmsBookingRooms room : booking.getActiveRooms()) {
            for(int i = 0; i < room.numberOfGuests; i++) {
                PmsGuests guest = new PmsGuests();
                if(room.guests.size() > i) {
                    guest = room.guests.get(i);
                }
                if(guest.email == null || guest.email.trim().isEmpty() || !guest.email.contains("@")) {
                    result.fieldsValidation.put("guest_" + room.pmsBookingRoomId + "_email", "Invalid email");
                    result.isValid = false;
                }
                if(guest.name == null || guest.name.trim().isEmpty()) {
                    result.fieldsValidation.put("guest_" + room.pmsBookingRoomId + "_name", "Invalid name");
                    result.isValid = false;
                }
                if(guest.phone == null || guest.phone.trim().isEmpty()) {
                    result.fieldsValidation.put("guest_" + room.pmsBookingRoomId + "_phone", "Invalid phone");
                    result.isValid = false;
                }
                break;
            }
        }
        
        if(!result.isLoggedOn) {
            String type = result.profileType;
            String prefix = "user_";
            if(type.equals("organization")) {
                prefix = "company_";
            }
            for(String key : result.fields.keySet()) {
                String value = result.fields.get(key);
                if(key.startsWith(prefix) && (value == null || value.trim().isEmpty())) {
                    result.fieldsValidation.put(key, "Field is required");
                    result.isValid = false;
                }
            }
        }
        
    }

    private void addLoggedOnInformation(GuestAddonsSummary result) {
        User loggedOnUser = userManager.getLoggedOnUser();
        if(loggedOnUser == null) {
            result.loggedOnName = "";
            result.isLoggedOn = false;
        } else {
            result.loggedOnName = loggedOnUser.fullName;
            result.isLoggedOn = true;
        }
    }

    @Override
    public GuestAddonsSummary logOn(BookingLogonData logindata) {
        try {
            userManager.logOn(logindata.username, logindata.password);
        }catch(ErrorException e) {}
        return generateSummary();
    }

    @Override
    public GuestAddonsSummary logOut() {
        userManager.logout();
        return generateSummary();
    }

    @Override
    public GuestAddonsSummary changeNumberOnType(BookingTypeChange change) {
        PmsBooking booking = pmsManager.getCurrentBooking();
        int current = 0;
        List<PmsBookingRooms> roomsAdded = new ArrayList();
        for(PmsBookingRooms room : booking.rooms) {
            if(!room.bookingItemTypeId.equals(change.id)) {
                continue;
            }
            if(room.numberOfGuests.equals(change.guests)) {
                roomsAdded.add(room);
                current++;
            }
        }
        
        if(current > change.numberOfRooms) {
            //Remove rooms.
            List<PmsBookingRooms> toRemove = new ArrayList();
            for(int i = 0; i < current-change.numberOfRooms;i++) {
                toRemove.add(roomsAdded.get(i));
            }
            booking.rooms.removeAll(toRemove);
        } else {
            //Add rooms
            for(int i = 0; i < change.numberOfRooms-current;i++) {
                PmsBookingRooms toAddToCurrentBooking = new PmsBookingRooms();
                toAddToCurrentBooking.bookingItemTypeId = change.id;
                toAddToCurrentBooking.numberOfGuests = change.guests;
                toAddToCurrentBooking.date = new PmsBookingDateRange();
                toAddToCurrentBooking.date.start = normalizeDate(change.start, true);
                toAddToCurrentBooking.date.end = normalizeDate(change.end, false);
                booking.rooms.add(toAddToCurrentBooking);
            }
        }
        try {
            pmsManager.setBooking(booking);
        }catch(Exception e) {
            logPrintException(e);
        }
        
        return generateSummary();
    }
}
