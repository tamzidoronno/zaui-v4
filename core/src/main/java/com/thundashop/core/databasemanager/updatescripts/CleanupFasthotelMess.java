/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists.
 *
 * @ktonder
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.core.bookingengine.BookingEngineAbstract;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import com.thundashop.core.storemanager.data.Store;
import java.util.Date;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class CleanupFasthotelMess extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("24/02-2017");
    }
    
    @Override
    public String getId() {
        return "abf0d884-9d61-44db-9ad1-2e584c61b8b6";
    }
    
    public static void main(String[] args) {
        new CleanupFasthotelMess().runSingle();
    }
    
    @Override
    public void run() {
        Store store = new Store();
        store.id = "e625c003-9754-4d66-8bab-d1452f4d5562";
        
        logOnStore(store);
        
        BookingEngineAbstract engine = getManager(BookingEngineAbstract.class, store, "demo");
        
        for (Booking booking : engine.getAllBookings()) {
            if (booking.bookingItemTypeId.equals("20ce418f-60d2-4630-a2c2-db8ef707d6df") && !booking.bookingItemId.isEmpty()) {
                booking.bookingItemTypeId = "6e2df470-3149-4611-803d-7b7a2e948225";
                booking.bookingItemId = "";
                engine.saveObject(booking);
            }
        }
        // Your magic code goes here :D
    }
}
