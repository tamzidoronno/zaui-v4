/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.mongodb.*;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.storemanager.data.Store;
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
import java.util.Set;
import org.mongodb.morphia.Morphia;
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
    
    private boolean sandbox = false;

    @Autowired
    private StorePool storePool;
    
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

    private synchronized boolean isDeepFreezed(DataCommon data) {
        String storeId = data.storeId;
        if(data instanceof Store) {
            storeId = data.id;
        }
        Store store = storePool.getStore(storeId);
        if (store != null && store.isDeepFreezed) {
            return true;
        }
        
        return false;
    }
    
    public synchronized void save(DataCommon data, Credentials credentials) throws ErrorException {
        if (data.rowCreatedDate == null) {
            data.rowCreatedDate = new Date();
        }
        
        if (isDeepFreezed(data)) {
            return;
        }
        
        saveWithOverrideDeepfreeze(data, credentials);
    }
    
    public synchronized void saveWithOverrideDeepfreeze(DataCommon data, Credentials credentials) throws ErrorException {
        checkId(data);
        data.onSaveValidate();

        if (sandbox) {
            return;
        }

        addDataCommonToDatabase(data, credentials);
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
        DB mongoDb = mongo.getDB(credentials.manangerName);
        DBCollection collection = mongoDb.getCollection("col_" + credentials.storeid);
        return getData(collection);
    }

    private List<DataCommon> getData(DBCollection collection) {
        DBCursor cur = collection.find();
        List<DataCommon> all = new ArrayList<DataCommon>();
        while (cur.hasNext()) {
            DBObject dbObject = cur.next();
            String className = (String) dbObject.get("className");
            if (className != null) {
                try {
                    Class.forName(className);
                } catch (ClassNotFoundException ex) {
                    logger.warning(this, "Database object has references to object that does not exists: " + className + " collection: " + collection.getName() + " manager: " + collection.getDB().getName());
                    continue;
                }
            }

            try {
                DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, dbObject);
                if (dataCommon.deleted == null) {
                    all.add(dataCommon);
                }
            } catch (Exception ex) {
                System.out.println("Figure out this : " + collection.getName() + " " + collection.getDB().getName());
                System.out.println(dbObject);
                ex.printStackTrace();
            }
        }
        cur.close();
        return all;
    }

    public synchronized void delete(DataCommon data, Credentials credentials) throws ErrorException {
        if (sandbox) {
            return;
        }
        
        if (isDeepFreezed(data)) {
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

        if (searchCriteria != null) {
            for (String key : searchCriteria.keySet()) {
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

    public DatabaseSyncMessage getSyncMessage() {
        DatabaseSyncMessage syncMessage = new DatabaseSyncMessage();
        List<String> databases = mongo.getDatabaseNames();
        for (String managerName : databases) {
            if (managerName.equals("LoggerManager")) {
                continue;
            }
            Set<String> collectionNames = mongo.getDB(managerName).getCollectionNames();
            for (String colName : collectionNames) {
                DBCollection collection = mongo.getDB(managerName).getCollection(colName);
                ManagerData data = new ManagerData();
                data.collection = colName;
                data.database = managerName;
                data.datas = getData(collection);
                syncMessage.managerDatas.add(data);
            }
        }
        return syncMessage;
    }

    public DataCommon findObject(String uuid, String manager) {
        List<String> dbs = mongo.getDatabaseNames();
        for (String db : dbs) {
            if(db.equals("LoggerManager")) {
                continue;
            }
            
            if(manager != null && !db.equals(manager)) {
                continue;
            }
            
            DB tmpdb = mongo.getDB(db);
            Set<String> collections = tmpdb.getCollectionNames();
            for (String collection : collections) {
                DBCollection tmpcollection = tmpdb.getCollection(collection);
                BasicDBObject field = new BasicDBObject();
                field.put("_id", uuid);
                DBCursor res = tmpcollection.find(field);
                if (res.size() == 1) {
                    Morphia morphia = new Morphia();
                    morphia.map(DataCommon.class);
                    return morphia.fromDBObject(DataCommon.class, res.next());
                }
            }
        }
        return new DataCommon();
    }

    public void save(DatabaseSyncMessage sync) {
        for (ManagerData managerData : sync.managerDatas) {
            DBCollection col = mongo.getDB(managerData.database).getCollection(managerData.collection);
            for (DataCommon data : managerData.datas) {
                DBObject dbObject = morphia.toDBObject(data);
                col.save(dbObject);
            }
        }
    }
}
class DataCommonSorter implements Comparator<DataCommon> {

    @Override
    public int compare(DataCommon o1, DataCommon o2) {
        return o1.rowCreatedDate.compareTo(o2.rowCreatedDate);
    }
}
