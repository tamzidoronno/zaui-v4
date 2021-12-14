package com.thundashop.core.databasemanager;

import com.mongodb.DBObject;
import com.mongodb.Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class WubookLogCleanerDB {

    private final Mongo mongo;

    @Autowired
    public WubookLogCleanerDB(@Qualifier("localMongo") MongoClientProvider provider) {
        this.mongo = provider.getMongoClient();
    }

    public Set<String> getAllCollection(String manager) {
        return mongo.getDB(manager).getCollectionNames();
    }

    public int deleteByQuery(String manager, String collectionName, DBObject query) {
        return mongo.getDB(manager)
                .getCollection(collectionName)
                .remove(query)
                .getN();
    }


}
