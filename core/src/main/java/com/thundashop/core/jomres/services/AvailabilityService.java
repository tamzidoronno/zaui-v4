package com.thundashop.core.jomres.services;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.jomres.dto.PMSBlankBooking;
import com.thundashop.core.jomres.dto.UnavailabilityDate;
import com.thundashop.core.jomres.dto.UpdateAvailabilityResponse;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.thundashop.core.jomres.services.Constants.*;

public class AvailabilityService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(AvailabilityService.class);

    public UpdateAvailabilityResponse deleteBlankBooking(String baseUrl, String token, PMSBlankBooking booking) {
        UpdateAvailabilityResponse res = new UpdateAvailabilityResponse();
        createHttpClient();
        try {
            String url = baseUrl + MAKE_PROPERTY_AVAILABLE+booking.getPropertyId()+"/"+booking.getContractId();
            Request request = getHttpBearerTokenRequest(url, token,
                    null, null, "DELETE");
            Response response = httpClient.newCall(request).execute();
            res = responseDataParser.parseChangeAvailabilityResponse(response);
            res.setMessage("");
        }  catch (Exception e) {
            logger.error(Throwables.getStackTraceAsString(e));
            res.setMessage("Got Unexpected Error, Please Check Log stacktrace..\n" + e.getMessage() + "\n");
        }
        
        res.setStart(booking.getDateFrom());
        res.setEnd(booking.getDateTo());
        res.setPropertyId(booking.getPropertyId());
        res.setAvailable(true);
        return res;
    }

    public UpdateAvailabilityResponse createBlankBooking(String baseUrl, String token, String channel, int jomresPropertyId, Booking booking) {
        UpdateAvailabilityResponse res = new UpdateAvailabilityResponse();
        createHttpClient();
        String url = baseUrl + MAKE_PROPERTY_UNAVAILABLE;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(booking.endDate);
        calendar.add(Calendar.DATE, -1);
        Date lastNightStay = calendar.getTime();
        try {
            Map<String, String> formData =
                    getFormDataForAvailability(jomresPropertyId, booking.startDate, lastNightStay);
            Request request = getHttpBearerTokenRequest(url, token,
                    addChannelIntoHeaders(null, channel), formData, "PUT");
            Response response = httpClient.newCall(request).execute();
            res = responseDataParser.parseChangeAvailabilityResponse(response);
            res.setMessage("");
        } catch (Exception e) {
            logger.error(Throwables.getStackTraceAsString(e));
            res.setMessage("Got Unexpected Error, Please Check Log stacktrace..\n" + e.getMessage() + "\n");
        }
        res.setStart(booking.startDate);
        res.setPropertyId(jomresPropertyId);
        res.setEnd(booking.endDate);
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
}
