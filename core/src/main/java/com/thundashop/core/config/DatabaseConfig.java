package com.thundashop.core.config;

import com.thundashop.services.config.FrameworkConfig;
import com.thundashop.core.databasemanager.Database3;
import com.thundashop.core.databasemanager.DatabaseRemoteConnectionStringProvider;
import com.thundashop.repository.db.LazyMongoClientProvider;
import com.thundashop.repository.db.MongoClientProvider;
import com.thundashop.repository.db.MongoClientProviderImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.UnknownHostException;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Configuration
public class DatabaseConfig {

    @Value("${db.localMongo.host}")
    private String localMongoHost;
    @Value("${db.localMongo.port}")
    private Integer localMongoPort;

    @Bean(name = "localMongo")    
    public MongoClientProvider localMongoClientProvider() throws UnknownHostException {
        return MongoClientProviderImpl.builder()
                .setHost(localMongoHost)
                .setPort(localMongoPort)
                .build();
    }

    @Bean(name = "supportMongo")
    public MongoClientProvider supportMongoClientProvider() throws UnknownHostException{
        return MongoClientProviderImpl.builder()
                .setHost("192.168.100.1")
                .setPort(27017)
                .setOptions(options -> options
                        .connectTimeout(2000)
                        .socketTimeout(2000)
                        .build())
                .build();
    }

    @Bean(name = "remoteMongo")
    public MongoClientProvider remoteMongoClientProvider() {
        return LazyMongoClientProvider.builder()
                .setConnectionString(DatabaseRemoteConnectionStringProvider::getConnectionString)
                .build();
    }

    @Bean(name = "oAuthMongo")
    public MongoClientProvider oAuthMongoClientProvider() throws UnknownHostException{
        return LazyMongoClientProvider.builder()
                .setConnectionString(() -> "mongodb://oauth:02349890uqadsfajsl3n421k24j3nblksadnf@192.168.100.1/oauth")
                .build();
    }

    @Bean(name = "storeIdDb")
    public Database3 storeIdDd(FrameworkConfig frameworkConfig) throws UnknownHostException{
        MongoClientProvider provider = isNotEmpty(frameworkConfig.getStoreCreationIP()) && frameworkConfig.productionMode
                ? MongoClientProviderImpl.builder().setHost(frameworkConfig.getStoreCreationIP()).setPort(27018).build()
                : localMongoClientProvider();

        return new Database3(provider);
    }

}