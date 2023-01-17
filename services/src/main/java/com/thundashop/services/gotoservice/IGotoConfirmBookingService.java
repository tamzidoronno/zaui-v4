package com.thundashop.services.gotoservice;

import com.thundashop.core.gotohub.dto.GotoConfirmBookingRequest;
import com.thundashop.core.pmsmanager.PmsBooking;

public interface IGotoConfirmBookingService {
    PmsBooking confirmGotoBooking(PmsBooking pmsBooking, GotoConfirmBookingRequest confirmBookingReq);
    GotoConfirmBookingRequest updateConfirmRequest(GotoConfirmBookingRequest confirmBookingRequest);
}
