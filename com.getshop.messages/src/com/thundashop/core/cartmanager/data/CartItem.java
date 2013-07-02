/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.cartmanager.data;

import com.thundashop.core.productmanager.data.Product;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class CartItem implements Serializable {
    private String cartItemId = UUID.randomUUID().toString();
    private List<String> variations = new ArrayList();
    
    private Product product;
    
    private int count = 0;

    CartItem() {
    }
    
    public boolean isSame(String productId, List<String> variations) {
        if (!this.product.id.equals(productId)) {
            return false;
        }
        
        if (variations.size() != this.variations.size()) {
            return false;
        }
        
        for (String varId : this.variations) {
            if (!variations.contains(varId)) {
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

    public void setVariations(List<String> variations) {
        this.variations = variations;
    }

    public Double getPrice() {
        return product.getPrice(variations) * count;
    }

    public Product getProduct() {
        return product;
    }

    public List<String> getVariations() {
        return variations;
    }
}
