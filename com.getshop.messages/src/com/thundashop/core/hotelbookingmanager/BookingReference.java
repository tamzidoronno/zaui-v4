package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BookingReference extends DataCommon {

    boolean hasRoom(String id) {
        for(ReservedRoom rroom :rooms) {
            if(rroom.roomId.equals(id)) {
                return true;
            }
        }
        
        return false;
    }

    public Date getInvoicedTo() {
        if(invoicedTo == null && startDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            cal.add(Calendar.MONTH, 3);
            if(!cal.after(new Date())) {
                cal.set(Calendar.MONTH, 8);
            }
            if(cal.get(Calendar.DAY_OF_MONTH) < 15) {
                cal.add(Calendar.MONTH, 1);
            }
            cal.set(Calendar.YEAR, 2015);
            invoicedTo = cal.getTime();
        }
        
        
        
        return invoicedTo;
    }

    void incrementInvoicedTo() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(invoicedTo);
        cal.add(Calendar.MONTH, 1);
        invoicedTo = cal.getTime();
    }


    static class uploadArxStatus {
        public static Integer NOTHING_UPLOADED = 0;
        public static Integer OUTDOORSUPLOADED = 1;
        public static Integer ALLROOMSUPDATED = 2;
    }
    
    public int bookingReference;
    public Date startDate;
    public Date endDate;
    public String language = "nb_NO";
    public List<Integer> codes = new ArrayList();
    private List<String> roomIds = new ArrayList();
    public List<ReservedRoom> rooms = new ArrayList();
    public String roomtype = null;
    
    //0 = No rooms has been up
    public HashMap<String, Integer> uploadedRoomToArx = new HashMap();
    public ContactData contact = new ContactData();
    public Double bookingFee = 0.0;
    public boolean updateArx = true;
    public boolean active = true;
    public boolean confirmed = false;
    public String sentWelcomeMessages = "true";
    public Integer parkingSpots = 0;
    public String heardAboutUs = "";
    Date failed = null;
    public Date invoicedTo = null;
    public String userId;
    
    boolean isToday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        int dayofyear = cal.get(Calendar.DAY_OF_YEAR);
        int year = cal.get(Calendar.YEAR);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date());
        int dayofyear2 = cal2.get(Calendar.DAY_OF_YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if ((dayofyear == dayofyear2) && (year == year2)) {
            return true;
        }
        return false;
    }
    
    public boolean needNewOrder() {
        if(!active && confirmed) {
            return false;
        }
        if(!confirmed) {
            return false;
        }
        
        Date curInvoiced = getInvoicedTo();
        
        Calendar futureDate = Calendar.getInstance();
        futureDate.setTime(new Date());
        futureDate.add(Calendar.DAY_OF_YEAR, 15);

        return curInvoiced.before(futureDate.getTime());
    }

    boolean isNow() {
        if (isToday()) {
            return true;
        }

        Date now = new Date();
        if (startDate.before(now) && endDate.before(now)) {
            return true;
        }
        return false;
    }

    void setCartItemIds(List<String> ids) {
        for(int i = 0; i < ids.size(); i++) {
            rooms.get(i).cartItemId = ids.get(i);
        }
    }
    
    @Deprecated
    public List<String> getRoomIdsForConversion() {
        return roomIds;
    }
    
    boolean allRoomsClean(HashMap<String, Room> rooms) {
        boolean clean = true;
        for (String roomId : getRoomIds()) {
            Room room = rooms.get(roomId);
            if (!room.isClean && !room.isCleanedToday()) {
                clean = false;
                break;
            }
        }
        return clean;
    }

    public void addRoom(String id) {
        ReservedRoom room = new ReservedRoom();
        room.roomId = id;
        rooms.add(room);
    }

    List<String> getRoomIds() {
        List<String> ids = new ArrayList();
        for(ReservedRoom room : rooms) {
            ids.add(room.roomId);
        }
        return ids;
    }
    
    void moveRoom(String oldRoom, String newRoomId) {
        for(ReservedRoom room : rooms) {
            if(room.roomId.equals(oldRoom)) {
                room.roomId = newRoomId;
            }
        }
    }

    
    public String getRoomIdOnCartItem(String cartItemId) {
        for(ReservedRoom room : rooms) {
            if(room.cartItemId != null && room.cartItemId.equals(cartItemId)) {
                return room.roomId;
            }
        }
        
        if(rooms.size() == 1 && rooms.get(0).cartItemId == null) {
            rooms.get(0).cartItemId = cartItemId;
            return rooms.get(0).roomId;
        }
        
        if (rooms.size() == 1) {
            return rooms.get(0).roomId;
        }
        
        return null;
    }

    void setCartItemToRoom(String roomId, String cartItemId) {
        for(ReservedRoom room : rooms) {
            if(room.roomId != null && room.roomId.equals(roomId)) {
                room.cartItemId = cartItemId;
            }
        }
    }

    int statusOnRoom(Room room) {
        if(uploadedRoomToArx.get(room.id) == null) {
            return BookingReference.uploadArxStatus.NOTHING_UPLOADED;
        }
        return uploadedRoomToArx.get(room.id);
    }
    
    boolean isBetween(Date start, Date end) {
        if(active && start.after(startDate)) {
            return true;
        }
        
        if(!active) {
            if(start.before(startDate) && end.after(endDate)) {
                return true;
            }
        }
        return false;
    }

    boolean isStopped() {
        if(confirmed && !active) {
            return true;
        }
        return false;
    }
    
}
