package com.thundashop.core.ordermanager;

import com.getshop.scope.GetShopSchedulerBase;

public class CheckOrdersNotCaptured extends GetShopSchedulerBase {

    public CheckOrdersNotCaptured(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }
    
    @Override
    public void execute() throws Exception {
        getApi().getOrderManager().checkForOrdersFailedCollecting();
    }
    
}
