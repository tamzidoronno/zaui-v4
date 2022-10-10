package com.thundashop.core.jomres;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.mongodb.BasicDBObject;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.BookingTimeLineFlatten;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.jomres.dto.FetchBookingResponse;
import com.thundashop.core.jomres.dto.JomresBooking;
import com.thundashop.core.jomres.dto.JomresGuest;
import com.thundashop.core.jomres.dto.JomresProperty;
import com.thundashop.core.jomres.dto.PMSBlankBooking;
import com.thundashop.core.jomres.dto.UpdateAvailabilityResponse;
import com.thundashop.core.jomres.services.AvailabilityService;
import com.thundashop.core.jomres.services.BaseService;
import com.thundashop.core.jomres.services.BookingService;
import com.thundashop.core.jomres.services.PriceService;
import com.thundashop.core.jomres.services.PropertyService;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingComment;
import com.thundashop.core.pmsmanager.PmsBookingDateRange;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsGuests;
import com.thundashop.core.pmsmanager.PmsInvoiceManager;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.pmsmanager.TimeRepeaterData;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.repository.utils.SessionInfo;

@Component
@GetShopSession
public class JomresManager extends GetShopSessionBeanNamed implements IJomresManager {
    @Autowired Database db;
    @Autowired PmsManager pmsManager;
    @Autowired BookingEngine bookingEngine;
    @Autowired PmsInvoiceManager pmsInvoiceManager;
    @Autowired MessageManager messageManager;
    @Autowired StoreManager storeManager;
    @Autowired OrderManager orderManager;
    @Autowired JomresLogManager jomresLogManager;
    @Autowired StoreApplicationPool storeApplicationPool;

    private static final Logger logger = LoggerFactory.getLogger(JomresManager.class);

    BaseService jomresService = new BaseService();
    AvailabilityService availabilityService = new AvailabilityService();
    JomresConfiguration jomresConfiguration = new JomresConfiguration();

    Map<String, JomresRoomData> pmsItemToJomresRoomDataMap = new HashMap<>();
    Map<Integer, Set<PMSBlankBooking>> pmsBlankBookings = new HashMap<>();
    Map<Long, JomresBookingData> jomresToPmsBookingMap = new HashMap<>();
    Map<String, JomresBookingData> pmsToJomresBookingMap = new HashMap<>();
    Map<Integer, JomresRoomData> jomresPropertyToRoomDataMap = new HashMap<>();
    String cmfClientAccessToken = null;
    Date cmfClientTokenGenerationTime = new Date();
    private List<UpdateAvailabilityResponse> failedAvailabilityToSendEmail = new ArrayList<>();

    @Override
    public void initialize() throws SecurityException {
        super.initialize();
        stopScheduler("jomresFetchBooking");
        stopScheduler("jomresUpdateAvailability");

        createScheduler("jomresFetchBooking", "*/6 * * * *", JomresFetchBookingScheduler.class);
        createScheduler("jomresUpdateAvailability", "*/7 * * * *", JomresUpdateAvailabilityScheduler.class);
    }
    //Change this message if Jomres change their error message for delete request of non-existing black bookings
    String JOMRES_BLACK_BOOKING_DOES_NOT_EXIST_ERROR_MESSAGE = "Black booking does not exist";

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

    public void handleIfUnauthorizedExceptionOccurred(Exception e) throws Exception {
        if (e.getMessage().contains("code: 401") || e.getMessage().contains("invalid token") || e.getMessage().contains("unauthorized")) {
            logText("Invalid Token! Check credentials... Operation aborted..");
            invalidateToken();
            throw e;
        }
    }

    private void deleteAllDbObjectWithSameClass(DataCommon sampleData) {
        if (sampleData == null) return;
        SessionInfo session = getSessionInfo();
        BasicDBObject queryObect = getQueryObjectWithClassName(sampleData);
        String collectionPrefix = "col_";
        db.getMongo().getDB(session.getManagerName())
            .getCollection(collectionPrefix + session.getStoreId())
            .remove(queryObect);
    }

    @Override
    public boolean changeConfiguration(JomresConfiguration newConfiguration) {
        deleteAllDbObjectWithSameClass(jomresConfiguration);
        jomresConfiguration.updateConfiguration(newConfiguration);
        saveObject(jomresConfiguration);
        invalidateToken();
        return true;
    }

    private boolean handleEmptyJomresConfiguration() {
        if (jomresPropertyToRoomDataMap.isEmpty() || jomresConfiguration == null) {
            logger.info("No room to Jomres Property mapping found for this hotel. No need to fetch bookings...");
            logText("No room to Jomres Property mapping found for this hotel.");
            logText("No need to fecth bookings...");
            return true;
        }
        return false;
    }

