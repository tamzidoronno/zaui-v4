/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager;

import com.thundashop.core.common.SmartDataMap;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.OrderLight;

/**
 *
 * @author ktonder
 */
public class OrderMap extends SmartDataMap<String, Order, OrderLight> {

    public OrderMap(Database database, String storeId, Class manager) {
        super(database, storeId, manager, Order.class);
    }

    @Override
    public int getVersionNumber() {
        return 1;
    }
    
}
