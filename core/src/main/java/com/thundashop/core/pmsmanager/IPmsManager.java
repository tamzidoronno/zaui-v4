package com.thundashop.core.pmsmanager;

import com.thundashop.core.arx.AccessLog;
import com.thundashop.core.bookingengine.data.RegistrationRules;
import com.thundashop.core.common.Administrator;
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
    public List<PmsBooking> getAllBookings(PmsBookingFilter state);
    
    @Administrator
    public void logEntry(String logText, String bookingId, String itemId);
    
    @Administrator
    public PmsBooking getBooking(String bookingId);
    
    @Administrator
    public PmsBooking getBookingFromRoom(String pmsBookingRoomId);
    
    @Administrator
    public String setNewRoomType(String roomId, String bookingId, String newType);
    
    @Administrator
    public String setBookingItem(String roomId, String bookingId, String itemId);
    
    @Administrator
    public PmsBookingRooms changeDates(String roomId, String bookingId, Date start, Date end);
    
    @Administrator
    public void deleteBooking(String bookingId);
    
    @Administrator
    public void saveBooking(PmsBooking booking);
    
    public PmsPricing getPrices(Date start, Date end); 
    
    @Administrator
    public PmsPricing setPrices(PmsPricing prices); 
    
    @Administrator
    public String createOrder(String bookingId, NewOrderFilter filter);
    
    @Administrator
    public void saveConfiguration(PmsConfiguration notifications);
    
    @Administrator
    public void confirmBooking(String bookingId, String message);
    
    @Administrator
    public void unConfirmBooking(String bookingId, String message);
    
    @Administrator
    public PmsStatistics getStatistics(PmsBookingFilter filter);
    
    @Administrator
    public void removeFromBooking(String bookingId, String roomId) throws Exception;
    
    @Administrator
    public String getContract(String bookingId) throws Exception;
    
    @Administrator
    public PmsIntervalResult getIntervalAvailability(PmsIntervalFilter filter);
    
    @Administrator
    public Boolean isClean(String itemId);
    
    @Administrator
    public List<PmsAdditionalItemInformation> getAllAdditionalInformationOnRooms();
    
    @Administrator
    public List<PmsBookingRooms> getRoomsNeedingIntervalCleaning(Date day);
    
    @Administrator
    public List<PmsBookingRooms> getRoomsNeedingCheckoutCleaning(Date day);
    
    @Administrator
    public String addBookingItem(String bookingId, String item, Date start, Date end);
    
    @Administrator
    public String addBookingItemType(String bookingId, String item, Date start, Date end);
    
    @Administrator
    public String getDefaultMessage(String bookingId);
    
    @Administrator
    public void doNotification(String key, String bookingId);
    
    @Administrator
    public void addComment(String bookingId, String comment);
    
    @Administrator
    public String createPrepaymentOrder(String bookingId);
    
    @Administrator
    public List<PmsLog> getLogEntries(PmsLog filter);
    
    @Administrator
    public List<PmsBookingRooms> updateRepeatingDataForBooking(PmsRepeatingData data, String bookingId);
     
    @Administrator
    public void setNewCleaningIntervalOnRoom(String roomId, Integer interval);
    @Administrator
    public PmsBookingRooms getRoomForItem(String itemId, Date atTime);
    
    @Administrator
    public void undeleteBooking(String bookingId);
    
    public PmsConfiguration getConfiguration();
    public void processor();
    public String getCurrenctContract() throws Exception;
    public void addAddonToCurrentBooking(String itemtypeId) throws Exception;
    public void removeFromCurrentBooking(String roomId) throws Exception;
    public List<PmsBooking> getAllBookingsUnsecure(PmsBookingFilter state);
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
    public void handleDoorControl(String doorId, List<AccessLog> accessLogs) throws Exception;
    public void checkDoorStatusControl() throws Exception;
}
