package com.thundashop.core.pmsmanager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class PmsMonthlyOrderStatistics {
    LinkedList<PmsOrderStatisticsEntry> entries = new LinkedList();
    double totalInc;
    double totalEx;

    void setData(LinkedList<PmsOrderStatisticsEntry> res) {
        Calendar cal = Calendar.getInstance();
        LinkedHashMap<String,PmsOrderStatisticsEntry> finalResult = new LinkedHashMap();
        for(PmsOrderStatisticsEntry entry : res) {
            cal.setTime(entry.day);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            String offset = cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.YEAR);
            PmsOrderStatisticsEntry result = new PmsOrderStatisticsEntry();
            if(finalResult.containsKey(offset)) {
                result = finalResult.get(offset);
            } else {
                finalResult.put(offset, result);
                result.day = cal.getTime();
            }
            result.append(entry);
        }
        entries = new LinkedList(finalResult.values());
        
        List<PmsOrderStatisticsEntry> toRemove = new ArrayList();
        for(PmsOrderStatisticsEntry stats : entries) {
            if(stats.priceEx.isEmpty()) {
                toRemove.add(stats);

            }
        }
        
        entries.removeAll(toRemove);
    }
    
}
