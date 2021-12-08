package com.thundashop.core.webmanager;


import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

public class OkHttpResponse {

    private final Response response;
    private final String body;

    public OkHttpResponse(Response response) {
        this.response = response;

        // close responseBody for connection recycling
        try (ResponseBody body = response.body()){
            this.body = (body != null) ? body.string() : null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

}
