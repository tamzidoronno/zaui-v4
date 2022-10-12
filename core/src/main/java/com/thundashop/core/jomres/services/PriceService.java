package com.thundashop.core.jomres.services;

import static com.thundashop.core.jomres.services.Constants.PRICE_GETTING_URL;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

public class PriceService extends BaseService{
    public Map<String, Double> getDailyPriceBetweenDates(String baseUrl, String token, String channel, int jomresPropertyId,
                                              Date start, Date end) throws Exception {
        try {
            createHttpClient();
            String url = baseUrl + PRICE_GETTING_URL + jomresPropertyId;

            Request request = getHttpBearerTokenRequest(url, token, addChannelIntoHeaders(null, channel),null, "GET");
            Response response = httpClient.newCall(request).execute();

            return responseDataParser.parseDailyPriceMatrixBetweenDates(response, start, end);
        }  catch (IOException e) {
            throw new Exception("Failed to execute the update Availability REST API request:\n\t" + e.getMessage());
        }
        catch (Exception e) {
            throw new Exception("Failed:\n\t" + e.getMessage());
        }
    }

    public Map<String, Double> getDailyPrice(String baseUrl, String token, String channel, int jomresPropertyId) throws Exception {
        try {
            createHttpClient();
            String url = baseUrl + PRICE_GETTING_URL + jomresPropertyId;

            Request request = getHttpBearerTokenRequest(url, token, addChannelIntoHeaders(null, channel),null, "GET");
            Response response = httpClient.newCall(request).execute();

            return responseDataParser.parseDailyPriceMatrix(response);
        }  catch (IOException e) {
            throw new Exception("Failed to execute get daily price REST API request:\n\t" + e.getMessage());
        }
        catch (Exception e) {
            throw new Exception("Failed:\n\t" + e.getMessage());
        }
    }
}
