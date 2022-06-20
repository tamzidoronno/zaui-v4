package com.thundashop.core.jomres.services;

import com.thundashop.core.jomres.JomresManager;
import com.thundashop.core.jomres.ResponseDataParser;
import okhttp3.*;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class BaseService {

    OAuthClient tokenClient =  null;
    OkHttpClient httpClient = null;
    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);


    ResponseDataParser responseDataParser = new ResponseDataParser();

    public String getAccessToken(String clientId, String clientSecret, String tokenURL) throws Exception {
        createOAuthClient();
        String accessToken = null;
        try {
            OAuthClientRequest request = createTokenRequest(clientId, clientSecret, tokenURL);

            OAuthJSONAccessTokenResponse oAuthResponse = getAccessTokenResponse(request);

            accessToken = oAuthResponse.getAccessToken();

        } catch (Exception e) {
            logger.error("Failed to get the access token. Reason: ");
            logger.error(e.getMessage());
            logger.error("Returning null Token...");
            throw e;
        }
        return accessToken;
    }

    public OAuthClientRequest createTokenRequest(String clientId, String clientSecret, String tokenURL)
            throws OAuthSystemException {
        return OAuthClientRequest.tokenLocation(tokenURL)
                .setGrantType(GrantType.CLIENT_CREDENTIALS)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .buildBodyMessage();
    }

    public OAuthClientRequest getBearerTokenRequest(String url, String accessToken)
            throws IOException, OAuthSystemException {
        System.out.println("Request to "+url);
        logger.debug("Creating request url: "+url);
        OAuthClientRequest request =  new OAuthBearerClientRequest(url)
                .setAccessToken(accessToken)
                .buildHeaderMessage();
        return request;

    }

    public Request getHttpBearerTokenRequest(
            String url, String accessToken, Map<String, String> headers, Map<String, String> formData, String method){

        RequestBody body = getFormDataRequestBody(formData, method);

        logger.debug("Creating request for URL: "+url);
        System.out.println("Request to Url: "+url);
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .method(method, body)
                .addHeader("Authorization", "Bearer " + accessToken);

        if(headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        return requestBuilder.build();
    }

    public OAuthJSONAccessTokenResponse getAccessTokenResponse(OAuthClientRequest request)
            throws OAuthProblemException, OAuthSystemException {
        return tokenClient.accessToken(request, OAuth.HttpMethod.POST, OAuthJSONAccessTokenResponse.class);
    }

    public OAuthClient createOAuthClient() {
        if(tokenClient == null) tokenClient =  new OAuthClient(new URLConnectionClient());
        return tokenClient;
    }

    public OkHttpClient createHttpClient() {
        if(httpClient == null) httpClient = new OkHttpClient().newBuilder()
                .build();
        return httpClient;
    }

    RequestBody getFormDataRequestBody(Map<String, String> formData, String method){
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (formData != null) {
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                bodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }
            return bodyBuilder.build();
        } else if(method =="GET"|| method == "DELETE"){
            return null;
        }
        else return  bodyBuilder.addFormDataPart("", "")
                .build();
    }

    Map<String, String> addChannelIntoHeaders(Map<String, String> existingHeaders, String channel){
        if(existingHeaders==null) existingHeaders = new HashMap<String, String>();
        existingHeaders.put("X-JOMRES-channel-name", channel);
        return existingHeaders;
    }

    static <T> void inspect(Object o) throws IllegalAccessException {
        Field[] fields = o.getClass().getDeclaredFields();
        System.out.printf("%d fields:%n", fields.length);
        for (Field field : fields) {
            System.out.printf("%s %s %s %s%n",
                    Modifier.toString(field.getModifiers()),
                    field.getType().getSimpleName(),
                    field.getName(),
                    field.get(o).toString()
            );

        }
    }

}
