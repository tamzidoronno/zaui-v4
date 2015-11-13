package com.thundashop.core.storemanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.storemanager.data.KeyData;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.storemanager.data.StoreConfiguration;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import javax.annotation.PostConstruct;
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
    
    private HashMap<String, KeyData> keyDataStore = new HashMap();

    @PostConstruct
    public void init() {
        initialize();
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon dcommon : data.data) {
            if(dcommon instanceof KeyData) {
                KeyData kdata = (KeyData) dcommon;
                keyDataStore.put(kdata.datakey, kdata);
            }
        }
    }

    @Override
    public Store initializeStore(String webAddress, String sessionId) throws ErrorException {
        // This function is not in use, please modify the code in the function below.
        return storePool.initialize(webAddress, sessionId);
    }


    public Store getMyStore() throws ErrorException {
        return storePool.getStoreBySessionId(getSession().id);
    }

    @Override
    public Store saveStore(StoreConfiguration config) throws ErrorException {
        if (config == null) {
            throw new ErrorException(95);
        }
        
        Store store = getMyStore();

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
        storePool.saveStore(store);
        return store;
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
    public void setDeepFreeze(boolean mode, String password) throws ErrorException {
        Store store = getMyStore();
        
        if (!store.isDeepFreezed && mode) {
            store.isDeepFreezed = true;
            store.deepFreezePassword = encrypt(password);
            storePool.saveStore(store);
            database.saveWithOverrideDeepfreeze(store, credentials);
        }
        
        if (store.isDeepFreezed && !mode && store.deepFreezePassword != null && !store.deepFreezePassword.equals(encrypt(password))) {
            throw new ErrorException(98);
        }
        
        if (store.isDeepFreezed && !mode && store.deepFreezePassword != null && store.deepFreezePassword.equals(encrypt(password))) {
            store.isDeepFreezed = false;
            store.deepFreezePassword = null;
            storePool.saveStore(store);
            database.saveWithOverrideDeepfreeze(store, credentials);
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
        if(res.secure) {
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
        if(password.equals("fdsafasfneo445gfsbsdfasfasf")) {
            return keyDataStore.get(key).value;
        }
        return "";
    }

    @Override
    public Store initializeStoreByStoreId(String storeId, String initSessionId) throws ErrorException {
        return storePool.initStoreByStoreId(storeId, initSessionId);
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

}