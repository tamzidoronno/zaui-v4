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
import com.thundashop.core.storemanager.data.StoreCounter;
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
            if (dataCommon instanceof StoreCounter) {
                counter = (StoreCounter) dataCommon;
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
        Store store = getStoreByWebaddress(webAddress);
        
        if (store == null) {
            webAddress = webAddress.replace(".local.", ".");
            webAddress = webAddress.replace(".mpal.", ".");
            webAddress = webAddress.replace(".dev.", ".");
            store = getStoreByWebaddress(webAddress);
        }
        
        
        if (store == null && webAddress.contains("old.")) {
            webAddress = webAddress.replace("old.", "");
            store = getStoreByWebaddress(webAddress);
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
        String storeAddress = "https://"+store.webAddress;
        String username = store.configuration.emailAdress;
        String to = store.configuration.emailAdress;
        String from = "post@getshop.com";
        String title = "Your webshop is ready.";
        String content = "<div style=\"text-align: center;\">\n" +
"    <div style=\"border-radius:4px;border:solid 1px #666; background-color:#3f3f3f; color:#fff; border: solid 1px; max-width: 400px;width:100%;display:inline-block;text-align:left;\">\n" +
"        <div style=\"border-bottom: solid 1px #BBB;padding: 10px;background-color:#d4d4d4;color:#000;position:relative;\">Thank you for trying getshop\n" +
"            <span style=\"position:absolute; right:10px; font-weight:bold\">\n" +
"        </div>\n" +
"        <div style=\"font-size:12px; padding: 10px;\">\n" +
"            We at GetShop are happy that you are willing to test our unique e-commerce solution. \n" +
"            <br><br>\n" +
"            We really hope you enjoy GetShop as much as we put our pride into shaping it to become the best out there. Please don't hesitate to contact us if there is anything we can do for you, our support team will do everything in their power to satisfy your needs.\n" +
"            <br><br>\n" +
"            <table width='100%'>\n" +
"                <tr>\n" +
"                    <td width='50%' style='font-size:12px;color:#fff;'>Your store address</td>\n" +
"                    <td style='font-size:12px;color:#fff;'><a style=\"color:#b4ebff; text-decoration:none;\" href=\""+storeAddress+"\">"+storeAddress+"</a></td>\n" +
"                </tr>\n" +
"                <tr>\n" +
"                    <td width='50%' style='font-size:12px;color:#fff;'>Your username</td>\n" +
"                    <td style='font-size:12px;color:#fff;'><a style=\"color:#b4ebff; text-decoration:none;\" href=\"mailto:"+username+"\">"+username+"</a></td>\n" +
"                </tr>\n" +
"            </table>\n" +
"            <br><br>\n" +
"            For more information and help visit:<br>\n" +
"            <a style=\"color:#b4ebff; text-decoration:none;\" href=\"https://www.getshop.com/support.html\">https://www.getshop.com/support.html</a>\n" +
"        </div>\n" +
"        <div style=\"border-top: solid 1px #BBB;padding: 10px;background-color:#d4d4d4;color:#000;font-size:12px;position:relative;\">\n" +
"            <table width=\"100%\">\n" +
"                <tr>\n" +
"                    <td style=\"font-size:12px;\">\n" +
"                        GetShop Support<br>\n" +
"                        +47 940 10 704<br>\n" +
"                    </td>\n" +
"                    <td style=\"font-size:12px;\">\n" +
"                        <a href=\"mailto:post@getshop.com\" style=\"text-decoration:none;\">post@getshop.com</a><br>\n" +
"                        <a href=\"https://www.getshop.com\" style=\"text-decoration:none;\">https://www.getshop.com</a><br>\n" +
"                    </td>\n" +
"                    <td>\n" +
"                        <img src=\"\">\n" +
"                    </td>"
                + "<td>\n" +
"                        <img src=\"https://www.getshop.com/displayImage.php?id=52803b18-84a9-4f06-8074-a83e82d47853\" />\n" +
"                    </td>"
                + "\n" +
"                </tr>\n" +
"            </table>\n" +
"            <br>\n" +
"            <div style=\"text-align:right;\">\n" +
"                Strandgaten 21, 4380 Hauge I Dalane, Norway\n" +
"            </div>\n" +
"        </div>\n" +
"    </div>\n" +
"</div>";

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
}
