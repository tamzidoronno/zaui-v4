package com.thundashop.services.jomresservice;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@Slf4j
public class JomresApiService {
    OAuthClient tokenClient;
    OkHttpClient httpClient;
    JomresApiResponseParser responseDataParser;

    public JomresApiService() {
        tokenClient = new OAuthClient(new URLConnectionClient());
        httpClient = new OkHttpClient().newBuilder().build();
        responseDataParser = new JomresApiResponseParser();
    }

    public String getAccessToken(String clientId, String clientSecret, String tokenURL) {
        String accessToken = null;
        try {
            OAuthClientRequest request = createTokenRequest(clientId, clientSecret, tokenURL);
            OAuthJSONAccessTokenResponse oAuthResponse = tokenClient.accessToken(request, OAuth.HttpMethod.POST,
                    OAuthJSONAccessTokenResponse.class);
            accessToken = oAuthResponse.getAccessToken();
        } catch (Exception e) {
            log.error("Failed to get the access token. Reason: {}. Actual exception: {}", e.getMessage(), e);
        }
        return accessToken;
    }

    public List<CompletableFuture<? extends Object>> getAsyncTaskResults(List<Supplier<? extends Object>> tasks) {
        ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<CompletableFuture<? extends Object>> futures = tasks.stream()
                .map(task -> CompletableFuture.supplyAsync(task, es))
                .collect(Collectors.toList());
        es.shutdown();
        return futures;
    }

    private OAuthClientRequest createTokenRequest(String clientId, String clientSecret, String tokenURL)
            throws OAuthSystemException {
        return OAuthClientRequest.tokenLocation(tokenURL)
                .setGrantType(GrantType.CLIENT_CREDENTIALS)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .buildBodyMessage();
    }

    public OAuthClientRequest getBearerTokenRequest(String url, String accessToken)
            throws IOException, OAuthSystemException {
        log.info("Sending request to: " + url);
        OAuthClientRequest request = new OAuthBearerClientRequest(url)
                .setAccessToken(accessToken)
                .buildHeaderMessage();
        return request;                                       
    }

    public Request getHttpBearerTokenRequest(
            String url, String accessToken, Map<String, String> headers, Map<String, String> formData, String method) {

        RequestBody body = getFormDataRequestBody(formData, method);

        log.info("Sending request to: {} Method: {} Header: {} Body: {}", url, method, headers, formData);
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .method(method, body)
                .addHeader("Authorization", "Bearer " + accessToken);

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        return requestBuilder.build();
    }

    private RequestBody getFormDataRequestBody(Map<String, String> formData, String method) {
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (formData != null) {
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                bodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }
            return bodyBuilder.build();
        } else if (method == "GET" || method == "DELETE") {
            return null;
        } else
            return bodyBuilder.addFormDataPart("", "")
                    .build();
    }

    public Map<String, String> addChannelIntoHeaders(Map<String, String> existingHeaders, String channel) {
        if (existingHeaders == null)
            existingHeaders = new HashMap<String, String>();
        existingHeaders.put("X-JOMRES-channel-name", channel);
        return existingHeaders;
    }

}
