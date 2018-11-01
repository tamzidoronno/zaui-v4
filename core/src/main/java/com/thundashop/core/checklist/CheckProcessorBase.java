/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.checklist;

import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsManager;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author ktonder
 */
public abstract class CheckProcessorBase {

    private final OrderManager orderManager;
    private final PmsManager pmsManager;
    
    public CheckProcessorBase(OrderManager orderManager, PmsManager pmsManager) {
        this.orderManager = orderManager;
        this.pmsManager = pmsManager;
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }

    public PmsManager getPmsManager() {
        return pmsManager;
    }
    
    public List<Order> getOrders(PmsBooking booking) {
        List<Order> orders = booking.orderIds.stream()
                .map(orderId -> getOrderManager().getOrder(orderId))
                .collect(Collectors.toList());
        return orders;
    }
    
}
