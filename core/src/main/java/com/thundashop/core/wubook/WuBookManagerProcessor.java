package com.thundashop.core.wubook;

import com.getshop.scope.GetShopSchedulerBase;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerSubBase;
import java.util.List;

public class WuBookManagerProcessor extends GetShopSchedulerBase {

    public WuBookManagerProcessor(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }    
    
    @Override
    public void execute() throws Exception {
        if(!getApi().getStoreManager().isProductMode()) {
            return;
        }

        getApi().getWubookManager().fetchNewBookings(getMultiLevelName());
        getApi().getWubookManager().updateShortAvailability(getMultiLevelName());
    }
    
}
