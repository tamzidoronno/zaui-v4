package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import java.util.Date;
import java.util.List;

@GetShopApi
public interface IHotelBookingManager {
    /**
     * Check if a room is available in the given time periode.
     * @param startDate The first day
     * @param endDate The last day
     * @param type The type of room to search for-
     * @return Number of avilable rooms.
     * @throws ErrorException 
     */
    public Integer checkAvailable(Date startDate, Date endDate, String type) throws ErrorException;
    
 
    /**
     * 
     * @param roomType
     * @param startDate
     * @param endDate
     * @throws ErrorException 
     */
    public void reserveRoom(String roomType, Date startDate, Date endDate) throws ErrorException;
    
    /**
     * Add new room to the manager.
     * @param room
     * @throws ErrorException 
     */
    @Administrator
    public void saveRoom(Room room) throws ErrorException;
    
    @Administrator
    public void removeRoom(String id) throws ErrorException;
    
    /**
     * Set a new code to a given room.
     * @param code The code to set
     * @param roomId The id of the room to update.
     * @throws ErrorException 
     */
    @Administrator
    public void setCode(String code, String roomId) throws ErrorException;
    
    /**
     * 
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<Room> getAllRooms() throws ErrorException;
    
    
    public List<RoomType> getRoomTypes() throws ErrorException;
    @Administrator
    public void saveRoomType(RoomType type) throws ErrorException;
    @Administrator
    public void removeRoomType(String id) throws ErrorException;
}
