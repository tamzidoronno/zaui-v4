package com.thundashop.core.pmsmanager;

import com.thundashop.core.arx.AccessLog;
import com.thundashop.core.bookingengine.data.RegistrationRules;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Customer;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.FilterOptions;
import com.thundashop.core.common.FilteredData;
import com.thundashop.core.common.ForceAsync;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.pmseventmanager.PmsEventFilter;
import com.thundashop.core.usermanager.data.User;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Property management system.<br>
 */

@GetShopApi
@GetShopMultiLayerSession
public interface IPmsManager {
    
    @Administrator
    public void checkInRoom(String pmsBookingRoomId);
    
    @Administrator
    public void setDefaultAddons(String bookingId);
    
    @Administrator
    public void markRoomAsCleaned(String itemId);
    
    @Administrator
    public void updatePriceMatrixOnRoom(String pmsBookingRoomId, LinkedHashMap<String,Double> priceMatrix);
    
    public void warnFailedBooking(PmsBooking booking);
    
    @Administrator
    public void setCurrentBooking(String bookingId);
    
    @Administrator
    public void simpleCompleteCurrentBooking();
    
    @Administrator
    public List<PmsRoomSimple> getAllRoomsOnOrder(String orderId);
    
    @Administrator
    public void markRoomAsCleanedWithoutLogging(String itemId);
            
    @Administrator
    public void undoLastCleaning(String itemId);
            
    @Administrator
    public void removeProductFromRoom(String pmsBookingRoomId, String productId);
    
    @Administrator
    public boolean hasLockSystemActive();
    
    @Administrator
    public Double getPriceForRoomWhenBooking(Date start, Date end, String itemType);
            
    @Administrator
    public void createAllVirtualOrders();
    
    @Administrator
    public void tryAddToEngine(String pmsBookingRoomId);
    
    @Administrator
    public FilteredData getAllCrmUsers(FilterOptions filter);
    
    @Administrator
    public void logEntryObject(PmsLog log);
    
    @Administrator
    public void forceMarkRoomAsCleaned(String itemId);
    
    @Administrator
    public void deleteAllBookings(String code);
    
    @Administrator
    public List<CleaningStatistics> getCleaningStatistics(Date start, Date end);
    
    @Administrator
    @ForceAsync
    public List<SimpleCleaningOverview> getSimpleCleaningOverview(Date start, Date end);
    
    @Administrator
    public String generateNewCodeForRoom(String roomId);
    
    @Administrator
    public List<SimpleInventory> getSimpleInventoryList(String roomName);
    
    @Administrator
    public List<PmsRoomSimple> getRoomsToSwap(String roomId, String moveToType);
    
    @Administrator
    public List<PmsCleaningHistory> getCleaningHistoryForItem(String itemId);
    
    @Administrator
    public String swapRoom(String roomId, List<String> roomIds);
    
    @Administrator
    public void sendConfirmation(String email, String bookingId, String type);
    
    @Administrator
    public void reportMissingInventory(List<SimpleInventory> inventories, String itemId, String roomId);
    
    @Administrator
    public void saveFilter(String name, PmsBookingFilter filter);
    
    @Administrator
    public List<PmsBookingFilter> getAllPmsFilters();
    
    @Administrator
    public void deletePmsFilter(String name);
    
    @Administrator
    public PmsBookingFilter getPmsBookingFilter(String name);
    
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
    public boolean removeFromWaitingList(String pmsRoomId);
    
    @Administrator
    public boolean addToWaitingList(String pmsRoomId);
    
    @Administrator
    public List<PmsBookingAddonViewItem> getItemsForView(String viewId, Date date);
    
    @Administrator
    public void removeAddonFromRoom(String id, String pmsBookingRooms);
        
    @Administrator
    public void markAddonDelivered(String id);
    
    @Administrator
    public void cancelRoom(String roomId);
    
    @Administrator
    public void checkOutRoom(String pmsBookingRoomId);
    
    @Administrator
    public void undoCheckOut(String pmsBookingRoomId);
    
    
    @Administrator
    public List<PmsAddonDeliveryLogEntry> getDeliveryLog(List<String> productIds, Date start, Date end);
    
    @Administrator
    public List<PmsAddonDeliveryLogEntry> getDeliveryLogByView(String viewId, Date start, Date end);
    
    @Administrator
    public void deleteDeliveryLogEntry(String id);
    
    @Administrator
    public void sendSmsToGuest(String guestId, String message);
    
    public List<PmsBooking> getAllBookings(PmsBookingFilter state);
    
    @Administrator
    public Integer getNumberOfCustomers(PmsBookingFilter state);
    
    @Administrator
    public List<PmsCustomerRow> getAllUsers(PmsBookingFilter filter);
    
    @Administrator
    public void createNewUserOnBooking(String bookingId, String name, String orgId);
    
    @Administrator
    public void addToWorkSpace(String pmsRoomId);
    
    @Administrator
    public List<PmsBookingRooms> getWorkSpaceRooms();
    
    @Administrator
    public User createUser(PmsNewUser newUser);
    
    public Date getEarliestEndDate(String pmsBookingRoomId);
    
