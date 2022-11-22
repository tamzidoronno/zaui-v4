package com.thundashop.zauiactivity.dto;

import java.util.List;

public class OctoProductAvailabilityRequestDto {
    private Integer productId;
    private String optionId;
    private String localDateStart;
    private String localDateEnd;
    private String currency;
    private List<Unit> units = null;
}

