package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class PmsStatistics implements Serializable {
    public LinkedList<StatisticsEntry> entries = new LinkedList();
    public LinkedList<SalesStatisticsEntry> salesEntries = new LinkedList(); 
    public SleepoverStatistics sleepoverstats = new SleepoverStatistics();
    HashMap<String, PmsDeliverLogStats> deliveryStats;
    
    void addEntry(StatisticsEntry entry) {
        entries.add(entry);
    }

    void buildTotal() {
        StatisticsEntry total = new StatisticsEntry();
        total.isTotal = true;
        double budget = 0;
        int avgCounter = 0;
        for(StatisticsEntry entry : entries) {
            total.totalPrice += entry.totalPrice;
            total.totalRooms += entry.totalRooms;
            total.squareMetres += entry.squareMetres;
            budget = entry.bugdet;
            
            total.roomsRentedOut += entry.roomsRentedOut;
            total.spearRooms += entry.spearRooms;
            total.coverage += entry.coverage;
            total.revPar += entry.revPar;
            total.totalRemaining += entry.totalRemaining;
            total.arrivals += entry.arrivals;
            total.departures += entry.departures;
            total.totalForcasted += entry.totalForcasted;
            total.guestCount += entry.guestCount;
            sleepoverstats.nighsSlept += entry.roomsRentedOut;
            
            sumarizeGuests(entry);
            
        }
        
        total.bugdet = budget;
        if(entries.size() > 0) {
            total.coverage = total.coverage / entries.size();
        }
        if(total.roomsRentedOut > 0) {
            total.avgPrice = (double)Math.round(total.totalPrice / total.roomsRentedOut);
            total.avgPriceForcasted = (double)Math.round(total.totalForcasted / total.roomsRentedOut);
        }
        if(entries.size() > 0) {
            total.revPar = (double)Math.round(total.revPar / entries.size());
        }
        
        
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
            result.bookingValue += entry.bookingValue;
            sleepoverstats.nightsSold += entry.nights;
            
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

    private void sumarizeGuests(StatisticsEntry entry) {
        for(String k : entry.guests.keySet()) {
            int count = entry.guests.get(k);
            if(sleepoverstats.guests.containsKey(k)) {
                count += sleepoverstats.guests.get(k);
            }
            sleepoverstats.guests.put(k, count);
        }
        
        for(String k : entry.guestsCompany.keySet()) {
            int count = entry.guestsCompany.get(k);
            if(sleepoverstats.guestsCompany.containsKey(k)) {
                count += sleepoverstats.guestsCompany.get(k);
            }
            sleepoverstats.guestsCompany.put(k, count);
        }
        
        for(String k : entry.guestsConference.keySet()) {
            int count = entry.guestsConference.get(k);
            if(sleepoverstats.guestsConference.containsKey(k)) {
                count += sleepoverstats.guestsConference.get(k);
            }
            sleepoverstats.guestsConference.put(k, count);
        }
        
        for(String k : entry.guestsRegular.keySet()) {
            int count = entry.guestsRegular.get(k);
            if(sleepoverstats.guestsRegular.containsKey(k)) {
                count += sleepoverstats.guestsRegular.get(k);
            }
            sleepoverstats.guestsRegular.put(k, count);
        }
        
        for(String k : entry.uniqueGuests.keySet()) {
            int count = entry.uniqueGuests.get(k);
            if(sleepoverstats.uniqueGuests.containsKey(k)) {
                count += sleepoverstats.uniqueGuests.get(k);
            }
            sleepoverstats.uniqueGuests.put(k, count);
        }
    }

    StatisticsEntry getTotal() {
        for(StatisticsEntry entry : entries) {
            if(entry.isTotal) {
                return entry;
            }
        }
        return null;
    }

    Integer getStayOversForDate(Date time) {
        for(StatisticsEntry entry : entries) {
            if(entry.isSameDay(time)) {
                return entry.roomsRentedOut - entry.arrivals;
            }
        }
        return 0;
        
    }

    void clearBilled() {
         for(StatisticsEntry entry : entries) {
             entry.totalPrice = 0.0;
         }
    }

}
