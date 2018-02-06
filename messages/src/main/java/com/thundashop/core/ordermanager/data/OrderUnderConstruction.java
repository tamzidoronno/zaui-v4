/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager.data;

import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.common.DataCommon;

/**
 * This is a special order that is under construction. 
 * 
 * Example, a booking needs a temporary place
 * to add all cartItems etc. When user is done
 * manipulating the order, this order will be 
 * transferred from a ConstructionOrder to a 
 * normal order.
 * 
 * @author ktonder
 */
public class OrderUnderConstruction extends DataCommon {
    public Order order;

    public void finalizeOrder() {
        if (order.cart == null) {
            order.cart = new Cart();
        }
    }
}
