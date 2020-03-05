/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pos;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author ktonder
 */
public class PosTab extends DataCommon {
    public String name = "";
    public String createdByUserId = "";
    public String cashPointId = "";
    public List<CartItem> cartItems = new ArrayList();
    public double cashWithDrawal;
    public int incrementalTabId = 0;
    public int printedToKitchenTimes = 0;
    public String tabTaxGroupId;
    public Double discount;
    public String createdBySessionId = "";

    
    void removeCartItem(CartItem cartItem) {
        CartItem oldCartItem = cartItems.stream()
                .filter(o -> o.getCartItemId().equals(cartItem.getCartItemId()))
                .findFirst()
                .orElse(null);
        
        if (oldCartItem == null) {
            return;
        }
        
        oldCartItem.remove(cartItem);
     
        clearEmptyLines();
    }
    
    private void clearEmptyLines() {
        cartItems.removeIf(item -> item.getTotalAmount() == 0);
    }

    public CartItem getCartItem(String cartItemId) {
        return cartItems.stream()
                .filter(o -> o.getCartItemId().equals(cartItemId))
                .findAny()
                .orElse(null);
    }

    Double getTotalAmount() {
        Double total = 0.0;
        for(CartItem item : cartItems) {
            total += item.getTotalAmount();
        }
        return total;
    }
}
