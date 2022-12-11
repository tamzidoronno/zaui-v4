package com.thundashop.services.gotoservice;

import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.services.bookingservice.IPmsBookingService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

import static com.thundashop.core.gotohub.constant.GoToStatusCodes.*;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.CANCELLATION_DEADLINE_PASSED;

@Service
public class GotoCancellationValidationService implements IGotoCancellationValidationService{
    @Autowired
    IPmsBookingService pmsBookingService;
    @Autowired
    IGotoBookingCancellationService cancellationService;
    @Override
    public PmsBooking validateCancellationReq(String reservationId, Date deletionRequestTime, PmsConfiguration config,
                                              int cutoffHour, SessionInfo pmsManagerSession) throws Exception {
        PmsBooking booking = pmsBookingService.getPmsBookingById(reservationId, pmsManagerSession);
        validateExistingBooking(booking);
        validateBookingCancellationDeadline(booking, deletionRequestTime, cutoffHour, config);
        return booking;
    }

    private void validateExistingBooking(PmsBooking booking) throws GotoException {
        if (booking == null) {
            throw new GotoException(BOOKING_CANCELLATION_NOT_FOUND.code,
                    BOOKING_CANCELLATION_NOT_FOUND.message);
        }
        if (booking.getActiveRooms().isEmpty())
            throw new GotoException(BOOKING_CANCELLATION_ALREADY_CANCELLED.code,
                    BOOKING_CANCELLATION_ALREADY_CANCELLED.message);
    }

    private void validateBookingCancellationDeadline(PmsBooking booking, Date delReqTime, int cutoffHour, PmsConfiguration config)
            throws Exception {
        // cancellation deadline would be applied only for paid bookings
        if(booking.orderIds == null || booking.orderIds.isEmpty())
            return;
        delReqTime = DateUtils.truncate(delReqTime, Calendar.MINUTE);
        for (PmsBookingRooms room : booking.rooms) {
            validateRoomCancellationDeadline(room, delReqTime, cutoffHour, config);
        }
    }

    private void validateRoomCancellationDeadline(PmsBookingRooms room, Date delReqTime, int cutoffHour, PmsConfiguration config)
            throws Exception {
        Date cancellationDeadLine = cancellationService.getCancellationDeadLine(room.date.start, cutoffHour, config);
        cancellationDeadLine = DateUtils.truncate(cancellationDeadLine, Calendar.HOUR);
        if (delReqTime.after(cancellationDeadLine)) {
            throw new GotoException(CANCELLATION_DEADLINE_PASSED.code, CANCELLATION_DEADLINE_PASSED.message);
        }
    }
}
