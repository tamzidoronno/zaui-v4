/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.productmanager.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class ProductVariation implements Serializable {
    public List<ProductVariation> children = new ArrayList();
    public double priceDifference = 0;
    public String title = "";
}
