package com.thundashop.core.jomres;

import com.getshop.scope.GetShopSchedulerBase;
import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerSubBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class JomresManagerProcessor extends GetShopSchedulerBase {
    @Autowired
    JomresManager jomresManager;

    public JomresManagerProcessor(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
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
            jomresManager.updateAvailability();
            jomresManager.fetchBookings();
            getApi().getWubookManager().fetchNewBookings(multilevelname);
//            getApi().getWubookManager().updateShortAvailability(multilevelname);
        }catch(Exception e) {
            GetShopLogHandler.logPrintStatic("Failed to handle wubook api call, " + e.getMessage() + " multilevelname: " + multilevelname, storeId);
            GetShopLogHandler.logStack(e, storeId);
        }
    }

}
