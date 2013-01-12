/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import java.util.HashMap;

/**
 *
 * @author privat
 */
public class AppConfiguration extends DataCommon {
    public HashMap<String, Setting> settings = new HashMap<>();
    public int sticky;
    public int sequence;
    public String appName;
    public int inheritate;
    public String originalPageId;

    public AppConfiguration secureClone() {
        AppConfiguration appConfig = new AppConfiguration();
        appConfig.sticky = sticky;
        appConfig.sequence = sequence;
        appConfig.appName = appName;
        appConfig.inheritate = inheritate;
        appConfig.originalPageId = originalPageId;
        
        appConfig.id = id;
        appConfig.storeId = storeId;
        appConfig.deleted = deleted;
        appConfig.className = className;
        appConfig.rowCreatedDate = rowCreatedDate;
        
        for (String key : settings.keySet()) {
            Setting setting = settings.get(key);
            
            Setting clonedSetting = new Setting();
            clonedSetting.value = setting.value;
            
            if (setting.secure)
                clonedSetting.value = "****************";
                
            appConfig.settings.put(key, clonedSetting);
        }

        return appConfig;
    }
}
