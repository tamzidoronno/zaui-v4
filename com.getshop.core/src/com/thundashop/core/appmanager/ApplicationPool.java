/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.appmanager;

import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class ApplicationPool extends ManagerBase {

    public HashMap<String, ApplicationSettings> applications = new HashMap();

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataObject : data.data) {
            if (dataObject instanceof ApplicationSettings) {
                ApplicationSettings settings = (ApplicationSettings) dataObject;
                applications.put(settings.id, settings);
            }
        }
    }
    
    @PostConstruct
    public void init() {
        storeId = "all";
        initialize();
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
    
    private void addToAppList(ApplicationSettings setting, String storeId, List<ApplicationSettings> list) {
        list.add(setting);

    }

    public List<ApplicationSettings> getAll(String storeId) {
        updateApplicationSet();
        ArrayList<ApplicationSettings> list = new ArrayList(applications.values());

        //Getshop owns them all. this is the getshop id.
        if (storeId.equals("cdae85c1-35b9-45e6-a6b9-fd95c18bb291")) {
            Collections.sort(list, new ApplicationSettings());
            return list;
        }

        ArrayList<ApplicationSettings> returnlist = new ArrayList();
        for (ApplicationSettings settings : list) {
            
            if (settings.isPublic) {
                addToAppList(settings, storeId, returnlist);
            } else if (storeId.equals(settings.ownerStoreId) || settings.allowedStoreIds.contains(storeId)) {
                addToAppList(settings, storeId, returnlist);
            }
        }
        
        

        Collections.sort(returnlist, new ApplicationSettings());
        return returnlist;
    }
}