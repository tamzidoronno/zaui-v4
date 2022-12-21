package com.thundashop.services.validatorservice;

import static com.thundashop.core.gotohub.constant.GoToStatusCodes.DIFFERENT_CURRENCY;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.EMAIL_OR_PHONE_MISSING;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.EMPTY_BOOKING_ITEM;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.text.ParseException;
import java.util.List;

import com.thundashop.core.gotohub.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thundashop.core.pmsmanager.PmsConfiguration;
import com.thundashop.repository.utils.SessionInfo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GotoBookingRequestValidationService implements IGotoBookingRequestValidationService {
    @Autowired
    private IGotoRoomValidationService roomValidationService;
    @Autowired
    private IZauiActivityValidationService zauiActivityValidationService;

    @Override
    public void validateSaveBookingDto(GotoBookingRequest bookingRequest,
                                       String systemCurrency,
                                       PmsConfiguration configuration,
                                       SessionInfo bookingItemTypeSession,
                                       SessionInfo zauiActivitySessionInfo,
                                       SessionInfo productSessionInfo) throws GotoException, ParseException {
        validateCurrency(bookingRequest.getCurrency(), systemCurrency);
        validateBookerInfo(bookingRequest.getOrderer());
        validateNoOfBookingItems(bookingRequest.getRooms(), bookingRequest.getActivities());
        validateRooms(bookingRequest.getRooms(), bookingItemTypeSession, configuration);
        validateActivities(bookingRequest.getActivities(), zauiActivitySessionInfo, productSessionInfo);
    }

    private void validateRooms(List<GotoRoomRequest> bookingRooms, SessionInfo bookingItemTypeSession, PmsConfiguration config)
            throws ParseException, GotoException {

        for (GotoRoomRequest room : bookingRooms) {
            roomValidationService.validateGotoRoomRequest(room, bookingItemTypeSession, config);
        }

    }

    private void validateNoOfBookingItems(List<GotoRoomRequest> rooms, List<GotoActivityReservationDto> activities)
            throws GotoException {
        if ((rooms == null || rooms.isEmpty()) && (activities == null || activities.isEmpty())) {
            log.debug("Booking is not saved since there are no rooms to add");
            throw new GotoException(EMPTY_BOOKING_ITEM.code, EMPTY_BOOKING_ITEM.message);
        }
    }

    private void validateBookerInfo(GotoBooker booker) throws GotoException {
        if (isBlank(booker.getEmail()) && (booker.getMobile() == null || isBlank(booker.getMobile().getAreaCode())
                || isBlank(booker.getMobile().getPhoneNumber())))
            throw new GotoException(EMAIL_OR_PHONE_MISSING.code, EMAIL_OR_PHONE_MISSING.message);
    }

    private void validateCurrency(String bookingCurrency, String systemCurrency) throws GotoException {
        if (isNotBlank(bookingCurrency) && bookingCurrency.equals(systemCurrency))
            return;
        log.error("Booking currency didn't match with system currency..");
        log.error("Booking currency: " + bookingCurrency);
        throw new GotoException(DIFFERENT_CURRENCY.code, DIFFERENT_CURRENCY.message);
    }

    private void validateActivities(List<GotoActivityReservationDto> activities, SessionInfo activitySession, SessionInfo productSession) throws GotoException {
        for(GotoActivityReservationDto activity : activities) {
            try {
                zauiActivityValidationService.validateGotoBookingActivity(activity, activitySession, productSession);
            } catch (GotoException e) {
                if(activity.getOctoReservationResponse() != null && activity.getOctoReservationResponse().getOptionId() != null)
                    e.setMessage(e.getMessage() + ", OptionId: " + activity.getOctoReservationResponse().getOptionId());
                throw e;
            }
        }
    }
}
