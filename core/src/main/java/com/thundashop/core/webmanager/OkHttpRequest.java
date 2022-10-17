package com.thundashop.core.webmanager;

public class OkHttpRequest {
    private final String auth;
    private final String payload;
    private final String url;
    private final boolean jsonPost;

    private OkHttpRequest(String auth, String payload, String url, boolean jsonPost) {
        this.auth = auth;
        this.payload = payload;
        this.url = url;
        this.jsonPost = jsonPost;
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

    public static class OkHttpRequestBuilder {
        private String  auth;
        private String payload;
        private String url;
        private boolean jsonPost;        

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
            return new OkHttpRequest(auth, payload, url, jsonPost);
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
