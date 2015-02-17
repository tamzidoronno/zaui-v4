/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.getshop.scope.GetShopSessionScope;
import com.google.gson.Gson;
import com.thundashop.app.contentmanager.data.ContentData;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.PageManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class ManagerBase {
    public String storeId = "";
    public Credentials credentials = null;
    
    @Autowired
    public DatabaseSaver databaseSaver;
    
    @Autowired
    public Logger log;
    
    @Autowired
    private GetShopSessionScope scope;
    
    public boolean isSingleton = false;
    public boolean ready = false;
    public Session session;

	private ManagerSetting managerSettings = new ManagerSetting();
    
    private Database database;

    @Autowired
    public void setDatabase(Database database) {
        this.database = database;
    }

    public Database getDatabase() {
        return database;
    }

    
    
    public ManagerBase() {
    }

    public void initialize() {
        Credentials credentials = new Credentials(this.getClass());
        credentials.manangerName = this.getClass().getSimpleName();
        credentials.password = UUID.randomUUID().toString();
        credentials.storeid = storeId;

        this.credentials = credentials;

        if (databaseSaver == null) {
            throw new NullPointerException("No database saver?");
        }
        
        if (database == null) {
            throw new NullPointerException("No database saver?");
        }
        
        if (databaseSaver != null && database != null && !credentials.manangerName.equals("LoggerManager")) {
            DataRetreived dataRetreived = new DataRetreived();
            dataRetreived.data = database.retreiveData(credentials);
            
            for (DataCommon common : dataRetreived.data) {
                if (common instanceof ManagerSetting) {
                    this.managerSettings = (ManagerSetting)common;
                }
            }

            ((ManagerBase) this).dataFromDatabase(dataRetreived);
        }
		
        this.ready = true;
    }

    public Session getSession() {
        return session;
    }

    /**
     * Dont use this, override onEvent. this is for logging purposes only!
     *
     * @param eventName
     * @param eventReferance
     */
    public void onEventPrivate(String eventName, String eventReferance) {
        try {
            onEvent(eventName, eventReferance);
        } catch (ErrorException ex) {
            log.error(this, "Failed to run event for manager "
                    + this.getClass().getSimpleName()
                    + ", event : "
                    + eventName
                    + " reference: "
                    + eventReferance, ex);
        }
    }

    public String makeSeoUrl(String name) {
        if (name == null) {
            return "";
        }

        String newAddress = 
                name.replace(" ", "_")
                .replace("&", "_")
                .replace("/", "_")
                .replace("\\", "_")
                .replace("\"", "_")
                .replace("'", "_")
                .toLowerCase();
        
        return newAddress;
    }

    public void onEvent(String eventName, String eventReferance) throws ErrorException {
    }

    public void dataFromDatabase(DataRetreived data) {
    }

    public void saveObject(DataCommon data) throws ErrorException {
        updateTranslation(data, false);
        data.storeId = storeId;
        databaseSaver.saveObject(data, credentials);
    }
 
    public void deleteObject(DataCommon data) throws ErrorException {
        databaseSaver.deleteObject(data, credentials);
    }

    protected void setManagerSetting(String key, String value) {
        managerSettings.keys.put(key, value);
        saveObject(managerSettings);
    }

    protected String getManagerSetting(String key) {
        return managerSettings.keys.get(key);
    }

    public void updateTranslation(Object data, boolean loading) {
        StoreApplicationPool storeApplicationPool = (StoreApplicationPool)scope.getManagerBasedOnNameAndStoreId("scopedTarget.storeApplicationPool", storeId);
        
        // If there is no session there is most likely no translation to update?
        if (getSession() == null || storeApplicationPool == null || data == null) {
            return;
        }

        Application settingsApplication = storeApplicationPool.getApplication("d755efca-9e02-4e88-92c2-37a3413f3f41");
        if (settingsApplication == null) {
            return;
        }
       
        
        
        HashMap<String, Setting> settings = settingsApplication.settings;
        
        if(settings != null && settings.containsKey("languages")) {
            Gson sgon = new Gson();
            Setting langsetting = settings.get("languages");
            List<String> langcodes = sgon.fromJson(langsetting.value, ArrayList.class);
            
            if (langcodes == null) {
                return;
            }
            
            if(langcodes.size() > 0) {
                TranslationHelper helper = new TranslationHelper(getSession().language, "nb_NO");
                
                helper.checkFields(data, loading);
            }
        }

    }
}
