package com.thundashop.core.jomres.services;

import static com.thundashop.core.jomres.services.Constants.GET_PROPERTY_DETAILS_URL;
import static com.thundashop.core.jomres.services.Constants.GET_PROPERTY_IDS_URL_THROUGH_CHANNEL;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;

import com.google.common.base.Throwables;
import com.thundashop.core.jomres.dto.JomresProperty;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertyService extends BaseService {

    public List<Integer> getChannelsPropertyIDs(String baseUrl, String token, String channelName) throws Exception {
        try {
            OAuthClientRequest request = getBearerTokenRequest(baseUrl + GET_PROPERTY_IDS_URL_THROUGH_CHANNEL, token);
            request.addHeader("X-JOMRES-channel-name", channelName);
            OAuthResourceResponse response = tokenClient.resource(request, "GET", OAuthResourceResponse.class);
            return responseDataParser.parseAllPropertyIds(response, true);
        } catch (Exception e) {
            log.error("Failed to get channel property id's, channel name: {}", channelName);
            throw e;
        }
    }

    public List<JomresProperty> getPropertiesFromIds(
            String baseUrl, String token, String channelName, List<Integer> propertyIds) {
        List<JomresProperty> properties = new ArrayList<>();
        for (int propertyId : propertyIds) {
            JomresProperty response = getPropertyNameFromPropertyId(
                    baseUrl,
                    token,
                    channelName,
                    propertyId);
            if (response != null) {
                properties.add(response);
            }
        }
        return properties;
    }

    // async call was failing with error 504 from jomres. that's why not using this
    // right now.
    public List<JomresProperty> getPropertiesFromIdsAsync(String baseUrl, String token, String channelName,
            List<Integer> propertyIds) {
        List<Supplier<?>> tasks = getJomresPropertyTask(baseUrl, token, channelName, propertyIds);
        List<CompletableFuture<?>> results = getAsyncTaskResults(tasks);
        List<JomresProperty> properties = getPropertiesFromFutureResults(results);
        return properties;
    }

    private List<Supplier<?>> getJomresPropertyTask(
            String baseUrl, String token, String channelName, List<Integer> propertyIds) {
        List<Supplier<?>> tasks = new ArrayList<>();
        for (int propertyId : propertyIds) {
            tasks.add(() -> {
                try {
                    return getPropertyNameFromPropertyId(
                            baseUrl,
                            token,
                            channelName,
                            propertyId);
                } catch (Exception e) {
                    log.error("Failed to fetch property name, cause: {}, propertyId: {}", e.getMessage(), propertyId);
                    throw new RuntimeException(e);
                }
            });
        }
        return tasks;
    }

    private List<JomresProperty> getPropertiesFromFutureResults(List<CompletableFuture<?>> results) {
        List<JomresProperty> jomresProperties = new ArrayList<>();
        for (CompletableFuture<?> result : results) {
            try {
                jomresProperties.add((JomresProperty) result.get());
            } catch (ExecutionException | InterruptedException e) {
                log.error(Throwables.getStackTraceAsString(e));
                log.error("Failed to get a property, check log files");
                log.error(e.getMessage());
            }
        }
        return jomresProperties.stream()
                .sorted(Comparator.comparingInt(JomresProperty::getPropertyId))
                .collect(Collectors.toList());
    }

    private JomresProperty getPropertyNameFromPropertyId(
            String baseUrl, String token, String channelName, int propertyId) {
        String url = baseUrl + GET_PROPERTY_DETAILS_URL + propertyId + "/";
        try {
            JomresProperty jomresProperty = new JomresProperty();
            jomresProperty.setPropertyId(propertyId);

            OAuthClientRequest request = getBearerTokenRequest(url, token);
            request.addHeader("X-JOMRES-channel-name", channelName);

            OAuthResourceResponse response = tokenClient.resource(request, "GET", OAuthResourceResponse.class);
            String propertyName = responseDataParser.parsePropertyNameFromPropertyDetails(response);

            if (StringUtils.isBlank(propertyName)) {
                log.error("Got blank property name for property id {}", propertyId);
                return null;
            }
            jomresProperty.setPropertyName(propertyName);
            return jomresProperty;

        } catch (Exception e) {
            log.error("Failed to get property from {}. Reason: {}. Actual Error: {}", url, e.getMessage(), e);
            log.error(Throwables.getStackTraceAsString(e));
            return null;
        }
    }

}
