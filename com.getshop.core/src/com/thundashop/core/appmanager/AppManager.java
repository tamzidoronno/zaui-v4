package com.thundashop.core.appmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.appmanager.data.ApplicationSubscription;
import com.thundashop.core.appmanager.data.ApplicationSynchronization;
import com.thundashop.core.appmanager.data.AvailableApplications;
import com.thundashop.core.common.AppConfiguration;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.PageArea;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The appmanager manage and keep track of the applications in getshop.
 * Each application has an appsettingsid, and an instanceid. The appsettings
 * id is the id of the main application, the instanceid is the id of the instances to the application.
 */
@Component
@GetShopSession
public class AppManager extends ManagerBase implements IAppManager {

    public List<ApplicationSynchronization> toSync;
    private Date lastConnected = new Date();
    Map<String, ApplicationSubscription> addedApps = new HashMap();

    @Autowired
    private PageManager pageManager;

    @Autowired
    private ApplicationPool applicationPool;


    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataObject : data.data) {
            if (dataObject instanceof ApplicationSynchronization) {
                addToSync((ApplicationSynchronization) dataObject);
            }
            if (dataObject instanceof ApplicationSubscription) {
                ApplicationSubscription sub = (ApplicationSubscription) dataObject;
                addedApps.put(sub.appSettingsId, sub);
            }
        }
    }

    @Override
    public AvailableApplications getAllApplications() throws ErrorException {
        AvailableApplications retMessage = new AvailableApplications();
        retMessage.applications = applicationPool.getAll(storeId);
        return retMessage;
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

        applicationPool.addApplicationSettings(settings);
        return settings;
    }

    @Override
    public void saveApplication(ApplicationSettings settings) throws ErrorException {
        saveSettings(settings);
    }

    private void saveSettings(ApplicationSettings settings) throws ErrorException {
        if (settings.ownerStoreId == null || !settings.ownerStoreId.equals(storeId)) {
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

        if(id.equals("newapp")) {
            return null;
        }
        
        return null;
    }

    @Override
    public void setSyncApplication(String id) throws ErrorException {
        ApplicationSettings app = this.getApplication(id);
        if (app != null) {
            ApplicationSynchronization sync = new ApplicationSynchronization();
            sync.appId = app.id;
            sync.userId = getSession().currentUser.id;
            sync.appName = app.appName;
            addToSync(sync);
            sync.storeId = storeId;
            databaseSaver.saveObject(sync, credentials);
        }
    }

    private void addToSync(ApplicationSynchronization sync) {
        if (toSync == null) {
            toSync = new ArrayList();
        }

        toSync.add(sync);
    }

    @Override
    public List<ApplicationSynchronization> getSyncApplications() throws ErrorException {
        lastConnected = new Date();
        if (toSync == null) {
            return new ArrayList();
        }

        List<ApplicationSynchronization> result = new ArrayList();
        
        if(getSession().currentUser == null) {
            return result;
        }
        
        String loggedOnuserId = getSession().currentUser.id;
        for (ApplicationSynchronization sync : toSync) {
            if (sync.userId.equals(loggedOnuserId)) {
                result.add(sync);
            }
        }

        //Cleaning time.
        for (ApplicationSynchronization syncer : result) {
            databaseSaver.deleteObject(syncer, credentials);
            toSync.remove(syncer);
        }

        return result;
    }

    @Override
    public boolean isSyncToolConnected() throws ErrorException {
        long now = new Date().getTime();
        long lastTime = lastConnected.getTime();
        long diff = now - lastTime;
        if (diff < (1000 * 10 * 2)) {
            return true;
        }
        return false;
    }

    @Override
    public Map<String, ApplicationSubscription> getAllApplicationSubscriptions(boolean include) throws ErrorException {
        for (ApplicationSubscription sub : addedApps.values()) {
            sub.numberOfInstancesAdded = 0;
        }

        for (AppConfiguration config : pageManager.getApplications()) {
            if (config.appSettingsId == null) {
                continue;
            }
            ApplicationSubscription subscription = addedApps.get(config.appSettingsId);
            if (subscription == null) {
                subscription = new ApplicationSubscription();
                subscription.appSettingsId = config.appSettingsId;
            }
            subscription.app = getApplication(subscription.appSettingsId);
            if (subscription.from_date == null || subscription.from_date.after(config.rowCreatedDate)) {
                updateSubscription(subscription, config);
            }
            if (subscription.appSettingsId == null) {
                continue;
            }

            subscription.numberOfInstancesAdded++;
            if(!include) {  
                subscription.app = null;
            }
            addedApps.put(config.appSettingsId, subscription);
        }
        

        return addedApps;
    }

    private void updateSubscription(ApplicationSubscription subscription, AppConfiguration config) throws ErrorException {
        if(subscription.to_date != null) {
            return;
        }
        Calendar cal = Calendar.getInstance();
        subscription.from_date = config.rowCreatedDate;
        cal.setTime(subscription.from_date);
        cal.add(Calendar.DAY_OF_YEAR, subscription.app.trialPeriode);
        subscription.to_date = cal.getTime();
        subscription.storeId = storeId;
        subscription.payedfor = false;
        saveSubscription(subscription);
    }

    private void saveSubscription(ApplicationSubscription subscription) throws ErrorException {
        databaseSaver.saveObject(subscription, credentials);
    }

    @Override
    public List<ApplicationSettings> getApplicationSettingsUsedByWebPage() throws ErrorException {
        List<AppConfiguration> apps = pageManager.getApplications();
        
        if (apps == null) {
            return new ArrayList<ApplicationSettings>();
        }
        
        Set<ApplicationSettings> settings = new HashSet();
        for (AppConfiguration app : apps) {
            ApplicationSettings setting = getApplication(app.appSettingsId);
            if (setting != null) {
                settings.add(setting);
            }
        }
        
        return new ArrayList(settings);
    }
}
