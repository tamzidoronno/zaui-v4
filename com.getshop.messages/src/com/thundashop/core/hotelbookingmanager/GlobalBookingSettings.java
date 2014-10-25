package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.DataCommon;

public class GlobalBookingSettings extends DataCommon {
    public String name = "";
    public String type = "hotel";
    public Integer minRentalDays = 1;
    public Integer maxRentalDaysAhead = 1000;
    public boolean roomThumbNails = false;
    public Integer parkingSpots = 0;
}
