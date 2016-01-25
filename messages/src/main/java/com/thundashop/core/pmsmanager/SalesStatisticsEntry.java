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
    
    public void finalize() {
        avgOrderPrice = (int)(totalPrice / numberOfOrders);
    }

    void addPayment(String paymentType, Double total) {
        Double start = paymentTypes.get(paymentType);
        if(start == null) {
            start = 0.0;
        }
        
        start += total;
        totalPrice = (double)Math.round(totalPrice);
        paymentTypes.put(paymentType, start);
        
    }
}
