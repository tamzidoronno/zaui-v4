package com.thundashop.repository.db;

import com.mongodb.*;
import com.thundashop.core.common.DataCommon;
import org.mongodb.morphia.Morphia;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

public class Database {

    private final Mongo mongo;

    private final Morphia morphia;

    private Database(String host, int port, EntityMappers mappers) {
        try {
            mongo = new Mongo(host, port);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        morphia = new Morphia();
        morphia.getMapper().getConverters().addConverter(BigDecimalConverter.class);
        morphia.map(mappers.getEntities());
    }

    public static Database of(String host, int port, EntityMappers mappers) {
        return new Database(host, port, mappers);
    }

    public DataCommon save(String dbName, String collectionName, DataCommon data) {
        notNull(data, "DataCommon should not be null");
        DBObject dbObject = morphia.toDBObject(data);

        mongo.getDB(dbName)
                .getCollection(collectionName)
                .save(dbObject);

        return data;
    }

    public <T> List<T> query(String dbName, String collectionName, Class<T> entityClass, DBObject query) {
        return query(dbName, collectionName, entityClass, query, new BasicDBObject(), Integer.MAX_VALUE);
    }

    public <T> List<T> query(String dbName, String collectionName, Class<T> entityClass, DBObject query,
                             DBObject orderBy, int limit) {
        DBCollection col = mongo.getDB(dbName).getCollection(collectionName);
        List<T> retObjects = new ArrayList<>();

        try (DBCursor res = col.find(query).sort(orderBy).limit(limit)) {
            while (res.hasNext()) {
                DBObject it = res.next();
                T data = morphia.fromDBObject(entityClass, it);
                retObjects.add(data);
            }
        }

        return retObjects;
    }

    public void dropDatabase(String dbName) {
        mongo.dropDatabase(dbName);
    }

    public void update(String dbName, String collectionName, BasicDBObject searchQuery, BasicDBObject setQuery) {
        DBCollection col = mongo.getDB(dbName).getCollection(collectionName);
        col.update(searchQuery, setQuery);
    }
}
