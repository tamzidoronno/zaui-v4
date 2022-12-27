package com.thundashop.services.validatorservice;

import com.thundashop.core.gotohub.dto.GotoActivityReservationDto;
import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.services.productservice.IProductService;
import com.thundashop.services.zauiactivityservice.IZauiActivityService;
import com.thundashop.zauiactivity.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.thundashop.core.gotohub.constant.GoToStatusCodes.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class ZauiActivityValidationService implements IZauiActivityValidationService {
    @Autowired
    IZauiActivityService zauiActivityService;
    @Autowired
    IProductService productService;

    @Override
    public void validateGotoBookingActivity(GotoActivityReservationDto activity, SessionInfo zauiActivitySession, SessionInfo productSession) throws GotoException {
        validateActivityObject(activity.getOctoReservationResponse());
        validateOptionId(activity.getOctoReservationResponse().getOptionId());
        validateZauiActivity(activity.getOctoReservationResponse().getOptionId(), zauiActivitySession);
        validateAvailabilityId(activity.getOctoReservationResponse().getAvailabilityId());
        validateAvailability(activity.getOctoReservationResponse().getAvailability());
        validateUnitItems(activity.getOctoReservationResponse().getUnitItems());
        validatePricing(activity.getOctoReservationResponse().getPricing(), productSession);
    }

    private void validateActivityObject(OctoBooking octoReservationResponse) throws GotoException {
        if(octoReservationResponse == null)
            throw new GotoException(OCTO_RESERVATION_RESPONSE_MISSING.code, OCTO_RESERVATION_RESPONSE_MISSING.message);
    }

    private void validateOptionId(String optionId) throws GotoException {
        if(isBlank(optionId))
            throw new GotoException(ACTIVITY_OPTION_ID_MISSING.code, ACTIVITY_OPTION_ID_MISSING.message);
    }

    private void validateZauiActivity(String optionId, SessionInfo sessionInfo) throws GotoException {
        ZauiActivity zauiActivity = zauiActivityService.getZauiActivityByOptionId(optionId, sessionInfo);
        if(zauiActivity == null)
            throw new GotoException(ZAUI_ACTIVITY_NOT_AVAILABLE.code, ZAUI_ACTIVITY_NOT_AVAILABLE.message);
    }

    private void validateAvailabilityId(String availabilityId) throws GotoException {
        if(isBlank(availabilityId))
            throw new GotoException(ACTIVITY_AVAILABILITY_ID_MISSING.code, ACTIVITY_AVAILABILITY_ID_MISSING.message);
    }

    private void validateAvailability(OctoProductAvailability availability) throws GotoException {
        if(availability == null)
            throw new GotoException(ACTIVITY_AVAILABILITY_MISSING.code, ACTIVITY_AVAILABILITY_MISSING.message);
        if(isBlank(availability.getLocalDateTimeStart()))
            throw new GotoException(ACTIVITY_START_TIME_MISSING.code, ACTIVITY_START_TIME_MISSING.message);
        if(isBlank(availability.getLocalDateTimeEnd()))
            throw new GotoException(ACTIVITY_END_TIME_MISSING.code, ACTIVITY_END_TIME_MISSING.message);
    }

    private void validateUnitItems(List<UnitItemOnBooking> unitItems) throws GotoException {
        if(unitItems == null || unitItems.isEmpty())
            throw new GotoException(ACTIVITY_UNIT_ITEM_INCORRECT.code, ACTIVITY_UNIT_ITEM_INCORRECT.message);
    }

    private void validatePricing(Pricing pricing, SessionInfo productSessionInfo) throws GotoException {
        if(pricing == null)
            throw new GotoException(ACTIVITY_PRICING_MISSING.code, ACTIVITY_PRICING_MISSING.message);
        List<Double> taxRate = pricing.getIncludedTaxes().stream()
                .map(taxData -> new Double(taxData.getRate())).collect(Collectors.toList());
        validateTaxRates(taxRate, productSessionInfo);
    }

    @Override
    public void validateTaxRates(List<Double> taxRatesFromOctoBooking, SessionInfo productSessionInfo) throws GotoException {
        Set<Double> taxRateFromSystem = productService
                .getAllTaxGroups(productSessionInfo).stream()
                .map(taxGroup -> taxGroup.taxRate).collect(Collectors.toSet());
        List<String> invalidTaxRates = new ArrayList<>();
        for(Double rateFromBooking : taxRatesFromOctoBooking) {
            if(!taxRateFromSystem.contains(rateFromBooking))
                invalidTaxRates.add(String.valueOf(rateFromBooking));
        }
        if(!invalidTaxRates.isEmpty())
            throw new GotoException(ACTIVITY_PRICING_INVALID_TAX_RATE.code, ACTIVITY_PRICING_INVALID_TAX_RATE.message
                    + ", invalid rates: {" + String.join(", ", invalidTaxRates) + "}");
    }
}
