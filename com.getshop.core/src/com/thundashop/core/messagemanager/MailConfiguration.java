/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.*;
import com.thundashop.core.pagemanager.IPageManager;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@GetShopSession
public class MailConfiguration implements MailConfig {
    
    private MailSettings settings = new MailSettings();
    
    @Autowired
    public Logger log;
    
    @Autowired
    private IPageManager pageManager;
    
    @Override
    public void setup(String storeId) {
        Map<String, Setting> confSettings = pageManager.getSecuredSettingsInternal("Mail");

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
    
    @Override
    public MailSettings getSettings() {
        return settings;
    }
}