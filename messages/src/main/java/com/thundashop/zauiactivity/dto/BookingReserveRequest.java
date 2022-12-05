package com.thundashop.zauiactivity.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class BookingReserveRequest {
    private Integer productId;
    private String optionId;
    private String availabilityId;
    private String notes;
    private List<UnitItemReserveRequest> unitItems = null;
}

