package com.thundashop.services.validatorservice;

import com.thundashop.core.gotohub.dto.GotoActivityReservationDto;
import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.repository.utils.SessionInfo;

public interface IZauiActivityValidationService {

    void validateGotoBookingActivity(GotoActivityReservationDto activity, SessionInfo zauiActivitySession, SessionInfo productSession) throws GotoException;
}
