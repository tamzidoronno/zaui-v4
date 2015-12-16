package com.thundashop.core.pmsmanager;

import java.util.Date;

public class StatisticsEntry {
    public Date date;
    public Double totalPrice = 0.0;
    public Double avgPrice = 0.0;
    public Integer roomsRentedOut = 0;
    public Integer spearRooms = 0;
    public Integer totalRooms = 0;
    public Integer coverage = 0;
    public Double bugdet = 0.0;
    
    public void finalize() {
        avgPrice = totalPrice / roomsRentedOut;
        totalPrice = (double)Math.round(totalPrice);
        avgPrice = (double)Math.round(avgPrice);
        spearRooms = totalRooms - roomsRentedOut;
        double result = (double)roomsRentedOut / (double)totalRooms;
        result *= 100;
        coverage = (int)result;
    }
}