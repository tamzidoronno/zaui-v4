package com.thundashop.core.storemanager;

import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshop.GetShop;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.storemanager.data.StoreConfiguration;
import java.util.ArrayList;
import java.util.HashMap;
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
public class StoreManager extends ManagerBase implements IStoreManager {

    private SessionFactory sessionFactory = new SessionFactory();
    private ConcurrentHashMap<String, Store> stores = new ConcurrentHashMap<>();
    @Autowired
    public MailFactory mailFactory;

    @Autowired
    public StoreManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
        isSingleton = true;
        storeId = "all";
    }

    @PostConstruct
    public void init() {
        initialize();
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
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

    private Store getStoreByWebaddress(String webAddress) throws ErrorException {
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

    private Store getStore(String id) {
        Store store = stores.get(id);
        store.storeId = storeId;
        return store;
    }

    public Store getStoreBySessionId(String sessionId) throws ErrorException {
        String storeId = sessionFactory.getObject(sessionId, "storeId");
        if (storeId == null) {
            return null;
        }

        Store store = getStore(storeId);

        if (store == null) {
            throw new ErrorException(23);
        }

        return store;
    }

    @Override
    public Store initializeStore(String webAddress, String sessionId) throws ErrorException {
        Store store = getStoreByWebaddress(webAddress);
        if (store == null) {
            return null;
        }
        if (!sessionFactory.exists(sessionId)) {
            sessionFactory.addToSession(sessionId, "storeId", store.id);
        }
        
        if(store.configuration.configurationFlags == null) {
            store.configuration.configurationFlags = new HashMap();
        }
        
        if(AppContext.devMode) {
            store.configuration.configurationFlags.put("devMode","true");
        }
        
        return store;
    }

    @Scheduled(fixedDelay = 5000)
    public void saveSessionFactory() throws ErrorException {
        
//        if(sessionFactory != null  && sessionFactory.ready) {
//            sessionFactory.prepareForSaving();
//            sessionFactory.storeId = "all";
//            databaseSaver.saveObject(sessionFactory, credentials);
//        }
    }

    @Override
    public Store getMyStore() throws ErrorException {
        return this.getStore();
    }

    @Override
    public Store saveStore(StoreConfiguration config) throws ErrorException {
        Store store = getStore();
        store.configuration = config;
        databaseSaver.saveObject(store, credentials);
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
        databaseSaver.saveObject(store, credentials);
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
        databaseSaver.saveObject(store, credentials);
        return store;
    }

    @Override
    public Store createStore(String shopname, String email, String password) throws ErrorException {
        String webAddress = shopname.replace(" ", "").toLowerCase();
        
        GetShop getshopManager = getManager(GetShop.class);
        if(isAddressTaken(webAddress)) {
            throw new ErrorException(94);
        }
        
        Store store = createStoreObject(webAddress, shopname, email);
        notifyUsByEmail(store);
        return store;
    }
    
    @Override
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

    private void notifyUsByEmail(Store store) throws ErrorException {
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

        mailFactory.setStoreId(storeId);
        mailFactory.send(from, to, title, content);
    }

    private Store createStoreObject(String webAddress, String shopname, String email) throws ErrorException {   
        Store store = new Store();
        store.storeId = storeId;
        store.webAddress = webAddress;
        store.configuration = new StoreConfiguration();
        store.configuration.shopName = shopname;
        store.configuration.emailAdress = email;
        store.partnerId = "GetShop";

        databaseSaver.saveObject(store, credentials);
        stores.put(store.id, store);
        return store;
    }

    @Override
    public Store setIntroductionRead() throws ErrorException {
        Store store = getStore();
        store.readIntroduction = true;
        databaseSaver.saveObject(store, credentials);
        return store;
    }

    @Override
    public Store enableSMSAccess(boolean toggle, String password) throws ErrorException {
        Store store = getStore();
        if (password.equals("3322xcEE-_239%")) {
            store.configuration.hasSMSPriviliges = toggle;
            databaseSaver.saveObject(store, credentials);
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
            databaseSaver.saveObject(store, credentials);
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
        Store store = stores.get(id);
        if(store == null) {
            throw new ErrorException(23);
        }
        
        store.partnerId = partner;
        databaseSaver.saveObject(store, credentials);
    }
    
    public boolean isSmsActivate(String storeId) {
        Store store = stores.get(storeId);
        
        if (store != null) {
            return getStore(storeId).configuration.hasSMSPriviliges;
        }
        
        return false;
    }
}