/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.productmanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.LightWeight;
import com.thundashop.core.common.SmartDataMap;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductLight;

/**
 *
 * @author ktonder
 */
public class ProductMap extends SmartDataMap<String, Product, ProductLight>{

    public ProductMap(Database database, String storeId, Class manager) {
        super(database, storeId, manager, Product.class);
    }

    @Override
    public int getVersionNumber() {
        return 1;
    }
    
}
