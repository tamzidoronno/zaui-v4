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
    public PmsBooking confirmGotoBooking(PmsBooking pmsBooking, GotoConfirmBookingRequest confirmBookingReq, SessionInfo pmsManagerSession) {
        pmsBooking = updateItemOctoBookings(pmsBooking, confirmBookingReq, pmsManagerSession);
        pmsBooking.paymentMethodNameFromGoto = confirmBookingReq.getPaymentMethod();
        return pmsBooking;
    }

    private PmsBooking updateItemOctoBookings(PmsBooking pmsBooking, GotoConfirmBookingRequest confirmBookingReq, SessionInfo pmsManagerSession) {
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
