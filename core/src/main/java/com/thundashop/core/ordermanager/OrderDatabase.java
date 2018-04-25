/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionObject;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.ordermanager.data.Order;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class OrderDatabase extends ManagerBase {
    
    @Autowired
    private Database database;
    
    private HashMap<String, Order> orders = new HashMap();
    
    public Order getOrder(String orderId) {
        
        if (orders.get(orderId) == null) {
            Order order = database.get(orderId, OrderManager.class, storeId, Order.class);
            if (order != null) {
                orders.put(order.id, order);
            }
        }
        
        return orders.get(orderId);
    }
}
