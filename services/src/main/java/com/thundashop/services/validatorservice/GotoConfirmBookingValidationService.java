package com.thundashop.services.validatorservice;

import static com.thundashop.core.gotohub.constant.GoToStatusCodes.*;
import static com.thundashop.core.gotohub.constant.GotoConstants.GOTO_PAYMENT;
import static com.thundashop.core.gotohub.constant.GotoConstants.STAY_PAYMENT;
import static com.thundashop.zauiactivity.constant.ZauiConstants.OCTO_CONFIRMED_STATUS;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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
        if(gotoConfirmBookingReq == null) return booking;
        validateIsActivityMisMatchedWithHoldBooking(gotoConfirmBookingReq.getActivities(), booking.bookingZauiActivityItems);
        validateActivities(gotoConfirmBookingReq.getActivities());
        validateOctoReservationIds(gotoConfirmBookingReq.getActivities(), booking.bookingZauiActivityItems);
        validateIfActivitiesConfirmed(gotoConfirmBookingReq.getActivities());
        validatePaymentMethod(paymentId, gotoConfirmBookingReq.getPaymentMethod(), gotoConfirmBookingReq.getActivities());
        return booking;
    }

    private void validateBookingId(PmsBooking booking) throws GotoException {
        if (booking == null) {
            throw new GotoException(BOOKING_NOT_FOUND.code, BOOKING_NOT_FOUND.message);
        }
        if (booking.getActiveRooms().isEmpty() && zauiActivityService.isAllActivityCancelled(booking.bookingZauiActivityItems))
            throw new GotoException(BOOKING_DELETED.code, BOOKING_DELETED.message);
    }

    private void validateIsActivityMisMatchedWithHoldBooking(List<GotoActivityConfirmationDto> activitiesFromGoto,
                                                             List<BookingZauiActivityItem> holdBookingActivityItems) throws GotoException {
        if(!isHoldBookingHasActivity(holdBookingActivityItems) && isConfirmBookingReqHasActivity(activitiesFromGoto))
            throw new GotoException(CONFIRMATION_HOLD_BOOKING_DOESNT_HAVE_ACTIVITY);
        if(isHoldBookingHasActivity(holdBookingActivityItems) && !isConfirmBookingReqHasActivity(activitiesFromGoto))
            throw new GotoException(CONFIRMATION_REQUEST_ACTIVITY_MISSING);
    }

    private void validateOctoReservationIds(List<GotoActivityConfirmationDto> activitiesFromGoto,
                                            List<BookingZauiActivityItem> activityItems) throws GotoException {
        if((activityItems == null || activityItems.isEmpty()) && (activitiesFromGoto == null || activitiesFromGoto.isEmpty()))
            return;
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
        if(activities.stream().anyMatch(activity -> !activity.getOctoConfirmationResponse().getStatus().equals(OCTO_CONFIRMED_STATUS)))
            throw new GotoException(OCTO_RESERVATION_NOT_CONFIRMED.code, OCTO_RESERVATION_NOT_CONFIRMED.message);
    }

    private void validatePaymentMethod(String paymentMethodId, String requestedPaymentMethod,
                                       List<GotoActivityConfirmationDto> activities) throws GotoException {
        if(isNotBlank(requestedPaymentMethod) && requestedPaymentMethod.equals(GOTO_PAYMENT)) {
            if(activities != null && !activities.isEmpty()) throw new GotoException(ACTIVITY_GOTO_PAYMENT_METHOD);
            if(isBlank(paymentMethodId)) throw new GotoException(PAYMENT_METHOD_NOT_FOUND);
        }
        if(isNotBlank(requestedPaymentMethod) && requestedPaymentMethod.equals(STAY_PAYMENT))
            throw new GotoException(WITHOUT_ACTIVITY_STAY_PAYMENT_METHOD);
    }

    private void validateActivities(List<GotoActivityConfirmationDto> activities) throws GotoException {
        for (GotoActivityConfirmationDto activity : activities) {
            try {
                validateActivity(activity);
            } catch (GotoException e) {
                if (activity.getOctoConfirmationResponse() != null
                        && activity.getOctoConfirmationResponse().getOptionId() != null)
                    e.setMessage(e.getMessage() + ", OptionId: " + activity.getOctoConfirmationResponse().getOptionId());
                throw e;
            }
        }
    }

    private void validateActivity(GotoActivityConfirmationDto activity) throws GotoException {
        if(activity.getOctoConfirmationResponse() == null) {
            throw new GotoException(CONFIRMATION_RESPONSE_MISSING);
        }
        if(activity.getOctoConfirmationResponse().getPricing() == null) {
            throw new GotoException(CONFIRMATION_PRICING_MISSING);
        }
        if(activity.getOctoConfirmationResponse().getPricing().getIncludedTaxes() == null) {
            throw new GotoException(CONFIRMATION_INCLUDED_TAX_RATE_MISSING);
        }
    }
    private boolean isHoldBookingHasActivity(List<BookingZauiActivityItem> activityItems) {
        return activityItems != null && !activityItems.isEmpty();
    }
    private boolean isConfirmBookingReqHasActivity (List<GotoActivityConfirmationDto> activitiesFromGoto) {
        return activitiesFromGoto!= null && !activitiesFromGoto.isEmpty();
    }
}
