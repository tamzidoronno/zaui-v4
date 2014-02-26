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
import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.storemanager.data.Store;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.UUID;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author ktonder
 */
public class CopyStore {

    public static void main(String args[]) throws UnknownHostException, ErrorException {
        String originalStoreId = "b1671790-ac57-40eb-9523-ce02d87159f4";
        String newAddress = "utvikautoservice.getshop.com";
        String newStoreId = UUID.randomUUID().toString();
        
        ApplicationContext context = new ClassPathXmlApplicationContext("All.xml");
        AppContext.appContext = context;
        Database database = context.getBean(Database.class);

        Mongo m = new Mongo("localhost", 27017);

        for (String databaseName : m.getDatabaseNames()) {
            if (databaseName.equals("LoggerManager")) {
                continue;
            }

            String storeId = databaseName.equals("StoreManager") ? "all" : originalStoreId;
            String newStoreIdi = databaseName.equals("StoreManager") ? "all" : newStoreId;
            DB db = m.getDB(databaseName);
            DBCollection collection = db.getCollection("col_" + storeId);
            DBCursor stores = collection.find();

            Credentials cred = new Credentials(null);
            cred.manangerName = databaseName;
            cred.password =  newStoreId;
            cred.storeid = newStoreId;
            
            while (stores.hasNext()) {
                DBObject data = stores.next();
                Morphia morphia = new Morphia();
                morphia.map(DataCommon.class);
                DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, data);
                dataCommon.storeId = newStoreId;
                
                if (dataCommon instanceof Store) {
                    Store store = (Store)dataCommon;
                    
                    if (!store.id.equals(originalStoreId)) {
                        continue;
                    }
                    
                    System.out.println("Store: " + store.id + " Database: " +databaseName);
                    store.webAddressPrimary = newAddress;
                    store.webAddress = null;
                    store.storeId = newStoreIdi;
                    store.id = newStoreId;
                    store.additionalDomainNames = new ArrayList();
                    database.save(store, cred);
                } else {
                    database.save(dataCommon, cred);
                }
            }
        }
        
        System.out.println("Done setting up store, new storeId: "+ newStoreId);
        System.exit(-1);
    }
}
