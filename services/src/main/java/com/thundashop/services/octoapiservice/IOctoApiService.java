package com.thundashop.services.octoapiservice;

import java.util.List;

import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.zauiactivity.dto.*;
import com.thundashop.zauiactivity.dto.OctoBookingConfirm;
import com.thundashop.zauiactivity.dto.OctoBookingConfirmRequest;
import com.thundashop.zauiactivity.dto.OctoBookingReserve;

public interface IOctoApiService {
    List<OctoSupplier> getAllSuppliers();

    OctoSupplier getSupplierById(Integer supplierId);

    List<OctoProduct> getOctoProducts(Integer supplierId);

    public OctoBookingReserve reserveBooking(Integer supplierId, OctoBookingReserveRequest bookingReserveRequest);

    public OctoBookingConfirm confirmBooking(Integer supplierId, String bookingId, OctoBookingConfirmRequest octoBookingConfirmRequest);

    OctoProduct getOctoProductById(Integer supplierId, Integer productId);

    List<OctoProductAvailability> getOctoProductAvailability(Integer supplierId, OctoProductAvailabilityRequestDto availabilityRequest);

    OctoBookingConfirm cancelBooking(Integer supplierId, String bookingId);
}
