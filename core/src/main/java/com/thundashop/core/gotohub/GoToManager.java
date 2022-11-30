package com.thundashop.core.gotohub;

import static com.thundashop.core.gotohub.constant.GotoConstants.DAILY_PRICE_DATE_FORMATTER;
import static com.thundashop.core.gotohub.constant.GotoConstants.cancellationDateFormatter;
import static com.thundashop.core.gotohub.constant.GotoConstants.checkinOutDateFormatter;
import static com.thundashop.core.gotohub.constant.GotoConstants.df;
import static com.thundashop.core.gotohub.constant.GotoConstants.formatter;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.gotohub.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.gotohub.constant.GoToStatusCodes;
import com.thundashop.core.gotohub.constant.GotoConstants;
import com.thundashop.core.gotohub.schedulers.GotoExpireBookingScheduler;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pmsbookingprocess.BookingProcessRooms;
import com.thundashop.core.pmsbookingprocess.PmsBookingProcess;
import com.thundashop.core.pmsbookingprocess.StartBooking;
import com.thundashop.core.pmsmanager.NewOrderFilter;
import com.thundashop.core.pmsmanager.PmsAdditionalTypeInformation;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingComment;
import com.thundashop.core.pmsmanager.PmsBookingDateRange;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import com.thundashop.core.pmsmanager.PmsGuests;
import com.thundashop.core.pmsmanager.PmsInvoiceManager;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.pmsmanager.TimeRepeater;
import com.thundashop.core.pmsmanager.TimeRepeaterData;
import com.thundashop.core.pmsmanager.TimeRepeaterDateRange;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.wubook.WubookManager;
import com.thundashop.services.gotoservice.GotoService;

import lombok.extern.slf4j.Slf4j;

@Component
@GetShopSession
@Slf4j
public class GoToManager extends GetShopSessionBeanNamed implements IGoToManager {
    @Autowired
    PmsManager pmsManager;

    @Autowired
    StoreManager storeManager;

    @Autowired
    StorePool storePool;

    @Autowired
    BookingEngine bookingEngine;

    @Autowired
    PmsInvoiceManager pmsInvoiceManager;

    @Autowired
    PmsBookingProcess pmsProcess;

    @Autowired
    StoreApplicationPool storeApplicationPool;

    @Autowired
    OrderManager orderManager;

    @Autowired
    MessageManager messageManager;

    @Autowired
    UserManager userManager;

    @Autowired
    WubookManager wubookManager;

    @Autowired
    GotoService gotoService;

    private GoToConfiguration goToConfiguration;
    private final String CURRENCY_CODE = "currencycode";
    private List<String> cancelledBookingList = new ArrayList<>();

    @Override
    public void initialize() throws SecurityException {
        super.initialize();
        goToConfiguration = gotoService.getGotoConfiguration(getSessionInfo());

        stopScheduler("AutoExpireBookings");
        createScheduler("AutoExpireBookings", "*/5 * * * *", GotoExpireBookingScheduler.class);
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
    }

    @Override
    public boolean saveConfiguration(GoToConfiguration configuration) {
        deleteObject(goToConfiguration);
        saveObject(configuration);
        goToConfiguration = configuration;
        return true;
    }

    @Override
    public GoToConfiguration getConfiguration() {
        return goToConfiguration;
    }

    @Override
    public GoToApiResponse getHotelInformation() {
        try {
            removeCurrentUser();
            Hotel hotel = gotoService.getHotelInformation(storeManager.getMyStore(), pmsManager.getConfiguration(),
                    storeManager.getStoreSettingsApplicationKey("currencycode"));
            return new GoToApiResponse(true,
                    GoToStatusCodes.FETCHING_HOTEL_INFO_SUCCESS.code,
                    GoToStatusCodes.FETCHING_HOTEL_INFO_SUCCESS.message,
                    hotel);
        } catch (Exception e) {
            logPrintException(e);
            return new GoToApiResponse(false,
                    GoToStatusCodes.FETCHING_HOTEL_INFO_FAIL.code,
                    GoToStatusCodes.FETCHING_HOTEL_INFO_FAIL.message,
                    null);
        }
    }

    @Override
    public GoToApiResponse getRoomTypeDetails() {
        try {
            removeCurrentUser();
            StartBooking arg = getBookingArgument(new Date(), 0);
            List<GoToRoomData> goToRoomData = getGoToRoomData(false, arg);
            List<RoomType> roomTypes = new ArrayList<>();
            for (GoToRoomData roomData : goToRoomData) {
                if (isBlank(roomData.getGoToRoomTypeCode())) {
                    continue;
                }
                RoomType roomType = getRoomTypesFromRoomData(roomData);
                roomTypes.add(roomType);
            }
            return new GoToApiResponse(true,
                    GoToStatusCodes.FETCHING_ROOM_TYPE_INFO_SUCCESS.code,
                    GoToStatusCodes.FETCHING_ROOM_TYPE_INFO_SUCCESS.message,
                    roomTypes);
        } catch (Exception e) {
            logPrintException(e);
            return new GoToApiResponse(false,
                    GoToStatusCodes.FETCHING_ROOM_TYPE_INFO_FAIL.code,
                    GoToStatusCodes.FETCHING_ROOM_TYPE_INFO_FAIL.message,
                    null);
        }
    }

    @Override
    public GoToApiResponse getPriceAndAllotment() {
        Date from = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(from);
        cal.add(Calendar.DATE, 30);
        Date to = cal.getTime();
        return getPriceAndAllotmentWithDate(from, to);
    }

