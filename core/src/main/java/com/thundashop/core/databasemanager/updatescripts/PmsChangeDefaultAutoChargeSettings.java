/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists.
 *
 * @ktonder
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.mongodb.BasicDBObject;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import com.thundashop.core.pmsmanager.PmsBookingAddonItem;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import com.thundashop.core.storemanager.data.Store;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class PmsChangeDefaultAutoChargeSettings extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("22/04-2019");
    }
    
    @Override
    public String getId() {
        return "ef27ce28-1660-4995-9d2b-609b648468f7";
    }
    
    public static void main(String[] args) {
        new PmsChangeDefaultAutoChargeSettings().runSingle();
    }
    
    @Override
    public void run() {
        
        List<Store> stores = storePool.getAllStores();
        for(Store store : stores) {
            List<String> names = database.getMultilevelNames("PmsManager", store.id);
            for(String name : names) {
                BasicDBObject query = new BasicDBObject();
                List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
                obj.add(new BasicDBObject("className", "com.thundashop.core.pmsmanager.PmsConfiguration"));
                query.put("$and", obj);
        
                List<DataCommon> res = database.query("PmsManager_" + name, store.id, query);
                for(DataCommon tmp : res) {
                    if(tmp instanceof PmsConfiguration) {
                        boolean save = false;
                        PmsConfiguration config = (PmsConfiguration)tmp;
                        config.wubookAutoCharging = false;
                        database.save("PmsManager_" + name,"col_"+store.id, config);
                    }
                }
                
            }
        }
        
    }
}
