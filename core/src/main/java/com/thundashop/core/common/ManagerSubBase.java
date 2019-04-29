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
import static com.thundashop.core.common.GetShopLogHandler.logPrintStatic;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.DatabaseRemote;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.ordermanager.data.VirtualOrder;
import com.thundashop.core.pagemanager.GetShopModules;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ktonder
 */
public class ManagerSubBase {
    protected String storeId = "";
    protected Credentials credentials = null;
    private String overrideCollectionName;
    
    @Autowired
    protected Logger log; 
    
    @Autowired
    public StoreApplicationPool applicationPool;
    
    @Autowired
    public com.thundashop.core.storemanager.StorePool storePool;
    
    @Autowired
    public FrameworkConfig frameworkConfig;
    
    private GetShopModules modules = new GetShopModules();
    
    protected boolean isSingleton = false;
    protected boolean ready = false;
//    private Session session;
    private Map<Long, Session> threadSessions = Collections.synchronizedMap(new HashMap<Long, Session>());

    private ManagerSetting managerSettings = new ManagerSetting();
    
    @Autowired
    protected Database database;
    
    @Autowired
    protected DatabaseRemote databaseRemote;
    
    
    private HashMap<String, GetShopScheduler> schedulers = new HashMap();
    private HashMap<String, GetShopSchedulerBase> schedulersBases = new HashMap();
    private boolean anyDataRetreived = false;

    public Database getDatabase() {
        return database;
    }

