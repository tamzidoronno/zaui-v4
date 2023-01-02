package com.thundashop.services.gotoservice;

import com.thundashop.core.gotohub.constant.GotoConstants;
import com.thundashop.core.gotohub.dto.*;
import com.thundashop.core.pmsmanager.*;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.services.zauiactivityservice.IZauiActivityService;
import com.thundashop.zauiactivity.dto.BookingZauiActivityItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import static com.thundashop.core.gotohub.constant.GotoConstants.DAILY_PRICE_DATE_FORMATTER;
import static com.thundashop.core.gotohub.constant.GotoConstants.checkinOutDateFormatter;
import static com.thundashop.core.gotohub.constant.GotoConstants.BOOKING_ITEM_TYPE_ID_FOR_VIRTUAL_GOTO_ROOM;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class GotoHoldBookingService implements IGotoHoldBookingService {
    @Autowired
    IZauiActivityService zauiActivityService;
    @Autowired
    IGotoBookingCancellationService bookingCancellationService;

    @Override
    public PmsBooking getBooking(GotoBookingRequest gotoBooking, PmsBooking pmsBooking, PmsConfiguration config,
            SessionInfo zauiActivityManagerSession) throws Exception {
        return mapBookingToPmsBooking(gotoBooking, pmsBooking, config, zauiActivityManagerSession);
    }

    @Override
    public GotoBookingResponse getBookingResponse(PmsBooking pmsBooking, GotoBookingRequest booking,
            PmsConfiguration config,
            int cuttOffHours) throws Exception {
        List<RatePlanCode> ratePlans = new ArrayList<>();
        List<RoomTypeCode> roomTypes = new ArrayList<>();
        List<GotoRoomResponse> roomsResponse = new ArrayList<>();
        for (GotoRoomRequest room : booking.getRooms()) {
            GotoRoomResponse roomRes = mapRoomRequestToRoomRes(room, cuttOffHours, config);
            roomsResponse.add(roomRes);
            ratePlans.add(new RatePlanCode(room.getRatePlanCode()));
            roomTypes.add(new RoomTypeCode(room.getRoomCode()));
        }
        DecimalFormat priceFormat = new DecimalFormat("#.##");
        double totalPrice = Double.parseDouble(priceFormat.format(pmsBooking.getTotalPrice()));
        PriceTotal priceTotal = new PriceTotal();
        priceTotal.setAmount(totalPrice);
        priceTotal.setCurrency(booking.getCurrency());

        GotoBookingResponse bookingResponse = new GotoBookingResponse();
        bookingResponse.setReservationId(pmsBooking.id);
        bookingResponse.setHotelCode(booking.getHotelCode());
        bookingResponse.setRooms(roomsResponse);
        bookingResponse.setRatePlans(ratePlans);
        bookingResponse.setRoomTypes(roomTypes);
        bookingResponse.setPriceTotal(priceTotal);
        bookingResponse.setActivities(getActivitiesFromPmsBooking(pmsBooking.bookingZauiActivityItems));
        return bookingResponse;
    }

    private List<GotoActivityReservationDto> getActivitiesFromPmsBooking(List<BookingZauiActivityItem> activityItems) {
        return activityItems.stream().map(activityItem -> {
            GotoActivityReservationDto activity = new GotoActivityReservationDto();
            activity.setOctoReservationResponse(activityItem.getOctoBooking());
            return activity;
        }).collect(Collectors.toList());
    }

    private GotoRoomResponse mapRoomRequestToRoomRes(GotoRoomRequest room, int cuttOffHours, PmsConfiguration config)
            throws Exception {
        GotoRoomResponse roomRes = new GotoRoomResponse();
        roomRes.setCheckInDate(room.getCheckInDate());
        roomRes.setCheckOutDate(room.getCheckOutDate());
        roomRes.setAdults(room.getAdults());
        roomRes.setChildrenAges(room.getChildrenAges());
        roomRes.setCancelationDeadline(bookingCancellationService.getCancellationDeadLine(room.getCheckInDate(),
                cuttOffHours, config));
        roomRes.setPrice(room.getPrice());
        return roomRes;
    }

    private PmsBooking mapBookingToPmsBooking(GotoBookingRequest booking, PmsBooking pmsBooking,
            PmsConfiguration config,
            SessionInfo zauiActivityManagerSession) throws Exception {
        for (PmsBookingRooms room : pmsBooking.getAllRooms()) {
            room.unmarkOverBooking();
        }
        pmsBooking.channel = GotoConstants.GOTO_BOOKING_CHANNEL_NAME;
        pmsBooking.language = booking.getLanguage();
        pmsBooking.isPrePaid = true;
        if (isNotBlank(booking.getComment())) {
            PmsBookingComment comment = new PmsBookingComment();
            comment.userId = "";
            comment.comment = booking.getComment();
            comment.added = new Date();
            pmsBooking.comments.put(System.currentTimeMillis(), comment);
        }

        GotoBooker booker = booking.getOrderer();
        String user_fullName = booker.getFirstName() + " " + booker.getLastName();
        String user_cellPhone = booker.getMobile().getAreaCode() + booker.getMobile().getPhoneNumber();

        pmsBooking.registrationData.resultAdded.put("user_fullName", user_fullName);
        pmsBooking.registrationData.resultAdded.put("user_cellPhone", user_cellPhone);
        pmsBooking.registrationData.resultAdded.put("user_emailAddress", booker.getEmail());

        mapRoomsToPmsRooms(booking, pmsBooking, config);

        for (GotoActivityReservationDto activity : booking.getActivities()) {
            BookingZauiActivityItem activityItem = zauiActivityService.mapActivityToBookingZauiActivityItem(
                    activity.getOctoReservationResponse(), zauiActivityManagerSession);
            pmsBooking = zauiActivityService.addActivityToBooking(activityItem, activity.getOctoReservationResponse(),
                    pmsBooking);
        }
        return pmsBooking;
    }

    private void mapRoomsToPmsRooms(GotoBookingRequest booking, PmsBooking pmsBooking, PmsConfiguration config)
            throws Exception {
        for (GotoRoomRequest gotoBookingRoom : booking.getRooms()) {
            PmsBookingRooms room = mapRoomToPmsRoom(booking, gotoBookingRoom, config);
            pmsBooking.addRoom(room);
        }
        if (booking.getRooms() == null || booking.getRooms().isEmpty()) {
            PmsBookingRooms room = new PmsBookingRooms();
            room.bookingItemTypeId = BOOKING_ITEM_TYPE_ID_FOR_VIRTUAL_GOTO_ROOM;
            room.date.start = new Date();
            room.date.end = new Date();
            pmsBooking.addRoom(room);
        }
    }

    private PmsBookingRooms mapRoomToPmsRoom(GotoBookingRequest booking, GotoRoomRequest gotoBookingRoom,
            PmsConfiguration config)
            throws Exception {
        PmsBookingRooms pmsBookingRoom = new PmsBookingRooms();
        pmsBookingRoom = setCheckinOutDate(pmsBookingRoom, gotoBookingRoom, config);
        int numberOfChildren = gotoBookingRoom.getChildrenAges().size();
        pmsBookingRoom.numberOfGuests = gotoBookingRoom.getAdults() + numberOfChildren;
        pmsBookingRoom.bookingItemTypeId = gotoBookingRoom.getRoomCode();

        PmsGuests guest = new PmsGuests();
        guest.email = booking.getOrderer().getEmail();
        guest.name = booking.getOrderer().getFirstName() + " " + booking.getOrderer().getLastName();
        guest.phone = booking.getOrderer().getMobile().getPhoneNumber();
        guest.prefix = booking.getOrderer().getMobile().getAreaCode();
        pmsBookingRoom.guests.add(guest);

        pmsBookingRoom = setGotoBookingPrice(pmsBookingRoom, gotoBookingRoom);

        for (int i = 1; i < pmsBookingRoom.numberOfGuests; i++) {
            PmsGuests extGuest = new PmsGuests();
            if (numberOfChildren > 0) {
                extGuest.isChild = true;
                numberOfChildren--;
            }
            pmsBookingRoom.guests.add(extGuest);
        }
        return pmsBookingRoom;
    }

    private PmsBookingRooms setCheckinOutDate(PmsBookingRooms room, GotoRoomRequest gotoBookingRoom,
            PmsConfiguration config)
            throws ParseException {
        checkinOutDateFormatter.setLenient(false);
        Date checkin = checkinOutDateFormatter.parse(gotoBookingRoom.getCheckInDate());
        Date checkout = checkinOutDateFormatter.parse(gotoBookingRoom.getCheckOutDate());
        room.date = new PmsBookingDateRange();
        room.date.start = config.getDefaultStart(checkin);
        room.date.end = config.getDefaultEnd(checkout);
        return room;
    }

    private PmsBookingRooms setGotoBookingPrice(PmsBookingRooms pmsBookingRoom, GotoRoomRequest gotoBookingRoom) {
        DAILY_PRICE_DATE_FORMATTER.setLenient(false);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pmsBookingRoom.date.start);
        Map<String, Double> dailyPricesFromGoto = gotoBookingRoom.getPrice().getDailyPrices()
                .stream()
                .collect(Collectors
                        .toMap(GotoRoomDailyPrice::getDate, GotoRoomDailyPrice::getPrice));
        while (calendar.getTime().before(pmsBookingRoom.date.end)) {
            String dailyPriceKey = DAILY_PRICE_DATE_FORMATTER.format(calendar.getTime());
            Double price = dailyPricesFromGoto.get(dailyPriceKey);
            pmsBookingRoom.priceMatrix.put(PmsBookingRooms.getOffsetKey(calendar, PmsBooking.PriceType.daily), price);
            calendar.add(Calendar.DATE, 1);
        }
        pmsBookingRoom.totalCost = gotoBookingRoom.getPrice().getTotalRoomPrice();
        return pmsBookingRoom;
    }
}
