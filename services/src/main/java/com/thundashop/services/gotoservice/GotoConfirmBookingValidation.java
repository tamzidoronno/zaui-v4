package com.thundashop.services.gotoservice;

import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.services.bookingservice.IPmsBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.thundashop.core.gotohub.constant.GoToStatusCodes.BOOKING_DELETED;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.BOOKING_NOT_FOUND;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.PAYMENT_METHOD_NOT_FOUND;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class GotoConfirmBookingValidation implements IGotoConfirmBookingValidation{
    @Autowired
    IPmsBookingService pmsBookingService;
    @Override
    public PmsBooking validateConfirmBookingReq(String reservationId, String paymentId, SessionInfo pmsManagerSession) throws GotoException {
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
