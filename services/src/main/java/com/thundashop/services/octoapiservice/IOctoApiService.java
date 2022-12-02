package com.thundashop.services.octoapiservice;

import java.util.List;

import com.thundashop.zauiactivity.dto.BookingConfirm;
import com.thundashop.zauiactivity.dto.BookingConfirmRequest;
import com.thundashop.zauiactivity.dto.BookingReserve;
import com.thundashop.zauiactivity.dto.BookingReserveRequest;
import com.thundashop.zauiactivity.dto.OctoProduct;
import com.thundashop.zauiactivity.dto.OctoProductAvailability;
import com.thundashop.zauiactivity.dto.OctoProductAvailabilityRequestDto;
import com.thundashop.zauiactivity.dto.OctoSupplier;

public interface IOctoApiService {
        List<OctoSupplier> getAllSuppliers();

        OctoSupplier getSupplierById(Integer supplierId);

        List<OctoProduct> getOctoProducts(Integer supplierId);

        OctoProduct getOctoProductById(Integer supplierId, Integer productId);

        List<OctoProductAvailability> getOctoProductAvailability(Integer supplierId, OctoProductAvailabilityRequestDto availabilityRequest);

        List<BookingReserve> reserveBooking(Integer supplierId, BookingReserveRequest bookingReserveRequest);

        List<BookingConfirm> confirmBooking(Integer supplierId, String bookingId, BookingConfirmRequest bookingConfirmRequest);

        List<BookingConfirm> cancelBooking(Integer supplierId, String bookingId);
}
