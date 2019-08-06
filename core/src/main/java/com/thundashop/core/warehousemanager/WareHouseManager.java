/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.warehousemanager;

import com.getshop.scope.GetShopSession;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class WareHouseManager extends ManagerBase implements IWareHouseManager {
    public HashMap<String, WareHouse> wareHouses = new HashMap();
    public HashMap<String, WareHouseLocation> wareHouseLocations = new HashMap();

    @Autowired
    private ProductManager productManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        data.data.stream()
                .forEach(o -> {
                    if (o instanceof WareHouse) {
                        wareHouses.put(o.id, (WareHouse)o);
                    }
                    if (o instanceof WareHouseLocation) {
                        wareHouseLocations.put(o.id, (WareHouseLocation)o);
                    }
                });
    }
    
    @Override
    public void deleteWareHouse(String id) {
        WareHouse wh = wareHouses.remove(id);
        if (wh != null) {
            deleteObject(wh);
        }
    }

    @Override
    public List<WareHouse> getWareHouses() {
        wareHouses.values().stream().forEach(o -> finalize(o));
        return new ArrayList(wareHouses.values());
    }

    @Override
    public void createWareHouse(String name) {
        WareHouse wareHouse = new WareHouse();
        wareHouse.name = name;
        saveObject(wareHouse);
        wareHouses.put(wareHouse.id, wareHouse);
    }

    @Override
    public void addWareHouseLocation(WareHouseLocation location) {
        saveObject(location);
        wareHouseLocations.put(location.id, location);
    }

    @Override
    public void deleteWareHouseLocation(String wareHouseLocationId) {
        WareHouseLocation location = wareHouseLocations.remove(wareHouseLocationId);
        if (location != null) {
            deleteObject(location);
        }
    }

    private void finalize(WareHouse wareHouse) {
        wareHouse.wareHouseLoccations = wareHouseLocations.values()
                .stream()
                .filter(o -> o.wareHouseId.equals(wareHouse.id))
                .sorted((WareHouseLocation a, WareHouseLocation b) -> {
                    if (a.locationName.equals(b.locationName)) {
                        if (a.row.equals(b.row)) {
                            return a.column.compareTo(b.column);
                        } else {
                            return a.row.compareTo(b.row);
                        }
                    } else {
                        return a.locationName.compareTo(b.locationName);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public WareHouse getWareHouse(String wareHouseId) {
        WareHouse wareHouse = wareHouses.get(wareHouseId);
        
        if (wareHouse != null) {
            finalize(wareHouse);
            return wareHouse;
        }
        
        return null;
    }

    @Override
    public WareHouseLocation getWareHouseLocation(String wareHouseLocationId) {
        return wareHouseLocations.get(wareHouseLocationId);
    }

    @Override
    public void adjustStockQuantity(String productId, int quantity, String warehouseId) {
        WareHouse wareHouse = wareHouses.get(warehouseId);
        createDiff("", productId, (quantity*-1), wareHouse);
    }

    /**
     * Will return true if the system added a new
     * cartitem row.
     * 
     * @param order
     * @return 
     */
    public boolean updateStockQuantity(Order order) {
        if (order == null || order.getCartItems() == null || order.id == null || order.id.isEmpty()) {
            return false;
        }
        
        List<StockQuantityRow> oldStockQuantityRows = getQuantityRowsFromDatabase(order);
        
        Map<String, List<CartItem>> itemsGroupedByProduct = getQuantityRows(order);
        
        List<String> productIds = getProductIdsInDatabaseAndFromOrder(oldStockQuantityRows, itemsGroupedByProduct);
        
        for (WareHouse wareHouse : wareHouses.values()) {
            for (String productId : productIds) {
                int oldCountForOrder = getCountForThisOrder(oldStockQuantityRows, productId, wareHouse);
                int countInOrder = getQuantityForProductInOrder(itemsGroupedByProduct, productId, wareHouse);
                int diff = countInOrder - oldCountForOrder;

                if (diff != 0) {
                    createDiff(order.id, productId, diff, wareHouse);
                }
            }
        }
        
        return false;
        
    }
    
    private List<StockQuantityRow> getQuantityRowsFromDatabase(Order order) {
        BasicDBObject query = new BasicDBObject();
        query.put("className", "com.thundashop.core.warehousemanager.StockQuantityRow");
        query.put("orderId", order.id);
    
        List<StockQuantityRow> quantityRowsThisOrder = database.query("WareHouseManager", storeId, query)
                    .stream()
                    .map(o -> (StockQuantityRow)o)
                    .collect(Collectors.toList());
        
        return quantityRowsThisOrder;
    }

    private Map<String, List<CartItem>> getQuantityRows(Order order) {
        return order.getCartItems()
                .stream()
                .collect(Collectors.groupingBy(item -> item.getProductId()));
    }

    private List<String> getProductIdsInDatabaseAndFromOrder(List<StockQuantityRow> quantityRowsThisOrder, Map<String, List<CartItem>> itemsGroupedByProduct) {
        List<String> productIds = quantityRowsThisOrder.stream()
                .map(o -> o.productId)
                .collect(Collectors.toList());
        
        productIds.addAll(itemsGroupedByProduct.keySet());
        
        productIds = productIds.stream().distinct().collect(Collectors.toList());
        
        return productIds;
    }

    private int getCountForThisOrder(List<StockQuantityRow> quantityRowsThisOrder, String productId, WareHouse wareHouse) {
        return quantityRowsThisOrder.stream()
                    .filter(o -> o.productId.equals(productId))
                    .filter(o -> o.warehouseId.equals(wareHouse.id) || (o.warehouseId.isEmpty() && wareHouse.isDefault))
                    .mapToInt(o -> o.count)
                    .sum();
    }

    private int getQuantityForProductInOrder(Map<String, List<CartItem>> itemsGroupedByProduct, String productId, WareHouse wareHouse) {
        int countInOrder = 0;
        
        if (itemsGroupedByProduct.get(productId) != null) {
            countInOrder = itemsGroupedByProduct.get(productId)
                .stream()
                .filter(o -> o.wareHouseId.equals(wareHouse.id) || (o.wareHouseId.isEmpty() && wareHouse.isDefault))
                .mapToInt(o -> o.getCount())
                .sum();    
        }

        return countInOrder;
    }

    private void createDiff(String orderId, String productId, int diff, WareHouse warehouse) {
        StockQuantityRow row = new StockQuantityRow();
        row.orderId = orderId;
        row.count = diff;
        row.productId = productId;
        row.warehouseId = warehouse.id;
        row.stockUnitValue = productManager.getProduct(productId).stockValue;
        saveObject(row);
        
        productManager.changeStockQuantity(productId, (diff*-1));
        productManager.changeStockQuantityForWareHouse(productId, (diff*-1), warehouse.id);
    }

    @Override
    public List<StockQuantityRow> getStockQuantityRowsForProduct(String productId, int limit) {
        BasicDBObject query = new BasicDBObject();
        query.put("className", "com.thundashop.core.warehousemanager.StockQuantityRow");
        query.put("productId", productId);
        
        DBCollection collection = database.getCollection("WareHouseManager", storeId);
        DBCursor cur = collection
                .find(query)
                .sort(new BasicDBObject("rowCreatedDate", -1))
                .limit(limit);
        
        ArrayList<StockQuantityRow> retList = new ArrayList();
        
        while (cur.hasNext()) {
            DataCommon dataCommon = database.convert(cur.next());
            retList.add((StockQuantityRow)dataCommon);
        }

        return retList;
    }

    @Override
    public void setAsDefaultWareHosue(String wareHouseId) {
        WareHouse wareHouse = getWareHouse(wareHouseId);
        
        wareHouses.values()
                .stream()
                .forEach(o -> {
                    o.isDefault = false;
                    saveObject(o);
                });
        
        wareHouse.isDefault = true;
        saveObject(wareHouse);
    }

    @Override
    public MonthStockReport getMonthStockReport(String wareHouseId, int year, int month) {
        int javaMonth = month - 1;
        Date start = getFirstDateOfMonth(year, javaMonth);
        Date end = getFirstDayOfNextMonth(year, javaMonth);
        
        MonthStockReport report = new MonthStockReport();
        
        for (Product product : productManager.getAllProducts()) {
            List<StockQuantityRow> stockRecordsForProduct = getStockQuantityRowsForProduct(product.id, Integer.MAX_VALUE);
            List<StockQuantityRow> ingoingRecords = getRecordsBetween(new Date(0), start, stockRecordsForProduct);
            List<StockQuantityRow> changesRecords = getRecordsBetween(start, end, stockRecordsForProduct);
            
            ProductStockReport productStockReport = new ProductStockReport();
            productStockReport.calculateValues(ingoingRecords, changesRecords);
            productStockReport.productId = product.id;
            
            report.stockReports.add(productStockReport);
        }
        
        System.out.println("Get it between " + start + " " + end);
        return report;
    }

    private Date getFirstDateOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getFirstDayOfNextMonth(int year, int javaMonth) {
        int nextYear = year;
        int nextMonth = javaMonth + 1;
        
        if (nextMonth >= 12) {
            nextYear++;
            nextMonth = 0;
        }
        
        Date end = getFirstDateOfMonth(nextYear, nextMonth);
        return end;
    }

    private List<StockQuantityRow> getRecordsBetween(Date start, Date end, List<StockQuantityRow> stockRecordsForProduct) {
        return stockRecordsForProduct.stream()
                .filter(o -> {
                    long startTime = start.getTime();
                    long endTime = end.getTime();
                    long recordTime = o.rowCreatedDate.getTime();
                    return startTime <= recordTime && recordTime < endTime;
                })
                .collect(Collectors.toList());
    }

}