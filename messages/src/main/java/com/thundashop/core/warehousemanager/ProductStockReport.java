/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.warehousemanager;

import java.util.List;

/**
 *
 * @author ktonder
 */
public class ProductStockReport {
    public String productId;
    public int ingoingUnitCount;
    public int outgoingUnitCount;
    public int changesUnitCount;
    
    public double ingoingStockValue;
    public double outgoingStockValue;
    public double changesStockValue;

    public void calculateValues(List<StockQuantityRow> ingoingRecords, List<StockQuantityRow> changesRecords) {
        ingoingUnitCount = ingoingRecords.stream()
                .mapToInt(o -> o.count * -1)
                .sum();
        
        ingoingStockValue = ingoingRecords.stream()
                .mapToDouble(o -> o.stockUnitValue * o.count * -1)
                .sum();
        
        
        changesUnitCount = changesRecords.stream()
                .mapToInt(o -> o.count * -1)
                .sum();
        
        changesStockValue = changesRecords.stream()
                .mapToDouble(o -> o.stockUnitValue * o.count * -1)
                .sum();
        
        
        outgoingUnitCount = ingoingUnitCount + changesUnitCount;
        outgoingStockValue = ingoingStockValue + changesStockValue;
    }
}
