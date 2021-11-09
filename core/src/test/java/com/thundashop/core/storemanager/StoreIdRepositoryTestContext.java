package com.thundashop.core.storemanager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreIdRepositoryTestContext {

    @Bean
    public ExtendedDatabase3 database3() {
        return new ExtendedDatabase3("localhost", 27019);
    }

}
