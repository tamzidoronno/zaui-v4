package com.thundashop.core.config;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpConfig {

    @Bean(name = "powerOfficeGoClient")
    public OkHttpClient powerOfficeGoClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(5, TimeUnit.MINUTES)
                .build();

        return client;
    }

}
