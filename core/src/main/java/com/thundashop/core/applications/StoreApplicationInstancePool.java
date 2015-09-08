/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.applications;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.ApplicationInstance;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Setting;
import com.thundashop.core.common.Settings;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class StoreApplicationInstancePool extends ManagerBase implements IStoreApplicationInstancePool {

    @Autowired
    private StoreApplicationPool storeApplicationPool;

    private Map<String, ApplicationInstance> applicationInstances;

    @Override
    public void dataFromDatabase(DataRetreived data) {
        applicationInstances = data.data.stream()
                .filter(o -> o.getClass() == ApplicationInstance.class)
                .map(o -> (ApplicationInstance) o)
                .collect(Collectors.toMap(o -> o.id, o -> o));
    }

    @Override
    public ApplicationInstance createNewInstance(String applicationId) {
        Application app = storeApplicationPool.getApplication(applicationId);

        if (app == null) {
            return null;
        }

        ApplicationInstance applicationInstance = new ApplicationInstance();
        applicationInstance.appSettingsId = app.id;
        saveObject(applicationInstance);

        applicationInstances.put(applicationInstance.id, applicationInstance);
        return applicationInstance.secureClone();
    }

    @Override
    public ApplicationInstance getApplicationInstance(String applicationInstanceId) {
        ApplicationInstance instance = applicationInstances.get(applicationInstanceId);

        if (instance == null) {
            return null;
        }

        return instance.secureClone();
    }

    @Override
    public ApplicationInstance setApplicationSettings(Settings settings) {
        ApplicationInstance application = applicationInstances.get(settings.appId);

        HashMap<String, Setting> saveSettings = application.settings;
        if (saveSettings == null) {
            saveSettings = new HashMap<>();
        }

        for (Setting setting : settings.settings) {
            if (setting.secure && setting.value.equals("****************")) {
                saveSettings.put(setting.id, application.settings.get(setting.id));
            } else {
                saveSettings.put(setting.id, setting);
            }
        }

        application.settings = saveSettings;
        saveObject(application);
        return application.secureClone();
    }

    /**
     * This should be used. there is no such thing as getting one application by a 
     * php name.
     * 
     * @param phpApplicationName
     * @deprecated
     */
    @Deprecated
    public HashMap<String, Setting> getApplicationInstanceSettingsByPhpName(String phpApplicationName) {
        Application app = null;
        
        for (Application application : storeApplicationPool.getApplications()) {
            if (application.appName.equals(phpApplicationName)) {
                app = application;
                break;
            }
        }
        
        if (app != null) {
            for (ApplicationInstance instance : applicationInstances.values()) {
                if (instance.appSettingsId.equals(app.id)) {
                    return instance.settings;
                }
            }
        }
        
        return null;
    }
}
