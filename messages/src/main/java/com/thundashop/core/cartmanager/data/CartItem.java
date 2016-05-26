/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.cartmanager.data;

import com.google.gson.Gson;
import com.thundashop.core.productmanager.data.Product;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class CartItem implements Serializable {
    private String cartItemId = UUID.randomUUID().toString();
    private Map<String, String> variations = new HashMap();
    
    private Product product;
    
    private int count = 0;
    public Date startDate;
    public Date endDate;
    public Date newStartDate;
    public Date newEndDate;
    public Date periodeStart = null;

    public CartItem() {
    }
    
    public boolean isSame(String productId, Map<String, String> variations) {
        if (!this.product.id.equals(productId)) {
            return false;
        }
        
        if (variations.size() != this.variations.size()) {
            return false;
        }
        
        for (String varId : this.variations.values()) {
            if (!variations.values().contains(varId)) {
                return false;
            }
        }
        
        return true;
    }
    
    public String getCartItemId() {
        return cartItemId;
    }

    public void increseCounter() {
        this.count++;
    }
    
    public void decreaseCounter() {
        if (this.count > 1) {
            this.count++;
        }
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setVariations(Map<String, String> variations) {
        this.variations = variations;
    }

    public Product getProduct() {
        return product;
    }

    public Map<String, String> getVariations() {
        
        return variations;
    }

    public void doFinalize() {
        product.doFinalize();
    }

    public Date getStartingDate() {
        if(newStartDate != null) {
            return newStartDate;
        }
        return startDate;
    }
    
    public Date getEndingDate() {
        if(newEndDate != null) {
            return newEndDate;
        }
        return endDate;
    }

    public CartItem copy() {
        Gson gson = new Gson();
        String res = gson.toJson(this);
        CartItem newItem = gson.fromJson(res, CartItem.class);
        newItem.cartItemId = UUID.randomUUID().toString();
        return newItem;
    }
}
