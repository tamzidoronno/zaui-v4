/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.amesto;

import com.getshop.javaapi.APIAmestoManager;
import com.getshop.scope.GetShopSchedulerBase;

/**
 *
 * @author hung
 */
public class AmestoSync extends GetShopSchedulerBase {
    
    @Override
    public void execute() throws Exception {
        
        String hostname = getApi().getStoreApplicationPool().getApplication("66b4483d-3384-42bb-9058-2ac915c77d80").getSetting("hostname");
        
        APIAmestoManager amestoManager = getApi().getAmestoManager();
        
        amestoManager.syncAllCostumers(hostname);
        amestoManager.syncAllStockQuantity(hostname);
        amestoManager.syncAllOrders(hostname);
    }
}
