package com.thundashop.core.channelmanagerexperiment.wubook;

import com.getshop.scope.GetShopSchedulerBase;
import com.thundashop.core.common.GetShopLogHandler;

public class WuBookManagerProcessor extends GetShopSchedulerBase {

    public WuBookManagerProcessor(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }    
    
    @Override
    public void execute() throws Exception {
        if(!getApi().getStoreManager().isProductMode()) {
            return;
        }
        
        String storeId = getApi().getStoreManager().getStoreId();
        
        String multilevelname = getMultiLevelName();
        try {
            getApi().getWubookManager().fetchNewBookings(multilevelname);
            getApi().getWubookManager().updateShortAvailability(multilevelname);
        }catch(Exception e) {
            GetShopLogHandler.logPrintStatic("Failed to handle wubook api call, " + e.getMessage() + " multilevelname: " + multilevelname, storeId);
            GetShopLogHandler.logStack(e, storeId);
        }
    }
    
}