    @Editor
    public void logEntry(String logText, String bookingId, String itemId);
    
    @Editor
    public PmsBooking getBookingFromRoom(String pmsBookingRoomId);
    
    public List<PmsRoomSimple> getMyRooms();
    
    public boolean isActive();
    
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
    
    @Administrator
    public void sendMessageOnRoom(String email, String title, String message, String roomId);
    
    @Administrator
    public void sendSmsOnRoom(String prefix, String phone, String message, String roomId);
    
    public void endRoom(String roomId);
    
    @Administrator
    public void splitBooking(List<String> roomIds);
    
    @Administrator
    public void splitStay(String roomId, Date splitDate);
    
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
    public void sendCode(String prefix, String phoneNumber, String roomId);
    
    @Administrator
    public void setGuestOnRoom(List<PmsGuests> guests, String bookingId, String roomId);
    
    @Administrator
    public void setGuestOnRoomWithoutModifyingAddons(List<PmsGuests> guests, String bookingId, String roomId);
    
    @Administrator
    public void updateAddonsBasedOnGuestCount(String pmsRoomId);
    
    @Administrator
    public void resetPriceForRoom(String pmsRoomId);
    
    @Administrator
    public void confirmBooking(String bookingId, String message);
    
    @Administrator
    public void unConfirmBooking(String bookingId, String message);
    
    @Administrator
    @ForceAsync
    public PmsStatistics getStatistics(PmsBookingFilter filter);
    
    public Date convertTextDate(String text);
    
    @Administrator
    public List<PmsBookingAddonItem> getAddonsAvailable();
    
    @Customer
    public String removeFromBooking(String bookingId, String roomId) throws Exception;
    
    @Editor
    public String getContract(String bookingId) throws Exception;
    
    public String getContractByLanguage(String language) throws Exception;
    
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
    @ForceAsync
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
    public void sendPaymentLinkWithText(String orderId, String bookingId, String email, String prefix, String phone, String message);
    
    @Administrator
    public void sendPaymentRequest(String bookingId, String email, String prefix, String phone, String message);
    
    @Administrator
    public String getMessage(String bookingId, String key);
    
    @Administrator
    public void failedChargeCard(String orderId, String bookingId);
    
    /**
     * Whenever a card has been fetched using the automated process this function is called 
     * DO NOT CHANGE IT!
     * @param bookingId 
     */
    @Administrator
    public void doChargeCardFromAutoBooking(String bookingId);
    
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
    public List<PmsRoomSimple> getSimpleRoomsForGroup(String bookingEngineId);
    
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
    public void markRoomDirty(String itemId) throws Exception;
    
    @Administrator
    public void setBookingByAdmin(PmsBooking booking, boolean keepRoomPrices) throws Exception;
    
    @Administrator
    public PmsActivityLines getActivitiesEntries(Date start, Date end);

    public void freezeSubscription(String pmsBookingRoomId, Date freezeUntil);
    
    public PmsPricing getPrices(Date start, Date end); 
    public PmsBooking getBooking(String bookingId);
    public PmsConfiguration getConfiguration();
    public void processor();
    public void hourlyProcessor();
    public String getCurrenctContract() throws Exception;
    public void removeFromCurrentBooking(String roomId) throws Exception;
    public List<PmsBooking> getAllBookingsUnsecure(PmsBookingFilter state);
    public List<PmsBooking> getAllBookingsForLoggedOnUser();
    public RegistrationRules initBookingRules();
    public void addRepeatingData(PmsRepeatingData data) throws Exception;
    public List<Integer> getAvailabilityForType(String bookingItemId, Date startTime, Date endTime, Integer intervalInMinutes);
    public PmsBookingDateRange getDefaultDateRange();
    public List<PmsBookingRooms> getAllRoomTypes(Date start, Date end);
    public void setBooking(PmsBooking addons) throws Exception;
    public PmsBooking getCurrentBooking();
    public PmsBooking startBooking();
    public PmsBooking completeCurrentBooking();
    public void returnedKey(String roomId);
    public Integer getNumberOfAvailable(String itemType, Date start, Date end);
    public void checkDoorStatusControl() throws Exception;
    public List<Integer> updateRoomByUser(String bookingId, PmsBookingRooms room) throws Exception;
    
    public PmsAdditionalTypeInformation getAdditionalTypeInformationById(String typeId) throws Exception;
    
    public List<PmsAdditionalTypeInformation> getAdditionalTypeInformation() throws Exception;
    
    @Administrator
    public void saveAdditionalTypeInformation(PmsAdditionalTypeInformation info) throws Exception;
        
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
    public ConferenceData getConferenceData(String bookingId);
    
    @Administrator
    public void saveConferenceData(ConferenceData data);
    
    @Administrator
    public List<ConferenceData> getFutureConferenceData(Date fromDate);
        
    /**
     * Key = date / day
     * @return 
     */
    @Administrator
    public Map<Long, List<ConferenceData>> getGroupedConferenceData(Date fromDate);
    
    @Administrator
    public void detachOrderFromBooking(String bookingId, String orderId);
    
