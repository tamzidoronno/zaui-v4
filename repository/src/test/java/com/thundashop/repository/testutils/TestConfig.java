package com.thundashop.repository.testutils;

import java.io.File;

import com.thundashop.repository.config.Config;

public class TestConfig {

    public static Config newInstance() {
        return Config.of(new File("src/test/resources/test.properties"));
    }

}
