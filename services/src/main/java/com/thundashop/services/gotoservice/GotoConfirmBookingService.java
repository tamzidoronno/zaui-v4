package com.thundashop.services.gotoservice;

import com.thundashop.core.gotohub.dto.GotoActivityConfirmationDto;
import com.thundashop.core.gotohub.dto.GotoConfirmBookingRequest;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.services.bookingservice.IPmsBookingService;
import com.thundashop.zauiactivity.dto.BookingZauiActivityItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GotoConfirmBookingService implements IGotoConfirmBookingService {
    @Autowired
    IPmsBookingService pmsBookingService;
    @Override
    public PmsBooking confirmGotoBooking(String bookingID, GotoConfirmBookingRequest confirmBookingReq, SessionInfo pmsManagerSession) {
        return updateItemOctoBookings(bookingID, confirmBookingReq, pmsManagerSession);
    }

    private PmsBooking updateItemOctoBookings(String bookingID, GotoConfirmBookingRequest confirmBookingReq, SessionInfo pmsManagerSession) {
        PmsBooking pmsBooking = pmsBookingService.getPmsBookingById(bookingID, pmsManagerSession);
        if(confirmBookingReq == null)
            return pmsBooking;
        Map<String, BookingZauiActivityItem> octoResIdToItemMap = pmsBooking.bookingZauiActivityItems.stream()
                .collect(Collectors.toMap(x-> x.getOctoBooking().getId(), x-> x));
        for(GotoActivityConfirmationDto activity : confirmBookingReq.getActivities()) {
            octoResIdToItemMap.get(activity.getOctoReservationId()).setOctoBooking(activity.getOctoConfirmationResponse());
        }
        return pmsBooking;
    }
}
