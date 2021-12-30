package com.thundashop.core.storemanager;

import com.thundashop.core.databasemanager.MongoClientProviderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreIdRepositoryTestContext {

    @Bean
    public ExtendedDatabase3 database3() {
        return new ExtendedDatabase3(MongoClientProviderImpl.builder()
                .setHost("localhost")
                .setPort(27019)
                .build());
    }

}
