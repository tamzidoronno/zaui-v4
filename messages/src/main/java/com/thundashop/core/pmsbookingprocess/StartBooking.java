package com.thundashop.core.pmsbookingprocess;

import com.thundashop.core.pmsmanager.BrowserVersion;
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
    String bookingId = "";
    String roomId = "";
    String language = "";
    String browserLanguage = "";

    int getGuests() {
        return adults+children;
    }
}
