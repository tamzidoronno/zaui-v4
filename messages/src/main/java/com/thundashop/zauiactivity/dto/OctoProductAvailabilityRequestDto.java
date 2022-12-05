package com.thundashop.zauiactivity.dto;

import java.util.List;

import lombok.Data;

@Data
public class OctoProductAvailabilityRequestDto {
    private Integer productId;
    private String optionId;
    private String localDateStart;
    private String localDateEnd;
    private String currency;
    private List<AvailabilityUnit> units = null;
}
class AvailabilityUnit {
    String id;
    Integer quantity;
}

