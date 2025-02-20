/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.thundashop.core.common.DataScript;
import com.thundashop.services.config.FrameworkConfig;
import com.thundashop.core.common.GetShopLogHandler;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class DatabaseUpdater {

    private List<UpdateScript> scripts;

    @Autowired
    public Database database;
    
    @Autowired
    public FrameworkConfig config;
    
    @Autowired
    public void setDaemons(List<UpdateScript> scripts){
        this.scripts = scripts;
        Collections.sort(scripts, (UpdateScript u1, UpdateScript u2) -> u1.getAddedDate().compareTo(u2.getAddedDate()));
    }
    
    public boolean check(ApplicationContext context) {
        GetShopLogHandler.logPrintStatic(" ============ Checing db scripts ==============", null);
        boolean found = false;
        for (UpdateScript script : scripts) {
            
            if (script.doNotRun()) {
                continue;
            }
            
            
            String id = script.getClass().getSimpleName();
            DataScript dbScript = new DataScript();
            dbScript.id = id;
            
            if (!database.exists("GetShop", "dbscripts", dbScript)) {
                GetShopLogHandler.logPrintStatic("DB UPDATING... " + script.getClass().getSimpleName(), null);
                try {
                    found = true;
                    script.run();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                database.save("GetShop", "dbscripts", dbScript);
            }
            
            
//            script.run();
        }
        
        GetShopLogHandler.logPrintStatic(" ============ Done ==============", null);
        return found;
    }
    
}
