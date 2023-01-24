package com.thundashop.services.validatorservice;

import com.google.common.base.Throwables;
import com.thundashop.core.gotohub.dto.GotoActivityReservationDto;
import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.core.pmsmanager.TimeRepeaterData;
import com.thundashop.repository.exceptions.NotUniqueDataException;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.services.zauiactivityservice.IZauiActivityService;
import com.thundashop.zauiactivity.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.thundashop.core.gotohub.constant.GoToStatusCodes.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@Slf4j
public class ZauiActivityValidationService implements IZauiActivityValidationService {
    @Autowired
    IZauiActivityService zauiActivityService;

    @Override
    public void validateGotoBookingActivity(GotoActivityReservationDto activity, SessionInfo zauiActivitySession,
            String systemCurrency, List<TimeRepeaterData> hotelClosure) throws GotoException {
        validateActivityHotelClosedRestriction(activity, hotelClosure);
        validateActivityObject(activity.getOctoReservationResponse());
        validateOptionId(activity.getOctoReservationResponse().getOptionId());
        validateSupplier(activity.getSupplierId(), zauiActivitySession);
        validateZauiActivity(activity.getOctoReservationResponse().getOptionId(), zauiActivitySession);
        validateAvailabilityId(activity.getOctoReservationResponse().getAvailabilityId());
        validateAvailability(activity.getOctoReservationResponse().getAvailability());
        validateUnitItems(activity.getOctoReservationResponse().getUnitItems());
        validatePricing(
                activity.getSupplierId(), activity.getOctoReservationResponse().getPricing(), zauiActivitySession,
                systemCurrency);
    }

    private void validateActivityHotelClosedRestriction(GotoActivityReservationDto activity, List<TimeRepeaterData> hotelClosure)
            throws GotoException{
        for(TimeRepeaterData period: hotelClosure) {
            if(period.firstEvent!= null && period.firstEvent.start != null && period.endingAt != null
                    && isActivityInBetweenClosedPeriod(activity, period))
                throw new GotoException(ACTIVITY_CONFLICT_WITH_HOTEL_CLOSURE);
        }
    }
    private boolean isActivityInBetweenClosedPeriod(GotoActivityReservationDto activity, TimeRepeaterData closedPeriod) {
        Date activityStartDate = new DateTime(activity.getOctoReservationResponse().getAvailability().getLocalDateTimeStart()).toDate();
        Date activityEndDate = new DateTime(activity.getOctoReservationResponse().getAvailability().getLocalDateTimeEnd()).toDate();
        return !activityEndDate.before(closedPeriod.firstEvent.start) && !activityStartDate.after(closedPeriod.endingAt);
    }

    private void validateActivityObject(OctoBooking octoReservationResponse) throws GotoException {
        if (octoReservationResponse == null)
            throw new GotoException(OCTO_RESERVATION_RESPONSE_MISSING.code, OCTO_RESERVATION_RESPONSE_MISSING.message);
    }

    private void validateOptionId(String optionId) throws GotoException {
        if (isBlank(optionId))
            throw new GotoException(ACTIVITY_OPTION_ID_MISSING.code, ACTIVITY_OPTION_ID_MISSING.message);
    }

    private void validateSupplier(Integer supplierId, SessionInfo zauiActivitySession) throws GotoException {
        if (supplierId == null)
            throw new GotoException(ACTIVITY_SUPPLIER_ID_INVALID.code, ACTIVITY_SUPPLIER_ID_INVALID.message);
        ZauiConnectedSupplier supplier;
        try {
            supplier = zauiActivityService.getZauiActivityConfig(zauiActivitySession).getConnectedSuppliers()
                    .stream()
                    .filter(s -> s.getId() == supplierId)
                    .findFirst().orElse(null);

        } catch (NotUniqueDataException e) {
            log.error(Throwables.getStackTraceAsString(e));
            throw new GotoException(ZAUI_ACTIVITY_CONFIG_FETCH_FAILED.code, ZAUI_ACTIVITY_CONFIG_FETCH_FAILED.message);
        }
        if (supplier == null)
            throw new GotoException(ACTIVITY_SUPPLIER_ID_NOT_CONNECTED.code,
                    ACTIVITY_SUPPLIER_ID_NOT_CONNECTED.message);

    }

    private void validateZauiActivity(String optionId, SessionInfo sessionInfo) throws GotoException {
        ZauiActivity zauiActivity = zauiActivityService.getZauiActivityByOptionId(optionId, sessionInfo);
        if (zauiActivity == null)
            throw new GotoException(ZAUI_ACTIVITY_NOT_AVAILABLE.code, ZAUI_ACTIVITY_NOT_AVAILABLE.message);
    }

