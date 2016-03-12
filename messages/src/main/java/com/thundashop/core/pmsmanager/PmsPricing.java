
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.HashMap;

public class PmsPricing extends DataCommon {
    public Integer defaultPriceType = 1;
    public HashMap<String, HashMap<String, Double>> dailyPrices = new HashMap(); 
    public HashMap<String, ArrayList<ProgressivePriceAttribute>> progressivePrices = new HashMap(); 
    public boolean pricesExTaxes = false;
    public boolean privatePeopleDoNotPayTaxes = false;
}
