package com.thundashop.core.pmsmanager;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.data.Order;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

public class PmsOrderStatistics implements Serializable  {
    LinkedList<PmsOrderStatisticsEntry> entries = new LinkedList();
    
    PmsOrderStatistics() {
    }
    
    public void createStatistics(List<Order> ordersToUse, PmsOrderStatsFilter filter) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(filter.start);
        while(true) {
            createStatsEntry(cal, ordersToUse, filter);
            
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if(cal.getTime().after(filter.end)) {
                break;
            }
        }
    }

    private void createStatsEntry(Calendar cal,List<Order> ordersToUse, PmsOrderStatsFilter filter) {
        HashMap<String, Double> priceInc = new HashMap();
        HashMap<String, Double> priceEx = new HashMap();
        for(Order order : ordersToUse) {
            if(filter.displayType == null || filter.displayType.equals("dayregistered")) {
                if(!order.createdOnDay(cal.getTime())) {
                    continue;
                }
                
                if (order.cart == null)
                    continue;
                
                for(CartItem item : order.cart.getItems()) {
                    Double inc = priceInc.get(item.getProduct().id);
                    Double ex = priceEx.get(item.getProduct().id);
                    
                    if(inc == null) {
                        inc = 0.0;
                    }
                    if(ex == null) {
                        ex = 0.0;
                    }
                    inc += (item.getProduct().price * item.getCount());
                    ex += (item.getProduct().priceExTaxes * item.getCount());
                    
                    priceInc.put(item.getProduct().id, inc);
                    priceEx.put(item.getProduct().id, ex);
                }
            } else if(filter.displayType.equals("firstdayslept")) {
                for(CartItem item : order.cart.getItems()) {
                    if(!item.startsOnDate(cal.getTime(), order.rowCreatedDate)) {
                        continue;
                    }
                    Double inc = priceInc.get(item.getProduct().id);
                    Double ex = priceEx.get(item.getProduct().id);
                    
                    if(inc == null) { 
                        inc = 0.0;
                    }
                    if(ex == null) {
                        ex = 0.0;
                    }
                    inc += (item.getProduct().price * item.getCount());
                    ex += (item.getProduct().priceExTaxes * item.getCount());
                    
                    priceInc.put(item.getProduct().id, inc);
                    priceEx.put(item.getProduct().id, ex);
                }
            } else if(filter.displayType.equals("dayslept")) {
                for(CartItem item : order.cart.getItems()) {
                    double minutesInDay = item.getNumberOfMinutesForDay(cal);
                    if(minutesInDay == 0.0) {
                        continue;
                    }
                    Double inc = priceInc.get(item.getProduct().id);
                    Double ex = priceEx.get(item.getProduct().id);
                    
                    if(inc == null) { 
                        inc = 0.0;
                    }
                    if(ex == null) {
                        ex = 0.0;
                    }
                    
                    if(minutesInDay == -1 && item.startsOnDate(cal.getTime(), order.rowCreatedDate)) {
                        inc += (item.getProduct().price * item.getCount());
                        ex += (item.getProduct().priceExTaxes * item.getCount());
                    } else {
                        inc += item.getPriceIncForMinutes() * minutesInDay;
                        ex += item.getPriceExForMinutes() * minutesInDay;
                    }
                    
                    priceInc.put(item.getProduct().id, inc);
                    priceEx.put(item.getProduct().id, ex);
                }
            } else if(filter.displayType.equals("daypaid")) {
                if(!order.paidOnDay(cal.getTime())) {
                    continue;
                }
                for(CartItem item : order.cart.getItems()) {
                    Double inc = priceInc.get(item.getProduct().id);
                    Double ex = priceEx.get(item.getProduct().id);
                    
                    if(inc == null) {
                        inc = 0.0;
                    }
                    if(ex == null) {
                        ex = 0.0;
                    }
                    inc += (item.getProduct().price * item.getCount());
                    ex += (item.getProduct().priceExTaxes * item.getCount());
                    
                    priceInc.put(item.getProduct().id, inc);
                    priceEx.put(item.getProduct().id, ex);
                }
            }
        }
        
        PmsOrderStatisticsEntry entry = new PmsOrderStatisticsEntry();
        entry.day = cal.getTime();
        entry.priceEx = priceEx;
        entry.priceInc = priceInc;
        entries.add(entry);
    }
    
}
