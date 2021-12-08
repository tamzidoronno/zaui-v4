package com.thundashop.core.webmanager;

import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OkHttpService {

    private static final MediaType mediaType = MediaType.parse("application/json");

    public OkHttpResponse post(OkHttpRequest httpRequest) {
        OkHttpClient client = httpRequest.getClient();
        RequestBody requestBody = RequestBody.Companion.create(httpRequest.getPayload(), mediaType);

        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + httpRequest.getToken())
                .url(httpRequest.getUrl())
                .method("POST", requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return new OkHttpResponse(response, response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
