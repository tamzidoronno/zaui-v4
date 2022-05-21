package com.thundashop.core.jomres.services;

import com.thundashop.core.jomres.dto.JomresBookedRoom;
import com.thundashop.core.jomres.dto.JomresBooking;
import com.thundashop.core.jomres.dto.JomresGuest;
import com.thundashop.core.sedox.autocryptoapi.Exception;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.thundashop.core.jomres.services.Constants.BOOKING_DETAILS_BY_ID;
import static com.thundashop.core.jomres.services.Constants.LIST_BOOKING_URL;

public class BookingService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    public List<JomresBooking> getJomresBookingsBetweenDates(String baseUrl, int propertyUID, String token,
                                                             Date start, Date end, String channel) throws Exception {
        try {
            List<JomresBooking> jomresBookings = new ArrayList<JomresBooking>();
            List<Long> bookingIds = getBookingsBetweenDates(baseUrl, propertyUID, token, start, end);
            for (long bookingId : bookingIds) {
                jomresBookings.add(getBookingDetailsByBookingId(baseUrl, propertyUID, token, bookingId, channel));
            }
            return jomresBookings;

        } catch (Exception e) {
            throw new Exception("Failed to fetch Jomres Bookings\n\tPropertyID: "+propertyUID+"\n"+e.getMessage());
        }
    }

    public List<JomresBooking> listAllBooking(String baseUrl, int propertyUID, String token) throws Exception {

        try {
            createOAuthClient();

            OAuthClientRequest request = getBearerTokenRequest(baseUrl + "listbooking/" + propertyUID, token);

            OAuthResourceResponse response = tokenClient.resource(request, "GET", OAuthResourceResponse.class);
            return responseDataParser.parseBooking(response);

        } catch (Exception e) {
            throw new Exception("Failed:\n\t" + e.getMessage1());
//            return new ArrayList<JomresBooking>();
        } catch (OAuthProblemException | OAuthSystemException | IOException e) {
            throw new Exception("Failed to execute the \"List All Bookings\" Availability REST" +
                    " API request:\n\t" + e.getMessage());
        }

    }

    public JomresBooking getBookingDetailsByBookingId(String baseUrl, int propertyUID, String token, long bookingId,
                                                      String channel) throws Exception {
        try {
            createOAuthClient();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            String startDate = dateFormat.format(start);
//            String endDate = dateFormat.format(end);

            String url = baseUrl + BOOKING_DETAILS_BY_ID + propertyUID + "/" + bookingId;

            OAuthClientRequest request = getBearerTokenRequest(url, token);
            request.addHeader("X-JOMRES-channel-name", channel);

            OAuthResourceResponse response = tokenClient.resource(request, "GET", OAuthResourceResponse.class);
            return responseDataParser.parseBookingDetails(response);

        } catch (Exception e) {
            throw new Exception("Failed:\n\t" + e.getMessage1());
        } catch (IOException | OAuthSystemException | OAuthProblemException e) {
            throw new Exception("Failed to execute the \"Get Booking Between Dates\" Availability REST" +
                    " API request:\n\t" + e.getMessage());
        }

    }

    public List<Long> getBookingsBetweenDates(String baseUrl, int propertyUID, String token, Date start, Date end) throws Exception {
        try {
            createOAuthClient();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String startDate = dateFormat.format(start);
            String endDate = dateFormat.format(end);

            String url = baseUrl + LIST_BOOKING_URL + propertyUID + "/" + startDate + "/" + endDate;

            OAuthClientRequest request = getBearerTokenRequest(url, token);

            OAuthResourceResponse response = tokenClient.resource(request, "GET", OAuthResourceResponse.class);
            return responseDataParser.parseBookingIds(response);

        } catch (Exception e) {
            throw new Exception("Failed:\n\t" + e.getMessage1());
        } catch (IOException | OAuthSystemException | OAuthProblemException e) {
            throw new Exception("Failed to execute the \"Get Booking Between Dates\" Availability REST" +
                    " API request:\n\t" + e.getMessage());
        }

    }

    public List<JomresBooking> getBookingDates(String baseUrl, long contractUID, int propertyUID, String token) {

        try {
            createOAuthClient();

            OAuthClientRequest request = getBearerTokenRequest(baseUrl + "listbooking/" + propertyUID + "/" + contractUID + "/arr-dep", token);

            OAuthResourceResponse response = tokenClient.resource(request, "GET", OAuthResourceResponse.class);
            return responseDataParser.parseBookingArrDep(response);

        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        } catch (OAuthProblemException | OAuthSystemException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public List<JomresBookedRoom> getBookingRoomDetails(String baseUrl, long contractUID, int propertyUID, String token) {

        try {
            createOAuthClient();

            OAuthClientRequest request = getBearerTokenRequest(baseUrl + "listbooking/"
                    + propertyUID + "/" + contractUID + "/rooms", token);

            OAuthResourceResponse response = tokenClient.resource(request, "GET", OAuthResourceResponse.class);
            return (List<JomresBookedRoom>) responseDataParser.parseBookingRoomDetails(response);

        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        } catch (OAuthProblemException e) {
            throw new RuntimeException(e);
        } catch (OAuthSystemException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public List<JomresGuest> getBookingGuestDetails(String baseUrl, long contractUID, int propertyUID, String token) {

        try {
            createOAuthClient();

            OAuthClientRequest request = getBearerTokenRequest(baseUrl + "listbooking/"
                    + propertyUID + "/" + contractUID + "/guest", token);

            OAuthResourceResponse response = tokenClient.resource(request, "GET", OAuthResourceResponse.class);
            return (List<JomresGuest>) responseDataParser.parsebookingGuestDetails(response);

        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        } catch (OAuthProblemException e) {
            throw new RuntimeException(e);
        } catch (OAuthSystemException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
