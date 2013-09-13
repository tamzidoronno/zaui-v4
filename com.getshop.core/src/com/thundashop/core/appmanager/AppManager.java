package com.thundashop.core.appmanager;

import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.appmanager.data.ApplicationSubscription;
import com.thundashop.core.appmanager.data.ApplicationSynchronization;
import com.thundashop.core.appmanager.data.AvailableApplications;
import com.thundashop.core.common.AppConfiguration;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Events;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.PageArea;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The appmanager manage and keep track of the applications in getshop.
 * Each application has an appsettingsid, and an instanceid. The appsettings
 * id is the id of the main application, the instanceid is the id of the instances to the application.
 */
@Component
@Scope("prototype")
public class AppManager extends ManagerBase implements IAppManager {

    public List<ApplicationSynchronization> toSync;
    private Date lastConnected = new Date();
    Map<String, ApplicationSubscription> addedApps;
    private UnpayedAppCache cache;

//    TODO
//    US this variable to retreive data.
//    private AvailableApplications applications = new AvailableApplications();
    @Autowired
    private ApplicationPool applicationPool;

    @Autowired
    public AppManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
        addedApps = new HashMap();
    }

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
        String partnerid = getStore().partnerId;
        if(getSession() != null && getSession().currentUser != null && getSession().currentUser.partnerid != null) {
            partnerid = getSession().currentUser.partnerid;
        }
        retMessage.applications = applicationPool.getAll(getStore(), partnerid);
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
        System.out.println("id not found :" + id);
        throw new ErrorException(18);
    }

    @Override
    public void setSyncApplication(String id) throws ErrorException {
        ApplicationSettings app = this.getApplication(id);
        ApplicationSynchronization sync = new ApplicationSynchronization();
        sync.appId = app.id;
        sync.userId = getSession().currentUser.id;
        sync.appName = app.appName;
        addToSync(sync);
        sync.storeId = storeId;
        databaseSaver.saveObject(sync, credentials);
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
        PageManager pagemanager = this.getManager(PageManager.class);
        for (ApplicationSubscription sub : addedApps.values()) {
            sub.numberOfInstancesAdded = 0;
        }

        for (AppConfiguration config : pagemanager.getApplications()) {
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
    public List<ApplicationSubscription> getUnpayedSubscription() throws ErrorException {
        Store store = getStore();
        if(store.isVIS) {
            return new ArrayList();
        }
        List<ApplicationSubscription> result = new ArrayList();
        if (cache != null) {
            if (cache.expire.after(new Date())) {
                return cache.cache;
            }
        }

        for (ApplicationSubscription apsub : getAllApplicationSubscriptions(true).values()) {
            if (!apsub.payedfor && apsub.to_date.before(new Date()) && apsub.numberOfInstancesAdded > 0) {
                if (apsub.app.price != null && apsub.app.price > 0) {
                    result.add(apsub);
                }
            }
        }

        cache = new UnpayedAppCache();
        cache.cache = result;

        Calendar expire = Calendar.getInstance();
        expire.setTime(new Date());
        expire.add(Calendar.SECOND, 30);
        cache.expire = expire.getTime();

        return result;
    }

    public void renewAllApplications(String password) throws ErrorException {
        if (password.equals("fdder9bbvnfif909ereXXff")) {
            for (ApplicationSubscription sub : getUnpayedSubscription()) {
                sub.from_date = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(sub.from_date);
                cal.add(Calendar.YEAR, 1);
                sub.to_date = cal.getTime();
                sub.payedfor = true;
                saveSubscription(sub);
            }
            cache = null;
        }
    }
    
    @Override
    public void onEvent(String eventName, String eventReferance) throws ErrorException {
        if (Events.ALL_APPS_REMOVED.equals(eventName)) {
            cache = null;
        }
    }
}
