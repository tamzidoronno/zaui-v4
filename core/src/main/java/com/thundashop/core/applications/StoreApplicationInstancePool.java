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
import com.thundashop.core.listmanager.ListManager;
import com.thundashop.core.pagemanager.GetShopModules;
import com.thundashop.core.pagemanager.PageManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    @Autowired
    private PageManager pageManger;
    
    @Autowired
    private ListManager listManager;
    
    private Map<String, ApplicationInstance> applicationInstances;
    private Map<String, Map<String, ApplicationInstance>> moduleApplicationInstances = new HashMap<String, Map<String, ApplicationInstance>>();
    
    private HashMap<String, Integer> cachedSecurity = new HashMap();

    private GetShopModules modules = new GetShopModules();
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        applicationInstances = data.data.stream()
                .filter(o -> o.getClass() == ApplicationInstance.class)
                .map(o -> (ApplicationInstance) o)
                .collect(Collectors.toMap(o -> o.id, o -> o));
        
        loadRemoteData();
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

        getCurrentApplicationInstances().put(applicationInstance.id, applicationInstance);
        return checkSecurity(applicationInstance.secureClone());
    }

    @Override
    public ApplicationInstance getApplicationInstance(String applicationInstanceId) {
        ApplicationInstance instance = getCurrentApplicationInstances().get(applicationInstanceId);
        
        if (instance == null) {
            return null;
        }

        if (getSession() != null && getSession().currentUser != null && getSession().currentUser.type >= 50) {
            return checkSecurity(instance);
        }
        
        return checkSecurity(instance.secureClone());
        
    }

    @Override
    public ApplicationInstance setApplicationSettings(Settings settings) {
        ApplicationInstance application = getCurrentApplicationInstances().get(settings.appId);

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
        
        cachedSecurity.remove(application.id);
        return checkSecurity(application);
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
            for (ApplicationInstance instance : getCurrentApplicationInstances().values()) {
                if (instance.appSettingsId.equals(app.id)) {
                    return instance.settings;
                }
            }
        }
        
        return null;
    }

    private ApplicationInstance checkSecurity(ApplicationInstance secureClone) {
        overrideGlobalSettings(secureClone);
        
        if(getSession() != null && getSession().currentUser != null && (getSession().currentUser.isAdministrator() || getSession().currentUser.isEditor())) {
            return secureClone;
        }
        
        int lowestAccessLevelForAppOnPages = Integer.MAX_VALUE;
        
        if (cachedSecurity.get(secureClone.id) == null) {
            List<String> pages = pageManger.getPagesForApplicationOnlyBody(secureClone.id);
            lowestAccessLevelForAppOnPages = getLowestAccessLevel(pages);
            cachedSecurity.put(secureClone.id, lowestAccessLevelForAppOnPages);
        } else {
            lowestAccessLevelForAppOnPages = cachedSecurity.get(secureClone.id);
        }
        
        if (lowestAccessLevelForAppOnPages > 0 && (getSession() == null || getSession().currentUser == null)) {
            secureClone.appSettingsId = "access_denied";
        }
        
        if (getSession() != null && getSession().currentUser != null && getSession().currentUser.type < lowestAccessLevelForAppOnPages) {
            secureClone.appSettingsId = "access_denied";
        }
        
        return secureClone;
    }

    private int getLowestAccessLevel(List<String> pages) {
        int lowestAccessLevelForAppOnPages = Integer.MAX_VALUE;
        if (pages.size() == 0) {
            lowestAccessLevelForAppOnPages = 0;
        } else {
            for (String pageId : pages) {
                int accessLevel = listManager.getHighestAccessLevel(pageId);
                if (accessLevel < lowestAccessLevelForAppOnPages) {
                    lowestAccessLevelForAppOnPages = accessLevel ;
                }
            }
        }
        return lowestAccessLevelForAppOnPages;
    }

    @Override
    public List<ApplicationInstance> getApplicationInstances(String applicationId) {
        List<ApplicationInstance> result = new ArrayList();
        for(ApplicationInstance app : getCurrentApplicationInstances().values()) {
            if(app.appSettingsId.equals(applicationId)) {
                if (getSession() != null && getSession().currentUser != null && getSession().currentUser.type >= 50) {
                    result.add(checkSecurity(app));
                } else {
                    result.add(checkSecurity(app.secureClone()));
                }                
            }
        }
        
        return result;
    }

    private void overrideGlobalSettings(ApplicationInstance secureClone) {
        Application application = applicationPool.getApplication(secureClone.appSettingsId);
        boolean hasEditorOrAdminPriveliges = getSession() != null && getSession().currentUser != null && getSession().currentUser.type >= 50;
        
        if (application == null || application.settings == null) {
            return;
        }
        
        for (String key : application.settings.keySet()) {
//            if (secureClone.settings.containsKey(key)) {
//                continue;
//            }
            
            if (hasEditorOrAdminPriveliges) {
                secureClone.settings.put(key, application.settings.get(key));
            } else {
                secureClone.settings.put(key, application.settings.get(key).secureClone());
            }
        }
    }

    public ApplicationInstance makeDuplicatedApplication(String appId) {
        ApplicationInstance app = getApplicationInstance(appId);
        if (app != null) {
            try {
                ApplicationInstance newApp = (ApplicationInstance) app.clone();
                newApp.id = UUID.randomUUID().toString();
                getCurrentApplicationInstances().put(newApp.id, newApp);
                saveObject(newApp);
                return newApp;
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(StoreApplicationInstancePool.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
        return null;
    }
    
    private Map<String, ApplicationInstance> getCurrentApplicationInstances() {
        if (isCmsModule()) {
            return applicationInstances;
        }
        
        if (moduleApplicationInstances.get(getCurrentGetShopModule()) == null) {
            moduleApplicationInstances.put(getCurrentGetShopModule(), new HashMap());
        }
        
        return moduleApplicationInstances.get(getCurrentGetShopModule());
    }

    private void loadRemoteData() {
        modules.getModules().stream().forEach(m -> {
            databaseRemote.getAll("StoreApplicationInstancePool", "all", m.id).forEach(o -> {
                if (o instanceof ApplicationInstance) {
                    ApplicationInstance instance = (ApplicationInstance)o;
                    if (moduleApplicationInstances.get(instance.getshopModule) == null) {
                        moduleApplicationInstances.put(instance.getshopModule, new HashMap());
                    }
                    moduleApplicationInstances.get(instance.getshopModule).put(instance.id, instance);
                }
            });
        });
    }

    public List<String> getDistinctApplicationsUsedForPool() {
        return getCurrentApplicationInstances()
                .values()
                .stream()
                .map(i -> i.appSettingsId)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationInstance getApplicationInstanceWithModule(String applicationInstanceId, String moduleName) {
        if (moduleApplicationInstances.get(moduleName) == null) {
            return null;
        }
        
        ApplicationInstance instance = moduleApplicationInstances.get(moduleName).get(applicationInstanceId);
        if (instance == null) {
            return null;
        }

        if (getSession() != null && getSession().currentUser != null && getSession().currentUser.type >= 50) {
            instance =  checkSecurity(instance);
            return instance;
        }
        
        return checkSecurity(instance.secureClone());
    }

    @Override
    public ApplicationInstance createNewInstanceWithId(String applicationId, String instanceId) {
        ApplicationInstance applicationInstance = new ApplicationInstance();
        applicationInstance.appSettingsId = applicationId;
        applicationInstance.id = instanceId;
        
        saveObject(applicationInstance);

        getCurrentApplicationInstances().put(applicationInstance.id, applicationInstance);
        return applicationInstance;
    }
}
