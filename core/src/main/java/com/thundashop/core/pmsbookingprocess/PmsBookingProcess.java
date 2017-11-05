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
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsInvoiceManager;
import com.thundashop.core.pmsmanager.PmsManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    @Override
    public StartBookingResult startBooking(StartBooking object) {
        
        object.start = pmsInvoiceManager.normalizeDate(object.start, true);
        object.end = pmsInvoiceManager.normalizeDate(object.end, false);
        
        StartBookingResult result = new StartBookingResult();
        List<BookingItemType> types = bookingEngine.getBookingItemTypes();
        PmsBooking existing = pmsManager.getCurrentBooking();
        
        for(BookingItemType type : types) {
            if(!type.visibleForBooking) {
                continue;
            }
            BookingProcessRooms room = new BookingProcessRooms();
            room.description = type.description;
            room.availableRooms = bookingEngine.getNumberOfAvailable(type.id, object.start, object.end);
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
                    Double price = pmsInvoiceManager.calculatePrice(type.id, object.start, object.end, true, existing);
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
        
        result.totalAmount = existing.getTotalPrice();
        
        return result;
    }
    
}
