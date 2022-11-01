package com.thundashop.repository;

import java.net.UnknownHostException;
import java.util.UUID;

import com.thundashop.repository.config.Config;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.db.MongoClientProvider;
import com.thundashop.repository.db.MongoClientProviderImpl;
import com.thundashop.repository.testutils.TestConfig;
import com.thundashop.repository.utils.SessionInfo;

public abstract class TestCommon {

    public static Database database;

    public static void init() {
        Config config = TestConfig.newInstance();

        try {
            MongoClientProvider provider = MongoClientProviderImpl.builder()
                    .setHost(config.getAsString("mongo.host"))
                    .setPort(config.getAsInt("mongo.port"))
                    .build();

            database = new Database(provider);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public SessionInfo buildSessionInfo(String dbName) {
        return SessionInfo.builder()
                .setStoreId(UUID.randomUUID().toString())
                .setCurrentUserId("testUserId")
                .setLanguage("en")
                .setManagerName(dbName)
                .build();
    }

}
