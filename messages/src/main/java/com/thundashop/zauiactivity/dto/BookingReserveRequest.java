package com.thundashop.zauiactivity.dto;

import java.util.List;

import lombok.Data;

@Data
public class BookingReserveRequest {
    private Integer productId;
    private String optionId;
    private String availabilityId;
    private String notes;
    private List<UnitItemReserveRequest> unitItems = null;
}

@Data
class UnitItemReserveRequest {
    private String unitId;
}