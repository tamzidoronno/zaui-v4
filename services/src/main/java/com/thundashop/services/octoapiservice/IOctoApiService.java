package com.thundashop.services.octoapiservice;

import java.io.IOException;
import java.util.List;

import com.thundashop.zauiactivity.dto.*;

public interface IOctoApiService {
    public String getSupplierName(Integer supplierId);

    public List<OctoProduct> getProducts(Integer supplierId) throws IOException;

    public List<Availability> getAvailability(Integer supplierId, AvailabilityRequest availabilityRequest);

    public List<BookingReserve> reserveBooking(Integer supplierId, BookingReserveRequest bookingReserveRequest);

    public List<BookingConfirm> confirmBooking(Integer supplierId, String bookingId,
            BookingConfirmRequest bookingConfirmRequest);

    public List<BookingConfirm> cancelBooking(Integer supplierId, String bookingId);
}
