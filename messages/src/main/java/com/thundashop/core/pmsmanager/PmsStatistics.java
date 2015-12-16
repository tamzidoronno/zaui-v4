package com.thundashop.core.pmsmanager;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class PmsStatistics {
    public LinkedHashMap<Date, StatisticsEntry> entries = new LinkedHashMap();

    void addEntry(StatisticsEntry entry) {
        entries.put(entry.date, entry);
    }
}
