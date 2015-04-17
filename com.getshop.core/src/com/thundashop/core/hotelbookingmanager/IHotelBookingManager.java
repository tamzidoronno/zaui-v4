package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.ordermanager.OrderManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@GetShopApi
public interface IHotelBookingManager {
    /**
     * Check if a room is available in the given time periode.
     * @param startDate The first day unix timestamp.
     * @param endDate The last day unix timestamp.
     * @param type The type of room to search for-
     * @return Number of avilable rooms. -1, the date is set before todays date, -2 end date is before the start date.
     * @throws ErrorException 
     */
    public Integer checkAvailable(long startDate, long endDate, String productId, AdditionalBookingInformation additional) throws ErrorException;
    
    /**
     * 
     * @param roomType
     * @param startDate The first day unix timestamp.
     * @param endDate The last day unix timestamp.
     * @param count The number of rooms to book.
     * @throws ErrorException 
     */
    public Integer reserveRoom(long startDate, long endDate, Integer count) throws ErrorException;
    
    public GlobalBookingSettings getBookingConfiguration() throws ErrorException;
    
    public List<UsersBookingData> getAllBookingsForUser();
    
    /**
     * Add new room to the manager.
     * @param room
     * @throws ErrorException 
     */
    @Administrator
    public void saveRoom(Room room) throws ErrorException;
    
    @Administrator
    public void removeRoom(String id) throws ErrorException;
    
    public Room getRoom(String id) throws ErrorException;
    
    public void updateCart();
    
    public void setVistorData(HashMap<Integer,List<Visitors>> visitors) throws ErrorException;
    
    public String getEmailMessage(String language) throws ErrorException;
    
    public String completeOrder();
    
    /**
     * Change a room for a reference.
     * @param reference
     * @param oldRoom the old room
     * @param newRoomId
     * @throws ErrorException 
     */
    @Administrator
    public void moveRoomOnReference(Integer reference, String oldRoom, String newRoomId) throws ErrorException;
    
    /**
     * Change a room for a reference.
     * @param reference
     * @param oldRoom the old room
     * @param newRoomId
     * @throws ErrorException 
     */
    @Administrator
    public void tempGrantAccess(Integer reference, String roomId) throws ErrorException;
    
    @Administrator
    public List<TempAccess> getAllTempAccesses() throws ErrorException;
    
    /**
     * 
     * @return
     * @throws ErrorException 
     */
    public List<Room> getAllRooms() throws ErrorException;
    
    /**
     * Get all references
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<BookingReference> getAllReservations() throws ErrorException;
    
    /**
     * Get all references
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<UsersBookingData> getAllReservationsArx() throws ErrorException;
    
    
    @Administrator
    public void deleteReference(int reference) throws ErrorException;
    
    public ArrayList getRoomProductIds() throws ErrorException;
    
    @Administrator
    public void setArxConfiguration(ArxSettings settings) throws ErrorException;
    
    public Integer checkAvailableParkingSpots(long startDate, long endDate) throws ErrorException;
    
    @Administrator
    public void setVismaConfiguration(VismaSettings settings) throws ErrorException;
    
    @Editor
    public void markRoomAsReady(String roomId) throws ErrorException;
    
    public BookingReference getReservationByReferenceId(Integer referenceId) throws ErrorException;

    @Administrator
    public void setBookingConfiguration(GlobalBookingSettings settings) throws ErrorException;

    @Administrator
    public boolean isRoomAvailable(String roomId, long startDate, long endDate) throws ErrorException;

    @Administrator
    public void notifyUserAboutRoom(BookingReference reference, RoomInformation roomInfo, Integer code) throws ErrorException;

    @Editor
    public List<UsersBookingData> getAllUsersBookingData();

    @Editor
    public List<UsersBookingData> getAllActiveUserBookings();
    
    @Administrator
    public void updateUserBookingData(UsersBookingData userdata) throws ErrorException;
    
    @Administrator
    public void deleteUserBookingData(String id) throws ErrorException;
    
    @Administrator
    public UsersBookingData getUserBookingData(String id);
    
    public UsersBookingData getCurrentUserBookingData();
    
    public List<ArxLogEntry> getArxLog() throws ErrorException;
    
    public void checkForVismaTransfer() throws ErrorException;
    public void checkForArxTransfer() throws ErrorException;
    public void checkForWelcomeMessagesToSend() throws ErrorException;
    public void checkForOrdersToGenerate() throws ErrorException;
    public void updateAdditionalInformation(AdditionalBookingInformation info) throws ErrorException;
    public void clearBookingReservation();

    public String getUserIdForRoom(String roomNumber) ;
}
