package com.thundashop.core.jomres.services;

import com.google.gson.Gson;
import com.thundashop.core.jomres.dto.Availability;
import com.thundashop.core.jomres.dto.UpdateAvailabilityResponse;
import com.thundashop.core.sedox.autocryptoapi.Exception;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.thundashop.core.jomres.services.Constants.AVAILABILITY_SENDING_DATE_FORMAT;
import static com.thundashop.core.jomres.services.Constants.MAKE_PROPERTY_UNAVAILABLE;

public class AvailabilityService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(AvailabilityService.class);

    //SRP stands for "Single Room Property"
    public String changePropertyAvailability(String baseUrl, String token, String channel, int jomresPropertyId,
                                              Map<Date, Date> dateRanges, boolean availabilityStatus) throws Exception {
        try {
            if (dateRanges == null || dateRanges.isEmpty()) {
                return "";
            }
            createHttpClient();
            DateFormat formatter = new SimpleDateFormat(AVAILABILITY_SENDING_DATE_FORMAT);
            String availabilities = "[";
            for (Date start : dateRanges.keySet()) {
                Date end = dateRanges.get(start);
                String startDateString = formatter.format(start);
                String endDateString = formatter.format(end);
                Availability availability = new Availability(startDateString, endDateString, availabilityStatus);
                Gson gson = new Gson();
                String availabilityInString = gson.toJson(availability, Availability.class);
                availabilities += availabilityInString + ",";
            }

            //removing last extra ","
            availabilities = availabilities.substring(0, availabilities.length() - 1);

            availabilities += "]";


            Map<String, String> formData = new HashMap<String, String>();
            formData.put("property_uid", String.valueOf(jomresPropertyId));
            formData.put("availability", availabilities);

            logger.debug("Form Data Body:\n\tProperty id: " + jomresPropertyId + "\n\tAvailability: " + availabilities);
            Request request = getHttpBearerTokenRequest(baseUrl + MAKE_PROPERTY_UNAVAILABLE, token,
                    addChannelIntoHeaders(null, channel), formData, "PUT");

            Response response = httpClient.newCall(request).execute();
            UpdateAvailabilityResponse res = responseDataParser.parseChangeAvailabilityResponse(response);
            if (res.success) {
                return "Successfully Updated Availability";
            }
            else {
                logger.error("Failed  to update the availability for property: "+jomresPropertyId+"\n"+res.errorMessage);
                return "Failed  to update the availability for property: "+jomresPropertyId+"\n"+res.errorMessage;
            }
        } catch (Exception e) {
            throw new Exception("Failed:\n\t" + e.getMessage1());
        } catch (IOException e) {
            throw new Exception("Failed to execute the update Availability REST API request:\n\t" + e.getMessage());
        }
    }
}
