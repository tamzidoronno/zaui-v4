package com.thundashop.services.octoapiservice;

import java.io.IOException;
import java.util.List;

import com.thundashop.zauiactivity.dto.*;

public interface IOctoApiService {
    public List<OctoSupplier> getAllSuppliers();

    public List<OctoProduct> getProducts(Integer supplierId) throws IOException;

    public List<OctoProductAvailability> getAvailability(Integer supplierId,
            OctoProductAvailabilityRequestDto availabilityRequest);

    public List<BookingReserve> reserveBooking(Integer supplierId, BookingReserveRequest bookingReserveRequest);

    public List<BookingConfirm> confirmBooking(Integer supplierId, String bookingId,
            BookingConfirmRequest bookingConfirmRequest);

    public List<BookingConfirm> cancelBooking(Integer supplierId, String bookingId);
}
