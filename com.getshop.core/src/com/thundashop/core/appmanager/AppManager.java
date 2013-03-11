package com.thundashop.core.appmanager;

import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.appmanager.data.ApplicationSynchronization;
import com.thundashop.core.appmanager.data.AvailableApplications;
import com.thundashop.core.common.AppConfiguration;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.ApplicationPoolImpl;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.PageArea;
import java.util.ArrayList;
import java.util.List;
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
public class AppManager extends ManagerBase implements IAppManager {
    public List<ApplicationSynchronization> toSync;
    
//    TODO
//    US this variable to retreive data.
//    private AvailableApplications applications = new AvailableApplications();

    @Autowired
    private ApplicationPool applicationPool;
    
    @Autowired
    public AppManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataObject : data.data) {
            if (dataObject instanceof ApplicationSynchronization) {
                addToSync((ApplicationSynchronization)dataObject);
            }
        }
    }

    @Override
    public AvailableApplications getAllApplications() throws ErrorException {
        AvailableApplications retMessage = new AvailableApplications();
        retMessage.applications = applicationPool.getAll(this.getSession().storeId);
        retMessage.addedApplications = getAddedApplications();
        
        return retMessage;
    }
    
    private ArrayList<ApplicationSettings> getAddedApplications() {
        ArrayList<ApplicationSettings> appSettings = new ArrayList();
        PageManager pageManager = getManager(PageManager.class);
        for (ApplicationSettings app : applicationPool.getAll(this.getSession().storeId)) {
            if (pageManager.applicationPool.isApplicationAdded(app)) {
                appSettings.add(app);
            }
        }
        return appSettings;
    }

    @Override
    public ApplicationSettings createApplication(String appName) throws ErrorException {
        ApplicationSettings settings = new ApplicationSettings();
        settings.appName = appName;

        settings.allowedAreas = new ArrayList();
        settings.allowedAreas.add(PageArea.Type.LEFT);
        settings.allowedAreas.add(PageArea.Type.MIDDLE);
        settings.allowedAreas.add(PageArea.Type.RIGHT);
        settings.description = "";
        settings.price = 0.0;
        settings.userId = getSession().currentUser == null ? "" : getSession().currentUser.id;
        settings.ownerStoreId = storeId;
        settings.storeId = storeId;

        applicationPool.addApplicationSettings(settings);
        return settings;
    }

    @Override
    public void saveApplication(ApplicationSettings settings) throws ErrorException {
        saveSettings(settings);
    }

    private void saveSettings(ApplicationSettings settings) throws ErrorException {
        if (settings.ownerStoreId == null || !settings.ownerStoreId.equals(getStore().id)) {
            throw new ErrorException(26);
        }
        applicationPool.addApplicationSettings(settings);
    }

    @Override
    public void deleteApplication(String id) throws ErrorException {
        applicationPool.deleteApplication(id);
    }

    @Override
    public ApplicationSettings getApplication(String id) throws ErrorException {
        for (ApplicationSettings appSettings : getAllApplications().applications) {
            String appId = appSettings.id;
            if (appId.equals(id)) {
                return applicationPool.get(appId);
            }
        }
        throw new ErrorException(18);
    }

    @Override
    public void setSyncApplication(String id) throws ErrorException {
        ApplicationSettings app = this.getApplication(id);
        ApplicationSynchronization sync = new ApplicationSynchronization();
        sync.appId = app.id;
        sync.userId = getSession().currentUser.id;
        addToSync(sync);
        sync.storeId = storeId;
        databaseSaver.saveObject(sync, credentials);
    }

    private void addToSync(ApplicationSynchronization sync) {
        if(toSync == null) {
            toSync = new ArrayList();
        }
        
        toSync.add(sync);
    }

    @Override
    public List<ApplicationSynchronization> getSyncApplications() throws ErrorException {
        if(toSync == null) {
            return new ArrayList();
        }
        
        List<ApplicationSynchronization> result = new ArrayList();
        String loggedOnuserId = getSession().currentUser.id;
        for(ApplicationSynchronization sync : toSync) {
            if(sync.userId.equals(loggedOnuserId)) {
                result.add(sync);
            }
        }
        
        //Cleaning time.
        for(ApplicationSynchronization syncer : result) {
            databaseSaver.deleteObject(syncer, credentials);
            toSync.remove(syncer);
        }
        
        return result;
    }

    @Override
    public void swapApplication(String fromAppId, String toAppId) throws ErrorException {
        PageManager manager = getManager(PageManager.class);
        ApplicationSettings toApp = getApplication(toAppId);
        
        ApplicationPoolImpl pool = manager.applicationPool;
        Map<String, AppConfiguration> allAddedApplications = pool.getApplications();
        for(String instanceId : allAddedApplications.keySet()) {
            AppConfiguration config = allAddedApplications.get(instanceId);
            if(config.appSettingsId != null && config.appSettingsId.equals(fromAppId)) {
                System.out.println("Changing: " + config.id);
                config.appSettingsId = toApp.id;
                databaseSaver.saveObject(config, credentials);
            }
        }
    }

}
