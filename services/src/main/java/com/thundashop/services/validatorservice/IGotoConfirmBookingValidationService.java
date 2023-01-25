package com.thundashop.services.validatorservice;

import com.thundashop.core.gotohub.dto.GotoConfirmBookingRequest;
import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.zauiactivity.dto.ZauiActivityConfig;

public interface IGotoConfirmBookingValidationService {
    PmsBooking validateConfirmBookingReq(String reservationId, String paymentId, SessionInfo pmsManagerSession,
                                         ZauiActivityConfig activityConfig, GotoConfirmBookingRequest confirmBookingReq) throws GotoException;
}
