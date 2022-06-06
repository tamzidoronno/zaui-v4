package com.thundashop.core.jomres.services;

import com.thundashop.core.jomres.dto.JomresBooking;
import com.thundashop.core.sedox.autocryptoapi.Exception;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.thundashop.core.jomres.services.Constants.BOOKING_DETAILS_BY_ID;
import static com.thundashop.core.jomres.services.Constants.LIST_BOOKING_URL;

public class BookingService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    public List<JomresBooking> getJomresBookingsBetweenDates(String baseUrl, int propertyUID, String token, Date start, Date end) throws Exception {
        try {
            createOAuthClient();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String startDate = dateFormat.format(start);
            String endDate = dateFormat.format(end);

            String url = baseUrl + LIST_BOOKING_URL + propertyUID + "/" + startDate + "/" + endDate;

            OAuthClientRequest request = getBearerTokenRequest(url, token);

            OAuthResourceResponse response = tokenClient.resource(request, "GET", OAuthResourceResponse.class);
            return responseDataParser.parseBookingsFromList(response);

        } catch (Exception e) {
            throw new Exception("Failed:\n\t" + e.getMessage1());
        } catch (IOException | OAuthSystemException | OAuthProblemException e) {
            throw new Exception("Failed to execute the \"Get Booking Between Dates\" Availability REST" +
                    " API request:\n\t" + e.getMessage());
        }

    }

    public JomresBooking getCompleteBooking(String baseUrl, String token, String channel, JomresBooking booking){
        try{
            createOAuthClient();
            String url = baseUrl + BOOKING_DETAILS_BY_ID + booking.propertyUid + "/" + booking.bookingId;

            OAuthClientRequest request = getBearerTokenRequest(url, token);
            request.addHeader("X-JOMRES-channel-name", channel);

            OAuthResourceResponse response = tokenClient.resource(request, "GET", OAuthResourceResponse.class);
            booking.setNumberOfGuests(responseDataParser.parseBookingGuestsNumber(response));
            booking.setCustomer(responseDataParser.parseBookingGuestDetails(response));
            return booking;

        }catch (Exception e){
            logger.error(e.getMessage1());
            return null;

        } catch (IOException | OAuthSystemException | OAuthProblemException e) {
            logger.error("Failed to execute the \"Get Booking Between Dates\" Availability REST" +
                    " API request:\n\t" + e.getMessage());
            System.out.println("Failed to execute the \"Get Booking Between Dates\" Availability REST" +
                    " API request:\n\t" + e.getMessage());
            return null;
        }

    }

}