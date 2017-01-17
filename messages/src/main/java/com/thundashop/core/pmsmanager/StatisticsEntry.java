package com.thundashop.core.pmsmanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class StatisticsEntry {
    public Date date;
    public Double totalPrice = 0.0;
    public Double avgPrice = 0.0;
    public Integer roomsRentedOut = 0;
    public Integer spearRooms = 0;
    public Integer totalRooms = 0;
    public Integer coverage = 0;
    public Double bugdet = 0.0;
    public HashMap<Integer, Integer> addonsCount = new HashMap();
    public HashMap<Integer, Double> addonsPrice = new HashMap();
    public HashMap<Integer, Double> addonsPriceEx = new HashMap();
    
    public HashMap<String, Integer> uniqueGuests = new HashMap();
    public HashMap<String, Integer> guests = new HashMap();
    public HashMap<String, Integer> guestsCompany = new HashMap();
    public HashMap<String, Integer> guestsConference = new HashMap();
    public HashMap<String, Integer> guestsRegular = new HashMap();
    public List<String> roomsIncluded = new ArrayList();
    
    public void finalize() {
        avgPrice = totalPrice / roomsRentedOut;
        totalPrice = (double)Math.round(totalPrice);
        avgPrice = (double)Math.round(avgPrice);
        spearRooms = totalRooms - roomsRentedOut;
        double result = (double)roomsRentedOut / (double)totalRooms;
        result *= 100;
        coverage = (int)result;
    }

    void append(StatisticsEntry entry) {
        totalPrice += entry.totalPrice;
        roomsRentedOut += entry.roomsRentedOut;
        totalRooms += entry.totalRooms;
        finalize();
    }
}