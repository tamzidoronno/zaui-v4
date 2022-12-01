package com.thundashop.zauiactivity.dto;

import java.util.List;

public class OctoProductAvailability {
    private String id;
    private String localDateTimeStart;
    private String localDateTimeEnd;
    private Boolean allDay;
    private String status;
    private Integer vacancies;
    private Integer capacity;
    private List<UnitPricing> unitPricing = null;
    private Pricing pricing;
}

class Pricing {
    String currency;
    Integer currencyPrecision;
    Integer subtotal;
    Integer tax;
    Integer total;
}

class UnitPricing {
    private String unitId;
    private String currency;
    private Integer currencyPrecision;
    private Integer total;
}