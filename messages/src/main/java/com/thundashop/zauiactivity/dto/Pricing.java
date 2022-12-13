package com.thundashop.zauiactivity.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Pricing {
    String currency;
    Integer currencyPrecision;
    Integer subtotal;
    Integer tax;
    Integer total;
    public List<TaxData> includedTaxes = new ArrayList<>();
}
