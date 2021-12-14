package com.thundashop.core.webmanager;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OkHttpService {

    private static final MediaType mediaType = MediaType.parse("application/json");

    private final OkHttpClient okHttpClient;

    @Autowired
    public OkHttpService(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

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

    public OkHttpResponse get(OkHttpRequest httpRequest) {
        OkHttpClient client = httpRequest.getClient() != null ? httpRequest.getClient() : okHttpClient;

        Request request = new Request.Builder()
                .url(httpRequest.getUrl())
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            return new OkHttpResponse(response, response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
