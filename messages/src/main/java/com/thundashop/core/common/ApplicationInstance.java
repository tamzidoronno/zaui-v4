package com.thundashop.core.common;

import java.util.HashMap;

@GetShopRemoteObject
public class ApplicationInstance extends DataCommon {
    public HashMap<String, Setting> settings = new HashMap<>();
    public String appSettingsId;

    public ApplicationInstance secureClone() {
        ApplicationInstance appConfig = new ApplicationInstance();
        appConfig.appSettingsId = appSettingsId;
        
        appConfig.id = id;
        appConfig.storeId = storeId;
        appConfig.deleted = deleted;
        appConfig.className = className;
        appConfig.rowCreatedDate = rowCreatedDate;
        
        for (String key : settings.keySet()) {
            Setting setting = settings.get(key);
            
            Setting clonedSetting = setting.secureClone();
                
            appConfig.settings.put(key, clonedSetting);
        }

        return appConfig;
    }
    
    public String getSetting(String key) {
        Setting setting = settings.get(key);
        if (setting == null) {
            return "";
        }
        
        if (setting.value == null) {
            return "";
        }
        
        return setting.value;
    }

    public void clear() {
        settings = new HashMap<>();
    }
}
