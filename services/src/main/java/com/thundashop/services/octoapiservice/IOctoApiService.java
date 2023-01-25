package com.thundashop.services.octoapiservice;

import java.util.List;

import com.thundashop.core.common.ZauiException;
import com.thundashop.zauiactivity.dto.*;
import com.thundashop.zauiactivity.dto.OctoBookingConfirmRequest;
import com.thundashop.zauiactivity.dto.OctoBooking;

public interface IOctoApiService {
    List<OctoSupplier> getAllSuppliers() throws ZauiException;

    OctoSupplier getSupplierById(Integer supplierId) throws ZauiException;

    List<OctoProduct> getOctoProducts(Integer supplierId) throws ZauiException;

    public OctoBooking reserveBooking(Integer supplierId, OctoBookingReserveRequest bookingReserveRequest,
            ZauiActivityConfig config) throws ZauiException;

    public OctoBooking confirmBooking(Integer supplierId, String bookingId,
            OctoBookingConfirmRequest octoBookingConfirmRequest, ZauiActivityConfig config) throws ZauiException;

    OctoProduct getOctoProductById(Integer supplierId, Integer productId) throws ZauiException;

    List<OctoProductAvailability> getOctoProductAvailability(Integer supplierId,
            OctoProductAvailabilityRequestDto availabilityRequest) throws ZauiException;

    OctoBooking cancelBooking(Integer supplierId, String bookingId, ZauiActivityConfig config) throws ZauiException;
}
