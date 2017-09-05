package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import java.util.HashMap;
import java.util.List;


/**
 * Property management system.<br>
 */
@GetShopApi
@GetShopMultiLayerSession
public interface IPmsPaymentTerminal {
    
    @Administrator
    public List<PaymentTerminalSearchResult> findBookings(String phoneNumber);
    
    @Administrator
    public PmsBooking getBooking(String bookingId);
    
    @Administrator
    public String payIndividualRoom(String pmsBookingRoomId);
    
    @Administrator
    public List<PmsOrderSummary> getOrderSummary(String bookingId);
    
    @Administrator
    public void addProductToRoom(String productId, String pmsBookingRoomId);
    
    @Administrator
    public void removeProductFromRoom(String productId, String pmsBookingRoomId);
    
    public PmsBooking startBooking(PmsStartBooking data);
    
    /**
     * Returns all rooms available as a map where the room size (number of guest) is returned as second result.
     * Sorted as largest room first.
     * @param data
     * @return 
     */
    public HashMap<Integer, Integer> getMaxNumberOfRooms(PmsStartBooking data);
    
    /**
     * Returns a map of types that the booking can be changed to with number of rooms available accioated to it.
     * @param pmsBookingRoomId
     * @return 
     */
    public HashMap<String, Integer> getRoomTypesThatRoomCanBeChangedTo(String pmsBookingRoomId);
    
    @Administrator
    public PmsBookingRooms changeRoomTypeOnRoom(String pmsBookingRoomId, String newTypeId);
    
    /**
     * Change the number of guests on a room.
     * @param pmsBookingRoomId
     * @param count 
     * @return The new price for the room.
     */
    @Administrator
    public Double changeGuestCountOnRoom(String pmsBookingRoomId, Integer count);
    
    public boolean updateBooking(PmsBooking booking, User user, Company company);
    
    @Administrator
    public void printReciept(String orderId);
}
