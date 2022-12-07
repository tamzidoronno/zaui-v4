package com.thundashop.zauiactivity.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class OctoProductAvailability {
    private String id;
    private String localDateTimeStart;
    private String localDateTimeEnd;
    private Boolean allDay;
    private String status;
    private Integer vacancies;
    private Integer capacity;
    private List<UnitPricing> unitPricing = new ArrayList<>();
    private Pricing pricing;
}

@Data
class Pricing {
    String currency;
    Integer currencyPrecision;
    Integer subtotal;
    Integer tax;
    Integer total;
}

@Data
class UnitPricing {
    private String unitId;
    private String currency;
    private Integer currencyPrecision;
    private Integer total;
}