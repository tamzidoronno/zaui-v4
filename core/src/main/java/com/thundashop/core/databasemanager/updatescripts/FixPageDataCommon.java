/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists.
 *
 * @ktonder
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pagemanager.data.PageLayout;
import com.thundashop.core.storemanager.data.Store;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.mongodb.morphia.annotations.Transient;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class FixPageDataCommon extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("28/03-2016");
    }
    
    @Override
    public String getId() {
        return "8b962236-2bdb-414d-8a35-b880bdb4a212";
    }
    
    public static void main(String[] args) {
        new FixPageDataCommon().runSingle();
    }
    
    @Override
    public void run() {
        
        Mongo mongo = database.getMongo();
        DB db = mongo.getDB("PageManager");
        for (String colName : db.getCollectionNames()) {
            DBCollection col = db.getCollection(colName);
            DBCursor cursor = col.find();
            while(cursor.hasNext()) {
                DBObject dbObject = cursor.next();
                DBObject layout = (DBObject) dbObject.get("layout");
                if (layout != null) {
                    DBObject areas = (DBObject) layout.get("areas");
                    if (areas != null) {
                        layout.put("body", areas.get("body"));
                        layout.put("areas", null);
                        dbObject.put("layout", layout);
                        col.save(dbObject);
                    }
                }
            }
        }
    }
}
