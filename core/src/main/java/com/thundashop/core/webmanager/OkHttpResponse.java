package com.thundashop.core.webmanager;


import okhttp3.Response;

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

    @Override
    public String toString() {
        return "OkHttpResponse{" +
                "response=" + response +
                ", responseBody='" + body + '\'' +
                '}';
    }
}
