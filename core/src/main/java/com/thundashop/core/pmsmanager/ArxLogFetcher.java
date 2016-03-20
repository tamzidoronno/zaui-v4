
package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSchedulerBase;
import com.thundashop.core.arx.AccessLog;
import com.thundashop.core.arx.ArxConnection;
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
        while(true) {
            long next = System.currentTimeMillis();
            System.out.println("Checking interval: " + start + " - " + next);
            String hostName = "https://" + config.arxHostname + ":5002/arx/eventexport?start_date="+start+"&end_date="+next;
            String result = "";
            try {
                result = connection.httpLoginRequest(hostName, config.arxUsername, config.arxPassword, "");
                start = next;
                System.out.println(result);
                long start2 = System.currentTimeMillis();
                HashMap<String, List<AccessLog>> res = getApi().getArxManager().generateDoorLogForAllDoorsFromResult(result);
                long end2 = System.currentTimeMillis();
                long diff = end2 - start2;
                System.out.println("api call time: " + diff);
                for(String doorId : res.keySet()) {
                    if(res.get(doorId).size() > 0) {
                        handleDoorControl(doorId, res.get(doorId));
                    }
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
            try { Thread.sleep(2000); }catch(Exception e) { e.printStackTrace(); }
        }
    }

    private void handleDoorControl(String doorId, List<AccessLog> get) {
        System.out.println("Need to handle door: " + doorId + ", " + get.size());
    }
    
}
