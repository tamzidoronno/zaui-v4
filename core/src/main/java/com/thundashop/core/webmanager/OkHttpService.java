package com.thundashop.core.webmanager;

import com.squareup.okhttp.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OkHttpService {

    private static final MediaType mediaType = MediaType.parse("application/json");

    public OkHttpResponse post(OkHttpRequest httpRequest) {
        OkHttpClient client = httpRequest.getClient();

        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + httpRequest.getToken())
                .url(httpRequest.getUrl())
                .method("POST", RequestBody.create(mediaType, httpRequest.getPayload()))
                .build();

        try {
            Response response = client.newCall(request).execute();

            // OkHttpResponse close the responseBody upon constructor calling.
            return new OkHttpResponse(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
