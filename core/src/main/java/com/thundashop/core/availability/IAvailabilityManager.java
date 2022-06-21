package com.thundashop.core.availability;

import com.thundashop.core.bookingengine.data.RegistrationRules;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.*;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pmsmanager.*;
import com.thundashop.core.usermanager.data.User;

import java.util.*;

/**
 * Availability management system.<br>
 */

@GetShopApi
@GetShopMultiLayerSession
public interface IAvailabilityManager {
    
    @Administrator
    void checkInRoom(String pmsBookingRoomId);
    
    @Administrator
    boolean hasLockAccessGroupConnected(String typeId);
    
    @Administrator
    void reinstateStay(String pmsBookingRoomId, Integer minutes);
    
    @Administrator
    void setDefaultAddons(String bookingId);
    
    @Administrator
    void addProductsToAddons();
    
    @Administrator
    void markRoomAsCleaned(String itemId);
    
    @Administrator
    void updateCommentOnBooking(String bookingId, String commentId, String newText);
    
    @Administrator
    void updatePriceMatrixOnRoom(String pmsBookingRoomId, LinkedHashMap<String,Double> priceMatrix);
    
    void warnFailedBooking(PmsBooking booking);
    
    @Administrator
    String getBookingSummaryText(String pmsBookingId);
    
    @Administrator
    void setCurrentBooking(String bookingId);
    
    @Administrator
    void simpleCompleteCurrentBooking();
    
    @Administrator
    void togglePrioritizedRoom(String roomId);
    
    @Administrator
    List<PmsRoomSimple> getAllRoomsOnOrder(String orderId);
    
    @Administrator
    void markRoomAsCleanedWithoutLogging(String itemId);
            
    @Administrator
    void undoLastCleaning(String itemId);
            
    @Administrator
    void removeProductFromRoom(String pmsBookingRoomId, String productId);
    
    @Administrator
    boolean hasLockSystemActive();
    
    @Administrator
    Double getPriceForRoomWhenBooking(Date start, Date end, String itemType);
            
    @Administrator
    void createAllVirtualOrders();
    
    @Administrator
    void tryAddToEngine(String pmsBookingRoomId);
    
    @Administrator
    FilteredData getAllCrmUsers(FilterOptions filter);
    
    @Administrator
    void logEntryObject(PmsLog log);
    
    @Administrator
    void forceMarkRoomAsCleaned(String itemId);
    
    @Administrator
    void deleteAllBookings(String code);
    
    @Administrator
    List<CleaningStatistics> getCleaningStatistics(Date start, Date end);
    
    @Administrator
    @ForceAsync
    List<SimpleCleaningOverview> getSimpleCleaningOverview(Date start, Date end);
    
    @Administrator
    String generateNewCodeForRoom(String roomId);
    
    @Administrator
    List<SimpleInventory> getSimpleInventoryList(String roomName);
    
    @Administrator
    void addToBlockList(PmsBlockedUser block);
    
    @Administrator
    void removeFromBlockList(String blockedId);
    
    @Administrator
    List<PmsRoomSimple> getRoomsToSwap(String roomId, String moveToType);
    
    @Administrator
    List<PmsCleaningHistory> getCleaningHistoryForItem(String itemId);
    
    @Administrator
    String swapRoom(String roomId, List<String> roomIds);
    
    @Administrator
    void sendConfirmation(String email, String bookingId, String type);
    
    @Administrator
    void reportMissingInventory(List<SimpleInventory> inventories, String itemId, String roomId);
    
    @Administrator
    void saveFilter(String name, PmsBookingFilter filter);
    
    @Administrator
    List<PmsBookingFilter> getAllPmsFilters();
    
    @Administrator
    void deletePmsFilter(String name);
    
    @Administrator
    PmsBookingFilter getPmsBookingFilter(String name);
    
    @Administrator
    void removeAddonFromRoomById(String addonId, String roomId);
    
    @Administrator
    void removeAddonFromRoomByIds(List<String> addonId, String roomId);
    
    @Administrator
    void saveCareTakerJob(PmsCareTaker job);
    
    @Administrator
    void removeCareTakerJob(String jobId);
    
    @Administrator
    List<PmsCareTaker> getCareTakerJobs();
    
    @Administrator
    PmsCareTaker getCareTakerJob(String id);
    
    @Administrator
    void completeCareTakerJob(String id);
    
