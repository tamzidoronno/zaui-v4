/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pagemanager;

import com.thundashop.core.appmanager.AppManager;
import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.data.Store;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    private HashMap<String, AppConfiguration> applicationInstances = new HashMap<String, AppConfiguration>();
    @Autowired
    public Logger logger;
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
        applicationInstances.put(appConfiguration.id, appConfiguration);
    }

    public AppConfiguration createNewApplication(String applicationSettingsId) throws ErrorException {
        String appid = UUID.randomUUID().toString();
        return createNewApplication(applicationSettingsId, appid);
    }

    public AppConfiguration createNewApplication(String applicationSettingsId, String applicationId) throws ErrorException {
        AppManager appManager = pageManager.getManager(AppManager.class);
        ApplicationSettings setting = appManager.getApplication(applicationSettingsId);
        if (setting.type.equals(ApplicationSettings.Type.Theme)) {
            removeAllThemeApplications();
        }

        if (applicationId == null || applicationId.equals("")) {
            applicationId = UUID.randomUUID().toString();
        }

        AppConfiguration appConfiguration = new AppConfiguration();
        appConfiguration.id = applicationId;
        appConfiguration.sticky = 0;
        appConfiguration.appName = setting.appName;
        appConfiguration.storeId = storeId;
        appConfiguration.appSettingsId = setting.id;
        databaseSaver.saveObject(appConfiguration, credentials);
        applicationInstances.put(appConfiguration.id, appConfiguration);

        return appConfiguration;
    }

    public AppConfiguration stickApplication(String appId, int sticky) throws ErrorException {
        AppConfiguration appConfig = applicationInstances.get(appId);
        appConfig.sticky = sticky;
        databaseSaver.saveObject(appConfig, credentials);
        return finalizeApplication(appConfig);
    }

    private AppConfiguration finalizeApplication(AppConfiguration app) throws ErrorException {
        HashMap<String, Setting> settings = new HashMap();

        for (AppConfiguration iapp : applicationInstances.values()) {
            if (iapp.appName.equals("Settings")) {
                settings = iapp.settings;
            }
        }

        return app.secureClone();
    }

    /**
     * Returns an application instance given by the Id.
     *
     * @param applicationId
     * @return
     * @throws ErrorException
     */
    public AppConfiguration get(String application) throws ErrorException {
        AppConfiguration app = applicationInstances.get(application);
        if (app == null) {
            throw new ErrorException(58);
        }
        return finalizeApplication(app);
    }

    /**
     * Dont use this one, please use List<AppConfiguration>
     * getApplications(appsettingsid);
     */
    @Deprecated
    public AppConfiguration getByName(String applicationName) throws ErrorException {
        for (AppConfiguration app : applicationInstances.values()) {
            if (app.appName.equals(applicationName)) {
                return finalizeApplication(app);
            }
        }

        return null;
    }

    public List<AppConfiguration> getStickedApplications() throws ErrorException {
        List<AppConfiguration> ret = new ArrayList<AppConfiguration>();

        for (AppConfiguration app : applicationInstances.values()) {
            if (app.sticky > 0) {
                ret.add(finalizeApplication(app));
            }
        }

        return ret;
    }

    public Map<String, AppConfiguration> getApplications() throws ErrorException {
        Map<String, AppConfiguration> retApps = new HashMap();
        for (String key : applicationInstances.keySet()) {
            AppConfiguration app = applicationInstances.get(key);
            retApps.put(key, finalizeApplication(app));
        }

        addDefaultThemeIfNotExists(retApps);
        return retApps;
    }

    public AppConfiguration saveSettings(Settings settings) throws ErrorException {
        AppConfiguration application = applicationInstances.get(settings.appId);

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
        databaseSaver.saveObject(application, credentials);
        return application;
    }

    public void deleteApplication(String applicationId) throws ErrorException {
        AppConfiguration app = get(applicationId);
        applicationInstances.remove(applicationId);
        databaseSaver.deleteObject(app, credentials);
    }

    public void saveApplicationConfiguration(AppConfiguration config) throws ErrorException {
        AppConfiguration app = get(config.id);
        if (app == null) {
            ErrorException error = new ErrorException(18);
            error.additionalInformation = "Could not find the configuration object for the application to save";
            throw error;
        }

        applicationInstances.put(config.id, config);
        databaseSaver.saveObject(config, credentials);
    }

    public AppConfiguration getSecured(String appNameOrId) {
        for (AppConfiguration app : applicationInstances.values()) {
            if (app.id.equals(appNameOrId)) {
                return app;
            } else  if (app.appName.equals(appNameOrId)) {
                return app;
            }
        }

        return null;
    }

    public boolean isApplicationAdded(ApplicationSettings appSetting) {
        for (AppConfiguration appConfig : applicationInstances.values()) {
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
                if (app.appSettingsId == null) {
                    continue;
                }

                setting = appManager.getApplication(app.appSettingsId);
                if (setting.type.equals(ApplicationSettings.Type.Theme)) {
                    return;
                }
            } catch (ErrorException ex) {
                logger.warning(this, "Applicatoin added,  " + app.appName + " but does not have a ApplicationSetting object, appsettingsid: " + app.appSettingsId);
            }
        }

        StoreManager storeManger = pageManager.getManager(StoreManager.class);
        Store store = storeManger.getMyStore();
        store.configuration.colors.baseColor = "FF6103";
        store.configuration.colors.backgroundColor = "FF9955";
        store.configuration.colors.textColor = "000000";

        AppConfiguration themeApp = createNewApplication("efcbb450-8f26-11e2-9e96-0800200c9a66");
        retApps.put(themeApp.id, finalizeApplication(themeApp));
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    private void removeAllThemeApplications() throws ErrorException {
        AppManager appManager = pageManager.getManager(AppManager.class);

        List<String> remove = new ArrayList<String>();
        for (AppConfiguration appConfig : applicationInstances.values()) {
            try {
                ApplicationSettings setting = appManager.getApplication(appConfig.appSettingsId);
                if (setting.type.equals(ApplicationSettings.Type.Theme)) {
                    remove.add(appConfig.id);
                }
            } catch (ErrorException ex) {
            }
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
            StoreManager storeManager = pageManager.getManager(StoreManager.class);
            storeManager.saveStore(store.configuration);
        }
    }

    public List<AppConfiguration> getThemeApplications() {
        List<AppConfiguration> apps = new ArrayList();
        for (AppConfiguration config : applicationInstances.values()) {
            try {
                AppManager appManager = pageManager.getManager(AppManager.class);
                if (config.appSettingsId == null) {
                    continue;
                }
                ApplicationSettings appSettings = appManager.getApplication(config.appSettingsId);
                if (appSettings.isSingleton
                        || appSettings.type.equals(ApplicationSettings.Type.Theme)
                        || appSettings.type.equals(ApplicationSettings.Type.System)) {
                    apps.add(finalizeApplication(config));
                }
            } catch (ErrorException ex) {
                java.util.logging.Logger.getLogger(ApplicationPoolImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return apps;
    }

    public List<AppConfiguration> getApplications(String appSettingsId) throws ErrorException {
        List<AppConfiguration> retApps = new ArrayList();

        if (appSettingsId == null) {
            return retApps;
        }

        for (AppConfiguration app : applicationInstances.values()) {
            if (appSettingsId.equals(app.appSettingsId)) {
                retApps.add(finalizeApplication(app));
            }
        }

        return retApps;
    }
}
