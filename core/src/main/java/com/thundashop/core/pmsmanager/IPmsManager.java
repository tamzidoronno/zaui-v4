package com.thundashop.core.pmsmanager;

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
    public List<Room> getAllRoomTypes(long start, long end);
    public void setBooking(PmsBooking addons) throws Exception;
    public PmsBooking getCurrentBooking();
    public PmsBooking startBooking();
    public HashMap<String,Integer> validateCurrentBooking();
    public Integer completeCurrentBooking();
    
    @Administrator
    public List<PmsBooking> getAllBookings(PmsBookingFilter state);
    
    @Administrator
    public PmsBooking getBooking(String bookingId);
    
    @Administrator
    public String setNewRoomType(String roomId, String bookingId, String newType);
    
    @Administrator
    public String changeDates(String roomId, String bookingId, Date start, Date end);
    
    @Administrator
    public String setVisitors(String roomId, String bookingId, Integer numberOfVisitors, List<PmsGuests> guests);
    
    @Administrator
    public String updatePrice(String roomId, String bookingId, Double price);
    
    @Administrator
    public String updateType(String roomId, String bookingId, Integer priceType);
}