    @Override
    public GoToApiResponse getPriceAndAllotmentWithDate(Date from, Date to) {
        try {
            removeCurrentUser();
            List<PriceAllotment> priceAllotments = getPriceAllotments(from, to);
            checkDateRangeValidity(from, to);
            return new GoToApiResponse(true,
                    GoToStatusCodes.FETCHING_PRICE_ALLOTMENT_SUCCESS.code,
                    GoToStatusCodes.FETCHING_PRICE_ALLOTMENT_SUCCESS.message,
                    priceAllotments);
        } catch (GotoException e) {
            logPrintException(e);
            return new GoToApiResponse(false, e.getStatusCode(), e.getMessage(), null);
        } catch (Exception e) {
            logPrintException(e);
            return new GoToApiResponse(false,
                    GoToStatusCodes.FETCHING_PRICE_ALLOTMENT_FAIL.code,
                    GoToStatusCodes.FETCHING_PRICE_ALLOTMENT_FAIL.message,
                    null);
        }
    }

    @Override
    public GoToApiResponse saveBooking(GotoBookingRequest booking) {
        try {
            removeCurrentUser();
            handleDifferentCurrencyBooking(booking.getCurrency());
            PmsBooking pmsBooking = getBooking(booking);
            if (pmsBooking == null) {
                throw new GotoException(GoToStatusCodes.SAVE_BOOKING_FAIL.code,
                        GoToStatusCodes.SAVE_BOOKING_FAIL.message);
            }
            pmsManager.saveBooking(pmsBooking);
            pmsInvoiceManager.clearOrdersOnBooking(pmsBooking);
            handleOverbooking(pmsBooking);

            GotoBookingResponse bookingResponse = getBookingResponse(pmsBooking.id, booking,
                    pmsBooking.getTotalPrice());
            return new GoToApiResponse(true,
                    GoToStatusCodes.SAVE_BOOKING_SUCCESS.code,
                    GoToStatusCodes.SAVE_BOOKING_SUCCESS.message,
                    bookingResponse);
        } catch (GotoException e) {
            handleNewBookingError(booking, e.getMessage(), e.getStatusCode());
            return new GoToApiResponse(false, e.getStatusCode(), e.getMessage(), null);
        } catch (Exception e) {
            logPrintException(e);
            handleNewBookingError(booking, GoToStatusCodes.SAVE_BOOKING_FAIL.message,
                    GoToStatusCodes.SAVE_BOOKING_FAIL.code);
            return new GoToApiResponse(false, GoToStatusCodes.SAVE_BOOKING_FAIL.code,
                    GoToStatusCodes.SAVE_BOOKING_FAIL.message, null);
        }
    }

    @Override
    public GoToApiResponse confirmBooking(String reservationId) {
        try {
            saveSchedulerAsCurrentUser();
            PmsBooking pmsBooking = findCorrelatedBooking(reservationId);
            if (pmsBooking == null) {
                throw new GotoException(GoToStatusCodes.BOOKING_NOT_FOUND.code,
                        GoToStatusCodes.BOOKING_NOT_FOUND.message);
            }
            handleIfBookingDeleted(pmsBooking);
            pmsBooking = setPaymentMethod(pmsBooking);
            handlePaymentOrder(pmsBooking, getCheckoutDateFromPmsBookingRooms(pmsBooking.rooms));
            return new GoToApiResponse(true,
                    GoToStatusCodes.BOOKING_CONFIRMATION_SUCCESS.code,
                    GoToStatusCodes.BOOKING_CONFIRMATION_SUCCESS.message,
                    null);
        } catch (GotoException e) {
            handleUpdateBookingError(reservationId, e.getMessage(), e.getStatusCode());
            return new GoToApiResponse(false, e.getStatusCode(), e.getMessage(), null);
        } catch (Exception e) {
            logPrintException(e);
            handleUpdateBookingError(reservationId, GoToStatusCodes.BOOKING_CONFIRMATION_FAILED.message,
                    GoToStatusCodes.BOOKING_CONFIRMATION_FAILED.code);
            return new GoToApiResponse(false,
                    GoToStatusCodes.BOOKING_CONFIRMATION_FAILED.code,
                    GoToStatusCodes.BOOKING_CONFIRMATION_FAILED.message,
                    null);
        }
    }

    @Override
    public GoToApiResponse cancelBooking(String reservationId) {
        try {
            cancelledBookingList.add(reservationId);
            Date deletionRequestTime = new Date();
            saveSchedulerAsCurrentUser();
            PmsBooking pmsBooking = findCorrelatedBooking(reservationId);
            if (pmsBooking == null) {
                throw new GotoException(GoToStatusCodes.BOOKING_CANCELLATION_NOT_FOUND.code,
                        GoToStatusCodes.BOOKING_CANCELLATION_NOT_FOUND.message);
            }
            if (pmsBooking.getActiveRooms().isEmpty())
                throw new GotoException(GoToStatusCodes.BOOKING_CANCELLATION_ALREADY_CANCELLED.code,
                        GoToStatusCodes.BOOKING_CANCELLATION_ALREADY_CANCELLED.message);
            handleDeletionIfCutOffHourPassed(pmsBooking.id, deletionRequestTime);
            pmsManager.logEntry("Deleted by channel manager", pmsBooking.id, null);

            pmsManager.deleteBooking(pmsBooking.id);
            handleOrderForCancelledBooking(reservationId);
            sendEmailForCancelledBooking(pmsBooking);
            return new GoToApiResponse(true, GoToStatusCodes.BOOKING_CANCELLATION_SUCCESS.code,
                    GoToStatusCodes.BOOKING_CANCELLATION_SUCCESS.message, null);
        } catch (GotoException e) {
            handleUpdateBookingError(reservationId, e.getMessage(), e.getStatusCode());
            return new GoToApiResponse(false, e.getStatusCode(), e.getMessage(), null);

        } catch (Exception e) {
            logPrintException(e);
            handleUpdateBookingError(reservationId, GoToStatusCodes.BOOKING_CANCELLATION_FAILED.message,
                    GoToStatusCodes.BOOKING_CANCELLATION_FAILED.code);
            return new GoToApiResponse(false, GoToStatusCodes.BOOKING_CANCELLATION_FAILED.code,
                    GoToStatusCodes.BOOKING_CANCELLATION_FAILED.message, null);
        } finally {
            cancelledBookingList.remove(reservationId);
        }
    }

