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
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.ApplicationInstance;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.storemanager.data.StoreConfiguration;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author boggi
 */
public class UpgradeBase {

    public HashMap<String, ApplicationInstance> getAppAplications() throws UnknownHostException {
        HashMap<String, ApplicationInstance> retval = new HashMap();
        Mongo m = new Mongo("localhost", 27018);
        DB db = m.getDB("PageManager");

        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);

        Set<String> collections = db.getCollectionNames();
        for (String colection : collections) {
            DBCollection colectioninstance = db.getCollection(colection);
            DBCursor documents = colectioninstance.find();
            while (documents.hasNext()) {
                DBObject document = documents.next();
                DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, document);
                if (dataCommon instanceof ApplicationInstance) {
                    ApplicationInstance app = (ApplicationInstance) dataCommon;
                    retval.put(app.id, app);
                }
            }
        }
        m.close();
        return retval;
    }
    
    public List<DataCommon> getDataFromDatabase(Class manager, String storeId) throws UnknownHostException {
        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);
        
        Mongo m = new Mongo("localhost", 27018);
        DB db = m.getDB(manager.getSimpleName());
        DBCollection collection = db.getCollection("col_"+storeId);
        List<DataCommon> retstores = new ArrayList();
        DBCursor documents = collection.find();
        
        while (documents.hasNext()) {
            DBObject dbObject = documents.next();
            DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, dbObject);
            retstores.add(dataCommon);
        }
        
        m.close();
        return retstores;
    }

    public List<Application> getAllAppSettings() throws UnknownHostException {
        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);
        
        Mongo m = new Mongo("localhost", 27018);
        DB db = m.getDB("ApplicationPool");
        DBCollection collection = db.getCollection("col_all");
        List<Application> retstores = new ArrayList();
        DBCursor documents = collection.find();
        
        while (documents.hasNext()) {
            DBObject dbObject = documents.next();
            DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, dbObject);
            if (dataCommon instanceof Application) {
                retstores.add((Application)dataCommon);
            }
        }
        
        m.close();
        return retstores;
        
    }
    
    public List<Store> getAllStores() throws UnknownHostException {
        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);
        
        Mongo m = new Mongo("localhost", 27018);
        DB db = m.getDB("StoreManager");
        DBCollection collection = db.getCollection("col_all");
        List<Store> retstores = new ArrayList();
        DBCursor documents = collection.find();
        
        while (documents.hasNext()) {
            DBObject dbObject = documents.next();
            DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, dbObject);
            if (dataCommon instanceof Store) {
                retstores.add((Store)dataCommon);
            }
        }
        
        m.close();
        return retstores;
    }
     
    public void saveObject(DataCommon object, String manager) throws UnknownHostException {
        Mongo m = new Mongo("localhost", 27018);
        DB db = m.getDB(manager);
        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);

        if (object.storeId == null) {
            System.out.println("No store id set");
            System.exit(0);
        }
        if (object.id == null || object.id.equals("")) {
            object.id = UUID.randomUUID().toString();
        }

        db.getCollection("col_" + object.storeId).save(morphia.toDBObject(object));
        m.close();
        try {
            Thread.sleep(20);
        }catch(Exception e) {}
    }

    public HashMap<String, HashMap<String, Page>> getAllPages() throws UnknownHostException {
        HashMap<String, HashMap<String, Page>> retval = new HashMap();
        Mongo m = new Mongo("localhost", 27018);
        DB db = m.getDB("PageManager");

        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);

        Set<String> collections = db.getCollectionNames();
        for (String colection : collections) {
            DBCollection colectioninstance = db.getCollection(colection);
            DBCursor documents = colectioninstance.find();
            while (documents.hasNext()) {
                DBObject document = documents.next();
                try {
                    DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, document);
                    if (dataCommon instanceof Page) {
                        Page page = (Page) dataCommon;
                        if(!retval.containsKey(colection)) {
                            retval.put(colection, new HashMap());
                        }
                        retval.get(colection).put(page.id, page);
                    }
                }catch(Exception e) {
                    continue;
                }
            }
        }
        m.close();
        return retval;
    }

    public HashMap<String, Product> getAllProducts() throws UnknownHostException {
        HashMap<String, Product> retval = new HashMap();
        Mongo m = new Mongo("localhost", 27018);
        DB db = m.getDB("ProductManager");

        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);

        Set<String> collections = db.getCollectionNames();
        for (String colection : collections) {
            DBCollection colectioninstance = db.getCollection(colection);
            DBCursor documents = colectioninstance.find();
            while (documents.hasNext()) {
                DBObject document = documents.next();
                DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, document);
                if (dataCommon instanceof Product) {
                    Product prod = (Product) dataCommon;
                    retval.put(prod.id, prod);
                }
            }
        }
        m.close();
        return retval;
    }


    public ApplicationInstance createApp(String appName, String appId) {
        String appid = UUID.randomUUID().toString();
        ApplicationInstance appConfiguration = new ApplicationInstance();
        appConfiguration.id = appid;
        appConfiguration.appSettingsId = appId;
        return appConfiguration;
    }
}