    @Override
    public List<JomresProperty> getJomresChannelProperties() {
        if (!connectToApi()) {
            return new ArrayList<>();
        }
        try {
            PropertyService propertyService = new PropertyService();
            List<Integer> propertyIds = propertyService.getChannelsPropertyIDs(
                    jomresConfiguration.clientBaseUrl, cmfClientAccessToken, jomresConfiguration.channelName
            );
            return propertyService.getPropertiesFromIds(
                    jomresConfiguration.clientBaseUrl, cmfClientAccessToken, jomresConfiguration.channelName, propertyIds);
        } catch (Exception e) {
            logPrintException(e);
            logText("Failed to load Jomres Properties..." + e.getMessage());
            return new ArrayList<>();
        }
    }

    void saveNewRoomData(JomresRoomData roomData) {
        jomresPropertyToRoomDataMap.put(roomData.jomresPropertyId, roomData);
        pmsItemToJomresRoomDataMap.put(roomData.bookingItemId, roomData);
        saveObject(roomData);
    }

    private BasicDBObject getQueryObjectWithClassName(DataCommon sampleData) {
        BasicDBObject queryObject = new BasicDBObject();
        queryObject.append("className", sampleData.className);
        return queryObject;
    }

    void handleExistingRoomDataWhileMapping(JomresRoomData roomData) {
        JomresRoomData existingPropertyMapping = jomresPropertyToRoomDataMap.get(roomData.jomresPropertyId);
        if (existingPropertyMapping != null) {
            logText("Jomres Property Uid  " + roomData.jomresPropertyId + " is already mapped with roomId: " + roomData.bookingItemId);
            logText("Deleted existing room mapping for Property Id: " + roomData.jomresPropertyId);
        }
        JomresRoomData existingItemMapping = pmsItemToJomresRoomDataMap.get(roomData.bookingItemId);
        if (existingItemMapping != null) {
            logText("Pms room id " + roomData.bookingItemId + " is already mapped with Jomres property id " + roomData.jomresPropertyId);
            logText("Deleted existing room mapping for pms room id: " + roomData.bookingItemId);
        }
    }

    private void deleteExistingMapping() {
        if (jomresPropertyToRoomDataMap != null && !jomresPropertyToRoomDataMap.isEmpty()) {
            JomresRoomData firstRoomData = jomresPropertyToRoomDataMap.values().stream().findFirst().get();
            deleteAllDbObjectWithSameClass(firstRoomData);
        }
        jomresPropertyToRoomDataMap = new HashMap<>();
        pmsItemToJomresRoomDataMap = new HashMap<>();
    }

    @Override
    public boolean saveMapping(List<JomresRoomData> mappingRoomData) {
        if(!jomresConfiguration.isEnable){
            logger.info("Jomres connection is disabled");
            return false;
        }
        deleteExistingMapping();
        for (JomresRoomData roomData : mappingRoomData) {
            handleExistingRoomDataWhileMapping(roomData);
            saveNewRoomData(roomData);
        }
        return true;
    }

    @Override
    public JomresConfiguration getConfigurationData() {
        return jomresConfiguration;
    }

    @Override
    public List<JomresRoomData> getMappingData() {
        if(!jomresConfiguration.isEnable){
            logger.info("Jomres connection is disabled");
            return new ArrayList<>();
        }
        return new ArrayList<>(jomresPropertyToRoomDataMap.values());
    }

    @Override
    public boolean updateAvailability() throws Exception {
        LocalTime startTime = LocalTime.now();
        if (!connectToApi()) return false;
        if (handleEmptyJomresConfiguration()) return false;
        logText("Started Jomres Update availability");
        logger.info("Started Jomres Update availability");
        Calendar calendar = Calendar.getInstance();
        Date startDate = calendar.getTime();
        calendar.add(Calendar.DATE, 60);
        Date endDate = calendar.getTime();

        for (JomresRoomData roomData : pmsItemToJomresRoomDataMap.values()) {
            try {
                calendar.setTime(startDate);
                BookingItem bookingItem = bookingEngine.getBookingItem(roomData.bookingItemId);
                if (bookingItem == null) {
                    logText("The room is not found in Pms for Jomres PropertyId: " + roomData.jomresPropertyId
                            + ", pms bookingItemId: " + roomData.bookingItemId);
                    logText("Room is deleted from Pms or mapping is removed.");
                    logger.info("The room is not found in Pms for Jomres PropertyId: {}, pms bookingItemId: {}",
                            roomData.jomresPropertyId, roomData.bookingItemId);
                    continue;
                }
                logger.info("Started updating availability for room: {}, PropertyId: {}", bookingItem.bookingItemName, roomData.jomresPropertyId);
                logText("Started updating availability for room: " + bookingItem.bookingItemName +
                        ", PropertyId: " + roomData.jomresPropertyId);

                calendar.setTime(startDate);

                Set<String> existingBookingIds = updateAndGetPmsBookingIdsOfBlankBookings(
                        startDate, endDate, roomData.jomresPropertyId, roomData.bookingItemId);

                Map<String, PMSBlankBooking> blankBookings = getBlankBookingsForProperty(roomData.jomresPropertyId);
                deleteIfExtraBlankBookingExist(existingBookingIds, blankBookings, startDate, endDate);
                sendEmailForFailedAvailResponses();
                logger.info("Update availability ended");
                logText("Update availability ended");
            } catch (Exception e) {
                logPrintException(e);
                BookingItem item = bookingEngine.getBookingItem(roomData.bookingItemId);
                logText("Failed to update availability for JomresPropertyId: " + roomData.jomresPropertyId
                        + ", Room/RoomType Name: " + item.bookingItemName);
                logger.info("Failed to update availability for JomresPropertyId: {}, PmsRoomId: {}, Room/RoomType Name: {}",
                        roomData.jomresPropertyId, roomData.bookingItemId, item.bookingItemName);
                handleIfUnauthorizedExceptionOccurred((Exception) e);
                logText("Possible Reason: "+((Exception) e).getMessage());

            }
        }
        LocalTime endTime = LocalTime.now();
        logger.info("Time takes to update availability: {}s", ChronoUnit.SECONDS.between(startTime, endTime));
        return true;
    }

