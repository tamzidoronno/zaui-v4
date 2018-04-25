/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.getshop.scope.GetShopSession;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerSubBase;
import com.thundashop.core.common.SessionFactory;
import com.thundashop.core.common.StoreComponent;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.storemanager.StorePool;
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
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.mongodb.morphia.Morphia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class Database extends StoreComponent {

    public static int mongoPort = 27018;
//    public static int mongoPort = 27015;

    private Mongo mongo;
    private Morphia morphia;
    
    private String collectionPrefix = "col_";
    
    @Autowired
    public Logger logger;

    private boolean sandbox = false;

    @Autowired
    private StorePool storePool;
    
    @Autowired
    private BackupRepository backupRepository;

    public void activateSandBox() {
        sandbox = true;
    }

    public Mongo getMongo() {
        return mongo;
    }

    public <T> T get(String id, Class manager, String storeId, Class<T> type) {
        DBCollection collection = mongo.getDB(manager.getSimpleName()).getCollection(collectionPrefix + storeId);
        
        BasicDBObject query = new BasicDBObject();
	query.put("_id", id);
        
        DBObject object = collection.findOne(query);
        
        return morphia.fromDBObject(type, object);
    }
    
    public Database() throws UnknownHostException {
        try {
            createDataFolder();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        mongo = new Mongo("localhost", mongoPort);
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
        if (data.id == null || data.id.isEmpty()) {
            data.id = UUID.randomUUID().toString();
        }
    }

    private synchronized boolean isDeepFreezed(DataCommon data) {
        String storeId = data.storeId;
        if (data instanceof Store) {
            storeId = data.id;
        }
        Store store = storePool.getStore(storeId);
        if (store != null && store.isDeepFreezed) {
            return true;
        }

        return false;
    }

    private void createDataFolder() throws IOException {
        File file = new File("data");

        if (file.exists() && file.canWrite() && file.isDirectory()) {
            return;
        }

        if (file.exists() && !file.isDirectory()) {
            GetShopLogHandler.logPrintStatic("The file " + file.getPath() + " is not a folder", null);
            System.exit(-1);
        }

        file.mkdir();

        if (!file.exists()) {
            GetShopLogHandler.logPrintStatic("=======================================================================================================", null);
            GetShopLogHandler.logPrintStatic("Was not able to create folder " + file.getCanonicalPath(), null);
            GetShopLogHandler.logPrintStatic("=======================================================================================================", null);
            System.exit(-1);
        }

    }

    private void addDataCommonToDatabase(DataCommon data, Credentials credentials) {
//        logSavedMessge(data, credentials.manangerName, collectionPrefix + data.storeId);
        data.gs_manager = credentials.manangerName;
        DBObject dbObject = morphia.toDBObject(data);
        
        if (data.deepFreeze) {
            return;
        }
        
        mongo.getDB(credentials.manangerName).getCollection(collectionPrefix + data.storeId).save(dbObject);
    }

    public List<DataCommon> retreiveData(Credentials credentials) {
        DB mongoDb = mongo.getDB(credentials.manangerName);
        DBCollection collection = mongoDb.getCollection("col_" + credentials.storeid);
        return getData(collection);
    }

    public List<DataCommon> getAllDataForStore(String storeId) {
        ArrayList<DataCommon> datas = new ArrayList();

        for (String db : mongo.getDatabaseNames()) {
            DB mongoDb = mongo.getDB(db);
            for (String colName : mongoDb.getCollectionNames()) {
                if (colName.contains(storeId)) {
                    DBCollection collection = mongoDb.getCollection(colName);
                    DBCursor cur = collection.find();

                    while (cur.hasNext()) {
                        DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, cur.next());
                        dataCommon.gs_manager = mongoDb.getName();
                        dataCommon.colection = collection.getName();
                        datas.add(dataCommon);
                    }
                }
            }
        }

        return datas;
    }

    private List<DataCommon> getData(DBCollection collection) {
        BasicDBObject query = createQuery();
        
        DBCursor cur = collection.find(query);
        List<DataCommon> all = new ArrayList<DataCommon>();
        
        List<DBObject> dbObjects = new ArrayList();
        
        while (cur.hasNext()) {
            dbObjects.add(cur.next());
        }
        
        for (DBObject dbObject : dbObjects) {
            String className = (String) dbObject.get("className");
            if (className != null) {
                try {
                    Class.forName(className);
                } catch (ClassNotFoundException ex) {
//                    logger.warning(this, "Database object has references to object that does not exists: " + className + " collection: " + collection.getName() + " manager: " + collection.getDB().getName());
                    continue;
                }
            }

            try {
                DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, dbObject);
                if (dataCommon.deleted == null) {
                    dataCommon.colection = collection.getName();
                    dataCommon.gs_manager = collection.getDB().getName();
                    all.add(dataCommon);
                }
            } catch (ClassCastException ex) {
                // Nothing to do, the class probably been deleted but not the data in database.
            } catch (Exception ex) {
                GetShopLogHandler.logPrintStatic("Figure out this : " + collection.getName() + " " + collection.getDB().getName(), null);
                GetShopLogHandler.logPrintStatic(dbObject, null);
                ex.printStackTrace();
            }
        }
        cur.close();
        return all;
    }

    private BasicDBObject createQuery() {
        BasicDBObject andQuery = new BasicDBObject();
        List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
        obj.add(new BasicDBObject("deleted", null));
        
        obj.add(addBannedClass("com.thundashop.core.messagemanager.SmsLogEntry"));
        obj.add(addBannedClass("com.thundashop.core.messagehandler.data.MailSent"));
        andQuery.put("$and", obj);
        
        return andQuery;
        
    }

    private BasicDBObject addBannedClass(String bannedClassName) {
        BasicDBObject neQuery = new BasicDBObject();
        neQuery.put("className", new BasicDBObject("$ne", bannedClassName));
        return neQuery;
    }

    public synchronized void delete(Class mangagerClass, DataCommon data) {
        data.deleted = new Date();
        save(mangagerClass.getSimpleName(), collectionPrefix + data.storeId, data);
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
          return findWithDeleted(collection, startDate, endDate, db, searchCriteria, false);
    }

    public DataCommon getObject(Credentials credentials, String id) {
        return getObjectDirect(credentials.manangerName, collectionPrefix + credentials.storeid, id);
    }

    private DataCommon getObjectDirect(String database, String collectionName, String id) {
        DBCollection collection = mongo.getDB(database).getCollection(collectionName);
        DBObject searchById = new BasicDBObject("_id", id);
        DBObject found = collection.findOne(searchById);

        if (found == null) {
            return null;
        }

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
            if (db.equals("LoggerManager")) {
                continue;
            }

            if (manager != null && !db.equals(manager)) {
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
                    DataCommon common = morphia.fromDBObject(DataCommon.class, res.next());
                    if (common.deleted != null) {
                        return null;
                    }

                    return common;
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
                if (data.deepFreeze) {
                    continue;
                }
                col.save(dbObject);
            }
        }
    }

    public Stream<DataCommon> getAll(String dbName, String storeId) {
        DBCollection col = mongo.getDB(dbName).getCollection("col_" + storeId);
        return col.find().toArray().stream()
                .map(o -> morphia.fromDBObject(DataCommon.class, o));
    }

    public void refreshDatabase(List<DataCommon> datas) {
        for (DataCommon data : datas) {
            DBCollection col = mongo.getDB(data.gs_manager).getCollection(data.colection);
            DBObject obj = morphia.toDBObject(data);
            if (data.deepFreeze) {
                continue;
            }
            col.save(obj);
        }
    }

    public boolean exists(String database, String collection, DataCommon data) {
        DBCollection col = mongo.getDB(database).getCollection(collection);
        return col.findOne(data.id) != null;
    }

    public void save(Class managerClass, DataCommon data) {
        checkId(data);

        if (data.storeId == null || data.storeId.isEmpty()) {
            throw new RuntimeException("storeid not specified");
        }

        save(managerClass.getSimpleName(), collectionPrefix + data.storeId, data);
    }

    public void save(String database, String collection, DataCommon data) {
        checkId(data);
        DBCollection col = mongo.getDB(database).getCollection(collection);
        DBObject dbObject = morphia.toDBObject(data);
        
        if (data.rowCreatedDate == null) {
            data.rowCreatedDate = new Date();
        }

        logSavedMessge(data, database, collection);
        
        if (data.deepFreeze) {
            return;
        }
        
        col.save(dbObject);
    }

    public List<String> getMultilevelNames(String simpleName, String storeId) {
        List<String> dbsToCheck = mongo.getDatabaseNames().stream()
                .filter(name -> name.startsWith(simpleName))
                .collect(Collectors.toList());

        List<String> retValues = new ArrayList();
        for (String dbName : dbsToCheck) {
            DB db = mongo.getDB(dbName);
            if (db.collectionExists(collectionPrefix + storeId)) {
                if (dbName.split("_").length > 1) {
                    retValues.add(dbName.split("_")[1]);
                }
            }
        }

        return retValues;
    }

    public List<DataCommon> query(String manager, String storeId, DBObject query) {
        DB db = mongo.getDB(manager);
        DBCollection col = db.getCollection("col_" + storeId);
        DBCursor res = col.find(query);
        List<DataCommon> retObjecs = new ArrayList();
        while (res.hasNext()) {
            DBObject nx = res.next();
            DataCommon data = morphia.fromDBObject(DataCommon.class, nx);
            retObjecs.add(data);
        }

        return retObjecs;
    }

    /**
     * ************** SAVE FUNCTIONS ****************
     */
    public synchronized void save(DataCommon data, Credentials credentials) throws ErrorException {
        if (data.rowCreatedDate == null) {
            data.rowCreatedDate = new Date();
        }

        if (isDeepFreezed(data)) {
            return;
        }

        checkId(data);
        data.onSaveValidate();

        if (sandbox) {
            return;
        }

        addDataCommonToDatabase(data, credentials);
    }

    private void logSavedMessge(DataCommon newObject, String database, String collection) {
        String userId = "";
        if (getSession() != null && getSession().currentUser != null) {
            userId = getSession().currentUser.id;
        }
        
        if (database.contains("'")) {
            throw new RuntimeException("Database names are not allowed to contain '");
        }
        
        DataCommon oldObject = getObjectDirect(database, collection, newObject.id);
        if (oldObject != null) {
            backupRepository.saveBackup(userId, oldObject, storeId, database, collection);
        }

        
    }

    public boolean verifyThatStoreIdentifierNotInUse(String identifier) {
        DB db = mongo.getDB("StoreManager");
        
        for (String colName : db.getCollectionNames()) {
            DBCollection col = db.getCollection(colName);
            
            BasicDBObject finder = new BasicDBObject();
            finder.put("className", "com.thundashop.core.storemanager.data.Store");
            finder.put("identifier", identifier);
            
            int found = col.find(finder).size();
            if (found > 0) {
                return true;
            }
        }
        
        return false;
    }

    public List<DataCommon> findWithDeleted(String collection, Date startDate, Date endDate, String db, HashMap<String, String> searchCriteria, boolean incDeleted) {
        DB mongoDb = mongo.getDB(db);

        DBCollection dbCollection = mongoDb.getCollection(collection);
        BasicDBObject query = new BasicDBObject();
        if(startDate != null && endDate != null) {
            query.put("rowCreatedDate", BasicDBObjectBuilder.start("$gte", startDate).add("$lte", endDate).get());
        }
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
                boolean add = dataCommon.deleted == null;
                if(incDeleted) {
                    add = true;
                }
                if (add) {
                    all.add(dataCommon);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        Collections.sort(all, new DataCommonSorter());

        return all;    }
}

class DataCommonSorter implements Comparator<DataCommon> {

    @Override
    public int compare(DataCommon o1, DataCommon o2) {
        return o1.rowCreatedDate.compareTo(o2.rowCreatedDate);
    }
}
