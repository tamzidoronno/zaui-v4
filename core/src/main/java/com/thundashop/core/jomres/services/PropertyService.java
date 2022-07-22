package com.thundashop.core.jomres.services;

import com.google.common.base.Throwables;
import com.thundashop.core.jomres.dto.JomresProperty;
import com.thundashop.core.sedox.autocryptoapi.Exception;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.thundashop.core.jomres.services.Constants.*;

public class PropertyService extends BaseService{
    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);
    public List<Integer> getChannelsPropertyIDs(String baseUrl, String token, String channelName) throws Exception {
        try {
            createOAuthClient();

            OAuthClientRequest request = getBearerTokenRequest(baseUrl + GET_PROPERTY_IDS_URL_THROUGH_CHANNEL, token);
            request.addHeader("X-JOMRES-channel-name", channelName);

            OAuthResourceResponse response = tokenClient.resource(request, "GET", OAuthResourceResponse.class);

            return responseDataParser.parseAllPropertyIds(response, true);

        } catch (Exception e){
            logger.error(e.getMessage());
            return new ArrayList<>();
        } catch (OAuthProblemException | IOException | OAuthSystemException e) {
            logger.error(Throwables.getStackTraceAsString(e));
            throw new Exception(e.getMessage());
        }
    }

    private List<Supplier<?>> getJomresPropertyTask(
            String baseUrl, String token, String channelName, List<Integer> propertyIds){
        List<Supplier<?>> tasks = new ArrayList<>();
        for(int propertyId: propertyIds){
            tasks.add( ()-> {
                        try {
                            return getPropertyNameFromPropertyId(
                                    baseUrl,
                                    token,
                                    channelName,
                                    propertyId);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        }
        return tasks;
    }

    private List<JomresProperty> getPropertiesFromFutureResults(List<CompletableFuture<?> >results){
        List<JomresProperty> jomresProperties = new ArrayList<>();
        for(CompletableFuture<?> result: results){
            try{
                jomresProperties.add((JomresProperty) result.get());
            } catch (ExecutionException | InterruptedException e) {
                logger.error(Throwables.getStackTraceAsString(e));
                logText("Failed to get a property, check log files");
                logText(e.getMessage());
            }
        }
        return jomresProperties.stream()
                .sorted(Comparator.comparingInt(JomresProperty::getPropertyId))
                .collect(Collectors.toList());
    }

    public List<JomresProperty> getPropertiesFromIds(String baseUrl, String token, String channelName, List<Integer>propertyIds){
        List<Supplier<?> > tasks = getJomresPropertyTask(baseUrl, token, channelName, propertyIds);
        List<CompletableFuture<?>> results = getAsyncTaskResults(tasks);
        List<JomresProperty> properties = getPropertiesFromFutureResults(results);
        return properties;
    }

    public JomresProperty getPropertyNameFromPropertyId(
            String baseUrl, String token, String channelName, int propertyId) throws Exception{
        try {
            createOAuthClient();
            JomresProperty jomresProperty = new JomresProperty();
            jomresProperty.setPropertyId(propertyId);
            String url = baseUrl + GET_PROPERTY_DETAILS_URL+propertyId+"/";
            OAuthClientRequest request = getBearerTokenRequest(url, token);
            request.addHeader("X-JOMRES-channel-name", channelName);

            OAuthResourceResponse response = tokenClient.resource(request, "GET", OAuthResourceResponse.class);
            String propertyName = responseDataParser.parsePropertyNameFromPropertyDetails(response);
            if(propertyName!=null)
                jomresProperty.setPropertyName(propertyName);

            return jomresProperty;

        } catch (Exception e) {
            throw new Exception("From property service: "+e.getMessage1());
        } catch (IOException | OAuthSystemException |OAuthProblemException e){
            logger.error(Throwables.getStackTraceAsString(e));
            throw new Exception("From property service: ");
        }
    }

}
