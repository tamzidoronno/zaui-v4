/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class DeliveryTask extends Task {
    public List<DeliveryOrder> orders = new ArrayList();
    public boolean cage = false;

    @Override
    int getOrderCount() {
        return orders.size();
    }
}
