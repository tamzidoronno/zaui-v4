/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class GetShopTimer {
    private static GetShopTimer timers = new GetShopTimer();
    
    private HashMap<Long, Map<Long, GetShopTimePerformanceEntry>> logs = new HashMap();
    
    private void clear() {
        Long threadId = Thread.currentThread().getId();
        logs.put(threadId, new HashMap<Long, GetShopTimePerformanceEntry>());
    }
    
   
    
    public void addLogEntry(String description, String managerName) {
        if(logs.get(Thread.currentThread().getId()) == null) {
            return;
        }
        GetShopTimePerformanceEntry entry = new GetShopTimePerformanceEntry();
        entry.sequence = logs.get(Thread.currentThread().getId()).size();
        entry.milliseconds = System.currentTimeMillis();
        entry.description = description;
        entry.accumulatedTiming = entry.milliseconds - getFirstTime();
        entry.betweenLastLoggedAndNow = entry.milliseconds - getLastLoggedTime();
        entry.managerName = managerName;
        
        Map<Long, GetShopTimePerformanceEntry> maps = logs.get(Thread.currentThread().getId());
        maps.put(new Long(maps.size()), entry);
    }

    private long getFirstTime() {
        GetShopTimePerformanceEntry firstEntry = logs.get(Thread.currentThread().getId()).get(0L);
        
        if (firstEntry == null) {
            return System.currentTimeMillis();
        }
        
        return firstEntry.milliseconds;
    }

    private long getLastLoggedTime() {
        Map<Long, GetShopTimePerformanceEntry> maps = logs.get(Thread.currentThread().getId());
        GetShopTimePerformanceEntry lastEntry = maps.get(new Long(maps.size() - 1));
        
        if (lastEntry == null) {
            return System.currentTimeMillis();
        }
        
        return lastEntry.milliseconds;
    }
    
    public static void start() {
        timers.clear();
        timers.addLogEntry("Started", "StoreHandler");
    }
    
    public static void destroy() {
        timers.clear();
    }
    
    public static void timeEntry(String description, String managerName) {
        timers.addLogEntry(description, managerName);
    }
    
    public static String getPrintedTiming() {
        Map<Long, GetShopTimePerformanceEntry> maps = timers.logs.get(Thread.currentThread().getId());
        
        if (maps == null) {
            return "";
        }
        
        if (maps.size() < 2) {
            return "";
        }
        
        String lines = "";
        for (GetShopTimePerformanceEntry timeEntry : maps.values()) {
            lines += "      " + timeEntry.sequence + " | " + String.format("%10d", timeEntry.betweenLastLoggedAndNow) + " | " + String.format("%10d", timeEntry.accumulatedTiming) + " | " + timeEntry.managerName + " | " + timeEntry.description + "\n";
        }
        
        return lines;
    }
}
