package com.thundashop.services.validatorservice;

import com.thundashop.core.gotohub.dto.GotoActivityReservationDto;
import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.repository.utils.SessionInfo;

import java.util.List;

public interface IZauiActivityValidationService {

    void validateGotoBookingActivity(GotoActivityReservationDto activity, SessionInfo zauiActivitySession, SessionInfo productSession) throws GotoException;

    void validateTaxRates(List<Double> taxRate, SessionInfo productSessionInfo) throws GotoException;
}
