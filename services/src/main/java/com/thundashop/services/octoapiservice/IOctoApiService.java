package com.thundashop.services.octoapiservice;

import java.util.List;

import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.zauiactivity.dto.*;
import com.thundashop.zauiactivity.dto.OctoBookingConfirmRequest;
import com.thundashop.zauiactivity.dto.OctoBooking;

public interface IOctoApiService {
    List<OctoSupplier> getAllSuppliers() throws ZauiException;

    OctoSupplier getSupplierById(Integer supplierId) throws ZauiException;

    List<OctoProduct> getOctoProducts(Integer supplierId) throws ZauiException;

    public OctoBooking reserveBooking(Integer supplierId, OctoBookingReserveRequest bookingReserveRequest) throws ZauiException;

    public OctoBooking confirmBooking(Integer supplierId, String bookingId, OctoBookingConfirmRequest octoBookingConfirmRequest) throws ZauiException;

    OctoProduct getOctoProductById(Integer supplierId, Integer productId) throws ZauiException;

    List<OctoProductAvailability> getOctoProductAvailability(Integer supplierId, OctoProductAvailabilityRequestDto availabilityRequest) throws ZauiException;

    OctoBooking cancelBooking(Integer supplierId, String bookingId) throws ZauiException;
}
