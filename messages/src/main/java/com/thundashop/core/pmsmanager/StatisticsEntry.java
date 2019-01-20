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
    public Integer arrivals = 0;
    public Integer departures = 0;
    public Integer spearRooms = 0;
    public Integer totalRooms = 0;
    public Integer coverage = 0;
    public Integer guestCount = 0;
    public Integer coverageOriginal = 0;
    public Integer roomsNotIncluded = 0;
    public Double revPar = 0.0;
    public Double bugdet = 0.0;
    public HashMap<Integer, Integer> addonsCount = new HashMap();
    public HashMap<Integer, Double> addonsPrice = new HashMap();
    public HashMap<String, Double> roomsPrice = new HashMap();
    public HashMap<String, Double> roomsPriceOnDifferentTypes = new HashMap();
    public HashMap<String, Double> roomsPriceForecasted = new HashMap();
    public HashMap<Integer, Double> addonsPriceEx = new HashMap();
    public HashMap<String, Double> roomsRemovedFromStatistics = new HashMap();
    
    public HashMap<String, Integer> uniqueGuests = new HashMap();
    public HashMap<String, Integer> guests = new HashMap();
    public HashMap<String, Integer> guestsCompany = new HashMap();
    public HashMap<String, Integer> guestsConference = new HashMap();
    public HashMap<String, Integer> guestsRegular = new HashMap();
    public List<String> roomsIncluded = new ArrayList();
    public List<String> ordersUsed = new ArrayList();
    public List<String> roomsNotOnSameDay = new ArrayList();
    boolean isTotal = false;
    public Integer squareMetres = 0;
    public Integer totalRoomsOriginal;
    public Double totalForcasted = 0.0;
    public double totalRemaining = 0.0;
    double avgPriceForcasted = 0.0;
    
    public void finalize() {
        
        HashMap<String, Integer> newList = new HashMap();
        for(String k : ordersUsed) {
            newList.put(k, 1);
        }
        ordersUsed = new ArrayList(newList.keySet());
        
        if(roomsRentedOut > 0) {
            avgPrice = totalPrice / roomsRentedOut;
            avgPriceForcasted = totalForcasted / roomsRentedOut;
        } else {
            avgPrice = 0.0;
        }
        totalPrice = (double)Math.round(totalPrice);
        avgPrice = (double)Math.round(avgPrice);
        avgPriceForcasted = (double)Math.round(avgPriceForcasted);
        spearRooms = totalRooms - roomsRentedOut;
        revPar = (double)Math.round(totalPrice / (roomsRentedOut + spearRooms));
        double result = (double)roomsRentedOut / (double)totalRooms;
        result *= 100;
        coverage = (int)result;
        if(totalRoomsOriginal != null && roomsRentedOut != null) {
            result = (double)roomsRentedOut / (double)totalRoomsOriginal;
            result *= 100;
        }
        coverageOriginal = (int)result;
    }

    void append(StatisticsEntry entry) {
        totalPrice += entry.totalPrice;
        roomsRentedOut += entry.roomsRentedOut;
        totalRooms += entry.totalRooms;
        finalize();
    }

    void addRoomsNotIncluded(PmsOrderStatisticsEntry entry, StatisticsEntry statEntry, String offset) {
        roomsRemovedFromStatistics = new HashMap();
        for(String roomId : entry.priceExRoom.keySet()) {
            String tmpRoomId = roomId.replace("_"+offset, "");
            if(!statEntry.roomsIncluded.contains(tmpRoomId)) {
                roomsRemovedFromStatistics.put(tmpRoomId, entry.priceExRoom.get(roomId));
            }
        }
    }
}