package com.thundashop.core.activity.dto;

import java.util.List;

public class Availability {
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
    private String currency;
    private Integer currencyPrecision;
    private Integer subtotal;
    private Integer tax;
    private Integer total;
}
class UnitPricing {
    private String unitId;
    private String currency;
    private Integer currencyPrecision;
    private Integer total;
}