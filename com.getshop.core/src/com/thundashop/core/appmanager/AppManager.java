package com.thundashop.core.appmanager;

import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.data.PageArea;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        List<ApplicationSettings> appSettings = new ArrayList();
        for (String userId : settings.keySet()) {
            appSettings.addAll(settings.get(userId).values());
        }

        if (appSettings.size() > 0) {
            Collections.sort(appSettings, new Comparator<ApplicationSettings>() {
                @Override
                public int compare(final ApplicationSettings object1, final ApplicationSettings object2) {
                    return object1.appName.compareTo(object2.appName);
                }
            });
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
        if (settings.get(setting.userId) == null) {
            settings.put(setting.userId, new TreeMap());
        }

        TreeMap<String, ApplicationSettings> map = settings.get(setting.userId);
        map.put(setting.id, setting);
    }

    private void saveSettings(ApplicationSettings settings) throws ErrorException {
        String userid = getSession().currentUser.id;
        settings.storeId = getStore().id;
        settings.userId = userid;
        databaseSaver.saveObject(settings, credentials);
    }

    private TreeMap<String, ApplicationSettings> getApplications() {
        String userid = getSession().currentUser.id;
        if (settings.get(userid) == null) {
            return new TreeMap();
        }

        return settings.get(userid);
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
        if (settings.get(getSession().currentUser.id) == null) {
            throw new ErrorException(1015);
        }
        String userid = getSession().currentUser.id;
        ApplicationSettings setting = settings.get(userid).get(id);
        if (setting == null) {
            throw new ErrorException(1015);
        }
        return setting;
    }
}
