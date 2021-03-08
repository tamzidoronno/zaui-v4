package com.getshop.bookingengine;

import com.getshop.scope.GetShopSessionScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookingEngineTestContext {
    @Bean
    public BookingEngineTestHelper testHelper() {
        return new BookingEngineTestHelper();
    }

    @Bean
    public GetShopSessionScope sessionScope() {
        return new GetShopSessionScope();
    }
}
