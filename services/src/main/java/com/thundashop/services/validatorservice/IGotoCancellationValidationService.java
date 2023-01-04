package com.thundashop.services.validatorservice;

import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import com.thundashop.repository.utils.SessionInfo;

import java.util.Date;

public interface IGotoCancellationValidationService {

    PmsBooking validateCancellationReq(String reservationId, Date deletionRequestTime, PmsConfiguration config,
                                       int cutoffHour, SessionInfo pmsManagerSession) throws Exception;
}
