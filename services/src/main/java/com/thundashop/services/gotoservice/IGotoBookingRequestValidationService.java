package com.thundashop.services.gotoservice;

import com.thundashop.core.gotohub.dto.GotoBookingRequest;
import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import com.thundashop.repository.utils.SessionInfo;

import java.text.ParseException;

public interface IGotoBookingRequestValidationService {

    void validateSaveBookingDto(GotoBookingRequest bookingRequest,
                                String systemCurrency,
                                PmsConfiguration configuration,
                                SessionInfo bookingItemTypeSession,
                                SessionInfo zauiActivitySessionInfo) throws GotoException, ParseException;
}
