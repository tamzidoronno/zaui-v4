package com.thundashop.core.webmanager;

import okhttp3.OkHttpClient;

public class OkHttpRequest {

    private final OkHttpClient client;
    private final String auth;
    private final String payload;
    private final String url;
    private final boolean jsonPost;

    private OkHttpRequest(OkHttpClient client, String auth, String payload, String url, boolean jsonPost) {
        this.client = client;
        this.auth = auth;
        this.payload = payload;
        this.url = url;
        this.jsonPost = jsonPost;
    }

    public static OkHttpRequestBuilder builder() {
        return new OkHttpRequestBuilder();
    }

    public OkHttpClient getClient() {
        return client;
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

    public static class OkHttpRequestBuilder {
        private OkHttpClient client;
        private String  auth;
        private String payload;
        private String url;
        private boolean jsonPost;

        public OkHttpRequestBuilder setClient(OkHttpClient client) {
            this.client = client;
            return this;
        }

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

        public OkHttpRequest build() {
            return new OkHttpRequest(client, auth, payload, url, jsonPost);
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
