package com.thundashop.core.webmanager;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Service
public class OkHttpService {

    private static final MediaType JSON = MediaType.parse("application/json");
    private static final MediaType TEXT = MediaType.parse("text/plain");

    private final OkHttpClient okHttpClient;

    @Autowired
    public OkHttpService(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public OkHttpResponse post(OkHttpRequest httpRequest) {
        OkHttpClient client = httpRequest.getClient() != null ? httpRequest.getClient() : okHttpClient;

        RequestBody requestBody = httpRequest.isJsonPost()
                ? RequestBody.Companion.create(httpRequest.getPayload(), JSON)
                : RequestBody.Companion.create(httpRequest.getPayload(), TEXT);

        Request.Builder requestBuilder = new Request.Builder();

        if (isNotEmpty(httpRequest.getAuth())) {
            requestBuilder.addHeader("Authorization", httpRequest.getAuth());
        }

        Request request = requestBuilder
                .url(httpRequest.getUrl())
                .post(requestBody)
                .build();

        return execute(client, request);
    }

    public OkHttpResponse get(OkHttpRequest httpRequest) {
        OkHttpClient client = httpRequest.getClient() != null ? httpRequest.getClient() : okHttpClient;

        Request request = new Request.Builder()
                .url(httpRequest.getUrl())
                .get()
                .build();

        return execute(client, request);
    }

    private OkHttpResponse execute(OkHttpClient client, Request request) {
        try (Response response = client.newCall(request).execute()) {
            return new OkHttpResponse(response, response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
