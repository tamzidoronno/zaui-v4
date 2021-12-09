package com.thundashop.core.webmanager;

import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

public class OkHttpResponse {

    private final Response response;
    private final String body;

    public OkHttpResponse(Response response) {
        this.response = response;

        // close responseBody for connection recycling
        try (ResponseBody body = response.body()){
            this.body = body.string();
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
