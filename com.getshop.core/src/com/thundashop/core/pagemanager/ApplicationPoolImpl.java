/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pagemanager;

import com.thundashop.core.appmanager.AppManager;
import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.data.Store;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@Scope("prototype")
public class ApplicationPoolImpl {
    /**
     * Applications that are added to the corresponding store.
     */
    private HashMap<String, AppConfiguration> applications = new HashMap<String, AppConfiguration>();
    
    @Autowired
    public Logger logger;
    
    @Autowired
    public StoreManager storeManager;
    
    private String storeId;
    private Credentials credentials;
            
    @Autowired
    public DatabaseSaver databaseSaver;
    
    private PageManager pageManager;

    public void initialize(Credentials credentials, String storeId) {
        this.credentials = credentials;
        this.storeId = storeId;
    }
    
    public void addFromDatabase(AppConfiguration appConfiguration) {
        applications.put(appConfiguration.id, appConfiguration);
    }
    
    public AppConfiguration createNewApplication(String applicationSettingsId) throws ErrorException {
        AppManager appManager = pageManager.getManager(AppManager.class);
        ApplicationSettings setting = appManager.getApplication(applicationSettingsId);
        if (setting.type.equals(ApplicationSettings.Type.Theme)) {
            removeAllThemeApplications();
        }
        AppConfiguration appConfiguration = new AppConfiguration();
        appConfiguration.sticky = 0;
        appConfiguration.appName = setting.appName;
        appConfiguration.storeId = storeId;
        appConfiguration.appSettingsId = setting.id;
        databaseSaver.saveObject(appConfiguration, credentials);
        applications.put(appConfiguration.id, appConfiguration);
        
        return appConfiguration;
    }

    public AppConfiguration stickApplication(String appId, int sticky) throws ErrorException {
        AppConfiguration appConfig = applications.get(appId);
        appConfig.sticky = sticky;
        databaseSaver.saveObject(appConfig, credentials);
        return appConfig.secureClone();
    }

    /**
     * Returns an application given by the Id.
     * @param applicationId
     * @return
     * @throws ErrorException 
     */
    public AppConfiguration get(String application) throws ErrorException {
        AppConfiguration app = applications.get(application);
        if (app == null) {
            throw new ErrorException(58);
        }
        return app.secureClone();
    }
    
    public AppConfiguration getByName(String applicationName) {
        for (AppConfiguration app : applications.values()) {
            if (app.appName.equals(applicationName))
                return app.secureClone();
        }
        
        return null;
    }
    
    public List<AppConfiguration> getStickedApplications() {
        List<AppConfiguration> ret = new ArrayList<AppConfiguration>();
        
        for (AppConfiguration app : applications.values()) {
            if (app.sticky > 0) 
                ret.add(app.secureClone());
        }
        
        return ret;
    }
    
    public Map<String, AppConfiguration> getApplications() throws ErrorException {
        Map<String, AppConfiguration> retApps = new HashMap();
        for (String key : applications.keySet()) {
            AppConfiguration app = applications.get(key);
            retApps.put(key, app.secureClone());
        }
        
        addDefaultThemeIfNotExists(retApps);
        return retApps;
    }

    public AppConfiguration saveSettings(Settings settings) throws ErrorException {
        AppConfiguration application = applications.get(settings.appId);        
        
        HashMap<String, Setting> saveSettings = new HashMap<>();
        for (Setting setting : settings.settings) {
            saveSettings.put(setting.id, setting);
        }
        
        application.settings = saveSettings;
        databaseSaver.saveObject(application, credentials);
        return application;
    }

    public void deleteApplication(String applicationId) throws ErrorException {
        AppConfiguration app = get(applicationId);
        applications.remove(applicationId);
        databaseSaver.deleteObject(app, credentials);
    }

    public void saveApplicationConfiguration(AppConfiguration config) throws ErrorException {
        AppConfiguration app = get(config.id);
        if(app == null) {
            ErrorException error = new ErrorException(18);
            error.additionalInformation = "Could not find the configuration object for the application to save";
            throw error;
        }
        
        applications.put(config.id, config);
        databaseSaver.saveObject(config, credentials);
    }

    public AppConfiguration getSecured(String appName) {
        for (AppConfiguration app : applications.values()) {
            if (app.appName.equals(appName))
                return app;
        }
        
        return null;
    }

    public boolean isApplicationAdded(ApplicationSettings appSetting) {
        for (AppConfiguration appConfig : applications.values()) {
            if (appSetting.id.equals(appConfig.appSettingsId)) {
                return true;
            }
        }
        
        return false;
    }

    private void addDefaultThemeIfNotExists(Map<String, AppConfiguration> retApps) throws ErrorException {
        AppManager appManager = pageManager.getManager(AppManager.class);
        for (AppConfiguration app : retApps.values()) {
            ApplicationSettings setting;
            try {
                setting = appManager.getApplication(app.appSettingsId);
                if (setting.type.equals(ApplicationSettings.Type.Theme)) {
                    return;
                }
            } catch (ErrorException ex) {
                logger.warning(this, "application added but does not exists: " + app.appName + " applicationSettingsId: " + app.appSettingsId);
            }
            
        }
        
        AppConfiguration themeApp = createNewApplication("efcbb450-8f26-11e2-9e96-0800200c9a66");
        retApps.put(themeApp.id, themeApp.secureClone());
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    private void removeAllThemeApplications() throws ErrorException {
        AppManager appManager = pageManager.getManager(AppManager.class);
        
        List<String> remove = new ArrayList<String>();
        for (AppConfiguration appConfig : applications.values()) {
            try {
                ApplicationSettings setting = appManager.getApplication(appConfig.appSettingsId);
                if (setting.type.equals(ApplicationSettings.Type.Theme)) {
                    remove.add(appConfig.id);
                }
            } catch (ErrorException ex) {}
        }
        for (String rem : remove) {
            deleteApplication(rem);
        }
    }

    public void setThemeSelectedToStoreConfiguration(AppConfiguration res) throws ErrorException {
        AppManager appManager = pageManager.getManager(AppManager.class);
        ApplicationSettings setting = appManager.getApplication(res.appSettingsId);
        if (setting.type.equals(ApplicationSettings.Type.Theme)) {
            Store store = pageManager.getStore();
            store.configuration.hasSelectedDesign = true;
            storeManager.saveStore(store.configuration);
        }
    }

    public List<AppConfiguration> getThemeApplications() {
        List<AppConfiguration> apps = new ArrayList();
        for (AppConfiguration config : applications.values()) {
            try {
                AppManager appManager = pageManager.getManager(AppManager.class);
                if(config.appSettingsId == null) {
                    continue;
                }
                ApplicationSettings appSettings = appManager.getApplication(config.appSettingsId);
                if (appSettings.isSingleton || 
                        appSettings.type.equals(ApplicationSettings.Type.Theme) ||
                        appSettings.type.equals(ApplicationSettings.Type.System)) {
                    apps.add(config);
                }
            } catch (ErrorException ex) {
                java.util.logging.Logger.getLogger(ApplicationPoolImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return apps;
    }

}
