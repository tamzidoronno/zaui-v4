package com.thundashop.services.jomresservice;

import static com.thundashop.core.jomres.constants.Constants.CHANNEL_ANNOUNCEMENT_URL;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
public class JomresChannelService extends JomresApiService {
    public long getChannelId(String baseUrl, String channelName, String token) {
        try {
            OAuthClientRequest request = getBearerTokenRequest(baseUrl + CHANNEL_ANNOUNCEMENT_URL + channelName, token);
            OAuthResourceResponse response = tokenClient.resource(request, "GET", OAuthResourceResponse.class);

            JsonObject responseBody = new Gson().fromJson(response.getBody(), JsonObject.class);
            return responseBody.getAsJsonObject("data").get("response").getAsLong();

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to get the channel...");
            log.error("Channel Name: {}", channelName);
            return 0;
        }
    }

    public long announceChannel(String baseUrl, String channelName, String friendlyChannelName, String token)
            throws Exception {
        try {
            Map<String, String> formData = new HashMap<String, String>();
            formData.put("params", "{\"has_dictionaries\":true}");

            Request request = getHttpBearerTokenRequest(baseUrl + CHANNEL_ANNOUNCEMENT_URL + channelName + "/"
                    + friendlyChannelName, token, null, formData, "POST");

            Response response = httpClient.newCall(request).execute();

            return responseDataParser.parseChannelId(response);

        } catch (IOException e) {
            log.error("Failed to annouce the challen, IOException Occuered");
            log.error(e.getMessage());
            throw new Exception(
                    "Failed to announce the channel due to IOException\n\t" + "Channel Name: " + channelName);
        } catch (Exception e) {
            log.error("Failed to announce the channel...");
            log.error("Channel Name: {}", channelName);
            throw new Exception(
                    "Failed to announce the channel...\n\t" + "Channel Name: " + channelName + "\n\t" + e.getMessage());
        }
    }
}
