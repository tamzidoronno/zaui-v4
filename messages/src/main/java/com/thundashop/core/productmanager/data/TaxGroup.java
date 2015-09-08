package com.thundashop.core.productmanager.data;

import com.thundashop.core.common.DataCommon;

public class TaxGroup extends DataCommon {
    public int groupNumber = 0;
    public Double taxRate = 0.0;

    public Double getTaxRate() {
        return taxRate/100;
    }
}
