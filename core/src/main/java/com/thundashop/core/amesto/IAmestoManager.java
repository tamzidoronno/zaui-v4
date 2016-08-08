/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.amesto;

import com.getshop.scope.GetShopSchedulerBase;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;

/**
 *
 * @author hung
 */
@GetShopApi
public interface IAmestoManager {
    
    @Editor
    public void syncAllStockQuantity(String hostname) throws ErrorException;
    
    @Editor
    public void syncAllOrders(String hostname) throws ErrorException;
    
    @Editor
    public void syncAllCostumers(String hostname) throws ErrorException;
}
