package com.thundashop.services.validatorservice;

import com.thundashop.core.gotohub.dto.GotoBookingRequest;
import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.zauiactivity.dto.ZauiActivityConfig;

import java.text.ParseException;

public interface IGotoBookingRequestValidationService {

    void validateSaveBookingDto(GotoBookingRequest bookingRequest,
                                String systemCurrency,
                                PmsConfiguration configuration,
                                SessionInfo bookingItemTypeSession,
                                SessionInfo zauiActivitySessionInfo,
                                ZauiActivityConfig activityConfig) throws GotoException, ParseException;
}
