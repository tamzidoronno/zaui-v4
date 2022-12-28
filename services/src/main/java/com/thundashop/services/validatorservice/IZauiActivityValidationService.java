package com.thundashop.services.validatorservice;

import com.thundashop.core.gotohub.dto.GotoActivityReservationDto;
import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.repository.utils.SessionInfo;

import java.util.List;

public interface IZauiActivityValidationService {

    void validateGotoBookingActivity(GotoActivityReservationDto activity, SessionInfo zauiActivitySession, SessionInfo productSession
            , String systemCurrency) throws GotoException;

    void validateTaxRates(List<Double> taxRatesFromOctoBooking, SessionInfo productSessionInfo) throws GotoException;
}
