package com.thundashop.core.jomres;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.thundashop.core.jomres.dto.JomresBooking;
import com.thundashop.core.jomres.dto.JomresGuest;
import com.thundashop.core.jomres.dto.UpdateAvailabilityResponse;
import com.thundashop.core.sedox.autocryptoapi.Exception;
import okhttp3.Response;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ResponseDataParser {
    private static final Logger logger = LoggerFactory.getLogger(ResponseDataParser.class);

    public Map<String, Double> parseDailyPriceMatrixBetweenDates(Response response, Date start, Date end) throws Exception {
        try {
            if(response.code()==401){
                throw new Exception("statuse code: 401, unauthorized request\nCheck credentials...");
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Map<String, Double> dailyPriceMatrix = new HashMap<>();
            Calendar calender = Calendar.getInstance();
            calender.setTime(start);
            calender.add(Calendar.DATE, -1);
            start = calender.getTime();
            calender.setTime(end);
            calender.add(Calendar.DATE, 1);
            end = calender.getTime();
            Gson gson = new Gson();
            JsonObject responseBody = gson.fromJson(response.body().string(), JsonObject.class);
            if (responseBody.get("error_message") != null) {
                String errorMessage = responseBody.get("error_message").getAsString();
                throw new Exception(errorMessage);
            }

            JsonObject tariffSet = responseBody.getAsJsonObject("data")
                    .getAsJsonObject("response")
                    .getAsJsonObject("tariff_sets");
            for (Map.Entry<String, JsonElement> entry : tariffSet.entrySet()) {
                LinkedTreeMap ratePlan = (LinkedTreeMap) gson.fromJson(
                        tariffSet.get(entry.getKey()).toString(),
                        (new ArrayList<>()).getClass()
                ).get(0);

                LinkedTreeMap ratePerNight = (LinkedTreeMap) ratePlan.get("rate_per_night");
                double weeklyPrice = (double) ratePerNight.get("price_including_vat");
                double dailyPrice = weeklyPrice / 7.0;
                List<String> dates = (ArrayList<String>) gson.fromJson(ratePlan.get("dates").toString(), (new ArrayList<>()).getClass());
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
            throw e;
        } catch (JsonSyntaxException | IOException | ParseException e) {
            throw new Exception(e.getMessage() + "\n\tGot Invalid Response while updating availability\n\t" + "Code: "
                    + response.code() + ", Message: " + response.message());
        }
    }

    public Map<String, Double> parseDailyPriceMatrix(Response response) throws Exception {
        try {
            if(response.code()==401){
                throw new Exception("statuse code: 401, unauthorized request\nCheck credentials...");
            }
            logger.debug("Started parsing daily price matrix");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Map<String, Double> dailyPriceMatrix = new HashMap<>();

            Gson gson = new Gson();
            JsonObject responseBody = gson.fromJson(response.body().string(), JsonObject.class);
            if (responseBody.get("error_message") != null) {
                String errorMessage = responseBody.get("error_message").getAsString();
                throw new Exception(errorMessage);
            }

            JsonObject tariffSet = responseBody.getAsJsonObject("data").getAsJsonObject("response").getAsJsonObject("tariff_sets");
            for (Map.Entry<String, JsonElement> entry : tariffSet.entrySet()) {
                LinkedTreeMap ratePlan = (LinkedTreeMap) gson.fromJson(tariffSet.get(entry.getKey()).toString(),
                        (new ArrayList<>()).getClass()).get(0);
                LinkedTreeMap ratePerNight = (LinkedTreeMap) ratePlan.get("rate_per_night");
                double weeklyPrice = (double) ratePerNight.get("price_including_vat");
                double dailyPrice = weeklyPrice / 7.0;
                List<String> dates = (ArrayList<String>) gson.fromJson(ratePlan.get("dates").toString(), (new ArrayList<>()).getClass());
                for (String date : dates) {
                    String currentDate = format.format(format.parse(date));
                    dailyPriceMatrix.put(currentDate, dailyPrice);
                }

            }
            logger.debug("Ended parsing daily price matrix");
            return dailyPriceMatrix;

        } catch (Exception e) {
            throw e;
        } catch (JsonSyntaxException | IOException | ParseException e) {
            throw new Exception(e.getMessage() + "\n\tGot Invalid Response while updating availability\n\t" + "Code: "
                    + response.code() + ", Message: " + response.message());
        }
    }

    public int parseBookingGuestsNumber(OAuthResourceResponse response) throws Exception {
        if(response.getResponseCode()==401){
            throw new Exception("statuse code: 401, unauthorized request\nCheck credentials...");
        }

        Gson gson = new Gson();
        try {
            JsonObject responseBody = gson.fromJson(response.getBody(), JsonObject.class);

            if (responseBody.get("error_message") != null) {
                String errorMessage = responseBody.get("error_message").getAsString();
                throw new Exception(errorMessage);
            }

            JsonObject data = responseBody.getAsJsonObject("data");
            String bookingsString = data.get("response").toString();

            List<LinkedTreeMap> bookings = gson.fromJson(bookingsString, (new ArrayList<>()).getClass());
            LinkedTreeMap booking = bookings.stream().findFirst().orElse(null);
            if(booking!=null){
                LinkedTreeMap guestNumber = (LinkedTreeMap) booking.get("guest_numbers");
                return (int) Double.parseDouble(guestNumber.get("number_of_guests").toString());
            }
            else return 0;

        } catch (Exception e) {
            throw e;
        }
    }

    public JomresGuest parseBookingGuestDetails(OAuthResourceResponse response) throws Exception {
        if(response.getResponseCode()==401){
            throw new Exception("statuse code: 401, unauthorized request\nCheck credentials...");
        }

        Gson gson = new Gson();
        try {
            JsonObject responseBody = gson.fromJson(response.getBody(), JsonObject.class);

            if (responseBody.get("error_message") != null) {
                String errorMessage = responseBody.get("error_message").getAsString();
                throw new Exception(errorMessage);
            }

            JsonObject data = responseBody.getAsJsonObject("data");
            String bookingsString = data.get("response").toString();

            List<LinkedTreeMap> bookings = gson.fromJson(bookingsString, (new ArrayList<>()).getClass());
            LinkedTreeMap booking = bookings.stream().findFirst().orElse(null);
            if(booking!=null){
                return new JomresGuest((LinkedTreeMap) booking.get("guest_data"));
            }
            else return new JomresGuest();

        } catch (Exception e) {
            throw e;
        }
    }

    public List<JomresBooking> parseBookingsFromList(OAuthResourceResponse response) throws Exception {
        if(response.getResponseCode()==401){
            throw new Exception("statuse code: 401, unauthorized request\nCheck credentials...");
        }

        Gson gson = new Gson();
        try {
            logger.debug("Started parsing Jomres booking list between dates");
            JsonObject responseBody = gson.fromJson(response.getBody(), JsonObject.class);

            if (responseBody.get("error_message") != null) {
                String errorMessage = responseBody.get("error_message").getAsString();
                throw new Exception(errorMessage);
            }

            JsonObject data = responseBody.getAsJsonObject("data");
            String bookingsString = data.get("listbookingdate").toString();

            List<LinkedTreeMap> bookings = gson.fromJson(bookingsString, (new ArrayList<>()).getClass());
            List<JomresBooking> jomresBookings = new ArrayList<>();
            for(LinkedTreeMap booking: bookings){
                try {
                    jomresBookings.add(new JomresBooking(booking));
                } catch (ParseException e) {
                    logger.error("Falied to parse the booking for BookignId: "+booking.get("contract_uid").toString());
                }
            }
            logger.debug("Ended parsing Jomres booking list between dates");
            return jomresBookings;
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Long> parseAllPropertyIds(OAuthResourceResponse response, boolean channelProperties) throws Exception{
        if(response.getResponseCode()==401){
            throw new Exception("statuse code: 401, unauthorized request\nCheck credentials...");
        }

        List<Long> propertyUIDs = new ArrayList<Long>();
        Gson gson = new Gson();

        JsonObject responseBody = gson.fromJson(response.getBody(), JsonObject.class);
        JsonObject data = responseBody.getAsJsonObject("data");

        if (channelProperties) {
            JsonObject properties = data.getAsJsonObject("response");
            for (Map.Entry<String, JsonElement> property : properties.entrySet()) {
                propertyUIDs.add(Long.valueOf(property.getKey()));
            }
        } else {
            String properties = data.get("ids").toString();
            propertyUIDs = Arrays.stream(properties.replaceAll("\\[", "")
                            .replaceAll("]", "")
                            .split(","))
                    .map(propertyId -> Long.parseLong(propertyId))
                    .collect(Collectors.toList());

        }
        return propertyUIDs;
    }

    public long parseChannelId(Response response) throws IOException {
        if(response.code()==401){
            throw new Exception("statuse code: 401, unauthorized request\nCheck credentials...");
        }

        Gson gson = new Gson();
        JsonObject responseBody = gson.fromJson(response.body().string(), JsonObject.class);
        JsonObject data = responseBody.getAsJsonObject("data");
        return data.get("response").getAsLong();
    }

    public UpdateAvailabilityResponse parseChangeAvailabilityResponse(Response response) throws IOException {
        if(response.code()==401){
            throw new Exception("statuse code: 401, unauthorized request\nCheck credentials...");
        }
        Gson gson = new Gson();
        try {
            JsonObject responseBody = gson.fromJson(response.body().string(), JsonObject.class);
            if (responseBody.get("error_message") != null) {
                String errorMessage = responseBody.get("error_message").getAsString();
                throw new Exception(errorMessage);
            }

            JsonObject data = responseBody.getAsJsonObject("data").getAsJsonObject("response");
            boolean success = data.get("success").getAsBoolean();
            UpdateAvailabilityResponse finalRes = new UpdateAvailabilityResponse();
            finalRes.success = success;
            if(!success){
                finalRes.errorMessage = data.get("message").getAsString();
            }
            return finalRes;

        } catch (Exception e) {
            throw e;
        } catch (JsonSyntaxException | IOException e) {
            throw new Exception(e.getMessage() + "\n\tGot Invalid Response while updating availability\n\t" + "Code: "
                    + response.code() + ", Message: " + response.message());
        }

    }
}