    @Override
    public void cancelUnpaidBookings() {
        if (goToConfiguration.getUnpaidBookingExpirationTime() < 1)
            return;
        gotoService.getUnpaidGotoBookings(goToConfiguration.getUnpaidBookingExpirationTime(), pmsManager.getSessionInfo())
                .forEach(booking -> {
                    pmsManager.logEntry("Autodeleted Goto Booking because it has expired.", booking.id, null);
                    cancelBooking(booking.id);
                    log.info("Autodeleted unpaid Goto booking as it has been expired. Reservation Id: {}", booking.id);
                });
    }

    @Override
    public void sendEmailForCancelledRooms(String reservationId, String channel, PmsBookingRooms room) {
        if (isBlank(channel) || !channel.contains(GotoConstants.GOTO_BOOKING_CHANNEL_NAME))
            return;

        if (cancelledBookingList.contains(reservationId))
            return;

        String toEmail = goToConfiguration.getEmail();
        if (isBlank(toEmail)) {
            log.info("Coundn't send email because email config is not set.");
            return;
        }

        BookingItemType roomType = bookingEngine.getBookingItemType(room.bookingItemTypeId);
        String roomTypeNameWithDateRange = roomType.name
                + " ( " + checkinOutDateFormatter.format(room.date.start)
                + " <-> " + checkinOutDateFormatter.format(room.date.end) + " )";
        String subject = "WARNING: GOTO Booking Has Been Canceled!!";
        String message = "A room of Goto booking has been cancelled. <br>" +
                "Booking reservation Id: " + reservationId + ".<br>" +
                (isNotBlank(room.bookingItemTypeId) ? "Room Type Code: " + room.bookingItemTypeId + ".<br> " : "") +
                (isNotBlank(roomTypeNameWithDateRange) ? "Room/Room-Type Name: " + roomTypeNameWithDateRange + ".<br>"
                        : "")
                +
                "<br>" +
                "Please take action and notify hotel administrator if it is unexpected.<br>";

        messageManager.sendMail(toEmail, "", subject, message, "post@getshop.com", "");
    }

    public void sendEmailForCancelledBooking(PmsBooking booking) {
        String toEmail = goToConfiguration.getEmail();
        if (isBlank(toEmail)) {
            log.info("Coundn't send email because email config is not set.");
            return;
        }

        String subject = "WARNING: GOTO Booking Has Been Canceled!!";
        String checkinOutDateRange = getCheckinOutDateForCancelledBooking(booking);
        String message = "A Goto booking has been cancelled. <br>" +
                "Booking reservation Id: " + booking.id + ".<br>" +
                (isNotBlank(checkinOutDateRange) ? "Stay: " + checkinOutDateRange + ".<br>" : "") +
                "<br>" +
                "Please take action and notify hotel administrator if it is unexpected.<br>";

        messageManager.sendMail(toEmail, "", subject, message, "post@getshop.com", "");
    }

    private String getCheckinOutDateForCancelledBooking(PmsBooking booking) {
        if (booking.rooms.isEmpty())
            return "";
        return checkinOutDateFormatter.format(booking.getAllRooms().get(0).date.start)
                + " <-> " + checkinOutDateFormatter.format(booking.getAllRooms().get(0).date.end);
    }

    private void checkDateRangeValidity(Date from, Date to) throws GotoException {
        if (from.after(to)) {
            throw new GotoException(GoToStatusCodes.INVALID_DATE_RANGE.code,
                    GoToStatusCodes.INVALID_DATE_RANGE.message);
        }
    }

    private PmsBooking getBooking(GotoBookingRequest booking) throws Exception {
        PmsBooking pmsBooking = pmsManager.startBooking();
        pmsBooking = mapBookingToPmsBooking(booking, pmsBooking);
        pmsManager.setBookingByAdmin(pmsBooking, true);
        pmsInvoiceManager.clearOrdersOnBooking(pmsBooking);
        pmsBooking = pmsManager.doCompleteBooking(pmsBooking);
        return pmsBooking;
    }

    private String getBookingDetailsTextForMail(GotoBookingRequest booking) {
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("Booking Details:<br><br>")
                .append("   Rooms:")
                .append("<br><br>");
        for (GotoRoomRequest room : booking.getRooms()) {
            BookingItemType type = bookingEngine.getBookingItemType(room.getRoomCode());
            if (type != null) {
                textBuilder.append("      ").append(type.name).append("<br><br>");
                textBuilder.append("      ").append(room.getRoomCode()).append("<br><br>");
                textBuilder.append("   Arrival Date: ").append(room.getCheckInDate()).append("<br><br>");
                textBuilder.append("   Departure Date: ").append(room.getCheckOutDate()).append("<br><br>");
            } else
                textBuilder.append("      Room Type (BookingItemType) isn't found for Id: ")
                        .append(room.getRoomCode())
                        .append("<br><br>");

        }
        return textBuilder.toString();
    }

