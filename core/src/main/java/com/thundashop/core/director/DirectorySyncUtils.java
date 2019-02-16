/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.director;

import com.getshop.scope.GetShopSessionScope;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsGuests;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.system.GetShopSystem;
import com.thundashop.core.system.SystemManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ktonder
 */
public class DirectorySyncUtils {
   
    private GetShopSessionScope scope; 
    
    private SystemManager systemManager;
    
    private UserManager userManager;
    
    private StorePool storePool;

    public DirectorySyncUtils(GetShopSessionScope scope, SystemManager systemManager, UserManager userManager, StorePool storePool) {
        this.scope = scope;
        this.systemManager = systemManager;
        this.userManager = userManager;
        this.storePool = storePool;
    }
    
    public void createSystems() {
        PmsManager pmsManager = scope.getNamedSessionBean("default", PmsManager.class);
        BookingEngine bookingEngine = scope.getNamedSessionBean("default", BookingEngine.class);
        
        for (PmsBooking booking : pmsManager.getAllBookingsFlat()) {
            User user = userManager.getUserById(booking.userId);
            if (user.companyObject != null) {
                for (PmsBookingRooms room : booking.rooms) {
                    if (room.guests.isEmpty() || room.isDeleted())
                        continue;
                    
                    PmsGuests guest = room.guests.get(0);
                    BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
                    
                    GetShopSystem system = systemManager.getSystem(room.pmsBookingRoomId);
                    if (system != null) {
                        continue;
                    }
                    
                    system = new GetShopSystem();
                    system.id = room.pmsBookingRoomId;
                    
                    system.activeFrom = room.date.start;
                    
                    if (new Date().after(room.date.end)) {
                        system.activeTo = room.date.end;
                    }
                    
                    system.companyId = user.companyObject.id;
                    system.monthlyPrice = room.price; // TODO
                    system.systemName = type.name;
                    system.productId = type.productId;
                    system.webAddresses = guest.name;
                    system.serverVpnIpAddress = "10.0.4.33";
                    system.invoicedTo = room.invoicedTo;
                    system.numberOfMonthsToInvoice = booking.periodesToCreateOrderOn == null ? 1 : booking.periodesToCreateOrderOn;
                    
                    Store store = storePool.getStoreByWebaddress(system.webAddresses);
                    if (store != null) {
                        system.remoteStoreId = store.id;
                    }
                    
                    systemManager.saveSystem(system);
                }
                
            }
        }
    }
}
