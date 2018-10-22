/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author boggi
 */
public class AdvancePriceYieldCalculator {

    private HashMap<String, PmsAdvancePriceYield> yields;

    public AdvancePriceYieldCalculator(HashMap<String, PmsAdvancePriceYield> advancePriceYields) {
        this.yields = advancePriceYields;
    }

    Double doCalculation(Double price, Integer coverage, String typeId, Date start) {
        if(yields == null) {
            return price;
        }
        
        for(PmsAdvancePriceYield yieldplan : yields.values()) {
            if(!yieldplan.types.contains( typeId)) {
                continue;
            }
            
            if(start.after(yieldplan.start) && start.before(yieldplan.end)) {
                return calculcatePrice(coverage, price, start, yieldplan);
            }
        }
        return price;
    }

    private Double calculcatePrice(Integer coverage, Double price, Date day, PmsAdvancePriceYield yieldplan) {
        if(coverage == null || yieldplan == null) {
            return price;
        }
        long diff = day.getTime() - System.currentTimeMillis();
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        Integer dayOffset = mapDay(days);
        Integer percentageOffset = mapCoverage(coverage);
        String key = dayOffset + "_" + percentageOffset;
        if(yieldplan.yeilds.containsKey(key)) {
            YieldEntry operation = yieldplan.yeilds.get(key);
            Double newprice = price * ((double)operation.yield/100.0);
            return newprice;
        }
                
        return price;
    }

    private Integer mapDay(long days) {
        if(days >= 7 && days < 14) { return 14; }
        if(days >= 14 && days < 28) { return 28; }
        if(days >= 28 && days < 90) { return 90; }
        if(days >= 90 && days < 180) { return 180; }
        if(days >= 180 && days < 365) { return 365; }
        if(days >= 365 && days < 730) { return 730; }
        return (int)days;
    }

    private Integer mapCoverage(Integer coverage) {
        if(coverage >= 0 && coverage < 10) { return 10; }
        if(coverage >= 10 && coverage < 20) { return 20; }
        if(coverage >= 20 && coverage < 30) { return 30; }
        if(coverage >= 30 && coverage < 40) { return 40; }
        if(coverage >= 40 && coverage < 50) { return 50; }
        if(coverage >= 50 && coverage < 60) { return 60; }
        if(coverage >= 60 && coverage < 70) { return 70; }
        if(coverage >= 70 && coverage < 75) { return 75; }
        if(coverage >= 75 && coverage < 80) { return 80; }
        if(coverage >= 80 && coverage < 85) { return 85; }
        if(coverage >= 85 && coverage < 90) { return 90; }
        if(coverage >= 90 && coverage < 95) { return 95; }
        if(coverage >= 95 && coverage <= 100) { return 100; }
        
        return coverage;
    }
    
}
