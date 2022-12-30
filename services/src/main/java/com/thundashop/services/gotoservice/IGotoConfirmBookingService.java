package com.thundashop.services.gotoservice;

import com.thundashop.core.gotohub.dto.GotoConfirmBookingRequest;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.repository.utils.SessionInfo;

public interface IGotoConfirmBookingService {
    PmsBooking confirmGotoBooking(PmsBooking pmsBooking, GotoConfirmBookingRequest confirmBookingReq, SessionInfo pmsManagerSession);
}
