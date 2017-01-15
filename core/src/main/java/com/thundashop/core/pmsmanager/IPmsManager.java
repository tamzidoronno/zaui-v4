package com.thundashop.core.pmsmanager;

import com.thundashop.core.arx.AccessLog;
import com.thundashop.core.bookingengine.data.RegistrationRules;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Customer;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Property management system.<br>
 */

@GetShopApi
@GetShopMultiLayerSession
public interface IPmsManager {
    
    @Administrator
    public void markRoomAsCleaned(String itemId);
    
    @Administrator
    public void tryAddToEngine(String pmsBookingRoomId);
    
    @Administrator
    public void logEntryObject(PmsLog log);
    
    @Administrator
    public void forceMarkRoomAsCleaned(String itemId);
    
    @Administrator
    public void deleteAllBookings(String code);
    
    @Administrator
    public List<CleaningStatistics> getCleaningStatistics(Date start, Date end);
    
    @Administrator
    public String generateNewCodeForRoom(String roomId);
    
    @Administrator
    public List<SimpleInventory> getSimpleInventoryList(String roomName);
    
    @Administrator
    public void sendConfirmation(String email, String bookingId);
    
    @Administrator
    public void reportMissingInventory(List<SimpleInventory> inventories, String itemId, String roomId);
    
    @Administrator
    public void removeAddonFromRoomById(String addonId, String roomId);
    
    @Administrator
    public void saveCareTakerJob(PmsCareTaker job);
    
    @Administrator
    public void removeCareTakerJob(String jobId);
    
    @Administrator
    public List<PmsCareTaker> getCareTakerJobs();
    
    @Administrator
    public PmsCareTaker getCareTakerJob(String id);
    
    @Administrator
    public void completeCareTakerJob(String id);
    
    @Administrator
    public PmsAdditionalItemInformation getAdditionalInfo(String itemId);
    
    @Administrator
    public List<PmsBookingAddonViewItem> getItemsForView(String viewId, Date date);
    
    @Administrator
    public void removeAddonFromRoom(String id, String pmsBookingRooms);
        
    @Administrator
    public void markAddonDelivered(String id);
    
    @Administrator
    public List<PmsAddonDeliveryLogEntry> getDeliveryLog(List<String> productIds, Date start, Date end);
    
    @Administrator
    public List<PmsAddonDeliveryLogEntry> getDeliveryLogByView(String viewId, Date start, Date end);
    
    @Administrator
    public void deleteDeliveryLogEntry(String id);
    
    public List<PmsBooking> getAllBookings(PmsBookingFilter state);
    
    @Editor
    public void logEntry(String logText, String bookingId, String itemId);
    
    @Editor
    public PmsBooking getBookingFromRoom(String pmsBookingRoomId);
    
    public List<PmsRoomSimple> getMyRooms();
    
    @Editor
    public PmsBooking getBookingWithOrderId(String orderId);
    
    @Editor
    public PmsBooking getBookingFromRoomIgnoreDeleted(String roomId);
    
    @Administrator
    public String setBookingItemAndDate(String roomId, String itemId, boolean split, Date start, Date end);
    
    @Administrator
    public void addCartItemToRoom(CartItem item, String pmsBookingRoomId, String addedBy);
    
    @Administrator
    public String setNewRoomType(String roomId, String bookingId, String newType);
    
    @Administrator
    public List<PmsRoomSimple> getRoomsNeedingIntervalCleaningSimple(Date day);
    
    @Administrator
    public void sendMessage(String bookingId, String email, String title, String message);
    
    public void endRoom(String roomId);
    
    @Administrator
    public void splitBooking(List<String> roomIds);
    
    @Administrator
    public String setBookingItem(String roomId, String bookingId, String itemId, boolean split);
    
    @Administrator
    public PmsBookingRooms changeDates(String roomId, String bookingId, Date start, Date end);
    
    @Administrator
    public void deleteBooking(String bookingId);
    
    @Administrator
    public void createChannel(String channel);
    
    @Administrator
    public void removeChannel(String channel);
    
    @Administrator
    public void saveBooking(PmsBooking booking);
    
    public void endRoomWithDate(String pmsRoomId, Date date);
    
    @Administrator
    public PmsPricing setPrices(String code, PmsPricing prices); 
    
    @Administrator
    public PmsPricing getPricesByCode(String code, Date start, Date end);
    
    @Administrator
    public String createOrder(String bookingId, NewOrderFilter filter);
    
    @Administrator
    public void saveConfiguration(PmsConfiguration notifications);
    
    @Administrator
    public void sendCode(String phoneNumber, String roomId);
    
    @Administrator
    public void setGuestOnRoom(List<PmsGuests> guests, String bookingId, String roomId);
    
    @Administrator
    public void updateAddonsBasedOnGuestCount(String pmsRoomId);
    
    @Administrator
    public void resetPriceForRoom(String pmsRoomId);
    
    @Administrator
    public void confirmBooking(String bookingId, String message);
    
    @Administrator
    public void unConfirmBooking(String bookingId, String message);
    
    @Administrator
    public PmsStatistics getStatistics(PmsBookingFilter filter);
    
    @Administrator
    public List<PmsBookingAddonItem> getAddonsAvailable();
    
    @Customer
    public String removeFromBooking(String bookingId, String roomId) throws Exception;
    
    @Editor
    public String getContract(String bookingId) throws Exception;
    
    @Editor
    public PmsIntervalResult getIntervalAvailability(PmsIntervalFilter filter);
    
    @Editor
    public Boolean isClean(String itemId);
    