    private void handleOverbooking(PmsBooking pmsBooking) throws Exception {
        if (!pmsBooking.hasOverBooking())
            return;

        pmsManager.deleteBooking(pmsBooking.id);
        log.error("Goto Booking Failed, Reason: Overbooking");
        throw new GotoException(GoToStatusCodes.OVERBOOKING.code, GoToStatusCodes.OVERBOOKING.message);
    }

    private void handleIfBookingDeleted(PmsBooking pmsBooking) throws Exception {
        for (PmsBookingRooms room : pmsBooking.rooms) {
            if (!room.deleted)
                return;
        }
        throw new GotoException(GoToStatusCodes.BOOKING_DELETED.code, GoToStatusCodes.BOOKING_DELETED.message);
    }

    private void handleOrderForCancelledBooking(String reservationId) throws Exception {
        try {
            PmsBooking pmsBooking = pmsManager.getBooking(reservationId);
            List<String> orderIds = new ArrayList<>(pmsBooking.orderIds);
            for (String orderId : orderIds) {
                Order order = orderManager.getOrderSecure(orderId);
                if (order.isCreditNote || !order.creditOrderId.isEmpty()) {
                    continue;
                }
                List<PmsBooking> bookings = pmsManager.getBookingsFromOrderId(orderId);
                if (bookings.size() > 1) {
                    continue;
                }
                pmsInvoiceManager.creditOrder(pmsBooking.id, orderId);
            }
        } catch (Exception e) {
            throw new GotoException(GoToStatusCodes.ORDER_SYNCHRONIZATION_FAILED.code,
                    GoToStatusCodes.ORDER_SYNCHRONIZATION_FAILED.message);
        }
    }

    private Date trimTillHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    private void handleDeletionIfCutOffHourPassed(String reservationId, Date deletionRequestTime) throws Exception {
        PmsBooking booking = findCorrelatedBooking(reservationId);
        //cancellation deadline would be applied only for paid bookings
        if (booking == null || booking.orderIds == null || booking.orderIds.isEmpty())
            return;
        deletionRequestTime = trimTillHour(deletionRequestTime);

        for (PmsBookingRooms room : booking.rooms) {
            Date cancellationDeadLine = cancellationDateFormatter.parse(
                    getCancellationDeadLine(
                            checkinOutDateFormatter.format(room.date.start)));
            cancellationDeadLine = trimTillHour(cancellationDeadLine);
            if (deletionRequestTime.after(cancellationDeadLine)) {
                throw new GotoException(GoToStatusCodes.CANCELLATION_DEADLINE_PASSED.code,
                        GoToStatusCodes.CANCELLATION_DEADLINE_PASSED.message);
            }
        }
    }

    private void saveSchedulerAsCurrentUser() {
        getSession().currentUser = userManager.getUserById("gs_system_scheduler_user");
    }

    private void removeCurrentUser() {
        getSession().currentUser = null;
    }

    private void handlePaymentOrder(PmsBooking pmsBooking, String checkoutDate) throws Exception {
        try {
            Date endInvoiceAt = new Date();
            checkinOutDateFormatter.setLenient(false);
            if (checkinOutDateFormatter.parse(checkoutDate).after(endInvoiceAt))
                endInvoiceAt = checkinOutDateFormatter.parse(checkoutDate);

            NewOrderFilter filter = new NewOrderFilter();
            filter.createNewOrder = false;
            filter.prepayment = true;
            filter.endInvoiceAt = endInvoiceAt;

            if (pmsBooking.paymentType != null && !pmsBooking.paymentType.isEmpty()) {
                pmsInvoiceManager.autoCreateOrderForBookingAndRoom(pmsBooking.id, pmsBooking.paymentType);
            }
            String orderId = pmsInvoiceManager.autoCreateOrderForBookingAndRoom(pmsBooking.id,
                    pmsBooking.paymentType);
            pmsInvoiceManager.markOrderAsPaid(pmsBooking.id, orderId);
        } catch (Exception e) {
            logPrintException(e);
            log.error("Error occured while processing payment for goto booking..");
            log.error("Please check exception logs...");
            throw new GotoException(GoToStatusCodes.PAYMENT_FAILED.code, GoToStatusCodes.PAYMENT_FAILED.message);
        }

    }

    private void handleNewBookingError(GotoBookingRequest booking, String errorMessage, long errorCode) {
        String emailDetails = "Booking has been failed.<br><br>" +
                "Some other possible reason also could happen: <br>" +
                "1. Maybe there is one or more invalid room to book <br>" +
                "2. Overbooking would have happened for this booking<br>" +
                "Please notify admin to check<br>"
                + getBookingDetailsTextForMail(booking);
        log.debug("error code: " + errorCode + ", error message: " + errorMessage);
        log.debug("Email is sending to the Hotel owner...");
        log.debug(emailDetails);
        messageManager.sendMessageToStoreOwner(emailDetails, errorMessage);
        log.debug("Email sent");
    }

    private void handleUpdateBookingError(String reservationId, String errorMessage, long errorCode) {
        String emailDetails = "Booking Related Operation has been failed.<br><br>" +
                "Some other possible reason also could happen: <br>" +
                "1. The payment method is not valid or failed to activate<br>" +
                "2. Payment method is not saved in Goto Configuration<br>" +
                "Please notify admin to check<br>" +
                "<br>Booking Reservation ID: " + reservationId;
        log.debug("error code: " + errorCode + ", error message: " + errorMessage);
        log.debug("Email is sending to the Hotel owner...");
        log.debug(emailDetails);
        messageManager.sendMessageToStoreOwner(emailDetails, errorMessage);
        log.debug("Email sent");
    }

