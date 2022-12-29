package com.thundashop.core.gotohub;

import static com.thundashop.core.gotohub.constant.GoToStatusCodes.BOOKING_CANCELLATION_FAILED;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.BOOKING_CANCELLATION_SUCCESS;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.BOOKING_CONFIRMATION_FAILED;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.BOOKING_CONFIRMATION_SUCCESS;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.FETCHING_HOTEL_INFO_FAIL;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.FETCHING_HOTEL_INFO_SUCCESS;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.FETCHING_PRICE_ALLOTMENT_FAIL;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.FETCHING_PRICE_ALLOTMENT_SUCCESS;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.FETCHING_ROOM_TYPE_INFO_FAIL;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.FETCHING_ROOM_TYPE_INFO_SUCCESS;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.INVALID_DATE_RANGE_ALLOTMENT;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.LARGER_DATE_RANGE;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.NO_ALLOTMENT;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.ORDER_SYNCHRONIZATION_FAILED;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.PAYMENT_FAILED;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.PAYMENT_METHOD_ACTIVATION_FAILED;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.SAVE_BOOKING_FAIL;
import static com.thundashop.core.gotohub.constant.GoToStatusCodes.SAVE_BOOKING_SUCCESS;
import static com.thundashop.core.gotohub.constant.GotoConstants.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.text.ParseException;
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
import java.util.Optional;
import java.util.stream.Collectors;

import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.zauiactivity.ZauiActivityManager;
import com.thundashop.services.gotoservice.*;
import com.thundashop.services.validatorservice.IGotoBookingRequestValidationService;
import com.thundashop.services.validatorservice.IGotoCancellationValidationService;
import com.thundashop.services.validatorservice.IGotoConfirmBookingValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Component;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.BookingEngineNew;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.gotohub.constant.GotoConstants;
import com.thundashop.core.gotohub.dto.GoToApiResponse;
import com.thundashop.core.gotohub.dto.GoToConfiguration;
import com.thundashop.core.gotohub.dto.GoToRoomData;
import com.thundashop.core.gotohub.dto.GotoBookingRequest;
import com.thundashop.core.gotohub.dto.GotoBookingResponse;
import com.thundashop.core.gotohub.dto.GotoException;
import com.thundashop.core.gotohub.dto.GotoRoomRequest;
import com.thundashop.core.gotohub.dto.GotoConfirmBookingRequest;
import com.thundashop.core.gotohub.dto.GotoRoomRestriction;
import com.thundashop.core.gotohub.dto.Hotel;
import com.thundashop.core.gotohub.dto.PriceAllotment;
import com.thundashop.core.gotohub.dto.GotoConfirmBookingRes;
import com.thundashop.core.gotohub.dto.RatePlan;
import com.thundashop.core.gotohub.dto.RoomType;
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
import com.thundashop.core.pmsmanager.PmsBookingDateRange;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsConfiguration;
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
import com.thundashop.services.bookingservice.IPmsBookingService;

import lombok.extern.slf4j.Slf4j;

