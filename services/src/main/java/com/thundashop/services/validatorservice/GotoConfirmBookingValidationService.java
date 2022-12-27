package com.thundashop.services.validatorservice;

import static com.thundashop.core.gotohub.constant.GoToStatusCodes.BOOKING_DELETED;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.BOOKING_NOT_FOUND;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.PAYMENT_METHOD_NOT_FOUND;
import static com.thundashop.core.gotohub.constant.GotoConstants.GOTO_PAYMENT;
import static com.thundashop.core.gotohub.constant.GotoConstants.STAY_PAYMENT;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.thundashop.core.gotohub.dto.GotoConfirmBookingRequest;
import com.thundashop.services.zauiactivityservice.IZauiActivityService;
import com.thundashop.zauiactivity.constant.ZauiConstants;
import com.thundashop.zauiactivity.dto.BookingZauiActivityItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.services.bookingservice.IPmsBookingService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GotoConfirmBookingValidationService implements IGotoConfirmBookingValidationService {
    @Autowired
    IPmsBookingService pmsBookingService;
    @Autowired
    IZauiActivityService zauiActivityService;

    @Override
    public PmsBooking validateConfirmBookingReq(String reservationId, String paymentId, SessionInfo pmsManagerSession,
                                                GotoConfirmBookingRequest gotoConfirmBookingReq) throws GotoException {
        PmsBooking booking = pmsBookingService.getPmsBookingById(reservationId, pmsManagerSession);
        validateBookingId(booking);
        String requestedPaymentMethod = gotoConfirmBookingReq == null ? STAY_PAYMENT : gotoConfirmBookingReq.getPaymentMethod();
        validatePaymentMethod(paymentId, requestedPaymentMethod);
        return booking;
    }

    private void validateBookingId(PmsBooking booking) throws GotoException {
        if (booking == null) {
            throw new GotoException(BOOKING_NOT_FOUND.code, BOOKING_NOT_FOUND.message);
        }
        if (booking.getActiveRooms().isEmpty() && zauiActivityService.isAllActivityCancelled(booking.bookingZauiActivityItems))
            throw new GotoException(BOOKING_DELETED.code, BOOKING_DELETED.message);
    }

    private void validatePaymentMethod(String paymentMethodId, String requestedPaymentMethod) throws GotoException {
        if(isBlank(requestedPaymentMethod))
            requestedPaymentMethod = STAY_PAYMENT;

        if (isBlank(paymentMethodId) && requestedPaymentMethod.equals(GOTO_PAYMENT))
            throw new GotoException(PAYMENT_METHOD_NOT_FOUND.code, PAYMENT_METHOD_NOT_FOUND.message);
    }

}
