/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.storemanager.data.StoreConfiguration;
import com.thundashop.core.storemanager.data.TranslationObject;
import java.net.UnknownHostException;
import java.util.List;

/**
 *
 * @author boggi
 */
public class GetTranslationFromStore {
    public static void main(String args[]) throws UnknownHostException, ErrorException {
        Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("StoreManager");
        DBCollection collection = db.getCollection("col_all");
        DBCursor stores = collection.find();
        while (stores.hasNext()) {
            DBObject store = stores.next();
            String id = (String) store.get("_id");
            if(id.equals("de4ab020-cc36-4be9-af58-281c7b8d1189")) {
                DBObject storeconf = (DBObject) store.get("configuration");
                DBObject matrix = (DBObject) storeconf.get("translationMatrix");
                if(matrix == null) {
                    System.out.println("No translation found for this web shop");
                    System.exit(0);
                }
                for(String key : matrix.keySet()) {
                    DBObject translationObject = (DBObject) matrix.get(key);
                    System.out.println(translationObject.get("key") + ";-;" + translationObject.get("value"));
                }
            }
        }
    }
}
