package com.thundashop.core.jomres;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.jomres.dto.FetchBookingResponse;
import com.thundashop.core.jomres.dto.JomresBooking;
import com.thundashop.core.jomres.dto.JomresGuest;
import com.thundashop.core.jomres.dto.JomresProperty;
import com.thundashop.core.jomres.services.*;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pmsmanager.*;
import com.thundashop.core.sedox.autocryptoapi.Exception;
import com.thundashop.core.storemanager.StoreManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;
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

    JomresConfiguration jomresConfiguration = new JomresConfiguration();

    Map<String, JomresRoomData> pmsItemToJomresRoomDataMap = new HashMap<>();
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

    @Override
    public boolean changeConfiguration(JomresConfiguration newConfiguration){
        jomresConfiguration.updateConfiguration(newConfiguration);
        saveObject(jomresConfiguration);
        return true;
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

    void deleteJomresRoomData(JomresRoomData roomData){
        jomresPropertyToRoomDataMap.remove(roomData.jomresPropertyId);
        pmsItemToJomresRoomDataMap.remove(roomData.bookingItemId);
        deleteObject(roomData);
    }

    void saveNewRoomData(JomresRoomData roomData) throws Exception{
        jomresPropertyToRoomDataMap.put(roomData.jomresPropertyId, roomData);
        pmsItemToJomresRoomDataMap.put(roomData.bookingItemId, roomData);
        saveObject(roomData);
    }

    void handleExistingRoomDataWhileMapping(JomresRoomData roomData){
        JomresRoomData existingPropertyMapping =jomresPropertyToRoomDataMap.get(roomData.jomresPropertyId);
        if(existingPropertyMapping!=null) {
            logText("Jomres Property Uid  "+roomData.jomresPropertyId+ " is already mapped with roomId: "+roomData.bookingItemId);
            deleteJomresRoomData(existingPropertyMapping);
            logText("Deleted existing room mapping for Property Id: "+roomData.jomresPropertyId);
        }
        JomresRoomData existingItemMapping =pmsItemToJomresRoomDataMap.get(roomData.bookingItemId);
        if(existingItemMapping!=null) {
            logText("Pms room id "+roomData.bookingItemId+ " is already mapped with Jomres property id "+roomData.jomresPropertyId);
            deleteJomresRoomData(existingItemMapping);
            logText("Deleted existing room mapping for pms room id: "+roomData.bookingItemId);
        }
    }

    @Override
    public boolean saveMapping(List<JomresRoomData> mappingRoomData) throws Exception {
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
        if (!connectToApi()) {
            return false;
        }
        if(jomresPropertyToRoomDataMap.isEmpty() || jomresConfiguration==null){
            logText("No Room<->Jomres Property mapping found for this hotel.");
            logText("No need to update availability");
            logger.info("No Room<->Jomres Property mapping found for this hotel.");
            return false;
        }
        try {
            logText("Started Jomres Update availability");
            logger.debug("Started Jomres Update availability");
            Calendar calendar = Calendar.getInstance();
            Date startDate = calendar.getTime();
            calendar.add(Calendar.DATE, 60);
            Date endDate = calendar.getTime();
            Integer secondsInADay = 24 * 60 * 60;

            PmsIntervalFilter filter = new PmsIntervalFilter(startDate, endDate, secondsInADay);
            PmsIntervalResult timeline = pmsManager.getIntervalAvailability(filter);
            LinkedHashMap<String, LinkedHashMap<Long, IntervalResultEntry>> itemTimeLines = timeline.itemTimeLines;
            for (JomresRoomData roomData : pmsItemToJomresRoomDataMap.values()) {
                try{
                    Date unavailableStartingDay = null;
                    Date availableStartingDay = null;
                    calendar.setTime(startDate);

                    AvailabilityService availabilityService = new AvailabilityService();
                    Map<Date, Date> unavailableStartToEndDates = new HashMap<>();
                    Map<Date, Date> availableStartToEndDates = new HashMap<>();
                    Date unavailableEndingDay, availableEndingDay;
                    String updateAvailabilityStatus;
                    if (bookingEngine.getBookingItem(roomData.bookingItemId) == null) {
                        logText("The room is not found in Pms for Jomres PropertyId: " + roomData.jomresPropertyId
                                +", pms bookingItemId: "+roomData.bookingItemId);
                        logText("Room is deleted from Pms or mapping is removed.");
                        logger.debug("The room is not found in Pms for Jomres PropertyId: " + roomData.jomresPropertyId
                                +", pms bookingItemId: "+roomData.bookingItemId);
                        logger.debug("Room is deleted from Pms or mapping is removed.");
                        continue;
                    }

                    for (IntervalResultEntry availabilityInfo : itemTimeLines.get(roomData.bookingItemId).values()) {
                        if (availabilityInfo.count == 0 && (availabilityInfo.bookingIds == null || availabilityInfo.bookingIds.isEmpty())) {
                            if (availableStartingDay == null) availableStartingDay = calendar.getTime();

                            if (unavailableStartingDay != null) {
                                //subtracting one day from checkout day for getting date of last night staying
                                calendar.add(Calendar.DATE, -1);
                                unavailableEndingDay = calendar.getTime();

                                //restoring current date in calendar
                                calendar.add(Calendar.DATE, 1);
                                unavailableStartToEndDates.put(unavailableStartingDay, unavailableEndingDay);

                                unavailableStartingDay = null;
                            }
                        } else if (availabilityInfo.bookingIds != null && !availabilityInfo.bookingIds.isEmpty()) {
                            boolean needToMakeUnavailable = true;
                            for (String bookingId : availabilityInfo.bookingIds) {
                                //we don't need to decrease availability for the bookings we fetched from jomres
                                if (pmsToJomresBookingMap.get(bookingId) != null) needToMakeUnavailable = false;
                            }
                            if (needToMakeUnavailable && unavailableStartingDay == null)
                                unavailableStartingDay = calendar.getTime(); //it is the starting day of this unavailability
                            if (needToMakeUnavailable && availableStartingDay != null) {
                                //there is an ending of an availability date range if avaialbity starting day has some values.
                                calendar.add(Calendar.DATE, -1);
                                availableEndingDay = calendar.getTime();

                                //restoring current date in calendar
                                calendar.add(Calendar.DATE, 1);
                                availableStartToEndDates.put(availableStartingDay, availableEndingDay);

                                availableStartingDay = null;
                            }

                        }
                        calendar.add(Calendar.DATE, 1);
                    }

                    if (unavailableStartingDay != null) {
                        calendar.add(Calendar.DATE, -1);
                        unavailableEndingDay = calendar.getTime();
                        unavailableStartToEndDates.put(unavailableStartingDay, unavailableEndingDay);
                    }
                    logger.debug("Started unavailability update, Jomres property id: " + roomData.jomresPropertyId);
                    updateAvailabilityStatus = availabilityService.changePropertyAvailability(
                            jomresConfiguration.clientBaseUrl,
                            cmfClientAccessToken,
                            jomresConfiguration.channelName,
                            roomData.jomresPropertyId,
                            unavailableStartToEndDates,
                            false
                    );
                    logger.debug("Ended unavailability update, Jomres property id: " + roomData.jomresPropertyId);
                    logger.debug(updateAvailabilityStatus);
                    logText("Unavailability update status of Jomres Property "+roomData.jomresPropertyId+" is "+updateAvailabilityStatus);
                    if (availableStartingDay != null) {
                        calendar.add(Calendar.DATE, -1);
                        availableEndingDay = calendar.getTime();
                        availableStartToEndDates.put(availableStartingDay, availableEndingDay);
                    }

                    logger.debug("Started availability update, Jomres property id: " + roomData.jomresPropertyId);
                    updateAvailabilityStatus = availabilityService.changePropertyAvailability(
                            jomresConfiguration.clientBaseUrl,
                            cmfClientAccessToken,
                            jomresConfiguration.channelName,
                            roomData.jomresPropertyId,
                            availableStartToEndDates,
                            true
                    );
                    logger.debug("Ended availability update, Jomres property id: " + roomData.jomresPropertyId);
                    logger.debug(updateAvailabilityStatus);
                    logText("Availability update status of Jomres Property "+roomData.jomresPropertyId+" is "+updateAvailabilityStatus);
                } catch (Exception e) {
                    logger.error(e.getMessage1());
                    logPrintException(e);
                    logText("Failed to Update availability for Jomres Property Id: "+roomData.jomresPropertyId
                            +", Pms BookingItemId: "+roomData.bookingItemId);
                    logText(e.getMessage1());
                    handleIfUnauthorizedExceptionOccurred(e);
                } catch (java.lang.Exception e) {
                    logPrintException(e);
                    logText("Failed to Update availability for Jomres Property Id: "+roomData.jomresPropertyId
                            +", Pms BookingItemId: "+roomData.bookingItemId);
                    logText("Please check log files");
                }
            }
            logText("Ended Jomres Update availability");
            return true;
        } catch (java.lang.Exception e) {
            logPrintException(e);
            logText("Failed to Update availability... Check log files");
            invalidateToken();
            return false;
        }
    }


    public void createChannel() throws Exception {
        try {
            connectToApi();
            ChannelService channelService = new ChannelService();
            logger.debug(
                    channelService.announceChannel(
                            jomresConfiguration.clientBaseUrl, jomresConfiguration.channelName, "My%20New%20Channel", cmfClientAccessToken
                    ) + ""
            );
        } catch (Exception e) {
            logText("Failed to create channel...");
            logText(e.getMessage1());
            logger.error(e.getMessage1());
            logger.error("Failed to create channel...");
            logPrintException(e);
            handleIfUnauthorizedExceptionOccurred(e);
        }

    }

    @Override
    public List<FetchBookingResponse> fetchBookings() throws Exception {
        if (!connectToApi()) {
            return new ArrayList<>();
        }
        if(jomresPropertyToRoomDataMap.isEmpty() || jomresConfiguration==null){
            logger.info("No room to Jomres Property mapping found for this hotel. No need to fetch bookings...");
            logText("No room to Jomres Property mapping found for this hotel.");
            logText("No need to fecth bookings...");
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
                                "BookingId: "+jomresBooking.bookingId+", PropertyId: "+jomresBooking.propertyUid);
                    } catch (Exception e) {
                        logger.error(e.getMessage1());
                        logPrintException(e);
                        logText("Booking synchronization failed, BookingId: "+jomresBooking.bookingId
                                +", Property Id: "+jomresBooking.propertyUid);
                        logText(e.getMessage1());
                        handleIfUnauthorizedExceptionOccurred(e);
                    } catch (java.lang.Exception e) {
                        logPrintException(e);
                        logText("Booking synchronization failed, BookingId: "+jomresBooking.bookingId
                                +", Property Id: "+jomresBooking.propertyUid);
                    }
                }
                logger.debug("Booking has been synced for Jomres Property Id: " + propertyUID);
            } catch (Exception e) {
                logger.error(e.getMessage1());
                logPrintException(e);
                logText(e.getMessage1());
                logText("Booking synchronization has been failed for property id: "+propertyUID);
                handleIfUnauthorizedExceptionOccurred(e);
            }
        }
        logText("Ended Jomres fetch bookings for 60 days");
        return allBookings;

    }

    boolean isGuestInfoUpdated(JomresGuest customer, PmsBooking booking){
        PmsBookingRooms room = Optional.ofNullable(booking.rooms.get(0)).orElse(new PmsBookingRooms());
        PmsGuests guest = Optional.ofNullable(room.guests.get(0)).orElse(new PmsGuests());
        String guestEmail = Optional.ofNullable(guest.email).orElse("");
        String guestPhone = Optional.ofNullable(guest.phone).orElse("");

        if(!customer.telMobile.contains(guestPhone))
            return true;
        if(!customer.email.equals(guestEmail))
            return true;
        return false;
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

    void deleteJomresBookingData(JomresBookingData bookingData){
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

    String getJomresBookingErrorMessageForOwner(JomresBooking booking, String pmsRoomName){
        String arrival, departure;
        SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy");
        arrival = format.format(booking.arrivalDate);
        departure = format.format(booking.departure);
        String message = "Failed to add new booking in pms from Jomres.\n" +
                "Booking Id: "+booking.bookingId+", Room: "+pmsRoomName+".\n"+
                "Arraival: " + arrival + ", Departure: " + departure+".\n"+
                "Maybe there is a manual booking for this room or the room is closed by the system for a while.\n"+
                "Booking will be created in Pms Automatically as soon as the room is open.";
        return message;
    }

    private void sendErrorForBooking(JomresBooking booking, String pmsRoomName) {
        boolean isSentErrorMail = pmsManager.hasSentErrorNotificationForJomresBooking(booking.bookingId);
        if (!isSentErrorMail) {
            logger.debug("Error mail wasn't sent for booking, Jomres BookingId: "+booking.bookingId+" Property ID: "+booking.propertyUid);
            String emailMessage = getJomresBookingErrorMessageForOwner(booking, pmsRoomName);
            String subject = "Jomres Booking Creation Failed";
            logger.debug("email sending...");
            logText("Error email is sending for Jomres bookingId: "+booking.bookingId+", property id: "+booking.propertyUid);
            messageManager.sendJomresMessageToStoreOwner(
                    emailMessage, subject);
            logText("Error email sent");
            logger.debug("Booking Error Email has been sent");
            pmsManager.markSentErrorMessageForJomresBooking(booking.bookingId);
            logger.debug("Marked that the mail has been sent for this booking");
        }
    }

    private JomresBookingData addBookingToPms(JomresBooking booking, Map<String, Double> priceMatrix, PmsBooking newbooking,
                                              BookingItem pmsRoom, BookingItemType roomCategory) throws Exception {
        try {

            if (pmsRoom == null) {
                logger.debug("Room does not exist, his needs to be remapped. Category or Room in GetShop has been deleted.");
                logger.debug("Failed to add jomres booking, Jomres Booking Id: "+booking.bookingId+", propertyId: "+booking.propertyUid);
                logText("Room does not exist, this needs to be remapped. Category or Room in GetShop has been deleted.");
                logText("Failed to add booking, Jomres Booking Id: "+booking.bookingId+", propertyId: "+booking.propertyUid);
                return null;
            } else if (roomCategory == null) {
                logger.debug("Room Category does not exist, his needs to be remapped. Category in GetShop has been deleted.");
                logger.debug("Failed to add jomres booking, Jomres Booking Id: "+booking.bookingId+", propertyId: "+booking.propertyUid);
                logText("Room Category does not exist, this needs to be remapped. Category in GetShop has been deleted.");
                logText("Failed to add booking, Jomres Booking Id: "+booking.bookingId+", propertyId: "+booking.propertyUid);
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
                logText("Failed to add new booking in pms from Jomres: " + booking.bookingId+", Propertt Id: "+booking.propertyUid);
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