@Component
@GetShopSession
@Slf4j
@EnableRetry
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
    BookingEngineNew bookingEngineNew;
    @Autowired
    PmsInvoiceManager pmsInvoiceManager;
    @Autowired
    PmsBookingProcess pmsProcess;
    @Autowired
    StoreApplicationPool storeApplicationPool;
    @Autowired
    MessageManager messageManager;
    @Autowired
    OrderManager orderManager;
    @Autowired
    ProductManager productManager;
    @Autowired
    UserManager userManager;
    @Autowired
    WubookManager wubookManager;
    @Autowired
    ZauiActivityManager zauiActivityManager;
    @Autowired
    IGotoService gotoService;
    @Autowired
    IGotoBookingCancellationService bookingCancellationService;
    @Autowired
    IGotoHotelInformationService gotoHotelInformationService;
    @Autowired
    IGotoBookingRequestValidationService bookingRequestValidationService;
    @Autowired
    IGotoCancellationValidationService cancellationValidationService;
    @Autowired
    IGotoConfirmBookingService confirmBookingService;
    @Autowired
    IGotoConfirmBookingValidationService confirmBookingValService;
    @Autowired
    IPmsBookingService pmsBookingService;
    @Autowired
    IGotoHoldBookingService holdBookingService;

    private GoToConfiguration goToConfiguration;
    private final String CURRENCY_CODE = "currencycode";
    private List<String> cancelledBookingList = new ArrayList<>();

    @Override
    public void initialize() throws SecurityException {
        super.initialize();
        goToConfiguration = gotoService.getGotoConfiguration(getSessionInfo());
        stopScheduler("AutoExpireBookings");
        createScheduler("AutoExpireBookings", "*/5 * * * *", GotoExpireBookingScheduler.class, true);
    }

    @Override
    public boolean saveConfiguration(GoToConfiguration configuration) {
        deleteObject(goToConfiguration);
        // Reset configuration Id to store new Object for updated goto configuration rather updating deleted one.
        // The old configuration will be kept as a separated deleted object in db
        configuration.id = "";
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
            Hotel hotel = gotoHotelInformationService.getHotelInformation(storeManager.getMyStore(),
                    pmsManager.getConfiguration(),
                    storeManager.getStoreSettingsApplicationKey(CURRENCY_CODE));
            return new GoToApiResponse(true, FETCHING_HOTEL_INFO_SUCCESS.code, FETCHING_HOTEL_INFO_SUCCESS.message,
                    hotel);
        } catch (Exception e) {
            logPrintException(e);
            return new GoToApiResponse(false, FETCHING_HOTEL_INFO_FAIL.code, FETCHING_HOTEL_INFO_FAIL.message, null);
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
            return new GoToApiResponse(true, FETCHING_ROOM_TYPE_INFO_SUCCESS.code,
                    FETCHING_ROOM_TYPE_INFO_SUCCESS.message, roomTypes);
        } catch (Exception e) {
            logPrintException(e);
            return new GoToApiResponse(false, FETCHING_ROOM_TYPE_INFO_FAIL.code, FETCHING_ROOM_TYPE_INFO_FAIL.message,
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
            return new GoToApiResponse(true, FETCHING_PRICE_ALLOTMENT_SUCCESS.code,
                    FETCHING_PRICE_ALLOTMENT_SUCCESS.message, priceAllotments);
        } catch (GotoException e) {
            return new GoToApiResponse(false, e.getStatusCode(), e.getMessage(), null);
        } catch (Exception e) {
            logPrintException(e);
            return new GoToApiResponse(false, FETCHING_PRICE_ALLOTMENT_FAIL.code, FETCHING_PRICE_ALLOTMENT_FAIL.message,
                    null);
        }
    }

    @Override
    public GoToApiResponse saveBooking(GotoBookingRequest booking) {
        try {
            removeCurrentUser();
            bookingRequestValidationService.validateSaveBookingDto(
                    booking,
                    storeManager.getStoreSettingsApplicationKey(CURRENCY_CODE),
                    pmsManager.getConfiguration(),
                    bookingEngineNew.getSessionInfo(),
                    zauiActivityManager.getSessionInfo(),
                    productManager.getSessionInfo());
            validateBookingAllotmentRestrictions(booking);
            PmsBooking pmsBooking = getBooking(booking);
            if (pmsBooking == null) {
                throw new GotoException(SAVE_BOOKING_FAIL.code, SAVE_BOOKING_FAIL.message);
            }
            pmsManager.saveBooking(pmsBooking);
            pmsInvoiceManager.clearOrdersOnBooking(pmsBooking);
            handleOverbooking(pmsBooking);

            GotoBookingResponse bookingResponse = holdBookingService.getBookingResponse(pmsBooking, booking,
                    pmsManager.getConfiguration(), goToConfiguration.cuttOffHours);

            return new GoToApiResponse(true, SAVE_BOOKING_SUCCESS.code, SAVE_BOOKING_SUCCESS.message, bookingResponse);
        } catch (GotoException e) {
            handleNewBookingError(booking, e.getMessage(), e.getStatusCode());
            return new GoToApiResponse(false, e.getStatusCode(), e.getMessage(), null);
        } catch (Exception e) {
            logPrintException(e);
            handleNewBookingError(booking, SAVE_BOOKING_FAIL.message, SAVE_BOOKING_FAIL.code);
            return new GoToApiResponse(false, SAVE_BOOKING_FAIL.code, SAVE_BOOKING_FAIL.message, null);
        }
    }

    @Override
    public GoToApiResponse confirmBooking(String reservationId) {
        return confirmBookingWithActivities(reservationId, null);
    }

    @Override
    public GoToApiResponse confirmBookingWithActivities(String reservationId, GotoConfirmBookingRequest confirmBookingReq) {
        try {
            saveSchedulerAsCurrentUser();
            PmsBooking pmsBooking = confirmBookingValService.validateConfirmBookingReq(reservationId,
                    goToConfiguration.getPaymentTypeId(),
                    pmsManager.getSessionInfo(),
                    confirmBookingReq);
            pmsBooking = confirmBookingService.confirmGotoBooking(pmsBooking.id, confirmBookingReq, pmsManager.getSessionInfo());
            String paymentMethodNameFromGoto = confirmBookingReq == null ? STAY_PAYMENT : confirmBookingReq.getPaymentMethod();
            String paymentLink = confirmPayment(pmsBooking, paymentMethodNameFromGoto);
            pmsManager.saveBooking(pmsBooking);
            return new GoToApiResponse(true, BOOKING_CONFIRMATION_SUCCESS.code, BOOKING_CONFIRMATION_SUCCESS.message,
                    isBlank(paymentLink)? null : new GotoConfirmBookingRes(paymentLink));
        } catch (GotoException e) {
            return handleUpdateBookingError(reservationId, e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            logPrintException(e);
            return handleUpdateBookingError(reservationId, BOOKING_CONFIRMATION_FAILED.message,
                    BOOKING_CONFIRMATION_FAILED.code);
        }
    }

    @Override
    public GoToApiResponse cancelBooking(String reservationId) {
        try {
            cancelledBookingList.add(reservationId);
            Date deletionRequestTime = new Date();
            saveSchedulerAsCurrentUser();
            PmsBooking booking = cancellationValidationService.validateCancellationReq(reservationId,
                    deletionRequestTime, pmsManager.getConfiguration(),
                    goToConfiguration.cuttOffHours, pmsManager.getSessionInfo());
            pmsManager.deleteBooking(reservationId);
            pmsManager.logEntry("Deleted by channel manager", reservationId, null);
            handleOrderForCancelledBooking(reservationId);
            sendEmailForCancelledBooking(booking);
            try {
                bookingCancellationService.notifyGotoAboutCancellation(
                        frameworkConfig.getGotoCancellationEndpoint(), frameworkConfig.getGotoCancellationAuthKey(),
                        reservationId);
            } catch (Exception e) {
                log.error("Failed to acknowledge cancel booking.. Reason: {}, Actual error: {}", e.getMessage(), e);
            }
            return new GoToApiResponse(true, BOOKING_CANCELLATION_SUCCESS.code, BOOKING_CANCELLATION_SUCCESS.message,
                    null);
        } catch (GotoException e) {
            return handleUpdateBookingError(reservationId, e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            logPrintException(e);
            return handleUpdateBookingError(reservationId, BOOKING_CANCELLATION_FAILED.message,
                    BOOKING_CANCELLATION_FAILED.code);
        } finally {
            cancelledBookingList.remove(reservationId);
        }
    }

    @Override
    public void cancelUnpaidBookings() {
        if (goToConfiguration.getUnpaidBookingExpirationTime() < 1)
            return;
        gotoService
                .getUnpaidGotoBookings(goToConfiguration.getUnpaidBookingExpirationTime(), pmsManager.getSessionInfo())
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
            log.info("Couldn't send email because email config is not set.");
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
            throw new GotoException(INVALID_DATE_RANGE_ALLOTMENT.code, INVALID_DATE_RANGE_ALLOTMENT.message);
        }
    }

    private PmsBooking getBooking(GotoBookingRequest booking) throws Exception {
        PmsBooking pmsBooking = pmsManager.startBooking();
        pmsBooking = holdBookingService.getBooking(
                booking, pmsBooking, pmsManager.getConfiguration(), zauiActivityManager.getSessionInfo());
        pmsManager.setBookingByAdmin(pmsBooking, true);
        pmsInvoiceManager.clearOrdersOnBooking(pmsBooking);
        if(booking.getRooms() == null || booking.getRooms().isEmpty()) {
            holdBookingService.completeGotoBookingWithoutRoom(pmsBooking);
        } else {
            pmsBooking = pmsManager.doCompleteBooking(pmsBooking);
        }
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
        throw new GotoException(NO_ALLOTMENT.code, NO_ALLOTMENT.message);
    }

    private void handleOrderForCancelledBooking(String reservationId) throws Exception {
        try {
            PmsBooking pmsBooking = pmsManager.getBooking(reservationId);
            List<String> orderIds = new ArrayList<>(pmsBooking.orderIds);
            orderIds.stream()
                    .filter(orderId -> {
                        Order order = orderManager.getOrderSecure(orderId);
                        List<PmsBooking> bookings = pmsManager.getBookingsFromOrderId(orderId);
                        return !order.isCreditNote && order.creditOrderId.isEmpty() && bookings.size() <= 1;
                    })
                    .forEach(orderId -> pmsInvoiceManager.creditOrder(pmsBooking.id, orderId));
            orderIds = new ArrayList<>(pmsBooking.orderIds);
            orderIds.forEach(id -> {
                Order order = orderManager.getOrderSecure(id);
                if (order.status != Order.Status.PAYMENT_COMPLETED) {
                    pmsInvoiceManager.markOrderAsPaid(pmsBooking.id, id);
                }
            });

        } catch (Exception e) {
            logPrintException(e);
            throw new GotoException(ORDER_SYNCHRONIZATION_FAILED.code, ORDER_SYNCHRONIZATION_FAILED.message);
        }
    }

    private void saveSchedulerAsCurrentUser() {
        getSession().currentUser = userManager.getUserById("gs_system_scheduler_user");
    }

    private void removeCurrentUser() {
        getSession().currentUser = null;
    }

    private void handleOrderForGotoPayment(PmsBooking pmsBooking, Date checkoutDate) throws Exception {
        try {
            Date endInvoiceAt = new Date();
            if (checkoutDate.after(endInvoiceAt))
                endInvoiceAt = checkoutDate;

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
            log.error("Error occurred while processing payment for goto booking..");
            log.error("Please check exception logs...");
            throw new GotoException(PAYMENT_FAILED.code, PAYMENT_FAILED.message);
        }

    }

    private void handleNewBookingError(GotoBookingRequest booking, String errorMessage, long errorCode) {
        String emailDetails = "Booking has been failed.<br><br>" +
                "Some other possible reason also could happen: <br>" +
                "1. Maybe there is one or more invalid room to book <br>" +
                "2. Overbooking would have happened for this booking<br>" +
                "Please notify admin to check<br>"
                + getBookingDetailsTextForMail(booking);
        log.info("error code: " + errorCode + ", error message: " + errorMessage);
        log.info("Email is sending to the Hotel owner...");
        log.info(emailDetails);
        messageManager.sendMessageToStoreOwner(emailDetails, errorMessage);
        log.info("Email sent");
    }

    private GoToApiResponse handleUpdateBookingError(String reservationId, String errorMessage, long errorCode) {
        String emailDetails = "Booking Related Operation has been failed.<br><br>" +
                "Some other possible reason also could happen: <br>" +
                "1. The payment method is not valid or failed to activate<br>" +
                "2. Payment method is not saved in Goto Configuration<br>" +
                "Please notify admin to check<br>" +
                "<br>Booking Reservation ID: " + reservationId;
        log.info("error code: " + errorCode + ", error message: " + errorMessage);
        log.info("Email is sending to the Hotel owner...");
        log.info(emailDetails);
        messageManager.sendMessageToStoreOwner(emailDetails, errorMessage);
        log.info("Email sent");
        return new GoToApiResponse(false, errorCode, errorMessage, null);
    }

    private Date getCheckoutDateFromPmsBookingRooms(List<PmsBookingRooms> rooms) {
        Date checkOutDate = null;
        for (PmsBookingRooms room : rooms) {
            if (checkOutDate == null)
                checkOutDate = room.date.end;
            else if (checkOutDate.before(room.date.end))
                checkOutDate = room.date.end;
        }
        return checkOutDate;
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

    private void activatePaymentMethod(String pmethod) throws GotoException {
        try {
            if (!storeApplicationPool.isActivated(pmethod)) {
                storeApplicationPool.activateApplication(pmethod);
            }
        } catch (Exception e) {
            logPrintException(e);
            log.error("Error occurred while activate payment method, id: " + pmethod);
            throw new GotoException(PAYMENT_METHOD_ACTIVATION_FAILED.code, PAYMENT_METHOD_ACTIVATION_FAILED.message);
        }
    }

    private String confirmPayment(PmsBooking pmsBooking, String gotoPaymentMethodName) throws Exception {
        if(isNotBlank(gotoPaymentMethodName) && gotoPaymentMethodName.equals("GOTO_PAYMENT")) {
            pmsManager.saveBooking(handleGotoPayment(pmsBooking));
            return null;
        }
        else {
            pmsBooking.shortId = isNotBlank(pmsBooking.shortId) ? pmsBooking.shortId : pmsManager.getShortUniqueId(pmsBooking.id);
            pmsManager.saveBooking(pmsBooking);
            String paymentLinkFromConfig = pmsInvoiceManager.getPaymentLinkConfig().webAdress;
            String paymentLinkBase = paymentLinkFromConfig.endsWith("/") ? paymentLinkFromConfig : paymentLinkFromConfig + "/";
            return paymentLinkBase + "pr.php?id=" + pmsBooking.shortId;
        }
    }

    private PmsBooking handleGotoPayment(PmsBooking pmsBooking) throws Exception {
        pmsBooking = setDefaultPaymentMethod(pmsBooking);
        handleOrderForGotoPayment(pmsBooking, getCheckoutDateFromPmsBookingRooms(pmsBooking.rooms));
        return pmsBooking;
    }

    private PmsBooking setDefaultPaymentMethod(PmsBooking pmsBooking) throws Exception {
        String paymentMethodId = goToConfiguration.getPaymentTypeId();
        activatePaymentMethod(paymentMethodId);
        pmsBooking.paymentType = paymentMethodId;
        pmsBooking.isPrePaid = true;
        pmsManager.saveBooking(pmsBooking);
        return pmsBooking;
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
                log.error("failed, error message: {}, actual error: {}", ex.getMessage(), ex);
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
            throw new GotoException(LARGER_DATE_RANGE.code, LARGER_DATE_RANGE.message);
        if (numberOfDays < 0)
            throw new GotoException(INVALID_DATE_RANGE_ALLOTMENT.code, INVALID_DATE_RANGE_ALLOTMENT.message);
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

    private void validateBookingAllotmentRestrictions(GotoBookingRequest booking) throws ParseException, GotoException {
        Map<String, Map<String, Integer>> numberOfNeededRoomsDateWise = new HashMap<>();
        for (GotoRoomRequest room : booking.getRooms()) {
            validateBookingRoomRestrictions(room);
            Map<String, Integer> numberOfNeededRooms = addRoomIntoDayWiseBookingRoomCount(
                    numberOfNeededRoomsDateWise.get(room.getRoomCode()), room.getCheckInDate(), room.getCheckOutDate());
            numberOfNeededRoomsDateWise.put(room.getRoomCode(), numberOfNeededRooms);
        }
        for (String roomCode : numberOfNeededRoomsDateWise.keySet()) {
            Map<String, Integer> numberOfNeededRooms = numberOfNeededRoomsDateWise.get(roomCode);
            if (!isEnoughRoomAvailable(numberOfNeededRooms, roomCode)) {
                throwNoAllotmentException(roomCode);
            }
        }
    }

    private void validateBookingRoomRestrictions(GotoRoomRequest room) throws ParseException, GotoException {
        Date checkInDate = pmsManager.getConfiguration()
                .getDefaultStart(checkinOutDateFormatter.parse(room.getCheckInDate()));
        Date checkOutDate = pmsManager.getConfiguration()
                .getDefaultEnd(checkinOutDateFormatter.parse(room.getCheckOutDate()));
        Integer allotmentForOneRoom = pmsManager.getNumberOfAvailable(room.getRoomCode(), checkInDate, checkOutDate,
                true, true);
        if (allotmentForOneRoom == null || allotmentForOneRoom == 0) {
            throwNoAllotmentException(room.getRoomCode());
        }
    }

    boolean isEnoughRoomAvailable(Map<String, Integer> numberOfNeededRooms, String typeId) throws ParseException {
        for (String start : numberOfNeededRooms.keySet()) {
            StartBooking range = getBookingArgument(checkinOutDateFormatter.parse(start), 0);
            int allotment = pmsManager.getNumberOfAvailable(
                    typeId, range.start, range.end, false, true);
            if (allotment < numberOfNeededRooms.get(start))
                return false;
        }
        return true;
    }

    Map<String, Integer> addRoomIntoDayWiseBookingRoomCount(
            Map<String, Integer> existingRoomCount, String checkinDate, String checkoutDate) throws ParseException {
        if (existingRoomCount == null)
            existingRoomCount = new HashMap<>();
        String currentDate = checkinDate;
        Date checkin = checkinOutDateFormatter.parse(checkinDate);
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(checkin);
        while (currentDate.compareTo(checkoutDate) < 0) {
            int currentNeededRoom = Optional.ofNullable(existingRoomCount.get(currentDate)).orElse(0);
            currentNeededRoom++;
            existingRoomCount.put(currentDate, currentNeededRoom);
            currentCal.add(Calendar.DATE, 1);
            currentDate = checkinOutDateFormatter.format(currentCal.getTime());
        }
        return existingRoomCount;
    }

    private void throwNoAllotmentException(String roomCode) throws GotoException {
        String typeName = bookingEngine.getBookingItemType(roomCode).name;
        throw new GotoException(NO_ALLOTMENT.code,
                NO_ALLOTMENT.message + ", " +
                        "Room Type Name: " + typeName + ", " +
                        "RoomCode: " + roomCode);
    }
}