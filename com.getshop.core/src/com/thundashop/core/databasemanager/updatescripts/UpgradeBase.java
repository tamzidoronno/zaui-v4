/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.google.code.morphia.Morphia;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.common.AppConfiguration;
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

    public HashMap<String, AppConfiguration> getAppAplications() throws UnknownHostException {
        HashMap<String, AppConfiguration> retval = new HashMap();
        Mongo m = new Mongo("localhost", 27017);
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
                if (dataCommon instanceof AppConfiguration) {
                    AppConfiguration app = (AppConfiguration) dataCommon;
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
        
        Mongo m = new Mongo("localhost", 27017);
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

    public List<ApplicationSettings> getAllAppSettings() throws UnknownHostException {
        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);
        
        Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("ApplicationPool");
        DBCollection collection = db.getCollection("col_all");
        List<ApplicationSettings> retstores = new ArrayList();
        DBCursor documents = collection.find();
        
        while (documents.hasNext()) {
            DBObject dbObject = documents.next();
            DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, dbObject);
            if (dataCommon instanceof ApplicationSettings) {
                retstores.add((ApplicationSettings)dataCommon);
            }
        }
        
        m.close();
        return retstores;
        
    }
    
    public List<Store> getAllStores() throws UnknownHostException {
        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);
        
        Mongo m = new Mongo("localhost", 27017);
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
        Mongo m = new Mongo("localhost", 27017);
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
    }

    public HashMap<String, HashMap<String, Page>> getAllPages() throws UnknownHostException {
        HashMap<String, HashMap<String, Page>> retval = new HashMap();
        Mongo m = new Mongo("localhost", 27017);
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
                if (dataCommon instanceof Page) {
                    Page page = (Page) dataCommon;
                    if(!retval.containsKey(colection)) {
                        retval.put(colection, new HashMap());
                    }
                    retval.get(colection).put(page.id, page);
                }
            }
        }
        m.close();
        return retval;
    }

    public HashMap<String, Product> getAllProducts() throws UnknownHostException {
        HashMap<String, Product> retval = new HashMap();
        Mongo m = new Mongo("localhost", 27017);
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

    public void addApplicationToPage(Page page, String area, String appId) throws ErrorException {
        page.getPageArea(area).applicationsList.add(appId);
    }

    public AppConfiguration createApp(String appName, String appId) {
        String appid = UUID.randomUUID().toString();
        AppConfiguration appConfiguration = new AppConfiguration();
        appConfiguration.id = appid;
        appConfiguration.appName = appName;
        appConfiguration.sticky = 0;
        appConfiguration.appSettingsId = appId;
        return appConfiguration;
    }
}
