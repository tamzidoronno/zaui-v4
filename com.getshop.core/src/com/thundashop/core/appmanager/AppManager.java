package com.thundashop.core.appmanager;

import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.appmanager.data.AvailableApplications;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.data.PageArea;
import java.util.ArrayList;
import java.util.List;
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
    private AvailableApplications applications = new AvailableApplications();

    @Autowired
    private ApplicationPool applicationPool;
    
    @Autowired
    public AppManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataObject : data.data) {
            if (dataObject instanceof AvailableApplications) {
                applications = (AvailableApplications) dataObject;
            }
        }
    }

    @Override
    public List<ApplicationSettings> getAllApplications() throws ErrorException {
//        List<ApplicationSettings> appSettings = new ArrayList();
//        for (String appId : applications.applications) {
//            applicationPool.get(appId);
//        }
        
        return applicationPool.getAll();
//        return appSettings;
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
        applicationPool.addApplicationSettings(settings);
    }

    private void saveSettings(ApplicationSettings settings) throws ErrorException {
        String userid = getSession().currentUser.id;
        if (!settings.ownerStoreId.equals(getStore().id)) {
            throw new ErrorException(26);
        }
        settings.ownerStoreId = getStore().id;
        settings.userId = userid;
        databaseSaver.saveObject(settings, credentials);
    }

    @Override
    public void deleteApplication(String id) throws ErrorException {
        applicationPool.deleteApplication(id);
    }

    @Override
    public ApplicationSettings getApplication(String id) throws ErrorException {
        for (String appId : applications.applications) {
            if (appId.equals(id)) {
                return applicationPool.get(appId);
            }
        }
        return null;
    }

}
