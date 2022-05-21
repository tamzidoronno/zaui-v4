package com.thundashop.core.jomres;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.thundashop.core.jomres.dto.JomresBookedRoom;
import com.thundashop.core.jomres.dto.JomresBooking;
import com.thundashop.core.jomres.dto.JomresGuest;
import com.thundashop.core.jomres.services.BaseService;
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
                LinkedTreeMap ratePlan = (LinkedTreeMap) gson.fromJson(tariffSet.get(entry.getKey()).toString(),
                        (new ArrayList<>()).getClass()).get(0);
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
            return dailyPriceMatrix;

        } catch (Exception e) {
            throw e;
        } catch (JsonSyntaxException | IOException | ParseException e) {
            throw new Exception(e.getMessage() + "\n\tGot Invalid Response while updating availability\n\t" + "Code: "
                    + response.code() + ", Message: " + response.message());
        }
    }

    public JomresBooking parseBookingDetails(OAuthResourceResponse response) throws Exception {

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
            return new JomresBooking(bookings.stream().findFirst().orElse(null));

        } catch (Exception e) {
            throw e;
        } catch (ParseException e) {
            throw new Exception("Failed to get booking details:\n\t");
        }

    }

    public List<Long> parseBookingIds(OAuthResourceResponse response) throws Exception {
        Gson gson = new Gson();
        try {
            JsonObject responseBody = gson.fromJson(response.getBody(), JsonObject.class);

            if (responseBody.get("error_message") != null) {
                String errorMessage = responseBody.get("error_message").getAsString();
                throw new Exception(errorMessage);
            }

            JsonObject data = responseBody.getAsJsonObject("data");
            String bookingsString = data.get("listbookingdate").toString();

            List<LinkedTreeMap> bookings = gson.fromJson(bookingsString, (new ArrayList<>()).getClass());
            return bookings
                    .stream()
                    .map(booking -> (long) Double.parseDouble(booking.get("contract_uid").toString()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw e;
        }
    }

    public List<JomresBooking> parseBooking(OAuthResourceResponse response) {
        Gson gson = new Gson();

        JsonObject responseBody = gson.fromJson(response.getBody(), JsonObject.class);
        JsonObject data = Optional.ofNullable(responseBody.getAsJsonObject("data")).orElse(new JsonObject());
        String bookingsString = Optional.ofNullable(data.get("listbooking")).orElse(new JsonArray()).toString();

        List<LinkedTreeMap> bookings = gson.fromJson(bookingsString, (new ArrayList<>()).getClass());
        return bookings
                .stream()
                .map(booking -> {
                    try {
                        return new JomresBooking(booking);
                    } catch (ParseException e) {
                        logger.error("Failed to parse booking of contract id: " + booking.get("contract_uid").toString());
                        logger.error("booking details:  " + booking);
                        logger.error(e.getMessage());
                        return new JomresBooking();
                    }
                })
                .collect(Collectors.toList());
    }

    public List<JomresBooking> parseBookingArrDep(OAuthResourceResponse response) {
        Gson gson = new Gson();

        JsonObject responseBody = gson.fromJson(response.getBody(), JsonObject.class);
        JsonObject data = Optional.ofNullable(responseBody.getAsJsonObject("data")).orElse(new JsonObject());
        String bookingsString = Optional.ofNullable(data.get("listbookingarrdep")).orElse(new JsonArray()).toString();

        List<LinkedTreeMap> bookings = gson.fromJson(bookingsString, (new ArrayList<>()).getClass());
        return bookings
                .stream()
                .map(booking -> {
                    try {
                        return new JomresBooking(booking);
                    } catch (ParseException e) {
                        logger.error("Failed to parse booking of contract id: " + booking.get("contract_uid").toString());
                        logger.error("booking details:  " + booking);
                        logger.error(e.getMessage());
                        return new JomresBooking();
                    }
                })
                .collect(Collectors.toList());
    }

    public List<?> parseBookingRoomDetails(OAuthResourceResponse response) {
        Gson gson = new Gson();

        JsonObject responseBody = gson.fromJson(response.getBody(), JsonObject.class);
        JsonObject data = Optional.ofNullable(responseBody.getAsJsonObject("data")).orElse(new JsonObject());
        String roomDetailsString = Optional.ofNullable(data.get("listbookingrooms")).orElse(new JsonArray()).toString();

        List<LinkedTreeMap> roomDetails = gson.fromJson(roomDetailsString, (new ArrayList<>()).getClass());
        List<JomresBookedRoom> rooms = new ArrayList<>();
//        rooms =  roomDetails.stream().map(room-> new JomresBookedRoom(room)).collect(Collectors.toList());
        return rooms;
    }

    public List<?> parsebookingGuestDetails(OAuthResourceResponse response) {
        Gson gson = new Gson();

        JsonObject responseBody = gson.fromJson(response.getBody(), JsonObject.class);
        JsonObject data = Optional.ofNullable(responseBody.getAsJsonObject("data")).orElse(new JsonObject());
        String guestDetailsString = Optional.ofNullable(data.get("listbookingguest")).orElse(new JsonArray()).toString();

        List<LinkedTreeMap> guestDetails = gson.fromJson(guestDetailsString, (new ArrayList<>()).getClass());
        List<JomresGuest> guests = new ArrayList<>();
//        guests = guestDetails.stream().map(guest-> new JomresGuest(guest)).collect(Collectors.toList());
        return guests;
    }

    public List<Long> parseAllPropertyIds(OAuthResourceResponse response, boolean channelProperties) {
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
        Gson gson = new Gson();
        JsonObject responseBody = gson.fromJson(response.body().string(), JsonObject.class);
        JsonObject data = responseBody.getAsJsonObject("data");
        return data.get("response").getAsLong();
    }

    public boolean parseChangeAvailabilityResponse(Response response) throws IOException {
        Gson gson = new Gson();
        try {
            JsonObject responseBody = gson.fromJson(response.body().string(), JsonObject.class);
            if (responseBody.get("error_message") != null) {
                String errorMessage = responseBody.get("error_message").getAsString();
                throw new Exception(errorMessage);
            }

            JsonObject data = responseBody.getAsJsonObject("data").getAsJsonObject("response");
            return data.get("success").getAsBoolean();

        } catch (Exception e) {
            throw e;
        } catch (JsonSyntaxException | IOException e) {
            throw new Exception(e.getMessage() + "\n\tGot Invalid Response while updating availability\n\t" + "Code: "
                    + response.code() + ", Message: " + response.message());
        }

    }
}