    private String getCheckoutDateFromPmsBookingRooms(List<PmsBookingRooms> rooms) {
        Date checkOutDate = null;
        for (PmsBookingRooms room : rooms) {
            if (checkOutDate == null)
                checkOutDate = room.date.end;
            else if (checkOutDate.before(room.date.end))
                checkOutDate = room.date.end;
        }
        return checkinOutDateFormatter.format(checkOutDate);
    }

    private String getCancellationDeadLine(String checkin) throws Exception {
        Date checkinDate, cancellationDate;
        String cancellationDeadLine;
        Calendar calendar = Calendar.getInstance();
        Date date = checkinOutDateFormatter.parse(checkin);
        checkinDate = pmsManager.getConfiguration().getDefaultStart(date);
        calendar.setTime(checkinDate);
        calendar.add(Calendar.HOUR_OF_DAY, -goToConfiguration.cuttOffHours);
        cancellationDate = calendar.getTime();
        cancellationDeadLine = cancellationDateFormatter.format(cancellationDate);
        return cancellationDeadLine;
    }

    private GotoBookingResponse getBookingResponse(String reservationId, GotoBookingRequest booking, double totalPrice)
            throws Exception {
        List<RatePlanCode> ratePlans = new ArrayList<>();
        List<RoomTypeCode> roomTypes = new ArrayList<>();
        List<GotoRoomResponse> roomsResponse = new ArrayList<>();
        for (GotoRoomRequest room : booking.getRooms()) {
            GotoRoomResponse roomRes = new GotoRoomResponse();
            roomRes.setCheckInDate(room.getCheckInDate());
            roomRes.setCheckOutDate(room.getCheckOutDate());
            roomRes.setAdults(room.getAdults());
            roomRes.setChildrenAges(room.getChildrenAges());
            roomRes.setCancelationDeadline(getCancellationDeadLine(room.getCheckInDate()));
            roomRes.setPrice(room.getPrice());
            roomsResponse.add(roomRes);
            ratePlans.add(new RatePlanCode(room.getRatePlanCode()));
            roomTypes.add(new RoomTypeCode(room.getRoomCode()));
        }
        DecimalFormat df = new DecimalFormat("#.##");
        totalPrice = Double.valueOf(df.format(totalPrice));
        PriceTotal priceTotal = new PriceTotal();
        priceTotal.setAmount(totalPrice);
        priceTotal.setCurrency(booking.getCurrency());

        GotoBookingResponse bookingResponse = new GotoBookingResponse();
        bookingResponse.setReservationId(reservationId);
        bookingResponse.setHotelCode(booking.getHotelCode());
        bookingResponse.setRooms(roomsResponse);
        bookingResponse.setRatePlans(ratePlans);
        bookingResponse.setRoomTypes(roomTypes);
        bookingResponse.setPriceTotal(priceTotal);
        return bookingResponse;
    }

    private boolean isCurrencySameWithSystem(String currencyCode) {
        return currencyCode.equals(storeManager.getStoreSettingsApplicationKey(CURRENCY_CODE));
    }

    Map<String, Map<TimeRepeaterData, LinkedList<TimeRepeaterDateRange>>> getRestrictionData(
            Integer restrictionTypeId) {
        List<BookingItemType> bookingItemTypes = bookingEngine.getBookingItemTypesWithSystemType(null);
        Map<String, Map<TimeRepeaterData, LinkedList<TimeRepeaterDateRange>>> restrictionData = new HashMap<>();
        TimeRepeater repeater = new TimeRepeater();

        bookingItemTypes.forEach(bookingItemType -> {
            Map<TimeRepeaterData, LinkedList<TimeRepeaterDateRange>> restrictionToRangesMap = new HashMap<>();
            List<TimeRepeaterData> restrictionsForThisType = bookingEngine.getOpeningHoursWithType(bookingItemType.id,
                    restrictionTypeId);

            restrictionsForThisType.forEach(singleRestriction -> {
                LinkedList<TimeRepeaterDateRange> ranges = repeater.generateRange(singleRestriction);
                restrictionToRangesMap.put(singleRestriction, ranges);
            });
            restrictionData.put(bookingItemType.id, restrictionToRangesMap);
        });
        return restrictionData;
    }

    private void handleDifferentCurrencyBooking(String bookingCurrency) throws GotoException {
        if (isBlank(bookingCurrency) || isCurrencySameWithSystem(bookingCurrency))
            return;
        log.error("Booking currency didn't match with system currency..");
        log.error("Booking currency: " + bookingCurrency);
        throw new GotoException(GoToStatusCodes.DIFFERENT_CURRENCY.code, GoToStatusCodes.DIFFERENT_CURRENCY.message);
    }

    private void handleInvalidGuestRatePlanoBooking(
            Integer bookingNumberOfGuests, String roomTypeName, String ratePlaneCode, Integer maxNumberOfGuests)
            throws GotoException {
        if (isBlank(ratePlaneCode))
            throw new GotoException(
                    GoToStatusCodes.INVALID_RATE_PLAN_CODE.code, GoToStatusCodes.INVALID_RATE_PLAN_CODE.message);

        Integer ratePlanNumberOfGuest = new Integer(substringAfterLast(ratePlaneCode, "-"));
        String typeNameFromRatePlan = substringBeforeLast(ratePlaneCode, "-");
        if (isBlank(typeNameFromRatePlan) || !typeNameFromRatePlan.equals(roomTypeName)
                || ratePlanNumberOfGuest > maxNumberOfGuests || ratePlanNumberOfGuest < 1)
            throw new GotoException(
                    GoToStatusCodes.INVALID_RATE_PLAN_CODE.code, GoToStatusCodes.INVALID_RATE_PLAN_CODE.message);

        if (!ratePlanNumberOfGuest.equals(bookingNumberOfGuests))
            throw new GotoException(
                    GoToStatusCodes.NUMBER_OF_GUESTS_RATE_PLAN_CODE_MISMATCHED.code,
                    GoToStatusCodes.NUMBER_OF_GUESTS_RATE_PLAN_CODE_MISMATCHED.message);
        if (bookingNumberOfGuests > maxNumberOfGuests)
            throw new GotoException(
                    GoToStatusCodes.OVERFLOW_MAX_NUMBER_OF_GUESTS.code,
                    GoToStatusCodes.OVERFLOW_MAX_NUMBER_OF_GUESTS.message);
    }

