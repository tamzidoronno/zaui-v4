/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author ktonder
 */
public class HotelDomainController {
    private List<RoomType> roomTypes = new ArrayList();
    private List<Room> rooms = new ArrayList();
    private final Domain domain;
    private HotelBookingManager hotelBookingManager;


    HotelDomainController(Domain domain, HotelBookingManager hotelBookingManager) {
        this.domain = domain;
        this.hotelBookingManager = hotelBookingManager;
    }

    public void dataFromDatabase(List<DataCommon> data) {
        for (DataCommon dataCommon : data) {
            if (!isForMe(dataCommon))
                continue;
            
            if (dataCommon instanceof RoomType)
                roomTypes.add((RoomType)dataCommon);
            
            if (dataCommon instanceof Room)
                rooms.add((Room)dataCommon);
        }
    }

    public RoomType createRoomType(String name, double price, int size) {
        RoomType roomType = new RoomType();
        roomType.name = name;
        roomType.price = price;
        roomType.size = size;
        saveObject(roomType);
        roomTypes.add(roomType);
        return roomType; 
    }

    private void saveObject(DomainControlledObject domainControlledObject) {
        domainControlledObject.domainId = domain.id;
        hotelBookingManager.saveObject(domainControlledObject);
    }

    private boolean isForMe(DataCommon dataCommon) {
        if (dataCommon instanceof DomainControlledObject) {
            DomainControlledObject obj = (DomainControlledObject)dataCommon;
            return obj.domainId.equals(domain.id);
        }
        
        return false;
    }

    public Domain getDomain() {
        return domain;
    }

    public List<RoomType> getRoomTypes() {
        return roomTypes;
    }

    public void deleteRoomType(String roomTypeId) {
        RoomType toRemove = roomTypes.stream().filter(o -> o.id.equals(roomTypeId)).findFirst().orElse(null);
        if (toRemove != null) {
            roomTypes.remove(toRemove);
            hotelBookingManager.deleteObject(toRemove);
        }
    }

    public RoomType getRoomType(String roomTypeId) {
        return roomTypes.stream().filter(o -> o.id.equals(roomTypeId)).findFirst().orElse(null);
    }

    public void saveRoom(Room room) {
        rooms.add(room);
        saveObject(room);
    }

    public List<Room> getRooms() {
        rooms.stream().forEach(o -> finalizeRoom(o));
        return rooms;
    }

    public Room getRoom(String roomId) {
        return rooms.stream().filter(o -> o.id.equals(roomId)).findFirst().orElse(null);
    }

    private void finalizeRoom(Room room) {
        room.roomType = getRoomType(room.roomTypeId);
    }

    public void deleteRoom(String roomId) {
        Room room = rooms.stream().filter(o -> o.id.equals(roomId)).findFirst().orElse(null);
        if (room != null) {
            rooms.remove(room);
            hotelBookingManager.deleteObject(room);
        }
    }
}
