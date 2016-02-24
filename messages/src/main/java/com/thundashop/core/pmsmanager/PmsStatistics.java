package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.LinkedList;

public class PmsStatistics implements Serializable {
    public LinkedList<StatisticsEntry> entries = new LinkedList();
    public LinkedList<SalesStatisticsEntry> salesEntries = new LinkedList(); 
    
    void addEntry(StatisticsEntry entry) {
        entries.add(entry);
    }

    void buildTotal() {
        StatisticsEntry total = new StatisticsEntry();
        for(StatisticsEntry entry : entries) {
            total.totalPrice += entry.totalPrice;
            total.avgPrice = -1.0;
            
            total.roomsRentedOut = -1;
            total.spearRooms = -1;
            total.coverage += entry.coverage;
        }
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
}
