package com.thundashop.core.webmanager;

import com.squareup.okhttp.OkHttpClient;

public class OkHttpRequest {

    private final OkHttpClient client;
    private final AuthType authType;
    private final String token;
    private final String payload;
    private final String url;

    public enum AuthType {
        BEARER
    }

    private OkHttpRequest(OkHttpClient client, AuthType authType, String token, String payload, String url) {
        this.client = client;
        this.authType = authType;
        this.token = token;
        this.payload = payload;
        this.url = url;
    }

    public static OkHttpRequestBuilder builder() {
        return new OkHttpRequestBuilder();
    }

    public OkHttpClient getClient() {
        return client;
    }

    public AuthType getAuthType() {
        return authType;
    }

    public String getToken() {
        return token;
    }

    public String getPayload() {
        return payload;
    }

    public String getUrl() {
        return url;
    }

    public static class OkHttpRequestBuilder {
        private OkHttpClient client;
        private OkHttpRequest.AuthType authType;
        private String token;
        private String payload;
        private String url;

        public OkHttpRequestBuilder setClient(OkHttpClient client) {
            this.client = client;
            return this;
        }

        public OkHttpRequestBuilder setAuthType(OkHttpRequest.AuthType authType) {
            this.authType = authType;
            return this;
        }

        public OkHttpRequestBuilder setPayload(String payload) {
            this.payload = payload;
            return this;
        }

        public OkHttpRequestBuilder setToken(String token) {
            this.token = token;
            return this;
        }

        public OkHttpRequestBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public OkHttpRequest build() {
            return new OkHttpRequest(client, authType, token, payload, url);
        }
    }

    @Override
    public String toString() {
        return "OkHttpRequest{" +
                "authType=" + authType +
                ", token='" + token + '\'' +
                ", payload='" + payload + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
