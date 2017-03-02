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
public class PickupTask extends Task {
    public List<PickupOrder> orders = new ArrayList();
    public boolean cage;
    boolean barcodeValidated = false;
    
    @Override
    public int getOrderCount() {
        return orders.size();
    }

    @Override
    void setPodBarcodeStringToTasks() {
        podBarcode = orders.stream().map(order -> order.podBarcode).distinct().collect(Collectors.joining(","));
    }

    public void changeCountedCopies(String orderReference, int quantity, int containers) {
        for (PickupOrder order : orders) {
            if (order.referenceNumber.trim().equals(orderReference.trim())) {
                order.countedBundles = quantity;
                order.countedContainers = containers;
            }
        }
    }

    void setOrderException(String orderReference, String exceptionId) {
        for (PickupOrder order : orders) {
            if (order.referenceNumber.trim().equals(orderReference.trim())) {
                order.exceptionId = exceptionId;
            }
        }
    }

    PickupOrder getOrder(String orderReference) {
        for (PickupOrder order : orders) {
            if (order.referenceNumber.trim().equals(orderReference.trim())) {
                return order;
            }
        }
        
        return null;
    }
}