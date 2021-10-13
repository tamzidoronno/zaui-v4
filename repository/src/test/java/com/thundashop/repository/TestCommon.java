package com.thundashop.repository;

import com.thundashop.repository.common.SessionInfo;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.db.EntityMappersImpl;
import com.thundashop.repository.testutils.TestConfig;
import com.thundashop.repository.utils.Config;

import java.util.UUID;

public abstract class TestCommon {

    public static Database database;

    public static void init() {
        Config config = TestConfig.newInstance();
        database = Database.of(config.getAsString("mongo.host"),
                config.getAsInt("mongo.port"), new EntityMappersImpl());
    }

    public SessionInfo buildSessionInfo() {
        return SessionInfo.builder()
                .setStoreId(UUID.randomUUID().toString())
                .setCurrentUserId("testUserId")
                .setLanguage("en")
                .build();
    }

}