    private void activatePaymentMethod(String pmethod) throws GotoException {
        try {
            if (!storeApplicationPool.isActivated(pmethod)) {
                storeApplicationPool.activateApplication(pmethod);
            }
        } catch (Exception e) {
            log.error("Error occurred while activate payment method, id: " + pmethod);
            throw new GotoException(GoToStatusCodes.PAYMENT_METHOD_ACTIVATION_FAILED.code,
                    GoToStatusCodes.PAYMENT_METHOD_ACTIVATION_FAILED.message);
        }
    }

    private String getPaymentTypeId() throws GotoException {
        if (isBlank(goToConfiguration.getPaymentTypeId()))
            throw new GotoException(GoToStatusCodes.PAYMENT_METHOD_NOT_FOUND.code,
                    GoToStatusCodes.PAYMENT_METHOD_NOT_FOUND.message);
        return goToConfiguration.paymentTypeId;
    }

    private PmsBookingRooms setCorrectStartEndTime(PmsBookingRooms room, GotoRoomRequest gotoBookingRoom) throws Exception {
        Date checkin = checkinOutDateFormatter.parse(gotoBookingRoom.getCheckInDate());
        Date checkout = checkinOutDateFormatter.parse(gotoBookingRoom.getCheckOutDate());
        PmsConfiguration config = pmsManager.getConfiguration();

        room.date = new PmsBookingDateRange();
        room.date.start = config.getDefaultStart(checkin);
        room.date.end = config.getDefaultEnd(checkout);
        return room;
    }

    private PmsBookingRooms mapRoomToPmsRoom(GotoBookingRequest booking, GotoRoomRequest gotoBookingRoom) throws Exception {
        BookingItemType type = bookingEngine.getBookingItemType(gotoBookingRoom.getRoomCode());
        PmsBookingRooms pmsBookingRoom = new PmsBookingRooms();
        pmsBookingRoom = setCorrectStartEndTime(pmsBookingRoom, gotoBookingRoom);
        int numberOfChildren = gotoBookingRoom.getChildrenAges().size();
        pmsBookingRoom.numberOfGuests = gotoBookingRoom.getAdults() + numberOfChildren;
        pmsBookingRoom.bookingItemTypeId = gotoBookingRoom.getRoomCode();

        if (type == null || type.deleted != null) {
            log.error("booking room type does not exist, BookingItemTypeId: " + gotoBookingRoom.getRoomCode());
            throw new GotoException(GoToStatusCodes.ROOM_TYPE_NOT_FOUND.code,
                    GoToStatusCodes.ROOM_TYPE_NOT_FOUND.message + gotoBookingRoom.getRoomCode());
        }
        handleInvalidGuestRatePlanoBooking(pmsBookingRoom.numberOfGuests, type.name, gotoBookingRoom.getRatePlanCode(),
                type.size);

        PmsGuests guest = new PmsGuests();
        guest.email = booking.getOrderer().getEmail();
        guest.name = booking.getOrderer().getFirstName() + " " + booking.getOrderer().getLastName();
        guest.phone = booking.getOrderer().getMobile().getPhoneNumber();
        guest.prefix = booking.getOrderer().getMobile().getAreaCode();
        pmsBookingRoom.guests.add(guest);

        for (GotoRoomDailyPrice dailyPrice : gotoBookingRoom.getPrice().getDailyPrices()) {
            Date day = DAILY_PRICE_DATE_FORMATTER.parse(dailyPrice.getDate());
            Calendar calendarDay = Calendar.getInstance();
            calendarDay.setTime(day);
            pmsBookingRoom.priceMatrix.put(
                    PmsBookingRooms.getOffsetKey(calendarDay, PmsBooking.PriceType.daily),
                    dailyPrice.getPrice());
        }
        pmsBookingRoom.totalCost = gotoBookingRoom.getPrice().getTotalRoomPrice();

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

    private PmsBooking setPaymentMethod(PmsBooking pmsBooking) throws Exception {
        String paymentMethodId = getPaymentTypeId();
        activatePaymentMethod(paymentMethodId);
        pmsBooking.paymentType = paymentMethodId;
        pmsBooking.isPrePaid = true;
        pmsManager.saveBooking(pmsBooking);
        return pmsBooking;
    }

    private PmsBooking mapBookingToPmsBooking(GotoBookingRequest booking, PmsBooking pmsBooking) throws Exception {
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
            PmsBookingRooms room = mapRoomToPmsRoom(booking, gotoBookingRoom);
            pmsBooking.addRoom(room);
        }

        if (pmsBooking.rooms.isEmpty()) {
            log.debug("Booking is not saved since there are no rooms to add");
            throw new GotoException(GoToStatusCodes.EMPTY_ROOM_LIST.code, GoToStatusCodes.EMPTY_ROOM_LIST.message);
        }
        return pmsBooking;
    }

    private PmsBooking findCorrelatedBooking(String reservationId) {
        if (isNotBlank(reservationId))
            return pmsManager.getBooking(reservationId);
        return null;
    }

