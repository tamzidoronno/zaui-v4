package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.DataCommon;

public class GlobalBookingSettings extends DataCommon {
    public String name = "";
    public String type = "hotel";
    public Integer minRentalDays = 1;
    public Integer referenceCount = 2000;
    public Integer maxRentalDaysAhead = 1000;
    public boolean roomThumbNails = false;
    public boolean showReferenceNumber = false;
    public boolean displayHeardAboutUs = false;
    public Integer parkingSpots = 0;
    public String extraBookingInformation = "";
    public String summaryPage = "";
    public Integer longTermRentalDays = 30;
}
