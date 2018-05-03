/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists.
 *
 * @ktonder
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.mongodb.BasicDBObject;
import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.OrderLight;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingLight;
import com.thundashop.core.storemanager.data.Store;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class MakeLightWeightObjects extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("25/04-2018");
    }
    
    @Override
    public String getId() {
        return "c358beb9-c495-47cc-af21-e10d9aa8ff49";
    }
    
    public static void main(String[] args) {
        new MakeLightWeightObjects().runSingle();
    }
    
    @Override
    public void run() {
//        List<Store> stores = storePool.getAllStores();
//        System.out.println("Stores to update: " + stores.size());
//        stores.stream()
//                .forEach(store -> {
//                    BasicDBObject finder = new BasicDBObject();
//                    finder.put("className", "com.thundashop.core.ordermanager.data.Order");
//                    database.query("OrderManager", store.id, finder).forEach(dataCommon -> {
//                        if (dataCommon instanceof Order) {
//                            System.out.println("Creating a lightweight object");
//                            Order order = (Order)dataCommon;
//                            OrderLight light = new OrderLight(order);
//                            light.storeId = store.id;
//                            database.save("OrderManager", "col_"+store.id, light);
//                        } else {
//                            System.out.println("Data: " + dataCommon);
//                        }
//                    });
//                    
//                    finder = new BasicDBObject();
//                    finder.put("className", "com.thundashop.core.pmsmanager.PmsBooking");
//                    database.query("PmsManager_default", store.id, finder).forEach(dataCommon -> {
//                        if (dataCommon instanceof PmsBooking) {
//                            System.out.println("Creating a lightweight object Pms");
//                            PmsBooking booking = (PmsBooking)dataCommon;
//                            PmsBookingLight light = new PmsBookingLight();
//                            light.mainObjectId = booking.id;
//                            light.storeId = store.id;
//                            database.save("PmsManager_default", "col_"+store.id, light);
//                        } else {
//                            System.out.println("Data: " + dataCommon);
//                        }
//                    });
//                });
//        
        // Your magic code goes here :D
    }
}
