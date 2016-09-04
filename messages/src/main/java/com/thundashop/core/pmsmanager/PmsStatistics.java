package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

public class PmsStatistics implements Serializable {
    public LinkedList<StatisticsEntry> entries = new LinkedList();
    public LinkedList<SalesStatisticsEntry> salesEntries = new LinkedList(); 
    
    void addEntry(StatisticsEntry entry) {
        entries.add(entry);
    }

    void buildTotal() {
        StatisticsEntry total = new StatisticsEntry();
        double budget = 0;
        for(StatisticsEntry entry : entries) {
            total.totalPrice += entry.totalPrice;
            total.avgPrice = -1.0;
            budget = entry.bugdet;
            
            total.roomsRentedOut = -1;
            total.spearRooms = -1;
            total.coverage += entry.coverage;
        }
        total.bugdet = budget;
        total.coverage = total.coverage / entries.size();
        entries.add(total);
    }

    void buildTotalSales() {
        SalesStatisticsEntry result = new SalesStatisticsEntry();
        int avgCount = 0;
        for(SalesStatisticsEntry entry : salesEntries) {
            result.totalPrice += entry.totalPrice;
            result.nights += entry.nights;
            result.numberOfOrders += entry.numberOfOrders;
            result.avgOrderPrice += entry.avgOrderPrice;
            if(entry.avgOrderPrice > 0) {
                avgCount++;
            }
            result.avgPrice += entry.avgPrice;
            for(String type : entry.paymentTypes.keySet()) {
                result.addPayment(type, entry.paymentTypes.get(type));
            }
        }
        if(avgCount > 0) {
            result.avgOrderPrice /= avgCount;
        }
        
        salesEntries.add(result);
    }

    void setView(PmsBookingFilter filter) {
        String timeInterval = filter.timeInterval;
        if(timeInterval == null || timeInterval.isEmpty() || timeInterval.equals("daily")) {
            return;
        }
        
        Calendar entrycal = Calendar.getInstance();
        
        HashMap<String, SalesStatisticsEntry> newSalesEntries = new HashMap();
        HashMap<String, StatisticsEntry> newEntries = new HashMap();
        
        for(SalesStatisticsEntry entry : salesEntries) {
            entrycal.setTime(entry.date);
            addSalesEntry(newSalesEntries, filter, entry, entrycal);
        }
        
        for(StatisticsEntry entry : this.entries) {
            if(entry.date == null) {
                continue;
            }
            entrycal.setTime(entry.date);
            addEntry(newEntries, filter, entry, entrycal);
        }
        
        entries.clear();
        entries.addAll(newEntries.values());
        
        salesEntries.clear();
        salesEntries.addAll(newSalesEntries.values());
        
        Collections.sort(entries, new Comparator<StatisticsEntry>(){
            public int compare(StatisticsEntry o1, StatisticsEntry o2){
                return o1.date.compareTo(o2.date);
            }
       });
        
        Collections.sort(salesEntries, new Comparator<SalesStatisticsEntry>(){
            public int compare(SalesStatisticsEntry o1, SalesStatisticsEntry o2){
                return o1.date.compareTo(o2.date);
            }
       });
    }

    private void addSalesEntry(HashMap<String, SalesStatisticsEntry> entries, PmsBookingFilter filter, SalesStatisticsEntry entry, Calendar entrycal) {
        String key = generateCorrectKey(filter, entrycal);
        SalesStatisticsEntry current = new SalesStatisticsEntry();
        if(entries.containsKey(key)) {
            current = entries.get(key);
        } else {
            current.date = entrycal.getTime();
            entries.put(key, current);
        }
        
        current.append(entry);
    }

    private void addEntry(HashMap<String, StatisticsEntry> entries, PmsBookingFilter filter, StatisticsEntry entry, Calendar entrycal) {
        String key = generateCorrectKey(filter, entrycal);
        StatisticsEntry current = new StatisticsEntry();
        if(entries.containsKey(key)) {
            current = entries.get(key);
        } else {
            current.date = entrycal.getTime();
            entries.put(key, current);
        }
        
        current.append(entry);    
    }
    
    private String generateCorrectKey(PmsBookingFilter filter, Calendar entrycal) {
        Integer year, month, day;
        entrycal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        if(filter.timeInterval.equals("weekly")) {
            entrycal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        } else if(filter.timeInterval.equals("monthly")) {
            entrycal.set(Calendar.DAY_OF_MONTH, 1);
        } else {
            entrycal.set(Calendar.DAY_OF_YEAR, 1);
        }
        
        year = entrycal.get(Calendar.YEAR);
        month = entrycal.get(Calendar.MONTH);
        day = entrycal.get(Calendar.DAY_OF_MONTH);
        
        String key = year + "-" + month + "-" + day;
        return key;
    }

}
