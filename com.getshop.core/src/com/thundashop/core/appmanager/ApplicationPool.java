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
import com.thundashop.core.getshop.GetShop;
import com.thundashop.core.storemanager.data.Store;
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
    public GetShop getshop;
    
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
                ApplicationSettings settings = (ApplicationSettings) dataObject;
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
    
    private void addToAppList(ApplicationSettings setting, Store store, List<ApplicationSettings> list) {
        List<String> partnerapps = getshop.getPartnerApplicationList(store.partnerId);
        if(store.id.equals("6acac00e-ef8a-4213-a75b-557c5d1cd150") || store.partnerId.equalsIgnoreCase("getshop")) {
            //Ignore filtering applications when the partner portal is asking for applications.
            list.add(setting);
        } else if(partnerapps.contains(setting.id) || setting.type.equals(ApplicationSettings.Type.System) || 
                setting.appName.equals("PayPal") || 
                setting.appName.equals("ProductManager") ||
                setting.appName.equals("ProductList") ||
                setting.appName.equals("ImageDisplayer") ||
                setting.appName.equals("LeftMenu") ||
                setting.appName.equals("Footer") ||
                setting.appName.equals("ContentManager") ||
                setting.appName.equals("SlickTheme")) 
        {
            list.add(setting);
        }
    }

    public List<ApplicationSettings> getAll(Store store, String partnerid) {
        updateApplicationSet();
        ArrayList<ApplicationSettings> list = new ArrayList(applications.values());

        //Getshop owns them all. this is the getshop id.
        if (store.id.equals("cdae85c1-35b9-45e6-a6b9-fd95c18bb291")) {
            Collections.sort(list, new ApplicationSettings());
            return list;
        }

        ArrayList<ApplicationSettings> returnlist = new ArrayList();
        for (ApplicationSettings settings : list) {
            
            if (settings.isPublic) {
                addToAppList(settings, store, returnlist);
            } else if (store.id.equals(settings.ownerStoreId) || settings.allowedStoreIds.contains(store.id) || settings.allowedStoreIds.contains(partnerid)) {
                addToAppList(settings, store, returnlist);
            }
        }
        
        

        Collections.sort(returnlist, new ApplicationSettings());
        return returnlist;
    }
}