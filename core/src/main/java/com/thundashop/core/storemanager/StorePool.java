/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.storemanager;

import com.getshop.scope.GetShopSessionScope;
import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.SessionFactory;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.storemanager.data.StoreConfiguration;
import com.thundashop.core.storemanager.data.StoreCounter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
    private StoreCounter counter = null;
    
    @Autowired
    public Database database;
    
    @Autowired
    public MessageManager messageManager;
    
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
            if (dataCommon instanceof StoreCounter) {
                counter = (StoreCounter) dataCommon;
            }
        }
        
        stores.values()
            .stream()
            .filter(o -> o.incrementalStoreId == null)
            .forEach(store -> {
                if (store != null) {
                    saveStore(store);
                }
            });
    }
   
    public synchronized Store getStoreByWebaddress(String webAddress) throws ErrorException {
        Store store = null;

        Integer incStoreIdFromWebAddr = null;
        
        if (webAddress != null && webAddress.contains("pms")) {
            String[] splitted = webAddress.split("pms");
            try { incStoreIdFromWebAddr = Integer.parseInt(splitted[0]); } catch (Exception ex) {}
        }
        
        for (Store istore : stores.values()) {
            if (incStoreIdFromWebAddr != null && istore.incrementalStoreId != null && istore.incrementalStoreId.equals(incStoreIdFromWebAddr)) {
                return istore;
            }
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
    
    public synchronized SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public synchronized boolean isAddressTaken(String address) throws ErrorException {
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
    
    public synchronized Store createStoreObject(String shopname, String email, String password, boolean notify) throws ErrorException {   
        String webAddress = shopname.replace(" ", "").toLowerCase();
        Store store = new Store();
        store.storeId = "all";
        store.webAddress = webAddress;
        store.configuration = new StoreConfiguration();
        store.configuration.shopName = shopname;
        store.configuration.emailAdress = email;
        store.id = UUID.randomUUID().toString();

        database.save(store, credentials);
        stores.put(store.id, store);
        
        //MEssage usself about creation.
        GetShopSessionScope scope = AppContext.appContext.getBean(GetShopSessionScope.class);
        scope.setStoreId(store.id, "", null);
        messageManager.sendMail("post@getshop.com", "post@getshop.com", "Instance created", "shopname, String email; " + shopname + ", " +  email, "post@getshop.com", "post@getshop.com");
        
        return store;
    }
    
    public synchronized Object autoCreateStoreObject(String name) {
        if(isAddressTaken(name)) {
            return null;
        }
        return createStoreObject(name, "", "", false);
    }
    
    public synchronized Store initialize(String webAddress, String sessionId) throws ErrorException {
        String orgWebAddress = webAddress;
        
        Store store = getStoreByWebaddress(webAddress);
        
        if (webAddress == null) {
            throw new ErrorException(1044);
        }
        if (store == null) {
            webAddress = webAddress.replace(".3.0.local.", ".3.0.");
            webAddress = webAddress.replace(".3.0.mpal.", ".3.0.");
            webAddress = webAddress.replace(".3.0.mdev.", ".3.0.");
            webAddress = webAddress.replace(".local.", ".");
            webAddress = webAddress.replace(".mlocal.", ".");
            webAddress = webAddress.replace(".beta.", ".");
            webAddress = webAddress.replace("gsmobile", "");
            webAddress = webAddress.replace(".mpal.", ".");
            webAddress = webAddress.replace(".dev.", ".");
            webAddress = webAddress.replace(".mdev.", ".");
            store = getStoreByWebaddress(webAddress);
            if (store == null) {
                webAddress = webAddress.replace(".3.0", "");
                store = getStoreByWebaddress(webAddress);
            }
            
            if (store == null) {
                store = getStoreByIdentifier(webAddress);
            }
        }
        
        
        if (store == null && webAddress.contains("www.")) {
            webAddress = webAddress.replace("www.", "");
            store = getStoreByWebaddress(webAddress);
        }
        if (store == null && !webAddress.contains("www.")) {
            webAddress = "www."+webAddress;
            store = getStoreByWebaddress(webAddress);
        }
        
        if (store == null) {
            store = getStore(orgWebAddress);
        }
        
        if (store == null) {
            return null;
        }
        
        if (store != null) {
            initStore(store, sessionId);
        }
        
        
        return store;
    }

    public synchronized void saveStore(Store store) throws ErrorException {
        store.storeId = "all";
        
        if (store.incrementalStoreId == null) {
            store.incrementalStoreId = getNextIncrementalStoreId();
        }
        
        stores.put(store.id, store);
        database.save(store, credentials);
    }
    
    public synchronized Store getStoreBySessionId(String sessionId) throws ErrorException {
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
    
    public synchronized Store getStoreBySessionIdDontUpdateSession(String sessionId) throws ErrorException {
        String storeId = getSessionFactory().getObjectPingLess(sessionId, "storeId");
        if (storeId == null) {
            return null;
        }

        Store store = getStore(storeId);

        if (store == null) {
            throw new ErrorException(23);
        }

        return store;
    }
    
    public synchronized boolean isSmsActivate(String storeId) {
        Store store = getStore(storeId);
        
        if (store != null) {
            return store.configuration.hasSMSPriviliges;
        }
        
        return false;
    }

    public synchronized void delete(Store store) throws ErrorException {
        if (store == null) {
            throw new ErrorException(26);
        } 
        
        if (store.isDeepFreezed) {
            return;
        }
        
        database.delete(store, credentials);
        stores.remove(store.id);
        stopStore(store);
    }
    
    private void stopStore(Store store) {
        if (AppContext.storePool != null) {
            AppContext.storePool.stop(store);
        }
    }
    
    @Scheduled(cron = "0 0 */1 * * *")
    public void test() {
        for (Store store : stores.values()) {
            if (store.isDeepFreezed) {
                stopStore(store);
            }
        }
    }

    public int incrementStoreCounter() throws ErrorException {
        if(counter == null) {
            counter = new StoreCounter();
            counter.id = UUID.randomUUID().toString();
            counter.counter = 20000;
        }
        counter.counter++;
        counter.storeId = "all";
        database.save(counter, credentials);
        return counter.counter;
    }

    public void loadStore(String arg, String newAddress) {
        List<DataCommon> datas = database.retreiveData(credentials);
        for (DataCommon dataCommon : datas) {
            if (dataCommon instanceof Store) {
                Store store = (Store) dataCommon;
                if (store.id != null && store.id.equals(arg)) {
                    stores.put(store.id, store);
                }
            }
        }
    }

    public Store initStoreByStoreId(String storeId, String sessionId) {
        Store store = stores.get(storeId);
        if (store != null) {
            initStore(store, sessionId);
        }
        
        return store;
    }

    private void initStore(Store store, String sessionId) {
        if (!getSessionFactory().exists(sessionId)) {
            getSessionFactory().addToSession(sessionId, "storeId", store.id);
        } else {
            //Check if the store has been chaged.
            Store curStore = getStoreBySessionId(sessionId);
            if(!curStore.id.equals(store.id)) {
                //Store has been changed.
                getSessionFactory().removeFromSession(sessionId);
                getSessionFactory().addToSession(sessionId, "storeId", store.id);
            }
        }
        
        if(store.configuration != null && store.configuration.configurationFlags == null) {
            store.configuration.configurationFlags = new HashMap();
        }
        
        if(AppContext.devMode) {
            store.configuration.configurationFlags.put("devMode","true");
        }
    }
    
    public List<Store> getAllStores() {
        return new ArrayList(stores.values());
    }

    private Store getStoreByIdentifier(String id) {
        for (Store store : stores.values()) {
            if (store.identifier != null && store.identifier.toLowerCase().equals(id.toLowerCase())) {
                return store;
            }
        }

        return null;
    }

    private Integer getNextIncrementalStoreId() {
        int start = 1000000;
        for (Store store : stores.values()) {
            if (store.incrementalStoreId != null && store.incrementalStoreId.intValue() > start) {
                start = store.incrementalStoreId.intValue();
            }
        }
        
        start++;
        return start;
    }
}
