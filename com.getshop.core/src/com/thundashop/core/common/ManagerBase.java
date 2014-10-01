/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.google.gson.Gson;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.IPageManager;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.data.Store;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ktonder
 */
public class ManagerBase {
    public String storeId = "";
    public Credentials credentials = null;
    public DatabaseSaver databaseSaver;
    public Logger log;
    public boolean isSingleton = false;
    public boolean ready = false;
    public Session session;

    @Autowired
    public Database database;
    private ArrayList parsedObjects;

    public ManagerBase(Logger log, DatabaseSaver databaseSaver) {
        this.databaseSaver = databaseSaver;
        this.log = log;
    }

    public void initialize() {
        Credentials credentials = new Credentials(this.getClass());
        credentials.manangerName = this.getClass().getSimpleName();
        credentials.password = UUID.randomUUID().toString();
        credentials.storeid = storeId;

        this.credentials = credentials;

        if (databaseSaver != null && database != null && !credentials.manangerName.equals("LoggerManager")) {
            DataRetreived dataRetreived = new DataRetreived();
            dataRetreived.data = database.retreiveData(credentials);
            ((ManagerBase) this).dataFromDatabase(dataRetreived);
        }

        try {
            setStoreIdToMailFactories();
        } catch (Exception ex) {
            log.error(this, "Could not set storeid to mailfactory", ex);
        }

        onReady();
        this.ready = true;
    }
    
    public void onReady() {
    }

    public HashMap<String, Setting> getSettings(String phpApplicationName) throws ErrorException {
        IPageManager pageManager = getManager(IPageManager.class);
        return pageManager.getApplicationSettings(phpApplicationName);
    }

    public Session getSession() {
        return session;
    }

    public <T> T getManager(Class managerType) {
        if (getSession() == null) {
            throw new NullPointerException("This function cat not be used from a component is not store");
        }

        String storeId = getSession().storeId;

        // TODO : Use storeId from userloggedin object.
        if (storeId == null || storeId.equals("") || storeId.equals("all")) {
            throw new NullPointerException("This function cat not be used from a component is not store");
        }

        StoreHandler storeHandler = AppContext.storePool.getStorePool(storeId);
        return storeHandler.getManager(managerType);
    }

    public <T> T getManager(Class managerType, String storeId) {
        StoreHandler storeHandler = AppContext.storePool.getStorePool(storeId);
        return storeHandler.getManager(managerType);
    }

    public Store getStore() throws ErrorException {
        StoreManager storeMgr = getManager(StoreManager.class);
        return storeMgr.getMyStore();
    }

    public void setStoreIdToMailFactories() throws IllegalArgumentException, IllegalAccessException {
        for (Field field : this.getClass().getFields()) {
            if (field.get(this) instanceof StoreComponent) {
                StoreComponent storeComponent = (StoreComponent) field.get(this);
                storeComponent.setStoreId(storeId);
            }
        }
    }

    public void throwEvent(String eventName, String eventReferance) {
        StoreHandler storeHandler = AppContext.storePool.getStorePool(storeId);
        storeHandler.sendEvent(this, eventName, eventReferance);
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

        return name.replace(" ", "_").replace("&", "_").replace("/", "_").replace("\\", "_").toLowerCase();
    }

    public void onEvent(String eventName, String eventReferance) throws ErrorException {
    }

    public void dataFromDatabase(DataRetreived data) {
    }

    public void saveObject(DataCommon data) throws ErrorException {
        parsedObjects = new ArrayList();
        updateTranslation(data, false);
        data.storeId = storeId;
        databaseSaver.saveObject(data, credentials);
    }
    
    
    /**
     * @param object
     * @param loading If you are loading the object, or if you are saving it.
     * @throws ErrorException 
     */
    public void updateTranslation(Object object, boolean loading) throws ErrorException {
		
		// If there is no session there is most likely no translation to update?
		if (getSession() == null) {
			return;
		}
		
        HashMap<String, Setting> settings = getSettings("Settings");
        if(settings != null && settings.containsKey("languages")) {
            Gson sgon = new Gson();
            Setting langsetting = settings.get("languages");
            List<String> langcodes = sgon.fromJson(langsetting.value, ArrayList.class);
            
            if (langcodes == null) {
                return;
            }
            
            if(langcodes.size() > 0) {
                TranslationHelper helper = new TranslationHelper(getSession().language, getMainLanguage());
                helper.checkFields(object, loading);
            }
        }
    }

    public void deleteObject(DataCommon data) throws ErrorException {
        databaseSaver.deleteObject(data, credentials);
    }

    public String getMainLanguage() throws ErrorException {
        String standardlang = "en_en";
        HashMap<String, Setting> settings = getSettings("Settings");
        if (settings.containsKey("language")) {
            standardlang = settings.get("language").value;
        }

        return standardlang;
    }
}
