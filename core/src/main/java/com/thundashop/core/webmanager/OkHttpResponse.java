package com.thundashop.core.webmanager;


import okhttp3.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OkHttpResponse {

    private final Response response;
    private final String body;

    public OkHttpResponse(Response response, String body) {
        this.response = response;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public int statusCode() {
        return response.code();
    }

    public boolean isSuccessful() {
        return response.isSuccessful();
    }

    public Map<String, String> headers() {
        Map<String, String> headers = new HashMap<>();

        for (Map.Entry<String, List<String>> it : response.headers().toMultimap().entrySet()) {
            StringBuilder values = new StringBuilder();
            for (String value : it.getValue()) {
                values.append(value).append(",");
            }
            headers.put(it.getKey(), values.toString());
        }

        return headers;
    }

    @Override
    public String toString() {
        return "OkHttpResponse{" +
                "response=" + response +
                ", responseBody='" + body + '\'' +
                '}';
    }
}
