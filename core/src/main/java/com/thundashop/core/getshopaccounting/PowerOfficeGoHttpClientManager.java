package com.thundashop.core.getshopaccounting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thundashop.core.webmanager.ZauiHttpRequest;
import com.thundashop.core.webmanager.OkHttpResponse;
import com.thundashop.services.core.httpservice.ZauiHttpService;

@Service
public class PowerOfficeGoHttpClientManager {

    private static final Logger logger = LoggerFactory.getLogger(PowerOfficeGoHttpClientManager.class);

    private final ZauiHttpService okHttpService;

    @Autowired
    public PowerOfficeGoHttpClientManager(ZauiHttpService okHttpService) {
        this.okHttpService = okHttpService;
    }

    public String post(String data, String token, String endpoint) {
        ZauiHttpRequest request = ZauiHttpRequest.builder()
                .setUrl(endpoint)
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
