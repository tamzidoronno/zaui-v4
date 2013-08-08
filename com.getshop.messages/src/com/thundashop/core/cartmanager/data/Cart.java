/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.cartmanager.data;

import com.google.gson.Gson;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ExchangeConvert;
import com.thundashop.core.common.Setting;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class Cart extends DataCommon {
    private List<CartItem> items = new ArrayList();
    private double shippingCost = 0;
    
    public Address address;
    
    private CartItem getCartItem(String productId, List<String> variations) {
        for (CartItem cartItem : items) {
            if (cartItem.isSame(productId, variations)) {
                return cartItem;
            }
        }
        return null;
    }
    
    public CartItem getCartItem(String cartItemId) {
        for (CartItem cartItem : items) {
            if (cartItem.getCartItemId().equals(cartItemId)) {
                return cartItem;
            }
        }
        
        return null;
    }
    
    public void addProduct(Product product, List<String> variations) {
        CartItem cartItem = getCartItem(product.id, variations);
        
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setVariations(variations);
            cartItem.setCount(0);
            items.add(cartItem);
        }
        
        cartItem.increseCounter();
    }
    
    public void removeProduct(String cartItemId) {
        CartItem cartItem = getCartItem(cartItemId);
        items.remove(cartItem);
    }

    public int getProductCount(String cartItemId) {
        CartItem cartItem = getCartItem(cartItemId);
        if (cartItem != null) {
            return cartItem.getCount();
        }
        
        return 0;
    }

    public void setProductCount(String cartItemId, int count) {
        CartItem cartItem = getCartItem(cartItemId);
        cartItem.setCount(count);
    }
   
    public void clear() {
        shippingCost = 0;
        items.clear();
    }

    public Double getTotal() {
        Double total = 0D;
        for (CartItem cartItem : items) {
            total += cartItem.getProduct().price * cartItem.getCount();
        }
        return total;
    }
    
    public List<CartItem> getItems() {
        return items;
    }

    @Override
    public Cart clone() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        Cart copied = gson.fromJson(json, Cart.class);
        return copied;
    }

    public void setShippingCost(double shippingCost) throws ErrorException {
        this.shippingCost = shippingCost;
    }

    public double getShippingCost() {
        return shippingCost;
    }
      
}