    private Set<String> updateAndGetPmsBookingIdsOfBlankBookings(Date start, Date end, int propertyId, String bookingItemId) throws Exception, ParseException {
        Set<String> jomresBookingRoomIds =
                jomresToPmsBookingMap.values().stream().map(o -> o.pmsRoomId).collect(Collectors.toSet());

        Map<String, PMSBlankBooking> blankBookings = getBlankBookingsForProperty(propertyId);
        BookingTimeLineFlatten itemTimeline = bookingEngine.getTimeLinesForItem(start, end, bookingItemId);
        List<Supplier<?>> tasks = new ArrayList<>();

        for (Booking booking : itemTimeline.getBookings()) {
            PMSBlankBooking bBooking = blankBookings.get(booking.id);
            if (jomresBookingRoomIds.contains(booking.externalReference)) continue;
            tasks.add( ()-> updateAvailabilityForPmsBooking(
                                    bBooking, propertyId, booking.id, booking.bookingDeleted, booking.startDate, booking.endDate)
            );
        }
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date startBegin = df.parse(df.format(start));
        Date endBegin = df.parse(df.format(end));
        List<TimeRepeaterData> closedPeriods = pmsManager.getConfiguration().closedOfPeriode
                .stream()
                .filter(tData -> !startBegin.after(tData.firstEvent.end) && !endBegin.before(tData.firstEvent.start))
                .collect(Collectors.toList());
        for(TimeRepeaterData globalCloser: closedPeriods){
            PMSBlankBooking bBooking = blankBookings.get(globalCloser.repeaterId);
            tasks.add( () -> updateAvailabilityForPmsBooking(bBooking, propertyId, globalCloser.repeaterId, false,
                    globalCloser.firstEvent.start, globalCloser.firstEvent.end)
            );
        }
        List<CompletableFuture<?>> results = jomresService.getAsyncTaskResults(tasks);
        return availabilityService.getPmsBookingIdsFromFutureResults(results);
    }

    private String updateAvailabilityForPmsBooking(
            PMSBlankBooking bBooking, int propertyId, String bookingId, boolean isDeleted, Date start, Date end) {
        logger.info("Started Update Availability to Jomres (Async) {}", LocalTime.now());
        if (bBooking != null && isBlankBookingUpdated(bBooking, isDeleted, start, end)) {
            deleteBlankBookingCompletely(bBooking);
        }
        createBlankBooking(bookingId, propertyId, isDeleted, start, end);
        logger.info("Ended Update Availability to Jomres (Async) {}", LocalTime.now());
        return bookingId;
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

    private boolean isBlankBookingUpdated(PMSBlankBooking blankBooking, boolean isDeleted, Date start, Date end) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if (blankBooking.getDateFrom().compareTo(formatter.format(start)) != 0) return true;
        if (blankBooking.getDateTo().compareTo(formatter.format(end)) != 0) return true;
        return isDeleted;
    }

    private void createBlankBooking(String bookingId, int propertyId, boolean isDeleted, Date start, Date end) {
        if (isDeleted) return;
        logger.info("Creating new blankBooking..");
        UpdateAvailabilityResponse response = availabilityService.createBlankBooking(jomresConfiguration.clientBaseUrl,
                cmfClientAccessToken, jomresConfiguration.channelName, propertyId, start, end);
        if (!response.isSuccess()) {
            //error email will be sent after completing all tasks
            failedAvailabilityToSendEmail.add(response);
            return;
        }
        PMSBlankBooking newBlankBooking =
                new PMSBlankBooking(response.getContractId(), bookingId, propertyId, start, end);
        saveObject(newBlankBooking);
        pmsBlankBookings.put(propertyId, pmsBlankBookings.computeIfAbsent(propertyId, k -> new HashSet<>()));
        pmsBlankBookings.get(propertyId).add(newBlankBooking);
    }

