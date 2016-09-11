
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
public class PmsMailStatistics extends GetShopSchedulerBase {
    
    public PmsMailStatistics(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }


    @Override
    public void execute() throws Exception {
        Calendar cal = Calendar.getInstance();
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        int lastOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayofmonth = cal.get(Calendar.DAY_OF_MONTH);
        if(dayofweek != Calendar.SUNDAY && dayofmonth != lastOfMonth) {
            return;
        }
        
        String storeAddr = getApi().getStoreManager().getMyStore().getDefaultWebAddress();
        if(!storeAddr.startsWith("http")) {
            storeAddr = "http://" + storeAddr;
        }
        if(!getApi().getStoreManager().isProductMode()) {
            storeAddr = "http://wilhelmsenhouse.3.0.local.getshop.com";
        }
        String sessionId = getApi().getStoreManager().getCurrentSession();
        String url = storeAddr + "/scripts/generatePmsStatistics.php?username=" + getUsername() + "&password=" + getPassword();
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        client.execute(request);
    }
}
