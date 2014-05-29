package com.thundashop.core.hotelbookingmanager;
import com.thundashop.core.common.DataCommon;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Room extends DataCommon {
    List<BookedDate> bookedDates;
    public String roomType;
    public String currentCode;
    public String roomName;
    
    //Identify what lock is connected to this room.
    public String lockId;

    boolean isAvilable(Date start, Date end) {
        
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
                if(bcal.get(Calendar.DAY_OF_YEAR) == i) {
                    return false;
                }
            }
        }
        
        return true;
    }

    void reserveDates(long startDate, long endDate, int bookingReference) {
        Random randomGenerator = new Random();
        int code = randomGenerator.nextInt(10000);
        
        for(long day = startDate; day <= endDate; day += 86400) {
            BookedDate booked = new BookedDate();
            booked.bookingReference = bookingReference;
            booked.code = code;
            booked.date = new Date(day);
            System.out.println("Booking room: " + roomName + " : " + lockId + " : " + booked);
        }
    }

}