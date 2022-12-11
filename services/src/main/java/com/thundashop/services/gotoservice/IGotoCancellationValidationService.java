package com.thundashop.services.gotoservice;

import com.thundashop.core.gotohub.dto.GoToConfiguration;
import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import com.thundashop.repository.utils.SessionInfo;

import java.util.Date;

public interface IGotoCancellationValidationService {

    void validateCancellationReq(String reservationId, Date deletionRequestTime, PmsConfiguration config,
                                 int cutoffHour, SessionInfo pmsManagerSession) throws GotoException, Exception;
}
