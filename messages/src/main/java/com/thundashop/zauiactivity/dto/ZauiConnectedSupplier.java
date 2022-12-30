package com.thundashop.zauiactivity.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class ZauiConnectedSupplier extends OctoSupplier {
    private List<TaxRateMap> taxRateMapping;

    public String getSupplierAccountNumberByRate(Double taxRate) {
        TaxRateMap taxRateMapping =  this.getTaxRateMapping().stream().filter(rate -> taxRate.equals(rate.getTaxRate())).findFirst().orElse(null);
        if(taxRateMapping == null)
            return null;
        return taxRateMapping.getAccountNo();
    }
}

@Getter 
@Setter
class TaxRateMap {
    private double TaxRate;
    private String AccountNo;
}