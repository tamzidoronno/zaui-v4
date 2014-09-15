package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
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
    public Integer checkAvailable(long startDate, long endDate, String type) throws ErrorException;
    
    /**
     * 
     * @param roomType
     * @param startDate The first day unix timestamp.
     * @param endDate The last day unix timestamp.
     * @param count The number of rooms to book.
     * @throws ErrorException 
     */
    public String reserveRoom(String roomType, long startDate, long endDate, int count, ContactData contact, boolean markAsInctive, String language) throws ErrorException;
    
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
    
    
    public String getEmailMessage(String language) throws ErrorException;
    
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
     * 
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<Room> getAllRooms() throws ErrorException;
    
    /**
     * Get all references
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<BookingReference> getAllReservations() throws ErrorException;
    
    
    @Administrator
    public void deleteReference(int reference) throws ErrorException;
    
    
    public List<RoomType> getRoomTypes() throws ErrorException;

    @Administrator
    public void saveRoomType(RoomType type) throws ErrorException;
    
    @Administrator
    public void removeRoomType(String id) throws ErrorException;
    
    @Administrator
    public void updateReservation(BookingReference reference) throws ErrorException;
    
    @Administrator
    public void setArxConfiguration(ArxSettings settings) throws ErrorException;
    
    @Administrator
    public void setVismaConfiguration(VismaSettings settings) throws ErrorException;
    
    @Editor
    public void markRoomAsReady(String roomId) throws ErrorException;
    
    public BookingReference getReservationByReferenceId(Integer referenceId) throws ErrorException;
    
    public List<ArxLogEntry> getArxLog() throws ErrorException;
    
    public void checkForVismaTransfer() throws ErrorException;
    public void checkForArxTransfer() throws ErrorException;
    public void checkForWelcomeMessagesToSend() throws ErrorException;
    
}
