package com.thundashop.core.pmsmanager;

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
    public List<Room> getAllRoomTypes(Date start, Date end);
    public void setBooking(PmsBooking addons) throws Exception;
    public PmsBooking getCurrentBooking();
    public PmsBooking startBooking();
    public Integer completeCurrentBooking();
    
    @Administrator
    public void markRoomAsCleaned(String itemId);
    
    @Administrator
    public List<PmsBooking> getAllBookings(PmsBookingFilter state);
    
    public List<PmsBooking> getAllBookingsUnsecure(PmsBookingFilter state);
    
    @Administrator
    public PmsBooking getBooking(String bookingId);
    
    @Administrator
    public String setNewRoomType(String roomId, String bookingId, String newType);
    
    @Administrator
    public String setBookingItem(String roomId, String bookingId, String itemId);
    
    @Administrator
    public String changeDates(String roomId, String bookingId, Date start, Date end);
    
    @Administrator
    public void deleteBooking(String bookingId);
    
    @Administrator
    public void saveBooking(PmsBooking booking);
    
    public PmsPricing getPrices(Date start, Date end); 
    
    @Administrator
    public PmsPricing setPrices(PmsPricing prices); 
    
    @Administrator
    public String createOrder(String bookingId, NewOrderFilter filter);
    
    public PmsConfiguration getConfiguration();
    
    @Administrator
    public void saveConfiguration(PmsConfiguration notifications);
    
    @Administrator
    public void confirmBooking(String bookingId, String message);
    
    @Administrator
    public void unConfirmBooking(String bookingId, String message);
    
    @Administrator
    public PmsStatistics getStatistics(PmsBookingFilter filter);
    
    public void addAddonToCurrentBooking(String itemtypeId) throws Exception;
    public void removeFromCurrentBooking(String roomId) throws Exception;
    
    @Administrator
    public String getContract(String bookingId) throws Exception;
    public String getCurrenctContract() throws Exception;
    
    public void processor();
    
    @Administrator
    public PmsIntervalResult getIntervalAvailability(PmsIntervalFilter filter);
    
    @Administrator
    public Boolean isClean(String itemId);
    
    @Administrator
    public List<PmsAdditionalItemInformation> getAllAdditionalInformationOnRooms();
    
    @Administrator
    public List<PmsBookingRooms> getRoomsNeedingIntervalCleaning(Date day);
    
    public RegistrationRules initBookingRules();
    
    public List<TimeRepeaterDateRange> addRepeatingData(PmsRepeatingData data);
}
