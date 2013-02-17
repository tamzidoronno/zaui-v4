/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.google.code.morphia.Morphia;
import com.mongodb.*;
import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.StoreHandler;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class Database {

    private Mongo mongo;
    private Morphia morphia;
    private String collectionPrefix = "col_";
    
    @Autowired
    public Logger logger;
    
    @Autowired
    public DatabaseSocketHandler databaseSocketHandler;
    
    private boolean sandbox = false;

    public void activateSandBox() {
        sandbox = true;
    }

    public Database() throws UnknownHostException {
        try {
            createDataFolder();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        mongo = new Mongo("localhost", 27017);
        morphia = new Morphia();
        morphia.map(DataCommon.class);
    }

    public void dropTables(Credentials credentials) throws SQLException {
        DBCollection collection = mongo.getDB(credentials.manangerName).getCollection(collectionPrefix + "3987asd8f2");
        collection.remove(new BasicDBObject());

        collection = mongo.getDB(credentials.manangerName + "_counter").getCollection(collectionPrefix + "3987asd8f2");
        collection.remove(new BasicDBObject());
    }

    private void checkId(DataCommon data) throws ErrorException {
        if (data.id == null || data.id == "") {
            throw new ErrorException(64);
        }
    }

    public synchronized void save(DataCommon data, Credentials credentials) throws ErrorException {
        checkId(data);
        data.onSaveValidate();

        if (sandbox) {
            return;
        }

        if (data.rowCreatedDate == null) {
            data.rowCreatedDate = new Date();
        }

        addDataCommonToDatabase(data, credentials);
        databaseSocketHandler.objectSaved(data, credentials);
    }
    
    private void createDataFolder() throws IOException {
        File file = new File("data");
        
        if (file.exists() && file.canWrite() && file.isDirectory()) {
            return;
        }
            

        if (file.exists() && !file.isDirectory()) {
            System.out.println("The file " + file.getPath() + " is not a folder");
            System.exit(-1);
        }
        
        file.mkdir();
        
        if (!file.exists()) {
            System.out.println("=======================================================================================================");
            System.out.println("Was not able to create folder " + file.getCanonicalPath());
            System.out.println("=======================================================================================================");
            System.exit(-1);
        }
                
    }
    
    private void addDataCommonToDatabase(DataCommon data, Credentials credentials) {
        DBObject dbObject = morphia.toDBObject(data);
        mongo.getDB(credentials.manangerName).getCollection(collectionPrefix + data.storeId).save(dbObject);
    }

    public List<DataCommon> retreiveData(Credentials credentials) {
        List<DataCommon> all = new ArrayList<DataCommon>();
        DB mongoDb = mongo.getDB(credentials.manangerName);
        DBCollection collection = mongoDb.getCollection("col_" + credentials.storeid);
        DBCursor cur = collection.find();
        while (cur.hasNext()) {
            DBObject dbObject = cur.next();

            try {
                DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, dbObject);
                if (dataCommon.deleted == null) {
                    all.add(dataCommon);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Figure out this: " + credentials.manangerName);
                System.out.println(dbObject);
            }
        }

        return all;
    }

    public synchronized void delete(DataCommon data, Credentials credentials) throws ErrorException {
        if (sandbox) {
            return;
        }

        data.deleted = new Date();
        save(data, credentials);
    }

    public List<DataCommon> find(String collection, Date startDate, Date endDate, String db, HashMap<String, String> searchCriteria) {
        DB mongoDb = mongo.getDB(db);

        DBCollection dbCollection = mongoDb.getCollection(collection);
        BasicDBObject query = new BasicDBObject();
        query.put("rowCreatedDate", BasicDBObjectBuilder.start("$gte", startDate).add("$lte", endDate).get());
        DBCursor cursor = dbCollection.find(query);
        
        if(searchCriteria != null) {
            for(String key : searchCriteria.keySet()) {
                query.put(key, searchCriteria.get(key));
            }
        }

        List<DataCommon> all = new ArrayList<DataCommon>();
        while (cursor.hasNext()) {
            DBObject dbObject = cursor.next();

            try {
                DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, dbObject);
                if (dataCommon.deleted == null) {
                    all.add(dataCommon);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        Collections.sort(all, new DataCommonSorter());

        return all;

    }

    public void objectFromOtherSource(DataCommon dataCommon, Credentials credentials) {
        addDataCommonToDatabase(dataCommon, credentials);
        StoreHandler storeHandler = AppContext.storePool.getStorePool(credentials.storeid);
        ManagerBase managerBase = storeHandler.getManager(credentials.getManager());
        DataRetreived dataRetreived = new DataRetreived();
        dataRetreived.data = new ArrayList();
        dataRetreived.data.add(dataCommon);
        managerBase.dataFromDatabase(dataRetreived);
    }

    public DataCommon getObject(Credentials credentials, String id) {
        DBCollection collection = mongo.getDB(credentials.manangerName).getCollection(collectionPrefix + credentials.storeid);
        DBObject searchById = new BasicDBObject("_id", id);
        DBObject found = collection.findOne(searchById);
        
        try {
            return morphia.fromDBObject(DataCommon.class, found);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
}
class DataCommonSorter implements Comparator<DataCommon> {
    @Override
    public int compare(DataCommon o1, DataCommon o2) {
        return o1.rowCreatedDate.compareTo(o2.rowCreatedDate);
    }
}
