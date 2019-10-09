/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.external;

import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.TaxGroup;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class ExternalPosProduct implements Serializable {
    public String id;
    public String productName;
    public Double defaultPrice;
    public List<TaxGroup> allowedTaxGroups = new ArrayList();
    
    public void makeExternalPosProduct(Product product) {
        id = product.id;
        productName = product.name;
        defaultPrice = product.price;
        allowedTaxGroups.add(product.taxGroupObject);
        allowedTaxGroups.addAll(product.additionalTaxGroupObjects);
    }
}
