package com.thundashop.core.jomres;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.thundashop.core.jomres.dto.JomresBooking;
import com.thundashop.core.jomres.dto.JomresGuest;
import com.thundashop.core.jomres.dto.UpdateAvailabilityResponse;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;

@Slf4j
public class ResponseDataParser {
    Gson gson = new Gson();

    public Map<String, Double> parseDailyPriceMatrixBetweenDates(Response response, Date start, Date end)
            throws Exception {
        JsonObject responseBody = getResponseBody(response);

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Map<String, Double> dailyPriceMatrix = new HashMap<>();
            Calendar calender = Calendar.getInstance();
            calender.setTime(start);
            calender.add(Calendar.DATE, -1);
            start = calender.getTime();
            calender.setTime(end);
            calender.add(Calendar.DATE, 1);
            end = calender.getTime();

            JsonObject tariffSet = responseBody.getAsJsonObject("data")
                    .getAsJsonObject("response")
                    .getAsJsonObject("tariff_sets");
            for (Map.Entry<String, JsonElement> entry : tariffSet.entrySet()) {
                LinkedTreeMap ratePlan = (LinkedTreeMap) gson.fromJson(
                        tariffSet.get(entry.getKey()).toString(),
                        (new ArrayList<>()).getClass()).get(0);

                LinkedTreeMap ratePerNight = (LinkedTreeMap) ratePlan.get("rate_per_night");
                double weeklyPrice = (double) ratePerNight.get("price_including_vat");
                double dailyPrice = weeklyPrice / 7.0;
                List<String> dates = (ArrayList<String>) gson.fromJson(ratePlan.get("dates").toString(),
                        (new ArrayList<>()).getClass());
                for (String date : dates) {
                    Date currentDate = format.parse(date);
                    if (currentDate.before(end) && currentDate.after(start)) {
                        String current = format.format(format.parse(date));
                        dailyPriceMatrix.put(current, dailyPrice);
                    }
                }

            }
            return dailyPriceMatrix;

        } catch (Exception e) {
            log.error("Failed to parse daily price matrix between dates response. Reason: {}. Response body: ",
                    e.getMessage(), response.body().string());
            throw e;
        }
    }

    public Map<String, Double> parseDailyPriceMatrix(Response response) throws Exception {
        JsonObject responseBody = getResponseBody(response);

        try {
            log.info("Started parsing daily price matrix");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Map<String, Double> dailyPriceMatrix = new HashMap<>();

            JsonObject tariffSet = responseBody.getAsJsonObject("data").getAsJsonObject("response")
                    .getAsJsonObject("tariff_sets");
            for (Map.Entry<String, JsonElement> entry : tariffSet.entrySet()) {
                LinkedTreeMap ratePlan = (LinkedTreeMap) gson.fromJson(tariffSet.get(entry.getKey()).toString(),
                        (new ArrayList<>()).getClass()).get(0);
                LinkedTreeMap ratePerNight = (LinkedTreeMap) ratePlan.get("rate_per_night");
                double weeklyPrice = (double) ratePerNight.get("price_including_vat");
                double dailyPrice = weeklyPrice / 7.0;
                List<String> dates = (ArrayList<String>) gson.fromJson(ratePlan.get("dates").toString(),
                        (new ArrayList<>()).getClass());
                for (String date : dates) {
                    String currentDate = format.format(format.parse(date));
                    dailyPriceMatrix.put(currentDate, dailyPrice);
                }

            }
            log.info("Ended parsing daily price matrix");
            return dailyPriceMatrix;

        } catch (Exception e) {
            log.error("Failed to parse daily price matrix response. Reason: {}. Response body: ",
                    e.getMessage(), response.body().string());
            throw e;
        }
    }

    public int parseBookingGuestsNumber(OAuthResourceResponse response) throws Exception {
        JsonObject responseBody = getOAuthResponseBody(response);

        try {
            JsonObject data = responseBody.getAsJsonObject("data");
            String bookingsString = data.get("response").toString();

            List<LinkedTreeMap> bookings = gson.fromJson(bookingsString, (new ArrayList<>()).getClass());
            LinkedTreeMap booking = bookings.stream().findFirst().orElse(null);

            if (booking == null) {
                return 0;
            }

            LinkedTreeMap guestNumber = (LinkedTreeMap) booking.get("guest_numbers");
            return (int) Double.parseDouble(guestNumber.get("number_of_guests").toString());

        } catch (Exception e) {
            log.error("Failed to parse booking guest number response. Reason: {}. Response body: ",
                    e.getMessage(), response.getBody());
            throw e;
        }
    }

    public JomresGuest parseBookingGuestDetails(OAuthResourceResponse response) throws Exception {
        JsonObject responseBody = getOAuthResponseBody(response);

        try {
            JsonObject data = responseBody.getAsJsonObject("data");
            String bookingsString = data.get("response").toString();

            List<LinkedTreeMap> bookings = gson.fromJson(bookingsString, (new ArrayList<>()).getClass());
            LinkedTreeMap booking = bookings.stream().findFirst().orElse(null);

            if (booking == null) {
                return new JomresGuest();
            }

            return new JomresGuest((LinkedTreeMap) booking.get("guest_data"));

        } catch (Exception e) {
            log.error("Failed to parse booking guest details response. Reason: {}. Response body: ",
                    e.getMessage(), response.getBody());
            throw e;
        }
    }

    public List<JomresBooking> parseBookingsFromList(OAuthResourceResponse response) throws Exception {
        JsonObject responseBody = getOAuthResponseBody(response);
        try {
            log.info("Started parsing Jomres booking list between dates");
            JsonObject data = responseBody.getAsJsonObject("data");
            String bookingsString = data.get("listbookingdate").toString();

            List<LinkedTreeMap> bookings = gson.fromJson(bookingsString, (new ArrayList<>()).getClass());
            List<JomresBooking> jomresBookings = new ArrayList<>();
            for (LinkedTreeMap booking : bookings) {
                try {
                    jomresBookings.add(new JomresBooking(booking));
                } catch (ParseException e) {
                    log.error("Falied to parse the booking for Id: {}. Reason: {}",
                            booking.get("contract_uid").toString(), e.getMessage());
                    log.error(Throwables.getStackTraceAsString(e));
                }
            }
            log.info("Ended parsing Jomres booking list between dates");
            return jomresBookings;
        } catch (Exception e) {
            log.error("Failed to parse booking list response. Reason: {}. Response body: ",
                    e.getMessage(), response.getBody());
            throw e;
        }
    }

    public List<Integer> parseAllPropertyIds(OAuthResourceResponse response, boolean channelProperties)
            throws Exception {
        JsonObject responseBody = getOAuthResponseBody(response);

        List<Integer> propertyUIDs = new ArrayList<>();
        try {
            JsonObject data = responseBody.getAsJsonObject("data");

            if (channelProperties) {
                JsonObject properties = data.getAsJsonObject("response");
                for (Map.Entry<String, JsonElement> property : properties.entrySet()) {
                    propertyUIDs.add(Integer.valueOf(property.getKey()));
                }
            } else {
                String properties = data.get("ids").toString();
                propertyUIDs = Arrays.stream(properties.replaceAll("\\[", "")
                        .replaceAll("]", "")
                        .split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());

            }

        } catch (Exception e) {
            log.error("Failed to parse response for all propertyIds. Reason: {}. Response body: ",
                    e.getMessage(), response.getBody());
            throw e;
        }
        return propertyUIDs;
    }

    public long parseChannelId(Response response) throws Exception {
        JsonObject responseBody = getResponseBody(response);
        try{
            JsonObject data = responseBody.getAsJsonObject("data");
            return data.get("response").getAsLong();
        } catch (Exception e) {
            log.error("Failed to parse channelId from response. Reason: {}. Response body: ",
                    e.getMessage(), response.body().string());
            throw e;
        }
    }

    public String parsePropertyNameFromPropertyDetails(OAuthResourceResponse response) throws Exception {
        JsonObject responseBody = getOAuthResponseBody(response);
        try{
            JsonObject data = responseBody.getAsJsonObject("data").getAsJsonObject("response");
            String propertyName = data.get("property_name").getAsString();
            return propertyName;
        } catch (Exception e) {
            log.error("Failed to parse property name. Reason: {}. Response body: ",
                    e.getMessage(), response.getBody());
            throw e;
        }
        
    }

    public UpdateAvailabilityResponse parseChangeAvailabilityResponse(Response response) throws Exception {
        JsonObject responseBody = getResponseBody(response);
        try {
            JsonObject data = responseBody.getAsJsonObject("data");
            return gson.fromJson(data.getAsJsonObject("response").toString(), UpdateAvailabilityResponse.class);
        } catch (Exception e) {
            log.error("Failed to parse change availability response. Reason: {}. Response body: ",
                    e.getMessage(), response.body().string());
            throw e;
        }

    }

    private JsonObject getResponseBody(Response response) throws Exception {
        if (response.code() == 401) {
            throw new Exception("statuse code: 401, unauthorized request\nCheck credentials...");
        }
        JsonObject responseBody = gson.fromJson(response.body().string(), JsonObject.class);
        if (responseBody.get("error_message") != null) {
            String errorMessage = responseBody.get("error_message").getAsString();
            throw new Exception("Got error response. Error message: " + errorMessage);
        }
        return responseBody;
    }

    private JsonObject getOAuthResponseBody(OAuthResourceResponse response) throws Exception {
        if (response.getResponseCode() == 401) {
            throw new Exception("statuse code: 401, unauthorized request\nCheck credentials...");
        }
        JsonObject responseBody = gson.fromJson(response.getBody(), JsonObject.class);
        if (responseBody.get("error_message") != null) {
            String errorMessage = responseBody.get("error_message").getAsString();
            throw new Exception("Got error response. Error message: " + errorMessage);
        }
        return responseBody;
    }
}
