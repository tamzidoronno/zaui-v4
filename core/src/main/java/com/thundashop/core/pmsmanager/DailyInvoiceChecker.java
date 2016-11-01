
package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSchedulerBase;
import com.ibm.icu.util.Calendar;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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
