/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.productmanager.data;

import java.io.Serializable;

/**
 *
 * @author ktonder
 */
public class ProductDynamicPrice implements Serializable {

    public ProductDynamicPrice() {
    }

    
    public ProductDynamicPrice(int id) {
        this.id = id;
    }
    
    public int id;
    public int from;
    public int to; 
    public double price;
}
