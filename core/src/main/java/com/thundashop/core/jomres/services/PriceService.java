package com.thundashop.core.jomres.services;

import com.thundashop.core.sedox.autocryptoapi.Exception;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.thundashop.core.jomres.services.Constants.*;

public class PriceService extends BaseService{
    public Map<String, Double> getDailyPriceBetweenDates(String baseUrl, String token, String channel, int jomresPropertyId,
                                              Date start, Date end) throws Exception {
        try {
            createHttpClient();
            DateFormat formatter = new SimpleDateFormat(AVAILABILITY_SENDING_DATE_FORMAT);
            String url = baseUrl + PRICE_GETTING_URL + jomresPropertyId;

            Request request = getHttpBearerTokenRequest(url, token, addChannelIntoHeaders(null, channel),null, "GET");
            Response response = httpClient.newCall(request).execute();

            return responseDataParser.parseDailyPriceMatrixBetweenDates(response, start, end);
        } catch (Exception e) {
            throw new Exception("Failed:\n\t" + e.getMessage1());
        } catch (IOException e) {
            throw new Exception("Failed to execute the update Availability REST API request:\n\t" + e.getMessage());
        }
    }

    public Map<String, Double> getDailyPrice(String baseUrl, String token, String channel, int jomresPropertyId) throws Exception {
        try {
            createHttpClient();
            DateFormat formatter = new SimpleDateFormat(AVAILABILITY_SENDING_DATE_FORMAT);
            String url = baseUrl + PRICE_GETTING_URL + jomresPropertyId;

            Request request = getHttpBearerTokenRequest(url, token, addChannelIntoHeaders(null, channel),null, "GET");
            Response response = httpClient.newCall(request).execute();

            return responseDataParser.parseDailyPriceMatrix(response);
        } catch (Exception e) {
            throw new Exception("Failed:\n\t" + e.getMessage1());
        } catch (IOException e) {
            throw new Exception("Failed to execute the update Availability REST API request:\n\t" + e.getMessage());
        }
    }
}
