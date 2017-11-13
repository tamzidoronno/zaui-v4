/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.storemanager.StoreManager;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import static org.apache.commons.lang3.time.DateUtils.isSameDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static org.apache.commons.lang3.time.DateUtils.isSameDay;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class PmsSelfManagement extends GetShopSessionBeanNamed implements IPmsSelfManagement {
    @Autowired
    private PmsManager pmsManager;
    
    @Autowired
    private OrderManager orderManager;
    
    @Autowired
    private StoreManager storeManager;
    
    @Autowired
    private CartManager cartManager;
    
    @Override
    public PmsBooking getBookingById(String id) {
        storeManager.currentSecretId = id;
        return pmsManager.getBookingBySecretId(id);
    }

    @Override
    public Order getOrderById(String id, String orderId) {
        storeManager.currentSecretId = id;
        PmsBooking booking = getBookingById(id);
        
        if (booking == null) {
            return null;
        }
        
        if (!booking.orderIds.contains(orderId)) {
            return null;
        }
        
        return orderManager.getOrderSecure(orderId);
    }
    
    @Override
    public List<PmsBookingAddonItem> getAddonsWithDiscountForBooking(String id, String pmsBookingRoomId) {
        storeManager.currentSecretId = id;
        return pmsManager.getAddonsWithDiscountForBooking(pmsBookingRoomId);
    }

    @Override
    public void saveAddonSetup(String id, List<PmsSelfManageAddon> addons) {
        storeManager.currentSecretId = id;
        
        PmsBooking booking = getBookingById(id);
        
        if (booking == null) {
            return;
        }
        
        
        for (PmsSelfManageAddon addon : addons) {
            PmsBookingRooms room = booking.rooms.stream()
                .filter(b -> b.pmsBookingRoomId.equals(addon.roomId))
                .findFirst()
                .orElse(null);
            
            if (room == null) {
                continue;
            }

            for (AddonDay day : addon.days) {
                if (day.active && !addonAlreadyAdded(addon, room, day)) {            
                    addAddonToRoom(addon, day, room);
                }
                
                if (!day.active && addonAlreadyAdded(addon, room, day) && !isAddonPaidFor(addon, room, day)) {
                    removeAddonFromRoom(addon, room, day);
                }
            }
        }
        
        triggerCreateOrder(booking);
    }

    private void removeAddonFromRoom(PmsSelfManageAddon addon, PmsBookingRooms room, AddonDay day) {
        PmsBookingAddonItem addonItem = getAlreadyAdded(addon, room, day);
        if (pmsManager.isAddonPaidFor(addonItem)) {
            return;
        }
        pmsManager.removeAddonFromRoomById(addonItem.addonId, addon.roomId);
    }

    private void addAddonToRoom(PmsSelfManageAddon addon, AddonDay day, PmsBookingRooms room) {
        PmsBookingAddonItem pmsAddon = pmsManager.getAddonsWithDiscountForBooking(addon.roomId)
                .stream()
                .filter(o -> o.productId.equals(addon.productId))
                .findAny()
                .orElse(null);
        
        pmsAddon.date = day.date;
        if(pmsAddon.dependsOnGuestCount) {
            pmsAddon.count = room.numberOfGuests;
        } else {
            pmsAddon.count = 1;
        }
        pmsManager.addAddonToRoom(addon.roomId, pmsAddon);
    }

    private void triggerCreateOrder(PmsBooking booking) throws ErrorException {
        pmsManager.removeAllUnclosedOrders(booking.id);
        
        NewOrderFilter filter = new NewOrderFilter();

        pmsManager.createOrder(booking.id, filter);
    }
    
    @Override
    public Object preProcessMessage(Object object, Method executeMethod) {
        storeManager.currentSecretId = "";
        return super.preProcessMessage(object, executeMethod); 
    }

    private PmsBookingAddonItem getAlreadyAdded(PmsSelfManageAddon selfAddon, PmsBookingRooms room, AddonDay day) {
        for (PmsBookingAddonItem addon : room.addons) {
            if (addon.productId.equals(selfAddon.productId)) {
                if (isSameDay(addon.date, day.date)) {
                    return addon;
                }
            }
        }
        
        return null;   
    }
    
    private boolean addonAlreadyAdded(PmsSelfManageAddon selfAddon, PmsBookingRooms room, AddonDay day) {
        return getAlreadyAdded(selfAddon, room, day) != null;
    }

    private boolean isAddonPaidFor(PmsSelfManageAddon addon, PmsBookingRooms room, AddonDay day) {
        // Implement this.
        return false;
    }
    
    
}
