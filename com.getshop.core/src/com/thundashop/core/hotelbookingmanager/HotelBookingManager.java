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
    public HashMap<Integer, BookingReference> bookingReferences = new HashMap();
    
    
    @Autowired
    MessageManager msgmgr;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon dbobj : data.data) {
            if(dbobj instanceof RoomType) {
                roomTypes.put(dbobj.id, (RoomType)dbobj);
            }
            if(dbobj instanceof Room) {
                Room room = (Room)dbobj;
                room.bookedDates = new ArrayList();
                rooms.put(dbobj.id, room);
            }
            if(dbobj instanceof BookingReference) {
                BookingReference reference = (BookingReference)dbobj;
                bookingReferences.put(reference.bookingReference, reference);
            }
        }
    }
    
    @Autowired
    public HotelBookingManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }
    
    @Override
    public Integer checkAvailable(long startDate, long endDate, String typeName) throws ErrorException {
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
            if(type.name.trim().equalsIgnoreCase(typeName.trim())) {
                rtype = type;
            }
        }
        
        if(rtype == null) {
            msgmgr.mailFactory.send("post@getshop.com", "post@getshop.com", "Booking failed for " + storeId + " room type is fail type : " + typeName, getStore().webAddress + " : " + getStore().webAddressPrimary + " : ");
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
    public String reserveRoom(String roomType, long startDate, long endDate, int count, ContactData contact) throws ErrorException {
        //First make sure there is enough rooms available.
        RoomType roomtype = getRoomType(roomType);
        Integer availableRooms = checkAvailable(startDate, startDate, roomtype.name);
        if(availableRooms < count) {
            return "-1";
        }

        
        Date start = new Date(startDate*1000);
        Date end = new Date(endDate*1000);
        
        BookingReference reference = new BookingReference();
        reference.bookingReference = genereateReferenceId();
        reference.startDate = start;
        reference.endDate = end;
            
        for(int i = 0; i < count; i++) {
            Room room = getAvailableRoom(roomtype.id, start, end);
            reference.codes.add(room.reserveDates(start, end, reference.bookingReference));
            reference.roomIds.add(room.id);            
            room.storeId = storeId;
            databaseSaver.saveObject(room, credentials);
        }
        reference.storeId = storeId;
        reference.contact = contact;
        
        databaseSaver.saveObject(reference, credentials);
        bookingReferences.put(reference.bookingReference, reference);
        return new Integer(reference.bookingReference).toString();
    }

    @Override
    public void saveRoom(Room room) throws ErrorException {
        if(room.id != null) {
            Room oldRoom = rooms.get(room.id);
            if(oldRoom != null && oldRoom.currentCode != null && room.currentCode != null && !oldRoom.currentCode.equals(room.currentCode)) {
                updateLockOnRoom(room);
            }
        }
        room.storeId = storeId;
        if(!room.isActive) {
            System.out.println("Room is inactive");
        } else {
            System.out.println("Is active");
        }
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

    
    private Room getAvailableRoom(String roomTypeId, Date startDate, Date endDate) {
        for(Room room : rooms.values()) {
            if(!room.roomType.equals(roomTypeId)) {
                continue;
            }
            if(room.isAvilable(startDate, endDate)) {
                return room;
            }
        }
        
        return null;
    }

    private int genereateReferenceId() {
        int count = 0;
        for(int curcount : bookingReferences.keySet()) {
            if(count < curcount) {
                count = curcount;
            }
        }
        count++;
        return count;
    }

    private RoomType getRoomType(String roomTypeName) {
        for(RoomType roomtype : roomTypes.values()) {
            if(roomtype.name.equals(roomTypeName)) {
                return roomtype;
            }
        }
        return null;
    }

    @Override
    public List<BookingReference> getAllReservations() throws ErrorException {
        return new ArrayList(bookingReferences.values());
    }

    @Override
    public void deleteReference(int reference) throws ErrorException {
        BookingReference ref = bookingReferences.get(reference);
        if(ref == null) {
            throw new ErrorException(1025);
        }
        for(String roomId : ref.roomIds) {
            Room room = getRoom(roomId);
            room.removeBookedRoomWithReferenceNumber(reference);
            databaseSaver.saveObject(room, credentials);
        }
        databaseSaver.deleteObject(ref, credentials);
        bookingReferences.remove(reference);
    }

    @Override
    public Room getRoom(String id) throws ErrorException {
        return rooms.get(id);
    }
}
