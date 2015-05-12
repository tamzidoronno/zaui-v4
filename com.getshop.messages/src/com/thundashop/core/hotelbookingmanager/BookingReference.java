package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.DataCommon;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BookingReference implements Serializable {
    public int bookingReference;
    public Date startDate;
    public Date endDate;
    public String language = "nb_NO";
    public List<RoomInformation> roomsReserved = new ArrayList();
    public boolean updateArx = true;
    public boolean confirmed = false;
    public boolean sentCheckInMessages = false;
    public Integer parkingSpots = 0;
    public String heardAboutUs = "";
    Date failed = null;
    
    boolean isBetween(Date start, Date end) {
        if(start.before(startDate) && end.after(endDate)) {
            return true;
        }
        return false;
    }
    
    public Integer getNumberOfNights() {
         return (int)( (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24))+1;
    }
    
    public Integer getNumberOfNights(Integer year, Integer month, Integer week, Integer day) {
         Calendar cal = Calendar.getInstance();
         cal.setTime(startDate);
         
         Integer numberOfNights = 0;
         while(true) {
             if(cal.getTime().after(endDate)) {
                break;
             }
             
             
             if(cal.get(Calendar.YEAR) != year) {
                 cal.add(Calendar.DAY_OF_YEAR, 1);
                continue;
             }
             
             if(week != null && cal.get(Calendar.WEEK_OF_YEAR) != week) {
                 cal.add(Calendar.DAY_OF_YEAR, 1);
                 continue;
             }
             
             if(month != null && cal.get(Calendar.MONTH) != (month-1)) {
                cal.add(Calendar.DAY_OF_YEAR, 1);
                continue;
             }
             
             if(day != null && cal.get(Calendar.DAY_OF_YEAR) != day) {
                cal.add(Calendar.DAY_OF_YEAR, 1);
                continue;
             }
             
             cal.add(Calendar.DAY_OF_YEAR, 1);
             numberOfNights++;
         }
         return numberOfNights;
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
        startDate = convertToStartdate(startDate);
        endDate = convertToEndDate(endDate);
        
        if((startDate >= this.startDate.getTime()) && (startDate < this.endDate.getTime())) {
            return true;
        }
        
        if((endDate > this.startDate.getTime()) && (endDate <= this.endDate.getTime())) {
            return true;
        }
        if(startDate <= this.startDate.getTime() && endDate >= this.endDate.getTime()) {
            return true;
        }
        
        return false;
    }
    
    public boolean isActive() {
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

    private long convertToStartdate(long startDate) {
        Calendar calstart = Calendar.getInstance();
        calstart.setTimeInMillis(startDate);
        calstart.set(Calendar.HOUR_OF_DAY, 16);
        calstart.set(Calendar.MINUTE, 00);
        startDate = calstart.getTimeInMillis();
        return startDate;
    }

    private long convertToEndDate(long endDate) {
        Calendar calend = Calendar.getInstance();
        calend.setTimeInMillis(endDate);
        calend.set(Calendar.HOUR_OF_DAY, 11);
        calend.set(Calendar.MINUTE, 00);
        endDate = calend.getTimeInMillis();
        return endDate;
    }

    boolean isInPeriode(Integer year, Integer month, Integer week) {
        Calendar startcal = Calendar.getInstance();
        Calendar endcal = Calendar.getInstance();
        
        startcal.setTime(startDate);
        endcal.setTime(endDate);
        
        if(startcal.get(Calendar.YEAR) < year && year > endcal.get(Calendar.YEAR)) {
            return false;
        }
        
        if(month != null) {
            if(startcal.get(Calendar.MONTH) < month && month > endcal.get(Calendar.MONTH)) {
                return false;
            }
        }
        
        if(week != null) {
            if(startcal.get(Calendar.WEEK_OF_YEAR) < week && week > endcal.get(Calendar.WEEK_OF_YEAR)) {
                return false;
            }
        }
        
        return true;
    }

}
