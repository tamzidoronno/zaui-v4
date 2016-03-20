
package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSchedulerBase;
import com.thundashop.core.arx.AccessLog;
import com.thundashop.core.arx.ArxConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ArxLogFetcher extends GetShopSchedulerBase {

    public ArxLogFetcher(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }

    @Override
    public void execute() throws Exception {
        PmsConfiguration config = getApi().getPmsManager().getConfiguration(getMultiLevelName());
        if(config.arxHostname == null || config.arxHostname.isEmpty()) {
            return;
        }
        
        ArxConnection connection = new ArxConnection();
        
        long start = System.currentTimeMillis();
        int i = 0;
        while(true) {
            long next = System.currentTimeMillis();
            String hostName = "https://" + config.arxHostname + ":5002/arx/eventexport?start_date="+start+"&end_date="+next;
            String result = "";
            try {
                long now = System.currentTimeMillis();
                result = connection.httpLoginRequest(hostName, config.arxUsername, config.arxPassword, "");
                long diff = System.currentTimeMillis();
                diff = diff - now;
                if(i < 3) {
                    System.out.println("Time takes to fetch from arx server 12: " + diff + "(" + hostName + "), result length: " + result.length());
                }
                start = next;
                HashMap<String, List<AccessLog>> res = getApi().getArxManager().generateDoorLogForAllDoorsFromResult(result);
                for(String doorId : res.keySet()) {
                    if(res.get(doorId).size() > 0) {
                        getApi().getPmsManager().handleDoorControl(getMultiLevelName(), doorId, res.get(doorId));
                    }
                }
                i++;
            }catch(Exception e) {
                e.printStackTrace();
            }
            try { Thread.sleep(2000); }catch(Exception e) { e.printStackTrace(); }
        }
    }

    
    
}
