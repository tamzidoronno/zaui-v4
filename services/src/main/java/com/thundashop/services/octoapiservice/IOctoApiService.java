package com.thundashop.services.octoapiservice;

import java.io.IOException;
import java.util.List;

import com.thundashop.zauiactivity.dto.BookingConfirm;
import com.thundashop.zauiactivity.dto.BookingConfirmRequest;
import com.thundashop.zauiactivity.dto.BookingReserve;
import com.thundashop.zauiactivity.dto.BookingReserveRequest;
import com.thundashop.zauiactivity.dto.OctoProduct;
import com.thundashop.zauiactivity.dto.OctoProductAvailability;
import com.thundashop.zauiactivity.dto.OctoProductAvailabilityRequestDto;

public interface IOctoApiService {
    public String getSupplierName(Integer supplierId);
    public List<OctoProduct> getProducts(Integer supplierId) throws IOException;
    public List<OctoProductAvailability> getAvailability(Integer supplierId, OctoProductAvailabilityRequestDto availabilityRequest);
    public List<BookingReserve> reserveBooking(Integer supplierId, BookingReserveRequest bookingReserveRequest);
    public List<BookingConfirm> confirmBooking(Integer supplierId, String bookingId, BookingConfirmRequest bookingConfirmRequest);
    public List<BookingConfirm> cancelBooking(Integer supplierId, String bookingId);
}
