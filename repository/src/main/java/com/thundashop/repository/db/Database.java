package com.thundashop.repository.db;

import com.mongodb.*;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.pmsmanager.PmsLog;
import org.mongodb.morphia.Morphia;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

public class Database {

    private final Mongo mongo;

    private final Morphia morphia;

    private Database(String host, int port) {
        try {
            mongo = new Mongo(host, port);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        morphia = new Morphia();
        morphia.getMapper().getConverters().addConverter(BigDecimalConverter.class);
        morphia.map(DataCommon.class);
    }

    public static Database of(String host, int port) {
        return new Database(host, port);
    }

    public DataCommon save(String dbName, String collectionName, DataCommon data) {
        notNull(data, "DataCommon should not be null");
        DBObject dbObject = morphia.toDBObject(data);

        mongo.getDB(dbName)
                .getCollection(collectionName)
                .save(dbObject);

        return data;
    }

    public List<DataCommon> query(String dbName, String collectionName, DBObject query) {
        return query(dbName, collectionName, query, new BasicDBObject(), Integer.MAX_VALUE);
    }

    public List<DataCommon> query(String dbName, String collectionName, DBObject query, DBObject orderBy, int limit) {
        DBCollection col = mongo.getDB(dbName).getCollection(collectionName);
        List<DataCommon> retObjects = new ArrayList<>();

        try (DBCursor res = col.find(query).sort(orderBy).limit(limit)) {
            while (res.hasNext()) {
                DBObject it = res.next();
                DataCommon data = morphia.fromDBObject(DataCommon.class, it);
                retObjects.add(data);
            }
        }

        return retObjects;
    }

    public void dropDatabase(String dbName) {
        mongo.dropDatabase(dbName);
    }

}
