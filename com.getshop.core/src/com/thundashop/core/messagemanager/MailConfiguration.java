/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.thundashop.core.common.*;
import com.thundashop.core.pagemanager.IPageManager;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@Scope("prototype")
class MailConfiguration {
    class Settings {
        public String sendMailFrom = "post@getshop.com";
        public String hostname = "smtp.gmail.com";
        public String username = "post@getshop.com";
        public String password = "shootthatbitch";
        public boolean enableTls = true;
        public int port = 587;
    }
    
    private Settings settings;
    
    @Autowired
    public Logger log;
    
    public void setup(String storeId) {
        settings = new Settings();
        StoreHandler storeHandler = AppContext.storePool.getStorePool(storeId);
        
        
        Map<String, Setting> confSettings = null;
        if (storeHandler != null) {
            IPageManager pageManager = storeHandler.getManager(IPageManager.class);
            confSettings = pageManager.getSecuredSettingsInternal("Mail");
        }

        if (confSettings != null) {
            if (confSettings.get("hostname") != null) {
                settings.hostname = confSettings.get("hostname").value;
            }
            
            if (confSettings.get("port") != null) {
                settings.port = Integer.valueOf(confSettings.get("port").value);
            }
            
            if (confSettings.get("password") != null) {
                settings.password = confSettings.get("password").value;
            }
            
            if (confSettings.get("username") != null) {
                settings.username = confSettings.get("username").value;
                settings.sendMailFrom = confSettings.get("username").value;
            }
            
            if (confSettings.get("enabletls") != null) {
                String enableTls = confSettings.get("enabletls").value;
                if (enableTls != null && enableTls.equals("true")) {
                    settings.enableTls = true;
                }
            }
        }
        
    }
    
    public MailConfiguration.Settings getSettings() {
        return settings;
    }
}