package com.thundashop.core.getshopaccounting;

import com.thundashop.core.webmanager.OkHttpRequest;
import com.thundashop.core.webmanager.OkHttpResponse;
import com.thundashop.core.webmanager.OkHttpService;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PowerOfficeGoHttpClientManager {

    private static final Logger logger = LoggerFactory.getLogger(PowerOfficeGoHttpClientManager.class);

    private final OkHttpClient client;
    private final OkHttpService okHttpService;

    @Autowired
    public PowerOfficeGoHttpClientManager(OkHttpClient okHttpClient, OkHttpService okHttpService) {
        this.client = okHttpClient;
        this.okHttpService = okHttpService;
    }

    public String post(String data, String token, String endpoint) {
        OkHttpRequest request = OkHttpRequest.builder()
                .setUrl(endpoint)
                .setClient(client)
                .setAuth("Bearer " + token)
                .setPayload(data)
                .jsonPost(true)
                .build();

        OkHttpResponse response = okHttpService.post(request);

        if (!response.isSuccessful()) {
            logger.warn("PowerOfficeGo api error response: {}", response);
            throw new RuntimeException("PowerOfficeGo api error statusCode: " + response.statusCode());
        }

        return response.getBody();
    }
}
