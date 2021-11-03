package com.thundashop.core.config;

import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.databasemanager.Database3;
import com.thundashop.core.databasemanager.WubookLogCleanerDB;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Configuration
public class DatabaseConfig {

    @Bean(name = "storeIdDb")
    public Database3 storeIdDd(FrameworkConfig frameworkConfig) {
        String host = isNotEmpty(frameworkConfig.getStoreCreationIP()) && frameworkConfig.productionMode
                ? frameworkConfig.getStoreCreationIP() : "localhost";

        return new Database3(host, 27018);
    }

    @Bean
    public WubookLogCleanerDB wubookLogCleanerDB() {
        return new WubookLogCleanerDB("localhost", 27018);
    }

}