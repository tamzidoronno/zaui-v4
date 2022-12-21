package com.thundashop.services.gotoservice;

import com.thundashop.core.gotohub.dto.GotoConfirmBookingRequest;
import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.repository.utils.SessionInfo;

public interface IGotoConfirmBookingValidationService {
    PmsBooking validateConfirmBookingReq(String reservationId, String paymentId, SessionInfo pmsManagerSession
            , GotoConfirmBookingRequest confirmBookingReq) throws GotoException;
}
