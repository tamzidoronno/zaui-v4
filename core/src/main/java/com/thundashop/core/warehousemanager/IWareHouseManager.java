/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.warehousemanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IWareHouseManager {
    @Administrator
    public void deleteWareHouse(String id);
    
    @Administrator
    public List<WareHouse> getWareHouses();
    
    @Administrator
    public WareHouse getWareHouse(String wareHouseId);
    
    @Administrator
    public void createWareHouse(String name);
    
    @Administrator
    public void addWareHouseLocation(WareHouseLocation location);
    
    @Administrator
    public void deleteWareHouseLocation(String wareHouseLocationId);
    
    @Administrator
    public WareHouseLocation getWareHouseLocation(String wareHouseLocationId);
    
    @Administrator
    public void adjustStockQuantity(String productId, int quantity, String warehouseId, String comment);
    
    @Administrator
    public List<StockQuantityRow> getStockQuantityRowsForProduct(String productId, int limit);
    
    @Administrator
    public List<StockQuantityRow> getStockQuantityForWareHouseBetweenDate(String warehouseId, Date start, Date end);
    
    @Administrator
    public void setAsDefaultWareHosue(String wareHouseId);
    
    /**
     * Returns the report for a specific warehouse, but if you leave the warehouseid blank 
     * it will return an overview for all warehouses.
     * 
     * @param wareHouseId
     * @param year
     * @param month
     * @return 
     */
    @Administrator
    public MonthStockReport getMonthStockReport(String wareHouseId, int year, int month);
}
