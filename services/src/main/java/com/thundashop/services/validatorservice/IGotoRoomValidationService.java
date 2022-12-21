package com.thundashop.services.validatorservice;

import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.core.gotohub.dto.GotoRoomRequest;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import com.thundashop.repository.utils.SessionInfo;

import java.text.ParseException;

public interface IGotoRoomValidationService {
    void validateGotoRoomRequest(GotoRoomRequest room, SessionInfo bookingItemTypeSession, PmsConfiguration config) throws GotoException, ParseException;
}
