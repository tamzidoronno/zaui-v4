package com.thundashop.services.gotoservice;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Service
@Slf4j
public class GotoBookingCancellationService implements IGotoBookingCancellationService{

    OkHttpClient okHttpClient;
    @Autowired
    public GotoBookingCancellationService(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }
    @Override
    public void notifyGotoAboutCancellation(String baseUrl, String authKey, String reservationId) {
        if(isBlank(baseUrl) || isBlank(authKey)) {
            log.info("no url or authkey has been found in config file for goto cancellation acknowledgement");
        }
        String url = baseUrl+ reservationId;
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.addHeader("Authorization", authKey);
        RequestBody body = RequestBody.Companion.create("", MediaType.parse("text/plain"));
        Request request = requestBuilder
                .url(url)
                .put(null)
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            log.error("Failed to execute and get response");
        }
        if(!response.isSuccessful()) log.error("Failed to acknowledge goto about cancellation");
        try {
            response.body().string();
        } catch (IOException e) {
            log.error("Failed to read response body");
        }
    }
}
