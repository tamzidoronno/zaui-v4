package com.thundashop.core.activity.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.activity.dto.*;
import com.thundashop.core.webmanager.ZauiHttpRequest;
import com.thundashop.core.webmanager.ZauiHttpResponse;
import com.thundashop.services.core.httpservice.ZauiHttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OctoApiService {
    @Autowired
    private ZauiHttpService zauiHttpService;
    private static final Logger logger = LoggerFactory.getLogger(OctoApiService.class);

    public String getSupplierName(Integer supplierId) {
        String url = Constants.OCTO_API_ENDPOINT +"/suppliers/"+supplierId;
        String result = getHttpResponseBody(url,null,"GET", null);
        Type listType = new TypeToken<List<JsonObject>>(){}.getType();
        List<JsonObject> suppliers =  new Gson().fromJson(result, listType);
        JsonObject supplier =  suppliers.get(0);
        return String.valueOf(supplier.get("name"));
    }

    public List<OctoProduct> getProducts(Integer supplierId) throws IOException {
        String url = Constants.OCTO_API_ENDPOINT +"/suppliers/"+supplierId+"/products";
        Map<String,String> headers = new HashMap<>();
        headers.put(Constants.OCTO_CONTENT.getLeft(), Constants.OCTO_CONTENT.getRight());
        String result = getHttpResponseBody(url,headers,"GET", null);
        Type listType = new TypeToken<List<OctoProduct>>(){}.getType();
        return new Gson().fromJson(result, listType);
    }

    public List<Availability> getAvailability(Integer supplierId, AvailabilityRequest availabilityRequest) {
        String url = Constants.OCTO_API_ENDPOINT +"/suppliers/"+supplierId+ "/availability";
        Map<String,String> headers = new HashMap<>();
        headers.put(Constants.OCTO_PRICING.getLeft(), Constants.OCTO_PRICING.getRight());
        String result = getHttpResponseBody(url,headers,"POST", availabilityRequest.toString());
        Type listType = new TypeToken<List<Availability>>(){}.getType();
        return new Gson().fromJson(result, listType);
    }

    public List<BookingReserve> reserveBooking(Integer supplierId, BookingReserveRequest bookingReserveRequest) {
        String url = Constants.OCTO_API_ENDPOINT +"/suppliers/"+supplierId+ "/bookings";
        String result = getHttpResponseBody(url,null,"POST", bookingReserveRequest.toString());
        Type listType = new TypeToken<BookingReserve>(){}.getType();
        return new Gson().fromJson(result, listType);
    }

    public List<BookingConfirm> confirmBooking(Integer supplierId, String bookingId, BookingConfirmRequest bookingConfirmRequest) {
        String url = Constants.OCTO_API_ENDPOINT +"/suppliers/" +supplierId+ "/bookings" +bookingId+ "/confirm";
        String result = getHttpResponseBody(url,null,"POST", bookingConfirmRequest.toString());
        Type listType = new TypeToken<BookingConfirm>(){}.getType();
        return new Gson().fromJson(result, listType);
    }

    public List<BookingConfirm> cancelBooking(Integer supplierId, String bookingId) {
        String url = Constants.OCTO_API_ENDPOINT +"/suppliers/" +supplierId+ "/bookings" +bookingId+ "/cancel";
        String result = getHttpResponseBody(url,null,"POST", null);
        Type listType = new TypeToken<BookingConfirm>(){}.getType();
        return new Gson().fromJson(result, listType);
    }

    private String getHttpResponseBody(String url, Map<String,String> headers, String method, String payload) {
        ZauiHttpRequest request = ZauiHttpRequest.builder()
                .setAuth("Bearer " + Constants.OCTO_API_KEY)
                .setUrl(url)
                .setHeaders(headers)
                .setPayload(payload)
                .build();
        ZauiHttpResponse response = method == "POST" ?  zauiHttpService.post(request) : zauiHttpService.get(request);
        if (!response.isSuccessful()) {
            logger.error("Unsuccessful request {}, response: {}", request, response);
            throw new RuntimeException(String.format("Unsuccessful request url: [%s] , code: [%s]",
                    url, response.statusCode()));
        }
        return response.getBody();
    }
}
