/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.appmanager;

import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class ApplicationPool extends ManagerBase {
    public HashMap<String, ApplicationSettings> applications = new HashMap();

    @Autowired
    public ApplicationPool(Logger log, DatabaseSaver databaseSaver, Database database) {
        super(log, databaseSaver);
        storeId = "all";
        this.database = database;
        initialize();
    }
    
    @Override
    public void dataFromDatabase(DataRetreived data) { 
        
        for (DataCommon dataObject : data.data) {
            if (dataObject instanceof ApplicationSettings) {
                ApplicationSettings settings = (ApplicationSettings)dataObject;
                applications.put(settings.id, settings);
            }
        }
        
    }
    
    public synchronized void addApplicationSettings(ApplicationSettings settings) throws ErrorException {
        settings.storeId = "all";
        databaseSaver.saveObject(settings, credentials);
        applications.put(settings.id, settings);
    }

    private void updateApplicationSet() {
        for (ApplicationSettings appSettings : applications.values()) {
            appSettings.complete();
        }
    }
    
    public synchronized ApplicationSettings get(String appId) {
        updateApplicationSet();
        return applications.get(appId);
    }

    public synchronized void deleteApplication(String id) throws ErrorException {
        ApplicationSettings settings;
        settings = applications.get(id);
        if (settings != null) {
            databaseSaver.deleteObject(settings, credentials);
            applications.remove(id);
        }
    }

    public List<ApplicationSettings> getAll(String storeid) {
        updateApplicationSet();
        ArrayList<ApplicationSettings> list = new ArrayList(applications.values());
        
        ArrayList<ApplicationSettings> returnlist = new ArrayList();
        for(ApplicationSettings settings : list) {
            if(settings.isPublic) {
                returnlist.add(settings);
            } else if(storeid.equals(settings.ownerStoreId)) {
                returnlist.add(settings);
            }
        }
        
        Collections.sort(returnlist, new ApplicationSettings());
        return returnlist;
    }
}