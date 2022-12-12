package com.thundashop.zauiactivity.dto;

import lombok.Data;

@Data
public class Pricing {
    String currency;
    Integer currencyPrecision;
    Integer subtotal;
    Integer tax;
    Integer total;
}
