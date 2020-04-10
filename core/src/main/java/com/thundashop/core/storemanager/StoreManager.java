package com.thundashop.core.storemanager;


import com.getshop.javaapi.GetShopApi;
import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.getshop.scope.GetShopSessionScope;
import com.google.gson.Gson;
import com.ibm.icu.util.Calendar;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshoplocksystem.GetShopLockSystemManager;
import com.thundashop.core.getshoplocksystem.LockServer;
import com.thundashop.core.getshoplocksystem.LockServerBase;
import com.thundashop.core.gsd.GdsManager;
import com.thundashop.core.gsd.GetShopDevice;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.pagemanager.GetShopModules;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.storemanager.data.KeyData;
import com.thundashop.core.storemanager.data.ModuleHomePages;
import com.thundashop.core.storemanager.data.SlaveStore;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.storemanager.data.StoreConfiguration;
import com.thundashop.core.storemanager.data.StoreCriticalMessage;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.webmanager.WebManager;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.PostConstruct;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class StoreManager extends ManagerBase implements IStoreManager {
    @Autowired
    public StorePool storePool;
    
    @Autowired
    public Database database;
    
    @Autowired
    public MailFactory mailFactory;
    
    @Autowired
    public GSEnvironments environments; 
    
    @Autowired
    public FrameworkConfig FrameworkConfig;
//    
    @Autowired
    public GetShopSessionScope getShopScope;
    
    @Autowired
    public OrderManager orderManager;
    
    @Autowired
    public GetShopLockSystemManager getShopLockSystemManager;
    
    @Autowired
    public GdsManager gdsManager;
    
    @Autowired
    public WebManager webManager;
    
    @Autowired
    public GetShopSessionScope getShopSessionScope;
    
    
    private HashMap<String, KeyData> keyDataStore = new HashMap();
    
    private HashMap<String, RemoteServerMetaData> backupInfo = new HashMap();
    
    public String currentSecretId;
    
    private GetShopModules modules = new GetShopModules();

    private ModuleHomePages moduleHomePages = new ModuleHomePages();
    
    public List<StoreCriticalMessage> messages = new ArrayList(); 
    
    private Date lastCheckedBackups = null;
    
    @PostConstruct
    public void init() {
        initialize();
    }

    public List<BookingEngine> getBookingEngines() {
        List<String> names = getMultiLevelNames();
        List<BookingEngine> result = new ArrayList();
        for(String name : names) {
            result.add(getShopSessionScope.getNamedSessionBean(name, BookingEngine.class));
        }
        
        return result;
    }

    public List<PmsManager> getPmsManagers() {
        List<String> names = getMultiLevelNames();
        List<PmsManager> result = new ArrayList();
        for(String name : names) {
            result.add(getShopSessionScope.getNamedSessionBean(name, PmsManager.class));
        }
        
        return result;
    }
    
    /**
     * PikTime is the time when getshop decided to go full product is king style.
     * The time where customers ruled getshop is over and getshop drives the development.
     * This will result in less special produced code for specific customers.
     * @return 
     */
    @Override
    public boolean isPikStore() {
        boolean doPush = false;
        if(lastCheckedBackups == null) {
            doPush = true;
        } else {
            long diff = System.currentTimeMillis() - lastCheckedBackups.getTime();
            if(diff > (1000*60*60*6)) {
                doPush = true;
            }
        }
            
        if(doPush) {
            lastCheckedBackups = new Date();
            doubleCheckTransferServersToBackupSystem();
        }
        return getStore().isPikStore();
    }
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon dcommon : data.data) {
            if(dcommon instanceof KeyData) {
                KeyData kdata = (KeyData) dcommon;
                keyDataStore.put(kdata.datakey, kdata);
            }
            if(dcommon instanceof StoreCriticalMessage) {
                StoreCriticalMessage msg = (StoreCriticalMessage) dcommon;
                messages.add(msg);
            }
            if(dcommon instanceof RemoteServerMetaData) {
                RemoteServerMetaData msg = (RemoteServerMetaData) dcommon;
                backupInfo.put(msg.id, msg);
            }
        }
        
        try {
            modules.getModules().stream().forEach(m -> {
                databaseRemote.getAll("StoreManager", "all", m.id).forEach(o -> {
                    if (o instanceof ModuleHomePages) {
                        moduleHomePages = (ModuleHomePages)moduleHomePages;
                    }
                });
            });
        }catch(Exception e) {
            
        }
    }

    @Override
    public Store initializeStore(String webAddress, String sessionId) throws ErrorException {
        // This function is not in use, please modify the code in the function below.
        return finalize(storePool.initialize(webAddress, sessionId));
    }

    public boolean isNewer(int year, int month, int day) {
        Store store = getMyStore();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        Date toCheck = new Date();
        return toCheck.after(cal.getTime());
    }

    
    @Override
    public Store getMyStore() throws ErrorException {
        return finalize(storePool.getStore(storeId));
    }

    @Override
    public Store saveStore(StoreConfiguration config) throws ErrorException {
        if (config == null) {
            throw new ErrorException(95);
        }
        
        Store store = getMyStore();
        
        if (!isCmsModule()) {
            saveModuleHomePage(config.homePage);
        }

        store.configuration = config;
        store.registrationUser = null;
        storePool.saveStore(store);
        return store;
    }

    @Override
    public Store setPrimaryDomainName(String domainName) throws ErrorException {
        Store store = getMyStore();

        if (store.webAddressPrimary != null && store.webAddressPrimary.trim().length() > 0) {
            if (store.additionalDomainNames == null) {
                store.additionalDomainNames = new ArrayList();
            }
            store.additionalDomainNames.add(store.webAddressPrimary);
        }

        store.webAddressPrimary = domainName;
        storePool.saveStore(store);
        return store;
    }

    @Override
    public Store removeDomainName(String domainName) throws ErrorException {
        Store store = getMyStore();
        if (store.additionalDomainNames != null) {
            store.additionalDomainNames.remove(domainName);
        }
        if (store.webAddressPrimary.equalsIgnoreCase(domainName)) {
            store.webAddressPrimary = "";
            if (store.additionalDomainNames != null && store.additionalDomainNames.size() > 0) {
                store.webAddressPrimary = store.additionalDomainNames.get(0);
                store.additionalDomainNames.remove(store.webAddressPrimary);
            }
        }
        if (store.webAddress.equalsIgnoreCase(domainName)) {
            store.webAddress = "";
        }
        storePool.saveStore(store);
        return store;
    }
    
    public void doubleCheckTransferServersToBackupSystem() {
        Gson gson = new Gson();
        
        List<LockServer> lockservers = getShopLockSystemManager.getLockServersUnfinalized();
        List<BackupServerInfo> toSendList = new ArrayList();
        for(LockServer server : lockservers) {
            RemoteServerMetaData serverInfo = getBackupServerInfoByServerId(server.getId());
            if(serverInfo.beenTransferred) {
                continue;
            }
            
            BackupServerInfo info = new BackupServerInfo();
            LockServerBase base = (LockServerBase) server;
            info.setData(base);
            toSendList.add(info);
            
        }
        
        List<GetShopDevice> devices = gdsManager.getDevices();
        for(GetShopDevice dev : devices) {
            RemoteServerMetaData serverInfo = getBackupServerInfoByServerId(dev.id);
            if(serverInfo.beenTransferred) {
                continue;
            }
            BackupServerInfo info = new BackupServerInfo();
            info.setData(dev);
            toSendList.add(info);
        }
        
        for(BackupServerInfo info : toSendList) {
            try {
                info.webaddr = getMyStore().getDefaultWebAddress();
                info.storeId = storeId;
                String toSend = gson.toJson(info);
                toSend = URLEncoder.encode(toSend, "UTF-8");
                String result = webManager.htmlGet("http://10.0.7.10:8800/?setServer="+toSend);
                if(result.equals("added")) {
                    RemoteServerMetaData serverInfo = new RemoteServerMetaData();
                    serverInfo.serverId = info.id;
                    serverInfo.beenTransferred = true;
                    saveObject(serverInfo);
                }
            }catch(Exception e) {
                logPrintException(e);
            }
        }
        
    }
    
    @Override
    public boolean isAddressTaken(String address) throws ErrorException {
        return storePool.isAddressTaken(address);
    }

    @Override
    public Store setIntroductionRead() throws ErrorException {
        Store store = getMyStore();
        store.readIntroduction = true;
        storePool.saveStore(store);
        return store;
    }

    @Override
    public Store enableSMSAccess(boolean toggle, String password) throws ErrorException {
        Store store = getMyStore();
        if (password.equals("3322xcEE-_239%")) {
            store.configuration.hasSMSPriviliges = toggle;
            storePool.saveStore(store);
        } else {
            throw new ErrorException(70);
        }
        return store;
    }

    @Override
    public Store enableExtendedMode(boolean toggle, String password) throws ErrorException {
        Store store = getMyStore();
        if (password.equals("32_9066_cdWDxzRF")) {
            store.isExtendedMode = toggle;
            storePool.saveStore(store);
        } else {
            throw new ErrorException(70);
        }
        return store;
    }

    @Override
    public String getStoreId() throws ErrorException {
        Store store = getMyStore();
        return store.id;
    }
    
    @Override
    public void delete() throws ErrorException {
        Store store = getMyStore();
        storePool.delete(store);
    }

    @Override
    public Store createStore(String hostname, String email, String password, boolean notify) throws ErrorException {
        /**
         * This function is not really in use. 
         * It skips StoreManager, and goes directly to StorePool 
         * and executes the createStore function there.
         * 
         * Its added here to support our api.
         */
        throw new UnsupportedOperationException("Not in use."); //To change body of generated methods, choose Tools | Templates.
    }

    private String encrypt(String password) throws ErrorException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (Exception ex) {
            throw new ErrorException(88);
        }
    }


    @Override
    public int generateStoreId() throws ErrorException {
        return storePool.incrementStoreCounter();
    }

    @Override
    public void setSessionLanguage(String id) throws ErrorException {
        getSession().language = id;
    }

    @Override
    public void setIsTemplate(String storeId, boolean isTemplate) {
        Store store = storePool.getStore(storeId);
        if (store != null) {
            store.isTemplate = isTemplate;
            storePool.saveStore(store);
        }
    }

    @Override
    public void saveKey(String key, String value, boolean secure) {
        KeyData keydata = new KeyData();
        if(keyDataStore.containsKey(key)) {
            keydata = keyDataStore.get(key);
        }
        keydata.datakey = key;
        keydata.value = value;
        keydata.secure = secure;
        keyDataStore.put(key, keydata);
        saveObject(keydata);
    }

    @Override
    public String getKey(String key) {
        KeyData res = keyDataStore.get(key);
        
        if (res == null) {
            return "";
        }
        
        if(res != null && res.secure) {
            if(!getSession().currentUser.isAdministrator()) {
                return "";
            }
        }
        
        return res.value;
    }

    @Override
    public void removeKey(String key) {
        KeyData obj = keyDataStore.get(key);
        keyDataStore.remove(key);
        deleteObject(obj);
    }

    @Override
    public String getKeySecure(String key, String password) {
        if(password != null && password.equals("fdsafasfneo445gfsbsdfasfasf")) {
            return keyDataStore.get(key) != null ? keyDataStore.get(key).value : "";
        }
        return "";
    }

    @Override
    public Store initializeStoreByStoreId(String storeId, String initSessionId) throws ErrorException {
        return storePool.initStoreByStoreId(storeId, initSessionId);
    }

    @Override
    public Store initializeStoreWithModuleId(String webAddress, String initSessionId, String moduleId) throws ErrorException {
        Store store = storePool.initialize(storeId, initSessionId);
        if (getSession() != null) {
            moduleId = moduleId == null || moduleId.isEmpty() ? "cms" : moduleId;
            getSession().put("currentGetShopModule", moduleId);
        }
        return store; 
    }
    
    @Override
    public void setImageIdToFavicon(String id) {
        Store store = getMyStore();
        store.favicon = id;
        storePool.saveStore(store);
    }

    @Override
    public Store autoCreateStore(String hostname) throws ErrorException {
        /* not in use, goes directly to storepool */
        return null;
    }

    @Override
    public boolean isProductMode() throws ErrorException {
        return FrameworkConfig.productionMode;
    }

    @Override
    public void syncData(String environment, String username, String password) throws ErrorException {
        if (FrameworkConfig.productionMode) {
            logPrint("This function is not allowed in production mode");
            return;
        }
        
        GSEnvironment environMent = environments.get(environment);
        
        try {
            GetShopApi api = environMent.getApi(getMyStore().webAddress);    
            List<DataCommon> datas = database.getAllDataForStore(storeId);
            
            Runnable task = () -> {
                try {
                    User user = api.getUserManager().logOn(username, password);
                    if (user == null || user.id.isEmpty() || !user.isAdministrator()) {
                        throw new ErrorException(26);
                    }
                    String dataToTransfer = StoreManager.toString((Serializable) datas);
                    byte[] bytes = compress(dataToTransfer);
                    double size = bytes.length;
                    logPrint("Data to transfer: " + Math.floor(size/1024) + "kb, " + Math.floor(size/1024/1024) + "mb");
                    api.getStoreManager().receiveSyncData(bytes);
                    logPrint("Data sent, should be availble online now!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            };

            Thread thread = new Thread(task);
            thread.setName("Synchonize data thread started");
            thread.start();     
            
        } catch (Exception ex) {
            if(ex instanceof java.lang.ClassNotFoundException) {
                logPrint("Class not found: " + ex.getMessage());
            } else {
                ex.printStackTrace();
            }
        }
    }
    
    private static Object fromString( String s ) throws IOException ,
                                                       ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode( s );
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(  data ) )) {
            Object o  = ois.readObject();
            return o;
        }
   }

    private static String toString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray()); 
    }
    
     public byte[] compress(String data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(data.getBytes());
        gzip.close();
        byte[] compressed = bos.toByteArray();
        bos.close();
        return compressed;
    }
	
    private String decompress(byte[] compressed) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(bis);
        BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        gis.close();
        bis.close();
        return sb.toString();
    }
     
    @Override
    public void receiveSyncData(byte[] data) throws ErrorException {
        try {
            logPrint("Sync data received");
            String json = decompress(data);
            List<DataCommon> datas = (List<DataCommon>) StoreManager.fromString(json);
            logPrint("Adding data to database: " + datas.size());
            database.refreshDatabase(datas);
            logPrint("Clearing store: " + storeId);
            getShopScope.clearStore(storeId);
            logPrint("Done");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<String> getAllEnvironments() {
        return new ArrayList(environments.getEnvironments().keySet());
    }

    @Override
    public List<String> getMultiLevelNames() throws ErrorException {
        Set<String> names = new TreeSet();
        
        List<String> fasthotelsOld = new ArrayList();
        fasthotelsOld.add("a152b5bd-80b6-417b-b661-c7c522ccf305");
        fasthotelsOld.add("3b647c76-9b41-4c2a-80db-d96212af0789");
        fasthotelsOld.add("e625c003-9754-4d66-8bab-d1452f4d5562");

        List<String> oldDefaults = new ArrayList();
        oldDefaults.add("123865ea-3232-4b3b-9136-7df23cf896c6");
        
        if(fasthotelsOld.contains(storeId)) {
            names.clear();
            names.add("demo");
        } else if(oldDefaults.contains(storeId)) {
            names.clear();
            names.add("default");
        } else if(isPikStore()) {
            names.clear();
            names.add("default");
        } else {
            for (GetShopSessionBeanNamed named : getShopScope.getSessionNamedObjects()) {
                if(named == null || named.getName() == null) {
                    logPrint("Null name");
                } else {
                    if(!named.getName().contains("'")) {
                        names.add(named.getName());
                    }
                }
            }
        }

        return new ArrayList(names);
    }

    @Override
    public void toggleIgnoreBookingErrors(String password) {
        if (!password.equals("asdjfiasdfjiejrisjadfisjdf")) {
            return;
        }
        
        Object ignore = getSession().get("ignoreBookingErrors");
        if (ignore != null) {
            getSession().remove("ignoreBookingErrors");
        } else {
            getSession().put("ignoreBookingErrors", "true");
        }
    }

    @Override
    public void setStoreIdentifier(String identifier) {
        boolean isInUse = database.verifyThatStoreIdentifierNotInUse(identifier);
        
        if (!isInUse) {
            Store store = getMyStore();
            store.identifier = identifier;
            storePool.saveStore(store);
        }
    }

    @Override
    public String getCurrentSession() throws Exception {
        return getSession().id;
    }

    private void saveModuleHomePage(String homePage) {
        String moduleName = getCurrentGetShopModule();
        moduleHomePages.homePages.put(moduleName, homePage);
        Credentials cred = new Credentials();
        cred.storeid = "all";
        cred.manangerName = "StoreManager";
        
        moduleHomePages.storeId = "all";
        databaseRemote.save(moduleHomePages, cred);
    }

    private Store finalize(Store store) {
        if (store != null && store.configuration != null) {
            store.configuration.moduleHomePages = moduleHomePages;
        }
        
        return store;
    }

    public void informStoreOwnerAboutCriticalInformation(String message) {
        StoreCriticalMessage msg = new StoreCriticalMessage();
        msg.message = message;
        saveObject(msg);
        messages.add(msg);
    }

    @Override
    public StoreCriticalMessage getCriticalMessage() {
        for(StoreCriticalMessage msg : messages) {
            if(msg.seenWhen == null) {
                return msg;
            }
        }
        return null;
    }

    @Override
    public void seenCriticalMessage(String id) {
        StoreCriticalMessage msg = null;
        for(StoreCriticalMessage tmp : messages) {
            if(tmp.id.equals(id)) {
                msg = tmp;
            }
        }
        
        msg.seenByUser = getSession().currentUser.id;
        msg.seenWhen = new Date();
        saveObject(msg);
    }

    @Override
    public void setDefaultMultilevelName(String multilevelname) {
        Store store = getMyStore();
        store.defaultMultilevelName = multilevelname;
        storePool.saveStore(store);
    }
    
    public void acceptGDPR() {
        Store store = getMyStore();
        store.acceptedGDPR = true;
        store.acceptedGDPRDate = new Date();
        store.acceptedByUser = getSession().currentUser.id;
        storePool.saveStore(store);
    }

    @Override
    public void changeTimeZone(String timezone) {
        Store store = getMyStore();
        store.setTimeZone(timezone);
        storePool.saveStore(store);
    }

    @Override
    public List<SlaveStore> getSlaves() {
        List<SlaveStore> retList = new ArrayList();

        storePool.getAllStores()
                .stream()
                .filter(store -> store.masterStoreId.equals(storeId))
                .forEach(slaveStoreObject -> {
                    SlaveStore slaveStore = new SlaveStore();
                    slaveStore.slaveStoreId = slaveStoreObject.id;
                    slaveStore.accepted = getStore().acceptedSlaveIds.contains(slaveStoreObject.id);
                    retList.add(slaveStore);
                });
        
        return retList;
    }

    @Override
    public void setMasterStoreId(String masterStoreId) {
        Store store = getStore();
        store.masterStoreId = masterStoreId;
        storePool.saveStore(store);
    }

    @Override
    public void acceptSlave(String slaveStoreId) {
        Store store = getStore();
        store.acceptedSlaveIds.removeIf(o -> o.equals(slaveStoreId));
        store.acceptedSlaveIds.add(slaveStoreId);
        storePool.saveStore(store);
    }

    public String getcountryCode() {
        Application settings = getStoreSettingsApplication();
        String countrycode = settings.getSetting("countrycode");
        if(countrycode == null || countrycode.isEmpty()) {
            countrycode = "NO";
        }
        return countrycode;
    }

    @Override
    public void toggleDeactivation(String password, boolean deactivated) {
        if(!password.equals("gfdoten35345gfsgfdEE__!")) {
            return;
        }
        Store store = getMyStore();
        store.deactivated = deactivated;
        storePool.saveStore(store);
    }

    @Override
    public boolean supportsCreateOrderOnDemand() {
        List<String> hasSupportForOnDemandOrders = new ArrayList();
        hasSupportForOnDemandOrders.add("1ed4ab1f-c726-4364-bf04-8dcddb2fb2b1"); //Bergstaden
        hasSupportForOnDemandOrders.add("61216a03-827d-44a6-a7f1-8939402c51c1"); //Svanhild
        
        List<String> avoidNewOrderProcess = new ArrayList();
        avoidNewOrderProcess.add("7b21932d-26ad-40a5-b3b6-c182f5ee4b2f");
        avoidNewOrderProcess.add("c63cdffc-765b-44b2-9694-3628d53726fa");
        if(avoidNewOrderProcess.contains(storeId)) {
            return false;
        }
        
        
        if(storeId != null && hasSupportForOnDemandOrders.contains(storeId)) {
            return true;
        }
        
        Store store = getMyStore();
        return store.newPaymentProcess;
    }

    public void toggleNewPaymentProcess() {
        Store store = getMyStore();
        store.newPaymentProcess = true;
        storePool.saveStore(store);
    }

    private RemoteServerMetaData getBackupServerInfoByServerId(String id) {
        for(RemoteServerMetaData tmp : backupInfo.values()) {
            if(tmp.serverId.equals(id)) {
                return tmp;
            }
        }
        
        return new RemoteServerMetaData();
    }

    public void invalidateServerBackup(String id) {
        for(RemoteServerMetaData tmp : backupInfo.values()) {
            if(tmp.serverId.equals(id)) {
                tmp.beenTransferred = false;
                saveObject(tmp);
            }
        }
    }

    public String getCurrency() {
        String currency = getStoreSettingsApplicationKey("currencycode");
        if(currency == null || currency.isEmpty()) {
            currency = "NOK";
        }
        return currency;
    }

    public String getLanguage() {
        String lang = getSession().language;
        if(lang == null || lang.isEmpty()) {
            lang = "no";
        }
        if(lang.equalsIgnoreCase("nb_no")) {
            lang = "no";
        }
        return lang;
    }

    public String getPrefix() {
        return getMyStore().configuration.defaultPrefix + "";
    }

    @Override
    public String getSelectedCurrency() {
        return getCurrency();
    }

    @Override
    public boolean hasEmailErrors() {
        return GetShopLogHandler.authenticationError.contains(storeId);
    }
}