package com.thundashop.core.getshopaccounting;

import com.thundashop.core.webmanager.OkHttpRequest;
import com.thundashop.core.webmanager.OkHttpResponse;
import com.thundashop.core.webmanager.OkHttpService;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.thundashop.core.webmanager.OkHttpRequest.AuthType.BEARER;

@Service
public class PowerOfficeGoHttpClientManager {

    private static final Logger logger = LoggerFactory.getLogger(PowerOfficeGoHttpClientManager.class);

    private final OkHttpClient client;
    private final OkHttpService okHttpService;

    @Autowired
    public PowerOfficeGoHttpClientManager(@Qualifier("powerOfficeGoClient") OkHttpClient client, OkHttpService okHttpService) {
        this.client = client;
        this.okHttpService = okHttpService;
    }

    public String post(String data, String token, String endpoint) {
        OkHttpRequest request = OkHttpRequest.builder()
                .setUrl(endpoint)
                .setClient(client)
                .setAuthType(BEARER)
                .setToken(token)
                .setPayload(data)
                .build();

        OkHttpResponse response = okHttpService.post(request);

        if (!response.isSuccessful()) {
            logger.warn("PowerOfficeGo api error statusCode: {} , requestBody: {}", response.statusCode(), request);
            throw new RuntimeException("PowerOfficeGo api error statusCode: " + response.statusCode());
        }

        return response.getBody();
    }
}
