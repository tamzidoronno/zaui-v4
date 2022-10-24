package com.thundashop.core.activity.dto;

import java.util.List;

public class AvailabilityRequest {
    private Integer productId;
    private String optionId;
    private String localDateStart;
    private String localDateEnd;
    private String currency;
    private List<Unit> units = null;
}

