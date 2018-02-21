/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager.data;

import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.DataCommon;
import java.util.HashMap;

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
    
    /**
     * Key = pms roomid.
     */
    public HashMap<String, Cart> carts = new HashMap();
    
    public Order order;

    public void finalizeOrder() {
        if (order.cart == null) {
            order.cart = new Cart();
        }
        
        order.cart.clear();
        
        carts.values().stream()
                .forEach(cart -> {
                    order.cart.address = cart.address;
                    order.cart.addCartItems(cart.getItems());
                    order.cart.references.add(cart.reference);
                });      
        
    }

    public void updateCartItems(Cart cart) {
        for (CartItem item : cart.getItems()) {
            for (Cart iCart : carts.values()){
                CartItem existingItem = iCart.getCartItem(item.getCartItemId());
                if (existingItem != null) {
                    iCart.replaceItem(item);
                }
            }
        }
    }
}
