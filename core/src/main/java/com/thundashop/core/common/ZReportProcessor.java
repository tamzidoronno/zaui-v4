package com.thundashop.core.common;

import com.getshop.scope.GetShopSchedulerBase;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import com.thundashop.core.utils.StringUtils;
import com.thundashop.core.utils.DateUtils;

public class ZReportProcessor extends GetShopSchedulerBase {

    public ZReportProcessor(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }

    @Override
    public void execute() throws Exception {

        String storeId = getApi().getStoreManager().getStoreId();
        String multilevelname = getMultiLevelName();

        try {
            PmsConfiguration pmsConfiguration = getApi().getPmsManager().getConfiguration(multilevelname);
            String scheduledTimeForZReport = pmsConfiguration.zReportProcessingTime;
            System.out.println("scheduledTimeForZReport: "+scheduledTimeForZReport);
            if (StringUtils.nonNull(scheduledTimeForZReport) && scheduledTimeForZReport.equals(DateUtils.getCurrentTime())) {
                getApi().getPosManager().createZReport("");
            }
        } catch (Exception e) {
            GetShopLogHandler.logPrintStatic("Failed to handle zreport scheduler, " + e.getMessage() + " multilevelname: " + multilevelname, storeId);
            GetShopLogHandler.logStack(e, storeId);
        }
    }
}
