/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists.
 *
 * @ktonder
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.storemanager.data.Store;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class UndeleteFailedDeletedBookings extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("27/01-2018");
    }
    
    @Override
    public String getId() {
        return "f8fa3f17-5756-4faf-b794-49093306c2c1";
    }
    
    public static void main(String[] args) {
        new UndeleteFailedDeletedBookings().runSingle();
    }
    
    @Override
    public void run() {
        // Your magic code goes here :D
        List<Store> stores = storePool.getAllStores();
        for(Store store : stores) {
            List<String> names = database.getMultilevelNames("PmsManager", store.id);
           for(String name : names) {
                List<DataCommon> data = getAllDataForStoreAndManager("PmsManager_" + name, store);
                for(DataCommon common : data) {
                    if(common instanceof PmsBooking) {
                        PmsBooking res = (PmsBooking)common;
                        if(res.deleted != null && res.deletedBySource != null && res.deletedBySource.equals("finalize") && (res.sessionId == null || res.sessionId.isEmpty())) {
                            res.deleted = null;
                            database.save("PmsManager_" + name, "col_" + store.id, common);
                        }
                    }
                }
           }
        }
    }
}
