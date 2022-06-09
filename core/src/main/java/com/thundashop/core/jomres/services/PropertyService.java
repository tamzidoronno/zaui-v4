package com.thundashop.core.jomres.services;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.thundashop.core.jomres.services.Constants.*;

public class PropertyService extends BaseService{
    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);
    public List<Long> getChannelsPropertyIDs(String baseUrl, String token, String channelName){
        try {
            createOAuthClient();

            OAuthClientRequest request = getBearerTokenRequest(baseUrl + GET_PROPERTY_IDS_URL_THROUGH_CHANNEL, token);
            request.addHeader("X-JOMRES-channel-name", channelName);

            OAuthResourceResponse response = tokenClient.resource(request, "GET", OAuthResourceResponse.class);

            return responseDataParser.parseAllPropertyIds(response, true);

        } catch (Exception e){
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<Long> getAllPropertyIDs(String baseUrl, String token){
        try {
            createOAuthClient();

            OAuthClientRequest request = getBearerTokenRequest(baseUrl + GET_PROPERTY_IDS_URL, token);

            OAuthResourceResponse response = tokenClient.resource(request, "GET", OAuthResourceResponse.class);

            return responseDataParser.parseAllPropertyIds(response, false);

        } catch (Exception e){
            logger.error(e.getMessage());
            return null;
        }
    }

}
