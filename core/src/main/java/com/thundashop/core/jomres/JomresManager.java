package com.thundashop.core.jomres;

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
import com.thundashop.core.jomres.dto.*;
import com.thundashop.core.jomres.services.*;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pmsmanager.*;
import com.thundashop.core.sedox.autocryptoapi.Exception;
import com.thundashop.core.storemanager.StoreManager;
import org.apache.commons.lang3.StringUtils;
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
    JomresConfiguration jomresConfiguration = new JomresConfiguration();

    Map<String, JomresRoomData> pmsItemToJomresRoomDataMap = new HashMap<>();
    Map<Integer, Set<PMSBlankBooking>> pmsBlankBookings = new HashMap<>();
    Map<Long, JomresBookingData> jomresToPmsBookingMap = new HashMap<>();
    Map<String, JomresBookingData> pmsToJomresBookingMap = new HashMap<>();
    Map<Integer, JomresRoomData> jomresPropertyToRoomDataMap = new HashMap<>();
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
        if(jomresPropertyToRoomDataMap.isEmpty()){
            logText("No Jomres room mapping found from database for this hotel, store id: "+ this.storeId);
        }

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
    public boolean testConnection(){
        return connectToApi();
    }

    public void invalidateToken(){
        cmfClientAccessToken=null;
    }

    public void handleIfUnauthorizedExceptionOccurred(Exception e) throws Exception{
        if(e.getMessage1().contains("code: 401")|| e.getMessage1().contains("invalid token") || e.getMessage1().contains("unauthorized")){
            logText("Invalid Token! Check credentials... Operation aborted..");
            invalidateToken();
            throw e;
        }
    }

    private void deleteAllDbObjectWithSameClass(DataCommon sampleData){
        if(sampleData!=null){
            BasicDBObject queryObect = getQueryObjectWithClassName(sampleData);
            String collectionPrefix = "col_";
            db.getMongo()
                    .getDB(sampleData.gs_manager)
                    .getCollection( collectionPrefix + sampleData.storeId)
                    .remove(queryObect);
        }
    }

    @Override
    public boolean changeConfiguration(JomresConfiguration newConfiguration){
        deleteAllDbObjectWithSameClass(jomresConfiguration);
        jomresConfiguration.updateConfiguration(newConfiguration);
        saveObject(jomresConfiguration);
        invalidateToken();
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
    public List<JomresProperty> getJomresChannelProperties() throws Exception {
        if (!connectToApi()) {
            return new ArrayList<>();
        }
        try {
            PropertyService propertyService = new PropertyService();
            List<Integer> propertyIds = propertyService.getChannelsPropertyIDs(
                    jomresConfiguration.clientBaseUrl, cmfClientAccessToken, jomresConfiguration.channelName
            );
            List<JomresProperty> jomresProperties = propertyService.getPropertiesFromIds(
                    jomresConfiguration.clientBaseUrl, cmfClientAccessToken, jomresConfiguration.channelName,propertyIds);
            return jomresProperties;
        } catch (java.lang.Exception e) {
            logPrintException(e);
            logText("Failed to load Jomres Properties...");
            return new ArrayList<>();
        }
    }

    void saveNewRoomData(JomresRoomData roomData) throws Exception{
        jomresPropertyToRoomDataMap.put(roomData.jomresPropertyId, roomData);
        pmsItemToJomresRoomDataMap.put(roomData.bookingItemId, roomData);
        saveObject(roomData);
    }

    private BasicDBObject getQueryObjectWithClassName(DataCommon sampleData){
        BasicDBObject queryObject = new BasicDBObject();
        queryObject.append("className", sampleData.className);
        return queryObject;
    }

    void handleExistingRoomDataWhileMapping(JomresRoomData roomData){
        JomresRoomData existingPropertyMapping =jomresPropertyToRoomDataMap.get(roomData.jomresPropertyId);
        if(existingPropertyMapping!=null) {
            logText("Jomres Property Uid  "+roomData.jomresPropertyId+ " is already mapped with roomId: "+roomData.bookingItemId);
            logText("Deleted existing room mapping for Property Id: "+roomData.jomresPropertyId);
        }
        JomresRoomData existingItemMapping =pmsItemToJomresRoomDataMap.get(roomData.bookingItemId);
        if(existingItemMapping!=null) {
            logText("Pms room id "+roomData.bookingItemId+ " is already mapped with Jomres property id "+roomData.jomresPropertyId);
            logText("Deleted existing room mapping for pms room id: "+roomData.bookingItemId);
        }
    }

    private void deleteExistingMapping(){
        if(jomresPropertyToRoomDataMap!= null && !jomresPropertyToRoomDataMap.isEmpty()){
            JomresRoomData firstRoomData = jomresPropertyToRoomDataMap.values().stream().findFirst().get();
            deleteAllDbObjectWithSameClass(firstRoomData);
        }
        jomresPropertyToRoomDataMap = new HashMap<>();
        pmsItemToJomresRoomDataMap = new HashMap<>();
        return;
    }
    @Override
    public boolean saveMapping(List<JomresRoomData> mappingRoomData) throws Exception {
        deleteExistingMapping();
        for (JomresRoomData roomData : mappingRoomData) {
            handleExistingRoomDataWhileMapping(roomData);
            saveNewRoomData(roomData);
        }
        return true;
    }

    @Override
    public JomresConfiguration getConfigurationData() throws Exception {
        return jomresConfiguration;
    }

    @Override
    public List<JomresRoomData> getMappingData() throws Exception {
        return jomresPropertyToRoomDataMap.values().stream().collect(Collectors.toList());
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
        if(booking.bookingDeleted) return;
        AvailabilityService service = new AvailabilityService();
        logger.debug("Creating new blankBooking..");
        UpdateAvailabilityResponse response = service.createBlankBooking(jomresConfiguration.clientBaseUrl,
                cmfClientAccessToken, jomresConfiguration.channelName, propertyId, booking);
        if(!response.isSuccess()){
            sendErrorUpdateAvailability(response);
            return;
        }
        PMSBlankBooking newBlankBooking =
                new PMSBlankBooking(response.getContractId(), booking.id, propertyId, booking.startDate, booking.endDate);
        saveObject(newBlankBooking);
        pmsBlankBookings.put(propertyId, pmsBlankBookings.computeIfAbsent(propertyId, k-> new HashSet<>()));
        pmsBlankBookings.get(propertyId).add(newBlankBooking);
    }

    private void deleteBlankBookingCompletely(PMSBlankBooking booking) {
        logger.debug("Deleting blank Booking...");
        AvailabilityService service = new AvailabilityService();
        UpdateAvailabilityResponse res =
                service.deleteBlankBooking(jomresConfiguration.clientBaseUrl, cmfClientAccessToken, booking);
        if(!res.isSuccess()) {
            sendErrorUpdateAvailability(res);
            if(!isBlankBookingNeedToDeleteFromDb(res)) return;
        }
        deleteObject(booking);
        pmsBlankBookings.get(booking.getPropertyId()).remove(booking);
    }

    private boolean isBlankBookingNeedToDeleteFromDb(UpdateAvailabilityResponse res){
        return (StringUtils.isNotBlank(res.getMessage()) && res.getMessage().contains("does not exist"));
    }

    private void deleteIfExtraBlankBookingExist(
            Set<String> existingBookingIds, Map<String, PMSBlankBooking> blankBookingMap, Date start, Date end) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = formatter.format(start);
        String endDate = formatter.format(end);
        List<PMSBlankBooking> bBookingsForDeletion = blankBookingMap.values()
                .stream()
                .filter(o-> o.getDateFrom().compareTo(endDate) <=0 && o.getDateTo().compareTo(startDate) >=0)
                .filter(o-> !existingBookingIds.contains(o.getFlatBookingId()))
                .collect(Collectors.toList());
        for (PMSBlankBooking booking: bBookingsForDeletion){
            deleteBlankBookingCompletely(booking);
        }
    }

    @Override
    public List<FetchBookingResponse> fetchBookings() throws Exception {
        if (!connectToApi()) {
            return new ArrayList<>();
        }
        if (handleEmptyJomresCOnfiguration()) {
            return new ArrayList<>();
        }
        BookingService bookingService = new BookingService();
        PriceService priceService = new PriceService();

        Set<Integer> propertyUIDs = jomresPropertyToRoomDataMap.keySet();
        List<JomresBooking> bookings;
        List<FetchBookingResponse> allBookings = new ArrayList<>();
        Map<String, Double> dailyPriceMatrix;

        Date start = new Date();
        Date end;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(start);
        calendar.add(Calendar.DATE, 60);
        end = calendar.getTime();

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

                Map<String, Double> finalDailyPriceMatrix = dailyPriceMatrix;
                for (JomresBooking jomresBooking : bookings) {
                    String bookingStatus, pmsBookingId = "";

                    JomresBookingData jomresBookingData = null;
                    JomresRoomData jomresRoomData;
                    logger.debug("Started Syncing Booking Id: " + jomresBooking.bookingId + ", PropertyId: " + propertyUID);
                    try {
                        jomresRoomData = Optional.ofNullable(
                                jomresPropertyToRoomDataMap.get(jomresBooking.propertyUid)
                        ).orElse(new JomresRoomData());
                        String bookingItemId = jomresRoomData.bookingItemId;

                        BookingItem pmsRoom = bookingEngine.getBookingItem(bookingItemId);
                        BookingItemType pmsRoomCategory = null;

                        String roomCategoryName = "Not Found", roomName = "Not Found";
                        if (pmsRoom != null) {
                            roomName = pmsRoom.bookingItemName;
                            pmsRoomCategory = bookingEngine.getBookingItemType(pmsRoom.bookingItemTypeId);
                        } else {
                            logger.debug("The room is not found in Pms for Jomres BookingId: " + jomresBooking.bookingId);
                            logger.debug("Room is deleted from Pms or mapping is removed.");
                        }

                        if (pmsRoomCategory == null) {
                            logger.debug("The room category is not found in Pms for Jomres BookingId: " + jomresBooking.bookingId);
                            logger.debug("Room category is deleted from pms or mapping is removed");
                        } else roomCategoryName = pmsRoomCategory.name;

                        if (jomresBooking.status.equals("Cancelled") || jomresBooking.statusCode == 6) {
                            bookingStatus = "Cancelled";
                            logger.debug("Started deleting Booking Id: " + jomresBooking.bookingId);
                            deletePmsBooking(jomresBooking);
                            logger.debug("ended deleting Booking Id: " + jomresBooking.bookingId);
                        } else {
                            jomresBookingData = jomresToPmsBookingMap.get(jomresBooking.bookingId);

                            if (jomresBookingData == null || jomresBookingData.pmsBookingId.equals("")) {
                                bookingStatus = "Added";
                                logger.debug("started fetch complete booking, BookingId: " + jomresBooking.bookingId);

                                jomresBooking = bookingService.getCompleteBooking(
                                        jomresConfiguration.clientBaseUrl,
                                        cmfClientAccessToken,
                                        jomresConfiguration.channelName,
                                        jomresBooking
                                );
                                logger.debug("Ended fetch complete booking, BookingId: " + jomresBooking.bookingId);

                                logger.debug("Started adding Booking into pms BookingId: " + jomresBooking.bookingId);
                                jomresBookingData = addBookingToPms(jomresBooking, finalDailyPriceMatrix, null, pmsRoom, pmsRoomCategory);
                                logger.debug("ended adding Booking into pms BookingId: " + jomresBooking.bookingId);
                            } else {
                                bookingStatus = "Modified/Synced";

                                logger.debug("Started updating Booking into pms, BookingId: " + jomresBooking.bookingId);
                                jomresBookingData = updatePmsBooking(jomresBooking, finalDailyPriceMatrix, jomresBookingData, pmsRoom, pmsRoomCategory);
                                logger.debug("Ended updating Booking into pms BookingId: " + jomresBooking.bookingId);
                            }
                        }
                        if (!jomresBooking.status.equals("Cancelled") && (jomresBookingData == null || bookingStatus.equals(""))) {
                            bookingStatus = "Ignored";
                        }
                        if (jomresBookingData != null) {
                            pmsBookingId = jomresBookingData.pmsBookingId;
                            saveObject(jomresBookingData);
                            jomresToPmsBookingMap.put(jomresBooking.bookingId, jomresBookingData);
                            pmsToJomresBookingMap.put(jomresBookingData.pmsBookingId, jomresBookingData);
                        }

                        allBookings.add(new FetchBookingResponse(jomresBooking.bookingId,
                                bookingStatus,
                                jomresBooking.customer.name,
                                jomresBooking.arrivalDate,
                                jomresBooking.departure,
                                pmsBookingId,
                                roomName, roomCategoryName
                        ));
                        logger.debug("Booking Synced, Status: " + bookingStatus + ", " +
                                "BookingId: " + jomresBooking.bookingId + ", PropertyId: " + jomresBooking.propertyUid);
                    } catch (Exception e) {
                        logger.error(e.getMessage1());
                        logPrintException(e);
                        logText("Booking synchronization failed, BookingId: " + jomresBooking.bookingId
                                + ", Property Id: " + jomresBooking.propertyUid);
                        logText(e.getMessage1());
                        handleIfUnauthorizedExceptionOccurred(e);
                    } catch (java.lang.Exception e) {
                        logPrintException(e);
                        logText("Booking synchronization failed, BookingId: " + jomresBooking.bookingId
                                + ", Property Id: " + jomresBooking.propertyUid);
                    }
                }
                logger.debug("Booking has been synced for Jomres Property Id: " + propertyUID);
            } catch (Exception e) {
                logger.error(e.getMessage1());
                logPrintException(e);
                logText(e.getMessage1());
                logText("Booking synchronization has been failed for property id: " + propertyUID);
                handleIfUnauthorizedExceptionOccurred(e);
            }
        }
        logText("Ended Jomres fetch bookings for 60 days");
        allBookings = allBookings.stream()
                .sorted(Comparator.comparingLong(FetchBookingResponse::getBookingId).reversed())
                .collect(Collectors.toList());
        return allBookings;

    }

    boolean isGuestInfoUpdated(JomresGuest customer, PmsBooking booking){
        PmsBookingRooms room = Optional.ofNullable(booking.rooms.get(0)).orElse(new PmsBookingRooms());
        PmsGuests guest = Optional.ofNullable(room.guests.get(0)).orElse(new PmsGuests());
        String guestEmail = Optional.ofNullable(guest.email).orElse("");
        String guestPhone = Optional.ofNullable(guest.phone).orElse("");

        if(!customer.telMobile.contains(guestPhone))
            return true;
        return !customer.email.equals(guestEmail);
    }

    boolean isBookingNeedToBeSynced(JomresBooking jBooking, PmsBooking pBooking){
        PmsBookingRooms room = pBooking.rooms.get(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(!dateFormat.format(jBooking.arrivalDate).equals(dateFormat.format(room.date.start)))
            return true;
        if(!dateFormat.format(jBooking.departure).equals(dateFormat.format(room.date.end)))
            return true;
        if(jBooking.totalPrice != pBooking.getTotalPrice())
            return true;
        if(jBooking.totalPrice != room.totalCost)
            return true;
        if(room.deleted)
            return true;

        if(isGuestInfoUpdated(jBooking.customer, pBooking))
            return true;

        return false;
    }

    private void deleteJomresBookingData(JomresBookingData bookingData) {
        pmsToJomresBookingMap.remove(bookingData.pmsBookingId);
        jomresToPmsBookingMap.remove(bookingData.jomresBookingId);
        deleteObject(bookingData);
    }

    JomresBookingData updatePmsBooking(JomresBooking booking, Map<String, Double> priceMatrix, JomresBookingData bookingData,
                                       BookingItem pmsRoom, BookingItemType pmsRoomCategory) throws Exception {
        try {
            PmsBooking newbooking = findCorrelatedBooking(booking);
            if (newbooking == null) {
                deleteJomresBookingData(bookingData);
                return addBookingToPms(booking, priceMatrix, null, pmsRoom, pmsRoomCategory);
            } else {
                if (!isBookingNeedToBeSynced(booking, newbooking)) {
                    logger.debug("Booking didn't modified, BookingId: " + booking.bookingId + ", PropertyId: " + booking.propertyUid);
                    return bookingData;
                }
                BookingService bookingService = new BookingService();
                booking = bookingService.getCompleteBooking(
                        jomresConfiguration.clientBaseUrl,
                        cmfClientAccessToken,
                        jomresConfiguration.channelName,
                        booking
                );
                for (PmsBookingRooms room : newbooking.getActiveRooms()) {
                    if (room.isStarted()) {
                        pmsManager.logEntry(
                                "Failed to update from channel manager, stay already started.", newbooking.id, null
                        );
                        logText("Failed to update from channel manager, stay already started");
                        logText("BookingId: " + booking.bookingId + ", PropertyId: " + booking.propertyUid);
                        pmsManager.saveBooking(newbooking);
                        return null;
                    }
                    room.deletedByChannelManagerForModification = true;
                    pmsManager.removeFromBooking(newbooking.id, room.pmsBookingRoomId);
                }
                pmsManager.logEntry("Modified by channel manager", newbooking.id, null);
                logger.debug("For Simple modification we are gonna delete existing booking first, then creating new one...");
                logger.debug("Started delete booking, pms booking Id: "+newbooking.id);
                deletePmsBooking(booking);
                logger.debug("Ended delete booking, pms booking Id: "+newbooking.id);
                logger.debug("Updated booking will be added into pms");
                return addBookingToPms(booking, priceMatrix, null, pmsRoom, pmsRoomCategory);
            }
        } catch (Exception e) {
            logPrintException(e);
            logger.error(e.getMessage1());
            logger.error("Falied to update booking, Jomres booking Id: "+booking.bookingId+", Jomres Property Id: "+booking.propertyUid);
            logText("Falied to update booking, Jomres booking Id: "+booking.bookingId+", Jomres Property Id: "+booking.propertyUid);
            logText(e.getMessage1());
            return null;

        } catch (java.lang.Exception e) {
            logText("Falied to update booking, Jomres booking Id: "+booking.bookingId+", Jomres Property Id: "+booking.propertyUid);
            logPrintException(e);
            return null;
        }

    }

    void deletePmsBooking(JomresBooking booking) throws java.lang.Exception {
        PmsBooking newbooking = findCorrelatedBooking(booking);
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
        if(jomresConfiguration==null){
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

    private PmsBooking findCorrelatedBooking(JomresBooking booking) {
        PmsBooking newbooking = null;
        JomresBookingData jomresBookingData = jomresToPmsBookingMap.get(booking.bookingId);
        if (jomresBookingData != null && !jomresBookingData.pmsBookingId.equals("")) {
            newbooking = pmsManager.getBooking(jomresBookingData.pmsBookingId);
        }
        return newbooking;
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
        return  "Failed to add new booking in pms from Jomres.\n" +
                "Booking Id: "+booking.bookingId+", Room: "+pmsRoomName+".\n"+
                "Arraival: " + arrival + ", Departure: " + departure+".\n"+
                "Maybe there is a manual booking for this room or the room is closed by the system for a while.\n"+
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

        String subject = response.isAvailable()? "Blank Booking Deletion Failed" : "Blank Booking Creation Failed";

        if (!pmsManager.hasSentErrorNotificationForJomresAvailability(hashValueForErrorAvailability)) {
            logger.debug("Email is being sent...");
            String emailMessage = "Availability Update has been failed for a date range. \n" +
                    "Jomres Property UId: " + response.getPropertyId() + "\n" +
                    "Availability Start Date: " + response.getStart() + "\n+" +
                    "Availability End Date: " + response.getEnd() + "\n" +
                    "Property Availability in PMS: " + (response.isAvailable() ? "available" : "unavailable") + "\n\n" +
                    (StringUtils.isNotBlank(response.getMessage()) ? "Possible Reason: " + response.getMessage() : "") + "\n";

            if(!response.isAvailable()){
                emailMessage+= "Some other possible reason:\n" +
                        "   1. There is a booking in Jomres for this time period.\n"+
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
                                              BookingItem pmsRoom, BookingItemType roomCategory) throws Exception {
        try {
            if (pmsRoom == null) {
                logger.debug("Room does not exist, his needs to be remapped. Category or Room in GetShop has been deleted.");
                logger.debug("Failed to add jomres booking, Jomres Booking Id: " + booking.bookingId + ", propertyId: " + booking.propertyUid);
                logText("Room does not exist, this needs to be remapped. Category or Room in GetShop has been deleted.");
                logText("Failed to add booking, Jomres Booking Id: " + booking.bookingId + ", propertyId: " + booking.propertyUid);
                return null;
            } else if (roomCategory == null) {
                logger.debug("Room Category does not exist, his needs to be remapped. Category in GetShop has been deleted.");
                logger.debug("Failed to add jomres booking, Jomres Booking Id: " + booking.bookingId + ", propertyId: " + booking.propertyUid);
                logText("Room Category does not exist, this needs to be remapped. Category in GetShop has been deleted.");
                logText("Failed to add booking, Jomres Booking Id: " + booking.bookingId + ", propertyId: " + booking.propertyUid);
                return null;
            }

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
            room.bookingItemTypeId = pmsRoom.bookingItemTypeId;

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
                logger.error("Failed to add new booking in pms from Jomres: " + booking.bookingId);
                logText("Failed to add new booking in pms from Jomres: " + booking.bookingId + ", Propertt Id: " + booking.propertyUid);
                sendErrorForBooking(booking, pmsRoom.bookingItemName);
                return null;

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
            logger.error("Failed to Sync/Add booking, BookingId: " + booking.bookingId + ", PropertyId: " + booking.propertyUid);
            logPrintException(e);
            logText("Failed to Sync/Add booking, BookingId: " + booking.bookingId + ", PropertyId: " + booking.propertyUid);
            logText(e.getMessage1());
            return null;
        } catch (java.lang.Exception e) {
            logPrintException(e);
            logText(e.getMessage());
            logText("Failed to Sync/Add booking, BookingId: " + booking.bookingId + ", PropertyId: " + booking.propertyUid);
            return null;
        }
    }
}
