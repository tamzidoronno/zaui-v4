/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager;

import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.OrderFilter;
import com.thundashop.core.ordermanager.data.OrderResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author boggi
 */
public class OrderFiltering {
    List<Order> ordersToFilter = new ArrayList();

    void setOrders(List<Order> orders) {
        this.ordersToFilter = new ArrayList(orders);
    }
    List<Order> filterOrders(OrderFilter filter) {
        List<Order> toRemove = new ArrayList();
        filterByDate(filter, toRemove);
        filterByStatus(filter);
        filterByPaymentMethod(filter);
        filterByUsers(filter);
        return ordersToFilter;
    }

    private void filterByPaymentMethod(OrderFilter filter) {
        List<Order> toRemove;
        //Filter by payment method.
        String method = filter.paymentMethod;
        if(method == null) { method = ""; }
        method = method.replaceAll("-", "_");
        if(!method.isEmpty()) {
            toRemove = new ArrayList();
            for(Order order : ordersToFilter) {
                if(order.payment != null && order.payment.paymentType != null && !order.payment.paymentType.contains(method)) {
                    toRemove.add(order);
                }
            }
            ordersToFilter.removeAll(toRemove);
        }
    }

    private void filterByStatus(OrderFilter filter) {
        List<Order> toRemove;
        //Filter by status
        toRemove = new ArrayList();
        for(Order order : ordersToFilter) {
            if(filter.state == null || filter.state == 0) {
                continue;
            }
            if(order.status != filter.state) {
                toRemove.add(order);
            }
        }
        ordersToFilter.removeAll(toRemove);
    }

    private void filterByDate(OrderFilter filter, List<Order> toRemove) {
        //Filter by date.
        for(Order order : ordersToFilter) {
            if(filter.type.equals("createddate")) {
                if(inDate(order.rowCreatedDate, filter.start, filter.end)) {
                    continue;
                }
            } else if(filter.type.equals("paymentdate")) {
                if(inDate(order.paymentDate, filter.start, filter.end)) {
                    continue;
                }
            } else if(filter.type.equals("duedate")) {
                if(inDate(order.getDueDate(), filter.start, filter.end)) {
                    continue;
                }
            } else if(filter.type.equals("whenordered")) {
                if(inDate(order.shippingDate, filter.start, filter.end)) {
                    continue;
                }
            } else if(filter.type.equals("checkindate")) {
                if(inDate(order.getStartDateByItems(), filter.start, filter.end)) {
                    continue;
                }
            } else if(filter.type.equals("checkoutdate")) {
                if(inDate(order.getEndDateByItems(), filter.start, filter.end)) {
                    continue;
                }
            }
            toRemove.add(order);
        }
        ordersToFilter.removeAll(toRemove);
    }

    private boolean inDate(Date toCheck, Date start, Date end) {
        if(toCheck == null) {
            return false;
        }
        if(toCheck.before(start)) {
            return false;
        }
        if(toCheck.after(end)) {
            return false;
        }
        return true;
    }

    public OrderResult sum(List<OrderResult> orderFilterResult) {
        OrderResult res = new OrderResult();
        for(OrderResult r : orderFilterResult) {
            res.amountExTaxes += r.amountExTaxes;
            res.restAmount += r.restAmount;
            res.amountIncTaxes += r.amountIncTaxes;
            res.amountPaid += r.amountPaid;
        }
        return res;
    }

    private void filterByUsers(OrderFilter filter) {
        if(filter.customer == null || filter.customer.isEmpty()) {
            return;
        }
        List<Order> remove = new ArrayList();
        for(Order ord : ordersToFilter) {
            if(!filter.customer.contains(ord.userId)) {
                remove.add(ord);
            }
        }
        ordersToFilter.removeAll(remove);
    }
    
}
