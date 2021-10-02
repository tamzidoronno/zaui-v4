package com.thundashop.repository;

import com.thundashop.repository.db.Database;
import com.thundashop.repository.testutils.TestConfig;
import com.thundashop.repository.utils.Config;

public abstract class TestCommon {

    public static Database database;

    public static void init() {
        Config config = TestConfig.newInstance();
        database = Database.of(config.getAsString("mongo.host"), config.getAsInt("mongo.port"));
    }

}
