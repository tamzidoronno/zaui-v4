/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.google.code.morphia.Morphia;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.thundashop.core.common.AppConfiguration;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.listmanager.data.Entry;
import com.thundashop.core.listmanager.data.EntryList;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pagemanager.data.PageArea;
import com.thundashop.core.productmanager.data.Product;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author boggi
 */
public class ConvertProductListToUseListManager {
    
    public static void main(String args[]) throws UnknownHostException, ErrorException {
        Mongo m = new Mongo("localhost", 27017);
        //First, find all product lists in the system.
        DB db = m.getDB("PageManager");


        Set<String> collections = db.getCollectionNames();
        for (String colection : collections) {
            HashMap<String, AppConfiguration> pool = createApplicationPool(db, colection);
            
            //Now find pages which helds the apps.
            DBCollection selectedCollection = db.getCollection(colection);
            DBCursor allDocs = selectedCollection.find();
            while (allDocs.hasNext()) {
                DataCommon dataCommon = convert(allDocs.next());
                if (dataCommon instanceof Page) {
                    Page page = (Page) dataCommon;
                    for (String area : page.pageAreas.keySet()) {
                        PageArea pageArea = page.pageAreas.get(area);

                        for (String appId : pageArea.applicationsList) {
                            AppConfiguration app = pool.get(appId);
                            if (app != null) {
                                if (app.appName.equals("ProductList")) {
                                    createNewProductList(app.id, page.id, colection);
                                }
                            }
                        }
                    }

                }
            }
        }


    }

    private static HashMap<String, AppConfiguration> createApplicationPool(DB db, String colection) {
        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);

        //First build the application pool
        DBCollection selectedCollection = db.getCollection(colection);
        HashMap<String, AppConfiguration> pool = new HashMap();
        DBCursor allDocs = selectedCollection.find();
        while (allDocs.hasNext()) {
            DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, allDocs.next());
            if (dataCommon instanceof AppConfiguration) {
                AppConfiguration config = (AppConfiguration) dataCommon;
                pool.put(config.id, config);
            }
        }
        return pool;
    }

    private static void createNewProductList(String listId, String parentPageId, String collection) throws UnknownHostException {
        System.out.println("Building product list for id: " + listId);
        
        Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("ProductManager");
        
        DBCollection selectedCollection = db.getCollection(collection);
        DBCursor cursor = selectedCollection.find();
        List<Product> productList = new ArrayList();
        while(cursor.hasNext()) {
            DataCommon object = convert(cursor.next());
            if(object instanceof Product) {
                Product product = (Product) object;
                Page page = getPage(product.pageId, collection);
                if(page.parent != null && page.parent.id.equals(parentPageId)) {
                    productList.add(product);
                }
            }
        }
        
        m.close();
        
        m = new Mongo("localhost", 27017);
        db = m.getDB("ListManager");
        
        //We are finally ready to build it.
        EntryList list = new EntryList();
        list.appId = listId;
        list.entries = new ArrayList();
        list.storeId = collection.replace("col_", "");
        list.rowCreatedDate = new Date();
        list.id = UUID.randomUUID().toString();
        
        for(Product prod : productList) {
            Entry entry = new Entry();
            entry.name = prod.name;
            entry.productId = prod.id;
            entry.id = UUID.randomUUID().toString();

            list.entries.add(entry);
        }
        
        DBObject dbObject = toDbObject(list);
        m.getDB("ListManager").getCollection(collection).save(dbObject);
        m.close();
    }

    private static DataCommon convert(DBObject object) {
        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);
        DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, object);
        return dataCommon;
    }

    private static Page getPage(String pageId, String collection) throws UnknownHostException {
        Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("PageManager");
        
        DBCollection selectedCollection = db.getCollection(collection);
        BasicDBObject basic = new BasicDBObject();
        basic.put("_id", pageId);
        DBCursor cursor = selectedCollection.find(basic);
        DataCommon datacommon = convert(cursor.next());
        Page page = (Page)datacommon;
        
        m.close();
        
        return page;
    }

    private static DBObject toDbObject(DataCommon list) {
        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);
        return morphia.toDBObject(list);
    }
}
