/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@GetShopSession
public class PmsAddonManager extends GetShopSessionBeanNamed implements IPmsAddonManager {
    
    @Autowired
    PmsManager pmsManager;
    
    @Autowired
    ProductManager productManager;
    
    @Override
    public void addProductToGroup(PmsAddonFilter filter) {
        if(filter.rooms == null || filter.rooms.isEmpty()) {
            return;
        }
        
        PmsBooking booking = pmsManager.getBookingFromRoom(filter.rooms.get(0));
        String singleAction = "multiple day entries";
        if(filter.singleDay) { singleAction = "single day entry"; }
        String deleteAction  ="added to booking";
        if(filter.deleteAddons) { deleteAction = "removed from bookings"; }
        Product product = productManager.getProduct(filter.productId);
        
        String logText = "Grouped add addons done between : " + filter.start + " to " + filter.end + " " + singleAction + ", " + deleteAction + " - Product: " + product.name;
        pmsManager.logEntry(logText, booking.id, null);
        Calendar cal = Calendar.getInstance();
        cal.setTime(filter.start);
        PmsBookingAddonItem addonConfig = pmsManager.getAddonByProductId(filter.productId);
        List<PmsBookingAddonItem> toRemove = new ArrayList();
        while(true) {
            for(PmsBookingRooms room : booking.rooms) {
                if(!filter.rooms.contains(room.pmsBookingRoomId)) {
                    continue;
                }
                if(filter.deleteAddons) {
                    for(PmsBookingAddonItem itm : room.addons) {
                        if(!itm.productId.equals(filter.productId)) {
                            continue;
                        }
                        if(PmsBookingRooms.isSameDayStatic(itm.date, cal.getTime())) {
                            toRemove.add(itm);
                        }
                    }
                } else {
                    PmsBookingAddonItem addon = pmsManager.createAddonToAdd(addonConfig, cal.getTime(), room);
                    room.addons.add(addon);
                }
            }
            if(filter.singleDay) {
                break;
            }
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if(cal.getTime().after(filter.end)) {
                break;
            }
        }
        
        for(PmsBookingRooms room : booking.rooms) {
            room.addons.removeAll(toRemove);
        }
        
        pmsManager.saveBooking(booking);
    }

    @Override
    public boolean isValidAddonForDate(Date date, String productId) {
        PmsBookingAddonItem item = pmsManager.getAddonByProductId(productId);
        if(item != null) {
            return item.isValidForPeriode(date, date, new Date());
        }
        return false;
    }
}
