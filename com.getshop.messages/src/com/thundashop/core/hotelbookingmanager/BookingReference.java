package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BookingReference extends DataCommon {
    public int bookingReference;
    public Date startDate;
    public Date endDate;
    public String language = "nb_NO";
    public List<Integer> codes = new ArrayList();
    public List<RoomInformation> roomsReserved = new ArrayList();
    public HashMap<String, Boolean> isApprovedForCheckIn = new HashMap();
    public ContactData contact = new ContactData();
    public boolean active = true;
    public Double bookingFee = 0.0;
    public boolean updateArx = true;
    public boolean confirmed = false;
    public String sentWelcomeMessages = "true";
    public Integer parkingSpots = 0;
    public String heardAboutUs = "";
    public boolean payedFor = false;
    Date failed = null;
    
    public boolean isApprovedForCheckin(String roomId) {
        if(isApprovedForCheckIn.containsKey(roomId)) {
            return isApprovedForCheckIn.get(roomId);
        }
        return false;
    }

    boolean isBetween(Date start, Date end) {
        if(start.before(startDate) && end.after(endDate)) {
            return true;
        }
        return false;
    }
    
    public boolean isToday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        int dayofyear = cal.get(Calendar.DAY_OF_YEAR);
        int year = cal.get(Calendar.YEAR);
        
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date());
        int dayofyear2 = cal2.get(Calendar.DAY_OF_YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if((dayofyear == dayofyear2) && (year == year2)) {
            return true;
        }
        return false;
    }
    
    
    public boolean isNow() {
        if(isToday()) {
            return true;
        }
        
        Date now = new Date();
        if(startDate.before(now) && endDate.before(now)) {
            return true;
        }
        return false;
    }

    public boolean isBetweenDates(long startDate, long endDate) {
        if(startDate >= this.startDate.getTime() && startDate < this.endDate.getTime()) {
            return true;
        }
        
        if(endDate > this.startDate.getTime() && endDate <= this.endDate.getTime()) {
            return true;
        }
        if(startDate <= this.startDate.getTime() && endDate >= this.endDate.getTime()) {
            return true;
        }
        
        return false;
    }
    
    public boolean isBetweenCheckinAndCheckout() {
        Date now = new Date();
        if(now.after(startDate) && now.before(endDate)) {
            return true;
        }
        return false;
    }
    
    public List<String> getAllRooms() {
        List<String> allRooms = new ArrayList();
        for(RoomInformation room : roomsReserved) {
            allRooms.add(room.roomId);
        }
        return allRooms;
    }

    public boolean isStarted() {
        Date now = new Date();
        if(now.after(startDate)) {
            return true;
        }
        
        return false;
    }

    public boolean isEnded() {
        Date now = new Date();
        if(now.after(endDate)) {
            return true;
        }
        return false;
    }

    public boolean containsRoom(String roomNumber, HashMap<String, Room> rooms) {
        for (RoomInformation roomInfo : roomsReserved) {
            Room room = rooms.get(roomInfo.roomId);
            if (room != null && room.roomName.equals(roomNumber)) {
                return true;
            }
        }
       
        return false;
    }

}
