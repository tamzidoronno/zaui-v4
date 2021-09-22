/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.getshop.scope.GetShopSession;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class GrafanaManager extends ManagerBase{
     
    @Autowired
    public FrameworkConfig frameworkConfig;

    @Autowired
    @Qualifier("grafanaFeederExecutor")
    private TaskExecutor taskExecutor;
    
    public void addPoint(String dbName, String point, HashMap<String, Object> values) {
        GrafanaFeeder feeder = new GrafanaFeederImpl();
        
        if (storeId != null && storeId.equals("eafea78d-1eea-403f-abbb-3b23a6e61dae")) {
            feeder = new DummyGrafanaFeeder();
        }
        
        feeder.setDbName(dbName);
        feeder.setPoint(point);
        feeder.setValues(values);
        values.put("storeId", storeId);

        if(frameworkConfig.productionMode) {
            taskExecutor.execute(feeder);
        }
    }
    
}
