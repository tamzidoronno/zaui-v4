package com.thundashop.core.pmsmanager;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

public class PmsOrderStatistics implements Serializable  {
    LinkedList<PmsOrderStatisticsEntry> entries = new LinkedList();
    private List<String> roomProducts = new ArrayList();
    private final HashMap<String, User> users;
    
    public PmsOrderStatistics(List<String> roomProducts, HashMap<String, User> users) {
        this.roomProducts = roomProducts;
        this.users = users;
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

        PmsOrderStatisticsEntry entry = new PmsOrderStatisticsEntry();
        double total = 0.0;
        int orderscount = 0;
        for(Order order : ordersToUse) {
            if(filter.displayType == null || filter.displayType.equals("dayregistered")) {
                if(!order.createdOnDay(cal.getTime())) {
                    continue;
                }
                
                if (order.cart == null)
                    continue;
                
                orderscount++;
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
                    total += (item.getProduct().priceExTaxes  * item.getCount());
                    addProductOrderPrice(item.getProduct().id, order.id, (item.getProduct().priceExTaxes * item.getCount()), entry.priceExOrders);
                    addProductOrderPrice(item.getProduct().id, order.id, (item.getProduct().price * item.getCount()), entry.priceIncOrders);
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
                    addProductOrderPrice(item.getProduct().id, order.id, (item.getProduct().priceExTaxes * item.getCount()), entry.priceExOrders);
                    addProductOrderPrice(item.getProduct().id, order.id, (item.getProduct().price * item.getCount()), entry.priceIncOrders);
                }
            } else if(filter.displayType.equals("dayslept")) {
                for(CartItem item : order.cart.getItems()) {
                    double secondsInDay = item.getSecondsForDay(cal, filter.shiftHours != 0);
                    if(secondsInDay == 0.0) {
                        continue;
                    }
                    Double inc = priceInc.get(item.getProduct().id);
                    Double ex = priceEx.get(item.getProduct().id);
                    
                    if(roomProducts != null && !roomProducts.contains(item.getProduct().id)) {
                        secondsInDay = -1;
                    }
                    
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
                        Date createUse = new Date(order.rowCreatedDate.getTime());
                        if(filter.shiftHours != 0) {
                            Calendar tmpCal = Calendar.getInstance();
                            tmpCal.setTime(createUse);
                            tmpCal.add(Calendar.HOUR_OF_DAY, filter.shiftHours);
                            createUse = tmpCal.getTime();
                        }
                        if(item.startsOnDate(cal.getTime(), createUse)) {
//                            System.out.println(order.incrementOrderId+";addon;"+item.getProduct().name + ";" + item.getProduct().priceExTaxes * item.getCount() + ";" + cal.getTime() + ";" + users.get(order.userId).fullName);
                            totalCalc += (item.getProduct().price * item.getCount());
                            inc += (item.getProduct().price * item.getCount());
                            ex += (item.getProduct().priceExTaxes * item.getCount());
                            orderPriceInc += (item.getProduct().price * item.getCount());
                            orderPriceEx += (item.getProduct().priceExTaxes * item.getCount());
                            addProductOrderPrice(item.getProduct().id, order.id, (item.getProduct().priceExTaxes * item.getCount()), entry.priceExOrders);
                            addProductOrderPrice(item.getProduct().id, order.id, (item.getProduct().price * item.getCount()), entry.priceIncOrders);
                        }
                    } else {
                        totalCalc += item.getPriceIncForMinutes() * secondsInDay;
                        inc += item.getPriceIncForMinutes() * secondsInDay;
                        ex += item.getPriceExForMinutes() * secondsInDay;
                        orderPriceInc += item.getPriceIncForMinutes() * secondsInDay;
                        orderPriceEx += item.getPriceExForMinutes() * secondsInDay;
                        addProductOrderPrice(item.getProduct().id, order.id, item.getPriceExForMinutes() * secondsInDay, entry.priceExOrders);
                        addProductOrderPrice(item.getProduct().id, order.id, item.getPriceIncForMinutes() * secondsInDay, entry.priceIncOrders);
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
                    addProductOrderPrice(item.getProduct().id, order.id, (item.getProduct().priceExTaxes * item.getCount()), entry.priceExOrders);
                    addProductOrderPrice(item.getProduct().id, order.id, (item.getProduct().price * item.getCount()), entry.priceIncOrders);
                }
            }
        }
        
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

    private void addProductOrderPrice(String productId, String orderId, double price, HashMap<String, HashMap<String, Double>> toAdd) {
        if(!toAdd.containsKey(productId)) {
            toAdd.put(productId, new HashMap());
        }
        
        Double current = 0.0;
        if(toAdd.get(productId).containsKey(orderId)) {
            current = toAdd.get(productId).get(orderId);
        }
        
        current += price;
        
        toAdd.get(productId).put(orderId, current);
    }

    void removeEmptyProducts() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
