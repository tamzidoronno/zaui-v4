/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.thundashop.core.common.DataScript;
import com.thundashop.core.common.FrameworkConfig;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    public void check() {
        System.out.println(" ============ Checing db scripts ==============");
        for (UpdateScript script : scripts) {
            
            String id = script.getClass().getSimpleName();
            DataScript dbScript = new DataScript();
            dbScript.id = id;
            
            if (!database.exists("GetShop", "dbscripts", dbScript)) {
                System.out.println("DB UPDATING... " + script.getClass().getSimpleName());
                script.run();
                database.save("GetShop", "dbscripts", dbScript);
            }
            
            
//            script.run();
        }
        
        System.out.println(" ============ Done ==============");
    }
    
}