    public LinkedList<TimeRepeaterDateRange> generateRepeatDateRanges(TimeRepeaterData data);
    
    @Administrator
    public PmsRoomTypeAccessory saveAccessory(PmsRoomTypeAccessory accessory);
    
    public List<PmsRoomTypeAccessory> getAccesories();
    
    @Administrator
    public PmsBookingRooms getPrecastedRoom(String roomId, String bookingItemTypeId, Date from, Date to);
    
    @Administrator
    public void setNewStartDateAndAssignToRoom(String roomId, Date newStartDate, String bookingItemId);
    
    @Administrator
    public List<PmsGuestOption> findRelatedGuests(PmsGuests guest);
    
    @Administrator
    public List<PmsGuestOption> findRelatedByUserId(String userId);
    
    @Administrator
    public List<String> addSuggestedUserToBooking(String userId);
    
    @Administrator
    public void transferTicketsAsAddons();
    
    @Administrator
    public List<PmsBookingAddonItem> createAddonsThatCanBeAddedToRoom(String productId, String pmsBookingRoomId);

    @Administrator
    public List<PmsRoomSimple> getAllRoomsThatHasAddonsOfType(String type);

    @Administrator
    public void updateAddons(List<PmsBookingAddonItem> items, String bookingId) throws Exception;
    
    /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count 
     */
    @Administrator
    public void addProductToRoom(String productId, String pmsRoomId, Integer count);
   
    @Administrator
    public void addAddonToRoom(PmsBookingAddonItem addon, String pmsRoomId);
    
    @Administrator
    public List<PmsBookingAddonItem> getAddonsForRoom(String roomId);
    
    @Administrator
    public void checkForDeadCodesApac();
            
    public List<PmsBookingAddonItem> getAddonsWithDiscount(String pmsBookingRoomId);
    public List<PmsBookingAddonItem> getAddonsWithDiscountForBooking(String pmsBookingRoomId);
    public void toggleAddon(String itemId) throws Exception;
    public void addAddonToCurrentBooking(String itemtypeId) throws Exception;
    public void addAddonsToBooking(Integer type, String roomId, boolean remove);
    public void updateAddonsCountToBooking(Integer type, String roomId, Integer count);


    public void orderCreated(String orderId);
    public void orderChanged(String orderId);    
    public boolean hasNoBookings();    
    
    @Administrator
    public void transferFromOldCodeToNew(String pmsBookingRoomId);
    
    @Administrator
    public void generatePgaAccess(String pmsBookingId, String pmsBookingRoomId);
    
    @Administrator
    public void removePgaAccess(String pmsBookingId, String pmsBookingRoomId);
    
    @Administrator
    public void printCode(String gdsDeviceId, String pmsBookingRoomId);
    
    @Administrator
    public void markIgnoreUnsettledAmount(String bookingId);
    
    @Administrator
    public void markOtaPaymentsAutomaticallyPaidOnCheckin(Date start, Date end);
    
    @Administrator
    public void cleanupOrdersThatDoesNoLongerExists();
    
    @Administrator
    public List<PmsWubookCCardData> getCardsToSave();
        
    @Administrator
    public void wubookCreditCardIsInvalid(String bookingId, String reason);

    @Administrator
    public List<PmsBooking> getBookingsWithUnsettledAmountBetween(Date start, Date end);
    
    @Administrator
    public List<UnsettledRoomQuery> getAllRoomsWithUnsettledAmount(Date start, Date end);
    
    /**
     * If the autoassigned routines has failed for some reason, this will reset the status
     * and let the system retry to autoassigned the failed rooms.
     */
    @Administrator
    public void resetCheckingAutoAssignedStatus();
    
    @Administrator
    public void resetDeparmentsOnOrders();
    
    @Administrator
    public List<String> getExtraOrderIds(String pmsBookingId);
    
    @Administrator
    public List<PmsBooking> getBookingsFromGroupInvoicing(String orderId);
    
    /**
     * Returns true if there are anything in
     * the order that related to the specified room id.
     * 
     * @param pmsBookingRoomsId
     * @param orderId
     * @return 
     */
    @Administrator
    public boolean doesOrderCorrolateToRoom(String pmsBookingRoomsId, String orderId);
    
    @Administrator
    public PmsRoomPaymentSummary getSummary(String pmsBookingId, String pmsBookingRoomId);
    
    @Administrator
    public PmsRoomPaymentSummary getSummaryWithoutAccrued(String pmsBookingId, String pmsBookingRoomId);
    
    @Administrator
    public String createOrderFromCheckout(List<PmsOrderCreateRow> row, String paymentMethodId, String userId);
    
    @Administrator
    public void recheckOrdersAddedToBooking();
    
    @Administrator
    public void toggleAutoCreateOrders(String bookingId, String roomId);

    @Administrator
    public List<PmsRoomPaymentSummary> getSummaryForAllRooms(String pmsBookingId);
    
    @Administrator
    public void attachOrderToBooking(String bookingId, String orderId);
    
    @Administrator
    public boolean moveRoomToBooking(String roomId, String bookingId);
    
    public void setBestCouponChoiceForCurrentBooking() throws Exception;
    
}