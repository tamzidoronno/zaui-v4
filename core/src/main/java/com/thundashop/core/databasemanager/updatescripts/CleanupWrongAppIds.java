/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists.
 *
 * @ktonder
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.thundashop.core.applications.StoreApplicationInstancePool;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.ApplicationInstance;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerSetting;
import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pagemanager.data.PageCell;
import com.thundashop.core.storemanager.data.Store;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class CleanupWrongAppIds extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("07/02-2016");
    }
    
    @Override
    public String getId() {
        return "9d3965e9-b756-4e5f-968f-eaab96061092";
    }
    
    public static void main(String[] args) {
        new CleanupWrongAppIds().runSingle();
    }
    
    @Override
    public void run() {
        Map<Store, List<DataCommon>> allData = getAllData(StoreApplicationInstancePool.class);
        Map<Store, List<DataCommon>> allData2 = getAllData(StoreApplicationPool.class);
        changeIt("83df5ae3_ee55_47cf_b289_f88ca201be6e", allData, allData2);
	changeIt("0acf824b_9dc4_4ad3_bb3c_3c8b9ffb1fa8", allData, allData2);
	changeIt("f4c3fce7_123c_4dcc_b9ce_dfea2ac6b755", allData, allData2);
	changeIt("49b1af76_d6e8_4f8a_a652_8f6ffab1812e", allData, allData2);
	changeIt("a30bbe8f_15f7_4327_9f80_ece81ebd64e8", allData, allData2);
	changeIt("9683701a_8217_411f_a321_26775f645f06", allData, allData2);
	changeIt("c7b87917_6a26_43cf_9876_7e3ef9eeab3e", allData, allData2);
    }

    private void changeIt(String origId, Map<Store, List<DataCommon>> allData,  Map<Store, List<DataCommon>> allData2) {
        Mongo mongo = database.getMongo();
        
        BasicDBObject document = new BasicDBObject();
        document.put("_id", origId);
        DBObject obj = mongo.getDB("ApplicationPool").getCollection("col_all").findOne(document);
        if (obj != null) {
            mongo.getDB("ApplicationPool").getCollection("col_all").remove(obj);

            String id = (String)obj.get("_id");
            obj.put("_id", id.replaceAll("_", "-"));
            mongo.getDB("ApplicationPool").getCollection("col_all").save(obj);
            System.out.println("updated: " + obj.get("_id"));
        }
        
        for (Store store : allData.keySet()) {
            List<DataCommon> appInstances = allData.get(store).stream()
                    .filter(o -> isApplicationInstance(o))
                    .collect(Collectors.toList());
            
            for (DataCommon data : appInstances) {
                ApplicationInstance instance = (ApplicationInstance)data;
                if (instance.appSettingsId.equals(origId)) {
                    instance.appSettingsId = origId.replaceAll("_", "-");
                    database.save(StoreApplicationInstancePool.class, data);
                    System.out.println("Fixed");
                }
            }
        }
        
        for (Store store : allData2.keySet()) {
            List<DataCommon> appInstances = allData2.get(store).stream()
                    .filter(o -> o.getClass() == ManagerSetting.class)
                    .collect(Collectors.toList());
            
            for (DataCommon data : appInstances) {
                ManagerSetting setting = (ManagerSetting)data;
                if (setting.keys.get(origId) != null) {
                    setting.keys.put(origId.replaceAll("_", "-"), setting.keys.get(origId));
                    database.save(StoreApplicationPool.class, setting);
                }
            }
        }
    }

    private boolean isApplicationInstance(DataCommon o) {
        return o instanceof ApplicationInstance;
    }
}
