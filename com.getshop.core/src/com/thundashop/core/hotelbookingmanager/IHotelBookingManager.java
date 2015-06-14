package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.ArrayList;
import java.util.List;

@GetShopApi
public interface IHotelBookingManager {
    
    @Administrator
    public void createDomain(String name, String description);
    
    @Administrator
    public RoomType createRoomType(String domainId, String name, double price, int size);   
    
    @Administrator
    public void deleteRoomType(String domainId, String roomTypeId);
    
    
    @Administrator
    public void saveRoom(Room room);
    
    
    @Administrator
    public void deleteRoom(String domainId, String roomId);
    
    @Administrator
    public void deleteDomain(String domainId);
    
    public List<Room> getRooms(String domainId);
    
    public Room getRoom(String domainId, String roomId);
    
    public RoomType getRoomType(String domainId, String roomTypeId);
    
    public List<RoomType> getRoomTypes(String domainId);
    
    public Domain getDomain(String domainId);
    
    /**
     * 
     * @return 
     */
    public ArrayList<Domain> getDomains();
}
