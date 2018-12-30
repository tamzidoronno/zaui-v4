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
public enum ProductPriceOverrideType {
    /**
     * Will change the end price of the product when creating
     * the order to the price given in newValue
     */
    fixedprice,
    
    /**
     * Will apply the discount accordingly
     * the newValue is in percent.
     */
    discountpercent
}
