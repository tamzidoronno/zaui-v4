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

    Constants constants = new Constants();

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
            } else if (dataCommon instanceof Constants) {
                constants = (Constants) dataCommon;
            } else if (dataCommon instanceof JomresBookingData) {
                jomresToPmsBookingMap.put(((JomresBookingData) dataCommon).jomresBookingId, (JomresBookingData) dataCommon);
                pmsToJomresBookingMap.put(((JomresBookingData) dataCommon).pmsBookingId, (JomresBookingData) dataCommon);
            }
        }
         createScheduler("jomresprocessor", "* * * * *", JomresManagerProcessor.class);
    }

    void getHardCodedJomresRoomData() {
        if (pmsItemToJomresRoomDataMap.isEmpty()) {
            JomresRoomData roomData1 = new JomresRoomData();
            JomresRoomData roomData2 = new JomresRoomData();
            JomresRoomData roomData3 = new JomresRoomData();
            roomData1.bookingItemId = "ba5cbb25-ea4b-4184-a813-483ff6ef839b";
            roomData1.jomresPropertyId = 92;
            pmsItemToJomresRoomDataMap.put(roomData1.bookingItemId, roomData1);
            roomData2.bookingItemId = "2290a31b-0fcb-46b5-90fc-fb6735da752e";
            roomData2.jomresPropertyId = 93;
            pmsItemToJomresRoomDataMap.put(roomData2.bookingItemId, roomData2);
            roomData3.bookingItemId = "8e626df2-e48f-4129-929b-5a12612627a3";
            roomData3.jomresPropertyId = 94;
            pmsItemToJomresRoomDataMap.put(roomData3.bookingItemId, roomData3);
            saveObject(roomData1);
            saveObject(roomData2);
            saveObject(roomData3);
            for (Map.Entry<String, JomresRoomData> entry : pmsItemToJomresRoomDataMap.entrySet()) {
                jomresPropertyToRoomDataMap.put(entry.getValue().jomresPropertyId, entry.getValue());
            }
        }
    }

    public void logText(String string) {
        jomresLogManager.save(string, System.currentTimeMillis());
        logPrint(string);

    }

    @Override
    public boolean testConnection(){
        return connectToApi();
    }

    @Override
    public boolean changeCredentials(String clientId, String clientSecret){
        constants.JOMRES_CMF_REST_API_CLIENT_ID = clientId;
        constants.JOMRES_CMF_REST_API_CLIENT_SECRET = clientSecret;
        saveObject(constants);
        return true;
    }

    @Override
    public boolean updateAvailability() {
        getHardCodedJomresRoomData();
        if (!connectToApi()) {
            logger.error("Failed to connect with Jomres API..");
            logText("Failed to connect with Jomres API..");
            return false;
        }
        try {
            Calendar calendar = Calendar.getInstance();
            Date startDate = calendar.getTime();
            calendar.add(Calendar.DATE, 60);
            Date endDate = calendar.getTime();
            Integer secondsInADay = 24 * 60 * 60;

            PmsIntervalFilter filter = new PmsIntervalFilter(startDate, endDate, secondsInADay);
            PmsIntervalResult timeline = pmsManager.getIntervalAvailability(filter);
            LinkedHashMap<String, LinkedHashMap<Long, IntervalResultEntry>> itemTimeLines = timeline.itemTimeLines;

            for (JomresRoomData roomData : pmsItemToJomresRoomDataMap.values()) {
                Date unavailableStartingDay = null;
                Date availableStartingDay = null;
                calendar.setTime(startDate);

                AvailabilityService availabilityService = new AvailabilityService();
                Map<Date, Date> unavailableStartToEndDates = new HashMap<>();
                Map<Date, Date> availableStartToEndDates = new HashMap<>();
                Date unavailableEndingDay, availableEndingDay;
                String updateAvailabilityStatus;
                if (bookingEngine.getBookingItem(roomData.bookingItemId) == null) {
                    System.out.println("The room is not found in Pms for Jomres PropertyId: " + roomData.jomresPropertyId);
                    System.out.println("Room is deleted from Pms or mapping is removed.");
                    logger.error("The room is not found in Pms for Jomres BookingId: " + roomData.jomresPropertyId);
                    logger.error("Room is deleted from Pms or mapping is removed.");
                    logText("The room is not found in Pms for Jomres BookingId: " + roomData.jomresPropertyId);
                    logText("Room is deleted from Pms or mapping is removed.");
                    continue;
                }

                for (IntervalResultEntry availabilityInfo : itemTimeLines.get(roomData.bookingItemId).values()) {
                    if (availabilityInfo.count == 0 && (availabilityInfo.bookingIds == null || availabilityInfo.bookingIds.isEmpty())) {
                        if (availableStartingDay == null) availableStartingDay = calendar.getTime();

                        if (unavailableStartingDay != null) {
                            //subtracting one day from calendar for assinging appropriate ending day
                            calendar.add(Calendar.DATE, -1);
                            unavailableEndingDay = calendar.getTime();

                            //restoring current date in calendar
                            calendar.add(Calendar.DATE, 1);
                            unavailableStartToEndDates.put(unavailableStartingDay, unavailableEndingDay);

                            unavailableStartingDay = null;
                        }
                    } else if (availabilityInfo.bookingIds != null && !availabilityInfo.bookingIds.isEmpty()) {
                        boolean needToUpdateAvailability = true;
                        for (String bookingId : availabilityInfo.bookingIds) {
                            if (pmsToJomresBookingMap.get(bookingId) != null) needToUpdateAvailability = false;
                        }
                        if (needToUpdateAvailability && unavailableStartingDay == null)
                            unavailableStartingDay = calendar.getTime();
                        if (needToUpdateAvailability && availableStartingDay != null) {
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
                logText("marking property unavailable, Jomres property id: " + roomData.jomresPropertyId);
                availabilityService.changePropertyAvailability(
                        constants.CLIENT_BASE_URL,
                        cmfClientAccessToken,
                        constants.CHANNEL_NAME,
                        roomData.jomresPropertyId,
                        unavailableStartToEndDates,
                        false
                );

                logText("marking property available, Jomres property id: " + roomData.jomresPropertyId);
                if (availableStartingDay != null) {
                    calendar.add(Calendar.DATE, -1);
                    availableEndingDay = calendar.getTime();
                    availableStartToEndDates.put(availableStartingDay, availableEndingDay);
                }

                updateAvailabilityStatus = availabilityService.changePropertyAvailability(
                        constants.CLIENT_BASE_URL,
                        cmfClientAccessToken,
                        constants.CHANNEL_NAME,
                        roomData.jomresPropertyId,
                        availableStartToEndDates,
                        true
                );
                logText(updateAvailabilityStatus);
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage1());
            logText(e.getMessage1());
            logger.error(e.getMessage());
            logText("Failed to Update availability...");
            return false;
        } catch (java.lang.Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            logText("Failed to Update availability...");
            return false;
        }
    }


    public void createChannel() throws Exception {
        try {
            connectToApi();
            ChannelService channelService = new ChannelService();
            logger.debug(
                    channelService.announceChannel(
                            constants.CLIENT_BASE_URL, constants.CHANNEL_NAME, "My%20New%20Channel", cmfClientAccessToken
                    ) + ""
            );
        } catch (Exception e) {
            logText("Failed to create channel...");
            logText(e.getMessage1());
        }

    }

    @Override
    public List<FetchBookingResponse> fetchBookings() throws Exception {
        getHardCodedJomresRoomData();
        if (!connectToApi()) {
            logger.error("Failed to connect with Jomres API..");
            return new ArrayList<>();
        }
        BookingService bookingService = new BookingService();
        PriceService priceService = new PriceService();

        Set<Integer> propertyUIDs = jomresPropertyToRoomDataMap.keySet();
        List<JomresBooking> bookings = new ArrayList<>();
        List<FetchBookingResponse> allBookings = new ArrayList<>();
        Map<String, Double> dailyPriceMatrix;

        Date start = new Date();
        Date end;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(start);
        calendar.add(Calendar.DATE, 60);
        end = calendar.getTime();

        logText("FetchBooking process for 60 days is starting...");
        for (int propertyUID : propertyUIDs) {
            try {
                System.out.println("Started fetch booking for PropertyId: "+propertyUID);
                bookings = bookingService.getJomresBookingsBetweenDates(
                        constants.CLIENT_BASE_URL,
                        propertyUID,
                        cmfClientAccessToken,
                        start, end
                );
                System.out.println("ended fetch booking for PropertyId: "+propertyUID);
                System.out.println("Started fetch daily price matrix for PropertyId: "+propertyUID);
                dailyPriceMatrix = priceService.getDailyPrice(constants.CLIENT_BASE_URL, cmfClientAccessToken,
                        constants.CHANNEL_NAME, propertyUID);
                System.out.println("ended fetch daily price matrix for PropertyId: "+propertyUID);

                Map<String, Double> finalDailyPriceMatrix = dailyPriceMatrix;
                for (JomresBooking jomresBooking : bookings) {
                    String bookingStatus = "", pmsBookingId = "";
                    JomresBookingData jomresBookingData = null;
                    JomresRoomData jomresRoomData;
                    logText("Start Syncing Booking Id: " + jomresBooking.bookingId + ", PropertyId: " + propertyUID);
                    System.out.println("Start Syncing Booking Id: " + jomresBooking.bookingId + ", PropertyId: " + propertyUID);
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
                        } else if (pmsRoom == null) {
                            System.out.println("The room is not found in Pms for Jomres BookingId: " + jomresBooking.bookingId);
                            System.out.println("Room is deleted from Pms or mapping is removed.");
                            logger.error("The room is not found in Pms for Jomres BookingId: " + jomresBooking.bookingId);
                            logger.error("Room is deleted from Pms or mapping is removed.");
                        }

                        if (pmsRoomCategory == null) {
                            System.out.println("The room category is not found in Pms for Jomres BookingId: " + jomresBooking.bookingId);
                            System.out.println("Room category is deleted from pms or mapping is removed");
                            logger.error("The room category is not found in Pms for Jomres BookingId: " + jomresBooking.bookingId);
                            logger.error("Room category is deleted from pms or mapping is removed");
                        } else roomCategoryName = pmsRoomCategory.name;

                        if (jomresBooking.status.equals("Cancelled") || jomresBooking.statusCode == 6) {
                            bookingStatus = "Cancelled";
                            System.out.println("Started deleting Booking Id: " + jomresBooking.bookingId);
                            deletePmsBooking(jomresBooking);
                            System.out.println("ended deleting Booking Id: " + jomresBooking.bookingId);
                        } else {
                            jomresBookingData = jomresToPmsBookingMap.get(jomresBooking.bookingId);

                            if (jomresBookingData == null || jomresBookingData.pmsBookingId.equals("")) {
                                bookingStatus = "Added";
                                System.out.println("Started adding Booking into pms, BookingId: " + jomresBooking.bookingId);
                                System.out.println("started fetch complete booking, BookingId: " + jomresBooking.bookingId);

                                jomresBooking = bookingService.getCompleteBooking(
                                        constants.CLIENT_BASE_URL,
                                        cmfClientAccessToken,
                                        constants.CHANNEL_NAME,
                                        jomresBooking
                                );
                                System.out.println("ended fetch complete booking, BookingId: " + jomresBooking.bookingId);

                                jomresBookingData = addBookingToPms(jomresBooking, finalDailyPriceMatrix, null, pmsRoom, pmsRoomCategory);
                                System.out.println("ended adding Booking into pms BookingId: " + jomresBooking.bookingId);
                            } else {
                                bookingStatus = "Modified/Synced";
                                System.out.println("Started updating Booking into pms, BookingId: " + jomresBooking.bookingId);

                                if (jomresBooking == null) {
                                    System.out.println("Complete booking not found, error occurred");
                                    continue;
                                }
                                jomresBookingData = updatePmsBooking(jomresBooking, finalDailyPriceMatrix, jomresBookingData, pmsRoom, pmsRoomCategory);
                                System.out.println("ended updating Booking into pms BookingId: " + jomresBooking.bookingId);
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
                        logText("Booking Synced, Status: " + bookingStatus + ", " +
                                "BookingId: \"+booking.bookingId+\", PropertyId: \"+booking.propertyUid");
                    } catch (Exception e) {
                        logger.error(e.getMessage1());
                        logger.error(e.getMessage());
                        e.printStackTrace();
                        System.out.println("This booking synchronization failed");
                        logText("This booking synchronization failed");
                        logText(e.getMessage1());
                    } catch (java.lang.Exception e) {
                        logger.error(e.getMessage());
                        System.out.println("This booking synchronization failed");
                        logText("This booking synchronization failed");
                    }
                }
                logText("Booking has been synced for Jomres Property Id: " + propertyUID);
            } catch (Exception e) {
                logger.error(e.getMessage1());
                logText("Booking synchronization has been failed for property id: "+propertyUID);
                System.out.println("Booking synchronization has been failed for property id: "+propertyUID);
                logText(e.getMessage1());
            }
        }
        return allBookings;

    }

    JomresGuest getJomresCustomerFromPmsBooking(PmsBooking booking){
        JomresGuest customer = new JomresGuest();
        customer.name = booking.registrationData.resultAdded.get("user_fullName");
        customer.telMobile = booking.registrationData.resultAdded.get("user_cellPhone");
        customer.address = booking.registrationData.resultAdded.get("user_address_address");
        customer.city = booking.registrationData.resultAdded.get("user_address_city");
        customer.email = booking.registrationData.resultAdded.get("user_emailAddress");
        customer.postcode = booking.registrationData.resultAdded.get("user_address_postCode");
        return customer;
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

        JomresGuest jCustomerFromPms = getJomresCustomerFromPmsBooking(pBooking);
        if(!jBooking.customer.equals(jCustomerFromPms))
            return true;

        return false;
    }

    JomresBookingData updatePmsBooking(JomresBooking booking, Map<String, Double> priceMatrix, JomresBookingData bookingData,
                                       BookingItem pmsRoom, BookingItemType pmsRoomCategory) {
        try {
            PmsBooking newbooking = findCorrelatedBooking(booking);
            if (newbooking == null) {
                return addBookingToPms(booking, priceMatrix, null, pmsRoom, pmsRoomCategory);
            } else {
                if (!isBookingNeedToBeSynced(booking, newbooking)) {
                    logger.debug("Booking didn't modified, BookingId: " + booking.bookingId + ", PropertyId: " + booking.propertyUid);
                    logText("Booking didn't modified, BookingId: " + booking.bookingId + ", PropertyId: " + booking.propertyUid);
                    return bookingData;
                }
                booking.setNumberOfGuests(newbooking.rooms.get(0).numberOfGuests);
                booking.setCustomer(getJomresCustomerFromPmsBooking(newbooking));
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
                logText("For Simple modification we are gonna delete existing booking first, then creating new one...");
                deletePmsBooking(booking);
                return addBookingToPms(booking, priceMatrix, null, pmsRoom, pmsRoomCategory);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(e.getMessage1());
            return null;

        } catch (java.lang.Exception e) {
            logger.error(e.getMessage());
            return null;
        }

    }

    void deletePmsBooking(JomresBooking booking) throws java.lang.Exception {
        logText("Booking needs to be deleted, BookingId: " + booking.bookingId + ", PropertyId: " + booking.propertyUid);
        PmsBooking newbooking = null;
        newbooking = findCorrelatedBooking(booking);
        if (newbooking == null) {
            logger.debug("Did not find booking to delete.");
            logText("Didn't find to delete, BookingId: " + booking.bookingId + ", PropertyId: " + booking.propertyUid);
            return;
        } else {
            pmsManager.logEntry("Deleted by channel manager", newbooking.id, null);
            pmsManager.deleteBooking(newbooking.id);
            deleteObject(jomresToPmsBookingMap.get(booking.bookingId));
            jomresToPmsBookingMap.remove(booking.bookingId);
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
        logText("Booking deleted, Booking Id: " + booking.bookingId + ", Property Id: " + booking.propertyUid);
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
        Date currentTime = new Date();
        long timeDiff = currentTime.getTime() - cmfClientTokenGenerationTime.getTime();
        try {
            if (cmfClientAccessToken == null || timeDiff > 1800000) {  //token is expired after 30 minutes
                logText("Generating new token...");
                cmfClientTokenGenerationTime = new Date();
                cmfClientAccessToken = jomresService.getAccessToken(
                        constants.JOMRES_CMF_REST_API_CLIENT_ID,
                        constants.JOMRES_CMF_REST_API_CLIENT_SECRET,
                        constants.CMF_CLIENT_TOKEN_URL
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
            logger.error(e.getMessage());
            logText("Failed to connect with Jomres, please see log files.");
            return false;
        }

    }

    private BookingItem getPmsRoom(String bookingItemId) {
        BookingItem room = bookingEngine.getBookingItem(bookingItemId);
        return room;
    }


    JomresBookingData findExistingPmsBookingId(JomresBooking booking) {
        return jomresToPmsBookingMap.get(booking.bookingId);
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

    private void sendErrorForBooking(long bookingId, String message) {
        boolean isSentErrorMail = pmsManager.hasSentErrorNotificationForJomresBooking(bookingId);
        if (!isSentErrorMail) {
            String emailMessage = "storeId-" + storeId + "Error for jomres booking: " + bookingId + "\n\t" + message;
//            messageManager.sendErrorNotification(
//                    emailMessage, null);
            pmsManager.markSentErrorMessageForJomresBooking(bookingId);
        }
    }

    private JomresBookingData addBookingToPms(JomresBooking booking, Map<String, Double> priceMatrix, PmsBooking newbooking,
                                              BookingItem pmsRoom, BookingItemType roomCategory) throws Exception {
        try {

            if (pmsRoom == null) {
                logger.debug("This room does not exist (channel manager), " +
                        "this needs to be remapped. Category or Room in GetShop has been deleted.");
                return null;
            } else if (roomCategory == null) {
                logger.debug("This room category does not exist (channel manager), " +
                        "this needs to be remapped. Category in GetShop has been deleted.");
                return null;
            }

            long start = System.currentTimeMillis();

            if (newbooking == null) {
                newbooking = pmsManager.startBooking();
            }

            for (PmsBookingRooms room : newbooking.getAllRooms()) {
                room.unmarkOverBooking();
            }
            newbooking.channel = "jomres_" + constants.CHANNEL_NAME;
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
            guest.phone = booking.customer.telLandline;

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
//                            sendErrorForReservation(booking.reservationCode, "Failed to find room type for reservation");
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
                String message = "Maybe there is a manual booking for this room..\n\t" +
                        "Arraival: " + booking.arrivalDate + ", Departure: " + booking.departure;
                sendErrorForBooking(booking.bookingId, message);
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
//                room.unsettledAmount = 0.0;
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
                    String email = getStoreEmailAddress();
                    String content = "Possible overbooking happened:<br>" + text;
                    messageManager.sendMail(email, email, "Warning: possible overbooking happened", content, email, email);
//                    messageManager.sendMail("pal@getshop.com","pal@getshop.com", "Warning: possible overbooking happened", content,"pal@getshop.com","pal@getshop.com");

                }

            }

            if (orderId == null)
                orderId = pmsInvoiceManager.autoCreateOrderForBookingAndRoom(newbooking.id, newbooking.paymentType);
            Order order = orderManager.getOrder(orderId);
//            Double amount = orderManager.getTotalAmount(ord);
            pmsInvoiceManager.markOrderAsPaid(newbooking.id, orderId);

            JomresBookingData jomresBookingData = new JomresBookingData();
            jomresBookingData.pmsBookingId = newbooking.id;
            jomresBookingData.jomresBookingId = booking.bookingId;
            jomresBookingData.pmsRoomId = newbooking.rooms.get(0).pmsBookingRoomId;

            logger.debug("Time takes to complete one booking: " + (System.currentTimeMillis() - start) / 1000 + "s");
            return jomresBookingData;
        } catch (Exception e) {
            logger.error(e.getMessage1());
            logger.error(e.getMessage());
            logText("Failed to Sync/Add booking, BookingId: " + booking.bookingId + ", PropertyId: " + booking.propertyUid);
            logText(e.getMessage1());
            return null;
        } catch (java.lang.Exception e) {
            logger.error(e.getMessage());
            logText("Failed to Sync/Add booking, BookingId: " + booking.bookingId + ", PropertyId: " + booking.propertyUid);
            logText(e.getMessage());
            return null;
        }
    }

}
