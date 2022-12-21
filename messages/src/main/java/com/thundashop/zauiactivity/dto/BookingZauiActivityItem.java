package com.thundashop.zauiactivity.dto;

import com.thundashop.core.pmsmanager.PmsBookingAddonItem;
import java.util.List;

public class BookingZauiActivityItem extends PmsBookingAddonItem {
    public String id = java.util.UUID.randomUUID().toString();
    public String zauiActivityId;
    public Integer octoProductId;
    public String optionTitle;
    public String optionId;
    public String availabilityId;
    public List<Unit> units;
    public double unpaidAmount;
    public String notes;
    public String localDateTimeStart;
    public String localDateTimeEnd;
    public Integer supplierId;
    public String supplierName;
    public OctoBooking octoBooking;
}
