package com.thundashop.core.config;

import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.databasemanager.Database3;
import com.thundashop.core.databasemanager.MongoClientProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.UnknownHostException;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Configuration
public class DatabaseConfig {

    @Bean(name = "localMongo")
    public MongoClientProvider localMongoClientProvider() throws UnknownHostException {
        return MongoClientProvider.builder()
                .setHost("localhost")
                .setPort(27018)
                .build();
    }

    @Bean(name = "supportMongo")
    public MongoClientProvider supportMongoClientProvider() throws UnknownHostException {
        return MongoClientProvider.builder()
                .setHost("192.168.100.1")
                .setPort(27017)
                .setOptions(options -> options
                        .connectTimeout(2000)
                        .socketTimeout(2000)
                        .build())
                .build();
    }

    @Bean(name = "storeIdDb")
    public Database3 storeIdDd(FrameworkConfig frameworkConfig) throws UnknownHostException {
        MongoClientProvider provider = isNotEmpty(frameworkConfig.getStoreCreationIP()) && frameworkConfig.productionMode
                ? MongoClientProvider.builder().setHost(frameworkConfig.getStoreCreationIP()).setPort(27018).build()
                : localMongoClientProvider();

        return new Database3(provider);
    }

}