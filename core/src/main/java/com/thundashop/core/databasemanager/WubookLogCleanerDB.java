package com.thundashop.core.databasemanager;

import com.mongodb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.Set;

import static java.lang.String.format;

public class WubookLogCleanerDB {

    private static final Logger logger = LoggerFactory.getLogger(WubookLogCleanerDB.class);

    private final Mongo mongo;

    public WubookLogCleanerDB(String host, int port) {
        try {
            MongoClientOptions options = MongoClientOptions.builder()
                    .connectionsPerHost(1)
                    .build();
            mongo = new MongoClient(new ServerAddress(host, port), options);
        } catch (UnknownHostException e) {
            logger.error("host: {} , port: {}", host, port, e);
            throw new RuntimeException(format("host %s , port %d", host, port), e);
        }
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
