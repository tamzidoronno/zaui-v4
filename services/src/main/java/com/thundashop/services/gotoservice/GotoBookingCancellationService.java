package com.thundashop.services.gotoservice;

import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static com.thundashop.core.gotohub.constant.GoToStatusCodes.*;
import static com.thundashop.core.gotohub.constant.GotoConstants.cancellationDateFormatter;
import static com.thundashop.core.gotohub.constant.GotoConstants.checkinOutDateFormatter;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Slf4j
public class GotoBookingCancellationService implements IGotoBookingCancellationService{
    OkHttpClient okHttpClient;
    @Autowired
    public GotoBookingCancellationService(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public String getCancellationDeadLine(String checkin, int cutoffHour, PmsConfiguration config) throws Exception {
        Date checkinDate = checkinOutDateFormatter.parse(checkin);
        return cancellationDateFormatter.format(getCancellationDeadLine(checkinDate, cutoffHour, config));
    }

    @Override
    public Date getCancellationDeadLine(Date checkinDate, int cutoffHour, PmsConfiguration config) {
        Calendar calendar = Calendar.getInstance();
        checkinDate = config.getDefaultStart(checkinDate);
        calendar.setTime(checkinDate);
        calendar.add(Calendar.HOUR_OF_DAY, - cutoffHour);
        return calendar.getTime();
    }

    @Override
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public void notifyGotoAboutCancellation(String baseUrl, String authKey, String reservationId) throws Exception {
        if(isBlank(baseUrl) || isBlank(authKey)) {
            log.info("no url or authKey has been found in config file for goto cancellation acknowledgement");
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
        Response response;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            log.error("{}, ReservationId: {}, Reason: {}, Actual error: {}"
                    , CANCELLATION_ACK_FAILED.message, reservationId, e.getMessage(), e);
            throw e;
        }
        String responseBody;
        try {
            responseBody = response.body().string();
            log.info("goto booking acknowledgement response: {}", responseBody);
        } catch (IOException e) {
            log.error("{}, ReservationId: {}, Reason: {}, Actual error: {}"
                    , CANCELLATION_ACK_FAILED.message, reservationId, e.getMessage(), e);
            throw e;
        }
        if(!response.isSuccessful()) {
            log.error("{}, ReservationId: {}, Response Code: {}, Response Body: {}"
                    , CANCELLATION_ACK_FAILED.message, reservationId, response.code(), responseBody);
            throw new GotoException(CANCELLATION_ACK_FAILED.code, CANCELLATION_ACK_FAILED.message + "Response Body: " + responseBody);
        }
    }
}
