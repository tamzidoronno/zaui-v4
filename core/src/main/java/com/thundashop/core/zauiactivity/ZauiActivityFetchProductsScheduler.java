package com.thundashop.core.zauiactivity;
import com.getshop.scope.GetShopSchedulerBase;
import com.thundashop.core.common.GetShopLogHandler;
import org.springframework.beans.factory.annotation.Autowired;

public class ZauiActivityFetchProductsScheduler extends GetShopSchedulerBase {
    public ZauiActivityFetchProductsScheduler(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }

    @Override
    public void execute() throws Exception {
        String storeId = getApi().getStoreManager().getStoreId();
        String multiLevelName = getMultiLevelName();
        try {
            getApi().getZauiActivityManager().fetchZauiActivities(multiLevelName);
        }catch(Exception e) {
            GetShopLogHandler.logPrintStatic("Failed to handle zaui activity fetch, " + e.getMessage() + " multiLevelName: " + multiLevelName, storeId);
            GetShopLogHandler.logStack(e, storeId);
        }
    }

}
