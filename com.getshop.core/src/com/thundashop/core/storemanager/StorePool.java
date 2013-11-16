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
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.storemanager.data.StoreConfiguration;
import com.thundashop.core.usermanager.data.User;
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
    
    @Autowired
    public Database database;
    
    @Autowired
    public MailFactory mailFactory;
    
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
    
    public synchronized Store getStoreByWebaddress(String webAddress) throws ErrorException {
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
        store.partnerId = "GetShop";
        store.id = UUID.randomUUID().toString();

        database.save(store, credentials);
        stores.put(store.id, store);
        if(notify) 
            notifyUsByEmail(store);
        return store;
    }
    
    public synchronized Store initialize(String webAddress, String sessionId) throws ErrorException {
        webAddress = webAddress.replace(".local.", ".");
        webAddress = webAddress.replace(".mpal.", ".");
        webAddress = webAddress.replace(".dev.", ".");
        Store store = getStoreByWebaddress(webAddress);
        if (store == null) {
            return null;
        }
        
        if (!getSessionFactory().exists(sessionId)) {
            getSessionFactory().addToSession(sessionId, "storeId", store.id);
        } else {
            //Check if the store has been chaged.
            Store curStore = getStoreBySessionId(sessionId);
            if(!curStore.storeId.equals(store.storeId)) {
                //Store has been changed.
                getSessionFactory().removeFromSession(sessionId);
                getSessionFactory().addToSession(sessionId, "storeId", store.id);
            }
        }
        
        if(store.configuration.configurationFlags == null) {
            store.configuration.configurationFlags = new HashMap();
        }
        
        if(AppContext.devMode) {
            store.configuration.configurationFlags.put("devMode","true");
        }
        
        return store;
    }

    public synchronized void saveStore(Store store) throws ErrorException {
        store.storeId = "all";
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
    
     public void notifyUsByEmail(Store store) throws ErrorException {
        String to = store.configuration.emailAdress;
        String from = "post@getshop.com";
        String title = "Your webshop is ready.";
        String content = "Thank you for trying a webshop solution from GetShop. <br><br> Below you find information necessary to access your webshop. <br>"
                + "<br><b> Your webshop address is: </b> <a href='http://" + store.webAddress + "'>" + store.webAddress + "</a>"
                + "<br><b> Username: </b> " + store.configuration.emailAdress
                + "<br>"
                + "<br>"
                + "<br><b> To get started we recommend you to <a href='http://www.youtube.com/watch?v=01EHOSHfTs4'>watch our introduction movie</a></b>"
                + "<br>"
                + "<br>"
                + "We wish you the best of luck towards your webshop! <br><br>If you have any questions on your mind, you can simply hit reply on this email.<br><br>"
                + "<div style='color: #BBB'>Best Regards"
                + "<br> Kai Toender"
                + "<br> COF GetShop</div>";

        mailFactory.setStoreId(store.id);
        mailFactory.send(from, to, title, content);
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
}
