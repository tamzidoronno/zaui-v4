package com.thundashop.services.gotoservice;

import com.thundashop.core.gotohub.dto.GotoActivityConfirmationDto;
import com.thundashop.core.gotohub.dto.GotoConfirmBookingRequest;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.services.bookingservice.IPmsBookingService;
import com.thundashop.zauiactivity.dto.BookingZauiActivityItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

import static com.thundashop.core.gotohub.constant.GotoConstants.GOTO_PAYMENT;
import static com.thundashop.core.gotohub.constant.GotoConstants.STAY_PAYMENT;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class GotoConfirmBookingService implements IGotoConfirmBookingService {
    @Autowired
    IPmsBookingService pmsBookingService;
    @Override
    public PmsBooking confirmGotoBooking(PmsBooking pmsBooking, GotoConfirmBookingRequest confirmBookingReq) {
        pmsBooking = updateItemOctoBookings(pmsBooking, confirmBookingReq);
        pmsBooking.paymentMethodNameFromGoto = confirmBookingReq.getPaymentMethod();
        return pmsBooking;
    }
    @Override
    public GotoConfirmBookingRequest updatePaymentMethod(GotoConfirmBookingRequest confirmBookingRequest) {
        if (confirmBookingRequest == null) {
            confirmBookingRequest = new GotoConfirmBookingRequest();
        }
        if((confirmBookingRequest.getActivities() == null || confirmBookingRequest.getActivities().isEmpty())
                && isBlank(confirmBookingRequest.getPaymentMethod())) {
            confirmBookingRequest.setPaymentMethod(GOTO_PAYMENT);
        }
        else if(confirmBookingRequest.getActivities() != null
                && !confirmBookingRequest.getActivities().isEmpty()
                && isBlank(confirmBookingRequest.getPaymentMethod())
        ) {
            confirmBookingRequest.setPaymentMethod(STAY_PAYMENT);
        }
        return confirmBookingRequest;
    }

    private PmsBooking updateItemOctoBookings(PmsBooking pmsBooking, GotoConfirmBookingRequest confirmBookingReq) {
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