    @Administrator
    PmsAdditionalItemInformation getAdditionalInfo(String itemId);
    
    @Administrator
    boolean removeFromWaitingList(String pmsRoomId);
    
    @Administrator
    boolean addToWaitingList(String pmsRoomId);
    
    @Administrator
    List<PmsBookingAddonViewItem> getItemsForView(String viewId, Date date);
    
    @Administrator
    void removeAddonFromRoom(String id, String pmsBookingRooms);
        
    @Administrator
    void markAddonDelivered(String id);
    
    @Administrator
    void cancelRoom(String roomId);
    
    @Administrator
    void checkOutRoom(String pmsBookingRoomId);
    
    @Administrator
    void undoCheckOut(String pmsBookingRoomId);
    
    @Administrator
    List<PmsAddonDeliveryLogEntry> getDeliveryLog(List<String> productIds, Date start, Date end);
    
    @Administrator
    List<PmsAddonDeliveryLogEntry> getDeliveryLogByView(String viewId, Date start, Date end);
    
    @Administrator
    void deleteDeliveryLogEntry(String id);
    
    @Administrator
    void sendSmsToGuest(String guestId, String message);
    
    List<PmsBooking> getAllBookings(PmsBookingFilter state);
    
    @Administrator
    Integer getNumberOfCustomers(PmsBookingFilter state);
    
    @Administrator
    List<PmsCustomerRow> getAllUsers(PmsBookingFilter filter);
    
    @Administrator
    void createNewUserOnBooking(String bookingId, String name, String orgId);
    
    @Administrator
    boolean willAutoDelete(String pmsBookingId);
    
    @Administrator
    void addToWorkSpace(String pmsRoomId);
    
    @Administrator
    List<PmsBookingRooms> getWorkSpaceRooms();
    
    @Administrator
    User createUser(PmsNewUser newUser);
    
    Date getEarliestEndDate(String pmsBookingRoomId);
    
    @Editor
    void logEntry(String logText, String bookingId, String itemId);
    
    @Editor
    PmsBooking getBookingFromRoom(String pmsBookingRoomId);
    
    List<PmsRoomSimple> getMyRooms();
    
    boolean isActive();
    
    @Administrator
    void fixAllOrdersWithoutGoToPaymentId();
    
    @Editor
    PmsBooking getBookingWithOrderId(String orderId);
    
    @Editor
    PmsBooking getBookingFromRoomIgnoreDeleted(String roomId);
    
    @Administrator
    String setBookingItemAndDate(String roomId, String itemId, boolean split, Date start, Date end);
    
    @Administrator
    String addCartItemToRoom(CartItem item, String pmsBookingRoomId, String addedBy);
    
    @Administrator
    String setNewRoomType(String roomId, String bookingId, String newType);
    
    @Administrator
    List<PmsRoomSimple> getRoomsNeedingIntervalCleaningSimple(Date day);
    
    @Administrator
    void sendMessage(String bookingId, String email, String title, String message);
    
    @Administrator
    void sendMessageOnRoom(String email, String title, String message, String roomId);
    
    @Administrator
    void sendSmsOnRoom(String prefix, String phone, String message, String roomId);
    
    void endRoom(String roomId);
    
    @Administrator
    void splitBooking(List<String> roomIds);
    
    @Administrator
    void splitStay(String roomId, Date splitDate);
    
    @Administrator
    String setBookingItem(String roomId, String bookingId, String itemId, boolean split);
    
    @Administrator
    PmsBookingRooms changeDates(String roomId, String bookingId, Date start, Date end);
    
    @Administrator
    void deleteBooking(String bookingId);
    
    @Administrator
    void createChannel(String channel);
    
    @Administrator
    void removeChannel(String channel);
    
    @Administrator
    void saveBooking(PmsBooking booking);
    
    void endRoomWithDate(String pmsRoomId, Date date);
    
    @Administrator
    PmsPricing setPrices(String code, PmsPricing prices);
    
    @Administrator
    PmsPricing getPricesByCode(String code, Date start, Date end);
    
    @Administrator
    String createOrder(String bookingId, NewOrderFilter filter);
    
    @Administrator
    void saveConfiguration(PmsConfiguration notifications);
    
    @Administrator
    void sendCode(String prefix, String phoneNumber, String roomId);
    
