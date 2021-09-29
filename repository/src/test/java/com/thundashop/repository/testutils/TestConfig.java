package com.thundashop.repository.testutils;

import com.thundashop.repository.utils.Config;

import java.io.File;

public class TestConfig {

    public static Config newInstance() {
        return Config.of(new File("src/test/resources/test.properties"));
    }

}
