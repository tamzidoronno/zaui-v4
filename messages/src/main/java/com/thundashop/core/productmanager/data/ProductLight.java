/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.productmanager.data;

/**
 *
 * @author ktonder
 */
public class ProductLight {
    public String id;
    public String name;
    public double priceIncTaxes;
    public double priceExTaxes;

    public ProductLight(Product product) {
        id = product.id;
        name = product.name;
        priceIncTaxes = product.price;
        priceExTaxes = product.priceExTaxes;
    }
}
