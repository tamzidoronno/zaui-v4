package com.thundashop.core.webmanager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZauiHttpResponse {
    private final String body;
    private final int code;
    private final boolean isSuccessful;
    private final Map<String, List<String>> headers;

    public ZauiHttpResponse(String body, int code, boolean isSuccessful, Map<String, List<String>> headers) {
        this.body = body;
        this.code = code;
        this.isSuccessful = isSuccessful;
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public int statusCode() {
        return code;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public Map<String, String> headers() {
        Map<String, String> headers = new HashMap<>();

        for (String key : this.headers.keySet()) {
            StringBuilder values = new StringBuilder();
            for (String value : this.headers.get(key)) {
                values.append(value).append(",");
            }
            headers.put(key, values.toString());
        }

        return headers;
    }

    @Override
    public String toString() {
        return "OkHttpResponse{" +                
                ", responseBody='" + body + '\'' +
                '}';
    }
}
