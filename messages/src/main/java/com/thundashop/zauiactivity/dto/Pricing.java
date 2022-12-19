package com.thundashop.zauiactivity.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Pricing {
    private String currency;
    private Integer currencyPrecision;
    private double subtotal;
    private double tax;
    private double total;
    private List<TaxData> includedTaxes = new ArrayList<>();
}
