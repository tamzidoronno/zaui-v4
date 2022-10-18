package com.thundashop.core.jomres.services;

import static com.thundashop.core.jomres.services.Constants.CHANNEL_ANNOUNCEMENT_URL;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.Request;
import okhttp3.Response;

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

        }  catch (IOException e) {
            logger.error("Failed to annouce the challen, IOException Occuered");
            logger.error(e.getMessage());
            throw new Exception("Failed to announce the channel due to IOException\n\t"+ "Channel Name: "+channelName);
        }
        catch (Exception e) {
            logger.error("Failed to announce the channel...");
            logger.error("Channel Name: "+channelName);
            throw new Exception("Failed to announce the channel...\n\t"+ "Channel Name: "+channelName+"\n\t"+e.getMessage());

        }
    }
}
