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
    public List<String> roomIds = new ArrayList();
    public HashMap<String, Boolean> isApprovedForCheckIn = new HashMap();
    public ContactData contact = new ContactData();
    public Double bookingFee = 0.0;
    public boolean updateArx = true;
    public boolean sentWelcomeMessages = true;
    public Integer parkingSpots = 0;
    public String heardAboutUs = "";
    Date failed = null;

    public boolean isApprovedForCheckin(String roomId) {
        if (isApprovedForCheckIn.containsKey(roomId)) {
            return isApprovedForCheckIn.get(roomId);
        }
        return false;
    }

    
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

    boolean allRoomsClean(HashMap<String, Room> rooms) {
        boolean clean = true;
        for (String roomId : roomIds) {
            Room room = rooms.get(roomId);
            if (!room.isClean && !room.isCleanedToday()) {
                clean = false;
                break;
            }
        }
        return clean;
    }

    boolean isBetween(Date start, Date end) {
        if(start.before(startDate) && end.after(endDate)) {
            return true;
        }
        return false;
    }
}
