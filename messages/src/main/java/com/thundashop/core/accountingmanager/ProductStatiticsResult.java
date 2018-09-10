/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.accountingmanager;

import com.thundashop.core.ordermanager.data.Order;
import java.util.HashMap;

/**
 *
 * @author boggi
 */
public class ProductStatiticsResult {
    public String productId = "";
    public HashMap<String, Double> ordervalues = new HashMap();
    public Double totalValue = 0.0;

    public void addOrder(Order ord) {
        
    }
}
