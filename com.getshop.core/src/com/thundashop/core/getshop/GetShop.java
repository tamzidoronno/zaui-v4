package com.thundashop.core.getshop;

import com.getshop.scope.GetShopSessionScope;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshop.data.GetshopStore;
import com.thundashop.core.getshop.data.Partner;
import com.thundashop.core.getshop.data.PartnerData;
import com.thundashop.core.getshop.data.StartData;
import com.thundashop.core.getshop.data.WebPageData;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.storemanager.data.StoreConfiguration;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.mongodb.morphia.Morphia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class GetShop extends ManagerBase implements IGetShop {

    private HashMap<String, List<Partner>> partners;
    private HashMap<String, PartnerData> partnerData = new HashMap();

    
    @Autowired
    public Database database;

    @Autowired
    public StorePool storePool;
    
    @Autowired
    public FrameworkConfig frameworkConfig;
    
    @Override
    public List<GetshopStore> getStores(String code) {
        if (!code.equals("laskdjf034j523lknjksdjnfklqnwgflkjangfasnlkjqnwtwpdsfguoq32905htkasjdfklsdfnaksldth342o5hiy4n4vnyesfjn")) {
            return new ArrayList();
        }

        throw new RuntimeException("This function is not available yet in 2.0.0");
    }

    @PostConstruct
    public void init() {
        isSingleton = true;
        storeId = "all";
        partners = new HashMap();
        initialize();
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon obj : data.data) {
            if (obj instanceof Partner) {
                Partner toAdd = (Partner) obj;
                List<Partner> partnerList = getPartnerList(toAdd.partnerId);
                partnerList.add(toAdd);
            }
            if(obj instanceof PartnerData) {
                PartnerData pdata = (PartnerData)obj;
                partnerData.put(pdata.partnerId, pdata);
            }
        }
    }

    private void addUserInformation(GetshopStore getshopstore, Store store) {
        Credentials credentials = new Credentials(UserManager.class);
        credentials.manangerName = "UserManager";
        credentials.storeid = store.id;
        getshopstore.userAgents = new ArrayList();


        List<DataCommon> users = database.retreiveData(credentials);
        for (DataCommon duser : users) {
            if (duser instanceof User) {
                User user = (User) duser;
                if (user.type == User.Type.ADMINISTRATOR) {
                    getshopstore.email = user.emailAddress;
                    getshopstore.lastLoggedIn = user.lastLoggedIn;
                    getshopstore.username = user.username;
                }
                if (user.type == User.Type.ADMINISTRATOR) {
                    if (user.hasChrome) {
                        getshopstore.hasChrome = true;
                    }
                    getshopstore.userAgents.add(user.userAgent);
                }
            }
        }
    }

    @Override
    public void addUserToPartner(String userId, String partner, String password) throws ErrorException {

        if (!password.equals("feerfdafeerfdsaf433453bvcbhgftrhyer56346453465346")) {
            return;
        }

        removeFromPartnersIfExists(userId);
        List<Partner> partnerList = getPartnerList(partner);

        Partner newPartner = new Partner();
        newPartner.partnerId = partner;
        newPartner.userId = userId;

        partnerList.add(newPartner);
        newPartner.storeId = storeId;
        databaseSaver.saveObject(newPartner, credentials);
    }

    private void removeFromPartnersIfExists(String userId) {
        HashMap<String, List<Partner>> partners = getPartnerList();
        Partner toRemove = null;
        for (List<Partner> userList : partners.values()) {
            for (Partner partner : userList) {
                if (partner.userId.equals(userId)) {
                    toRemove = partner;
                }
            }

            if (toRemove != null) {
                userList.remove(toRemove);
            }
        }
    }

    private HashMap<String, List<Partner>> getPartnerList() {
        if (partners == null) {
            partners = new HashMap();
        }

        return partners;
    }

    private List<Partner> getPartnerList(String partner) {
        List<Partner> partnerList = partners.get(partner);
        if (partnerList == null) {
            partnerList = new ArrayList();
            partners.put(partner, partnerList);
        }
        return partnerList;
    }

    public boolean isPartner(String partnerId, String userId) {
        for (Partner partner : getPartnerList(partnerId)) {
            if (partner.userId.equals(userId)) {
                return true;
            }
        }

        return false;
    }

    private Partner findPartnerForUser(String userId) {
        HashMap<String, List<Partner>> partnerList = getPartnerList();
        for (List<Partner> partners : partnerList.values()) {
            for (Partner partner : partners) {
                if (partner.userId.equals(userId)) {
                    return partner;
                }
            }
        }
        
        return null;
    }

    @Override
    public String findAddressForUUID(String uuid) throws ErrorException {
        DataCommon obj = database.findObject(uuid, null);
        Store store = (Store) database.findObject(obj.storeId, "StoreManager");
        return store.webAddress;
    }

    @Override
    public String findAddressForApplication(String uuid) throws ErrorException {
        DataCommon obj = database.findObject(uuid, "PageManager");
        DataCommon store = database.findObject(obj.storeId, "StoreManager");
        Store toreturn = (Store) store;
        return toreturn.webAddress;
    }
    

    @Override
    public void setApplicationList(List<String> ids, String partnerId, String password) throws ErrorException {
        if(!password.equals("vcxubidnsituenguidnskgjnsdg")) {
            throw new ErrorException(26);
        }
        
        PartnerData data = getPartnerDataInternal(partnerId);
        data.applications = ids;
        savePartnerData(data);
    }

    @Override
    public PartnerData getPartnerData(String partnerId, String password) throws ErrorException {
        if(!password.equals("vcxubidnsituenguidnskgjnsdg")) {
            return null;
        }
        PartnerData data = getPartnerDataInternal(partnerId);
        return data;
    }

    public List<String> getPartnerApplicationList(String partnerid) {
        PartnerData partner = getPartnerDataInternal(partnerid);
        return partner.applications;
    }

    private PartnerData getPartnerDataInternal(String partnerid) {
        PartnerData result = partnerData.get(partnerid);
        if(result == null) {
            result = new PartnerData();
            result.partnerId = partnerid;
        }
        return result;
    }

    private void savePartnerData(PartnerData data) throws ErrorException {
        partnerData.put(data.partnerId, data);
        data.storeId = storeId;
        databaseSaver.saveObject(data, credentials);
    }

    public String getPartnerId(String userId) {
        Partner partner = findPartnerForUser(userId);
        if(partner != null) {
            return partner.partnerId;
        }
        return null;
    }
    
    @Override
    public synchronized Store createWebPage(WebPageData data) throws ErrorException {
        String local = frameworkConfig.productionMode ? "" : ".local";
        if(storePool == null) {
            System.out.println("Storepool null?");
        }
        
        User user = new User();
        user.fullName = data.fullName;
        user.password = data.password;
        user.emailAddress = data.emailAddress;
        user.username = data.emailAddress;
        
        String address = storePool.incrementStoreCounter()+local+".getshop.com";
        Store store = storePool.createStoreObject(address, data.emailAddress, data.password, true);
        store.registrationUser = user;
        return store;
    }

    @Override
    public String startStoreFromStore(StartData startData) {
        Store store = storePool.getStore(startData.storeId);
        if (store == null) {
            return "";
        }
        
        if (!store.isTemplate) {
            throw new ErrorException(26);
        }
        
        
        int nextStoreId = storePool.incrementStoreCounter();
        
        String newAddress = nextStoreId + ".getshop.com";
        if (!frameworkConfig.productionMode) {
            newAddress = nextStoreId + ".2.0.local.getshop.com";
        }
        
        
        try {
            String newStoreId = copyStore(startData.storeId, newAddress, startData);
            storePool.loadStore(newStoreId, newAddress);
            
            GetShopSessionScope scope = AppContext.appContext.getBean(GetShopSessionScope.class);
            scope.setStoreId(newStoreId);
            UserManager userManager = AppContext.appContext.getBean(UserManager.class);
            User user = new User();
            user.fullName = startData.name;
            user.emailAddress = startData.email;
            user.cellPhone = startData.phoneNumber;
            user.password = startData.password;
            
            userManager.saveUser(user);
            
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            return "";
        } catch (ErrorException ex) {
            throw ex;
        }
       
        
        
        return newAddress;
    }
    
    public String copyStore(String originalStoreId, String newAddress, StartData start) throws UnknownHostException {
        Database database = AppContext.appContext.getBean(Database.class);
        String newStoreId = UUID.randomUUID().toString();
        
        Mongo m = new MongoClient("localhost", Database.mongoPort);

        for (String databaseName : m.getDatabaseNames()) {
            if (databaseName.equals("LoggerManager") || databaseName.equals("UserManager")) {
                continue;
            }

            String storeId = databaseName.equals("StoreManager") ? "all" : originalStoreId;
            String newStoreIdi = databaseName.equals("StoreManager") ? "all" : newStoreId;
            DB db = m.getDB(databaseName);
            DBCollection collection = db.getCollection("col_" + storeId);
            DBCursor stores = collection.find();

            Credentials cred = new Credentials(null);
            cred.manangerName = databaseName;
            cred.password =  newStoreId;
            cred.storeid = newStoreId;
            
            while (stores.hasNext()) {
                DBObject data = stores.next();
                Morphia morphia = new Morphia();
                morphia.map(DataCommon.class);
                DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, data);
                dataCommon.storeId = newStoreId;
                
                if (dataCommon instanceof Store) {
                    Store store = (Store)dataCommon;
                    
                    if (!store.id.equals(originalStoreId)) {
                        continue;
                    }
                    
                    store.webAddressPrimary = newAddress;
                    store.webAddress = null;
                    store.storeId = newStoreIdi;
                    store.id = newStoreId;
                    store.isTemplate = false;
                    store.configuration.emailAdress = start.email;
                    store.configuration.shopName = start.shopName;
                    
                    store.additionalDomainNames = new ArrayList();
                    database.save(store, cred);
                } else {
                    database.save(dataCommon, cred);
                }
            }
        }
        
        m.close();
        
        return newStoreId;
    }
}
