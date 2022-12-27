package com.thundashop.services.jomresservice;

import static com.thundashop.core.jomres.constants.Constants.AVAILABILITY_SENDING_DATE_FORMAT;
import static com.thundashop.core.jomres.constants.Constants.MAKE_PROPERTY_AVAILABLE;
import static com.thundashop.core.jomres.constants.Constants.MAKE_PROPERTY_UNAVAILABLE;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.thundashop.core.jomres.dto.PMSBlankBooking;
import com.thundashop.core.jomres.dto.UnavailabilityDate;
import com.thundashop.core.jomres.dto.UpdateAvailabilityResponse;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
public class JomresAvailabilityService extends JomresApiService {

    public UpdateAvailabilityResponse deleteBlankBooking(String baseUrl, String token, PMSBlankBooking booking) {
        UpdateAvailabilityResponse res = new UpdateAvailabilityResponse();
        try {
            String url = baseUrl + MAKE_PROPERTY_AVAILABLE+booking.getPropertyId()+"/"+booking.getContractId();
            Request request = getHttpBearerTokenRequest(url, token,
                    null, null, "DELETE");
            Response response = httpClient.newCall(request).execute();
            res = responseDataParser.parseChangeAvailabilityResponse(response);
            res.setMessage("");
        }  catch (Exception e) {
            log.error(Throwables.getStackTraceAsString(e));
            res.setMessage("Got Unexpected Error, Please Check Log stacktrace..\n" + e.getMessage() + "\n");
        }
        
        res.setStart(booking.getDateFrom());
        res.setEnd(booking.getDateTo());
        res.setPropertyId(booking.getPropertyId());
        res.setAvailable(true);
        return res;
    }

    public UpdateAvailabilityResponse createBlankBooking(
            String baseUrl, String token, String channel, int jomresPropertyId, Date start, Date end) {
        UpdateAvailabilityResponse res = new UpdateAvailabilityResponse();
        String url = baseUrl + MAKE_PROPERTY_UNAVAILABLE;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(end);
        calendar.add(Calendar.DATE, -1);
        Date lastNightStay = calendar.getTime();
        try {
            Map<String, String> formData =
                    getFormDataForAvailability(jomresPropertyId, start, lastNightStay);
            Request request = getHttpBearerTokenRequest(url, token,
                    addChannelIntoHeaders(null, channel), formData, "PUT");
            Response response = httpClient.newCall(request).execute();
            res = responseDataParser.parseChangeAvailabilityResponse(response);
            res.setMessage("");
        } catch (Exception e) {
            log.error(Throwables.getStackTraceAsString(e));
            res.setMessage("Got Unexpected Error, Please Check Log stacktrace..\n" + e.getMessage() + "\n");
        }
        res.setStart(start);
        res.setPropertyId(jomresPropertyId);
        res.setEnd(end);
        res.setAvailable(false);
        return res;
    }

    private Map<String, String> getFormDataForAvailability(int propertyId, Date start, Date end) {
        Gson gson = new Gson();
        DateFormat formatter = new SimpleDateFormat(AVAILABILITY_SENDING_DATE_FORMAT);
        String startDate = formatter.format(start);
        String endDate = formatter.format(end);
        Map<String, String> formData = new HashMap<>();
        formData.put("property_uid", String.valueOf(propertyId));
        String availability;
        UnavailabilityDate unavailabilityDate = new UnavailabilityDate(startDate, endDate);
        availability = gson.toJson(unavailabilityDate, UnavailabilityDate.class);
        formData.put("availability", availability);
        formData.put("room_ids", "[]");
        formData.put("remote_booking_id", "");
        formData.put("text", "ZauiStay");
        return formData;
    }

    public Set<String> getPmsBookingIdsFromFutureResults(List<CompletableFuture<?>> results) {
        Set<String> bookingIds = new HashSet<>();
        for(CompletableFuture<?> result: results) {
            try{
                bookingIds.add((String) result.get());
            } catch (ExecutionException | InterruptedException e) {
                log.error(Throwables.getStackTraceAsString(e));
                log.error("Failed to get a property, check log files");
            }
        }
        return bookingIds;
    }
}
