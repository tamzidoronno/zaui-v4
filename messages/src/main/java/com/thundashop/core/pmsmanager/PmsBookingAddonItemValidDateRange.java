package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class PmsBookingAddonItemValidDateRange implements Serializable {
    
    public static class ValidDateType {
        public static Integer ONSTAY = 1; //Valid for the time periode when guest stay at hotel.
        public static Integer ONBOOKING = 2; //Valid for the time periode when guest book.
    }
    
    public Integer validType = PmsBookingAddonItemValidDateRange.ValidDateType.ONSTAY;
    public Date start;
    public Date end;
    public String id = UUID.randomUUID().toString();
}
