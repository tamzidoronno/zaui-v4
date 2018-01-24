package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PmsOrderStatisticsEntry implements Serializable {
    Date day;
    HashMap<String, Double> priceInc = new HashMap();
    //productid, price
    HashMap<String, Double> priceEx = new HashMap();
    //roomid_productid, price
    HashMap<String, Double> priceExRoom = new HashMap();
    HashMap<String, Double> priceIncOnOrder = new HashMap();
    HashMap<Long, Double> orderInc = new HashMap();
    HashMap<Long, Double> orderEx = new HashMap();
    
    //porductId, Orderid, price
    HashMap<String, HashMap<String, Double>> priceIncOrders = new HashMap();
    //porductId, Orderid, price
    HashMap<String, HashMap<String, Double>> priceExOrders = new HashMap();

    List<String> getOrderIds() {
        List<String> result = new ArrayList();
        for(String productId : priceExOrders.keySet()) {
            result.addAll(priceExOrders.get(productId).keySet());
        }
        
        List<String> uniqueList = new ArrayList();
        for(String orderId : result) {
            if(uniqueList.contains(orderId)) {
                continue;
            }
            uniqueList.add(orderId);
        }
        
        return uniqueList;
    }

    void append(PmsOrderStatisticsEntry entry) {
        for(String key : entry.priceEx.keySet()) {
            Double res = 0.0;
            if(priceEx.containsKey(key)) {
                res = priceEx.get(key);
            }
            res += entry.priceEx.get(key);
            priceEx.put(key, res);
        }
        
        for(String key : entry.priceInc.keySet()) {
            Double res = 0.0;
            if(priceInc.containsKey(key)) {
                res = priceInc.get(key);
            }
            res += entry.priceInc.get(key);
            priceInc.put(key, res);
        }
    }
}
