/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.messagemanager.ViewSmsHistory;
import com.thundashop.core.storemanager.data.Store;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 */
@Component
@GetShopSession
public class SmsHistoryManager extends GetShopSessionBeanNamed implements ISmsHistoryManager {

    @Autowired
    PmsManager pmsManager;
    
    @Override
    public void generateSmsUsage() {
        if(!storeId.equals("b703b793-c7f4-4803-83bb-106cab891d6c")) {
            return;
        }
        HashMap<String, String> stores = getStoresToCheck();
    }

    private HashMap<String, String> getStoresToCheck() {
        List<PmsBooking> bookings = pmsManager.getAllBookings(null);
        List<String> storesNotFound = new ArrayList();
        for(PmsBooking booking : bookings) {
            boolean saveBooking = false;
            for(PmsBookingRooms room : booking.getActiveRooms()) {
                if(room.isEnded()) {
                    continue;
                }
                if(room.guests.size() > 0) {
                    String storeId = getstoreId(room.guests.get(0).name);
                    if(storeId.isEmpty()) {
                        storeId = getstoreId(room.guests.get(0).name.replace("www.", ""));
                    }
                    if(storeId.isEmpty()) {
                        storeId = getstoreId(room.guests.get(0).name.replace("http://", ""));
                    }
                    if(storeId.isEmpty()) {
                        storeId = getstoreId(room.guests.get(0).name.replace("http://www.", ""));
                    }
                    if(storeId.isEmpty()) {
                        storeId = getstoreId(room.guests.get(0).name.replace("https://", ""));
                    }
                    if(storeId.isEmpty()) {
                        storeId = getstoreId(room.guests.get(0).name.replace("https://www.", ""));
                    }
                    
                    if(storeId.isEmpty()) {
                        storesNotFound.add(room.guests.get(0).name);
                    } else {
                        String productIdInner = "90621fc2-f584-4443-9918-9c4f87e59dd7";
                        String productIdOuter = "2f7a5b3d-cd5d-4f9c-83ba-b8e7dbad9256";
                        
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DAY_OF_MONTH, 1);
                        cal.set(Calendar.HOUR_OF_DAY, 12);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        
                        Date postingDate = cal.getTime();

                        cal.add(Calendar.MONTH, -1);
                        Integer month = cal.get(Calendar.MONTH)+1;
                        Integer year = cal.get(Calendar.YEAR);
                        
                        ViewSmsHistory history = new ViewSmsHistory(month, year);
                        ViewSmsHistory.SmsCounter res = history.viewByStoreId(database, storeId, room.guests.get(0).name);
                        
                        if(res.inner == 0 && res.outer == 0) {
                            continue;
                        }
                        
                        boolean doNot = false;
                        PmsBookingAddonItem toAddonInternal = null;
                        PmsBookingAddonItem toAddonExternal = null;
                        
                        for(PmsBookingAddonItem item : room.addons) {
                            if(!PmsBookingRooms.isSameDayStatic(item.date, postingDate)) {
                                continue;
                            }
                            if(item.productId.equals(productIdInner)) {
                                toAddonInternal = item;
                            }
                            if(item.productId.equals(productIdOuter)) {
                                toAddonExternal = item;
                            }

                        }
                        
                        if(doNot) {
                            continue;
                        }
                        
                        //Innland
                        if(toAddonInternal == null) {
                            PmsBookingAddonItem internal = pmsManager.getAddonByProductId(productIdInner);
                            toAddonInternal = pmsManager.createAddonToAdd(internal, postingDate, room);
                            room.addons.add(toAddonInternal);
                            toAddonInternal.count = res.inner;
                        }
                        
                        //Utland
                        if(toAddonExternal == null) {
                            PmsBookingAddonItem external = pmsManager.getAddonByProductId(productIdOuter);
                            toAddonExternal = pmsManager.createAddonToAdd(external, postingDate, room);
                            room.addons.add(toAddonExternal);
                            toAddonExternal.count = res.outer;
                        }
                        
                    
                        saveBooking = true;
                    }
                }
                
            }
            
            if(saveBooking) {
//                pmsManager.saveBooking(booking);
            }
            
            System.out.println("Stores not found");
            for(String notfound : storesNotFound) {
                System.out.println(notfound);
            }
            
            
        }
        return new HashMap();
    }

    private String getstoreId(String name) {
        Store store = storePool.getStoreByWebaddress(name);
        if(store != null) {
            return store.id;
        }
        return "";
    }
    
}
