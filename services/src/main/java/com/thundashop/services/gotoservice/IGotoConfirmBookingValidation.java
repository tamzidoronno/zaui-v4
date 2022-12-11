package com.thundashop.services.gotoservice;

import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.repository.utils.SessionInfo;

public interface IGotoConfirmBookingValidation {
    PmsBooking validateConfirmBookingId(String reservationId, SessionInfo pmsManagerSession) throws GotoException;
}
