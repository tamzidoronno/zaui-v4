package com.thundashop.services.validatorservice;

import com.thundashop.core.gotohub.dto.GotoActivityReservationDto;
import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.repository.exceptions.NotUniqueDataException;
import com.thundashop.repository.utils.SessionInfo;

import java.util.List;

public interface IZauiActivityValidationService {

    void validateGotoBookingActivity(GotoActivityReservationDto activity, SessionInfo zauiActivitySession,
            String systemCurrency) throws GotoException;

    void validateTaxRates(int supplierId, List<Double> taxRatesFromOctoBooking, SessionInfo zauiActivitySessionInfo)
            throws GotoException;
}
