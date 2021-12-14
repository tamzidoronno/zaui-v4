package com.thundashop.core.storemanager;

import com.thundashop.repository.db.MongoClientProviderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.UnknownHostException;

@Configuration
public class StoreIdRepositoryTestContext {

    @Bean
    public ExtendedDatabase3 database3() throws UnknownHostException {
        return new ExtendedDatabase3(MongoClientProviderImpl.builder()
                .setHost("localhost")
                .setPort(27019)
                .build());
    }

}
