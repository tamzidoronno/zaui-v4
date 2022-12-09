package com.thundashop.services.gotoservice;

import com.google.common.base.Throwables;
import com.thundashop.core.gotohub.dto.GotoException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

import static com.thundashop.core.gotohub.constant.GoToStatusCodes.CANCELLATION_ACKNOWLEDGMENT_FAILED;
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
    @Retryable(value = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 100))
    public void notifyGotoAboutCancellation(String baseUrl, String authKey, String reservationId) throws Exception {
        if(isBlank(baseUrl) || isBlank(authKey)) {
            log.info("no url or authkey has been found in config file for goto cancellation acknowledgement");
            System.out.println("no url or authkey has been found in config file for goto cancellation acknowledgement");
            return;
        }
        String url = baseUrl + reservationId;
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.addHeader("Authorization", authKey);
        RequestBody body = RequestBody.Companion.create("", MediaType.parse("text/plain"));
        Request request = requestBuilder
                .url(url)
                .put(body)
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            log.error("Failed to execute and get response");
            System.out.println("Failed to execute and get response");
            throw e;
        }
        String responseBody = "";
        try {
            responseBody = response.body().string();
            log.info("goto booking acknowledgement response: {}", responseBody);
            System.out.println("goto booking acknowledgement response: {}" + responseBody);
        } catch (IOException e) {
            log.error("Failed to read response body");
            System.out.println("Failed to read response body");
            throw e;
        }
        if(!response.isSuccessful()) {
            log.error("Failed to acknowledge goto about cancellation");
            log.error("Response Code: {}", response.code());
            System.out.println("Failed to acknowledge goto about cancellation");
            System.out.println("Response Code: {}"+response.code());
            throw new GotoException(CANCELLATION_ACKNOWLEDGMENT_FAILED.code,CANCELLATION_ACKNOWLEDGMENT_FAILED.message);
        }
    }

//    @Recover
//    public void recoverFromFailedAPiCall(Exception e, String url, String authKey, String reservationId) throws GotoException {
//        log.error("Cancellation has been failed for 5 times for Url: {}, authkey: {}, ResId: {}.. Terminating",
//                url, authKey, reservationId);
//        log.error(Throwables.getStackTraceAsString(e));
//        if(!(e instanceof GotoException))
//            throw new GotoException(CANCELLATION_ACKNOWLEDGMENT_FAILED.code,CANCELLATION_ACKNOWLEDGMENT_FAILED.message);
//    }
}