    private GoToRoomData mapBookingItemTypeToGoToRoomData(BookingItemType bookingItemType, BookingProcessRooms room,
            PmsAdditionalTypeInformation additionalInfo) {
        GoToRoomData roomData = new GoToRoomData();

        roomData.setBookingEngineTypeId(bookingItemType.id);
        roomData.setDescription(bookingItemType.description);
        roomData.setName(bookingItemType.name);
        roomData.setGoToRoomTypeCode(bookingItemType.id);
        roomData.setRoomCategory(getRoomType(bookingItemType.systemCategory));
        roomData.setMaxGuest(bookingItemType.size);
        roomData.setNumberOfAdults(additionalInfo.numberOfAdults);
        roomData.setNumberOfChildren(additionalInfo.numberOfChildren);
        roomData.setNumberOfUnits(bookingEngine.getBookingItemsByType(bookingItemType.id).size());
        if (room.images != null) {
            List<String> res = room.images.stream().filter(e -> isNotBlank(e.filename)).map(e -> e.filename)
                    .collect(Collectors.toList());
            roomData.setImages(res);
        }
        roomData.setStatus(bookingItemType.visibleForBooking);
        roomData.setPricesByGuests(room.pricesByGuests);
        roomData.setAvailableRooms(room.availableRooms);

        return roomData;
    }

    private String getRoomType(Integer type) {
        return BookingItemType.BookingSystemCategory.categories.get(type) != null
                ? BookingItemType.BookingSystemCategory.categories.get(type)
                : "ROOM";
    }

    private List<GoToRoomData> getGoToRoomData(boolean needPricing, StartBooking arg) throws Exception {
        List<GoToRoomData> goToRoomData = new ArrayList<>();
        List<BookingItemType> bookingItemTypes = bookingEngine.getBookingItemTypesWithSystemType(null);
        User user = getSession() != null && getSession().currentUser != null ? getSession().currentUser : new User();
        for (BookingItemType type : bookingItemTypes) {
            if (!type.visibleForBooking) {
                continue;
            }
            BookingProcessRooms room = new BookingProcessRooms();
            room.userId = user.id;
            room.description = type.getTranslatedDescription(getSession().language);
            room.availableRooms = pmsManager.getNumberOfAvailable(type.id, arg.start, arg.end, false, true);
            room.id = type.id;
            room.systemCategory = type.systemCategory;
            room.visibleForBooker = type.visibleForBooking;
            PmsAdditionalTypeInformation typeInfo = pmsManager.getAdditionalTypeInformationById(type.id);
            if (wubookManager.isRestrictedForOta(type.id, arg.start, arg.end))
                room.availableRooms = 0;
            try {
                room.images.addAll(typeInfo.images);
                room.sortDefaultImageFirst();
                room.name = type.getTranslatedName(getSession().language);
                room.maxGuests = type.size;
                if (needPricing) {
                    for (int i = 1; i <= type.size; i++) {
                        room.roomsSelectedByGuests.put(i, i);
                        Double price = getPriceForRoom(room, arg.start, arg.end, i, "");
                        room.pricesByGuests.put(i, price);
                    }
                }
            } catch (Exception ex) {
                log.error("failed {} {}", ex.getMessage(), ex);
            }
            goToRoomData.add(mapBookingItemTypeToGoToRoomData(type, room, typeInfo));
        }
        return goToRoomData;
    }

    private Double getPriceForRoom(BookingProcessRooms bookingProcessRoom, Date start, Date end, int numberofguests,
            String discountcode) {
        PmsBookingRooms room = new PmsBookingRooms();
        room.bookingItemTypeId = bookingProcessRoom.id;
        room.date = new PmsBookingDateRange();
        room.date.start = start;
        room.date.end = end;
        room.numberOfGuests = numberofguests;

        PmsBooking booking = new PmsBooking();
        booking.priceType = PmsBooking.PriceType.daily;
        booking.couponCode = discountcode;
        booking.userId = bookingProcessRoom.userId;
        pmsManager.setPriceOnRoom(room, true, booking);
        return room.price;
    }

    private StartBooking getBookingArgument(Date from, int i) {
        StartBooking arg = new StartBooking();
        PmsConfiguration config = pmsManager.getConfiguration();

        Calendar cal = Calendar.getInstance();
        cal.setTime(from);
        cal.add(Calendar.DAY_OF_YEAR, i);

        arg.start = config.getDefaultStart(cal.getTime());

        cal.add(Calendar.DAY_OF_YEAR, 1);

        arg.end = config.getDefaultEnd(cal.getTime());

        arg.rooms = 0;
        arg.adults = 1;
        arg.children = 0;

        return arg;
    }

