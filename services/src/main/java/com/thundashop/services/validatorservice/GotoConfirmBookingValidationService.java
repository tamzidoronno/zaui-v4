package com.thundashop.services.validatorservice;

import static com.thundashop.core.gotohub.constant.GoToStatusCodes.BOOKING_DELETED;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.BOOKING_NOT_FOUND;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.PAYMENT_METHOD_NOT_FOUND;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.OCTO_RESERVATION_ID_MISMATCHED;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.OCTO_RESERVATION_NOT_CONFIRMED;
import static com.thundashop.core.gotohub.constant.GotoConstants.GOTO_PAYMENT;
import static com.thundashop.core.gotohub.constant.GotoConstants.STAY_PAYMENT;
import static com.thundashop.zauiactivity.constant.ZauiConstants.OCTO_CONFIRMED_STATUS;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.thundashop.core.gotohub.dto.GotoActivityConfirmationDto;
import com.thundashop.core.gotohub.dto.GotoConfirmBookingRequest;
import com.thundashop.services.zauiactivityservice.IZauiActivityService;
import com.thundashop.zauiactivity.dto.BookingZauiActivityItem;
import org.apache.commons.collections4.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.services.bookingservice.IPmsBookingService;

import java.util.List;
import java.util.Set;
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
        validateOctoReservationIds(gotoConfirmBookingReq.getActivities(), booking.bookingZauiActivityItems);
        validateIfActivitiesConfirmed(gotoConfirmBookingReq.getActivities());
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

    private void validateOctoReservationIds(List<GotoActivityConfirmationDto> activitiesFromGoto,
                                            List<BookingZauiActivityItem> activityItems) throws GotoException {
        Set<String> existingOctoReservationIds = activityItems.stream()
                .map(activityItem -> activityItem.getOctoBooking().getId())
                .collect(Collectors.toSet());
        Set<String> octoReservationIdsFromReq = activitiesFromGoto.stream()
                .map(GotoActivityConfirmationDto::getOctoReservationId)
                .collect(Collectors.toSet());
        Set<String> missingIds = SetUtils.difference(existingOctoReservationIds, octoReservationIdsFromReq);
        Set<String> extraIds = SetUtils.difference(octoReservationIdsFromReq, existingOctoReservationIds);
        String missingIdsInString =  missingIds.size()>0 ?
                "Activities with following OctoReservationIds are missing: " + missingIds.stream()
                        .collect(Collectors.joining(", "))
                : "";
        String extraIdsInString =  extraIds.size()>0 ?
                "Activities with following OctoReservationIds don't exist in the reservation: " + extraIds.stream()
                        .collect(Collectors.joining(", "))
                : "";
        if(!existingOctoReservationIds.equals(octoReservationIdsFromReq))
            throw new GotoException(OCTO_RESERVATION_ID_MISMATCHED.code, OCTO_RESERVATION_ID_MISMATCHED.message +
                    "..\n " + missingIdsInString + ", " + extraIdsInString
                    );
    }

    private void validateIfActivitiesConfirmed(List<GotoActivityConfirmationDto> activities) throws GotoException {
        int size = activities.stream()
                .filter(activity-> !activity.getOctoConfirmationResponse().getStatus().equals(OCTO_CONFIRMED_STATUS))
                .collect(Collectors.toList()).size();
        if(size != 0)
            throw new GotoException(OCTO_RESERVATION_NOT_CONFIRMED.code, OCTO_RESERVATION_NOT_CONFIRMED.message);
    }

    private void validatePaymentMethod(String paymentMethodId, String requestedPaymentMethod) throws GotoException {
        if(isBlank(requestedPaymentMethod))
            requestedPaymentMethod = STAY_PAYMENT;

        if (isBlank(paymentMethodId) && requestedPaymentMethod.equals(GOTO_PAYMENT))
            throw new GotoException(PAYMENT_METHOD_NOT_FOUND.code, PAYMENT_METHOD_NOT_FOUND.message);
    }

}
