package com.thundashop.services.octoapiservice;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thundashop.core.common.ZauiException;
import com.thundashop.services.config.FrameworkConfig;
import com.thundashop.zauiactivity.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.webmanager.ZauiHttpRequest;
import com.thundashop.core.webmanager.ZauiHttpResponse;
import com.thundashop.services.core.httpservice.IZauiHttpService;
import com.thundashop.zauiactivity.constant.ZauiConstants;
import com.thundashop.zauiactivity.dto.OctoProduct;
import com.thundashop.zauiactivity.dto.OctoProductAvailability;
import com.thundashop.zauiactivity.dto.OctoProductAvailabilityRequestDto;
import com.thundashop.zauiactivity.dto.OctoSupplier;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OctoApiService implements IOctoApiService {
    @Autowired
    private IZauiHttpService zauiHttpService;

    private final Gson gson = new Gson();

    @Autowired
    private FrameworkConfig frameworkConfig;

    private String getOctoBaseUrl() {
        return frameworkConfig.getOctoBaseUrl();
    }

    private String getOctoApiKey() {
        return frameworkConfig.getOctoApiKey();
    }

    @Override
    public List<OctoSupplier> getAllSuppliers() throws ZauiException {
        String url = getOctoBaseUrl() + "/suppliers";
        String result = getHttpResponseBody(url, Collections.emptyMap(), "GET", null);
        Type listType = new TypeToken<List<OctoSupplier>>() {
        }.getType();
        return new Gson().fromJson(result, listType);
    }

    @Override
    public OctoSupplier getSupplierById(Integer supplierId) throws ZauiException {
        String url = getOctoBaseUrl() + "/suppliers/" + supplierId;
        String result = getHttpResponseBody(url, Collections.emptyMap(), "GET", null);
        Type listType = new TypeToken<OctoSupplier>() {
        }.getType();
        return new Gson().fromJson(result, listType);
    }

    @Override
    public List<OctoProduct> getOctoProducts(Integer supplierId) throws ZauiException {
        String url = getOctoBaseUrl() + "/suppliers/" + supplierId + "/products";
        log.info("<Zaui Activity Sync> Fetch Octo Activities Url: {}", url);
        Map<String, String> headers = new HashMap<>();
        headers.put(ZauiConstants.OCTO_CONTENT.getLeft(), ZauiConstants.OCTO_CONTENT.getRight());
        log.info("<Zaui Activity Sync> Fetch Octo Activities Headers: {}", headers);
        String result = getHttpResponseBody(url, headers, "GET", null);
        log.info("<Zaui Activity Sync> Fetch Octo Activities Result: {}", result);
        Type listType = new TypeToken<List<OctoProduct>>() {
        }.getType();
        return new Gson().fromJson(result, listType);
    }

    @Override
    public OctoProduct getOctoProductById(Integer supplierId, Integer productId) throws ZauiException {
        String url = getOctoBaseUrl() + "/suppliers/" + supplierId + "/products/" + productId;
        Map<String, String> headers = new HashMap<>();
        headers.put(ZauiConstants.OCTO_CONTENT.getLeft(), ZauiConstants.OCTO_CONTENT.getRight());
        String result = getHttpResponseBody(url, headers, "GET", null);
        Type listType = new TypeToken<OctoProduct>() {
        }.getType();
        return new Gson().fromJson(result, listType);
    }

    @Override
    public List<OctoProductAvailability> getOctoProductAvailability(Integer supplierId,
            OctoProductAvailabilityRequestDto availabilityRequest) throws ZauiException {
        String url = getOctoBaseUrl() + "/suppliers/" + supplierId + "/availability";
        Map<String, String> headers = new HashMap<>();
        headers.put(ZauiConstants.OCTO_PRICING.getLeft(),
                ZauiConstants.OCTO_PRICING.getRight() + "," + ZauiConstants.OCTO_NORWEGIAN_TAX.getRight());
        String result = getHttpResponseBody(url, headers, "POST", gson.toJson(availabilityRequest));
        Type listType = new TypeToken<List<OctoProductAvailability>>() {
        }.getType();
        return new Gson().fromJson(result, listType);

    }

    @Override
    public OctoBooking reserveBooking(Integer supplierId, OctoBookingReserveRequest bookingReserveRequest)
            throws ZauiException {
        String url = getOctoBaseUrl() + "/suppliers/" + supplierId + "/bookings";
        Map<String, String> headers = new HashMap<>();
        headers.put(ZauiConstants.OCTO_PRICING.getLeft(),
                ZauiConstants.OCTO_PRICING.getRight() + "," + ZauiConstants.OCTO_NORWEGIAN_TAX.getRight());
        String result = getHttpResponseBody(url, headers, "POST", gson.toJson(bookingReserveRequest));
        Type listType = new TypeToken<OctoBooking>() {
        }.getType();
        return new Gson().fromJson(result, listType);
    }

    @Override
    public OctoBooking confirmBooking(Integer supplierId, String bookingId,
            OctoBookingConfirmRequest octoBookingConfirmRequest) throws ZauiException {
        String url = getOctoBaseUrl() + "/suppliers/" + supplierId + "/bookings/" + bookingId
                + "/confirm";
        Map<String, String> headers = new HashMap<>();
        headers.put(ZauiConstants.OCTO_PRICING.getLeft(),
                ZauiConstants.OCTO_PRICING.getRight() + "," + ZauiConstants.OCTO_NORWEGIAN_TAX.getRight());
        String result = getHttpResponseBody(url, headers, "POST", gson.toJson(octoBookingConfirmRequest));
        Type listType = new TypeToken<OctoBooking>() {
        }.getType();
        return new Gson().fromJson(result, listType);
    }

    @Override
    public OctoBooking cancelBooking(Integer supplierId, String bookingId) throws ZauiException {
        String url = getOctoBaseUrl() + "/suppliers/" + supplierId + "/bookings/" + bookingId
                + "/cancel";
        Map<String, String> headers = new HashMap<>();
        headers.put(ZauiConstants.OCTO_PRICING.getLeft(),
                ZauiConstants.OCTO_PRICING.getRight() + "," + ZauiConstants.OCTO_NORWEGIAN_TAX.getRight());
        String result = getHttpResponseBody(url, headers, "DELETE", null);
        Type listType = new TypeToken<OctoBooking>() {
        }.getType();
        return new Gson().fromJson(result, listType);
    }

    private String getHttpResponseBody(String url, Map<String, String> headers, String method, String payload)
            throws ZauiException {
        ZauiHttpRequest request = ZauiHttpRequest.builder()
                .setAuth("Bearer " + getOctoApiKey())
                .setUrl(url)
                .setHeaders(headers)
                .setPayload(payload)
                .build();
        ZauiHttpResponse response;
        switch (method) {
            case "GET":
                response = zauiHttpService.get(request);
                break;
            case "POST":
                response = zauiHttpService.post(request);
                break;
            case "DELETE":
                response = zauiHttpService.delete(request);
                break;
            default:
                throw new ZauiException(500, method + " is not supported");
        }
        if (!response.isSuccessful()) {
            log.error("Unsuccessful octo api request {}, response: {}", request, response);
            throw new ZauiException(response.statusCode(), response.getErrorMessaage());
        }
        return response.getBody();
    }
}
