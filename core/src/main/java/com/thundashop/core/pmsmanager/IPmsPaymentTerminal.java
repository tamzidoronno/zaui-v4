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
    
    public boolean updateBooking(PmsBooking booking, User user, Company company);
}
