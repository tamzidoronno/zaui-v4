package com.thundashop.services.core.httpservice;

import java.io.IOException;
import java.util.Map;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thundashop.core.webmanager.ZauiHttpRequest;
import com.thundashop.core.webmanager.ZauiHttpResponse;

import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Service
@Slf4j
public class ZauiHttpService implements IZauiHttpService {

    private static final MediaType JSON = MediaType.parse("application/json");
    private static final MediaType TEXT = MediaType.parse("text/plain");

    private final OkHttpClient okHttpClient;

    @Autowired
    public ZauiHttpService(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
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

    @Override
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

    @Override
    public ZauiHttpResponse delete(ZauiHttpRequest httpRequest) {

        Request.Builder requestBuilder = new Request.Builder();
        if (isNotEmpty(httpRequest.getAuth())) {
            requestBuilder.addHeader("Authorization", httpRequest.getAuth());
        }

        Map<String, String> headers = httpRequest.getHeaders();
        if(headers != null)
            headers.keySet().forEach(key -> requestBuilder.addHeader(key, headers.get(key)));

        Request request = requestBuilder
                .url(httpRequest.getUrl())
                .delete()
                .build();

        return execute(request);
    }

    private ZauiHttpResponse execute(Request request) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            return new ZauiHttpResponse(response.body().string(), response.code(), response.isSuccessful(), response.headers().toMultimap(), null);
        } catch (IOException e) {
            log.error("ZauiHttpException occured for request url {}. Reason: {}. Actual error: {}", request.url().uri(), e.getMessage(), e);
            return new ZauiHttpResponse(null, 500, false, null, e.getMessage());
        }
    }
}