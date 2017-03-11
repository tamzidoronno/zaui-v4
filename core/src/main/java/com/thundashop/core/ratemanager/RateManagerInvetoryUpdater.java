
package com.thundashop.core.ratemanager;

import com.getshop.scope.GetShopSchedulerBase;

/**
 *
 * @author boggi
 */
public class RateManagerInvetoryUpdater extends GetShopSchedulerBase {
    
    public RateManagerInvetoryUpdater(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }


    @Override
    public void execute() throws Exception {
        getApi().getBookingComRateManagerManager().pushAllBookings(getMultiLevelName());
    }
}