    private void deleteBlankBookingCompletely(PMSBlankBooking booking) {
        logger.info("Deleting blank Booking...");
        UpdateAvailabilityResponse res =
                availabilityService.deleteBlankBooking(jomresConfiguration.clientBaseUrl, cmfClientAccessToken, booking);
        if (!res.isSuccess()) {
            //error email will be sent after completing all tasks
            failedAvailabilityToSendEmail.add(res);

            //if the blank booking is already deleted from Jomres, we are synced anyhow
            //otherwise we won't delete this blank booking id so that try to delete from Jomres in the next schedule
            if (!isBlankBookingNeedToDeleteFromDb(res)) return;
        }
        deleteObject(booking);
        pmsBlankBookings.get(booking.getPropertyId()).remove(booking);
        logger.info("Deleted Unnecessary Blank Bookings");
    }

    private boolean isBlankBookingNeedToDeleteFromDb(UpdateAvailabilityResponse res) {
        return (StringUtils.isNotBlank(res.getMessage()) &&
                res.getMessage().toLowerCase()
                        .contains(JOMRES_BLACK_BOOKING_DOES_NOT_EXIST_ERROR_MESSAGE.toLowerCase()));
    }

    private void deleteIfExtraBlankBookingExist(
            Set<String> existingBookingIds, Map<String, PMSBlankBooking> blankBookingMap, Date start, Date end)  throws Exception{
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = formatter.format(start);
        String endDate = formatter.format(end);
        List<PMSBlankBooking> bBookingsForDeletion = blankBookingMap.values()
                .stream()
                .filter(o -> o.getDateFrom().compareTo(endDate) <= 0 && o.getDateTo().compareTo(startDate) >= 0)
                .filter(o -> !existingBookingIds.contains(o.getFlatBookingId()))
                .collect(Collectors.toList());
        ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Runnable> tasks = new ArrayList<>();
        for (PMSBlankBooking booking : bBookingsForDeletion) {
            tasks.add(()-> deleteBlankBookingCompletely(booking));
        }
        CompletableFuture<?>[] futures = tasks.stream()
                .map(task -> CompletableFuture.runAsync(task, es))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        es.shutdown();
    }

    private FetchBookingResponse handleInvalidRoomForBooking(JomresBooking booking, FetchBookingResponse response) throws Exception {
        JomresRoomData jomresRoomData = jomresPropertyToRoomDataMap.get(booking.propertyUid);
        if (jomresRoomData == null) {
            logger.info("The room mapping is not found in Pms for Jomres BookingId: {}", booking.bookingId);
            logger.info("Property Id: {}", booking.propertyUid);
            throw new Exception("Message: Room Map is not Found in PMS");
        }
        String bookingItemId = jomresRoomData.bookingItemId;

        BookingItem pmsBookingItem = bookingEngine.getBookingItem(bookingItemId);

        if (pmsBookingItem == null) {
            logger.info("The room is not found in Pms for Jomres BookingId: {}, PMS BookingItemId: {}", booking.bookingId,
                    jomresRoomData.bookingItemId);
            throw new Exception("Message: Room is not Found (Maybe Deleted) in PMS");
        }
        response.setPmsRoomName(pmsBookingItem.bookingItemName);
        BookingItemType pmsRoomCategory = bookingEngine.getBookingItemType(pmsBookingItem.bookingItemTypeId);
        if (pmsRoomCategory == null) {
            logger.info("The room category is not found in Pms for Jomres BookingId: {}, PMS BookingItemTypeId: {}"
                    , booking.bookingId, pmsBookingItem.bookingItemTypeId);
            throw new Exception("Message: Room Type is not Found (Maybe Deleted) in PMS");
        }
        response.setPmsRoomCategoryName(pmsRoomCategory.name);
        return response;
    }

    private String addNewJomresBooking(JomresBooking booking, Map<String, Double> dailyPriceMatrix) throws Exception {
        logger.info("started fetch complete booking, BookingId: {}", booking.bookingId);
        BookingService bookingService = new BookingService();

        booking = bookingService.getCompleteBooking(
                jomresConfiguration.clientBaseUrl,
                cmfClientAccessToken,
                jomresConfiguration.channelName,
                booking
        );
        logger.info("Ended fetch complete booking, BookingId: {}", booking.bookingId);

        logger.info("Started adding Booking into pms BookingId: {}", booking.bookingId);
        BookingItem pmsBookingItem = bookingEngine.getBookingItem(jomresPropertyToRoomDataMap.get(booking.propertyUid).bookingItemId);
        JomresBookingData jomresBookingData = addBookingToPms(booking, dailyPriceMatrix, pmsBookingItem.bookingItemTypeId);
        saveJomresBookingData(jomresBookingData);
        logger.info("ended adding Booking into pms BookingId: {}", booking.bookingId);
        return jomresBookingData.pmsBookingId;
    }

