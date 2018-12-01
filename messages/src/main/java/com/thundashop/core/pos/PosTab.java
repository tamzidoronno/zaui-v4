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

    
    void removeCartItem(CartItem cartItem) {
        for (CartItem item : cartItems) {
            if (item.getCartItemId().equals(cartItem.getCartItemId())) {
                double diff = item.getProduct().price - cartItem.getProduct().price;
                for (int i=0; i<cartItem.getCount(); i++) {
                    item.getProduct().price = diff;
                }
            }
        }
        
        clearEmptyLines();
    }
    
    private void clearEmptyLines() {
        cartItems.removeIf(item -> item.getTotalAmount() == 0);
    }
 
}
