package com.thundashop.services.gotoservice;

import com.thundashop.core.gotohub.dto.GotoActivityConfirmationDto;
import com.thundashop.core.gotohub.dto.GotoConfirmBookingRequest;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.services.bookingservice.IPmsBookingService;
import com.thundashop.zauiactivity.dto.BookingZauiActivityItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GotoConfirmBookingService implements IGotoConfirmBookingService {
    @Autowired
    IPmsBookingService pmsBookingService;
    @Override
    public PmsBooking confirmGotoBooking(PmsBooking pmsBooking, GotoConfirmBookingRequest confirmBookingReq) {
        pmsBooking = updateItemOctoBookings(pmsBooking, confirmBookingReq);
        return pmsBooking;
    }
    @Override
    public GotoConfirmBookingRequest updateConfirmRequest(GotoConfirmBookingRequest confirmBookingRequest) {
        if (confirmBookingRequest == null) {
            confirmBookingRequest = new GotoConfirmBookingRequest();
        }
        if(confirmBookingRequest.getActivities() == null) confirmBookingRequest.setActivities(new ArrayList<>());
        if(confirmBookingRequest.getPaymentMethod() == null) {
            confirmBookingRequest.setPaymentMethod("");
        }
        return confirmBookingRequest;
    }

    private PmsBooking updateItemOctoBookings(PmsBooking pmsBooking, GotoConfirmBookingRequest confirmBookingReq) {
        if(pmsBooking.bookingZauiActivityItems == null || pmsBooking.bookingZauiActivityItems.isEmpty())
            return pmsBooking;
        Map<String, BookingZauiActivityItem> octoResIdToItemMap = pmsBooking.bookingZauiActivityItems.stream()
                .collect(Collectors.toMap(x-> x.getOctoBooking().getId(), x-> x));
        for(GotoActivityConfirmationDto activity : confirmBookingReq.getActivities()) {
            octoResIdToItemMap.get(activity.getOctoReservationId()).setOctoBooking(activity.getOctoConfirmationResponse());
        }
        return pmsBooking;
    }
}
