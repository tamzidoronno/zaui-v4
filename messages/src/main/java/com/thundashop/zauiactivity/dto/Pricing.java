package com.thundashop.zauiactivity.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Pricing {
    private String currency;
    private Integer currencyPrecision;
    private Long subtotal;
    private Long tax;
    private Long total;
    private List<TaxData> includedTaxes = new ArrayList<>();
}
