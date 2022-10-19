package com.thundashop.services.core.httpservice;

import java.io.IOException;
import java.util.Map;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thundashop.core.webmanager.ZauiHttpRequest;
import com.thundashop.core.webmanager.ZauiHttpResponse;

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

    public ZauiHttpResponse post(ZauiHttpRequest httpRequest) {

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

    public ZauiHttpResponse get(ZauiHttpRequest httpRequest) {

        Request.Builder requestBuilder = new Request.Builder();
        if (isNotEmpty(httpRequest.getAuth())) {
            requestBuilder.addHeader("Authorization", httpRequest.getAuth());
        }

        Map<String, String> headers = httpRequest.getHeaders();
        if(headers != null)
            headers.keySet().forEach(key -> requestBuilder.addHeader(key, headers.get(key)));

        Request request = requestBuilder
                .url(httpRequest.getUrl())
                .get()
                .build();

        return execute(request);
    }

    private ZauiHttpResponse execute(Request request) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            return new ZauiHttpResponse(response.body().string(), response.code(), response.isSuccessful(), response.headers().toMultimap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}