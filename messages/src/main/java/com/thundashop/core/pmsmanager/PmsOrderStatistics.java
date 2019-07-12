package com.thundashop.core.pmsmanager;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.mongodb.morphia.annotations.Transient;

public class PmsOrderStatistics implements Serializable  {
    LinkedList<PmsOrderStatisticsEntry> entries = new LinkedList();
    private List<String> roomProducts = new ArrayList();
    private final HashMap<String, User> users;
    private HashMap<String, Boolean> canUseNew = new HashMap();
    private HashMap<String, Double> usersTotal = new HashMap();
    
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
        
        HashMap<String, Order> orderMap = new HashMap();
        for(Order ord : ordersToUse) {
            orderMap.put(ord.id, ord);
        }
        
        for(PmsOrderStatisticsEntry entry : entries) {
            for(String productId : entry.priceExOrders.keySet()) {
                HashMap<String, Double> ordersHashMap = entry.priceExOrders.get(productId);
                for(String orderId : ordersHashMap.keySet()) {
                    Order order = orderMap.get(orderId);
                    String userId = order.userId;
                    
                    Double totalOnCustomer = 0.0;
                    if(usersTotal.containsKey(userId)) {
                        totalOnCustomer = usersTotal.get(userId);
                    }
                    totalOnCustomer += ordersHashMap.get(orderId);
                    usersTotal.put(userId, totalOnCustomer);
               }
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
            if(filter.displayType.equals("dayslept")) {
                if(!order.isForPeriodedaySlept(cal.getTime())) {
                    continue;
                }
            }
            
            double ordertotal = order.getTotalAmountUnfinalized();
            
            if(ordertotal < 1.0 && ordertotal > -1.0) {
                continue;
            }
            
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
                for(CartItem item : order.cart.getItemsUnfinalized()) {
                    if(canUseNewCalculation(item, entry, cal, order)) {
                        PmsOrderStatisticsEntry tmpEntry = calculatePeriodisatedValues(order, item,cal);
                        copy(priceEx, tmpEntry.priceEx);
                        copy(priceInc, tmpEntry.priceInc);
                        copyMap(orderEx, tmpEntry.orderEx);
                        copyMap(orderInc, tmpEntry.orderInc);
                        copy(entry.priceExRoom, tmpEntry.priceExRoom);
                        copyDoubleMap(entry.priceExOrders, tmpEntry.priceExOrders);
                        copyDoubleMap(entry.priceIncOrders, tmpEntry.priceIncOrders);
                    } else {
                        /** @this is a deprecated calculation routine. */
                        double secondsInDay = item.getSecondsForDay(cal, filter.shiftHours != 0);
                        if(secondsInDay == 0.0) {
                            continue;
                        }
                        Double inc = priceInc.get(item.getProduct().id);
                        Double ex = priceEx.get(item.getProduct().id);

                        if(roomProducts != null && !roomProducts.contains(item.getProduct().id) && !item.getProduct().id.equals("815d31ef-716d-4906-aae1-bddb028b55f4")) {
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

                            Double extotal = item.getSeconds() * item.getPriceExForMinutes();
                            Double itemTotalEx = item.getTotalEx();
                            Double diff = (extotal - itemTotalEx);
                            diff = (double)Math.round(diff*1000) / 1000;
                            addProductOrderPrice(item.getProduct().id, order.id, item.getPriceExForMinutes() * secondsInDay, entry.priceExOrders);
                            addProductOrderPrice(item.getProduct().id, order.id, item.getPriceIncForMinutes() * secondsInDay, entry.priceIncOrders);
                        }

                        priceInc.put(item.getProduct().id, inc);
                        priceEx.put(item.getProduct().id, ex);
                        if(totalCalc != 0.0 || priceIncOrder.containsKey(order.id)) {
                            priceIncOrder.put(order.id, totalCalc);
                            orderInc.put(order.incrementOrderId, orderPriceInc);                    
                            orderEx.put(order.incrementOrderId, orderPriceEx);
                        }
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
    
    double getTotalExForOrder(long id) {
        double total = 0.0;
        for(PmsOrderStatisticsEntry entry : entries) {
            Double extra = entry.orderEx.get(id);
            if(extra != null) {
                total += extra;
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

    private boolean canUseNewCalculation(CartItem item, PmsOrderStatisticsEntry entry, Calendar cal,Order order) {
        int year = cal.get(Calendar.YEAR);
        if(!item.isMatrixAndItemsValid()) {
            return false;
        }
        if(item.priceMatrix != null && !item.priceMatrix.isEmpty()) {
            return true;
        }
        if(item.itemsAdded != null && !item.itemsAdded.isEmpty()) {
            return true;
        }
        if(year < 2018) {
            return false;
        }
        return false;
    }

    private PmsOrderStatisticsEntry calculatePeriodisatedValues(Order order, CartItem item, Calendar cal) {


        PmsOrderStatisticsEntry entry = new PmsOrderStatisticsEntry();
        String date = PmsBookingRooms.convertOffsetToString(cal.getTime());
        Double price = 0.0;
        Double priceInc = 0.0;
        Double total = item.getTotalAmount();
        
        if(total < 1 && total > -1.0) {
            return entry;
        }
        
        Double taxRate = 1.0;
        if(item.getProduct() == null) {
           System.out.println("Null product on item: " + item);
        } else if(item.getProduct().taxGroupObject == null) {
//           System.out.println("Null tax object on product: " + item.getProduct().name);
        } else {
            taxRate = item.getProduct().taxGroupObject.taxRate;
        }
        if(item.priceMatrix != null) {
            for(String key : item.priceMatrix.keySet()) {
                if(key.equals(date)) {
                    double dayPriceInc = item.priceMatrix.get(key);
                        
                    double dayPriceEx = dayPriceInc / ((100 + taxRate)/100);
                    double existingDayPrice = 0.0;
                    if(entry.priceExRoom.containsKey(item.getProduct().externalReferenceId + "_" + date)) {
                        existingDayPrice = entry.priceExRoom.get(item.getProduct().externalReferenceId + "_" + date);
                    }
                    price += dayPriceEx;
                    priceInc += dayPriceInc;
                    entry.priceExRoom.put(item.getProduct().externalReferenceId + "_" + date, (existingDayPrice+dayPriceEx));
                }
            }
        }
        if(item.itemsAdded != null) {
            for(PmsBookingAddonItem addonItem : item.itemsAdded) {
                if(PmsBookingRooms.isSameDayStatic(addonItem.date, cal.getTime())) {
                    if(addonItem.price != null && addonItem.count != null) {
                        double itemPriceInc = addonItem.price*addonItem.count;
                        price += itemPriceInc / ((100 + taxRate)/100);
                        priceInc += itemPriceInc;
                    }
                }
            }
        }
        
        Double added = entry.priceEx.get(item.getProduct().id);
        if(added != null) {
            price += added;
        }
        String productId = item.getProduct().id;
        entry.priceEx.put(productId, price);
        entry.priceInc.put(productId, priceInc);
        HashMap<String, Double> ordersMap = entry.priceExOrders.get(productId);
        if(ordersMap == null) {
            ordersMap = new HashMap();
        }
        ordersMap.put(order.id, price);
        if(price != 0.0) {
            entry.priceExOrders.put(productId, ordersMap);
        }
        
        HashMap<String, Double> ordersMapInc = entry.priceIncOrders.get(productId);
        if(ordersMapInc == null) {
            ordersMapInc = new HashMap();
        }
        if(priceInc != 0.0) {
            ordersMapInc.put(order.id, priceInc);
        }
        entry.priceIncOrders.put(productId, ordersMapInc);
        
        
        entry.orderEx.put(order.incrementOrderId, price);
        entry.orderInc.put(order.incrementOrderId, priceInc);
        
        return entry;
    }

    private void copy(HashMap<String, Double> original, HashMap<String, Double> toAdd) {
        for(String key : toAdd.keySet()) {
            Double res = 0.0;
            if(original.containsKey(key)) {
                res = original.get(key);
            }
            res += toAdd.get(key);
            original.put(key, res);
        }
    }

    private void copyMap(HashMap<Long, Double> original, HashMap<Long, Double> toAdd) {
        for(Long key : toAdd.keySet()) {
            Double res = 0.0;
            if(original.containsKey(key)) {
                res = original.get(key);
            }
            res += toAdd.get(key);
            original.put(key, res);
        }
    }

    private void copyDoubleMap(HashMap<String, HashMap<String, Double>> original, HashMap<String, HashMap<String, Double>> toAdd) {
        for(String productId : toAdd.keySet()) {
            HashMap<String, Double> productMapOriginal = original.get(productId);
            if(productMapOriginal == null) {
                productMapOriginal = new HashMap();
            }
            HashMap<String, Double> productMapToAdd = toAdd.get(productId);
            copy(productMapOriginal, productMapToAdd);
            original.put(productId, productMapOriginal);
        }
        
    }

}
