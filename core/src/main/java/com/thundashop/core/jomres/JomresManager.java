package com.thundashop.core.jomres;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.BookingTimeLineFlatten;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.jomres.dto.*;
import com.thundashop.core.jomres.services.*;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pmsmanager.*;
import com.thundashop.core.sedox.autocryptoapi.Exception;
import com.thundashop.core.storemanager.StoreManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@GetShopSession
public class JomresManager extends GetShopSessionBeanNamed implements IJomresManager {
    @Autowired
    PmsManager pmsManager;
    @Autowired
    BookingEngine bookingEngine;
    @Autowired
    PmsInvoiceManager pmsInvoiceManager;
    @Autowired
    MessageManager messageManager;
    @Autowired
    StoreManager storeManager;
    @Autowired
    OrderManager orderManager;
    @Autowired
    JomresLogManager jomresLogManager;
    @Autowired
    StoreApplicationPool storeApplicationPool;

    private static final Logger logger = LoggerFactory.getLogger(JomresManager.class);

    JomresConfiguration jomresConfiguration = null;

    Map<String, JomresRoomData> pmsItemToJomresRoomDataMap = new HashMap<>();
    Map<Integer, Set<PMSBlankBooking>> pmsBlankBookings = new HashMap<>();
    Map<Long, JomresBookingData> jomresToPmsBookingMap = new HashMap<>();
    Map<String, JomresBookingData> pmsToJomresBookingMap = new HashMap<>();
    Map<Integer, JomresRoomData> jomresPropertyToRoomDataMap = new HashMap<>();
    BaseService jomresService = new BaseService();
    String cmfClientAccessToken = null;
    Date cmfClientTokenGenerationTime = new Date();

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof JomresRoomData) {
                pmsItemToJomresRoomDataMap.put(((JomresRoomData) dataCommon).bookingItemId, (JomresRoomData) dataCommon);
                jomresPropertyToRoomDataMap.put(((JomresRoomData) dataCommon).jomresPropertyId, (JomresRoomData) dataCommon);
            } else if (dataCommon instanceof JomresConfiguration) {
                jomresConfiguration = (JomresConfiguration) dataCommon;
            } else if (dataCommon instanceof JomresBookingData) {
                jomresToPmsBookingMap.put(((JomresBookingData) dataCommon).jomresBookingId, (JomresBookingData) dataCommon);
                pmsToJomresBookingMap.put(((JomresBookingData) dataCommon).pmsBookingId, (JomresBookingData) dataCommon);
            } else if (dataCommon instanceof PMSBlankBooking) {
                int proprertyId = ((PMSBlankBooking) dataCommon).getPropertyId();
                pmsBlankBookings.put(proprertyId, pmsBlankBookings.computeIfAbsent(proprertyId, k -> new HashSet<>()));
                pmsBlankBookings.get(((PMSBlankBooking) dataCommon).getPropertyId()).add((PMSBlankBooking) dataCommon);
            }
        }
        if (jomresPropertyToRoomDataMap.isEmpty()) {
            logText("No Jomres room mapping found from database for this hotel, store id: " + this.storeId);
        }

        //TODO will change it to 5 minutes
        createScheduler("jomresprocessor", "*/5 * * * *", JomresManagerProcessor.class);
    }

    public void logText(String string) {
        jomresLogManager.save(string, System.currentTimeMillis());
        logPrint(string);
    }

    @Override
    public List<JomresLog> getLogEntries() {
        return jomresLogManager.get().collect(Collectors.toList());
    }

    @Override
    public boolean testConnection() {
        return connectToApi();
    }

    public void invalidateToken() {
        cmfClientAccessToken = null;
    }

    public void checkIfUnauthorizedExceptionOccurred(Exception e) throws Exception {
        if (e.getMessage1().contains("code: 401") || e.getMessage1().contains("invalid token") || e.getMessage1().contains("unauthorized")) {
            logText("Invalid Token! Check credentials... Operation aborted..");
            invalidateToken();
            throw e;
        }
    }

    @Override
    public boolean changeCredentials(String clientId, String clientSecret) {
        jomresConfiguration.cmfRestApiClientId = clientId;
        jomresConfiguration.cmfRestApiClientSecret = clientSecret;
        saveObject(jomresConfiguration);
        return true;
    }

    private boolean handleEmptyJomresCOnfiguration() {
        if (jomresPropertyToRoomDataMap.isEmpty() || jomresConfiguration == null) {
            logger.info("No room to Jomres Property mapping found for this hotel. No need to fetch bookings...");
            logText("No room to Jomres Property mapping found for this hotel.");
            logText("No need to fecth bookings...");
            return true;
        }
        return false;
    }

    @Override
    public boolean updateAvailability() {
        if (!connectToApi()) return false;
        if (handleEmptyJomresCOnfiguration()) return false;
        logText("Started Jomres Update availability");
        logger.debug("Started Jomres Update availability");
        Calendar calendar = Calendar.getInstance();
        Date startDate = calendar.getTime();
        calendar.add(Calendar.DATE, 60);
        Date endDate = calendar.getTime();

        Set<String> jomresBookingRoomIds =
                jomresToPmsBookingMap.values().stream().map(o -> o.pmsRoomId).collect(Collectors.toSet());

        for (JomresRoomData roomData : pmsItemToJomresRoomDataMap.values()) {
            try {
                calendar.setTime(startDate);
                BookingItem bookingItem = bookingEngine.getBookingItem(roomData.bookingItemId);
                if (bookingItem == null) {
                    logText("The room is not found in Pms for Jomres PropertyId: " + roomData.jomresPropertyId
                            + ", pms bookingItemId: " + roomData.bookingItemId);
                    logText("Room is deleted from Pms or mapping is removed.");
                    logger.debug("The room is not found in Pms for Jomres PropertyId: " + roomData.jomresPropertyId
                            + ", pms bookingItemId: " + roomData.bookingItemId);
                    continue;
                }
                logger.debug("Started updating availability for room: " + bookingItem.bookingItemName +
                        ", PropertyId: " + roomData.jomresPropertyId);
                logText("Started updating availability for room: " + bookingItem.bookingItemName +
                        ", PropertyId: " + roomData.jomresPropertyId);

                Map<String, PMSBlankBooking> blankBookings = getBlankBookingsForProperty(roomData.jomresPropertyId);

                calendar.setTime(startDate);
                BookingTimeLineFlatten itemTimeline = bookingEngine.getTimeLinesForItem(startDate, endDate, roomData.bookingItemId);
                Set<String> existingBookingIds = new HashSet<>();
                List<Booking> bookings = itemTimeline.getBookings();
                Collections.sort(bookings, Booking.sortByStartDate());

                for (Booking booking : itemTimeline.getBookings()) {
                    PMSBlankBooking bBooking = blankBookings.get(booking.id);
                    if (jomresBookingRoomIds.contains(booking.externalReference)) continue;
                    if (bBooking == null) {
                        createBlankBooking(booking, roomData.jomresPropertyId);
                    } else if (isBlankBookingUpdated(bBooking, booking)) {
                        deleteBlankBookingCompletely(bBooking);
                        createBlankBooking(booking, roomData.jomresPropertyId);
                    }
                    existingBookingIds.add(booking.id);
                }
                blankBookings = getBlankBookingsForProperty(roomData.jomresPropertyId);
                deleteIfExtraBlankBookingExist(existingBookingIds, blankBookings, startDate, endDate);
                logger.debug("Update availability ended");
                logText("Update availability ended");
            } catch (RuntimeException e) {
                logPrintException(e);
                logText("Failed to update availability for JomresPropertyId: " + roomData.jomresPropertyId
                        + ", PmsRoomId: " + roomData.bookingItemId);
                logger.debug("Failed to update availability for JomresPropertyId: " + roomData.jomresPropertyId
                        + ", PmsRoomId: " + roomData.bookingItemId);
            }
        }
        return true;
    }

    private Map<String, PMSBlankBooking> getBlankBookingsForProperty(int propertyId) {
        if (pmsBlankBookings.get(propertyId) == null)
            return new HashMap<>();
        else
            return pmsBlankBookings.get(propertyId)
                    .stream()
                    .sorted(Comparator.comparing(PMSBlankBooking::getDateFrom))
                    .collect(Collectors
                            .toMap(PMSBlankBooking::getFlatBookingId,
                                    Function.identity(),
                                    (oldValue, newValue) -> oldValue,
                                    LinkedHashMap::new)
                    );
    }

    private boolean isBlankBookingUpdated(PMSBlankBooking blankBooking, Booking booking) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if (blankBooking.getDateFrom().compareTo(formatter.format(booking.startDate)) != 0) return true;
        if (blankBooking.getDateTo().compareTo(formatter.format(booking.endDate)) != 0) return true;
        return booking.bookingDeleted;
    }

    private void createBlankBooking(Booking booking, int propertyId) {
        if (booking.bookingDeleted) return;
        AvailabilityService service = new AvailabilityService();
        logger.debug("Creating new blankBooking..");
        UpdateAvailabilityResponse response = service.createBlankBooking(jomresConfiguration.clientBaseUrl,
                cmfClientAccessToken, jomresConfiguration.channelName, propertyId, booking);
        if (!response.isSuccess()) {
            sendErrorUpdateAvailability(response);
            return;
        }
        PMSBlankBooking newBlankBooking =
                new PMSBlankBooking(response.getContractId(), booking.id, propertyId, booking.startDate, booking.endDate);
        saveObject(newBlankBooking);
        pmsBlankBookings.put(propertyId, pmsBlankBookings.computeIfAbsent(propertyId, k -> new HashSet<>()));
        pmsBlankBookings.get(propertyId).add(newBlankBooking);
    }

    private void deleteBlankBookingCompletely(PMSBlankBooking booking) {
        logger.debug("Deleting blank Booking...");
        AvailabilityService service = new AvailabilityService();
        UpdateAvailabilityResponse res =
                service.deleteBlankBooking(jomresConfiguration.clientBaseUrl, cmfClientAccessToken, booking);
        if (!res.isSuccess()) {
            sendErrorUpdateAvailability(res);
            if (!isBlankBookingNeedToDeleteFromDb(res)) return;
        }
        deleteObject(booking);
        pmsBlankBookings.get(booking.getPropertyId()).remove(booking);
    }

    private boolean isBlankBookingNeedToDeleteFromDb(UpdateAvailabilityResponse res) {
        return (StringUtils.isNotBlank(res.getMessage()) && res.getMessage().contains("does not exist"));
    }

    private void deleteIfExtraBlankBookingExist(
            Set<String> existingBookingIds, Map<String, PMSBlankBooking> blankBookingMap, Date start, Date end) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = formatter.format(start);
        String endDate = formatter.format(end);
        List<PMSBlankBooking> bBookingsForDeletion = blankBookingMap.values()
                .stream()
                .filter(o -> o.getDateFrom().compareTo(endDate) <= 0 && o.getDateTo().compareTo(startDate) >= 0)
                .filter(o -> !existingBookingIds.contains(o.getFlatBookingId()))
                .collect(Collectors.toList());
        for (PMSBlankBooking booking : bBookingsForDeletion) {
            deleteBlankBookingCompletely(booking);
        }
    }

    private FetchBookingResponse handleInvalidRoomForBooking(JomresBooking booking, FetchBookingResponse response) throws Exception {
        JomresRoomData jomresRoomData = jomresPropertyToRoomDataMap.get(booking.propertyUid);
        if (jomresRoomData == null) {
            logger.debug("The room mapping is not found in Pms for Jomres BookingId: " + booking.bookingId);
            logger.debug("Property Id: " + booking.propertyUid);
            throw new Exception("Message: Room Map is not Found in PMS");
        }
        String bookingItemId = jomresRoomData.bookingItemId;

        BookingItem pmsBookingItem = bookingEngine.getBookingItem(bookingItemId);

        if (pmsBookingItem == null) {
            logger.debug("The room is not found in Pms for Jomres BookingId: " + booking.bookingId + ", PMS BookingItemId: "
                    + jomresRoomData.bookingItemId);
            throw new Exception("Message: Room is not Found (Maybe Deleted) in PMS");
        }
        response.setPmsRoomName(pmsBookingItem.bookingItemName);
        BookingItemType pmsRoomCategory = bookingEngine.getBookingItemType(pmsBookingItem.bookingItemTypeId);
        if (pmsRoomCategory == null) {
            logger.debug("The room category is not found in Pms for Jomres BookingId: " + booking.bookingId + ", PMS BookingItemTypeId: "
                    + pmsBookingItem.bookingItemTypeId);
            throw new Exception("Message: Room Type is not Found (Maybe Deleted) in PMS");
        }
        response.setPmsRoomCategoryName(pmsRoomCategory.name);
        return response;
    }

    private JomresBookingData addNewJomresBooking(JomresBooking booking, Map<String, Double> dailyPriceMatrix) throws Exception {
        logger.debug("started fetch complete booking, BookingId: " + booking.bookingId);
        BookingService bookingService = new BookingService();

        booking = bookingService.getCompleteBooking(
                jomresConfiguration.clientBaseUrl,
                cmfClientAccessToken,
                jomresConfiguration.channelName,
                booking
        );
        logger.debug("Ended fetch complete booking, BookingId: " + booking.bookingId);

        logger.debug("Started adding Booking into pms BookingId: " + booking.bookingId);
        BookingItem pmsBookingItem = bookingEngine.getBookingItem(jomresPropertyToRoomDataMap.get(booking.propertyUid).bookingItemId);
        JomresBookingData jomresBookingData = addBookingToPms(booking, dailyPriceMatrix, null, pmsBookingItem.bookingItemTypeId);
        logger.debug("ended adding Booking into pms BookingId: " + booking.bookingId);
        return jomresBookingData;
    }

    public List<FetchBookingResponse> fetchBookings() throws Exception {
        if (!connectToApi()) return new ArrayList<>();
        if (handleEmptyJomresCOnfiguration()) return new ArrayList<>();
        BookingService bookingService = new BookingService();
        PriceService priceService = new PriceService();

        Set<Integer> propertyUIDs = jomresPropertyToRoomDataMap.keySet();
        List<JomresBooking> bookings;
        List<FetchBookingResponse> allBookings = new ArrayList<>();
        Map<String, Double> dailyPriceMatrix;

        Date start = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        calendar.add(Calendar.DATE, 60);
        Date end = calendar.getTime();

        logText("FetchBooking process for 60 days is starting...");
        logger.debug("FetchBooking process for 60 days is starting...");

        for (int propertyUID : propertyUIDs) {
            try {
                bookings = bookingService.getJomresBookingsBetweenDates(
                        jomresConfiguration.clientBaseUrl,
                        propertyUID,
                        cmfClientAccessToken,
                        start, end
                );
                dailyPriceMatrix = priceService.getDailyPrice(jomresConfiguration.clientBaseUrl, cmfClientAccessToken,
                        jomresConfiguration.channelName, propertyUID);

                for (JomresBooking jomresBooking : bookings) {
                    logger.debug("Started Syncing Booking Id: " + jomresBooking.bookingId + ", PropertyId: " + propertyUID);
                    FetchBookingResponse response = new FetchBookingResponse();
                    try {
                        response.setBookingId(jomresBooking.bookingId);
                        response.setGuestName(jomresBooking.customer.name);
                        response.setArrivalDate(jomresBooking.arrivalDate);
                        response.setDepartureDate(jomresBooking.departure);
                        response = handleInvalidRoomForBooking(jomresBooking, response);
                        JomresBookingData jomresBookingData = jomresToPmsBookingMap.get(jomresBooking.bookingId);
                        PmsBooking pmsBooking = findCorrelatedBooking(jomresBookingData);


                        if (jomresBooking.status.equals("Cancelled") || jomresBooking.statusCode == 6) {
                            response.setStatus("Cancelled");
                            response.setPmsBookingId(pmsBooking == null ? pmsBooking.id : "");
                            logger.debug("Started deleting Booking Id: " + jomresBooking.bookingId);
                            deletePmsBooking(jomresBooking);
                            logger.debug("ended deleting Booking Id: " + jomresBooking.bookingId);
                            allBookings.add(response);
                            continue;
                        }
                        if (pmsBooking == null) {
                            response.setStatus("Added");
                            jomresBookingData = addNewJomresBooking(jomresBooking, dailyPriceMatrix);
                        } else if (pmsBooking.rooms.get(0).deleted) {
                            response.setStatus("Added");
                            deleteJomresBookingData(jomresBookingData);
                            deletePmsBooking(jomresBooking);
                            jomresBookingData = addNewJomresBooking(jomresBooking, dailyPriceMatrix);
                        } else {
                            response.setStatus("Modified/Synced");
                            updatePmsBookingNew(jomresBooking, pmsBooking, dailyPriceMatrix);
                        }
                    } catch (Exception e) {
                        String errorMessage = "Failed to Sync/Add booking, BookingId: " + jomresBooking.bookingId + ", PropertyId: " + jomresBooking.propertyUid;
                        logPrintException(e);
                        logText(e.getMessage1());
                        logText(errorMessage);
                        logger.error(errorMessage);
                        sendErrorForBooking(jomresBooking, response.getPmsRoomName());
                        response.setStatus("Ignored");
                        allBookings.add(response);
                    } catch (java.lang.Exception e) {
                        String errorMessage = "Failed to Sync/Add booking, BookingId: " + jomresBooking.bookingId + ", PropertyId: " + jomresBooking.propertyUid;
                        logPrintException(e);
                        logText(errorMessage);
                        logger.error(errorMessage);
                        sendErrorForBooking(jomresBooking, response.getPmsRoomName());
                        response.setStatus("Ignored");
                        allBookings.add(response);
                    }
                }
            } catch (Exception e) {
                logText(e.getMessage1());
                logText("Booking synchronization has been failed for property id: " + propertyUID);
                logger.error("Booking synchronization has been failed for property id: " + propertyUID);
                logPrintException(e);
                invalidateToken();
            }
        }
        return null;
    }

    boolean isGuestInfoUpdated(JomresGuest customer, PmsBooking booking) {
        PmsBookingRooms room = Optional.ofNullable(booking.rooms.get(0)).orElse(new PmsBookingRooms());
        PmsGuests guest = Optional.ofNullable(room.guests.get(0)).orElse(new PmsGuests());
        String guestEmail = Optional.ofNullable(guest.email).orElse("");
        String guestPhone = Optional.ofNullable(guest.phone).orElse("");

        if (!customer.telMobile.contains(guestPhone))
            return true;
        return !customer.email.equals(guestEmail);
    }
    private void deleteJomresBookingData(JomresBookingData bookingData) {
        pmsToJomresBookingMap.remove(bookingData.pmsBookingId);
        jomresToPmsBookingMap.remove(bookingData.jomresBookingId);
        deleteObject(bookingData);
    }
    private PmsGuests mapJomresGuestToPmsGuest(JomresGuest jGuest, PmsGuests pGuest) {
        pGuest.email = jGuest.email;
        pGuest.name = jGuest.name;
        pGuest.phone = jGuest.telMobile;
        return pGuest;
    }

    private PmsBooking updateJomresGuestInfo(JomresBooking jBooking, PmsBooking pBooking) {
        pBooking.registrationData.resultAdded.put("user_fullName", jBooking.customer.name);
        pBooking.registrationData.resultAdded.put("user_cellPhone", jBooking.customer.telMobile);
        pBooking.registrationData.resultAdded.put("user_address_address", jBooking.customer.address);
        pBooking.registrationData.resultAdded.put("user_address_city", jBooking.customer.city);
        pBooking.registrationData.resultAdded.put("user_emailAddress", jBooking.customer.email);
        pBooking.registrationData.resultAdded.put("user_address_postCode", jBooking.customer.postcode);
        PmsBookingRooms pmsRoom = pBooking.rooms.get(0);
        List<PmsGuests> updatedGuestList = new ArrayList<>();
        updatedGuestList.add(mapJomresGuestToPmsGuest(jBooking.customer, pmsRoom.guests.get(0)));
        pmsManager.setGuestOnRoomWithoutModifyingAddons(updatedGuestList, pBooking.id, pmsRoom.pmsBookingRoomId);
        pmsManager.saveBooking(pBooking);
        return pBooking;
    }

    private boolean isJStayDateChanged(PmsBookingRooms pmsRoom, Date jArrival, Date jDeparture) {
        if (!DateUtils.isSameDay(pmsRoom.date.start, jArrival)) return true;
        return !DateUtils.isSameDay(pmsRoom.date.end, jDeparture);
    }

    private boolean isBookingRoomChanged(PmsBookingRooms pmsRoom, JomresBooking jBooking) {
        JomresRoomData roomData = jomresPropertyToRoomDataMap.get(jBooking.propertyUid);
        return pmsRoom.bookingItemId != roomData.bookingItemId;
    }

    private void handleJomresBookingPriceChange(Map<String, Double> priceMatrix){
        //TODO: Credit ORder
        //TODO: Debit order
    }

    private void updatePmsBookingNew(JomresBooking jBooking, PmsBooking pBooking, Map<String, Double> priceMatrix) throws Exception {
        try {
            if (isGuestInfoUpdated(jBooking.customer, pBooking)) {
                pBooking = updateJomresGuestInfo(jBooking, pBooking);
            }
            PmsBookingRooms pmsRoom = pBooking.rooms.get(0);
            if (isJStayDateChanged(pmsRoom, jBooking.arrivalDate, jBooking.departure)) {
                pmsManager.changeDates(pmsRoom.pmsBookingRoomId, pBooking.id,
                        setCorrectTime(jBooking.arrivalDate, true), setCorrectTime(jBooking.departure, false));
                pmsManager.saveBooking(pBooking);
                //TODO: credit order
                //TODO: debit order
                handleJomresBookingPriceChange(priceMatrix);
            }
            if (isBookingRoomChanged(pmsRoom, jBooking)) {
                pmsManager.setBookingItemAndDate(pmsRoom.pmsBookingRoomId, pmsRoom.bookingItemId, false,
                        setCorrectTime(jBooking.arrivalDate, true), setCorrectTime(jBooking.departure, false));
                pmsManager.saveBooking(pBooking);
                //TODO: Credit ORder
                //TODO: Debit order
                handleJomresBookingPriceChange(priceMatrix);

            }
        } catch (java.lang.Exception e) {
//            logText("Falied to update booking, Jomres booking Id: " + jBooking.bookingId + ", Jomres Property Id: " + jBooking.propertyUid);
            logPrintException(e);
            throw new Exception("Falied to update booking, Jomres booking Id: " + jBooking.bookingId + ", Jomres Property Id: " + jBooking.propertyUid);
        }
    }

    void deletePmsBooking(JomresBooking booking) throws java.lang.Exception {
        PmsBooking newbooking = findCorrelatedBooking(jomresToPmsBookingMap.get(booking.bookingId));
        if (newbooking == null) {
            logger.debug("Did not find booking to delete.");
            logText("Didn't find to delete, BookingId: " + booking.bookingId + ", PropertyId: " + booking.propertyUid);
            return;
        } else {
            pmsManager.logEntry("Deleted by channel manager", newbooking.id, null);
            pmsManager.deleteBooking(newbooking.id);
            deleteJomresBookingData(jomresToPmsBookingMap.get(booking.bookingId));
        }
        newbooking = pmsManager.getBooking(newbooking.id);
        List<String> orderIds = new ArrayList<>(newbooking.orderIds);
        for (String orderId : orderIds) {
            Order order = orderManager.getOrderSecure(orderId);
            if (order.isCreditNote || !order.creditOrderId.isEmpty()) {
                continue;
            }
            List<PmsBooking> bookings = pmsManager.getBookingsFromOrderId(orderId);
            if (bookings.size() > 1) {
                continue;
            }
            pmsInvoiceManager.creditOrder(newbooking.id, orderId);
        }
        return;

    }

    private Date setCorrectTime(Date arrivalDate, boolean start) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(arrivalDate);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        String starttime = pmsManager.getConfigurationSecure().getDefaultStart();
        if (!start) {
            starttime = pmsManager.getConfigurationSecure().getDefaultEnd();
        }

        String[] starting = starttime.split(":");
        cal.set(Calendar.HOUR_OF_DAY, new Integer(starting[0]));
        cal.set(Calendar.MINUTE, new Integer(starting[1]));

        return cal.getTime();
    }

    public boolean connectToApi() {
        if (jomresConfiguration == null) {
            logText("No Jomres configuration is found for this hotel");
            return false;
        }
        Date currentTime = new Date();
        long timeDiff = currentTime.getTime() - cmfClientTokenGenerationTime.getTime();
        try {
            if (cmfClientAccessToken == null || timeDiff > 1800000) {  //token is expired after 30 minutes
                logText("Generating new token...");
                cmfClientTokenGenerationTime = new Date();
                cmfClientAccessToken = jomresService.getAccessToken(
                        jomresConfiguration.cmfRestApiClientId,
                        jomresConfiguration.cmfRestApiClientSecret,
                        jomresConfiguration.cmfClientTokenUrl
                );
            }
            if (cmfClientAccessToken == null) {
                logText("Failed to connect with jomres...");
                logger.error("Failed to connect with jomres...");
                return false;
            } else {
                logger.info("Successfully connected with jomres.");
                logText("Successfully connected with jomres.");
                return true;
            }
        } catch (java.lang.Exception e) {
            logPrintException(e);
            logText("Failed to connect with Jomres, please see log files.");
            return false;
        }
    }

    private PmsBooking findCorrelatedBooking(JomresBookingData jomresBookingData) {
        if (jomresBookingData != null && StringUtils.isNotBlank(jomresBookingData.pmsBookingId)) {
            return pmsManager.getBooking(jomresBookingData.pmsBookingId);
        }
        if (jomresBookingData == null) deleteJomresBookingData(jomresBookingData);

        return null;
    }

    private void checkIfPaymentMethodIsActive(String pmethod) {
        if (!storeApplicationPool.isActivated(pmethod)) {
            storeApplicationPool.activateApplication(pmethod);
        }
    }

    String getJomresBookingErrorMessageForOwner(JomresBooking booking, String pmsRoomName) {
        String arrival, departure;
        SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy");
        arrival = format.format(booking.arrivalDate);
        departure = format.format(booking.departure);
        return "Failed to add new booking in pms from Jomres.\n" +
                "Booking Id: " + booking.bookingId + ", Room: " + pmsRoomName + ".\n" +
                "Arraival: " + arrival + ", Departure: " + departure + ".\n" +
                "Maybe there is a manual booking for this room or the room is closed by the system for a while.\n" +
                "Booking will be created in Pms Automatically as soon as the room is open.";
    }

    private void sendErrorForBooking(JomresBooking booking, String pmsRoomName) {
        boolean isSentErrorMail = pmsManager.hasSentErrorNotificationForJomresBooking(booking.bookingId);
        if (!isSentErrorMail) {
            String emailMessage = getJomresBookingErrorMessageForOwner(booking, pmsRoomName);
            String subject = "Jomres Booking Creation Failed";
            logger.debug("Sending Email...");
            logText("Error email is sending for Jomres bookingId: " + booking.bookingId + ", property id: " + booking.propertyUid);
            messageManager.sendJomresMessageToStoreOwner(
                    emailMessage, subject);
            logText("Sent email");
            logger.debug("Booking Error Email has been sent");
            pmsManager.markSentErrorMessageForJomresBooking(booking.bookingId);
            logger.debug("Marked that the mail has been sent for this booking");
        }
    }

    private void sendErrorUpdateAvailability(UpdateAvailabilityResponse response) {
        String hashValueForErrorAvailability = response.getPropertyId() + "";

        if (StringUtils.isNotBlank(response.getStart())) hashValueForErrorAvailability += response.getStart();
        if (StringUtils.isNotBlank(response.getEnd())) hashValueForErrorAvailability += response.getEnd();
        hashValueForErrorAvailability += response.isAvailable();

        String subject = response.isAvailable() ? "Blank Booking Deletion Failed" : "Blank Booking Creation Failed";

        if (!pmsManager.hasSentErrorNotificationForJomresAvailability(hashValueForErrorAvailability)) {
            logger.debug("Email is being sent...");
            String emailMessage = "Availability Update has been failed for a date range. \n" +
                    "Jomres Property UId: " + response.getPropertyId() + "\n" +
                    "Availability Start Date: " + response.getStart() + "\n+" +
                    "Availability End Date: " + response.getEnd() + "\n" +
                    "Property Availability in PMS: " + (response.isAvailable() ? "available" : "unavailable") + "\n\n" +
                    (StringUtils.isNotBlank(response.getMessage()) ? "Possible Reason: " + response.getMessage() : "") + "\n";

            if (!response.isAvailable()) {
                emailMessage += "Some other possible reason:\n" +
                        "   1. There is a booking in Jomres for this time period.\n" +
                        "   2. There is already a blank booking for this time period.\n";
            }

            messageManager.sendJomresMessageToStoreOwner(emailMessage, subject);
            logger.debug("Sent");
            pmsManager.markSentErrorMessageForJomresAvail(hashValueForErrorAvailability);
            logText("Update Availability Error email sent to Owner");
            logText("Email Message: " + emailMessage);
        }

    }

    private JomresBookingData addBookingToPms(JomresBooking booking, Map<String, Double> priceMatrix, PmsBooking newbooking,
                                              String pmsBookingItemTypeId) throws Exception {
        try {
            long start = System.currentTimeMillis();

            if (newbooking == null) {
                newbooking = pmsManager.startBooking();
            }

            for (PmsBookingRooms room : newbooking.getAllRooms()) {
                room.unmarkOverBooking();
            }
            newbooking.channel = "jomres_" + jomresConfiguration.channelName;
            newbooking.jomresBookingId = booking.bookingId;
            newbooking.countryCode = booking.customer.countryCode;
            newbooking.isPrePaid = true;

            if (!booking.comment.equals("")) {
                PmsBookingComment comment = new PmsBookingComment();
                comment.userId = "";
                comment.comment = booking.comment;
                comment.added = new Date();
                newbooking.comments.put(System.currentTimeMillis(), comment);
            }

            newbooking.registrationData.resultAdded.put("user_fullName", booking.customer.name);
            newbooking.registrationData.resultAdded.put("user_cellPhone", booking.customer.telMobile);
            newbooking.registrationData.resultAdded.put("user_address_address", booking.customer.address);
            newbooking.registrationData.resultAdded.put("user_address_city", booking.customer.city);
            newbooking.registrationData.resultAdded.put("user_emailAddress", booking.customer.email);
            newbooking.registrationData.resultAdded.put("user_address_postCode", booking.customer.postcode);
            newbooking.jomresLastModified = booking.lastModified;

            Calendar calStart = Calendar.getInstance();
            PmsBookingRooms room = new PmsBookingRooms();

            room.date = new PmsBookingDateRange();
            room.date.start = setCorrectTime(booking.arrivalDate, true);
            room.date.end = setCorrectTime(booking.departure, false);
            room.numberOfGuests = booking.numberOfGuests;
            room.bookingItemTypeId = pmsBookingItemTypeId;

            PmsGuests guest = new PmsGuests();
            guest.email = booking.customer.email;
            guest.name = booking.customer.name;
            guest.phone = booking.customer.telMobile;

            room.guests.add(guest);

            newbooking.rooms.clear();
            newbooking.addRoom(room);

            if (newbooking.rooms.isEmpty()) {
                logger.debug("Returning since there are no rooms to add id: " + booking.bookingId);
                return null;
            }

            checkIfPaymentMethodIsActive("70ace3f0-3981-11e3-aa6e-0800200c9a66");
            newbooking.paymentType = "70ace3f0-3981-11e3-aa6e-0800200c9a66";

            if (room.bookingItemTypeId == null) {
                logger.error("Failed to find room type for booking: " + booking.bookingId);
                logText("Failed to find room type for booking: " + booking.bookingId);
            }

            pmsManager.setBooking(newbooking);

            pmsInvoiceManager.clearOrdersOnBooking(newbooking);
            pmsManager.setBookingItem(
                    newbooking.rooms.get(0).pmsBookingRoomId,
                    newbooking.id,
                    jomresPropertyToRoomDataMap.get(booking.propertyUid).bookingItemId,
                    false
            );
            newbooking = pmsManager.doCompleteBooking(newbooking);

            boolean doNormalPricing = true;
            if (newbooking == null) {
                throw new Exception("Jomres Booking Completion Failed");
            }

            String orderId = null;

            if ((pmsManager.getConfigurationSecure().usePricesFromChannelManager || storeManager.isPikStore()) && newbooking != null && doNormalPricing) {
                Date end = new Date();
                calStart.setTime(room.date.start);

                if (room.date.end.after(end)) {
                    end = room.date.end;
                }
                Date current = room.date.start;
                calStart.setTime(current);
                while (!current.after(room.date.end)) {
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(current);
                    String offset = PmsBookingRooms.getOffsetKey(calStart, PmsBooking.PriceType.daily);
                    room.priceMatrix.put(offset, priceMatrix.get(currentDate));
                    calStart.add(Calendar.DATE, 1);
                    current = calStart.getTime();
                }
                room.price = booking.totalPrice;
                room.totalCost = booking.totalPrice;

                pmsManager.saveBooking(newbooking);
                NewOrderFilter filter = new NewOrderFilter();
                filter.createNewOrder = false;
                filter.prepayment = true;
                filter.endInvoiceAt = end;
                pmsInvoiceManager.clearOrdersOnBooking(newbooking);
                if (!newbooking.hasOverBooking()) {
                    if (newbooking.paymentType != null && !newbooking.paymentType.isEmpty()) {
                        orderId = pmsInvoiceManager.autoCreateOrderForBookingAndRoom(newbooking.id, newbooking.paymentType);
                    }
                } else {
                    newbooking.rowCreatedDate = new Date();
                    PmsBookingRooms newBookingRoom = newbooking.rooms.get(0);
                    if (newBookingRoom.isOverBooking()) {
                        try {
                            pmsManager.removeFromBooking(newbooking.id, newBookingRoom.pmsBookingRoomId);
                        } catch (java.lang.Exception e) {
                            //Okay, it failed, that's okay.
                        }
                    }
                    String text = "An overbooking occured go to your booking admin panel handle it.<br><bR><br>booking dump:<br>"
                            + pmsManager.dumpBooking(newbooking, true);
                    text += "<br><br>";
                    text += "For more information about overbooking, see: https://getshop.com/double_booking_error.html";
                    String content = "Possible overbooking happened:<br>" + text;
                    messageManager.sendJomresMessageToStoreOwner(content, "Warning: possible overbooking happened");
                }
            }
            if (orderId == null)
                orderId = pmsInvoiceManager.autoCreateOrderForBookingAndRoom(newbooking.id, newbooking.paymentType);
            pmsInvoiceManager.markOrderAsPaid(newbooking.id, orderId);

            JomresBookingData jomresBookingData = new JomresBookingData();
            jomresBookingData.pmsBookingId = newbooking.id;
            jomresBookingData.jomresBookingId = booking.bookingId;
            jomresBookingData.pmsRoomId = newbooking.rooms.get(0).pmsBookingRoomId;

            logger.debug("Time takes to complete one booking: " + (System.currentTimeMillis() - start) / 1000 + "s");
            return jomresBookingData;
        } catch (Exception e) {
            logger.error(e.getMessage1());
            logText(e.getMessage1());
            logPrintException(e);
            throw e;
        } catch (java.lang.Exception e) {
            logPrintException(e);
            throw new Exception("Unexpected Error while adding new booking");
        }
    }
}
