package com.thundashop.core.pmsmanager;

import java.util.LinkedList;

public class PmsStatistics {
    public LinkedList<StatisticsEntry> entries = new LinkedList();

    void addEntry(StatisticsEntry entry) {
        entries.add(entry);
    }

    void buildTotal() {
        StatisticsEntry total = new StatisticsEntry();
        for(StatisticsEntry entry : entries) {
            total.totalPrice += entry.totalPrice;
            total.avgPrice += entry.avgPrice;
            
            total.roomsRentedOut += entry.roomsRentedOut;
            total.spearRooms += entry.spearRooms;
            total.coverage += entry.coverage;
        }
        total.coverage = total.coverage / entries.size();
        entries.add(total);
    }
}
