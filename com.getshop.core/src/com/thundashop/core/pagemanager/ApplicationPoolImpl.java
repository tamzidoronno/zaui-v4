/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pagemanager;

import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.Credentials;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private HashMap<String, AppConfiguration> applications = new HashMap<String, AppConfiguration>();
    
    @Autowired
    public Logger logger;
    
    private String storeId;
    private Credentials credentials;
            
    @Autowired
    public DatabaseSaver databaseSaver;
    
    public void initialize(Credentials credentials, String storeId) {
        this.credentials = credentials;
        this.storeId = storeId;
    }
    
    public void addFromDatabase(AppConfiguration appConfiguration) {
        applications.put(appConfiguration.id, appConfiguration);
    }
    
    public AppConfiguration createNewApplication(String applicationName) throws ErrorException {
        AppConfiguration appConfiguration = new AppConfiguration();
        appConfiguration.sticky = 0;
        appConfiguration.appName = applicationName;
        appConfiguration.storeId = storeId;
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
    
    public Map<String, AppConfiguration> getApplications() {
        Map<String, AppConfiguration> retApps = new HashMap();
        for (String key : applications.keySet()) {
            AppConfiguration app = applications.get(key);
            retApps.put(key, app.secureClone());
        }
        
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

}
