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
import com.thundashop.core.pmsmanager.PmsAdditionalTypeInformation;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingAddonItem;
import com.thundashop.core.pmsmanager.PmsBookingDateRange;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsGuests;
import com.thundashop.core.pmsmanager.PmsInvoiceManager;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.productmanager.ProductManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    
    @Override
    public StartBookingResult startBooking(StartBooking arg) {
        
        if(arg.adults < arg.rooms) {
            return null;
        }
        pmsManager.startBooking();
        
        arg.start = pmsInvoiceManager.normalizeDate(arg.start, true);
        arg.end = pmsInvoiceManager.normalizeDate(arg.end, false);
        
        StartBookingResult result = new StartBookingResult();
        List<BookingItemType> types = bookingEngine.getBookingItemTypes();
        PmsBooking existing = pmsManager.getCurrentBooking();
        
        for(BookingItemType type : types) {
            if(!type.visibleForBooking) {
                continue;
            }
            BookingProcessRooms room = new BookingProcessRooms();
            room.description = type.description;
            room.availableRooms = bookingEngine.getNumberOfAvailable(type.id, arg.start, arg.end);
            room.id = type.id;
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
            
            room.utilities.put("wifi", "Wifi");
            room.utilities.put("shield", "Silent room");
            room.utilities.put("desktop", "Tv");
            
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
                toAddToCurrentBooking.date.start = arg.start;
                toAddToCurrentBooking.date.end = arg.end;
                
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
        PmsBooking booking = pmsManager.getCurrentBooking();
        GuestAddonsSummary result = new GuestAddonsSummary();
        for(PmsBookingRooms room : booking.rooms) {
            RoomInfo returnroom = new RoomInfo();
            returnroom.start = room.date.start;
            returnroom.end = room.date.end;
            returnroom.guestCount = room.numberOfGuests;
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
                returnroom.addonsAvailable.put(toAddAddon.productId, toAddAddon);
            }
            
            addons = pmsManager.getAddonsForRoom(room.pmsBookingRoomId);
            returnroom.addonsAdded.clear();
            for(PmsBookingAddonItem item : addons) {
                AddonItem toAddAddon = new AddonItem();
                toAddAddon.setAddon(item);
                toAddAddon.name = productManager.getProduct(item.productId).name;
                returnroom.addonsAdded.put(toAddAddon.productId, toAddAddon);
            }
            result.rooms.add(returnroom);
        }
        
        List<PmsBookingAddonItem> addonsOnBooking = pmsManager.getAddonsWithDiscountForBooking(booking.rooms.get(0).pmsBookingRoomId);
        for(PmsBookingAddonItem item : addonsOnBooking) {
            AddonItem toAddAddon = new AddonItem();
            toAddAddon.setAddon(item);
            toAddAddon.name = productManager.getProduct(item.productId).name;
            result.items.add(toAddAddon);
        }
        
        
        return result;
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
    
}
