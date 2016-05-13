/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.getshop.scope.GetShopSchedulerBase;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.getshop.scope.GetShopSessionScope;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
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
    protected Logger log; 
    
    @Autowired
    private GetShopSessionScope scope;
    
    @Autowired
    public StoreApplicationPool applicationPool;
    
    @Autowired
    public com.thundashop.core.storemanager.StorePool storePool;
    
    protected boolean isSingleton = false;
    protected boolean ready = false;
    private Session session;

    private ManagerSetting managerSettings = new ManagerSetting();
    
    @Autowired
    protected Database database;
    private HashMap<String, GetShopScheduler> schedulers = new HashMap();
    private HashMap<String, GetShopSchedulerBase> schedulersBases = new HashMap();

    
    
    public Database getDatabase() {
        return database;
    }

    public Application getStoreSettingsApplication() {
        Application settingsApplication = applicationPool.getApplication("d755efca-9e02-4e88-92c2-37a3413f3f41");
        return settingsApplication;
    }
    
    public String getStoreSettingsApplicationKey(String key) {
        String result = getStoreSettingsApplication().getSetting(key);
        return result;
    }

    
    public ManagerSubBase() {
    }

    public void createScheduler(String schedulerReference, String scheduler, Class schedulerType) {
        GetShopScheduler gsscheduler = new GetShopScheduler();
        gsscheduler.storeId = storeId;
        gsscheduler.schedulerClassName = schedulerType;
        gsscheduler.id = schedulerReference;
        gsscheduler.scheduler = scheduler;
        gsscheduler.multilevelName = "";
        
        if (this instanceof GetShopSessionBeanNamed) {
            gsscheduler.multilevelName = ((GetShopSessionBeanNamed)this).getName();
        }
        
        startScheduler(gsscheduler, false);
        saveObject(gsscheduler);
    }
    
    public void createProcessor(GetShopSchedulerBase process) {
        GetShopScheduler gsscheduler = new GetShopScheduler();
        gsscheduler.storeId = storeId;
        gsscheduler.getShopSchedulerBase = process;
        
        if (this instanceof GetShopSessionBeanNamed) {
            gsscheduler.multilevelName = ((GetShopSessionBeanNamed)this).getName();
        }
        
        startScheduler(gsscheduler, true);
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

        
        if (database == null) {
            throw new NullPointerException("No database saver?");
        }
        
        if (database != null && !credentials.manangerName.equals("LoggerManager")) {
            DataRetreived dataRetreived = new DataRetreived();
            dataRetreived.data = database.retreiveData(credentials);
            
            for (DataCommon common : dataRetreived.data) {
                if (common instanceof ManagerSetting) {
                    this.managerSettings = (ManagerSetting)common;
                }
            }
            
            dataFromDatabase(dataRetreived);
            
            for (DataCommon common : dataRetreived.data) {
                if (common instanceof GetShopScheduler) {
                    GetShopScheduler sched = (GetShopScheduler)common;
                    startScheduler(sched, false);
                }
            }
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

    public String makeSeoUrl(String name, String prefix) {
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
        
        newAddress = prefix + newAddress;
        
        return newAddress;
    }

    public void onEvent(String eventName, String eventReferance) throws ErrorException {
    }

    public void dataFromDatabase(DataRetreived data) {
    }

    public void saveObject(DataCommon data) throws ErrorException {
        data.storeId = storeId;
        database.save(data, credentials);
    }
 
    public void deleteObject(DataCommon data) throws ErrorException {
        if (getSession() != null && getSession().currentUser != null) {
            data.gsDeletedBy = getSession().currentUser.id;
        }
        database.delete(data, credentials);
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
    
    public void stopScheduler(String reference) {
        GetShopScheduler ref = schedulers.remove(reference);
        GetShopSchedulerBase ref2 = schedulersBases.remove(reference);

        if (ref != null) {
            deleteObject(ref);
        }
        
        if (ref2 != null) {
            ref2.stop();
        }
    }

    private void startScheduler(GetShopScheduler gsscheduler, boolean autostart) {
        if (schedulers.containsKey(gsscheduler.id)) {
            return;
        }
        
        try {
            UserManager userManager = null;

            if (this instanceof UserManager) {
                userManager = (UserManager)this;
            } else {
                userManager = AppContext.appContext.getBean(UserManager.class);
            }

            User user = userManager.getInternalApiUser();
            String webAddress = storePool.getStore(storeId).getDefaultWebAddress();

            if(autostart) {
                GetShopSchedulerBase base = (GetShopSchedulerBase)gsscheduler.getShopSchedulerBase;
                base.setWebAddress(webAddress);
                base.setUsername(user.username);
                base.setPassword(user.metaData.get("password"));
                base.setMultiLevelName(gsscheduler.multilevelName);
                new Thread(base).start();
            } else {
                Class<?> clazz = Class.forName(gsscheduler.schedulerClassName.getCanonicalName());
                Constructor<?> ctor = clazz.getConstructor(String.class,String.class,String.class,String.class, String.class);
                GetShopSchedulerBase ret = (GetShopSchedulerBase) ctor.newInstance(webAddress, user.username, user.metaData.get("password"), gsscheduler.scheduler, gsscheduler.multilevelName);

                schedulersBases.put(gsscheduler.id, ret);
                schedulers.put(gsscheduler.id, gsscheduler);
            }
        } catch (Exception ex) {
            System.out.println("Could not start scheduler");
            ex.printStackTrace();
        }
    }
    
    public String getStoreDefaultAddress() {
        return storePool.getStore(storeId).getDefaultWebAddress();
    }
    
    public String getStoreEmailAddress() {
        return storePool.getStore(storeId).configuration.emailAdress;
    }
    
    public String getStoreDefaultPrefix() {
        return ""+storePool.getStore(storeId).configuration.defaultPrefix;
    }
    
    public String getStoreName() {
        return applicationPool.getApplication("d755efca-9e02-4e88-92c2-37a3413f3f41").getSetting("title");
    }
}
