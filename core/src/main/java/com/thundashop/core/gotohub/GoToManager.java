package com.thundashop.core.gotohub;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.applications.StoreApplicationPool;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.gotohub.dto.*;
import com.thundashop.core.gotohub.dto.Room;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pmsbookingprocess.BookingProcessRooms;
import com.thundashop.core.pmsbookingprocess.PmsBookingProcess;
import com.thundashop.core.pmsbookingprocess.StartBooking;
import com.thundashop.core.pmsmanager.*;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.utils.GoToStatusCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.*;

@Component
@GetShopSession
@Slf4j
public class GoToManager extends GetShopSessionBeanNamed implements IGoToManager {
    @Autowired PmsManager pmsManager;
    @Autowired StoreManager storeManager;
    @Autowired StorePool storePool;
    @Autowired BookingEngine bookingEngine;
    @Autowired PmsInvoiceManager pmsInvoiceManager;
    @Autowired PmsBookingProcess pmsProcess;
    @Autowired StoreApplicationPool storeApplicationPool;
    @Autowired OrderManager orderManager;
    @Autowired MessageManager messageManager;
    @Autowired UserManager userManager;

    private static final SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final SimpleDateFormat checkinOutDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat cancellationDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public GoToConfiguration goToConfiguration = new GoToConfiguration();

