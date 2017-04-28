
package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSchedulerBase;

/**
 *
 * @author boggi
 */
public class DailyInvoiceChecker extends GetShopSchedulerBase {
    
    public DailyInvoiceChecker(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }


    @Override
    public void execute() throws Exception {
        getApi().getPmsInvoiceManager().validateAllInvoiceToDates(getMultiLevelName());
    }
}
