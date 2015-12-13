/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.getshop.scope.GetShopSessionScope;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ktonder
 */
public class ManagerSubBase {
    protected String storeId = "";
    protected Credentials credentials = null;
    
    @Autowired
    protected DatabaseSaver databaseSaver;
    
    @Autowired
    protected Logger log;
    
    @Autowired
    private GetShopSessionScope scope;
    
    protected boolean isSingleton = false;
    protected boolean ready = false;
    private Session session;

	private ManagerSetting managerSettings = new ManagerSetting();
    
    private Database database;

    @Autowired
    public void setDatabase(Database database) {
        this.database = database;
    }

    public Database getDatabase() {
        return database;
    }

    
    
    public ManagerSubBase() {
    }

    public void initialize() {
        Credentials credentials = new Credentials(this.getClass());
        credentials.manangerName = this.getClass().getSimpleName();
        credentials.password = UUID.randomUUID().toString();
        credentials.storeid = storeId;

        this.credentials = credentials;

        if (getSessionBasedName() != null) {
            credentials.manangerName += "_"+getSessionBasedName();
        }
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

            dataFromDatabase(dataRetreived);
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

    public String getSessionBasedName() {
        return null;
    }
    
    /**
     * This function is called after each interface call.
     * 
     * Gives you the possibility to change the data before it leaves the jvm container.
     * 
     * @param object
     * @param executeMethod
     * @return 
     */
    public Object preProcessMessage(Object object, Method executeMethod) {
        return object;
    }
    
    public String getStoreId() {
        return storeId;
    }
    
    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }
    
    public void setSession(Session session) {
        this.session = session;
    }
    
    public Credentials getCredentials() {
        return credentials;
    }
    
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
  
    
    protected <V> V deepClone(V object) {
        try {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ObjectOutputStream oos = new ObjectOutputStream(baos);
          oos.writeObject(object);
          ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
          ObjectInputStream ois = new ObjectInputStream(bais);
          return (V) ois.readObject();
        }
        catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      }
//        if (result == null) {
//            return result;
//        }
//        
//        Gson gson = new GsonBuilder().serializeNulls().disableInnerClassSerialization().create();
//        String json = gson.toJson((Object) result);
//        V retObject = (V)gson.fromJson(json, result.getClass());
//        
//        return retObject;
//    }
}
