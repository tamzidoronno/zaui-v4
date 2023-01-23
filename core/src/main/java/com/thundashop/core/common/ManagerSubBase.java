/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.getshop.scope.GetShopSchedulerBase;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.thundashop.constant.GetShopSchedulerBaseType;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.DatabaseRemote;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.GetShopModules;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.repository.utils.SessionInfo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import static com.thundashop.core.common.GetShopLogHandler.logPrintStatic;

/**
 * @author ktonder
 */
public class ManagerSubBase {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ManagerSubBase.class);

    protected String storeId = "";
    protected Credentials credentials = null;
    protected boolean isSingleton = false;
    protected boolean ready = false;

    private final HashMap<String, GetShopScheduler> schedulers = new HashMap<>();
    private final HashMap<String, GetShopSchedulerBase> schedulersBases = new HashMap<>();
    private final Map<Long, Session> threadSessions = new ConcurrentHashMap<>();

    private final GetShopModules modules = new GetShopModules();
    private ManagerSetting managerSettings = new ManagerSetting();

    private boolean anyDataRetrieved = false;
    private String overrideCollectionName;

    @Autowired
    protected Logger log;

    @Autowired
    public StoreApplicationPool applicationPool;

    @Autowired
    public com.thundashop.core.storemanager.StorePool storePool;

    @Autowired
    public FrameworkConfig frameworkConfig;

    @Autowired
    protected Database database;

    @Autowired
    protected DatabaseRemote databaseRemote;

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
        createScheduler(schedulerReference, scheduler, schedulerType, anyDataRetrieved);
    }

    protected void createScheduler(GetShopSchedulerBaseType getShopSchedulerBaseType) {
        createScheduler(getShopSchedulerBaseType.name, getShopSchedulerBaseType.time, getShopSchedulerBaseType.className);
    }


    public void createScheduler(String schedulerReference, String scheduler, Class schedulerType,
                                boolean anyDataRetrieved) {
        // need to set this true for starting any schedular
        this.anyDataRetrieved = anyDataRetrieved;
        GetShopScheduler gsscheduler = new GetShopScheduler();
        gsscheduler.storeId = storeId;
        gsscheduler.schedulerClassName = schedulerType;
        gsscheduler.id = schedulerReference;
        gsscheduler.scheduler = scheduler;
        gsscheduler.multilevelName = "";

        if (this instanceof GetShopSessionBeanNamed) {
            gsscheduler.multilevelName = ((GetShopSessionBeanNamed) this).getName();
        }

        startScheduler(gsscheduler, false);
        saveObject(gsscheduler);
    }

    public void logPrint(Object key) {
        if (key instanceof Exception) {
            logPrintException((Exception) key);
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
            gsscheduler.multilevelName = ((GetShopSessionBeanNamed) this).getName();
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
            credentials.manangerName += "_" + getSessionBasedName();
        }

        if (database == null) {
            throw new NullPointerException("No database saver?");
        }

        boolean databaseFunctionInUse = isDatabaseMethodInUse();

        if (database != null && !credentials.manangerName.equals("LoggerManager") && databaseFunctionInUse
                && shouldLoadData()) {
            DataRetreived dataRetreived = new DataRetreived();
            dataRetreived.data = retreiveData(credentials);

            for (DataCommon common : dataRetreived.data) {
                if (common instanceof ManagerSetting) {
                    this.managerSettings = (ManagerSetting) common;
                }
            }

            dataFromDatabase(dataRetreived);

            anyDataRetrieved = anyNormalDataObject(dataRetreived.data);

            for (DataCommon common : dataRetreived.data) {
                if (common instanceof GetShopScheduler) {
                    GetShopScheduler sched = (GetShopScheduler) common;
                    startScheduler(sched, false);
                }
            }
        }

        this.ready = true;

        // Feature flag?
        for (Class oneTimeExectors : getOneTimExecutors()) {

            try {
                OneTimeExecutor v = (OneTimeExecutor) oneTimeExectors.getConstructor(ManagerSubBase.class)
                        .newInstance(this);

                if (hasBeenExecuted(oneTimeExectors) && !v.forceExecute()) {
                    continue;
                }
                v.run();
                retgisterExecuted(oneTimeExectors);
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(ManagerSubBase.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        logger.info("Started manager: {} in {} ms", this.getClass().getSimpleName(),
                (System.currentTimeMillis() - start));
    }

    private boolean isDatabaseMethodInUse() throws SecurityException {
        boolean dataFromDatabaseOverridden = false;
        try {
            Class[] cArg = new Class[1];
            cArg[0] = DataRetreived.class;
            dataFromDatabaseOverridden = this.getClass().getMethod("dataFromDatabase", cArg)
                    .getDeclaringClass() != ManagerSubBase.class;
        } catch (NoSuchMethodException ex) {
            logger.error("storeId-{}", storeId, ex);
        }
        return dataFromDatabaseOverridden;
    }

    public Session getSession() {
        long threadId = Thread.currentThread().getId();
        return threadSessions.get(threadId);
    }

    public String makeSeoUrl(String name, String prefix) {
        if (name == null) {
            return "";
        }

        String newAddress = name.replace(" ", "_")
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

    public void dataFromDatabase(DataRetreived data) {
    }

    public List<DataCommon> retreiveData(Credentials credentials) {
        return database.retreiveData(credentials);
    }

    public List<DataCommon> retreiveData(Credentials credentials, BasicDBObject additionalQuery) {
        return database.retreiveData(credentials, additionalQuery);
    }

    /**
     * This function should only be used when the saveObject is overridden
     * and there is a special reason to bypass it.
     *
     * @param data
     */
    public void saveObjectDirect(DataCommon data) {
        internalSave(data);
    }

    public void saveObject(DataCommon data) throws ErrorException {
        if (modules.shouldStoreRemote(getCurrentGetShopModule())) {
            if (data.getClass().getAnnotation(GetShopRemoteObject.class) != null) {
                data.getshopModule = getCurrentGetShopModule();
                storeRemote(data);
                return;
            }
        }

        internalSave(data);
    }

    private void internalSave(DataCommon data) throws ErrorException {
        data.storeId = storeId;
        data.lastModified = new Date();

        try {
            if (getSession() != null && getSession().currentUser != null && getSession().currentUser.id != null) {
                data.lastModifiedByUserId = getSession().currentUser.id;
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        if (getSession() != null) {
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
     * <p>
     * Gives you the possibility to change the data before it leaves the jvm
     * container.
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
                T ret = (T) ((DataCommon) object).clone();
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
        } catch (Exception e) {
            logger.error("", e);
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

        if (!anyDataRetrieved) {
            return;
        }

        try {
            UserManager userManager;

            if (this instanceof UserManager) {
                userManager = (UserManager) this;
            } else {
                userManager = AppContext.appContext != null ? AppContext.appContext.getBean(UserManager.class) : null;
            }

            if (userManager == null)
                return;

            if (storePool.getStore(storeId) == null)
                return;

            User user = userManager.getInternalApiUser();

            String webAddress = storePool.getStore(storeId).getDefaultWebAddress();

            if (autostart) {
                GetShopSchedulerBase base = (GetShopSchedulerBase) gsscheduler.getShopSchedulerBase;
                base.setWebAddress(webAddress);
                base.setUsername(user.username);
                base.setPassword(UserManager.internalApiUserPassword);
                base.setMultiLevelName(gsscheduler.multilevelName);
                base.setStoreId(storeId);
                // TODO: ThreadPool might useful
                Thread td = new Thread(base);
                td.setName("Direct Scheduler Thread: " + storeId + ", scheduler: " + base);
                td.start();
            } else {
                Class<?> clazz = Class.forName(gsscheduler.schedulerClassName.getCanonicalName());
                Constructor<?> ctor = clazz.getConstructor(String.class, String.class, String.class, String.class,
                        String.class);
                GetShopSchedulerBase ret = (GetShopSchedulerBase) ctor.newInstance(webAddress, user.username,
                        UserManager.internalApiUserPassword, gsscheduler.scheduler, gsscheduler.multilevelName);
                ret.setStoreId(storeId);

                schedulersBases.put(gsscheduler.id, ret);
                schedulers.put(gsscheduler.id, gsscheduler);
            }
        } catch (Exception ex) {
            logger.error("storeId-{} Could not start scheduler", storeId, ex);
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
        return "" + storePool.getStore(storeId).configuration.defaultPrefix;
    }

    public String getStoreName() {
        return applicationPool.getApplication("d755efca-9e02-4e88-92c2-37a3413f3f41").getSetting("title");
    }

    public void gsTiming(String description) {
        //
    }

    private void storeRemote(DataCommon data) {
        data.storeId = "all_" + getCurrentGetShopModule();
        data.lastModified = new Date();

        try {
            if (getSession() != null && getSession().currentUser != null && getSession().currentUser.id != null) {
                data.lastModifiedByUserId = getSession().currentUser.id;
            }
        } catch (Exception e) {
            logPrintException(e);
        }

        if (getSession() != null) {
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

        String currentModule = (String) getSession().get("currentGetShopModule");

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
    public void postProcessMessage(Method executeMethod, Object[] argObjects) {
    }

    /**
     * Override this function in order to start one time executors.
     * 
     * @return
     */
    public List<Class> getOneTimExecutors() {
        return new ArrayList<>();
    }

    private boolean hasBeenExecuted(Class oneTimeExectors) {
        DBCollection collection = getDBCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("_id", "one_time_executor_" + oneTimeExectors.getCanonicalName());

        return collection.find(query).size() != 0;
    }

    private DBCollection getDBCollection() {
        DB db = database.getMongo().getDB(getClass().getSimpleName());
        String collectionName = storeId;
        if (this instanceof GetShopSessionBeanNamed) {
            collectionName = collectionName + "_" + ((GetShopSessionBeanNamed) this).getSessionBasedName();
        }
        return db.getCollection("col_" + collectionName);
    }

    private void retgisterExecuted(Class oneTimeExectors) {
        DBCollection collection = getDBCollection();

        BasicDBObject query = new BasicDBObject();
        query.put("_id", "one_time_executor_" + oneTimeExectors.getCanonicalName());

        collection.save(query);
    }

    public String getCurrentUserId() {
        return Optional.ofNullable(getSession())
                .map(i -> i.currentUser)
                .map(i -> i.id)
                .orElse("");
    }

    public String getLanguage() {
        return Optional.ofNullable(getSession())
                .map(i -> i.language)
                .orElse("");
    }

    public SessionInfo getSessionInfo() {
        return SessionInfo.builder()
                .setStoreId(storeId)
                .setCurrentUserId(getCurrentUserId())
                .setLanguage(getLanguage())
                .setManagerName(credentials.manangerName)
                .build();
    }

    public SessionInfo getStoreIdInfo() {
        return SessionInfo.builder()
                .setStoreId(storeId)
                .setManagerName(credentials.manangerName)
                .build();
    }
}
