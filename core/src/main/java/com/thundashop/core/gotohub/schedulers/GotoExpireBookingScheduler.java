package com.thundashop.core.gotohub.schedulers;

import com.getshop.scope.GetShopSchedulerBase;
import com.thundashop.core.common.GetShopLogHandler;

public class GotoExpireBookingScheduler  extends GetShopSchedulerBase {
    public GotoExpireBookingScheduler(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }

    @Override
    public void execute() throws Exception {
        String storeId = getApi().getStoreManager().getStoreId();
        String multilevelname = getMultiLevelName();
        try {
            getApi().getGoToManager().cancelUnpaidBookings(multilevelname);
        }catch(Exception e) {
            GetShopLogHandler.logPrintStatic("Failed to handle jomres api call, " + e.getMessage() + " multilevelname: " + multilevelname, storeId);
            GetShopLogHandler.logStack(e, storeId);
        }
    }
}