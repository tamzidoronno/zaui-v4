package com.thundashop.core.jomres;

import com.getshop.scope.GetShopSchedulerBase;
import com.thundashop.core.common.GetShopLogHandler;

public class JomresManagerProcessor extends GetShopSchedulerBase {
    public JomresManagerProcessor(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }

    @Override
    public void execute() throws Exception {
//        if(!getApi().getStoreManager().isProductMode()) {
//            return;
//        }

        String storeId = getApi().getStoreManager().getStoreId();

        String multilevelname = getMultiLevelName();
        try {
            getApi().getJomresManager().fetchBookings(multilevelname);
            getApi().getJomresManager().updateAvailability(multilevelname);
        }catch(Exception e) {
            GetShopLogHandler.logPrintStatic("Failed to handle jomres api call, " + e.getMessage() + " multilevelname: " + multilevelname, storeId);
            GetShopLogHandler.logStack(e, storeId);
        }
    }
}
