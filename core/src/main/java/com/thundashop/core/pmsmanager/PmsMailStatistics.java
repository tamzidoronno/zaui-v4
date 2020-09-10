
package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSchedulerBase;
import java.util.Calendar;
import com.thundashop.core.storemanager.data.Store;
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
        String multi = getMultiLevelName();
        if(multi != null && !multi.equals("default")) {
            return;
        }
        Calendar cal = Calendar.getInstance();
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        int lastOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayofmonth = cal.get(Calendar.DAY_OF_MONTH);
        if(dayofweek != Calendar.SUNDAY && dayofmonth != lastOfMonth) {
            return;
        }
        
        Store store = getApi().getStoreManager().getMyStore();
        if(!store.id.equals("123865ea-3232-4b3b-9136-7df23cf896c6")) {
            return;
        }
        String storeAddr = store.getDefaultWebAddress();
        if(!storeAddr.startsWith("http")) {
            storeAddr = "http://" + storeAddr;
        }
        if(!getApi().getStoreManager().isProductMode()) {
            storeAddr = "http://wilhelmsenhouse.3.0.local.getshop.com";
        }
        String sessionId = getApi().getStoreManager().getCurrentSession();
        String url = storeAddr + "/scripts/generatePmsStatistics.php?username=" + getUsername() + "&password=" + getPassword();
        HttpClient client = new DefaultHttpClient();
        try {
            HttpGet request = new HttpGet(url);
            client.execute(request);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}
