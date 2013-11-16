package com.thundashop.core.storemanager;

import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.storemanager.data.StoreConfiguration;
import java.security.MessageDigest;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@Scope("prototype")
public class StoreManager extends ManagerBase implements IStoreManager {
    @Autowired
    public StorePool storePool;
    
    @Autowired
    public Database database;
    
    @Autowired
    public MailFactory mailFactory;

    @Autowired
    public StoreManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }

    @PostConstruct
    public void init() {
        initialize();
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
    }

    @Override
    public Store initializeStore(String webAddress, String sessionId) throws ErrorException {
        // This function is not in use, please modify the code in the function below.
        return storePool.initialize(webAddress, sessionId);
    }

    @Override
    public void connectStoreToPartner(String partner) throws ErrorException {
        Store store = getStore();
        connectStoreToPartner(store.id, partner);
    }

    public Store getMyStore() throws ErrorException {
        return storePool.getStoreBySessionId(getSession().id);
    }

    @Override
    public Store saveStore(StoreConfiguration config) throws ErrorException {
        if (config == null) {
            throw new ErrorException(95);
        }
        
        Store store = getStore();
        store.configuration = config;
        storePool.saveStore(store);
        return store;
    }

    @Override
    public Store setPrimaryDomainName(String domainName) throws ErrorException {
        Store store = getStore();

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
        Store store = getStore();
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
        Store store = getStore();
        store.readIntroduction = true;
        storePool.saveStore(store);
        return store;
    }

    @Override
    public Store enableSMSAccess(boolean toggle, String password) throws ErrorException {
        Store store = getStore();
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
        Store store = getStore();
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
        Store store = getStore();
        return store.id;
    }

    public void connectStoreToPartner(String id, String partner) throws ErrorException {
        if(partner.equals("") || partner.trim().length() == 0) {
            throw new ErrorException(26);
        }
        
        Store store = storePool.getStore(id);
        if(store == null) {
            throw new ErrorException(23);
        }
        
        store.partnerId = partner;
        storePool.saveStore(store);
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

    @Override
    public void setVIS(boolean toggle, String password) throws ErrorException {
        if(password.equals("fdasfddefdvcx_33%ccZss")) {
            Store store = getStore();
            store.isVIS = toggle;
            storePool.saveStore(store);
        }
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

  

}