package com.thundashop.core.webmanager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

public class ZauiHttpResponse {
    @Getter
    private final String body;
    private final int code;
    private final boolean isSuccessful;
    private final Map<String, List<String>> headers;
    @Getter
    private final String errorMessaage;

    public ZauiHttpResponse(String body, int code, boolean isSuccessful, Map<String, List<String>> headers, String errorMessaage) {
        this.body = body;
        this.code = code;
        this.isSuccessful = isSuccessful;
        this.headers = headers;
        this.errorMessaage = errorMessaage;
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
