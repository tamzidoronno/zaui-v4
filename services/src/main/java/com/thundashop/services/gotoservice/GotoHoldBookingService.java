package com.thundashop.services.gotoservice;

import com.thundashop.core.gotohub.constant.GotoConstants;
import com.thundashop.core.gotohub.dto.*;
import com.thundashop.core.pmsmanager.*;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.services.zauiactivityservice.IZauiActivityService;
import com.thundashop.zauiactivity.dto.ZauiActivity;
import com.thundashop.zauiactivity.dto.BookingZauiActivityItem;
import com.thundashop.zauiactivity.dto.Unit;
import com.thundashop.zauiactivity.dto.OctoBooking;
import com.thundashop.zauiactivity.dto.ActivityOption;
import com.thundashop.zauiactivity.dto.UnitItemOnBooking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.thundashop.core.gotohub.constant.GotoConstants.DAILY_PRICE_DATE_FORMATTER;
import static com.thundashop.core.gotohub.constant.GotoConstants.checkinOutDateFormatter;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class GotoHoldBookingService implements IGotoHoldBookingService{
    @Autowired
    IZauiActivityService zauiActivityService;
    @Override
    public PmsBooking getBooking(GotoBookingRequest gotoBooking, PmsBooking pmsBooking, PmsConfiguration config,
                                 SessionInfo zauiActivityManagerSession) throws Exception {
        return mapBookingToPmsBooking(gotoBooking, pmsBooking, config, zauiActivityManagerSession);
    }

    private PmsBooking mapBookingToPmsBooking(GotoBookingRequest booking, PmsBooking pmsBooking, PmsConfiguration config,
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

        List<GotoRoomRequest> bookingRooms = booking.getRooms();
        for (GotoRoomRequest gotoBookingRoom : bookingRooms) {
            PmsBookingRooms room = mapRoomToPmsRoom(booking, gotoBookingRoom, config);
            pmsBooking.addRoom(room);
        }
        for(GotoActivityReservationDto activity: booking.getActivities()) {
            BookingZauiActivityItem activityItem = mapActivityToBookingZauiActivityItem(activity, zauiActivityManagerSession);
            pmsBooking = zauiActivityService.addActivityToBooking(activityItem, activity.getOctoReservationResponse(), pmsBooking);
        }
        return pmsBooking;
    }

    private PmsBookingRooms mapRoomToPmsRoom(GotoBookingRequest booking, GotoRoomRequest gotoBookingRoom, PmsConfiguration config)
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

    private BookingZauiActivityItem mapActivityToBookingZauiActivityItem(GotoActivityReservationDto activity, SessionInfo sessionInfo) {
        BookingZauiActivityItem activityItem = new BookingZauiActivityItem();
        OctoBooking octoBooking = activity.getOctoReservationResponse();
        ZauiActivity zauiActivity = zauiActivityService.getZauiActivityByOptionId(octoBooking.getOptionId(), sessionInfo);
        ActivityOption bookedOption = zauiActivity.activityOptionList.stream()
                .filter(option -> option.getId().equals(octoBooking.getOptionId()))
                .findAny().orElse(null);
        activityItem.setZauiActivityId(zauiActivity.id);
        activityItem.setOctoProductId(zauiActivity.getProductId());
        activityItem.setOptionTitle(bookedOption.getInternalName());
        activityItem.setOptionId(bookedOption.getId());
        activityItem.setAvailabilityId(octoBooking.getAvailabilityId());
        activityItem.setUnits(getUnitFromOctoBookingUnitItems(octoBooking.getUnitItems()));
        activityItem.setUnpaidAmount(octoBooking.getPricing().getTotal());
        activityItem.setNotes(octoBooking.getNotes());
        activityItem.setLocalDateTimeStart(octoBooking.getAvailability().getLocalDateTimeStart());
        activityItem.setLocalDateTimeEnd(octoBooking.getAvailability().getLocalDateTimeEnd());
        activityItem.setSupplierId(zauiActivity.getSupplierId());
//        activityItem.setSupplierName(zauiActivity.getSupplierName());
        activityItem.setOctoBooking(octoBooking);
        return activityItem;
    }

    private PmsBookingRooms setCheckinOutDate(PmsBookingRooms room, GotoRoomRequest gotoBookingRoom, PmsConfiguration config)
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
                .collect(
                        Collectors.toMap(GotoRoomDailyPrice::getDate, GotoRoomDailyPrice::getPrice));
        while (calendar.getTime().before(pmsBookingRoom.date.end)) {
            String dailyPriceKey = DAILY_PRICE_DATE_FORMATTER.format(calendar.getTime());
            Double price = dailyPricesFromGoto.get(dailyPriceKey);
            pmsBookingRoom.priceMatrix.put(PmsBookingRooms.getOffsetKey(calendar, PmsBooking.PriceType.daily), price);
            calendar.add(Calendar.DATE, 1);
        }
        pmsBookingRoom.totalCost = gotoBookingRoom.getPrice().getTotalRoomPrice();

        return pmsBookingRoom;
    }
    private List<Unit> getUnitFromOctoBookingUnitItems(List<UnitItemOnBooking> unitItems) {
        Map<String, Integer> unitQuantity = new HashMap<>();
        for(UnitItemOnBooking unitItem : unitItems) {
            int count = 0;
            if(unitQuantity.containsKey(unitItem.getUnitId())) {
                count = unitQuantity.get(unitItem.getUnitId());
            }
            count++;
            unitQuantity.put(unitItem.getUnitId(), count);
        }

        return unitQuantity.keySet().stream()
                .map(unitId-> {
                    Unit unit = new Unit();
                    unit.setId(unitId);
                    unit.setQuantity(unitQuantity.get(unitId));
                    return unit;
                }).collect(Collectors.toList());
    }
}
