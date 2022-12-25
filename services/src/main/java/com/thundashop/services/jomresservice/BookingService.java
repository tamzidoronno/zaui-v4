package com.thundashop.services.jomresservice;

import static com.thundashop.core.jomres.constants.Constants.BOOKING_DETAILS_BY_ID;
import static com.thundashop.core.jomres.constants.Constants.LIST_BOOKING_URL;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;

import com.google.common.base.Throwables;
import com.thundashop.core.jomres.dto.JomresBooking;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingService extends BaseService {

    public List<JomresBooking> getJomresBookingsBetweenDates(String baseUrl, int propertyUID, String token, Date start, Date end) throws Exception {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String startDate = dateFormat.format(start);
            String endDate = dateFormat.format(end);

            String url = baseUrl + LIST_BOOKING_URL + propertyUID + "/" + startDate + "/" + endDate;

            OAuthClientRequest request = getBearerTokenRequest(url, token);

            OAuthResourceResponse response = tokenClient.resource(request, "GET", OAuthResourceResponse.class);
            return responseDataParser.parseBookingsFromList(response);

        } catch (Exception e) {
            log.error("Failed to execute the \"Get Booking Between Dates\", Cause: {}", e.getMessage());
            throw e;
        }

    }

    public JomresBooking getCompleteBooking(String baseUrl, String token, String channel, JomresBooking booking) {
        try {
            String url = baseUrl + BOOKING_DETAILS_BY_ID + booking.propertyUid + "/" + booking.bookingId;

            OAuthClientRequest request = getBearerTokenRequest(url, token);
            request.addHeader("X-JOMRES-channel-name", channel);

            OAuthResourceResponse response = tokenClient.resource(request, "GET", OAuthResourceResponse.class);
            booking.setNumberOfGuests(responseDataParser.parseBookingGuestsNumber(response));
            booking.setCustomer(responseDataParser.parseBookingGuestDetails(response));
            return booking;

        }  catch (Exception e) {
            log.error(Throwables.getStackTraceAsString(e));
            log.error("Failed to execute the \"Get Booking Between Dates\" Availability REST API request:\n\t{}",
                    e.getMessage());
            return null;
        }
    }

}