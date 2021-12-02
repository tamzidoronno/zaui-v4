package com.thundashop.repository;

import com.thundashop.repository.db.MongoClientProvider;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.db.EntityMappersImpl;
import com.thundashop.repository.testutils.TestConfig;
import com.thundashop.repository.utils.Config;

import java.net.UnknownHostException;
import java.util.UUID;

public abstract class TestCommon {

    public static Database database;

    public static void init() {
        Config config = TestConfig.newInstance();

        try {
            MongoClientProvider provider = MongoClientProvider.builder()
                    .setHost(config.getAsString("mongo.host"))
                    .setPort(config.getAsInt("mongo.port"))
                    .build();

            database = Database.of(provider, new EntityMappersImpl());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public SessionInfo buildSessionInfo() {
        return SessionInfo.builder()
                .setStoreId(UUID.randomUUID().toString())
                .setCurrentUserId("testUserId")
                .setLanguage("en")
                .build();
    }

}
