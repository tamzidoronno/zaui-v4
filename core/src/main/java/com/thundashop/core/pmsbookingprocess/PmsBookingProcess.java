/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsbookingprocess;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.google.gson.Gson;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.AddonsInclude;
import com.thundashop.core.cartmanager.data.Coupon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.gsd.GdsManager;
import com.thundashop.core.gsd.GetShopDevice;
import com.thundashop.core.gsd.RoomReceipt;
import com.thundashop.core.messagemanager.SmsHandlerAbstract;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.paymentterminalmanager.PaymentTerminalManager;
import com.thundashop.core.paymentterminalmanager.PaymentTerminalSettings;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.pdf.data.AccountingDetails;
import com.thundashop.core.pmsmanager.PmsAdditionalTypeInformation;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingAddonItem;
import com.thundashop.core.pmsmanager.PmsBookingDateRange;
import com.thundashop.core.pmsmanager.PmsBookingFilter;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import com.thundashop.core.pmsmanager.PmsGuests;
import com.thundashop.core.pmsmanager.PmsInvoiceManager;
import com.thundashop.core.pmsmanager.PmsInvoiceManagerNew;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.pmsmanager.PmsPricing;
import com.thundashop.core.pmsmanager.PmsUserDiscount;
import com.thundashop.core.pmsmanager.TimeRepeaterData;
import com.thundashop.core.pos.PosManager;
import com.thundashop.core.printmanager.PrintJob;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.verifonemanager.VerifoneManager;
import com.thundashop.core.webmanager.WebManager;
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
import org.joda.time.Days;
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
    PosManager posManager;
    
    @Autowired
    ProductManager productManager;
    
    @Autowired
    StoreManager storeManager;
    
    @Autowired
    CartManager cartManager;
    
    @Autowired
    UserManager userManager;
    private ArrayList itemsTaken;
    
    @Autowired
    OrderManager orderManager;
    
    @Autowired
    StoreApplicationPool storeApplicationPool;
    
    @Autowired
    VerifoneManager verifoneManager;
    
    @Autowired
    PaymentTerminalManager paymentTerminalManager;
    
    @Autowired
    WebManager webManager;
    
    @Autowired
    GdsManager gdsManager;
    
    @Autowired
    private InvoiceManager invoiceManager;
    
    public boolean testTerminalPrinter = false;
    public boolean testTerminalPaymentTerminal = false;
    private boolean isVerifone;
    
    @Override
    public StartBookingResult startBooking(StartBooking arg) {
        
        if(arg.start == null || arg.end == null) {
            return new StartBookingResult();
        }
        long timediff = arg.end.getTime() - arg.start.getTime();
        timediff = timediff / (86400*1000);
        if(timediff > 1825 || timediff < 0) {
            return new StartBookingResult();
        }
        
        Date now = storeManager.getMyStore().getCurrentTimeInTimeZone();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.HOUR_OF_DAY, -4);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date toCheck = cal.getTime();
        if(toCheck.after(arg.start)) {
            return new StartBookingResult();
        }
    
        Gson gson = new Gson();
        logPrint(gson.toJson(arg));
        if(arg.getGuests() < arg.rooms) {
            return new StartBookingResult();
        }
        PmsBooking booking = pmsManager.startBooking();
        if(arg.language != null && !arg.language.isEmpty()) {
            setSessionLanguage(arg.language);
        }
        
        if(booking.language == null || booking.language.isEmpty()) {
            booking.language = getSession().language;
        }
        if(arg.discountCode != null && !arg.discountCode.isEmpty()) {
            User discountUser = userManager.getUserByReference(arg.discountCode);
            if(discountUser != null) {
                booking.userId = discountUser.id;
                booking.couponCode = pmsInvoiceManager.getDiscountsForUser(discountUser.id).attachedDiscountCode;
            } else {
                booking.couponCode = arg.discountCode;
            }
        }
        
        booking.browserUsed = arg.browser;
        
        try {
            pmsManager.setBooking(booking);
        }catch(Exception e) {
            logPrintException(e);
        }
        
        arg.start = pmsManager.getConfigurationSecure().getDefaultStart(arg.start);
        arg.end = pmsManager.getConfigurationSecure().getDefaultEnd(arg.end);
        
        if(arg.start.after(arg.end)) {
            arg.end = correctToDayAfter(arg);
        }
        
        StartBookingResult result = new StartBookingResult();
        List<BookingItemType> types = bookingEngine.getBookingItemTypesWithSystemType(null);
        result.numberOfDays = pmsInvoiceManager.getNumberOfDays(arg.start, arg.end);
        
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
        
        pmsInvoiceManager.startCacheCoverage();
        for(BookingItemType type : types) {
            if(!type.visibleForBooking && !isAdministrator) {
                continue;
            }
            BookingProcessRooms room = new BookingProcessRooms();
            room.userId = booking.userId;
            room.description = type.getTranslatedDescription(getSession().language);
            room.availableRooms = pmsManager.getNumberOfAvailable(type.id, arg.start, arg.end, true, true);
            room.id = type.id;
            room.systemCategory = type.systemCategory;
            room.visibleForBooker = type.visibleForBooking;
            result.totalRooms += room.availableRooms;
            try {
                PmsAdditionalTypeInformation typeInfo = pmsManager.getAdditionalTypeInformationById(type.id);
                room.images.addAll(typeInfo.images);
                room.sortDefaultImageFirst();
                room.name = type.getTranslatedName(getSession().language);
                room.maxGuests = type.size;
                
                for(int i = 1; i <= type.size;i++) {
                    int count = 0;
                    for(PmsBookingRooms existingRoom : existing.rooms) {
                        if(existingRoom.bookingItemTypeId.equals(type.id) && existingRoom.numberOfGuests == i) {
                            count++;
                        }
                    }
                    room.roomsSelectedByGuests.put(i, count);
                    Double price = getPriceForRoom(room, arg.start, arg.end, i, booking.couponCode);
                    room.pricesByGuests.put(i, price);
                }
            } catch (Exception ex) {
                Logger.getLogger(PmsBookingProcess.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            result.rooms.add(room);
        }
        
        PmsConfiguration pmsConfig = pmsManager.getConfigurationSecure();
        if(!pmsConfig.doNotRecommendBestPrice) {
            selectMostSuitableRooms(result, arg);
        }
        result.totalAmount = pmsManager.getCurrentBooking().getTotalPrice();
        result.supportPayLaterButton = checkIfSupportPayLater(arg);
        result.supportedPaymentMethods = checkForSupportedPaymentMethods(booking);
        result.prefilledContactUser = "";
        result.startYesterday = isMidleOfNight() && PmsBookingRooms.isSameDayStatic(arg.start, new Date());
        result.bookingId = existing.id;
        
        if(booking.userId != null && !booking.userId.isEmpty()) {
            result.prefilledContactUser = userManager.getUserById(booking.userId).fullName;
        }
        
        addCouponPricesToRoom(result, arg);
        checkIfCouponIsValid(result, arg);
        checkForRestrictions(result, arg);
        addAddonsIncluded(result,arg);
        
        return result;
    }

    private boolean checkIfSupportPayLater(StartBooking arg) {
        PmsBooking booking = pmsManager.getCurrentBooking();
        if(booking == null) {
            return false;
        }
        Date startDate = booking.getStartDate();
        if(startDate == null) {
            startDate = arg.start;
        }
        if(startDate == null) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        
        PmsConfiguration config = pmsManager.getConfigurationSecure();
        Integer ignoreDays = config.ignorePaymentWindowDaysAheadOfStay;
        
        if (ignoreDays == 0) {
            return true;
        }
        
        if(ignoreDays <= 0) {
            return false;
        }
        
        long now = System.currentTimeMillis();
        long diff = cal.getTimeInMillis() - now;
        
        int days = (int) ((diff / 1000) / 86400);
        
        if(days >= ignoreDays) {
            return true;
        }
        
        return false;
    }
    
    private void selectMostSuitableRooms(StartBookingResult result, StartBooking arg) {
        logPrint("Need to find: " + arg.rooms + " rooms for :" + arg.getGuests());
        
        List<PmsBookingProcessorCalculator> toUse = new ArrayList();
        
        PmsBooking booking = pmsManager.getCurrentBooking();
        HashMap<String, Integer> maxRooms = new HashMap();
        
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
                maxRooms.put(room.id, room.availableRooms);
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
        int guestLeft = arg.getGuests();
        int roomsLeft = arg.rooms;
        int breaker = 0;
        while(true) {
            for(Integer roomIdx = 0; roomIdx < arg.rooms; roomIdx++) {
                for(PmsBookingProcessorCalculator lowest : toUse) {
                    if(maxRooms.get(lowest.room.id) == 0) {
                        continue;
                    }
                    if((guestLeft - lowest.guests) < (roomsLeft-1)) {
                        continue;
                    }
                    if((guestLeft - lowest.guests) < 0) {
                        continue;
                    }
                    int count = maxRooms.get(lowest.room.id);
                    count--;
                    maxRooms.put(lowest.room.id, count);
                    
                    listOfRooms.add(lowest);
                    guestLeft -= lowest.guests;
                    roomsLeft--;
                    break;
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
        
        boolean failed = false;
        
        if(guestLeft > 0) {
            logPrint("################ WARNING ################");
            logPrint("Not all guests where assigned a room");
            logPrint("################ WARNING ################");
            failed = true;
        }
        if(guestLeft < 0) {
            logPrint("################ WARNING ################");
            logPrint("Too many guests where assigned a room");
            logPrint("################ WARNING ################");
            failed = true;
        }
        if(roomsLeft > 0) {
            logPrint("################ WARNING ################");
            logPrint("Not all rooms where assigned");
            logPrint("################ WARNING ################");
            failed = true;
        }
        if(roomsLeft < 0) {
            logPrint("################ WARNING ################");
            logPrint("Too many rooms where assigned");
            logPrint("################ WARNING ################");
            failed = true;
        }
        
        logPrint("---------------: " + guestLeft + "-----------");
        int childToSet = arg.children;
        if(!failed) {
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
                    if(childToSet > 0) {
                        childToSet -= toAddToCurrentBooking.setGuestAsChildren(childToSet);
                    }
                    pmsManager.setPriceOnRoom(toAddToCurrentBooking, true, booking);
                    result.roomsSelected++;
                    booking.addRoom(toAddToCurrentBooking);
                    check.room.totalPriceForRoom = toAddToCurrentBooking.price;
            }
        }
        try {
            booking.calculateTotalCost();
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
        String[] defaultStart = pmsManager.getConfigurationSecure().getDefaultStart().split(":");
        String[] defaultEnd = pmsManager.getConfigurationSecure().getDefaultEnd().split(":");
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
        itemsTaken = new ArrayList();
        for(PmsBookingRooms room : booking.rooms) {
            RoomInfo returnroom = new RoomInfo();
            returnroom.start = room.date.start;
            returnroom.end = room.date.end;
            returnroom.guestCount = room.numberOfGuests;
            returnroom.roomId = room.pmsBookingRoomId;
            returnroom.roomName = bookingEngine.getBookingItemType(room.bookingItemTypeId).name;
            returnroom.maxGuests = bookingEngine.getBookingItemType(room.bookingItemTypeId).size;
            returnroom.totalCost = room.totalCost;
            returnroom.bookingItemTypeId = room.bookingItemTypeId;
            
            for(PmsGuests guest : room.guests) {
                GuestInfo info = createGuestInfo(guest);
                returnroom.guestInfo.add(info);
            }
            List<PmsBookingAddonItem> addons = pmsManager.getAddonsWithDiscount(room.pmsBookingRoomId);
            
            returnroom.addonsAvailable.clear();
            String curLang = getSession().language;
            for(PmsBookingAddonItem item : addons) {
                AddonItem toAddAddon = new AddonItem();
                toAddAddon.setAddon(item);
                String translation = item.getTranslationsByKey("descriptionWeb", curLang);
                if(translation != null && !translation.isEmpty()) {
                    toAddAddon.name = translation;
                }
                if(toAddAddon.name == null || toAddAddon.name.isEmpty() && productManager.getProduct(item.productId) != null) {
                    toAddAddon.name = productManager.getProduct(item.productId).name;
                }
                toAddAddon.icon = item.bookingicon;
                checkIsAddedToRoom(toAddAddon, room, item);
                if(!item.displayInBookingProcess.isEmpty() && !item.displayInBookingProcess.contains(room.bookingItemTypeId)) {
                    continue;
                }
                if (shouldIgnoreDueToGroupAddonFunctionallity(item, addons)) {
                    continue;
                }
                if(isAvailableForRoom(item, room)) {
                    returnroom.addonsAvailable.put(toAddAddon.productId, toAddAddon);
                }
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
        int numberOfAdults = 0;
        int numberOfChildren = 0;
        String curLang = getSession().language;
        for(PmsBookingRooms room : booking.getActiveRooms()) {
            for(int i = 0; i < room.numberOfGuests; i++) {
                if(room.guests.size() > i) {
                    PmsGuests guestInfo = room.guests.get(i);
                    if(guestInfo.isChild) {
                        numberOfChildren++;
                    } else {
                        numberOfAdults++;
                    }
                } else {
                    numberOfAdults++;
                }
            }
        }
        if(numberOfAdults == 1) {
            result.textualSummary.add(numberOfAdults + " x {adult}");
        } else {
            result.textualSummary.add(numberOfAdults + " x {adults}");
        }
        if(numberOfChildren == 1) {
            result.textualSummary.add(numberOfChildren + " x {child}");
        } else {
            result.textualSummary.add(numberOfChildren + " x {children}");
        }
        HashMap<Integer, Integer> typeCounter = new HashMap();
        for(PmsBookingRooms room : booking.getActiveRooms()) {
            BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
            Integer typecount = typeCounter.get(type.systemCategory);
            if(typecount == null) { typecount = 0; }
            typecount++;
            typeCounter.put(type.systemCategory,typecount);
        }
        for(Integer type : typeCounter.keySet()) {
            Integer count = typeCounter.get(type);
            if(count > 1) {
                result.textualSummary.add(typeCounter.get(type) + " x {selections_"+type+"}");
            } else {
                result.textualSummary.add(typeCounter.get(type) + " x {selection_"+type+"}");
            }
        }
        
        
        List<PmsBookingAddonItem> addons = pmsManager.getAddonsAvailable();
        for(PmsBookingAddonItem item : addons) {
            int added = 0;
            for(PmsBookingRooms room : booking.getActiveRooms()) {
                for(PmsBookingAddonItem tmp : room.addons) {
                    if(tmp.productId.equals(item.productId)) {
                        added += tmp.count;
                    }
                }
            }
            if(added > 0) {
                String text = item.getName();
                String translation = item.getTranslationsByKey("name", curLang);
                if(translation != null && !translation.isEmpty()) {
                    text = translation;
                }
                result.textualSummary.add(added + " x " + text);
            }
        }
        
        result.textualSummary.add("{totalprice} {currency}" + Math.round(booking.getTotalPrice()));
        
    }

    @Override
    public GuestAddonsSummary addAddons(AddAddons arg) {
        if(arg.roomId == null || arg.roomId.isEmpty()) {
            PmsBooking booking = null;
            booking = pmsManager.getCurrentBooking();
            itemsTaken = new ArrayList();
            for(PmsBookingRooms room : booking.getActiveRooms()) {
                if(canAddToRoom(arg.productId, room)) {
                    if(arg.count > 0) {
                        pmsManager.addProductToRoom(arg.productId, room.pmsBookingRoomId, arg.count);
                    } else {
                        pmsManager.addProductToRoomDefaultCount(arg.productId, room.pmsBookingRoomId);
                    }
                }
            }
        } else {
            if(arg.count > 0) {
                pmsManager.addProductToRoom(arg.productId, arg.roomId, arg.count);
            } else {
                pmsManager.addProductToRoomDefaultCount(arg.productId, arg.roomId);
            }
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
                newGuest.isChild = ginfo.isChild;
                newGuest.orderedOption = ginfo.selectedOptions;
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
        PmsBooking booking = pmsManager.getCurrentBooking();
        PmsBookingRooms room = booking.findRoom(roomId);
        booking.rooms.remove(room);
        try {
            pmsManager.setBooking(booking);
        }catch(Exception e) {
            logPrintException(e);
        }
        return generateSummary();
    }

    private GuestAddonsSummary generateSummary() {
        pmsManager.addDefaultAddons(pmsManager.getCurrentBooking());
        
        GuestAddonsSummary result = new GuestAddonsSummary();
        addRoomSummary(result);
        addItemSupported(result);
        addTextualSummary(result);
        addLoggedOnInformation(result);
        addGroupAddons(result);
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
    public void printReciept(BookingPrintRecieptData data) {
        logPrint("Starting printing service for " + data.terminalId + " - " + data.orderId);
        pmsManager.processor();   
        
        String ipaddr = getIpadressOnTerminal(data.terminalId);
        
        if(ipaddr == null || ipaddr.isEmpty()) {
            logPrint("Printing using integrated terminal");
            printRecieptIntegratatedTerminal(data.orderId, data.terminalId);
            return;
        }
        
        
        PaymentTerminalSettings settings = paymentTerminalManager.getSetings(1);
        Order order = orderManager.getOrderSecure(data.orderId);
        if(order.status != Order.Status.PAYMENT_COMPLETED) {
            return;
        }
        User user = userManager.getUserById(order.userId);
        String text = order.createThermalPrinterReciept(getAccountingDetails(), user);
        pmsManager.processor();
        String url = "http://" + settings.ip + ":8080/print.php";
        logPrint("Printing to address " + url);
        try {
            
            PmsBooking booking = pmsManager.getBookingWithOrderId(order.id);
            for(PmsBookingRooms room : booking.getActiveRooms()) {
                if(!order.containsRoom(room.pmsBookingRoomId)) {
                    continue;
                }
                if(room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
                    BookingItem item = bookingEngine.getBookingItem(room.bookingItemId);
                    text += "--------------------\r\n";
                    text += "###  ROOM CODE   ###\r\n";
                    text += "--------------------\r\n";
                    text += "Room: " + item.bookingItemName + "\r\n";
                    text += "Code: " + room.code + "\r\n";
                }
            }

            text += "\r\n";
            text += "\r\n";
            text += "\r\n";
            boolean addToLog = false;
            if(order != null && order.payment != null && order.payment.transactionLog != null) {
                addToLog = true;
            }
            if(storeManager.isProductMode()) {
                order.payment.transactionLog.put(System.currentTimeMillis(), "Print receipt (" + url + ")");
                webManager.htmlPostThreaded(url, text, false, "UTF-8");
            } else {
                order.payment.transactionLog.put(System.currentTimeMillis(), "Print receipt (dev mode)");
                logPrint("Printing receipt to url: " + url);
                logPrint(text);
            }
            orderManager.saveOrder(order);
        }catch(Exception e) {
            
        }
    }
    
    private AccountingDetails getAccountingDetails() throws ErrorException {
        Application settings = storeApplicationPool.getApplicationIgnoreActive("70ace3f0-3981-11e3-aa6e-0800200c9a66");
        AccountingDetails details = new AccountingDetails();
        if(settings != null) {
            details.accountNumber = settings.getSetting("accountNumber");
            details.iban = settings.getSetting("iban");
            details.swift = settings.getSetting("swift");
            details.address = settings.getSetting("address");
            details.city = settings.getSetting("city");
            details.companyName = settings.getSetting("companyName");
            details.contactEmail = settings.getSetting("contactEmail");
            details.dueDays = Integer.parseInt(settings.getSetting("duedays"));
            details.vatNumber = settings.getSetting("vatNumber");
            details.webAddress = settings.getSetting("webAddress");
            details.useLanguage = settings.getSetting("language");
            String kidSize = settings.getSetting("kidSize");
            if(kidSize != null && !kidSize.isEmpty()) {
                details.kidSize = new Integer(kidSize);
            }
            details.kidType = settings.getSetting("defaultKidMethod");
            details.type = settings.getSetting("type");
            details.currency = orderManager.getLocalCurrencyCode();
        }

        return details;
    }

    
    
    @Override
    public BookingResult completeBooking(CompleteBookingInput input) {
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
        booking = pmsManager.doCompleteBooking(pmsManager.getCurrentBooking());
         
        BookingResult res = new BookingResult();
        if(booking == null) {
            res.success = 0;
            return res;
        }
        
        PmsUserDiscount discount = pmsInvoiceManager.getDiscountsForUser(booking.userId);
        User usr = userManager.getUserById(booking.userId);
        if(usr != null && discount != null && usr.preferredPaymentType != null && usr.preferredPaymentType.equals("70ace3f0-3981-11e3-aa6e-0800200c9a66")) {
            booking.avoidAutoDelete = true;
            booking.paymentType = usr.preferredPaymentType;
            if(discount.supportInvoiceAfter) {
                booking.payLater = true;
            } else {
                pmsInvoiceManager.autoCreateOrderForBookingAndRoom(booking.id, booking.paymentType);
            }
        }
        
        res.success = 1;
        res.orderid = booking.id;
      
        booking.calculateTotalCost();
        res.amount = booking.getUnpaidAmount();
        
        booking.channel = "website";
        booking.payLater = input.payLater;
        if(input.payLater) {
            booking.avoidAutoDelete = true;
            pmsManager.logEntry("Pay later button pressed", booking.id, null);
            res.continuetopayment = 0;
        }
        pmsManager.saveBooking(booking);
        
        if(input.paymentMethod != null && !input.paymentMethod.isEmpty() && !booking.orderIds.isEmpty()) {
            Order order = orderManager.getOrderSecure(booking.orderIds.get(0));
            Application app = storeApplicationPool.getApplication(input.paymentMethod);
            order.payment.paymentType = app.getNameSpace();
        }
        
        for(PmsBookingRooms room : booking.rooms) {
            BookingResultRoom roomToReturn = new BookingResultRoom();
            roomToReturn.roomType = bookingEngine.getBookingItemType(room.bookingItemTypeId).name;
            for(PmsGuests guest : room.guests) {
                GuestInfo info = createGuestInfo(guest);
                roomToReturn.guests.add(info);
            }
            res.roomList.add(roomToReturn);
        }
        
        BookingResultUserData userData = new BookingResultUserData();
        userData.setUser(usr);
        res.userData = userData;
        
//        pmsManager.calculateCountryFromPhonePrefix(booking);
        
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
        
        if(!result.isLoggedOn && (booking.userId == null || booking.userId.isEmpty())) {
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

    private boolean canAddToRoom(String productId, PmsBookingRooms room) {
        List<PmsBookingAddonItem> allAddons = pmsManager.getAddonsAvailable();
        for(PmsBookingAddonItem item : allAddons) {
            if(!item.productId.equals(productId)) {
                continue;
            }
            if(isAvailableForRoom(item, room)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAvailableForRoom(PmsBookingAddonItem item,PmsBookingRooms room) {
        if(!item.onlyForBookingItems.isEmpty()) {
            List<BookingItem> items = bookingEngine.getAvailbleItems(room.bookingItemTypeId, room.date.start, room.date.end);
            for(BookingItem tmpItem : items) {
            String key = item.productId + "_" + tmpItem.id;
                if(item.onlyForBookingItems.contains(tmpItem.id) && !itemsTaken.contains(key)) {
                    itemsTaken.add(key);
                    return true;
                }
            }
            
            return false;
        }
        
        if(item.displayInBookingProcess.contains(room.bookingItemTypeId)) {
            return true;
        }
        return false;
    }

    @Override
    public GuestAddonsSummary removeGroupedRooms(RemoveGroupedRoomInput arg) {
        String roomId = arg.roomId;
        Integer guests = arg.guestCount;
        
        PmsBooking booking = pmsManager.getCurrentBooking();
        List<PmsBookingRooms> toRemove = new ArrayList();
        for(PmsBookingRooms room : booking.getActiveRooms()) {
            if(room.bookingItemTypeId.equals(roomId) && room.numberOfGuests.equals(guests)) {
                toRemove.add(room);
            }
        }
        
        booking.rooms.removeAll(toRemove);
        try {
            pmsManager.setBooking(booking);
        }catch(Exception e) {
            logPrintException(e);
        }
        
        return generateSummary();
    }

    @Override
    public BookingConfig getConfiguration() {
        Store store = storeManager.getMyStore();
        Application settings = getStoreSettingsApplication();
        String currencycode = settings.getSetting("currencycode");
        if(currencycode == null || currencycode.isEmpty()) {
            currencycode = "NOK";
        }
        PmsConfiguration config = pmsManager.getConfiguration();
        BookingConfig retval = new BookingConfig();
        retval.childAge = config.childMaxAge;
        retval.phonePrefix = "" + store.configuration.defaultPrefix;
        retval.currencyText = currencycode;
        retval.startYesterday = false;
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(store.getCurrentTimeInTimeZone());
        Integer hour = cal.get(Calendar.HOUR_OF_DAY);
        if(hour < 8) {
            retval.startYesterday = true;
        }
        
        retval.defaultCheckinTime = config.getDefaultStartRaw();
        retval.ignoreGuestInformation = config.ignoreGuestInformation;
        retval.doNotRecommendBestPrice = config.doNotRecommendBestPrice;

        List<BookingItemType> bookingItemTypes = bookingEngine.getBookingItemTypesWithSystemType(null);
        HashMap<Integer, String> typesActive = new HashMap();
        for(BookingItemType type : bookingItemTypes) {
            typesActive.put(type.systemCategory, "");
        }
        return retval;
    }

    @Override
    public GuestAddonsSummary changeDateOnRoom(StartBooking arg) {
        arg.start = pmsInvoiceManager.normalizeDate(arg.start, true);
        arg.end = pmsInvoiceManager.normalizeDate(arg.end, false);
        
        PmsBooking booking = pmsManager.getCurrentBooking();
        PmsBookingRooms room = booking.getRoom(arg.roomId);
        if(room != null) {
            pmsManager.changeDates(arg.roomId, booking.id, arg.start, arg.end);
        }
        
        return generateSummary();
    }

    private Double getPriceForRoom(BookingProcessRooms bookingProcessRoom, Date start, Date end, int numberofguests, String discountcode) {
            PmsBookingRooms room = new PmsBookingRooms();
            room.bookingItemTypeId = bookingProcessRoom.id;
            room.date = new PmsBookingDateRange();
            room.date.start = start;
            room.date.end = end;
            room.numberOfGuests = numberofguests;
            
            PmsBooking booking = new PmsBooking();
            booking.priceType = PmsBooking.PriceType.daily;
            booking.couponCode = discountcode;
            booking.userId = bookingProcessRoom.userId;
            pmsManager.setPriceOnRoom(room, true, booking);
            return room.price;
    }

    @Override
    public BookingResult completeBookingForTerminal(CompleteBookingInput input) {
        PmsBooking booking = pmsManager.completeCurrentBooking();
        
        String orderId = pmsInvoiceManager.autoCreateOrderForBookingAndRoom(booking.id, null);
        booking = pmsManager.getBookingUnsecure(booking.id);
        booking.channel = "terminal";
        if(!booking.orderIds.contains(orderId)) {
            booking.orderIds.add(orderId);
        }
        
        User user = new User();
        user.fullName = booking.rooms.get(0).guests.get(0).name;
        user.emailAddress = booking.rooms.get(0).guests.get(0).email;
        user.cellPhone = booking.rooms.get(0).guests.get(0).phone;
        user.prefix = booking.rooms.get(0).guests.get(0).prefix;
        userManager.saveUser(user);
        booking.userId = user.id;
        
        pmsManager.saveBooking(booking);
        
//        pmsManager.calculateCountryFromPhonePrefix(booking);
        
        BookingResult res = new BookingResult();
        res.success = 1;
        if(res == null) {
            res.success = 0;
        } else {
            res.bookingid = booking.id;
            if(orderId != null) {
                res.continuetopayment = 1;
                res.orderid = orderId;
            } else {
                res.continuetopayment = 0;
            }
        }
        
        
        
        if(orderId != null && !orderId.isEmpty()) {
            Order order = orderManager.getOrderSecure(orderId);
            res.amount = orderManager.getTotalAmount(order);
            chargeOrder(orderId, input.terminalId + "");
        }
        res.goToCompleted = !storeManager.isProductMode();
        
        return res;
    }

    @Override
    public StartPaymentProcessResult startPaymentProcess(StartPaymentProcess data) {
        if(data == null || data.reference == null || data.reference.length() < 4) {
            return null;
        }
        
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.searchWord = data.reference;
        List<PmsBooking> bookings = pmsManager.getAllBookingsInternal(filter);
        
        if (bookings.isEmpty()) {
            PmsBooking book = pmsManager.getBookingWithOrderId(data.reference);
            if (book != null) {
                bookings.add(book);
            }
        }
        
        if(bookings.isEmpty()) {
            return null;
        }
        
        List<PmsBooking> tmpList = new ArrayList();
        for(PmsBooking b : bookings) {
            boolean notEnded = false;
            for(PmsBookingRooms r : b.getActiveRooms()) {
                if(!r.isEnded()) {
                    notEnded = true;
                }
            }
            if(notEnded) {
                tmpList.add(b);
            }
        }
        bookings = tmpList;
        
        
        boolean allPaidFor = true;
        for(PmsBooking booking : bookings) {
            pmsInvoiceManager.autoCreateOrderForBookingAndRoom(booking.id, null);
            for(PmsBookingRooms r : booking.rooms) {
                if(!pmsInvoiceManager.isRoomPaidFor(r.pmsBookingRoomId)) {
                    allPaidFor = false;
                }
            }
        }
        
        
        if (allPaidFor) {
            StartPaymentProcessResult result = new StartPaymentProcessResult();
            result.paidFor = true;
            result.pmsBookingId = bookings.get(0).id;
            return result;
        }
        
        for(PmsBooking booking : bookings) {
            if(booking.getEndDate().before(new Date())) {
                continue;
            }
            if(booking.isDeleted || booking.getActiveRooms().isEmpty()) {
                continue;
            }
            
            if(booking.payedFor) {
                continue;
            }
            double amount = -1.0;
            String orderIdToReturn = null;
            for(String orderId : booking.orderIds) {
                Order order = orderManager.getOrderSecure(orderId);
                if(order != null && order.status != Order.Status.PAYMENT_COMPLETED) {
                    if(order.isExpedia() || order.isBookingCom()) {
                        continue;
                    }
                    amount = orderManager.getTotalAmount(order);
                    if(amount > 0) {
                        orderIdToReturn = order.id;
                        chargeOrder(order.id, data.terminalid);
                    }
                }
                
            }
            if(amount > 0) {
                StartPaymentProcessResult result = new StartPaymentProcessResult();
                User user = userManager.getUserById(booking.userId);
                result.name = user.fullName;
                result.amount = amount;
                result.orderId = orderIdToReturn;
                return result;
            }
        }
        
        return null;
    }

    @Override
    public void cancelPaymentProcess(StartPaymentProcess data) {
        if(!storeManager.isProductMode()) {
            if(isVerifone) {
                verifoneManager.getTerminalMessages().add("payment failed");
                verifoneManager.removeOrderToPay();
            } else {
                orderManager.getTerminalMessages().add("payment failed");
                orderManager.removeOrderToPay();
            }
            return;
        }
        PaymentTerminalSettings settings = paymentTerminalManager.getSetings(1);
        if(isVerifone) {
            verifoneManager.cancelPaymentProcess(settings.verifoneTerminalId);
        } else {
            Application app = applicationPool.getApplication("8edb700e-b486-47ac-a05f-c61967a734b1");
            String tokenId = app.getSetting("token0");
            orderManager.cancelPaymentProcess(tokenId);
        }
    }

    private Date correctToDayAfter(StartBooking arg) {
        Date start = arg.start;
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);
        
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(arg.end);
        endCal.set(Calendar.DAY_OF_YEAR, startCal.get(Calendar.DAY_OF_YEAR)+1);
        endCal.set(Calendar.YEAR, startCal.get(Calendar.YEAR));
        return endCal.getTime();
    }

    private List<String> checkForSupportedPaymentMethods(PmsBooking booking) {
        if(getSession() != null && getSession().currentUser != null) {
            return getSession().currentUser.enabledPaymentOptions;
        }
        
        if(booking.userId != null && !booking.userId.isEmpty()) {
            User bookinguser = userManager.getUserById(booking.userId);
            return bookinguser.enabledPaymentOptions;
        }
        
        return new ArrayList();
    }

    private boolean isMidleOfNight() {
        Date now = pmsManager.getConfigurationSecure().getCurrentTimeInTimeZone();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int hourofday = cal.get(Calendar.HOUR_OF_DAY);
        if(hourofday < 7) {
            return true;
        }
        return false;
    }

    @Override
    public void chargeOrderWithVerifoneTerminal(String orderId, String terminalId) {
        isVerifone = true;
        PaymentTerminalSettings settings = paymentTerminalManager.getSetings(1);
        verifoneManager.chargeOrder(orderId, settings.verifoneTerminalId, testTerminalPaymentTerminal);
    }
    
    public void chargeIntegratedTerminal(String orderId, String terminalId) {
        Integer paymentTerminalId = getpaymentTerminalId(terminalId);
        isVerifone = false;
        testTerminalPaymentTerminal = true;
        Application app = applicationPool.getApplication("8edb700e-b486-47ac-a05f-c61967a734b1");
        String tokenId = app.getSetting("token" + paymentTerminalId);
        orderManager.chargeOrder(orderId, tokenId); 
   }

    @Override
    public List<String> getTerminalMessages() {
        ArrayList retList = null;
        if(isVerifone) {
            retList = new ArrayList<String>(verifoneManager.getTerminalMessages());
            verifoneManager.getTerminalMessages().clear();
        } else {
            retList = new ArrayList<String>(orderManager.getTerminalMessages());
            orderManager.getTerminalMessages().clear();
        }
        return retList;
    }

    @Override
    public void addTestMessagesToQueue(String message) {
        if(storeManager.isProductMode()) {
            return;
        }
        if(isVerifone) {
            verifoneManager.getTerminalMessages().add(message);
        } else {
            orderManager.getTerminalMessages().add(message);
        }
        if (message.equals("payment failed") || message.equals("completed")) {
            
            if (message.equals("completed")) {
                if(isVerifone) {
                    verifoneManager.markOrderInProgressAsPaid();
                } else {
                    orderManager.markOrderInProgressAsPaid();
                }
            }
            if(isVerifone) {
                verifoneManager.removeOrderToPay();
            } else {
                orderManager.removeOrderToPay();
            }
        }
    }

    @Override
    public List<BookingProcessRoomStatus> getBooking(String pmsBookingId) {
        PmsBooking booking = pmsManager.getBookingUnsecure(pmsBookingId);
        Date now = new Date();
        List<BookingProcessRoomStatus> retList = new ArrayList();
        
        if (booking != null) {
            booking.rooms.stream()
                .forEach(room -> {
                    BookingProcessRoomStatus status = new BookingProcessRoomStatus();
                    status.checkin = room.date.start;
                    status.checkout = room.date.end;
                    status.checkinDateOk = room.isStarted() && !room.isEnded();
                    if (room.bookingItemId == null || room.bookingItemId.isEmpty()) {
                        status.roomIsClean = false;
                    } else {
                        status.roomIsClean = room.addedToArx;
                    }
                    status.paymentCompleted = pmsInvoiceManager.isRoomPaidFor(room.pmsBookingRoomId);
                    status.roomId = room.pmsBookingRoomId;
                    retList.add(status);
                });
        }
        
        return retList;
    }

    @Override
    public void setBookingItemToCurrentBooking(String roomId, String itemId) {
        String bookingId = pmsManager.getCurrentBooking().id;
        pmsManager.setBookingItem(roomId, bookingId, itemId, false);
    }

    @Override
    public void quickChangeGuestCountForRoom(String roomId, int guestCount) {
        PmsBooking booking = pmsManager.getCurrentBooking();
        
        PmsBookingRooms room = booking.getRoom(roomId);
        if (room != null) {
            room.numberOfGuests = guestCount;
        }
        
        try {
            pmsManager.setBooking(booking);
        }catch(Exception e) {
            logPrintException(e);
        }
    }
    
    @Override
    public GuestAddonsSummary changeGuestCountForRoom(String roomId, int guestCount) {
        quickChangeGuestCountForRoom(roomId, guestCount);
        return generateSummary();
    }

    private boolean shouldIgnoreDueToGroupAddonFunctionallity(PmsBookingAddonItem item, List<PmsBookingAddonItem> addons) {
        if (item.groupAddonType != null && !item.groupAddonType.isEmpty()) {
            return true;
        }
        
        List<String> productIdsPartOfAGroupAddon = addons.stream()
                .filter(o -> o.groupAddonType != null && !o.groupAddonType.isEmpty() && o.groupAddonType.equals("option"))
                .flatMap(o -> o.groupAddonSettings.groupProductIds.stream())
                .collect(Collectors.toList());
        
        return productIdsPartOfAGroupAddon.contains(item.productId);
    }

    private void addGroupAddons(GuestAddonsSummary result) {
        for (RoomInfo room : result.rooms) {
            List<PmsBookingAddonItem> addons = pmsManager.getAddonsWithDiscount(room.roomId);
            
            List<PmsBookingAddonItem> groupAddonsForEachGuest = addons.stream()
                    .filter(item -> item.groupAddonType != null && item.groupAddonType.equals("option"))
                    .filter(item -> item.displayInBookingProcess.contains(room.bookingItemTypeId))
                    .filter(item -> item.dependsOnGuestCount)
                    .collect(Collectors.toList());
            
            for (PmsBookingAddonItem groupAddon : groupAddonsForEachGuest) {
                List<AddonItem> addonsAvailable = addons
                        .stream()
                        .filter(item -> groupAddon.groupAddonSettings.groupProductIds.contains(item.productId))
                        .filter(item -> item.displayInBookingProcess.contains(room.bookingItemTypeId))
                        .map(item -> {
                            String curLang = getSession().language;
                            AddonItem addonItem = new AddonItem();
                            addonItem.setAddon(item);
                            addonItem.name = productManager.getProduct(item.productId).name;
                            String translatedName = item.getTranslationsByKey("name", curLang);
                            if(translatedName != null && !translatedName.isEmpty()) {
                                addonItem.name = translatedName;
                            }
                            String translatedDesc = item.getTranslationsByKey("descriptionWeb", curLang);
                            if(translatedDesc != null && !translatedDesc.isEmpty()) {
                                addonItem.descriptionWeb = translatedDesc;
                            }
                            return addonItem;
                        })
                        .collect(Collectors.toList());
                
                AddonItem addonItem = new AddonItem();
                addonItem.setAddon(groupAddon);
                
                PmsBookingAddonItem originalAddon = pmsManager.getBaseAddon(groupAddon.productId);

                String curLang = getSession().language;
                String translatedDesc = originalAddon.getTranslationsByKey("descriptionWeb", curLang);
                if(translatedDesc != null && !translatedDesc.isEmpty()) {
                    addonItem.descriptionWeb = translatedDesc;
                }

                
                addonItem.name = productManager.getProduct(groupAddon.productId).name;
                String translatedName = groupAddon.getTranslationsByKey("name", curLang);
                if(translatedName != null && !translatedName.isEmpty()) {
                    addonItem.name = translatedName;
                }
                GroupAddonItem groupAddonItem = new GroupAddonItem();
                groupAddonItem.mainItem = addonItem;
                groupAddonItem.items = addonsAvailable;
                
                room.availableGuestOptionAddons.add(groupAddonItem);
            }
            
        }
    }

    private void setSessionLanguage(String language) {
        if(language.equals("en")) {
            language = "en_en";
        }
        if(language.equals("no")) {
            language = "nb_NO";
        }
        storeManager.setSessionLanguage(language);
    }

    private GuestInfo createGuestInfo(PmsGuests guest) {
        GuestInfo info = new GuestInfo();
        info.name = guest.name;
        info.email = guest.email;
        info.prefix = guest.prefix;
        info.phone = guest.phone;
        info.isChild = guest.isChild;
        info.selectedOptions = guest.orderedOption;
        return info;
    }

    private void chargeOrder(String orderId, String terminalid) {
        String ipaddr = getIpadressOnTerminal(new Integer(terminalid));
        
        if(ipaddr != null && !ipaddr.isEmpty()) {
            chargeOrderWithVerifoneTerminal(orderId, terminalid);
        } else {
            chargeIntegratedTerminal(orderId, terminalid);
        }
    }

    private void printRecieptIntegratatedTerminal(String orderId, Integer terminalId) {
        String deviceId = getGsdDeviceId(terminalId);
        invoiceManager.sendReceiptToCashRegisterPoint(deviceId, orderId);
        pmsManager.processor();
        Order order = orderManager.getOrderSecure(orderId);
        PmsBooking booking = pmsManager.getBookingWithOrderId(order.id);
        for(PmsBookingRooms room : booking.getActiveRooms()) {
            if(!order.containsRoom(room.pmsBookingRoomId)) {
                continue;
            }
            if(room.bookingItemId != null && !room.bookingItemId.isEmpty() && room.addedToArx) {
                pmsManager.printCode(deviceId, room.pmsBookingRoomId);
            }
        }
    }

    @Override
    public String addBookingItemType(String bookingId, String type, Date start, Date end, String guestInfoFromRoom) {
        return pmsManager.addBookingItemType(bookingId, type, start, end, guestInfoFromRoom, false);
        
    }

    @Override
    public boolean hasPrintCodeSupportOnTerminal() {
        if(storeId.equals("ccf3c29b-62d9-4099-ae85-c300f85492b4")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean printCodeOnTerminal(String roomId, String phoneNumber, Integer terminalId) {
        if(!hasPrintCodeSupportOnTerminal()) {
            return false;
        }
        
        PmsBooking booking = pmsManager.getBookingFromRoomSecure(roomId);
        PmsBookingRooms room = booking.getRoom(roomId);
        User usr = userManager.getUserById(booking.userId);
        boolean foundnumber = false;
        if(usr.cellPhone != null && usr.cellPhone.equals(phoneNumber)) {
            foundnumber = true;
        }
        
        for(PmsGuests r : room.guests) {
            if(r.phone != null && r.phone.equals(phoneNumber)) {
                foundnumber = true;
            }
        }
        
        if(foundnumber) {
            String deviceId = getGsdDeviceId(terminalId);
            pmsManager.printCode(deviceId, roomId);
        }
        
        return foundnumber;
    }

    private String getGsdDeviceId(Integer terminalId) {
        Integer paymentTerminalId = getpaymentTerminalId(terminalId + "");
        Application app = applicationPool.getApplication("8edb700e-b486-47ac-a05f-c61967a734b1");
        String tokenId = app.getSetting("token" + paymentTerminalId); 
        GetShopDevice device = gdsManager.getDeviceByToken(tokenId);
        return device.id;
    }
    
    private Integer getpaymentTerminalId(String terminalId) {
        Application app = applicationPool.getApplication("6dfcf735-238f-44e1-9086-b2d9bb4fdff2");
        Integer paymentTerminalId = 0;
        if(app != null) { 
            paymentTerminalId = new Integer(app.getSetting("terminalid" + terminalId)); 
        }
        return paymentTerminalId;
    }

    private String getIpadressOnTerminal(Integer terminalId) {
        Application app = applicationPool.getApplication("6dfcf735-238f-44e1-9086-b2d9bb4fdff2");
        String ipaddr = "";
        if(app != null) {
            ipaddr = app.getSetting("ipaddr" + terminalId);
        }
        return ipaddr;
    }


    @Override
    public void simpleCompleteCurrentBooking() {
        PmsBooking booking = pmsManager.getCurrentBooking();
        pmsManager.simpleCompleteCurrentBooking();
//        pmsManager.calculateCountryFromPhonePrefix(booking);
    }

    private void addCouponPricesToRoom(StartBookingResult result, StartBooking arg) {
        if(arg.discountCode != null && !arg.discountCode.isEmpty()) {
            Coupon coupon = cartManager.getCoupon(arg.discountCode);
            if(coupon != null && coupon.addonsToInclude != null) {
               for(AddonsInclude inc : coupon.addonsToInclude) {
                   if(inc.includeInRoomPrice) {
                       continue;
                   }
                   
                   Product product = productManager.getProduct(inc.productId);
                   
                   for(BookingProcessRooms tmp : result.rooms) {
                       for(Integer guests : tmp.pricesByGuests.keySet()) {
                           double price = tmp.pricesByGuests.get(guests);
                           price += product.price * guests;
                           tmp.pricesByGuests.put(guests, price);
                       }
                   }
               }
            }
        }
    }

    private void checkIfCouponIsValid(StartBookingResult result, StartBooking arg) {
        boolean removeAvailability = false;
        List<String> roomsToRemove = new ArrayList();
        if(arg.discountCode != null && !arg.discountCode.isEmpty()) {
            Coupon coupon = cartManager.getCoupon(arg.discountCode);
            if(coupon != null) {
                for(BookingProcessRooms r : result.rooms) {
                    BookingItemType type = bookingEngine.getBookingItemType(r.id);
                    if(!cartManager.couponIsValid(new Date(), arg.discountCode, arg.start,arg.end, type.productId, arg.getNumberOfDays())) {
                        roomsToRemove.add(r.id);
                        removeAvailability = true;
                    }

                    if(removeAvailability && roomsToRemove.size() == result.rooms.size()) {
                        result.errorMessage = "outsideperiode::";
                    }

                }


                if(coupon.minDays > 0 && coupon.minDays > arg.getNumberOfDays()) {
                    result.errorMessage = "min_days:{arg}:" + coupon.minDays;
                    removeAvailability = true;
                }
                if(coupon.maxDays > 0 && coupon.maxDays < arg.getNumberOfDays()) {
                    result.errorMessage = "max_days:{arg}:" + coupon.maxDays;
                    removeAvailability = true;
                }
            }
            if(removeAvailability) {
                removeAllRooms(result, roomsToRemove);
            }
        }
    }

    private void removeAllRooms(StartBookingResult result, List<String> types) {
        for(BookingProcessRooms r : result.rooms) {
            if(!types.isEmpty() && !types.contains(r.id)) {
                continue;
            }
            r.availableRooms = 0;
            r.roomsSelectedByGuests = new HashMap();
        }
        result.roomsSelected = 0;
        
        
        PmsBooking currentbooking = pmsManager.getCurrentBooking();
        currentbooking.rooms.clear();
        try {
            pmsManager.setBooking(currentbooking);
        }catch(Exception e) {
            logPrintException(e);
        }     
    }

    private void checkForRestrictions(StartBookingResult result, StartBooking arg) {
        boolean remove = false;
        List<String> types = new ArrayList();
        for(BookingProcessRooms r : result.rooms) {
            if (pmsManager.isRestricted(r.id, arg.start, arg.end, TimeRepeaterData.TimePeriodeType.min_stay)) {
                remove = true;
                result.errorMessage = "min_days:{arg}:" + pmsManager.getLatestRestrictionTime();
                types.add(r.id);
            }
            if (pmsManager.isRestricted(r.id, arg.start, arg.end, TimeRepeaterData.TimePeriodeType.max_stay)) {
                remove = true;
                result.errorMessage = "max_days:{arg}:" + pmsManager.getLatestRestrictionTime();
                types.add(r.id);
            }
        }
        if(types.size() > 0) {
            removeAllRooms(result, types);
        }
    }

    private void addAddonsIncluded(StartBookingResult result, StartBooking arg) {
        
        if(!storeId.equals("ba845b2d-2293-4afc-91f1-eef47db7f8ca")) {
            return;
        }
        
        List<PmsBookingAddonItem> addons = pmsManager.getAddonsAvailable();
        for(PmsBookingAddonItem addon : addons) {
            if(addon.isIncludedInRoomPrice) {
                continue;
            }
            if(addon.includedInBookingItemTypes.size() > 0 && addon.isValidForPeriode(arg.start, arg.end, new Date())) {
                System.out.println(addon.productId + " : " + addon.includedInBookingItemTypes.size());
                for(BookingProcessRooms tmp : result.rooms) {
                    if(!addon.includedInBookingItemTypes.contains(tmp.id)) {
                        continue;
                    }
                    
                    for(Integer guests : tmp.pricesByGuests.keySet()) {
                        double price = tmp.pricesByGuests.get(guests);
                        if(addon.isSingle) {
                            price += addon.price;
                        } else {
                            price += addon.price * guests;
                        }
                        tmp.pricesByGuests.put(guests, price);
                    }
                }
            }
            
        }
    }

    @Override
    public CategoriesSummary getAllCategories() {
        CategoriesSummary result = new CategoriesSummary();
        try {
            List<BookingItemType> types = bookingEngine.getBookingItemTypesWithSystemType(null);
            for(BookingItemType type : types) {
                CategoriesSummaryEntry entry = new CategoriesSummaryEntry();
                entry.name = type.name;
                entry.categoryId = type.id;
                PmsAdditionalTypeInformation additiontal;
                    additiontal = pmsManager.getAdditionalTypeInformationById(type.id);
                entry.description = type.descriptionTranslations.get(getSession().language);
                if(entry.description == null || entry.description.isEmpty()) {
                    for(String k : type.descriptionTranslations.values()) {
                        if(k != null && !k.isEmpty()) {
                            entry.description = k;
                        }
                    }
                }
                if(entry.description == null || entry.description.isEmpty()) {
                    entry.description = type.description;
                }
                result.entries.put(entry.categoryId, entry);
                entry.images = additiontal.images;
            }
        } catch (Exception ex) {
            logPrintException(ex);
        }
        return result;
    }

    @Override
    public HashMap<Integer, Double> getPricesForRoom(Date start, Date end, String itemId) {
        HashMap<Integer,Double> result = new HashMap();
        BookingItem item = bookingEngine.getBookingItem(itemId);
        BookingItemType type = bookingEngine.getBookingItemType(item.bookingItemTypeId);
        Double price = pmsManager.getPriceForRoomWhenBooking(start, end, item.bookingItemTypeId);
        PmsPricing priceobject = pmsManager.getPrices(start, end);
        HashMap<Integer, Double> derivedPrices = priceobject.derivedPrices.get(type.id);
        Double additionalPrice = 0.0;
        for(int i = 1; i <= type.size; i++) {
            if(derivedPrices != null && derivedPrices.containsKey(i)) {
                additionalPrice += derivedPrices.get(i);
            }
            result.put(i, price + additionalPrice);
        }
        
        return result;
    }

    @Override
    public String addBookingItem(String bookingId, String type, Date start, Date end, String guestInfoFromRoom, String bookingItemId) {
        start = pmsManager.getConfigurationSecure().getDefaultStart(start);
        end = pmsManager.getConfigurationSecure().getDefaultEnd(end);
        
        String roomId = addBookingItemType(bookingId, type, start, end, guestInfoFromRoom);
        
        PmsBooking booking = pmsManager.getBooking(bookingId);
        PmsBookingRooms room = booking.getRoom(roomId);
        room.bookingItemId = bookingItemId;
        
        pmsManager.saveBooking(booking);
        
        return roomId;
    }
}
