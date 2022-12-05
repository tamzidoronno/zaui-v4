package com.thundashop.services.octoapiservice;

import java.io.IOException;
import java.util.List;

import com.thundashop.zauiactivity.dto.*;

public interface IOctoApiService {
    public List<OctoSupplier> getAllSuppliers();

    public List<OctoProduct> getProducts(Integer supplierId) throws IOException;

    public List<OctoProductAvailability> getAvailability(Integer supplierId,
            OctoProductAvailabilityRequestDto availabilityRequest);

    public OctoBookingReserve reserveBooking(Integer supplierId, OctoBookingReserveRequest bookingReserveRequest);

    public OctoBookingConfirm confirmBooking(Integer supplierId, String bookingId,
            OctoBookingConfirmRequest octoBookingConfirmRequest);

    public OctoBookingConfirm cancelBooking(Integer supplierId, String bookingId);
}
