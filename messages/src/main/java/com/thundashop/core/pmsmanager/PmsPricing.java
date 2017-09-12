
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.HashMap;

public class PmsPricing extends DataCommon {
    public static class PmsPricingCoverageType {
        public static Integer PERCENTAGE = 0;
        public static Integer FIXEDPRICE = 1;
    }
    
    public String code = "";
    public Integer defaultPriceType = 1;
    public HashMap<String, HashMap<String, Double>> dailyPrices = new HashMap(); 
    public HashMap<String, ArrayList<ProgressivePriceAttribute>> progressivePrices = new HashMap(); 
    public boolean pricesExTaxes = false;
    public boolean privatePeopleDoNotPayTaxes = false;
    public HashMap<String, Integer> channelDiscount = new HashMap();
    /* ItemtypeId, Map<NumberOfGuests, Price>> */
    public HashMap<String, HashMap<Integer, Double>> derivedPrices = new HashMap();
    public Double price_mon;
    public Double price_tue;
    public Double price_wed;
    public Double price_thu;
    public Double price_fri;
    public Double price_sat;
    public Double price_sun;
    public HashMap<String, Double> productPrices = new HashMap();
    public HashMap<Integer, Integer> longTermDeal = new HashMap();
    public HashMap<Integer, Double> coveragePrices = new HashMap();
    public Integer coverageType = PmsPricing.PmsPricingCoverageType.PERCENTAGE;
}
