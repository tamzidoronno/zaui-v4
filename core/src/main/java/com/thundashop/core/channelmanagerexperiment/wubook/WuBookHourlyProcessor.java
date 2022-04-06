/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.channelmanagerexperiment.wubook;

import com.getshop.scope.GetShopSchedulerBase;
import com.thundashop.core.common.GetShopLogHandler;

import java.util.Random;

/**
 *
 * @author boggi
 */
public class WuBookHourlyProcessor extends GetShopSchedulerBase {
      
    public WuBookHourlyProcessor(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    } 

    @Override
    public void execute() throws Exception {
        if(!getApi().getStoreManager().isProductMode()) {
            return;
        }
        try {
            Random rn = new Random();
            int min = 0;
            int max = 50000;
            int res = rn.nextInt(max - min + 1) + min;
            Thread.sleep(res);
        }catch(Exception e) {
        }
        String storeId = getApi().getStoreManager().getStoreId();
        try { getApi().getWubookManager().addNewBookingsPastDays(getMultiLevelName(), 2); }catch(Exception e) { GetShopLogHandler.logStack(e, storeId); }
        try { getApi().getWubookManager().updatePrices(getMultiLevelName()); }catch(Exception e) { GetShopLogHandler.logStack(e, storeId); }
        try { getApi().getWubookManager().checkForNoShowsAndMark(getMultiLevelName()); }catch(Exception e) { GetShopLogHandler.logStack(e, storeId); }
        try { getApi().getWubookManager().doubleCheckDeletedBookings(getMultiLevelName()); }catch(Exception e) { GetShopLogHandler.logStack(e, storeId); }
    }
}
