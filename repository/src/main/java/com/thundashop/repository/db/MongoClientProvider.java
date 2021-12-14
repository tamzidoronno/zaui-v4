package com.thundashop.repository.db;

import com.mongodb.MongoClient;

public interface MongoClientProvider {

    MongoClient getMongoClient();

}