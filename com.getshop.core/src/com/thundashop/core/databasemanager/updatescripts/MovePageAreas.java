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
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pagemanager.data.PageArea;

import java.net.UnknownHostException;
import java.util.Set;

/**
 *
 * @author ktonder
 */
public class MovePageAreas {
    public static void main(String args[]) throws UnknownHostException {
        Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("PageManager");
        Set<String> collectionNames = db.getCollectionNames();
        for (String collectionName : collectionNames) {
            DBCollection collection = db.getCollection(collectionName);
            DBCursor stores = collection.find();
            while (stores.hasNext()) {
                DBObject data = stores.next();
                Morphia morphia = new Morphia();
                morphia.map(DataCommon.class);
                DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, data);
            
                if (dataCommon instanceof Page) {
                    Page page = (Page)dataCommon;
                    int j = 1;

                    DBObject dbObject = morphia.toDBObject(page);
                    collection.save(dbObject);
                }
            }
        }
    }
}
