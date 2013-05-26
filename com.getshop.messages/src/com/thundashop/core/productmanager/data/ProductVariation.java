/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.productmanager.data;

import java.io.Serializable;

/**
 *
 * @author ktonder
 */
public class ProductVariation implements Serializable {
    public String attributeName = "";
    public String attributeValue = "";
    public double priceDifference = 0;
    public String description = "";
}
