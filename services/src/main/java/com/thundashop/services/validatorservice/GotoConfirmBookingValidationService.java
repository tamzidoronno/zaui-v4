package com.thundashop.services.validatorservice;

import static com.thundashop.core.gotohub.constant.GoToStatusCodes.BOOKING_DELETED;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.BOOKING_NOT_FOUND;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.PAYMENT_METHOD_NOT_FOUND;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.thundashop.core.gotohub.dto.GotoConfirmBookingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.services.bookingservice.IPmsBookingService;

@Service
public class GotoConfirmBookingValidationService implements IGotoConfirmBookingValidationService {
    @Autowired
    IPmsBookingService pmsBookingService;

    @Override
    public PmsBooking validateConfirmBookingReq(String reservationId, String paymentId, SessionInfo pmsManagerSession,
                                                GotoConfirmBookingRequest gotoConfirmBookingReq) throws GotoException {
        PmsBooking booking = pmsBookingService.getPmsBookingById(reservationId, pmsManagerSession);
        validateBookingId(booking);
        validatePaymentMethod(paymentId);
        return booking;
    }

    private void validateBookingId(PmsBooking booking) throws GotoException {
        if (booking == null) {
            throw new GotoException(BOOKING_NOT_FOUND.code, BOOKING_NOT_FOUND.message);
        }
        if (booking.getActiveRooms().isEmpty())
            throw new GotoException(BOOKING_DELETED.code, BOOKING_DELETED.message);
    }

    private void validatePaymentMethod(String paymentMethodId) throws GotoException {
        if (isBlank(paymentMethodId))
            throw new GotoException(PAYMENT_METHOD_NOT_FOUND.code, PAYMENT_METHOD_NOT_FOUND.message);
    }

}
