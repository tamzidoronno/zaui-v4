package com.thundashop.services.validatorservice;

import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.gotohub.dto.*;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.services.bookingitemtypeservice.IBookingItemTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import static com.thundashop.core.gotohub.constant.GoToStatusCodes.INCORRECT_DAILY_PRICE_MATRIX;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.INVALID_CHECKIN_CHECKOUT_FORMAT;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.INVALID_DATE_RANGE_BOOKING;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.INVALID_RATE_PLAN_CODE;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.NUMBER_OF_GUESTS_RATE_PLAN_CODE_MISMATCHED;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.OVERFLOW_MAX_NUMBER_OF_GUESTS;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.PRICE_MISSING;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.ROOM_TYPE_NOT_FOUND;
import static com.thundashop.core.gotohub.constant.GotoConstants.DAILY_PRICE_DATE_FORMATTER;
import static com.thundashop.core.gotohub.constant.GotoConstants.checkinOutDateFormatter;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

@Service
@Slf4j
public class GotoRoomValidationService implements IGotoRoomValidationService{
    @Autowired
    private IBookingItemTypeService bookingItemTypeService;
    @Override
    public void validateGotoRoomRequest(GotoRoomRequest room, SessionInfo bookingItemTypeSession, PmsConfiguration config)
            throws GotoException, ParseException {

        BookingItemType type = bookingItemTypeService.getBookingItemTypeById(room.getRoomCode(),
                bookingItemTypeSession);
        try {
            validateBookingItemType(type, room.getRoomCode());
            validateGuestNoAndRatePlan(
                    room.getAdults(), room.getChildrenAges().size(), room.getRatePlanCode(), type.name, type.size);
            validateCheckinOutDate(room.getCheckInDate(), room.getCheckOutDate(), config);
            validatePrice(room.getCheckInDate(), room.getCheckOutDate(), room.getPrice(), config);
        } catch (GotoException e) {
            e.setMessage(e.getMessage() + ", RoomCode: " + room.getRoomCode()
                    + ((type == null) ? "" : ", RoomType: " + type.name));
            throw e;
        }
    }

    private void validateBookingItemType(BookingItemType type, String roomCode) throws GotoException {
        if (type == null || type.deleted != null) {
            log.error("booking room type does not exist, BookingItemTypeId: " + roomCode);
            throw new GotoException(ROOM_TYPE_NOT_FOUND.code, ROOM_TYPE_NOT_FOUND.message);
        }
    }

    private void validateGuestNoAndRatePlan(
            Integer noOfAdults, Integer noOfChildren, String ratePlaneCode, String roomTypeName, Integer maxNoOfGuests)
            throws GotoException {
        Integer totalBookingGuest = noOfAdults + noOfChildren;
        if (isBlank(ratePlaneCode))
            throw new GotoException(INVALID_RATE_PLAN_CODE.code, INVALID_RATE_PLAN_CODE.message);

        Integer ratePlanNumberOfGuest = new Integer(substringAfterLast(ratePlaneCode, "-"));
        String typeNameFromRatePlan = substringBeforeLast(ratePlaneCode, "-");
        if (totalBookingGuest > maxNoOfGuests)
            throw new GotoException(OVERFLOW_MAX_NUMBER_OF_GUESTS.code, OVERFLOW_MAX_NUMBER_OF_GUESTS.message);
        if (isBlank(typeNameFromRatePlan) || !typeNameFromRatePlan.equals(roomTypeName)
                || ratePlanNumberOfGuest > maxNoOfGuests || ratePlanNumberOfGuest < 1)
            throw new GotoException(
                    INVALID_RATE_PLAN_CODE.code, INVALID_RATE_PLAN_CODE.message);
        if (!ratePlanNumberOfGuest.equals(totalBookingGuest))
            throw new GotoException(NUMBER_OF_GUESTS_RATE_PLAN_CODE_MISMATCHED.code,
                    NUMBER_OF_GUESTS_RATE_PLAN_CODE_MISMATCHED.message);
    }

    private void validateCheckinOutDate(String checkIn, String checkOut, PmsConfiguration config) throws GotoException {
        checkinOutDateFormatter.setLenient(false);
        Date checkInDate, checkOutDate;
        try {
            checkInDate = checkinOutDateFormatter.parse(checkIn);
            checkOutDate = checkinOutDateFormatter.parse(checkOut);
        } catch (ParseException e) {
            throw new GotoException(INVALID_CHECKIN_CHECKOUT_FORMAT.code, INVALID_CHECKIN_CHECKOUT_FORMAT.message);
        }
        checkInDate = config.getDefaultStart(checkInDate);
        checkOutDate = config.getDefaultEnd(checkOutDate);
        if (!checkInDate.before(checkOutDate))
            throw new GotoException(INVALID_DATE_RANGE_BOOKING.code, INVALID_DATE_RANGE_BOOKING.message);
    }

    private void validatePrice(String checkIn, String checkOut, GotoRoomPrice price, PmsConfiguration config)
            throws GotoException, ParseException {
        if (price == null || price.getDailyPrices() == null || price.getDailyPrices().isEmpty()
                || price.getTotalRoomPrice() == null) {
            throw new GotoException(PRICE_MISSING.code, PRICE_MISSING.message);
        }
        Date checkInDate = config.getDefaultStart(checkinOutDateFormatter.parse(checkIn));
        Date checkOutDate = config.getDefaultEnd(checkinOutDateFormatter.parse(checkOut));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(checkInDate);
        Map<String, Double> dailyPricesFromGoto;
        try {
            dailyPricesFromGoto = price.getDailyPrices()
                    .stream()
                    .collect(
                            Collectors.toMap(GotoRoomDailyPrice::getDate, GotoRoomDailyPrice::getPrice));
        } catch (Exception e) {
            throw new GotoException(INCORRECT_DAILY_PRICE_MATRIX.code, INCORRECT_DAILY_PRICE_MATRIX.message);
        }
        while (calendar.getTime().before(checkOutDate)) {
            String dailyPriceKey = DAILY_PRICE_DATE_FORMATTER.format(calendar.getTime());
            if (!dailyPricesFromGoto.containsKey(dailyPriceKey))
                throw new GotoException(PRICE_MISSING.code, PRICE_MISSING.message);
            Double roomPrice = dailyPricesFromGoto.get(dailyPriceKey);
            if (roomPrice < 0)
                throw new GotoException(INCORRECT_DAILY_PRICE_MATRIX.code, INCORRECT_DAILY_PRICE_MATRIX.message);
            calendar.add(Calendar.DATE, 1);
        }
    }
}
