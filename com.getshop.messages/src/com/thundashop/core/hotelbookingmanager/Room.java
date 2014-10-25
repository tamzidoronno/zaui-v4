package com.thundashop.core.hotelbookingmanager;
import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Room extends DataCommon {
    List<BookedDate> bookedDates = new ArrayList();
    public String roomType;
    public String currentCode;
    public String roomName;
    public Boolean isActive = true;
    public Boolean isClean = false;
    public Date lastCleaning;
    public BookingReference lastReservation = null;
    public List<Date> cleaningDates = new ArrayList();
    
    //Identify what lock is connected to this room.
    public String lockId;

    boolean isAvilable(Date start, Date end) {
        
        if(!isActive) {
            return false;
        }
        
        if(bookedDates == null) {
            return true;
        }
        
        Calendar calStart = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        
        calStart.setTime(start);
        calEnd.setTime(end);
        
        int startdayofyear = calStart.get(Calendar.DAY_OF_YEAR);
        int stopdayofyear = calEnd.get(Calendar.DAY_OF_YEAR);
        
        for(int i = startdayofyear; i <= stopdayofyear; i++) {
            for(BookedDate bdate : bookedDates) {
                Calendar bcal = Calendar.getInstance();
                bcal.setTime(bdate.date);
                if(bcal.get(Calendar.DAY_OF_YEAR) == i && bcal.get(Calendar.YEAR) == calStart.get(Calendar.YEAR)) {
                    return false;
                }
            }
        }
        
        return true;
    }

    public boolean isCleanedToday() {
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        int dayofyear = instance.get(Calendar.DAY_OF_YEAR);
        int year = instance.get(Calendar.YEAR);
        
        for(Date cleanedDate : cleaningDates) {
            instance.setTime(cleanedDate);
            if(instance.get(Calendar.DAY_OF_YEAR) == dayofyear && instance.get(Calendar.YEAR) == year) {
                return true;
            }
        }
        return false;
    }
    
    int reserveDates(Date start, Date end, int bookingReference) {
        Random randomGenerator = new Random();
        int code = 0;
        do {
            code = randomGenerator.nextInt(9999);
        }while(code < 1000);
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        
        while(true) {
            BookedDate booked = new BookedDate();
            booked.bookingReference = bookingReference;
            booked.code = code;
            booked.date = cal.getTime();
            System.out.println("Booking room: " + roomName + " : " + lockId + " : " + booked);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            bookedDates.add(booked);
            if(cal.getTime().after(end)) {
                break;
            }
        }
        return code;
    }

    void removeBookedRoomWithReferenceNumber(int reference) {
        List<BookedDate> toRemove = new ArrayList();
        for(BookedDate booked : bookedDates) {
            if(booked.bookingReference == reference) {
                toRemove.add(booked);
            }
        }
        
        for(BookedDate tmpremove : toRemove) {
            bookedDates.remove(tmpremove);
        }
    }

    List<BookedDate> getBookedDatesByReference(Integer reference) {
        List<BookedDate> result = new ArrayList();
        for(BookedDate bd : bookedDates) {
            if(bd.bookingReference == reference) {
                result.add(bd);
            }
        }
        return result;
    }

}