package com.thundashop.core.hotelbookingmanager;

import com.ibm.icu.util.Calendar;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class HotelBookingManager extends ManagerBase implements IHotelBookingManager {

    public HashMap<String, Room> rooms = new HashMap();
    public HashMap<String, RoomType> roomTypes = new HashMap();
    
    
    @Autowired
    MessageManager msgmgr;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon dbobj : data.data) {
            if(dbobj instanceof RoomType) {
                roomTypes.put(dbobj.id, (RoomType)dbobj);
            }
            if(dbobj instanceof Room) {
                rooms.put(dbobj.id, (Room)dbobj);
            }
        }
    }
    
    @Autowired
    public HotelBookingManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }
    
    @Override
    public Integer checkAvailable(long startDate, long endDate, String typeId) throws ErrorException {
        Date start = new Date(startDate*1000);
        Date end = new Date(endDate*1000);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(System.currentTimeMillis()));
        cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)-1);
        
        if(start.before(cal.getTime())) {
            return -1;
        }

        if(end.before(start)) {
            return -2;
        }
        
        RoomType rtype = null;
        for(RoomType type : roomTypes.values()) {
            if(type.name.trim().equalsIgnoreCase(typeId.trim())) {
                rtype = type;
            }
        }
        
        if(rtype == null) {
            msgmgr.mailFactory.send("post@getshop.com", "post@getshop.com", "Booking failed for " + storeId + " room type is fail type : " + typeId, getStore().webAddress + " : " + getStore().webAddressPrimary + " : ");
            throw new ErrorException(1023);
        }
        
        int count = 0;
        for(Room room : rooms.values()) {
            if(room.roomType.equals(rtype.id) && room.isAvilable(start, end)) {
                count++;
            }
        }
        
        
        return count;
    }

    @Override
    public void reserveRoom(String roomType, Date startDate, Date endDate) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveRoom(Room room) throws ErrorException {
        if(room.id != null) {
            Room oldRoom = rooms.get(room.id);
            if(!oldRoom.currentCode.equals(room.currentCode)) {
                updateLockOnRoom(room);
            }
        }
        room.storeId = storeId;
        databaseSaver.saveObject(room, credentials);
        rooms.put(room.id, room);
    }

    @Override
    public void removeRoom(String id) throws ErrorException {
        Room room = rooms.get(id);
        databaseSaver.deleteObject(room, credentials);
        rooms.remove(id);
    }

    @Override
    public void setCode(String code, String roomId) throws ErrorException {
        Room room = rooms.get(roomId);
        room.currentCode = code;
        saveRoom(room);
        updateLockOnRoom(room);
    }
    
    public void updateLockOnRoom(Room room) {
        System.out.println("Updating room lock.");
    }

    @Override
    public List<Room> getAllRooms() throws ErrorException {
        return new ArrayList(rooms.values());
    }

    @Override
    public List<RoomType> getRoomTypes() throws ErrorException {
        return new ArrayList(roomTypes.values());
    }

    @Override
    public void saveRoomType(RoomType type) throws ErrorException {
        type.storeId = storeId;
        databaseSaver.saveObject(type, credentials);
        roomTypes.put(type.id, type);
    }

    @Override
    public void removeRoomType(String id) throws ErrorException {
        RoomType type = roomTypes.get(id);
        //Check if rooms already exists on this type, if so, not allow it.
        List<Room> allRooms = getAllRooms();
        for(Room room : allRooms) {
            if(room.roomType.equals(type.id)) {
                throw new ErrorException(1020);
            }
        }
        databaseSaver.deleteObject(type, credentials);
        roomTypes.remove(id);
    }
}
