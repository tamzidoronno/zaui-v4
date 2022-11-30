package com.thundashop.services.octoapiservice;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.zauiactivity.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.webmanager.ZauiHttpRequest;
import com.thundashop.core.webmanager.ZauiHttpResponse;
import com.thundashop.services.core.httpservice.ZauiHttpService;
import com.thundashop.zauiactivity.constant.ZauiConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OctoApiService implements IOctoApiService {
    @Autowired
    private ZauiHttpService zauiHttpService;

    public List<OctoSupplier> getAllSuppliers() {
        String url = ZauiConstants.OCTO_API_ENDPOINT + "/suppliers";
        String result = getHttpResponseBody(url, null, "GET", null);
        Type listType = new TypeToken<List<OctoSupplier>>() {
        }.getType();
        return new Gson().fromJson(result, listType);
    }

    public List<OctoProduct> getProducts(Integer supplierId) {
        String url = ZauiConstants.OCTO_API_ENDPOINT + "/suppliers/" + supplierId + "/products";
        Map<String, String> headers = new HashMap<>();
        headers.put(ZauiConstants.OCTO_CONTENT.getLeft(), ZauiConstants.OCTO_CONTENT.getRight());
        String result = getHttpResponseBody(url, headers, "GET", null);
        Type listType = new TypeToken<List<OctoProduct>>() {
        }.getType();
        return new Gson().fromJson(result, listType);
    }

    public List<OctoProductAvailability> getAvailability(Integer supplierId,
            OctoProductAvailabilityRequestDto availabilityRequest) {
        String url = ZauiConstants.OCTO_API_ENDPOINT + "/suppliers/" + supplierId + "/availability";
        Map<String, String> headers = new HashMap<>();
        headers.put(ZauiConstants.OCTO_PRICING.getLeft(), ZauiConstants.OCTO_PRICING.getRight());
        String result = getHttpResponseBody(url, headers, "POST", availabilityRequest.toString());
        Type listType = new TypeToken<List<OctoProductAvailability>>() {
        }.getType();
        return new Gson().fromJson(result, listType);
    }

    public List<BookingReserve> reserveBooking(Integer supplierId, BookingReserveRequest bookingReserveRequest) {
        String url = ZauiConstants.OCTO_API_ENDPOINT + "/suppliers/" + supplierId + "/bookings";
        String result = getHttpResponseBody(url, null, "POST", bookingReserveRequest.toString());
        Type listType = new TypeToken<BookingReserve>() {
        }.getType();
        return new Gson().fromJson(result, listType);
    }

    public List<BookingConfirm> confirmBooking(Integer supplierId, String bookingId,
            BookingConfirmRequest bookingConfirmRequest) {
        String url = ZauiConstants.OCTO_API_ENDPOINT + "/suppliers/" + supplierId + "/bookings" + bookingId
                + "/confirm";
        String result = getHttpResponseBody(url, null, "POST", bookingConfirmRequest.toString());
        Type listType = new TypeToken<BookingConfirm>() {
        }.getType();
        return new Gson().fromJson(result, listType);
    }

    public List<BookingConfirm> cancelBooking(Integer supplierId, String bookingId) {
        String url = ZauiConstants.OCTO_API_ENDPOINT + "/suppliers/" + supplierId + "/bookings" + bookingId + "/cancel";
        String result = getHttpResponseBody(url, null, "POST", null);
        Type listType = new TypeToken<BookingConfirm>() {
        }.getType();
        return new Gson().fromJson(result, listType);
    }

    private String getHttpResponseBody(String url, Map<String, String> headers, String method, String payload) {
        ZauiHttpRequest request = ZauiHttpRequest.builder()
                .setAuth("Bearer " + ZauiConstants.OCTO_API_KEY)
                .setUrl(url)
                .setHeaders(headers)
                .setPayload(payload)
                .build();
        ZauiHttpResponse response = method == "POST" ? zauiHttpService.post(request) : zauiHttpService.get(request);
        if (!response.isSuccessful()) {
            log.error("Unsuccessful request {}, response: {}", request, response);
            throw new RuntimeException(String.format("Unsuccessful request url: [%s] , code: [%s]",
                    url, response.statusCode()));
        }
        return response.getBody();
    }
}
