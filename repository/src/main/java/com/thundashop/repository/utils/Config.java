package com.thundashop.repository.utils;

import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Supplier;

public class Config {

    private volatile Properties properties;

    private final Supplier<Properties> supplier;

    private Config(Supplier<Properties> supplier) {
        this.supplier = supplier;
    }

    public static Config of(final File file) {
        Validate.notNull(file, "Resource file should not be null");

        Supplier<Properties> supplier = () -> {
            FileInputStream fileInputStream;
            Properties prop = new Properties();
            try {
                fileInputStream = new FileInputStream(file);
                prop.load(fileInputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return prop;
        };

        return new Config(supplier);
    }

    public String getAsString(String key) {
        Properties prop = properties;

        if (prop == null) {
            init();
            prop = properties; // reassigned after initialized properties
        }

        return prop.getProperty(key) ;
    }

    public Integer getAsInt(String key) {
        String val = getAsString(key);
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private synchronized void init() {
        if (properties == null) {
            properties = supplier.get();
        }
    }

}
