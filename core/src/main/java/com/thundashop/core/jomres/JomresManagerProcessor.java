package com.thundashop.core.jomres;

import com.getshop.javaapi.APIYouTubeManager;
import com.getshop.scope.GetShopSchedulerBase;
import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.GetShopLogHandler;
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

            APIYouTubeManager youTubeManager = getApi().getYouTubeManager();
            youTubeManager.searchYoutube(multilevelname);
            jomresManager.fetchBookings();
//                    .searchYoutube(multilevelname);
//            getApi().getWubookManager().updateShortAvailability(multilevelname);
        }catch(Exception e) {
            GetShopLogHandler.logPrintStatic("Failed to handle jomres api call, " + e.getMessage() + " multilevelname: " + multilevelname, storeId);
            GetShopLogHandler.logStack(e, storeId);
        }
    }

}
