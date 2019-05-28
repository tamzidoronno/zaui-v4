
package com.thundashop.core.pos;

import java.util.Date;
import java.util.HashMap;

public class SalesPosReportEntry {
    public Date day;
    HashMap<String, Integer> productCounter = new HashMap();
    HashMap<String, Double> productValue = new HashMap();
    HashMap<String, Double> productTaxes = new HashMap();
}
