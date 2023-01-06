package com.thundashop.core.getshopaccounting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thundashop.core.webmanager.ZauiHttpRequest;
import com.thundashop.core.webmanager.ZauiHttpResponse;
import com.thundashop.services.core.httpservice.IZauiHttpService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PowerOfficeGoHttpClientManager {

    private final IZauiHttpService okHttpService;

    @Autowired
    public PowerOfficeGoHttpClientManager(IZauiHttpService okHttpService) {
        this.okHttpService = okHttpService;
    }

    public String post(String data, String token, String endpoint) {
        ZauiHttpRequest request = ZauiHttpRequest.builder()
                .setUrl(endpoint)
                .setAuth("Bearer " + token)
                .setPayload(data)
                .jsonPost(true)
                .build();

        ZauiHttpResponse response = okHttpService.post(request);

        if (!response.isSuccessful()) {
            log.warn("PowerOfficeGo api error response: {}", response);
            throw new RuntimeException("PowerOfficeGo api error statusCode: " + response.statusCode());
        }

        return response.getBody();
    }
}
