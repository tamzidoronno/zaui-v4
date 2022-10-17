package com.thundashop.core.webmanager;

import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class OkHttpRequest {
    private final String auth;
    private final String payload;
    private final String url;
    private final boolean jsonPost;
    private Map<String, String> headers;

    private OkHttpRequest(String auth, String payload, String url, boolean jsonPost, Map<String, String> headers) {
        this.auth = auth;
        this.payload = payload;
        this.url = url;
        this.jsonPost = jsonPost;
        this.headers = headers;
    }

    public static OkHttpRequestBuilder builder() {
        return new OkHttpRequestBuilder();
    }   

    public String getAuth() {
        return auth;
    }

    public String getPayload() {
        return payload;
    }

    public String getUrl() {
        return url;
    }

    public boolean isJsonPost() {
        return jsonPost;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public static class OkHttpRequestBuilder {
        private String  auth;
        private String payload;
        private String url;
        private boolean jsonPost;
        private Map<String, String> headers;

        public OkHttpRequestBuilder setAuth(String  auth) {
            this.auth = auth;
            return this;
        }

        public OkHttpRequestBuilder setPayload(String payload) {
            this.payload = payload;
            return this;
        }

        public OkHttpRequestBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public OkHttpRequestBuilder jsonPost(boolean jsonPost) {
            this.jsonPost = jsonPost;
            return this;
        }

        public OkHttpRequestBuilder addHeader(String key, String value) {
            String currentValue = headers.get(key);
            if(isNotEmpty(currentValue)) value = currentValue + ", " + value;
            headers.put(key, value);
            return this;
        }

        public OkHttpRequestBuilder setHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public OkHttpRequest build() {
            return new OkHttpRequest(auth, payload, url, jsonPost, headers);
        }
    }

    @Override
    public String toString() {
        return "OkHttpRequest{" +
                "auth=" + auth +
                ", payload='" + payload + '\'' +
                ", url='" + url + '\'' +
                ", jsonPost=" + jsonPost +
                '}';
    }
}