    private void validateAvailabilityId(String availabilityId) throws GotoException {
        if (isBlank(availabilityId))
            throw new GotoException(ACTIVITY_AVAILABILITY_ID_MISSING.code, ACTIVITY_AVAILABILITY_ID_MISSING.message);
    }

    private void validateAvailability(OctoProductAvailability availability) throws GotoException {
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        currentDate = cal.getTime();
        if (availability == null)
            throw new GotoException(ACTIVITY_AVAILABILITY_MISSING.code, ACTIVITY_AVAILABILITY_MISSING.message);
        if (isBlank(availability.getLocalDateTimeStart()))
            throw new GotoException(ACTIVITY_START_TIME_MISSING.code, ACTIVITY_START_TIME_MISSING.message);
        if (isBlank(availability.getLocalDateTimeEnd()))
            throw new GotoException(ACTIVITY_END_TIME_MISSING.code, ACTIVITY_END_TIME_MISSING.message);
        if(new DateTime(availability.getLocalDateTimeStart()).toDate().before(currentDate)) {
            throw new GotoException(ACTIVITY_BEFORE_CURRENT_DATE);
        }

    }

    private void validateUnitItems(List<UnitItemOnBooking> unitItems) throws GotoException {
        if (unitItems == null || unitItems.isEmpty())
            throw new GotoException(ACTIVITY_UNIT_ITEM_INCORRECT.code, ACTIVITY_UNIT_ITEM_INCORRECT.message);

    }

    private void validatePricing(int supplierId, Pricing pricing, SessionInfo productSessionInfo, String systemCurrency)
            throws GotoException {
        if (pricing == null)
            throw new GotoException(ACTIVITY_PRICING_MISSING.code, ACTIVITY_PRICING_MISSING.message);
        if (isBlank(pricing.getCurrency()) || !pricing.getCurrency().equals(systemCurrency)) {
            log.error("Activity currency didn't match with system currency..");
            log.error("Activity currency: {}", pricing.getCurrency() != null ? pricing.getCurrency() : "");
            throw new GotoException(DIFFERENT_CURRENCY.code, DIFFERENT_CURRENCY.message);
        }
        if(pricing.getIncludedTaxes() == null || pricing.getIncludedTaxes().isEmpty()) {
            throw new GotoException(RESERVATION_INCLUDED_TAX_RATE_MISSING);
        }
        List<Double> taxRate = pricing.getIncludedTaxes().stream()
                .map(taxData -> new Double(taxData.getRate())).collect(Collectors.toList());
        validateTaxRates(supplierId, taxRate, productSessionInfo);
    }

    @Override
    public void validateTaxRates(int supplierId, List<Double> taxRatesFromOctoBooking,
            SessionInfo zauiActivitySessionInfo) throws GotoException {
        Map<Double, String> taxRateFromActivityConfig;
        try {
            ZauiConnectedSupplier supplier = zauiActivityService
                    .getZauiActivityConfig(zauiActivitySessionInfo).getConnectedSuppliers()
                    .stream()
                    .filter(s -> s.getId() == supplierId)
                    .findFirst().get();

            taxRateFromActivityConfig = supplier.getTaxRateMapping().stream()
                    .collect(Collectors.toMap(TaxRateMap::getTaxRate, TaxRateMap::getAccountNo));
        } catch (NotUniqueDataException e) {
            log.error(Throwables.getStackTraceAsString(e));
            throw new GotoException(ZAUI_ACTIVITY_CONFIG_FETCH_FAILED.code, ZAUI_ACTIVITY_CONFIG_FETCH_FAILED.message);
        }
        List<String> invalidTaxRates = new ArrayList<>();
        for (Double rateFromBooking : taxRatesFromOctoBooking) {
            if (!taxRateFromActivityConfig.containsKey(rateFromBooking)
                    || isBlank(taxRateFromActivityConfig.get(rateFromBooking)))
                invalidTaxRates.add(String.valueOf(rateFromBooking));
        }
        if (!invalidTaxRates.isEmpty())
            throw new GotoException(ACTIVITY_PRICING_INVALID_TAX_RATE.code, ACTIVITY_PRICING_INVALID_TAX_RATE.message
                    + ", invalid rates: {" + String.join(", ", invalidTaxRates) + "}");
    }
}
