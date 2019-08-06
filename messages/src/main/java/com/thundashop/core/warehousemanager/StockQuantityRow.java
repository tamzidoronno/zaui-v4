/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.warehousemanager;

import com.thundashop.core.common.DataCommon;

/**
 *
 * @author ktonder
 */
public class StockQuantityRow extends DataCommon {
    public String productId = "";
    public String orderId = "";
    public double stockUnitValue = 0D;
    public String warehouseId = "";
    
    /**
     * Number of unit sold.
     */
    public int count;
    public boolean isAdjustment = false;
    
}
