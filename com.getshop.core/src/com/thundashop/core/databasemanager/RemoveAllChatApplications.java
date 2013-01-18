/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.google.code.morphia.Morphia;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.thundashop.core.common.AppConfiguration;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import java.net.UnknownHostException;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class RemoveAllChatApplications {
    public static void main(String args[]) throws UnknownHostException, ErrorException {
        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);
        
        Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("PageManager");
        for (String colName : db.getCollectionNames()) {
            DBCollection collection = db.getCollection(colName);
            DBCursor pages = collection.find();
            while (pages.hasNext()) {
                DBObject  dbObject = pages.next();
                DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, dbObject);
                if (dataCommon instanceof AppConfiguration) {
                    AppConfiguration appConfig = (AppConfiguration)dataCommon;
                    if (appConfig.appName.equals("Chat")) {
                        appConfig.deleted = new Date();
                        collection.save(morphia.toDBObject(appConfig));
                    }
                }
            }
        }
    }
}