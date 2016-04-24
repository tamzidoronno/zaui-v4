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
import com.thundashop.core.socket.CacheFactory;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
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
    
    @Autowired
    private CacheFactory cachingFactory;
    
    protected boolean isSingleton = false;
    protected boolean ready = false;
    
    private boolean sessionUsed = false;
    private Session session;
    
    private List<ManagerSubBase> otherManagersThisIsUsing = new ArrayList();

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
    
    public void createProcessor(String schedulerReference, Class schedulerType) {
        GetShopScheduler gsscheduler = new GetShopScheduler();
        gsscheduler.storeId = storeId;
        gsscheduler.schedulerClassName = schedulerType;
        gsscheduler.id = schedulerReference;
        gsscheduler.multilevelName = "";
        
        if (this instanceof GetShopSessionBeanNamed) {
            gsscheduler.multilevelName = ((GetShopSessionBeanNamed)this).getName();
        }
        
        startScheduler(gsscheduler, true);
        saveObject(gsscheduler);
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

    public Session getSessionSilent() {
        return session;
    }
    
    public Session getSession() {
        sessionUsed = true;    
        return session;
    }
    
    public boolean isSessionUsed() {
        return sessionUsed;
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
        boolean clearCache = database.save(data, credentials);
        
        if (clearCache) {
            clearCache(null);
        }
    }
 
    public void deleteObject(DataCommon data) throws ErrorException {
        if (getSession() != null && getSession().currentUser != null) {
            data.gsDeletedBy = getSession().currentUser.id;
        }
        
        boolean updateCache = database.delete(data, credentials);
        
        if (updateCache) {
            clearCache(null);
        }
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
        sessionUsed = false;
        this.session = session;
    }
    
    public Credentials getCredentials() {
        return credentials;
    }
    
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
  
    protected <V> V deepClone(V object) {
        if (object instanceof DataCommon) {
            try {
                V newObject = (V)((DataCommon)object).clone();
                ((DataCommon)newObject).deleted = new Date();
                return newObject;
            } catch (CloneNotSupportedException ex) {
                ex.printStackTrace();
            }
        }
        
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
                userManager = AppContext.appContext != null ? AppContext.appContext.getBean(UserManager.class) : null;
            }

            if (userManager == null) {
                return;
            }
            
            User user = userManager.getInternalApiUser();
            String webAddress = storePool.getStore(storeId).getDefaultWebAddress();
            
            Class<?> clazz = Class.forName(gsscheduler.schedulerClassName.getCanonicalName());
            Constructor<?> ctor = clazz.getConstructor(String.class,String.class,String.class,String.class, String.class);
            GetShopSchedulerBase ret = (GetShopSchedulerBase) ctor.newInstance(webAddress, user.username, user.metaData.get("password"), gsscheduler.scheduler, gsscheduler.multilevelName);

            schedulersBases.put(gsscheduler.id, ret);
            if(autostart) {
                new Thread(ret).start();
            } else {
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
    
    public String getStoreName() {
        return applicationPool.getApplication("d755efca-9e02-4e88-92c2-37a3413f3f41").getSetting("title");
    }
    
    public void clearCache(ClearCacheMessage msg) {
        if (cachingFactory == null || !ready)
            return;
        
        cachingFactory.clear(storeId, getClass().getSimpleName());    
        clearNotify(msg);
    }
    
    public void clearUsedSession() {
        sessionUsed = false;
    }

    private void clearNotify(ClearCacheMessage clearCacheMessage) {
        if (clearCacheMessage == null) {
            clearCacheMessage = new ClearCacheMessage();
        }
        
        clearCacheMessage.processedClasses.add(this.getClass());
        
        for (ManagerSubBase manager : otherManagersThisIsUsing) {
            manager.clearCacheMessage(clearCacheMessage);
        }
    }
    
    public void clearCacheMessage(ClearCacheMessage msg) {
        if (msg.processedClasses.contains(this.getClass())) {
            return;
        }
        
        clearCache(msg);
        msg.processedClasses.add(this.getClass());
        
        for (ManagerSubBase manager : otherManagersThisIsUsing) {
            manager.clearCacheMessage(msg);
        }
    }
 
    @PostConstruct
    public void buildDependencyMap() {
        List<Field> fileds = getAllFields(new LinkedList<Field>(), getClass());

        for (Field field : fileds) {
            if (ManagerSubBase.class.isAssignableFrom(field.getType())) {
                addManager(field);
            }
        }
    }

    private void addManager(Field field) throws SecurityException {
        try {
            field.setAccessible(true);
            ManagerSubBase manager = (ManagerSubBase) field.get(this);
            otherManagersThisIsUsing.add(manager);
        } catch (IllegalArgumentException ex) {
            java.util.logging.Logger.getLogger(ManagerSubBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManagerSubBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            fields = getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }
}