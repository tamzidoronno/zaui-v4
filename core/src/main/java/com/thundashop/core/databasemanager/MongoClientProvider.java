package com.thundashop.core.databasemanager;

import com.mongodb.MongoClient;

public interface MongoClientProvider {

    MongoClient getMongoClient();

}