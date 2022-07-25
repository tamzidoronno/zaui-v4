package com.thundashop.core.pmsbookingprocess;

import com.thundashop.core.pmsmanager.BrowserVersion;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import java.io.Serializable;
import java.util.Date;

public class StartBooking implements Serializable {
    public Date start;
    public Date end;
    public Integer rooms;
    public Integer adults;
    public Integer children;
    public String discountCode;
    public BrowserVersion browser;
    public String bookingId = "";
    public String roomId = "";
    public String language = "";
    public String browserLanguage = "";

    public int getGuests() {
        return adults+children;
    }
    
    public int getNumberOfDays() {
        return PmsBookingRooms.getNumberOfDays(start, end);
    }
}
