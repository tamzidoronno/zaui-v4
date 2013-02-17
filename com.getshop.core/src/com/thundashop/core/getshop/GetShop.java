package com.thundashop.core.getshop;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshop.data.GetshopStore;
import com.thundashop.core.getshop.data.Partner;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class GetShop extends ManagerBase implements IGetShop {
    
    private HashMap<String, List<Partner>> partners;

    @Autowired
    public GetShop(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
        isSingleton = true;
        storeId = "all";
        partners = new HashMap();
    }
    
    @Autowired
    public Database database;
    
    @Override
    public List<GetshopStore> getStores(String code) {
        if (!code.equals("laskdjf034j523lknjksdjnfklqnwgflkjangfasnlkjqnwtwpdsfguoq32905htkasjdfklsdfnaksldth342o5hiy4n4vnyesfjn"))
            return new ArrayList();
        
        List<GetshopStore> retstores = fetchStoreFromDB(null);
        return retstores;
    }

    @PostConstruct
    public void init() {
        initialize();
    }
    
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon obj : data.data) {
            if(obj instanceof Partner) {
                Partner toAdd = (Partner) obj;
                List<Partner> partnerList = getPartnerList(toAdd.partnerId);
                partnerList.add(toAdd);
            }
        }
    }
    
    private void addUserInformation(GetshopStore getshopstore, Store store) {
        Credentials credentials = new Credentials(UserManager.class);
        credentials.manangerName = "UserManager";
        credentials.storeid = store.id;
        
        List<DataCommon> users = database.retreiveData(credentials);
        for (DataCommon duser : users) {
            if (duser instanceof User) {
                User user = (User)duser;
                if (user.type == User.Type.ADMINISTRATOR) {
                    getshopstore.email = user.emailAddress;
                    getshopstore.lastLoggedIn = user.lastLoggedIn;
                    getshopstore.username = user.username;
                }
            }
        }
    }

    
    
    @Override
    public void addUserToPartner(String userId, String partner, String password) throws ErrorException {
        
        if(!password.equals("feerfdafeerfdsaf433453bvcbhgftrhyer56346453465346")) {
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
        for(List<Partner> userList : partners.values()) {
            for(Partner partner : userList) {
                if(partner.userId.equals(userId)) {
                    toRemove = partner;
                }
            }
            
            if(toRemove != null) {
                userList.remove(toRemove);
            }
        }
    }

    private HashMap<String, List<Partner>> getPartnerList() {
        if(partners == null) {
            partners = new HashMap();
        }
        
        return partners;
    }

    private List<Partner> getPartnerList(String partner) {
        List<Partner> partnerList = partners.get(partner);
        if(partnerList == null) {
            partnerList = new ArrayList();
            partners.put(partner, partnerList);
        }
        return partnerList;
    }
    
    public boolean isPartner(String partnerId, String userId) {
        for (Partner partner : getPartnerList(partnerId)) {
            if (partner.userId.equals(userId))
                return true;
        }
        
        return false;
    }

    @Override
    public List<GetshopStore> getStoresConnectedToMe() throws ErrorException {
        String userId = this.getSession().currentUser.id;
        Partner partner = findPartnerForUser(userId);
        
        if(partner == null) {
            return new ArrayList();
        }
        
        List<GetshopStore> retval = fetchStoreFromDB(partner.partnerId);
        return retval;
    }

    private List<GetshopStore> fetchStoreFromDB(String partnerId) {
        Credentials credentials = new Credentials(StoreManager.class);
        credentials.manangerName = StoreManager.class.getSimpleName();
        credentials.storeid = "all";
        
        List<GetshopStore> retstores = new ArrayList();
        List<DataCommon> stores = database.retreiveData(credentials);
        for (DataCommon storec : stores) {
            if (storec instanceof Store) {
                Store store = (Store)storec;
                if(partnerId != null && (store.partnerId == null || !partnerId.equals(store.partnerId))) {
                    continue;
                }
                
                GetshopStore getshopstore = new GetshopStore();
                getshopstore.webaddress = store.webAddress;
                getshopstore.created = store.rowCreatedDate;
                if (store.configuration != null)
                    getshopstore.configEmail = store.configuration.emailAdress;
                
                addUserInformation(getshopstore, store);
                retstores.add(getshopstore);
            }
        }
        
        return retstores;
    }

    private Partner findPartnerForUser(String userId) {
        HashMap<String, List<Partner>> partnerList = getPartnerList();
        for(List<Partner> partners : partnerList.values()) {
            for(Partner partner : partners) {
                if(partner.userId.equals(userId)) {
                    return partner;
                }
            }
        }
        
        return null;
    }

    @Override
    public void connectStoreToPartner(String partner) throws ErrorException {
        Store store = getStore();
        StoreManager storeManager = getManager(StoreManager.class);
        storeManager.connectStoreToPartner(store.id, partner);
    }

    @Override
    public String getPartnerId() throws ErrorException {
        String userId = getSession().currentUser.id;
        Partner partner = findPartnerForUser(userId);
        if(partner != null) {
            return partner.partnerId;
        }
        
        return "";
    }
}