    @Administrator
    void setGuestOnRoom(List<PmsGuests> guests, String bookingId, String roomId);
    
    @Administrator
    void setGuestOnRoomWithoutModifyingAddons(List<PmsGuests> guests, String bookingId, String roomId);
    
    @Administrator
    void updateAddonsBasedOnGuestCount(String pmsRoomId);
    
    @Administrator
    void resetPriceForRoom(String pmsRoomId);
    
    @Administrator
    void confirmBooking(String bookingId, String message);
    
    @Administrator
    void unConfirmBooking(String bookingId, String message);
    
    @Administrator
    @ForceAsync
    PmsStatistics getStatistics(PmsBookingFilter filter);
    
    Date convertTextDate(String text);
    
    @Administrator
    List<PmsBookingAddonItem> getAddonsAvailable();
    
    @Customer
    String removeFromBooking(String bookingId, String roomId) throws Exception;
    
    @Editor
    String getContract(String bookingId) throws Exception;
    
    String getContractByLanguage(String language) throws Exception;
    
    @Editor
    PmsIntervalResult getIntervalAvailability(PmsIntervalFilter filter);
    
    @Editor
    Boolean isClean(String itemId);
    
    @Editor
    Boolean isUsedToday(String itemId);
    
    @Editor
    List<PmsAdditionalItemInformation> getAllAdditionalInformationOnRooms();
    
    @Editor
    List<RoomCleanedInformation> getAllRoomsNeedCleaningToday();
    
    @Administrator
    void updateAdditionalInformationOnRooms(PmsAdditionalItemInformation info);
    
    HashMap<String, String> getChannelMatrix();
    
    @Editor
    List<PmsBookingRooms> getRoomsNeedingIntervalCleaning(Date day);
    
    @Editor
    @ForceAsync
    List<PmsBookingRooms> getRoomsNeedingCheckoutCleaning(Date day);
    
    @Administrator
    String addBookingItem(String bookingId, String item, Date start, Date end);
    
    @Administrator
    String addBookingItemType(String bookingId, String item, Date start, Date end, String guestInfoFromRoom);
    
    @Editor
    String getDefaultMessage(String bookingId);
    
    @Editor
    void doNotification(String key, String bookingId);
    
    @Editor
    void addComment(String bookingId, String comment);
    
    @Editor
    void addCommentToRoom(String roomId, String comment);
    
    @Administrator
    String createPrepaymentOrder(String bookingId);
    
    @Administrator
    void sendPaymentLink(String orderId, String bookingId, String email, String prefix, String phone);
    
    @Administrator
    void sendPaymentLinkWithText(String orderId, String bookingId, String email, String prefix, String phone, String message);
    
    @Administrator
    void sendPaymentRequest(String bookingId, String email, String prefix, String phone, String message);
    
    @Administrator
    String getMessage(String bookingId, String key);
    
    @Administrator
    void failedChargeCard(String orderId, String bookingId);
    
    /**
     * Whenever a card has been fetched using the automated process this function is called 
     * DO NOT CHANGE IT!
     * @param bookingId 
     */
    @Administrator
    void doChargeCardFromAutoBooking(String bookingId);
    
    @Administrator
    void sendMissingPayment(String orderId, String bookingId);
    
    @Editor
    List<PmsLog> getLogEntries(PmsLog filter);
    
    @Administrator
    List<PmsBookingRooms> updateRepeatingDataForBooking(PmsRepeatingData data, String bookingId);
     
    @Administrator
    void setNewCleaningIntervalOnRoom(String roomId, Integer interval);
    @Administrator
    PmsBookingRooms getRoomForItem(String itemId, Date atTime);
    
    @Administrator
    PmsBooking getBookingFromBookingEngineId(String bookingEngineId);
    
    @Administrator
    void undeleteBooking(String bookingId);
    
    @Administrator
    List<PmsRoomSimple> getSimpleRooms(PmsBookingFilter filter);
    
    @Administrator
    List<PmsRoomSimple> getSimpleRoomsForGroup(String bookingEngineId);
    
    @Administrator
    void sendMessageToAllTodaysGuests(String message);
    
    @Administrator
    void markKeyDeliveredForAllEndedRooms();
    
    @Administrator
    void changeInvoiceDate(String roomId, Date newDate);
    
    @Administrator
    @ForceAsync
    void checkIfGuestHasArrived() throws Exception;
    
