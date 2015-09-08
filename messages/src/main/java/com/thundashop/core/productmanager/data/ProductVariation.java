/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.productmanager.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class ProductVariation implements Serializable {
    public List<ProductVariation> children = new ArrayList();
    public double priceDifference = 0;
    public String id = UUID.randomUUID().toString();
    public String title = "";

    public ProductVariation get(String variationId) {
        if (id.equals(variationId)) {
            return this;
        }
        
        for (ProductVariation child : children) {
            ProductVariation ichild = child.get(variationId);
            if (ichild != null) {
                return ichild;
            }
        }
        
        return null;
    }
}
