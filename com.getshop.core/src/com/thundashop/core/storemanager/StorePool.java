/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.storemanager;

import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.SessionFactory;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.storemanager.data.StoreConfiguration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class StorePool {
    private ConcurrentHashMap<String, Store> stores = new ConcurrentHashMap<>();
    private SessionFactory sessionFactory = new SessionFactory();
    private Credentials credentials = new Credentials(StoreManager.class);
    
    @Autowired
    public Database database;
    
    @PostConstruct
    public void loadData() {
        credentials.manangerName = "StoreManager";
        credentials.password = "ASDFASDFASDFAS";
        credentials.storeid = "all";
        List<DataCommon> datas = database.retreiveData(credentials);
        for (DataCommon dataCommon : datas) {
            if (dataCommon instanceof Store) {
                Store store = (Store) dataCommon;
                stores.put(store.id, store);
            }
            if (dataCommon instanceof SessionFactory) {
                sessionFactory = (SessionFactory) dataCommon;
                sessionFactory.ready = true;
            }
        }
    }
    
    public Store getStoreByWebaddress(String webAddress) throws ErrorException {
        Store store = null;

        for (Store istore : stores.values()) {
            if (istore.webAddress != null && istore.webAddress.equalsIgnoreCase(webAddress)) {
                store = istore;
            }
            if (istore.webAddressPrimary != null && istore.webAddressPrimary.equalsIgnoreCase(webAddress)) {
                store = istore;
            }
            if (istore.additionalDomainNames != null) {
                for (String storeAddr : istore.additionalDomainNames) {
                    if (storeAddr.equalsIgnoreCase(webAddress)) {
                        store = istore;
                    }
                }
            }
        }

        return store;
    }
    
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public boolean isAddressTaken(String address) throws ErrorException {
        for(Store store : stores.values()) {
            if(store.webAddress!= null &&store.webAddress.equalsIgnoreCase(address)) {
                return true;
            }
            if(store.webAddressPrimary != null && store.webAddressPrimary.equalsIgnoreCase(address)) {
                return true;
            }
            if(store.additionalDomainNames != null) {
                for(String additional : store.additionalDomainNames) {
                    if(additional.equalsIgnoreCase(address)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public Store getStore(String id) {
        Store store = stores.get(id);
        return store;
    }
    
    public Store createStoreObject(String webAddress, String shopname, String email) throws ErrorException {   
        Store store = new Store();
        store.storeId = "all";
        store.webAddress = webAddress;
        store.configuration = new StoreConfiguration();
        store.configuration.shopName = shopname;
        store.configuration.emailAdress = email;
        store.partnerId = "GetShop";

        database.save(store, credentials);
        stores.put(store.id, store);
        return store;
    }
    
    public Store initialize(String webAddress, String sessionId) throws ErrorException {
        Store store = getStoreByWebaddress(webAddress);
        if (store == null) {
            return null;
        }
        
        if (!getSessionFactory().exists(sessionId)) {
            getSessionFactory().addToSession(sessionId, "storeId", store.id);
        }
        
        if(store.configuration.configurationFlags == null) {
            store.configuration.configurationFlags = new HashMap();
        }
        
        if(AppContext.devMode) {
            store.configuration.configurationFlags.put("devMode","true");
        }
        
        return store;
    }

    public void saveStore(Store store) throws ErrorException {
        store.storeId = "all";
        stores.put(store.id, store);
        database.save(store, credentials);
    }
    
    
    public Store getStoreBySessionId(String sessionId) throws ErrorException {
        String storeId = getSessionFactory().getObject(sessionId, "storeId");
        if (storeId == null) {
            return null;
        }

        Store store = getStore(storeId);

        if (store == null) {
            throw new ErrorException(23);
        }

        return store;
    }
    
    public boolean isSmsActivate(String storeId) {
        Store store = getStore(storeId);
        
        if (store != null) {
            return store.configuration.hasSMSPriviliges;
        }
        
        return false;
    }
}