    @Administrator
    void sendStatistics() throws Exception;
    
    @Administrator
    void markRoomDirty(String itemId) throws Exception;
    
    @Administrator
    void setBookingByAdmin(PmsBooking booking, boolean keepRoomPrices) throws Exception;
    
    @Administrator
    PmsActivityLines getActivitiesEntries(Date start, Date end);

    @Administrator
    boolean updatePrices(List<PmsPricingDayObject> prices);
    
    void freezeSubscription(String pmsBookingRoomId, Date freezeUntil);
    
    PmsPricing getPrices(Date start, Date end);
    PmsBooking getBooking(String bookingId);
    PmsConfiguration getConfiguration();
    void processor();
    void hourlyProcessor();
    String getCurrenctContract() throws Exception;
    void removeFromCurrentBooking(String roomId) throws Exception;
    List<PmsBooking> getAllBookingsUnsecure(PmsBookingFilter state);
    List<PmsBooking> getAllBookingsForLoggedOnUser();
    RegistrationRules initBookingRules();
    void addRepeatingData(PmsRepeatingData data) throws Exception;
    List<Integer> getAvailabilityForType(String bookingItemId, Date startTime, Date endTime, Integer intervalInMinutes);
    PmsBookingDateRange getDefaultDateRange();
    List<PmsBookingRooms> getAllRoomTypes(Date start, Date end);
    void setBooking(PmsBooking addons) throws Exception;
    PmsBooking getCurrentBooking();
    PmsBooking startBooking();
    PmsBooking completeCurrentBooking();
    void returnedKey(String roomId);
    Integer getNumberOfAvailable(String itemType, Date start, Date end);
    void checkDoorStatusControl() throws Exception;
    List<Integer> updateRoomByUser(String bookingId, PmsBookingRooms room) throws Exception;
    
    PmsAdditionalTypeInformation getAdditionalTypeInformationById(String typeId) throws Exception;
    
    List<PmsAdditionalTypeInformation> getAdditionalTypeInformation() throws Exception;
    
    @Administrator
    void saveAdditionalTypeInformation(PmsAdditionalTypeInformation info) throws Exception;
        
    @Administrator
    void massUpdatePrices(PmsPricing price, String bookingId) throws Exception;
    
    @Administrator
    void mergeBookingsOnOrders();
    
    @Administrator
    void checkForRoomsToClose();
    
    @Administrator
    boolean closeItem(String id, Date start, Date end, String source);
    
    @Administrator
    List<String> getpriceCodes();
    
    @Administrator
    void createNewPricePlan(String code);
    
    @Administrator
    void deletePricePlan(String code);
        
    @Administrator
    ConferenceData getConferenceData(String bookingId);
    
    @Administrator
    void saveConferenceData(ConferenceData data);
    
    @Administrator
    List<ConferenceData> getFutureConferenceData(Date fromDate);
        
    /**
     * Key = date / day
     * @return 
     */
    @Administrator
    Map<Long, List<ConferenceData>> getGroupedConferenceData(Date fromDate);
    
    @Administrator
    void detachOrderFromBooking(String bookingId, String orderId);
    
    LinkedList<TimeRepeaterDateRange> generateRepeatDateRanges(TimeRepeaterData data);
    
    @Administrator
    PmsRoomTypeAccessory saveAccessory(PmsRoomTypeAccessory accessory);
    
    List<PmsRoomTypeAccessory> getAccesories();
    
    @Administrator
    PmsBookingRooms getPrecastedRoom(String roomId, String bookingItemTypeId, Date from, Date to);
    
    @Administrator
    void setNewStartDateAndAssignToRoom(String roomId, Date newStartDate, String bookingItemId);
    
    @Administrator
    List<PmsGuestOption> findRelatedGuests(PmsGuests guest);
    
    @Administrator
    List<PmsGuestOption> findRelatedByUserId(String userId);
    
    @Administrator
    List<String> addSuggestedUserToBooking(String userId);
    
    @Administrator
    void transferTicketsAsAddons();
    
    @Administrator
    List<PmsBookingAddonItem> createAddonsThatCanBeAddedToRoom(String productId, String pmsBookingRoomId);

    @Administrator
    List<PmsRoomSimple> getAllRoomsThatHasAddonsOfType(String type);

    @Administrator
    void updateAddons(List<PmsBookingAddonItem> items, String bookingId) throws Exception;
    
