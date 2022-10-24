package com.thundashop.core.activity.dto;

import java.util.List;

public class BookingReserveRequest {
    private Integer productId;
    private String optionId;
    private String availabilityId;
    private String notes;
    private List<UnitItem> unitItems = null;
}