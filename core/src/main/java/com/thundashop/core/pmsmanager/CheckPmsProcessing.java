/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSchedulerBase;

/**
 *
 * @author boggi
 */
public class CheckPmsProcessing extends GetShopSchedulerBase {

    public CheckPmsProcessing(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }

    @Override
    public void execute() throws Exception {
        
        String name = getMultiLevelName();
        String storeId = getStoreId();
        
        if(storeId != null && storeId.equals("87cdfab5-db67-4716-bef8-fcd1f55b770b") && !name.equals("default")) {
            return;
        }
        getApi().getPmsManager().processor(name);
    }
    
}
