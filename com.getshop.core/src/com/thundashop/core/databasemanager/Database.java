/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.google.code.morphia.Morphia;
import com.mongodb.*;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.databasemanager.data.Credentials;
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
    private boolean sandbox = false;

    public void activateSandBox() {
        sandbox = true;
    }

    public Database() throws UnknownHostException {
        mongo = new Mongo("localhost", 27017);
        morphia = new Morphia();
        morphia.map(DataCommon.class);
    }

    // Only used by unit tests.
    public void dropTables(Credentials credentials) throws SQLException {
        DBCollection collection = mongo.getDB(credentials.manangerName).getCollection(collectionPrefix + "3987asd8f2");
        collection.remove(new BasicDBObject());

        collection = mongo.getDB(credentials.manangerName + "_counter").getCollection(collectionPrefix + "3987asd8f2");
        collection.remove(new BasicDBObject());
    }

    public void Close() throws SQLException {
    }

    private void checkId(DataCommon data) throws ErrorException {
        if (data.id == null || data.id == "") {
            throw new ErrorException(64);
        }
    }

    private void checkSecurity(Credentials credentials) {
        // TODO, implement securitycheck for credentials.
    }

    public synchronized void save(DataCommon data, Credentials credentials) throws ErrorException {
        checkSecurity(credentials);
        checkId(data);
        data.onSaveValidate();

        if (sandbox) {
            return;
        }

        if (data.rowCreatedDate == null) {
            data.rowCreatedDate = new Date();
        }

        DBObject dbObject = morphia.toDBObject(data);
        mongo.getDB(credentials.manangerName).getCollection(collectionPrefix + data.storeId).save(dbObject);
    }

    public List<DataCommon> retreiveData(Credentials credentials) {
        checkSecurity(credentials);
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
}


class DataCommonSorter implements Comparator<DataCommon> {
    @Override
    public int compare(DataCommon o1, DataCommon o2) {
        return o1.rowCreatedDate.compareTo(o2.rowCreatedDate);
    }
}