    private List<PriceAllotment> getPriceAllotments(Date from, Date to) throws Exception {
        List<PriceAllotment> allotments = new ArrayList<>();
        long numberOfDays = getDateDifference(from, to);
        Map<String, Map<TimeRepeaterData, LinkedList<TimeRepeaterDateRange>>> minStayInfo = getRestrictionData(
                TimeRepeaterData.TimePeriodeType.min_stay);
        Map<String, Map<TimeRepeaterData, LinkedList<TimeRepeaterDateRange>>> maxStayInfo = getRestrictionData(
                TimeRepeaterData.TimePeriodeType.max_stay);
        Map<String, Map<TimeRepeaterData, LinkedList<TimeRepeaterDateRange>>> noCheckInInfo = getRestrictionData(
                TimeRepeaterData.TimePeriodeType.noCheckIn);
        Map<String, Map<TimeRepeaterData, LinkedList<TimeRepeaterDateRange>>> noCheckOutInfo = getRestrictionData(
                TimeRepeaterData.TimePeriodeType.noCheckOut);
        for (int i = 0; i <= numberOfDays; i++) {
            StartBooking range = getBookingArgument(from, i);
            List<GoToRoomData> goToRoomData = getGoToRoomData(true, range);
            for (GoToRoomData roomData : goToRoomData) {
                if (roomData.getPricesByGuests() == null) {
                    continue;
                }
                int minStayRestriction = getRestrictionValueForADay(minStayInfo.get(roomData.getGoToRoomTypeCode()),
                        range.start);
                int maxStayRestriction = getRestrictionValueForADay(maxStayInfo.get(roomData.getGoToRoomTypeCode()),
                        range.start);
                int noCheckInRestriction = getRestrictionValueForADay(noCheckInInfo.get(roomData.getGoToRoomTypeCode()),
                        range.start);
                int noCheckOutRestriction = getRestrictionValueForADay(
                        noCheckOutInfo.get(roomData.getGoToRoomTypeCode()), range.start);

                GotoRoomRestriction restriction = new GotoRoomRestriction();
                restriction.setMinStay(minStayRestriction);
                restriction.setMaxStay(maxStayRestriction);
                restriction.setNoCheckin(noCheckInRestriction == 1);
                restriction.setNoCheckout(noCheckOutRestriction == 1);

                for (Map.Entry<Integer, Double> priceEntry : roomData.getPricesByGuests().entrySet()) {
                    PriceAllotment al = new PriceAllotment();
                    al.setStartDate(df.format(range.start));
                    al.setEndDate(df.format(range.end));
                    al.setRatePlanCode(roomData.getName() + "-" + priceEntry.getKey());
                    al.setRoomTypeCode(roomData.getGoToRoomTypeCode());
                    al.setPrice(priceEntry.getValue());
                    al.setAllotment(roomData.getAvailableRooms());
                    al.setCurrencyCode(storeManager.getStoreSettingsApplicationKey("currencycode"));
                    al.setRestrictions(restriction);
                    allotments.add(al);
                }
            }
        }
        return allotments;
    }

    private Integer getRestrictionValueForADay(
            Map<TimeRepeaterData, LinkedList<TimeRepeaterDateRange>> restrictionToRanges, Date dateToCheck) {
        int numberOfDays = 0;
        for (TimeRepeaterData restriction : restrictionToRanges.keySet()) {
            LinkedList<TimeRepeaterDateRange> ranges = restrictionToRanges.get(restriction);
            for (TimeRepeaterDateRange range : ranges) {
                if (range.start.after(dateToCheck))
                    break;
                if (range.isBetweenTime(dateToCheck)) {
                    numberOfDays = new Integer(restriction.timePeriodeTypeAttribute);
                    break;
                }
            }
        }
        return numberOfDays;
    }

    private long getDateDifference(Date start, Date end) throws GotoException {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        LocalDate localStart = start.toInstant().atZone(defaultZoneId).toLocalDate();
        LocalDate localEnd = end.toInstant().atZone(defaultZoneId).toLocalDate();
        long numberOfDays = ChronoUnit.DAYS.between(localStart, localEnd);
        if (numberOfDays > 30)
            throw new GotoException(GoToStatusCodes.LARGER_DATE_RANGE.code, GoToStatusCodes.LARGER_DATE_RANGE.message);
        if (numberOfDays < 0)
            throw new GotoException(GoToStatusCodes.INVALID_DATE_RANGE.code,
                    GoToStatusCodes.INVALID_DATE_RANGE.message);
        return numberOfDays;
    }

    private RoomType getRoomTypesFromRoomData(GoToRoomData roomData) {
        RoomType roomType = new RoomType();
        roomType.setRoomTypeCode(roomData.getGoToRoomTypeCode());
        roomType.setDescription(roomData.getDescription());
        roomType.setName(roomData.getName());
        roomType.setMaxGuest(roomData.getMaxGuest());
        roomType.setNumberOfAdults(roomData.getNumberOfAdults());
        roomType.setNumberOfUnit(roomData.getNumberOfAdults());
        roomType.setNumberOfChildren(roomData.getNumberOfChildren());
        roomType.setRoomCategory(roomData.getRoomCategory());
        roomType.setStatus(roomData.isStatus());
        List<RatePlan> ratePlans = new ArrayList<>();
        String start = LocalDate.now().format(formatter);
        String end = LocalDate.now().plusYears(1).format(formatter);
        for (int guest = 1; guest <= roomData.getMaxGuest(); guest++) {
            RatePlan newRatePlan = createNewRatePlan(guest, roomData.getName(), start, end);
            ratePlans.add(newRatePlan);
        }
        roomType.setRatePlans(ratePlans);
        return roomType;
    }

    private RatePlan createNewRatePlan(int numberOfGuests, String name, String start, String end) {
        RatePlan ratePlan = new RatePlan();
        ratePlan.setRatePlanCode(makeAndGetRatePlanCode(numberOfGuests, name));
        ratePlan.setName("Rate Plan - " + name + " - " + numberOfGuests);
        ratePlan.setDescription("Rate Plan for " + numberOfGuests + " guests");
        String about = "Rate Plan " + numberOfGuests + " is mainly for " + numberOfGuests + " guests." +
                " Price may vary for this rate plan and this rate plan will be applied" +
                " when someone book a room for " + numberOfGuests + " guests";
        ratePlan.setAbout(about);
        ratePlan.setGuestCount(String.valueOf(numberOfGuests));
        ratePlan.setEffectiveDate(start);
        ratePlan.setExpireDate(end);
        return ratePlan;
    }

    private String makeAndGetRatePlanCode(int numberOfGuests, String roomTypeName) {
        return roomTypeName + "-" + numberOfGuests;
    }
}