    public void setOverrideCollectionName(String overrideCollectionName) {
        this.overrideCollectionName = overrideCollectionName;
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
    
    public void logPrint(Object key) {
        if (key instanceof Exception) {
            logPrintException((Exception)key);
        } else {
            logPrintStatic(key, storeId);
        }
    }
    
    public void logPrintException(Exception ex) {
        GetShopLogHandler.logStack(ex, storeId);
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
    
    public void initialize() throws SecurityException {
        long start = System.currentTimeMillis();
        Credentials credentials = new Credentials(this.getClass());
        credentials.manangerName = this.getClass().getSimpleName();
        if (overrideCollectionName != null) {
            credentials.manangerName = overrideCollectionName;
        }
        credentials.password = UUID.randomUUID().toString();
        credentials.storeid = storeId;

        this.credentials = credentials;

        if (getSessionBasedName() != null) {
            credentials.manangerName += "_"+getSessionBasedName();
        }

        
        if (database == null) {
            throw new NullPointerException("No database saver?");
        }
        
        boolean databaseFunctionInUse = isDatabaseMethodInUse();
                
        if (database != null && !credentials.manangerName.equals("LoggerManager") && databaseFunctionInUse && shouldLoadData()) {
            DataRetreived dataRetreived = new DataRetreived();
            dataRetreived.data = database.retreiveData(credentials);
            
            for (DataCommon common : dataRetreived.data) {
                if (common instanceof ManagerSetting) {
                    this.managerSettings = (ManagerSetting)common;
                }
            }
    
            dataFromDatabase(dataRetreived);
            
            anyDataRetreived = anyNormalDataObject(dataRetreived.data);
            
            for (DataCommon common : dataRetreived.data) {
                if (common instanceof GetShopScheduler) {
                    GetShopScheduler sched = (GetShopScheduler)common;
                    startScheduler(sched, false);
                }
            }
        }
        
        this.ready = true;
        if (GetShopLogHandler.isDeveloper) {
            System.out.println("Started manager: " + this.getClass().getSimpleName() + " in " + (System.currentTimeMillis()-start) + "ms");
        }
    }

    private boolean isDatabaseMethodInUse() throws SecurityException {
        boolean dataFromDatabaseOverridden = false;
        try {
            Class[] cArg = new Class[1];
            cArg[0] = DataRetreived.class;
            dataFromDatabaseOverridden = this.getClass().getMethod("dataFromDatabase", cArg).getDeclaringClass() != ManagerSubBase.class;
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
        return dataFromDatabaseOverridden;
    }

    public Session getSession() {
        long threadId = Thread.currentThread().getId();
        return threadSessions.get(threadId);
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
                .replace("?", "_")
                .toLowerCase();
        
        newAddress = prefix + newAddress;
        
        return newAddress;
    }

    public void onEvent(String eventName, String eventReferance) throws ErrorException {
    }

    public void dataFromDatabase(DataRetreived data) {
    }

    public void saveObject(DataCommon data) throws ErrorException {
        if (modules.shouldStoreRemote(getCurrentGetShopModule())) {
            if (data.getClass().getAnnotation(GetShopRemoteObject.class) != null) {
                data.getshopModule = getCurrentGetShopModule();
                storeRemote(data);
                return;
            }
        }
        
        data.storeId = storeId;
        data.lastModified = new Date();
        
        try {
            if(getSession() != null && getSession().currentUser != null && getSession().currentUser.id != null) {
                data.lastModifiedByUserId = getSession().currentUser.id;
            }
        }catch(Exception e) {
            logPrintException(e);
        }
        
        if(getSession() != null) {
            String lang = getSession().language;
            data.validateTranslationMatrix();
            data.updateTranslation(lang);
        }
        
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
//        this.session = session;
        long threadId = Thread.currentThread().getId();

        if (session == null) {
            threadSessions.remove(threadId);
        } else {
            threadSessions.put(threadId, session);
        }
    }
    
    public Credentials getCredentials() {
        return credentials;
    }
    
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
  
    protected <T> T deepClone(T object) {
        if (object instanceof DataCommon) {
            try {
                T ret = (T) ((DataCommon)object).clone();
                return ret;
            } catch (CloneNotSupportedException ex) {
                java.util.logging.Logger.getLogger(ManagerSubBase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ObjectOutputStream oos = new ObjectOutputStream(baos);
          oos.writeObject(object);
          ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
          ObjectInputStream ois = new ObjectInputStream(bais);
          
          T res = (T) ois.readObject();
          baos.close();
          oos.close();
          bais.close();
          ois.close();
          
          return res;
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
        
        if (!anyDataRetreived) {
            return;
        }
        
        try {
            UserManager userManager = null;

            if (this instanceof UserManager) {
                userManager = (UserManager)this;
            } else {
                userManager = AppContext.appContext != null ? AppContext.appContext.getBean(UserManager.class) : null;
            }

            if (userManager == null)
                return;
            
            User user = userManager.getInternalApiUser();
            
            if (storePool.getStore(storeId) == null)
                return;
            
            String webAddress = storePool.getStore(storeId).getDefaultWebAddress();

            if(autostart) {
                GetShopSchedulerBase base = (GetShopSchedulerBase)gsscheduler.getShopSchedulerBase;
                base.setWebAddress(webAddress);
                base.setUsername(user.username);
                base.setPassword(user.internalPassword);
                base.setMultiLevelName(gsscheduler.multilevelName);
                base.setStoreId(storeId);
                Thread td = new Thread(base);
                td.setName("Direct Scheduler Thread: " + storeId + ", scheduler: " + base);
                td.start();
            } else {
                Class<?> clazz = Class.forName(gsscheduler.schedulerClassName.getCanonicalName());
                Constructor<?> ctor = clazz.getConstructor(String.class,String.class,String.class,String.class, String.class);
                GetShopSchedulerBase ret = (GetShopSchedulerBase) ctor.newInstance(webAddress, user.username, user.internalPassword, gsscheduler.scheduler, gsscheduler.multilevelName);
                ret.setStoreId(storeId);
                
                schedulersBases.put(gsscheduler.id, ret);
                schedulers.put(gsscheduler.id, gsscheduler);
            }
        } catch (Exception ex) {
            GetShopLogHandler.logPrintStatic("Could not start scheduler", null);
            ex.printStackTrace();
        }
    }
    
    public String getStoreDefaultAddress() {
        String addre = storePool.getStore(storeId).getDefaultWebAddress();
        
        if (!frameworkConfig.productionMode) {
            addre = addre.replace(".getshop", ".3.0.local.getshop");
        }
        
        return addre;
        
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
    
    public void gsTiming(String description) {
        GetShopTimer.timeEntry(description, getClass().getSimpleName());
    }

    private void storeRemote(DataCommon data) {
        data.storeId = "all_"+getCurrentGetShopModule();
        data.lastModified = new Date();
        
        try {
            if(getSession() != null && getSession().currentUser != null && getSession().currentUser.id != null) {
                data.lastModifiedByUserId = getSession().currentUser.id;
            }
        }catch(Exception e) {
            logPrintException(e);
        }
        
        if(getSession() != null) {
            String lang = getSession().language;
            data.validateTranslationMatrix();
            data.updateTranslation(lang);
        }
        
        databaseRemote.save(data, credentials);
    }
    
    public String getCurrentGetShopModule() {
        if (getSession() == null || getSession().get("currentGetShopModule") == null) {
            return "cms";
        }
        
        String moduleId = (String) getSession().get("currentGetShopModule");
        if (moduleId.isEmpty()) {
            return "cms";
        }
        
        return moduleId;
    }
    
    public boolean isCmsModule() {
        if (getSession() == null || getSession().get("currentGetShopModule") == null) {
            return true;
        }
        
        String currentModule = (String)getSession().get("currentGetShopModule");
            
        if (currentModule.isEmpty() || currentModule.equals("cms")) {
            return true;
        }
        
        return false;
    }

    private boolean anyNormalDataObject(List<DataCommon> data) {
        for (DataCommon i : data) {
            if (!(i instanceof GetShopScheduler)) {
                return true;
            }
        }
        
        return false;
    }

    public boolean shouldLoadData() {
        return true;
    }
    
    public Store getStore() {
        return storePool.getStore(storeId);
    }

    /**
     * @param executeMethod
     * @param argObjects 
     */
    public void postProcessMessage(Method executeMethod, Object[] argObjects) {}

    
}
