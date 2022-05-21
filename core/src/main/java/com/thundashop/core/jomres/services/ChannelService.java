package com.thundashop.core.jomres.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thundashop.core.jomres.dto.JomresBooking;
import com.thundashop.core.sedox.autocryptoapi.Exception;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.thundashop.core.jomres.services.Constants.CHANNEL_ANNOUNCEMENT_URL;

public class ChannelService extends BaseService{

    private static final Logger logger = LoggerFactory.getLogger(ChannelService.class);
    public long getChannelId(String baseUrl, String channelName, String token){
        try {
            createOAuthClient();

            OAuthClientRequest request = getBearerTokenRequest(baseUrl + CHANNEL_ANNOUNCEMENT_URL + channelName, token);
            OAuthResourceResponse response = tokenClient.resource(request, "GET", OAuthResourceResponse.class);

            JsonObject responseBody = new Gson().fromJson(response.getBody(), JsonObject.class);
            return responseBody.getAsJsonObject("data").get("response").getAsLong();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to get the channel...");
            logger.error("Channel Name: "+channelName);
            return 0;
        } catch (OAuthProblemException e) {
            throw new RuntimeException(e);
        } catch (OAuthSystemException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long announceChannel(String baseUrl, String channelName, String friendlyChannelName, String token) throws Exception {
        try {
            createHttpClient();

            Map<String, String> formData = new HashMap<String, String>();
            formData.put("params","{\"has_dictionaries\":true}");

            Request request = getHttpBearerTokenRequest(baseUrl+ CHANNEL_ANNOUNCEMENT_URL + channelName +"/"
                    +friendlyChannelName, token,null, formData, "POST");

            Response response = httpClient.newCall(request).execute();

            return responseDataParser.parseChannelId(response);

        } catch (Exception e) {
            logger.error("Failed to announce the channel...");
            logger.error("Channel Name: "+channelName);
            throw new Exception("Failed to announce the channel...\n\t"+ "Channel Name: "+channelName);

        } catch (IOException e) {
            logger.error("Failed to annouce the challen, IOException Occuered");
            logger.error(e.getMessage());
            throw new Exception("Failed to announce the channel due to IOException\n\t"+ "Channel Name: "+channelName);
        }
    }

//    public void assignPropertiesToChannel(String baseUrl, String channelName, ArrayList<Integer>propertyIds, int channelId, int cmsUserId){
//        try {
//            createHttpClient();
//            inspect(new JomresBooking());
//            Map<String, String> formData = new HashMap<String, String>();
//            formData.put("params","{\"has_dictionaries\":true}");
//
//            Request request = getHttpTokenRequest(baseUrl+ CHANNEL_ANNOUNCEMENT_URL + channelName +"/"
//                    +friendlyChannelName, token,null, formData, "POST");
//
//            Response response = httpClient.newCall(request).execute();
//
//            return responseDataParser.parseChannelId(response);

//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Failed to announce the channel...");
//            logger.error("Channel Name: "+channelName);
//
//        }
//    }
}
