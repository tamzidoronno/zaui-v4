/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.google.gson.Gson;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class CartCompositeKey implements Serializable {
    private String productId;
    private List<String> variations;

    public CartCompositeKey(String productId, List<String> variations) {
        this.productId = productId;
        this.variations = variations;
    }

    @Override
    public int hashCode() {
        java.util.Collections.sort(variations);
        String total = productId;
        
        for (String var : variations) {
             total = total + var;
        }
        
        return total.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }
 
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public List<String> getVariations() {
        return variations;
    }
}
