package com.thundashop.core.appmanager;

import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
public class AppManager extends ManagerBase implements IAppManager {
    
    TreeMap<String, TreeMap<String, ApplicationSettings>> settings;
    
    
    @Autowired
    public AppManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
        settings = new TreeMap();
    }
    
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataObject : data.data) {
            if (dataObject instanceof ApplicationSettings) {
                ApplicationSettings setting = (ApplicationSettings) dataObject;
                addApplicationSettings(setting);
            }
        }
    }

    @Override
    public List<ApplicationSettings> getAllApplications() throws ErrorException {
        TreeMap<String, ApplicationSettings> settings = getApplications(getSession().currentUser.id);
        List<ApplicationSettings> appSettings = new ArrayList();
        appSettings.addAll(settings.values());
        return appSettings;
    }

    @Override
    public ApplicationSettings createApplication(String appName) throws ErrorException {
        ApplicationSettings settings = new ApplicationSettings();
        settings.appName = appName;
        saveSettings(settings);
        addApplicationSettings(settings);
        return settings;
    }

    @Override
    public void saveApplication(ApplicationSettings settings) throws ErrorException {
        saveSettings(settings);
        addApplicationSettings(settings);
    }

    private void addApplicationSettings(ApplicationSettings setting) {
        if(settings.get(setting.userId) == null) {
            settings.put(setting.userId, new TreeMap());
        }
        
        TreeMap<String, ApplicationSettings> map = settings.get(setting.userId);
        map.put(setting.id, setting);
    }

    private void saveSettings(ApplicationSettings settings) throws ErrorException {
        settings.storeId = getStore().id;
        settings.userId = getSession().currentUser.id;
        databaseSaver.saveObject(settings, credentials);
    }

    private TreeMap<String, ApplicationSettings> getApplications(String id) {
        if(settings.get(id) == null) {
            return new TreeMap();
        }
        
        return settings.get(id);
    }

    @Override
    public void deleteApplication(String id) throws ErrorException {
        ApplicationSettings setting = getApplicationInternal(id);
        settings.get(getSession().currentUser.id).remove(id);
        databaseSaver.deleteObject(setting, credentials);
    }

    @Override
    public ApplicationSettings getApplication(String id) throws ErrorException {
        return getApplicationInternal(id);
        
    }
    
    private ApplicationSettings getApplicationInternal(String id) throws ErrorException {
        if(settings.get(getSession().currentUser.id) == null) {
            throw new ErrorException(1015);
        }
        ApplicationSettings setting = settings.get(getSession().currentUser.id).get(id);
        if(setting == null) {
            throw new ErrorException(1015);
        }
        
        return setting;
    }
    
    
}
