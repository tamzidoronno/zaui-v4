package com.thundashop.core.pmsbookingprocess;

import java.io.Serializable;
import java.util.Date;

public class StartBooking implements Serializable {
    public Date start;
    public Date end;
    public Integer rooms;
    public Integer adults;
    public Integer children;
    public String discountCode;
    String bookingId = "";
    String roomId = "";
    String language = "";

    int getGuests() {
        return adults+children;
    }
}
