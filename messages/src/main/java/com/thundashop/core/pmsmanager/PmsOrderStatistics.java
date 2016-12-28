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
    
    public PmsOrderStatistics() {
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
        HashMap<String, Double> priceIncOrder = new HashMap();
        HashMap<String, Double> priceEx = new HashMap();
        HashMap<Long, Double> orderEx = new HashMap();
        HashMap<Long, Double> orderInc = new HashMap();

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
                    
                    calculate(item, orderEx, order, orderInc, inc, ex, priceInc, priceEx);
                }
            } else if(filter.displayType.equals("firstdayslept")) {
                for(CartItem item : order.cart.getItems()) {
                    if(!item.startsOnDate(cal.getTime(), order.rowCreatedDate)) {
                        continue;
                    }
                    Double inc = priceInc.get(item.getProduct().id);
                    Double ex = priceEx.get(item.getProduct().id);
                    
                    if(inc == null) { inc = 0.0; }
                    if(ex == null) { ex = 0.0; }
                    
                    calculate(item, orderEx, order, orderInc, inc, ex, priceInc, priceEx);
                }
            } else if(filter.displayType.equals("dayslept")) {
                for(CartItem item : order.cart.getItems()) {
                    double secondsInDay = item.getSecondsForDay(cal);
                    if(order.incrementOrderId == 103977 && secondsInDay > 0 || secondsInDay < -1) {
                        System.out.println("Secondprice: " + item.getPriceIncForMinutes());
                        System.out.println("Seconds: " + secondsInDay + " - " + cal.getTime() + " : " + (secondsInDay * item.getPriceIncForMinutes()));
                        System.out.println("-----------------");
                    }
                    if(secondsInDay == 0.0) {
                        continue;
                    }
                    Double inc = priceInc.get(item.getProduct().id);
                    Double ex = priceEx.get(item.getProduct().id);
                    
                    Double totalCalc = priceIncOrder.get(order.id);
                    if(totalCalc == null) { totalCalc = 0.0; }
                    if(inc == null) { inc = 0.0; }
                    if(ex == null) { ex = 0.0; }
                    
                    Double orderPriceInc = 0.0;
                    Double orderPriceEx = 0.0;

                    if(orderEx.containsKey(order.incrementOrderId)) {
                        orderPriceEx = orderEx.get(order.incrementOrderId);
                    }
                    if(orderInc.containsKey(order.incrementOrderId)) {
                        orderPriceInc = orderInc.get(order.incrementOrderId);
                    }
                    
                    if(secondsInDay == -1) {
                        if(item.startsOnDate(cal.getTime(), order.rowCreatedDate)) {
                            totalCalc += (item.getProduct().price * item.getCount());
                            inc += (item.getProduct().price * item.getCount());
                            ex += (item.getProduct().priceExTaxes * item.getCount());
                            orderPriceInc += (item.getProduct().price * item.getCount());
                            orderPriceEx += (item.getProduct().priceExTaxes * item.getCount());
                        }
                    } else {
                        totalCalc += item.getPriceIncForMinutes() * secondsInDay;
                        inc += item.getPriceIncForMinutes() * secondsInDay;
                        ex += item.getPriceExForMinutes() * secondsInDay;
                        orderPriceInc += item.getPriceIncForMinutes() * secondsInDay;
                        orderPriceEx += item.getPriceExForMinutes() * secondsInDay;
                    }
                    
                    priceInc.put(item.getProduct().id, inc);
                    priceEx.put(item.getProduct().id, ex);
                    if(totalCalc != 0.0) {
                        priceIncOrder.put(order.id, totalCalc);
                        orderInc.put(order.incrementOrderId, orderPriceInc);                    
                        orderEx.put(order.incrementOrderId, orderPriceEx);
                    }
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
                    
                    calculate(item, orderEx, order, orderInc, inc, ex, priceInc, priceEx);
                }
            }
        }
        
        PmsOrderStatisticsEntry entry = new PmsOrderStatisticsEntry();
        entry.day = cal.getTime();
        entry.priceEx = priceEx;
        entry.priceInc = priceInc;
        entry.priceIncOnOrder = priceIncOrder;
        entry.orderEx = orderEx;
        entry.orderInc = orderInc;
        entries.add(entry);
    }

    private void calculate(CartItem item, HashMap<Long, Double> orderEx, Order order, HashMap<Long, Double> orderInc, Double inc, Double ex, HashMap<String, Double> priceInc, HashMap<String, Double> priceEx) {
        Double tmpEx = 0.0;
        Double tmpInc = 0.0;
        if(orderEx.containsKey(order.incrementOrderId)) {
            tmpEx = orderEx.get(order.incrementOrderId);
        }
        if(orderInc.containsKey(order.incrementOrderId)) {
            tmpInc = orderInc.get(order.incrementOrderId);
        }
        tmpInc += (item.getProduct().price * item.getCount());
        tmpEx += (item.getProduct().priceExTaxes * item.getCount());
        orderEx.put(order.incrementOrderId, tmpEx);
        orderInc.put(order.incrementOrderId, tmpInc);
        
        inc += (item.getProduct().price * item.getCount());
        ex += (item.getProduct().priceExTaxes * item.getCount());
        priceInc.put(item.getProduct().id, inc);
        priceEx.put(item.getProduct().id, ex);
    }

    double getTotalForOrder(String id) {
        double total = 0.0;
        for(PmsOrderStatisticsEntry entry : entries) {
            Double extra = entry.priceIncOnOrder.get(id);
            if(extra != null) {
                total += entry.priceIncOnOrder.get(id);
            }
        }
        return total;
    }
    
}
