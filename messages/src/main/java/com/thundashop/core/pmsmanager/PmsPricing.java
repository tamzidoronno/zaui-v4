
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.SortedSet;
import java.util.TreeSet;

public class PmsPricing extends DataCommon {

    LinkedHashMap<Integer, Integer> getLongTermDeal() {
        if(longTermDeal == null) {
            return new LinkedHashMap();
        }
        
        SortedSet<Integer> keys = new TreeSet<Integer>(longTermDeal.keySet());
        LinkedHashMap<Integer, Integer> result = new LinkedHashMap();
        for(Integer k : keys) {
            result.put(k, longTermDeal.get(k));
        }
        return result;
    }
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
    public HashMap<String, HashMap<Integer, Double>> derivedPricesChildren = new HashMap();
    public Double price_mon;
    public Double price_tue;
    public Double price_wed;
    public Double price_thu;
    public Double price_fri;
    public Double price_sat;
    public Double price_sun;
    public HashMap<String, Double> productPrices = new HashMap();
    public LinkedHashMap<Integer, Integer> longTermDeal = new LinkedHashMap();
    public HashMap<Integer, Double> coveragePrices = new HashMap();
    public Integer coverageType = PmsPricing.PmsPricingCoverageType.PERCENTAGE;
    
    public Date getStartDate() {
        Date retval = null;
        for(String type : dailyPrices.keySet()) {
            HashMap<String, Double> priceMap = dailyPrices.get(type);
            for(String date : priceMap.keySet()) {
                if(date.contains("-")) {
                    Date checkDate = PmsBookingRooms.convertOffsetToDate(date);
                    if(retval == null || retval.getTime() > checkDate.getTime()) {
                        retval = checkDate;
                    }
                }
            }
        }
        
        return retval;
    }
    
    public Date getEndDate() {
        Date retval = null;
        for(String type : dailyPrices.keySet()) {
            HashMap<String, Double> priceMap = dailyPrices.get(type);
            for(String date : priceMap.keySet()) {
                if(date.contains("-")) {
                    Date checkDate = PmsBookingRooms.convertOffsetToDate(date);
                    if(retval == null || retval.getTime() < checkDate.getTime()) {
                        retval = checkDate;
                    }
                }
            }
        }
        
        return retval;
    }
}
