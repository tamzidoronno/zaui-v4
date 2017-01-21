/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author ktonder
 */
public class DeliveryTask extends Task {
    public List<DeliveryOrder> orders = new ArrayList();

    public int containerCounted;
    
    @Override
    public int getOrderCount() {
        return orders.size();
    }

    public void changeQuantity(String orderReference, int quantity) {
        for (DeliveryOrder order : orders) {
            if (order.referenceNumber.equals(orderReference)) {
                order.quantity = quantity;
            }
        }
    }
    
    public void changeDriverQuantity(String orderReference, int quantity) {
        for (DeliveryOrder order : orders) {
            if (order.referenceNumber.equals(orderReference)) {
                order.driverDeliveryCopiesCounted = quantity;
            }
        }
    }

    @Override
    void setPodBarcodeStringToTasks() {
        podBarcode = orders.stream().map(order -> order.podBarcode).distinct().collect(Collectors.joining(","));
    }

}
