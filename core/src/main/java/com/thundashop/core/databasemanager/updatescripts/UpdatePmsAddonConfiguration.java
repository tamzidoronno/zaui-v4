/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists.
 *
 * @ktonder
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pmsmanager.PmsBookingAddonItem;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.storemanager.data.Store;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class UpdatePmsAddonConfiguration extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("16/12-2018");
    }
    
    @Override
    public String getId() {
        return "cf8ee45f-cd02-46b4-a102-50d566fcbe0b";
    }
    
    public static void main(String[] args) {
        new UpdatePmsAddonConfiguration().runSingle();
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
                        for(PmsBookingAddonItem item : config.addonConfiguration.values()) {
                            if(!item.includedInBookingItemTypes.isEmpty()) {
                                item.isIncludedInRoomPrice = true;
                                save = true;
                            }
                        }
                       if(save) {
                            database.save("PmsManager_" + name,"col_"+store.id, config);
                       }
                    }
                }
                
            }
        }
    }
}