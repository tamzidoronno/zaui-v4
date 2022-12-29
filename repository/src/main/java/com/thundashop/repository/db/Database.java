package com.thundashop.repository.db;

import com.mongodb.*;
import com.thundashop.core.common.DataCommon;
import com.thundashop.repository.utils.ZauiMorphia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

@Component("repositoryDatabase")
public class Database {

    private final Mongo mongo;

    @Autowired
    private ZauiMorphia morphia;

    @Autowired
    public Database(@Qualifier("localMongo")MongoClientProvider provider) {
        mongo = provider.getMongoClient();        
    }    

    public DataCommon save(String dbName, String collectionName, DataCommon data) {
        notNull(data, "DataCommon should not be null");
        DBObject dbObject = morphia.toDBObject(data);

        mongo.getDB(dbName)
                .getCollection(collectionName)
                .save(dbObject);

        return data;
    }

    public <T> List<T> query(String dbName, String collectionName, DBObject query) {
        return query(dbName, collectionName, query, new BasicDBObject(), Integer.MAX_VALUE);
    }

    public <T> List<T> query(String dbName, String collectionName, DBObject query,
                             DBObject orderBy, int limit) {
        DBCollection col = mongo.getDB(dbName).getCollection(collectionName);
        List<T> retObjects = new ArrayList<>();

        try (DBCursor res = col.find(query).sort(orderBy).limit(limit)) {
            while (res.hasNext()) {
                DBObject it = res.next();
                DataCommon data = morphia.fromDBObject(it);
                retObjects.add((T) data);
            }
        }

        return retObjects;
    }

    public void dropDatabase(String dbName) {
        mongo.dropDatabase(dbName);
    }

    public int updateMultiple(String dbName, String collectionName, DBObject searchQuery, DBObject setQuery) {
        DBCollection col = mongo.getDB(dbName).getCollection(collectionName);
        return col.updateMulti(searchQuery, setQuery).getN();
    }

    public <T> List<T> distinct(String dbName, String collectionName, String field, DBObject searchQuery) {
        DBCollection col = mongo.getDB(dbName).getCollection(collectionName);
        return (List<T>) col.distinct(field, searchQuery);
    }

    public <T> T findFirst(String dbName, String collectionName, DBObject searchQuery) {
        DBObject result = mongo.getDB(dbName).getCollection(collectionName).findOne(searchQuery);
        return (T) morphia.fromDBObject(result);
    }

    public DataCommon update(String dbName, String collectionName, DBObject filter,  DataCommon update) {
        notNull(update, "DataCommon should not be null");
        DBObject dbObject = new BasicDBObject("$set", morphia.toDBObject(update));
        mongo.getDB(dbName)
                .getCollection(collectionName)
                .update(filter,dbObject);
        return update;
    }
}
