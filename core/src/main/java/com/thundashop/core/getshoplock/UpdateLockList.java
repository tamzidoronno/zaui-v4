package com.thundashop.core.getshoplock;

import com.getshop.scope.GetShopSchedulerBase;

/**
 *
 * @author boggi
 */
public class UpdateLockList extends GetShopSchedulerBase {

    public UpdateLockList(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }
        
    @Override
    public void execute() throws Exception {
//        getApi().getGetShopLockManager().getAllLocks(getMultiLevelName());
    }
}

