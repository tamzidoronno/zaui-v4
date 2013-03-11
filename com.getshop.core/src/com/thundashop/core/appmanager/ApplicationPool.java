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
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
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
                System.out.println("DATA: " + settings.id);
            }
        }
        
        System.out.println("DONE: " + data.data.size());
    }

    public synchronized void addApplicationSettings(ApplicationSettings settings) throws ErrorException {
        databaseSaver.saveObject(settings, credentials);
        applications.put(settings.id, settings);
    }

    public synchronized ApplicationSettings get(String appId) {
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

    public List<ApplicationSettings> getAll() {
        ArrayList<ApplicationSettings> list = new ArrayList(applications.values());
        System.out.println(list.size());
        return list;
    }
    
    
}