    public List<FetchBookingResponse> fetchBookings() throws Exception {
        LocalTime startTime = LocalTime.now();
        if (!connectToApi()) return new ArrayList<>();
        if (handleEmptyJomresConfiguration()) return new ArrayList<>();
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
        logger.info("FetchBooking process for 60 days is starting...");
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
                    logger.info("Started Syncing Booking Id: {}, PropertyId: {}",jomresBooking.bookingId, propertyUID);
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
                            response.setPmsBookingId(pmsBooking == null ? "" : pmsBooking.id);
                            logger.info("Started deleting Booking Id: {}", jomresBooking.bookingId);
                            deletePmsBooking(jomresBooking, pmsBooking);
                            logger.info("ended deleting Booking Id: {}", jomresBooking.bookingId);
                            allBookings.add(response);
                            continue;
                        }
                        if (pmsBooking == null) {
                            response.setStatus("Added");
                            response.setPmsBookingId(addNewJomresBooking(jomresBooking, dailyPriceMatrix));
                        } else if (pmsBooking.rooms.get(0).deleted) {
                            response.setStatus("Added");
                            deletePmsBooking(jomresBooking, pmsBooking);
                            response.setPmsBookingId(addNewJomresBooking(jomresBooking, dailyPriceMatrix));
                        } else {
                            response.setPmsBookingId(pmsBooking.id);
                            response.setStatus("Modified/Synced");
                            updatePmsBooking(jomresBooking, pmsBooking, dailyPriceMatrix);
                        }
                        allBookings.add(response);
                    } catch (Exception e) {
                        String errorMessage = "Failed to Sync/Add booking, BookingId: " + jomresBooking.bookingId + ", PropertyId: " + jomresBooking.propertyUid;
                        logPrintException(e);
                        logText(e.getMessage());
                        logText(errorMessage);
                        logger.error(errorMessage);
                        sendErrorForBooking(jomresBooking, response.getPmsRoomName());
                        response.setStatus("Ignored");
                        allBookings.add(response);
                        handleIfUnauthorizedExceptionOccurred(e);
                    }
                }
                logger.info("Booking has been synced for Jomres Property Id: {}", propertyUID);
            } catch (Exception e) {
                logger.error(e.getMessage());
                logPrintException(e);
                logText(e.getMessage());
                logText("Booking synchronization has been failed for property id: " + propertyUID);
                handleIfUnauthorizedExceptionOccurred(e);
            }
        }
        logText("Ended Jomres fetch bookings for 60 days");
        allBookings = allBookings.stream()
                .sorted(Comparator.comparingLong(FetchBookingResponse::getBookingId).reversed())
                .collect(Collectors.toList());

        LocalTime endTime = LocalTime.now();
        logger.info("Time takes to complete fetch bookings: {}s", ChronoUnit.SECONDS.between(startTime, endTime));
        return allBookings;

    }

    boolean isGuestInfoChanged(JomresGuest customer, PmsBooking booking) {
        PmsBookingRooms room = Optional.ofNullable(booking.rooms.get(0)).orElse(new PmsBookingRooms());
        PmsGuests guest = Optional.ofNullable(room.guests.get(0)).orElse(new PmsGuests());
        String guestEmail = Optional.ofNullable(guest.email).orElse("");
        String guestPhone = Optional.ofNullable(guest.prefix).orElse("") + Optional.ofNullable(guest.phone).orElse("");
        String customerPhone = customer.mobilePrefix + customer.telMobile;

        if (StringUtils.isNotBlank(customer.name) && StringUtils.isNotBlank(guest.name)
                && !customer.name.equals(guest.name))
            return true;

        if (!customerPhone.contains(guestPhone))
            return true;
        return !customer.email.equals(guestEmail);
    }

    private void deleteJomresBookingData(JomresBookingData bookingData) {
        pmsToJomresBookingMap.remove(bookingData.pmsBookingId);
        jomresToPmsBookingMap.remove(bookingData.jomresBookingId);
        deleteObject(bookingData);
    }

    private void saveJomresBookingData(JomresBookingData bookingData) {
        pmsToJomresBookingMap.put(bookingData.pmsBookingId, bookingData);
        jomresToPmsBookingMap.put(bookingData.jomresBookingId, bookingData);
        saveObject(bookingData);
    }

    private PmsBooking updateJomresGuestInfo(JomresGuest customer, PmsBooking pBooking) {
        pBooking.registrationData.resultAdded.put("user_fullName", customer.name);
        pBooking.registrationData.resultAdded.put("user_cellPhone", customer.telMobile);
        pBooking.registrationData.resultAdded.put("user_address_address", customer.address);
        pBooking.registrationData.resultAdded.put("user_address_city", customer.city);
        pBooking.registrationData.resultAdded.put("user_emailAddress", customer.email);
        pBooking.registrationData.resultAdded.put("user_address_postCode", customer.postcode);
        PmsBookingRooms pmsRoom = pBooking.rooms.get(0);
        // at first taking the old list, then will replace the first guest info with updated one
        List<PmsGuests> updatedGuestList = pmsRoom.guests;
        PmsGuests guest = pmsRoom.guests.get(0);
        //only 0th index contain the valid guest info, so replacing 0th guest with updated guest info
        updatedGuestList.set(0, getPmsGuestFromJomresCustomer(guest, customer));
        pmsManager.setGuestOnRoomWithoutModifyingAddons(updatedGuestList, pBooking.id, pmsRoom.pmsBookingRoomId);
        pmsManager.saveBooking(pBooking);
        return pBooking;
    }

    private boolean isStayDateChanged(PmsBookingRooms pmsRoom, Date jArrival, Date jDeparture) {
        if (!DateUtils.isSameDay(pmsRoom.date.start, jArrival)) return true;
        return !DateUtils.isSameDay(pmsRoom.date.end, jDeparture);
    }

    private boolean isBookingRoomChanged(PmsBookingRooms pmsRoom, JomresBooking jBooking) {
        JomresRoomData roomData = jomresPropertyToRoomDataMap.get(jBooking.propertyUid);
        return !pmsRoom.bookingItemId.equals(roomData.bookingItemId);
    }

    private void handleJomresBookingPriceChange(
            PmsBooking pmsBooking, Double oldTotPrice, Double currentTotPrice, Map<String, Double> priceMatrix) {

        setBookingPrice(pmsBooking, currentTotPrice, priceMatrix);
        double currentPrice = pmsBooking.getTotalPrice();
        if(oldTotPrice != currentPrice)
            createNewOrder(pmsBooking.id, pmsBooking.paymentType);
    }

    private void createNewOrder(String pmsBookingId, String paymentType) {
        String orderId = pmsInvoiceManager.autoCreateOrderForBookingAndRoom(pmsBookingId, paymentType);
        pmsInvoiceManager.markOrderAsPaid(pmsBookingId, orderId);
    }

    private void updatePmsBooking(JomresBooking jBooking, PmsBooking pBooking, Map<String, Double> priceMatrix) throws Exception {
        try {
            if (isGuestInfoChanged(jBooking.customer, pBooking)) {
                pBooking = updateJomresGuestInfo(jBooking.customer, pBooking);
            }
            PmsBookingRooms pmsRoom = pBooking.rooms.get(0);
            if (isStayDateChanged(pmsRoom, jBooking.arrivalDate, jBooking.departure)) {
                Double oldTotPrice = pBooking.getTotalPrice();
                pmsRoom = pmsManager.changeDates(pmsRoom.pmsBookingRoomId, pBooking.id,
                        setCorrectTime(jBooking.arrivalDate, true), setCorrectTime(jBooking.departure, false));
                if (pmsRoom == null)
                    throw new Exception("Failed to update Checkin/out date for booking id: " + jBooking.bookingId);
                handleJomresBookingPriceChange(pBooking, oldTotPrice, jBooking.totalPrice, priceMatrix);
            }
            if (isBookingRoomChanged(pmsRoom, jBooking)) {
                Double oldTotPrice = pBooking.getTotalPrice();
                String newBookingItemId = jomresPropertyToRoomDataMap.get(jBooking.propertyUid).bookingItemId;
                pmsManager.setBookingItemAndDate(pmsRoom.pmsBookingRoomId, newBookingItemId, false,
                        setCorrectTime(jBooking.arrivalDate, true), setCorrectTime(jBooking.departure, false));
                pmsManager.saveBooking(pBooking);
                handleJomresBookingPriceChange(pBooking, oldTotPrice, jBooking.totalPrice, priceMatrix);
            }
        } catch (java.lang.Exception e) {
            logPrintException(e);
            throw new Exception("Falied to update booking, Jomres booking Id: " + jBooking.bookingId + ", Jomres Property Id: " + jBooking.propertyUid);
        }
    }

    void deletePmsBooking(JomresBooking booking, PmsBooking newbooking) {
        if (newbooking == null) {
            logger.info("Didn't find to delete, BookingId: {}, PropertyId: {}", booking.bookingId, booking.propertyUid);
            return;
        }
        pmsManager.logEntry("Deleted by channel manager", newbooking.id, null);
        pmsManager.deleteBooking(newbooking.id);

        deleteJomresBookingData(jomresToPmsBookingMap.get(booking.bookingId));
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
        if (!jomresConfiguration.isEnable) {
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
        } catch (Exception e) {
            logPrintException(e);
            logText("Failed to connect with Jomres, please see log files.");
            return false;
        }
    }

    private PmsBooking findCorrelatedBooking(JomresBookingData jomresBookingData) {
        if (jomresBookingData != null && StringUtils.isNotBlank(jomresBookingData.pmsBookingId)) {
            return pmsManager.getBooking(jomresBookingData.pmsBookingId);
        }
        if (jomresBookingData != null) deleteJomresBookingData(jomresBookingData);

        return null;
    }

    private void checkIfPaymentMethodIsActive(String pMethod) {
        if (!storeApplicationPool.isActivated(pMethod)) {
            storeApplicationPool.activateApplication(pMethod);
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
            logger.info("Sending Email...");
            logText("Error email is sending for Jomres bookingId: " + booking.bookingId + ", property id: " + booking.propertyUid);
            messageManager.sendJomresMessageToStoreOwner(
                    emailMessage, subject);
            logText("Sent email");
            logger.info("Booking Error Email has been sent");
            pmsManager.markSentErrorMessageForJomresBooking(booking.bookingId);
            logger.info("Marked that the mail has been sent for this booking");
        }
    }

    void sendEmailForFailedAvailResponses() {
        for(UpdateAvailabilityResponse response: failedAvailabilityToSendEmail){
            sendErrorUpdateAvailability(response);
        }
        //All emails have been sent, so the list need to be empty
        failedAvailabilityToSendEmail.clear();
    }

    private void sendErrorUpdateAvailability(UpdateAvailabilityResponse response) {
        StringBuilder hashValueForErrorAvailBuilder = new StringBuilder();
        hashValueForErrorAvailBuilder.append(response.getPropertyId() + "");
        if (StringUtils.isNotBlank(response.getStart())) hashValueForErrorAvailBuilder.append(response.getStart());
        if (StringUtils.isNotBlank(response.getEnd())) hashValueForErrorAvailBuilder.append(response.getEnd());
        hashValueForErrorAvailBuilder.append(response.isAvailable());
        String hashValueForErrorAvail = hashValueForErrorAvailBuilder.toString();
        if (pmsManager.hasSentErrorNotificationForJomresAvailability(hashValueForErrorAvail))
            return;
        String bookingItemId = jomresPropertyToRoomDataMap.get(response.getPropertyId()).bookingItemId;
        String bookingItemName = bookingEngine.getBookingItem(bookingItemId).bookingItemName;
        StringBuilder emailMessageBuilder = new StringBuilder();
        String subject = response.isAvailable() ? "Blank Booking Deletion Failed" : "Blank Booking Creation Failed";

        emailMessageBuilder.append("Availability Update has been failed for a date range. \n" +
                "Jomres Property Name: " + bookingItemName + "\n" +
                "Jomres Property UId: " + response.getPropertyId() + "\n" +
                "Availability Start Date: " + response.getStart() + "\n" +
                "Availability Resume Date: " + response.getEnd() + "\n" +
                "Property Availability in PMS: " + (response.isAvailable() ? "available" : "unavailable") + "\n\n" +
                (StringUtils.isNotBlank(response.getMessage()) ? "Possible Reason: " + response.getMessage() : "") + "\n");

        if (!response.isAvailable()) {
            emailMessageBuilder.append("Possible Solutions:\n" +
                    "   1. Please check if there is any booking in Jomres for this time period.\n" +
                    "   2. Please check if there is already a blank booking for this time period. " +
                        "If there is, the existing blank booking of Jomres won't be sync with PMS.\n" +
                    "   3. If 1 and 2 don't help, please check server connection with Jomres.\n");
        } else {
            emailMessageBuilder.append("Possible solutions:\n" +
                    "   1. Blank Booking is already deleted from Jomres.. in that case Jomres is synced with PMS, nothing to worry about.\n" +
                    "   2. If 1 doesn't help, please check server connection with Jomres.\n");
        }
        String emailMessage = emailMessageBuilder.toString();
        messageManager.sendJomresMessageToStoreOwner(emailMessage, subject);
        logger.info("Sent");
        logger.info("Email Message: " + emailMessage);
        pmsManager.markSentErrorMessageForJomresAvail(hashValueForErrorAvail);
        logText("Update Availability Error email sent to Owner");
    }

    private JomresBookingData addBookingToPms(JomresBooking booking, Map<String, Double> priceMatrix, String pmsBookingItemTypeId)
            throws Exception {
        try {
            LocalTime startTime = LocalTime.now();
            PmsBooking newbooking = pmsManager.startBooking();

            for (PmsBookingRooms room : newbooking.getAllRooms()) {
                room.unmarkOverBooking();
            }
            newbooking.ignoreOverrideTotPrice = true;
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

            PmsBookingRooms room = new PmsBookingRooms();

            room.date = new PmsBookingDateRange();
            room.date.start = setCorrectTime(booking.arrivalDate, true);
            room.date.end = setCorrectTime(booking.departure, false);
            room.numberOfGuests = booking.numberOfGuests;
            room.bookingItemTypeId = pmsBookingItemTypeId;

            PmsGuests guest = getPmsGuestFromJomresCustomer(new PmsGuests(), booking.customer);
            room.guests.add(guest);

            for(int i = 1; i<room.numberOfGuests ; i ++){
                PmsGuests guestWithoutInfo = new PmsGuests();
                room.guests.add(guestWithoutInfo);
            }

            newbooking.rooms.clear();
            newbooking.addRoom(room);

            if (newbooking.rooms.isEmpty()) {
                logger.info("Returning since there are no rooms to add id: {}", booking.bookingId);
                return null;
            }

            checkIfPaymentMethodIsActive("70ace3f0-3981-11e3-aa6e-0800200c9a66");
            newbooking.paymentType = "70ace3f0-3981-11e3-aa6e-0800200c9a66";

            if (room.bookingItemTypeId == null) {
                logger.error("Failed to find room type for booking: {}", booking.bookingId);
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

            if (newbooking == null) {
                throw new Exception("Jomres Booking Completion Failed");
            }
            newbooking = setBookingPrice(newbooking, booking.totalPrice, priceMatrix);
            String orderId = pmsInvoiceManager.autoCreateOrderForBookingAndRoom(newbooking.id, newbooking.paymentType);
            pmsInvoiceManager.markOrderAsPaid(newbooking.id, orderId);

            JomresBookingData jomresBookingData = new JomresBookingData();
            jomresBookingData.pmsBookingId = newbooking.id;
            jomresBookingData.jomresBookingId = booking.bookingId;
            jomresBookingData.pmsRoomId = newbooking.rooms.get(0).pmsBookingRoomId;
            newbooking.ignoreOverrideTotPrice = false;
            LocalTime endTime = LocalTime.now();
            logger.info("Time takes to complete one booking: {}s", ChronoUnit.SECONDS.between(startTime, endTime));
            return jomresBookingData;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logText(e.getMessage());
            logPrintException(e);
            throw e;
        }
    }

    private PmsGuests getPmsGuestFromJomresCustomer(PmsGuests guest, JomresGuest customer) {
        guest.email = customer.email;
        guest.name = customer.name;
        guest.phone = customer.telMobile;
        guest.prefix = StringUtils.isBlank(customer.mobilePrefix) ? "" : customer.mobilePrefix;
        return guest;
    }

    private PmsBooking setBookingPrice(PmsBooking pmsBooking, Double totalPrice, Map<String, Double> priceMatrix) {
        Calendar calStart = Calendar.getInstance();
        PmsBookingRooms room = pmsBooking.rooms.get(0);
        if ((pmsManager.getConfigurationSecure().usePricesFromChannelManager || storeManager.isPikStore())) {
            calStart.setTime(room.date.start);

            Date current = room.date.start;
            calStart.setTime(current);
            while (!current.after(room.date.end)) {
                String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(current);
                String offset = PmsBookingRooms.getOffsetKey(calStart, PmsBooking.PriceType.daily);
                room.priceMatrix.put(offset, priceMatrix.get(currentDate));
                calStart.add(Calendar.DATE, 1);
                current = calStart.getTime();
            }
            room.price = totalPrice;
            room.totalCost = totalPrice;

            pmsManager.saveBooking(pmsBooking);
            pmsInvoiceManager.clearOrdersOnBooking(pmsBooking);
            if (pmsBooking.hasOverBooking()) {
                pmsBooking.rowCreatedDate = new Date();
                PmsBookingRooms newBookingRoom = pmsBooking.rooms.get(0);
                if (newBookingRoom.isOverBooking()) {
                    try {
                        pmsManager.removeFromBooking(pmsBooking.id, newBookingRoom.pmsBookingRoomId);
                    } catch (java.lang.Exception e) {
                        //Okay, it failed, that's okay.
                    }
                }
                String text = "An overbooking occured go to your booking admin panel handle it.<br><bR><br>booking dump:<br>"
                        + pmsManager.dumpBooking(pmsBooking, true);
                text += "<br><br>";
                text += "For more information about overbooking, see: https://getshop.com/double_booking_error.html";
                String content = "Possible overbooking happened:<br>" + text;
                messageManager.sendJomresMessageToStoreOwner(content, "Warning: possible overbooking happened");
            }
        }
        return pmsBooking;
    }
}