    @Editor
    public Boolean isUsedToday(String itemId);
    
    @Editor
    public List<PmsAdditionalItemInformation> getAllAdditionalInformationOnRooms();
    
    @Editor
    public List<RoomCleanedInformation> getAllRoomsNeedCleaningToday();
    
    @Administrator
    public void updateAdditionalInformationOnRooms(PmsAdditionalItemInformation info);
    
    public HashMap<String, String> getChannelMatrix();
    
    @Editor
    public List<PmsBookingRooms> getRoomsNeedingIntervalCleaning(Date day);
    
    @Editor
    public List<PmsBookingRooms> getRoomsNeedingCheckoutCleaning(Date day);
    
    @Administrator
    public String addBookingItem(String bookingId, String item, Date start, Date end);
    
    @Administrator
    public String addBookingItemType(String bookingId, String item, Date start, Date end, String guestInfoFromRoom);
    
    @Editor
    public String getDefaultMessage(String bookingId);
    
    @Editor
    public void doNotification(String key, String bookingId);
    
    @Editor
    public void addComment(String bookingId, String comment);
    
    @Administrator
    public String createPrepaymentOrder(String bookingId);
    
    @Administrator
    public void sendPaymentLink(String orderId, String bookingId, String email, String prefix, String phone);
    
    @Administrator
    public void failedChargeCard(String orderId, String bookingId);
    
    @Administrator
    public void sendMissingPayment(String orderId, String bookingId);
    
    @Editor
    public List<PmsLog> getLogEntries(PmsLog filter);
    
    @Administrator
    public List<PmsBookingRooms> updateRepeatingDataForBooking(PmsRepeatingData data, String bookingId);
     
    @Administrator
    public void setNewCleaningIntervalOnRoom(String roomId, Integer interval);
    @Administrator
    public PmsBookingRooms getRoomForItem(String itemId, Date atTime);
    
    @Administrator
    public PmsBooking getBookingFromBookingEngineId(String bookingEngineId);
    
    @Administrator
    public void undeleteBooking(String bookingId);
    
    @Administrator
    public List<PmsRoomSimple> getSimpleRooms(PmsBookingFilter filter);
    
    @Administrator
    public void sendMessageToAllTodaysGuests(String message);
    
    @Administrator
    public void markKeyDeliveredForAllEndedRooms();
    
    @Administrator
    public void changeInvoiceDate(String roomId, Date newDate);
    
    @Administrator
    public void checkIfGuestHasArrived() throws Exception;
    
    @Administrator
    public void sendStatistics() throws Exception;
    
    @Administrator
    public void addProductToRoom(String productId, String pmsRoomId, Integer count);
    
    @Administrator
    public List<PmsBookingAddonItem> getAddonsForRoom(String roomId);
    
    public void addAddonsToBooking(Integer type, String roomId, boolean remove);
    public void updateAddonsCountToBooking(Integer type, String roomId, Integer count);
    
    public PmsPricing getPrices(Date start, Date end); 
    public PmsBooking getBooking(String bookingId);
    public PmsConfiguration getConfiguration();
    public void processor();
    public void hourlyProcessor();
    public String getCurrenctContract() throws Exception;
    public void addAddonToCurrentBooking(String itemtypeId) throws Exception;
    public void removeFromCurrentBooking(String roomId) throws Exception;
    public List<PmsBooking> getAllBookingsUnsecure(PmsBookingFilter state);
    public List<PmsBooking> getAllBookingsForLoggedOnUser();
    public RegistrationRules initBookingRules();
    public void addRepeatingData(PmsRepeatingData data) throws Exception;
    public List<Integer> getAvailabilityForType(String bookingItemId, Date startTime, Date endTime, Integer intervalInMinutes);
    public void toggleAddon(String itemId) throws Exception;
    public PmsBookingDateRange getDefaultDateRange();
    public List<Room> getAllRoomTypes(Date start, Date end);
    public void setBooking(PmsBooking addons) throws Exception;
    public PmsBooking getCurrentBooking();
    public PmsBooking startBooking();
    public PmsBooking completeCurrentBooking();
    public void returnedKey(String roomId);
    public Integer getNumberOfAvailable(String itemType, Date start, Date end);
    public void checkDoorStatusControl() throws Exception;
    public List<Integer> updateRoomByUser(String bookingId, PmsBookingRooms room) throws Exception;
    public List<PmsBookingAddonItem> getAddonsWithDiscount(String pmsBookingRoomId);
    
    
    public PmsAdditionalTypeInformation getAdditionalTypeInformationById(String typeId) throws Exception;
    
    public List<PmsAdditionalTypeInformation> getAdditionalTypeInformation() throws Exception;
    
    @Administrator
    public void saveAdditionalTypeInformation(PmsAdditionalTypeInformation info) throws Exception;
    
    @Administrator
    public void updateAddons(List<PmsBookingAddonItem> items, String bookingId) throws Exception;
    
    @Administrator
    public void massUpdatePrices(PmsPricing price, String bookingId) throws Exception;
    
    @Administrator
    public void mergeBookingsOnOrders();
    
    @Administrator
    public void checkForRoomsToClose();
    
    @Administrator
    public boolean closeItem(String id, Date start, Date end, String source);
    
    @Administrator
    public List<String> getpriceCodes();
    
    @Administrator
    public void createNewPricePlan(String code);
    
    @Administrator
    public void deletePricePlan(String code);
    
    @Administrator
    public List<PmsRoomSimple> getAllRoomsThatHasAddonsOfType(String type);
}
