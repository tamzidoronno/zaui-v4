package com.thundashop.zauiactivity.dto;

import lombok.Data;

import java.util.List;

@Data
public class ZauiConnectedSupplier extends OctoSupplier {
    public List<TaxRateMap> taxRateMapping;

    public String getSupplierAccountNumberByRate(Double taxRate) {
        TaxRateMap taxRateMapping =  this.getTaxRateMapping().stream().filter(rate -> taxRate.equals(rate.getTaxRate())).findFirst().orElse(null);
        if(taxRateMapping == null)
            return null;
        return taxRateMapping.getAccountNo();
    }
}

@Data
class TaxRateMap {
    public double TaxRate;
    public String AccountNo;
}