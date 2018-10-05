package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

class SalesStatisticsEntry implements Serializable {
    public Date date;
    public Double totalPrice = 0.0;
    public HashMap<String, Double> paymentTypes = new HashMap();
    public Integer nights = 0;
    public Integer avgPrice = 0;
    public Integer numberOfOrders = 0;
    public Integer avgOrderPrice = 0;
    public Double bookingValue = 0.0;
    public Double bookings = 0.0;
    
    public void finalize() {
        avgOrderPrice = (int)(totalPrice / numberOfOrders);
        
    }

    void addPayment(String paymentType, Double total) {
        Double start = paymentTypes.get(paymentType);
        if(start == null) {
            start = 0.0;
        }
        
        start += total;
        paymentTypes.put(paymentType, start);
        
    }

    void append(SalesStatisticsEntry entry) {
        totalPrice += entry.totalPrice;
        nights += entry.nights;
        numberOfOrders += entry.numberOfOrders;
        avgPrice = (int)(totalPrice / nights);
        avgOrderPrice = (int)(totalPrice / numberOfOrders);
        for(String key : entry.paymentTypes.keySet()) {
            Double newVal = 0.0;
            if(paymentTypes.containsKey(key)) {
                newVal = paymentTypes.get(key);
            }
            newVal += entry.paymentTypes.get(key);
            entry.paymentTypes.put(key, newVal);
        }
    }
}
