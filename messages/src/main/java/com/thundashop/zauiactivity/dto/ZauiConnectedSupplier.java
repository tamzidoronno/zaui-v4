package com.thundashop.zauiactivity.dto;

import java.util.List;

public class ZauiConnectedSupplier extends OctoSupplier {
    public List<TaxRateMap> taxRateMapping;
}

class TaxRateMap {
    public double TaxRate;
    public String AccountNo;
}