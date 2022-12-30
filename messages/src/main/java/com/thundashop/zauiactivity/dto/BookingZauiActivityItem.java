package com.thundashop.zauiactivity.dto;

import java.util.List;

import com.thundashop.core.pmsmanager.PmsBookingAddonItem;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

@Getter 
@Setter
public class BookingZauiActivityItem extends PmsBookingAddonItem {
    @Setter(AccessLevel.NONE)
    private String id = java.util.UUID.randomUUID().toString();

    private String zauiActivityId;
    private Integer octoProductId;
    private String optionTitle;
    private String optionId;
    private String availabilityId;
    private List<Unit> units;
    private double unpaidAmount;
    private String notes;
    private String localDateTimeStart;
    private String localDateTimeEnd;
    private Integer supplierId;
    private String supplierName;
    private OctoBooking octoBooking;
}