    @Override
    public GoToApiResponse getHotelInformation() {
        try {
            saveSchedulerAsCurrentUser();
            Hotel hotel = mapStoreToGoToHotel(storeManager.getMyStore(), pmsManager.getConfiguration());
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
    public GoToApiResponse getRoomTypeDetails() throws Exception {
        try {
            saveSchedulerAsCurrentUser();
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
    public GoToApiResponse getPriceAndAllotment() throws Exception {
        Date from = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(from);
        cal.add(Calendar.DATE, 30);
        Date to = cal.getTime();
        return getPriceAndAllotmentWithDate(from, to);
    }

    @Override
    public GoToApiResponse getPriceAndAllotmentWithDate(Date from, Date to) throws Exception {
        try {
            saveSchedulerAsCurrentUser();
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

    private void checkDateRangeValidity(Date from, Date to) throws GotoException {
        if (from.after(to)) {
            throw new GotoException(GoToStatusCodes.INVALID_DATE_RANGE.code, GoToStatusCodes.INVALID_DATE_RANGE.message);
        }
    }

    private PmsBooking getBooking(Booking booking) throws Exception {
        PmsBooking pmsBooking = findCorrelatedBooking(booking.getReservationId());
        if (pmsBooking == null) {
            pmsBooking = pmsManager.startBooking();
        }

        pmsBooking = mapBookingToPmsBooking(booking, pmsBooking);
        pmsManager.setBooking(pmsBooking);
        pmsInvoiceManager.clearOrdersOnBooking(pmsBooking);
        pmsBooking = pmsManager.doCompleteBooking(pmsBooking);
        return pmsBooking;
    }

    private String getBookingDetailsTextForMail(Booking booking) {
        String text = "Booking Details:<br><br>";
        text += "   Arrival Date: " + booking.getCheckInDate();
        text += "<br><br>";
        text += "   Departure Date: " + booking.getCheckOutDate();
        text += "<br><br>";
        text += "   Rooms:";
        text += "<br><br>";
        for (Room room : booking.getRooms()) {
            BookingItemType type = bookingEngine.getBookingItemType(room.getRoomCode());
            if (type != null) text += "      " + type.name + "<br><br>";
            else text += "      Room Type (BookingItemType) isn't found for Id: " + room.getRoomCode() + "<br><br>";

        }
        return text;
    }

    private void handleOverbooking(PmsBooking pmsBooking) throws Exception {
        if (!pmsBooking.hasOverBooking()) return;

        pmsManager.deleteBooking(pmsBooking.id);
        log.error("Goto Booking Failed, Reason: Overbooking");
        throw new GotoException(GoToStatusCodes.OVERBOOKING.code, GoToStatusCodes.OVERBOOKING.message);
    }

    @Override
    public GoToApiResponse saveBooking(Booking booking) {
        try {
            saveSchedulerAsCurrentUser();
            handleDifferentCurrencyBooking(booking.getCurrency());
            PmsBooking pmsBooking = getBooking(booking);
            if (pmsBooking == null) {
                throw new GotoException(GoToStatusCodes.SAVE_BOOKING_FAIL.code, GoToStatusCodes.SAVE_BOOKING_FAIL.message);
            }
            pmsManager.saveBooking(pmsBooking);
            pmsInvoiceManager.clearOrdersOnBooking(pmsBooking);
            handleOverbooking(pmsBooking);

            BookingResponse bookingResponse = getBookingResponse(pmsBooking.id, booking, pmsBooking.getTotalPrice());
            return new GoToApiResponse(true,
                    GoToStatusCodes.SAVE_BOOKING_SUCCESS.code,
                    GoToStatusCodes.SAVE_BOOKING_SUCCESS.message,
                    bookingResponse);
        } catch (GotoException e) {
            handleNewBookingError(booking, e.getMessage(), e.getStatusCode());
            return new GoToApiResponse(false, e.getStatusCode(), e.getMessage(), null);
        } catch (Exception e) {
            logPrintException(e);
            handleNewBookingError(booking, GoToStatusCodes.SAVE_BOOKING_FAIL.message, GoToStatusCodes.SAVE_BOOKING_FAIL.code);
            return new GoToApiResponse(false, GoToStatusCodes.SAVE_BOOKING_FAIL.code, GoToStatusCodes.SAVE_BOOKING_FAIL.message, null);
        }
    }

    private void handleIfBookingDeleted(PmsBooking pmsBooking) throws Exception {
        for (PmsBookingRooms room : pmsBooking.rooms) {
            if (!room.deleted) return;
        }
        throw new GotoException(GoToStatusCodes.BOOKING_DELETED.code, GoToStatusCodes.BOOKING_DELETED.message);
    }

    @Override
    public GoToApiResponse confirmBooking(String reservationId) {
        try {
            saveSchedulerAsCurrentUser();
            PmsBooking pmsBooking = findCorrelatedBooking(reservationId);
            if (pmsBooking == null) {
                throw new GotoException(GoToStatusCodes.BOOKING_NOT_FOUND.code, GoToStatusCodes.BOOKING_NOT_FOUND.message);
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
            throw new GotoException(GoToStatusCodes.ORDER_SYNCHRONIZATION_FAILED.code, GoToStatusCodes.ORDER_SYNCHRONIZATION_FAILED.message);
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
        deletionRequestTime = trimTillHour(deletionRequestTime);
        for (PmsBookingRooms room : booking.rooms) {
            Date cancellationDeadLine = cancellationDateFormatter.parse(
                    getCancellationDeadLine(
                            checkinOutDateFormatter.format(room.date.start)
                    )
            );
            cancellationDeadLine = trimTillHour(cancellationDeadLine);
            if (deletionRequestTime.after(cancellationDeadLine)) {
                throw new GotoException(GoToStatusCodes.CANCELLATION_DEADLINE_PASSED.code, GoToStatusCodes.CANCELLATION_DEADLINE_PASSED.message);
            }
        }
    }

    @Override
    public GoToApiResponse cancelBooking(String reservationId) {
        try {
            Date deletionRequestTime = new Date();
            saveSchedulerAsCurrentUser();
            PmsBooking pmsBooking = findCorrelatedBooking(reservationId);
            if (pmsBooking == null) {
                throw new GotoException(GoToStatusCodes.BOOKING_CANCELLATION_NOT_FOUND.code, GoToStatusCodes.BOOKING_CANCELLATION_NOT_FOUND.message);
            }
            handleDeletionIfCutOffHourPassed(pmsBooking.id, deletionRequestTime);
            pmsManager.logEntry("Deleted by channel manager", pmsBooking.id, null);

            pmsManager.deleteBooking(pmsBooking.id);
            handleOrderForCancelledBooking(reservationId);
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
        }
    }

    private void saveSchedulerAsCurrentUser() {
        getSession().currentUser = userManager.getUserById("gs_system_scheduler_user");
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

    private void handleNewBookingError(Booking booking, String errorMessage, long errorCode) {
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

        checkinDate = fixTime(checkin, pmsManager.getConfigurationSecure().getDefaultStart());
        calendar.setTime(checkinDate);
        calendar.add(Calendar.HOUR_OF_DAY, -goToConfiguration.cuttOffHours);
        cancellationDate = calendar.getTime();
        cancellationDeadLine = cancellationDateFormatter.format(cancellationDate);
        return cancellationDeadLine;
    }

    private BookingResponse getBookingResponse(String reservationId, Booking booking, double totalPrice) throws Exception {
        List<RatePlanCode> ratePlans = new ArrayList<>();
        List<RoomTypeCode> roomTypes = new ArrayList<>();
        String cancellationDeadLine = getCancellationDeadLine(booking.getCheckInDate());

        for (Room room : booking.getRooms()) {
            room.setCancelationDeadline(cancellationDeadLine);
            ratePlans.add(new RatePlanCode(room.getRatePlanCode()));
            roomTypes.add(new RoomTypeCode(room.getRoomCode()));

        }

        PriceTotal priceTotal = new PriceTotal();
        priceTotal.setAmount(totalPrice);
        priceTotal.setCurrency(booking.getCurrency());

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setReservationId(reservationId);
        bookingResponse.setHotelCode(booking.getHotelCode());
        bookingResponse.setCheckInDate(booking.getCheckInDate());
        bookingResponse.setCheckOutDate(booking.getCheckOutDate());
        bookingResponse.setRooms(booking.getRooms());
        bookingResponse.setRatePlans(ratePlans);
        bookingResponse.setRoomTypes(roomTypes);
        bookingResponse.setPriceTotal(priceTotal);
        return bookingResponse;
    }

    private boolean isCurrencySameWithSystem(String currencyCode) {
        return currencyCode.equals(storeManager.getStoreSettingsApplicationKey("currencycode"));
    }

    private void handleDifferentCurrencyBooking(String bookingCurrency) throws GotoException {
        if (StringUtils.isBlank(bookingCurrency) || isCurrencySameWithSystem(bookingCurrency)) return;
        log.error("Booking currency didn't match with system currency..");
        log.error("Booking currency: " + bookingCurrency);
        throw new GotoException(GoToStatusCodes.DIFFERENT_CURRENCY.code, GoToStatusCodes.DIFFERENT_CURRENCY.message);
    }

    private void activatePaymentMethod(String pmethod) throws GotoException {
        try {
            if (!storeApplicationPool.isActivated(pmethod)) {
                storeApplicationPool.activateApplication(pmethod);
            }
        } catch (Exception e) {
            log.error("Error occurred while activate payment method, id: " + pmethod);
            throw new GotoException(GoToStatusCodes.PAYMENT_METHOD_ACTIVATION_FAILED.code, GoToStatusCodes.PAYMENT_METHOD_ACTIVATION_FAILED.message);
        }
    }

    private String getPaymentTypeId() throws GotoException {
        if (StringUtils.isBlank(goToConfiguration.getPaymentTypeId()))
            throw new GotoException(GoToStatusCodes.PAYMENT_METHOD_NOT_FOUND.code, GoToStatusCodes.PAYMENT_METHOD_NOT_FOUND.message);
        return goToConfiguration.paymentTypeId;
    }

    private Date fixTime(String dateStr, String time) throws Exception {
        try {
            checkinOutDateFormatter.setLenient(false);
            Date date = checkinOutDateFormatter.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            String[] hourAndSecond = time.split(":");
            cal.set(Calendar.HOUR_OF_DAY, new Integer(hourAndSecond[0]));
            cal.set(Calendar.MINUTE, new Integer(hourAndSecond[1]));

            return cal.getTime();
        } catch (Exception e) {
            logPrintException(e);
            log.error("Date parsing failed.. Date in string-> " + dateStr);
            throw new GotoException(GoToStatusCodes.INVALID_CHECKIN_CHECKOUT_FORMAT.code,
                    GoToStatusCodes.INVALID_CHECKIN_CHECKOUT_FORMAT.message);
        }
    }

    private PmsBookingRooms setCorrectStartEndTime(PmsBookingRooms room, Booking booking) throws Exception {
        room.date = new PmsBookingDateRange();
        room.date.start = fixTime(booking.getCheckInDate(), pmsManager.getConfigurationSecure().getDefaultStart());
        room.date.end = fixTime(booking.getCheckOutDate(), pmsManager.getConfigurationSecure().getDefaultEnd());
        return room;
    }

    private PmsBookingRooms mapRoomToPmsRoom(Booking booking, Room gotoBookingRoom) throws Exception {
        PmsBookingRooms pmsBookingRoom = new PmsBookingRooms();
        pmsBookingRoom = setCorrectStartEndTime(pmsBookingRoom, booking);
        pmsBookingRoom.numberOfGuests = gotoBookingRoom.getAdults() + gotoBookingRoom.getChildrenAges().size();
        pmsBookingRoom.bookingItemTypeId = gotoBookingRoom.getRoomCode();

        if (bookingEngine.getBookingItemType(gotoBookingRoom.getRoomCode()) == null) {
            log.error("booking room type does not exist, BookingItemTypeId: " + gotoBookingRoom.getRoomCode());
            throw new GotoException(GoToStatusCodes.ROOM_TYPE_NOT_FOUND.code,
                    GoToStatusCodes.ROOM_TYPE_NOT_FOUND.additionalInfo(gotoBookingRoom.getRoomCode()).message);
        }
        PmsGuests guest = new PmsGuests();
        guest.email = booking.getOrderer().getEmail();
        guest.name = booking.getOrderer().getFirstName() + " " + booking.getOrderer().getLastName();
        guest.phone = booking.getOrderer().getMobile().getAreaCode() + booking.getOrderer().getMobile().getPhoneNumber();

        pmsBookingRoom.guests.add(guest);
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

    private PmsBooking mapBookingToPmsBooking(Booking booking, PmsBooking pmsBooking) throws Exception {
        for (PmsBookingRooms room : pmsBooking.getAllRooms()) {
            room.unmarkOverBooking();
        }
        pmsBooking.channel = "goto";
        pmsBooking.language = booking.getLanguage();
        pmsBooking.isPrePaid = true;
        if (StringUtils.isNotBlank(booking.getComment())) {
            PmsBookingComment comment = new PmsBookingComment();
            comment.userId = "";
            comment.comment = booking.getComment();
            comment.added = new Date();
            pmsBooking.comments.put(System.currentTimeMillis(), comment);
        }

        Orderer booker = booking.getOrderer();
        String user_fullName = booker.getFirstName() + " " + booker.getLastName();
        String user_cellPhone = booker.getMobile().getAreaCode() + booker.getMobile().getPhoneNumber();

        pmsBooking.registrationData.resultAdded.put("user_fullName", user_fullName);
        pmsBooking.registrationData.resultAdded.put("user_cellPhone", user_cellPhone);
        pmsBooking.registrationData.resultAdded.put("user_emailAddress", booker.getEmail());

        List<Room> bookingRooms = booking.getRooms();

        for (Room gotoBookingRoom : bookingRooms) {
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
        if (StringUtils.isNotBlank(reservationId))
            return pmsManager.getBooking(reservationId);
        return null;
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
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof GoToConfiguration) {
                goToConfiguration = (GoToConfiguration) dataCommon;
            }
        }
    }

    private GoToRoomData mapBookingItemTypeToGoToRoomData(BookingItemType bookingItemType, BookingProcessRooms room, PmsAdditionalTypeInformation additionalInfo) {
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
            List<String> res = room.images.stream().filter(e -> isNotBlank(e.filename)).map(e -> e.filename).collect(Collectors.toList());
            roomData.setImages(res);
        }
        roomData.setStatus(bookingItemType.visibleForBooking);
        roomData.setPricesByGuests(room.pricesByGuests);
        roomData.setAvailableRooms(room.availableRooms);

        return roomData;
    }

    private String getRoomType(Integer type) {
        return BookingItemType.BookingSystemCategory.categories.get(type) != null ? BookingItemType.BookingSystemCategory.categories.get(type) : "ROOM";
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
            room.availableRooms = pmsManager.getNumberOfAvailable(type.id, arg.start, arg.end, true, true);
            room.id = type.id;
            room.systemCategory = type.systemCategory;
            room.visibleForBooker = type.visibleForBooking;
            PmsAdditionalTypeInformation typeInfo = pmsManager.getAdditionalTypeInformationById(type.id);
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

    private Double getPriceForRoom(BookingProcessRooms bookingProcessRoom, Date start, Date end, int numberofguests, String discountcode) {
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

        Calendar cal = Calendar.getInstance();
        cal.setTime(from);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 1);
        cal.add(Calendar.DAY_OF_YEAR, i);

        arg.start = cal.getTime();

        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 11);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);

        arg.end = cal.getTime();
        arg.rooms = 0;
        arg.adults = 1;
        arg.children = 0;

        return arg;
    }

    private List<PriceAllotment> getPriceAllotments(Date from, Date to) throws Exception {
        List<PriceAllotment> allotments = new ArrayList<>();
        long numberOfDays = getDateDifference(from, to);
        for (int i = 0; i <= numberOfDays; i++) {
            StartBooking range = getBookingArgument(from, i);
            List<GoToRoomData> goToRoomData = getGoToRoomData(true, range);
            for (GoToRoomData roomData : goToRoomData) {
                if (roomData.getPricesByGuests() == null) {
                    continue;
                }
                for (Map.Entry<Integer, Double> priceEntry : roomData.getPricesByGuests().entrySet()) {
                    PriceAllotment al = new PriceAllotment();
                    al.setStartDate(df.format(range.start));
                    al.setEndDate(df.format(range.end));
                    al.setRatePlanCode(roomData.getName() + "-" + priceEntry.getKey());
                    al.setRoomTypeCode(roomData.getGoToRoomTypeCode());
                    al.setPrice(priceEntry.getValue());
                    al.setAllotment(roomData.getAvailableRooms());
                    al.setCurrencyCode(storeManager.getStoreSettingsApplicationKey("currencycode"));
                    allotments.add(al);
                }
            }
        }
        return allotments;
    }

    private long getDateDifference(Date start, Date end) throws GotoException {
        long difference_In_Time
                = end.getTime() - start.getTime();
        long numberOfDays = TimeUnit
                .MILLISECONDS
                .toDays(difference_In_Time);
        if (numberOfDays > 30)
            throw new GotoException(GoToStatusCodes.LARGER_DATE_RANGE.code, GoToStatusCodes.LARGER_DATE_RANGE.message);
        if (numberOfDays < 0)
            throw new GotoException(GoToStatusCodes.INVALID_DATE_RANGE.code, GoToStatusCodes.INVALID_DATE_RANGE.message);
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
        ratePlan.setRatePlanCode(name + "-" + numberOfGuests);
        ratePlan.setRestriction("");
        ratePlan.setName("Rate Plan - " + name + " - " + numberOfGuests);
        ratePlan.setDescription("Rate Plan for " + numberOfGuests + " guests");
        StringBuilder about = new StringBuilder().append("Rate Plan ").append(numberOfGuests).append(" is mainly for ").append(numberOfGuests).append(" guests.")
                .append(" Price may vary for this rate plan and this rate plan will be applied")
                .append(" when someone book a room for ").append(numberOfGuests).append(" guests");
        ratePlan.setAbout(about.toString());
        ratePlan.setGuestCount(String.valueOf(numberOfGuests));
        ratePlan.setEffectiveDate(start);
        ratePlan.setExpireDate(end);
        return ratePlan;
    }

    private Hotel mapStoreToGoToHotel(Store store, PmsConfiguration pmsConfiguration) {
        Hotel hotel = new Hotel();
        Contact contact = new Contact();

        hotel.setName(store.configuration.shopName);
        String address = "";
        if (isNotBlank(store.configuration.streetAddress)) {
            address = store.configuration.streetAddress;
        }
        address += store.configuration.Adress;
        hotel.setAddress(address);
        hotel.setCurrencyCode(storeManager.getStoreSettingsApplicationKey("currencycode"));
        hotel.setCheckinTime(pmsConfiguration.getDefaultStart());
        hotel.setCheckoutTime(pmsConfiguration.getDefaultEnd());
        contact.setEmail(store.configuration.emailAdress);
        contact.setOrganizationNumber(store.configuration.orgNumber);
        contact.setPhoneNumber(store.configuration.phoneNumber);
        String website = getHotelWebAddress(store);
        if (isNotBlank(website)) contact.setWebsite(website);

        hotel.setContactDetails(contact);
        hotel.setDescription("");

        String imageUrlPrefix = "https://" + store.webAddressPrimary + "//displayImage.php?id=";
        if (isNotBlank(store.configuration.mobileImagePortrait))
            hotel.getImages().add(imageUrlPrefix + store.configuration.mobileImagePortrait);
        if (isNotBlank(store.configuration.mobileImageLandscape))
            hotel.getImages().add(imageUrlPrefix + store.configuration.mobileImageLandscape);
        hotel.setStatus(store.deactivated ? "Deactivated" : "Active");
        return hotel;
    }

    private String getHotelWebAddress(Store store) {
        String webAddress = store.getDefaultWebAddress();
        if (webAddress.contains("getshop.com")) return webAddress;
        for (String address : store.additionalDomainNames) {
            if (address.contains("getshop.com")) return address;
        }
        return null;
    }
}