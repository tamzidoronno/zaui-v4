/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplock;

import com.getshop.scope.GetShopSchedulerBase;

/**
 *
 * @author boggi
 */
public class CheckAllOkGetShopLocks extends GetShopSchedulerBase  {

    public CheckAllOkGetShopLocks(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }
    
    @Override
    public void execute() throws Exception {
        getApi().getGetShopLockManager().checkIfAllIsOk(getMultiLevelName());
    }
    
}
