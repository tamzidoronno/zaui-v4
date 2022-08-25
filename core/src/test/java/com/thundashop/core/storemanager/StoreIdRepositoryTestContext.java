package com.thundashop.core.storemanager;

import com.thundashop.repository.db.MongoClientProviderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.UnknownHostException;

@Configuration
public class StoreIdRepositoryTestContext {

    public static int TEST_DB_PORT= 27019;

    @Bean
    public ExtendedDatabase3 database3() throws UnknownHostException {
        return new ExtendedDatabase3(MongoClientProviderImpl.builder()
                .setHost("localhost")
                .setPort(TEST_DB_PORT)
                .build());
    }

}
