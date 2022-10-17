package com.thundashop.services.core.httpservice;

import java.io.IOException;
import java.util.Map;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thundashop.core.webmanager.OkHttpRequest;
import com.thundashop.core.webmanager.OkHttpResponse;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Service
public class ZauiHttpService implements IZauiHttpService {

    private static final MediaType JSON = MediaType.parse("application/json");
    private static final MediaType TEXT = MediaType.parse("text/plain");

    private final OkHttpClient okHttpClient;

    @Autowired
    public ZauiHttpService(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public OkHttpResponse post(OkHttpRequest httpRequest) {

        RequestBody requestBody = httpRequest.isJsonPost()
                ? RequestBody.Companion.create(httpRequest.getPayload(), JSON)
                : RequestBody.Companion.create(httpRequest.getPayload(), TEXT);

        Request.Builder requestBuilder = new Request.Builder();

        if (isNotEmpty(httpRequest.getAuth())) {
            requestBuilder.addHeader("Authorization", httpRequest.getAuth());
        }

        Map<String, String> headers = httpRequest.getHeaders();
        if(headers != null)
            headers.keySet().forEach(key -> requestBuilder.addHeader(key, headers.get(key)));

        Request request = requestBuilder
                .url(httpRequest.getUrl())
                .post(requestBody)
                .build();

        return execute(request);
    }

    public OkHttpResponse get(OkHttpRequest httpRequest) {

        Request.Builder requestBuilder = new Request.Builder();
        if (isNotEmpty(httpRequest.getAuth())) {
            requestBuilder.addHeader("Authorization", httpRequest.getAuth());
        }

        Map<String, String> headers = httpRequest.getHeaders();
        if(headers != null)
            headers.keySet().forEach(key -> requestBuilder.addHeader(key, headers.get(key)));

        Request request = new Request.Builder()
                .url(httpRequest.getUrl())
                .get()
                .build();

        return execute(request);
    }

    private OkHttpResponse execute(Request request) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            return new OkHttpResponse(response.body().string(), response.code(), response.isSuccessful(), response.headers().toMultimap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}