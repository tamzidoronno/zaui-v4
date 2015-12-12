
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.Date;
import java.util.HashMap;

public class PmsPricing extends DataCommon {
    public Integer defaultPriceType = 1;
    public HashMap<String, Double> defaultTypePrice = new HashMap();
    public HashMap<String, HashMap<String, Double>> specifiedPrices = new HashMap(); 
}
