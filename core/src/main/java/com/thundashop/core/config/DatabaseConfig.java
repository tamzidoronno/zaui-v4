package com.thundashop.core.config;

import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.databasemanager.Database3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    @Bean(name = "storeIdDb")
    public Database3 storeIdDd(FrameworkConfig frameworkConfig) {
        return new Database3("10.0.9.33", 27018);
    }

}