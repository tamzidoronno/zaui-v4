/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager.updatescripts;

import org.mongodb.morphia.Morphia;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.thundashop.core.common.AppConfiguration;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.listmanager.data.EntryList;
import com.thundashop.core.listmanager.data.ListType;
import java.net.UnknownHostException;
import java.util.Set;

/**
 *
 * @author ktonder
 */
public class UpdateListTypes {
    public static void main(String args[]) throws UnknownHostException {
        Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("PageManager");
        DB listManagerDB = m.getDB("ListManager");
        Set<String> collectionNames = db.getCollectionNames();
        for (String collectionName : collectionNames) {
            DBCollection collection = db.getCollection(collectionName);
            DBCursor stores = collection.find();
            while (stores.hasNext()) {
                DBObject data = stores.next();
                Morphia morphia = new Morphia();
                morphia.map(DataCommon.class);
                DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, data);
                DBCollection listCollection = listManagerDB.getCollection("col_"+data.get("storeId"));
                
                if (dataCommon instanceof AppConfiguration) {
                    AppConfiguration app = (AppConfiguration)dataCommon;
                    // TOPMENU
                    if (app.appSettingsId != null && app.appSettingsId.equals("1051b4cf-6e9f-475d-aa12-fc83a89d2fd4")) {
                        DBCursor lists = listCollection.find();
                        while(lists.hasNext()) {
                            DBObject dbList = lists.next();
                            DataCommon listCommon = morphia.fromDBObject(DataCommon.class, dbList);
                            if (listCommon instanceof EntryList) {
                                EntryList entryList = (EntryList)listCommon;
                                if (entryList.appId != null && entryList.appId.equals(app.id)) {
                                    entryList.type = ListType.MENU;
                                    entryList.name = "TopMenu";
                                    DBObject saveIt = morphia.toDBObject(entryList);
                                    listCollection.save(saveIt);
                                }
                            }
                        }
                    }
                    
                    // LeftMenu
                    if (app.appSettingsId != null && app.appSettingsId.equals("00d8f5ce-ed17-4098-8925-5697f6159f66")) {
                        DBCursor lists = listCollection.find();
                        while(lists.hasNext()) {
                            DBObject dbList = lists.next();
                            DataCommon listCommon = morphia.fromDBObject(DataCommon.class, dbList);
                            if (listCommon instanceof EntryList) {
                                EntryList entryList = (EntryList)listCommon;
                                if (entryList.appId != null && entryList.appId.equals(app.id)) {
                                    entryList.type = ListType.MENU;
                                    entryList.name = "LeftMenu";
                                    DBObject saveIt = morphia.toDBObject(entryList);
                                    listCollection.save(saveIt);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
