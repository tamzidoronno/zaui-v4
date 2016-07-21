/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GrafanaManager {
     
    @Autowired
    public FrameworkConfig frameworkConfig;
    
    public void addPoint(String dbName, String point, HashMap<String, Object> values) {
        GrafanaFeeder feeder = new GrafanaFeeder();
        feeder.dbName = dbName;
        feeder.point = point;
        feeder.values = values;
        if(frameworkConfig.productionMode) {
            feeder.start();
        }
    }
    
}