    /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count 
     */
    @Administrator
    void addProductToRoom(String productId, String pmsRoomId, Integer count);
   
    @Administrator
    void addAddonToRoom(PmsBookingAddonItem addon, String pmsRoomId);
    
    @Administrator
    List<PmsBookingAddonItem> getAddonsForRoom(String roomId);
    
    @Administrator
    void checkForDeadCodesApac();
            
    List<PmsBookingAddonItem> getAddonsWithDiscount(String pmsBookingRoomId);
    List<PmsBookingAddonItem> getAddonsWithDiscountForBooking(String pmsBookingRoomId);
    void toggleAddon(String itemId) throws Exception;
    void addAddonToCurrentBooking(String itemtypeId) throws Exception;
    void addAddonsToBooking(Integer type, String roomId, boolean remove);
    
    @Administrator
    void addAddonsToBookingIgnoreRestriction(Integer type, String roomId, boolean remove);
    
    void updateAddonsCountToBooking(Integer type, String roomId, Integer count);


    void orderCreated(String orderId);
    void orderChanged(String orderId);
    boolean hasNoBookings();
    
    @Administrator
    void transferFromOldCodeToNew(String pmsBookingRoomId);
    
    @Administrator
    void generatePgaAccess(String pmsBookingId, String pmsBookingRoomId);
    
    @Administrator
    void removePgaAccess(String pmsBookingId, String pmsBookingRoomId);
    
    @Administrator
    void printCode(String gdsDeviceId, String pmsBookingRoomId);
    
    @Administrator
    void markIgnoreUnsettledAmount(String bookingId);
    
    @Administrator
    void markOtaPaymentsAutomaticallyPaidOnCheckin(Date start, Date end);
    
    @Administrator
    void cleanupOrdersThatDoesNoLongerExists();
    
    @Administrator
    List<PmsWubookCCardData> getCardsToSave();
        
    @Administrator
    void wubookCreditCardIsInvalid(String bookingId, String reason);

    @Administrator
    List<PmsBooking> getBookingsWithUnsettledAmountBetween(Date start, Date end);
    
    @Administrator
    List<UnsettledRoomQuery> getAllRoomsWithUnsettledAmount(Date start, Date end);
    
    /**
     * If the autoassigned routines has failed for some reason, this will reset the status
     * and let the system retry to autoassigned the failed rooms.
     */
    @Administrator
    void resetCheckingAutoAssignedStatus();
    
    @Administrator
    void resetDeparmentsOnOrders();
    
    @Administrator
    List<String> getExtraOrderIds(String pmsBookingId);
    
    @Administrator
    List<PmsBooking> getBookingsFromGroupInvoicing(String orderId);
    
    /**
     * Returns true if there are anything in
     * the order that related to the specified room id.
     * 
     * @param pmsBookingRoomsId
     * @param orderId
     * @return 
     */
    @Administrator
    boolean doesOrderCorrolateToRoom(String pmsBookingRoomsId, String orderId);
    
    @Administrator
    PmsRoomPaymentSummary getSummary(String pmsBookingId, String pmsBookingRoomId);
    
    @Administrator
    PmsRoomPaymentSummary getSummaryWithoutAccrued(String pmsBookingId, String pmsBookingRoomId);
    
    @Administrator
    @ForceAsync
    String createOrderFromCheckout(List<PmsOrderCreateRow> row, String paymentMethodId, String userId);
    
    @Administrator
    void recheckOrdersAddedToBooking();
    
    @Administrator
    void toggleAutoCreateOrders(String bookingId, String roomId);

    @Administrator
    List<PmsRoomPaymentSummary> getSummaryForAllRooms(String pmsBookingId);
    
    @Administrator
    void attachOrderToBooking(String bookingId, String orderId);
    
    @Administrator
    boolean moveRoomToBooking(String roomId, String bookingId);
    
    String setBestCouponChoiceForCurrentBooking() throws Exception;
    
    @Administrator
    Order updateOrderDetails(String bookingId, String orderId, boolean preview);
    
    @Administrator
    void moveAllOnUserToUser(String tomainuser, String secondaryuser);
    
    @Administrator
    List<PmsBooking> getConferenceBookings(PmsConferenceFilter filter);
    
    @Administrator
    PmsBooking getconferenceBooking(String conferenceId);
}