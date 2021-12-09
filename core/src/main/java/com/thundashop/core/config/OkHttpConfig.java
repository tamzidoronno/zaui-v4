package com.thundashop.core.config;

import com.squareup.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpConfig {

    @Bean(name = "powerOfficeGoClient")
    public OkHttpClient powerOfficeGoClient() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(3, TimeUnit.MINUTES);
        return client;
    }

}
