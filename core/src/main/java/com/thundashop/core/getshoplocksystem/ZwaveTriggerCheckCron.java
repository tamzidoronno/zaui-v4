/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import com.getshop.scope.GetShopSchedulerBase;

/**
 *
 * @author ktonder
 */
public class ZwaveTriggerCheckCron extends GetShopSchedulerBase {

    @Override
    public void execute() throws Exception {
        getApi().getGetShopLockSystemManager().triggerCronTab();
    }
    
}
