/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionObject;
import com.mongodb.BasicDBObject;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.OrderLight;
import com.thundashop.core.ordermanager.data.Payment;
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
//        
        return  orders.get(orderId);
    }

    public OrderLight orderSaved(Order order, OrderLight lightOrder) {
        if (lightOrder == null) {
            OrderLight light = new OrderLight(order);
            light.storeId = storeId;
            database.save("OrderManager", "col_"+storeId, light);
            return light;
        } else {
            lightOrder.update(order);
            database.save("OrderManager", "col_"+storeId, lightOrder);
            return lightOrder;
        }
    }

  
}
