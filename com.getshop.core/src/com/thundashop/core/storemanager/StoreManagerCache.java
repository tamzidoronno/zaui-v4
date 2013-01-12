package com.thundashop.core.storemanager;

import com.thundashop.app.logomanager.data.SavedLogo;
import com.thundashop.core.common.CachingKey;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.storemanager.data.StoreConfiguration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class StoreManagerCache implements IStoreManager {
    private StoreManager manager;
    private final String addr;

    public StoreManagerCache(StoreManager manager, String addr) {
        this.manager = manager;
        this.addr = addr;
    }
    
    @Override
    public Store initializeStore(String webAddress, String initSessionId) throws ErrorException {
        Store result = manager.initializeStore(webAddress, initSessionId);
        
        CachingKey key = new CachingKey();
        
        LinkedHashMap<String, Object> keys = new LinkedHashMap();
        keys.put("webAddress", webAddress);
        keys.put("initSessionId", initSessionId);
        
        key.args = keys;
        key.interfaceName = getInterfaceName();
        key.sessionId = manager.getSession().id;
        key.method = "initializeStore";
        
        manager.getCacheManager().addToCache(key, result, manager.getStoreId(), addr);
        return null;
    }

    public void removeInitializeStore() throws ErrorException {
        Store store = manager.getStore();
        String sessionId = manager.getSession().id;
        HashMap<CachingKey, Object> allCachedObjects = manager.getCacheManager().getAllCachedObjects(manager.storeId);
        for(CachingKey key : allCachedObjects.keySet()) {
            if(key.sessionId.equals(sessionId) && key.method.equals("initializeStore") && key.interfaceName.equals(getInterfaceName())) {
                manager.getCacheManager().removeFromCache(key, store.id, addr);
            }
        }
    }
    
    @Override
    public Store getMyStore() throws ErrorException {
        return null;
    }

    @Override
    public Store saveStore(StoreConfiguration config) throws ErrorException {
        removeInitializeStore();
        return null;
    }

    @Override
    public Store setPrimaryDomainName(String domainName) throws ErrorException {
        removeInitializeStore();
        return null;
    }

    @Override
    public Store removeDomainName(String domainName) throws ErrorException {
        removeInitializeStore();
        return null;
    }

    @Override
    public Store createStore(String hostname, String email, String password) throws ErrorException {
        return null;
    }

    @Override
    public Store setIntroductionRead() throws ErrorException {
        removeInitializeStore();
        return null;
    }

    @Override
    public Store enableSMSAccess(boolean toggle, String password) throws ErrorException {
        removeInitializeStore();
        return null;
    }

    @Override
    public Store enableExtendedMode(boolean toggle, String password) throws ErrorException {
        removeInitializeStore();
        return null;
    }

    private String getInterfaceName() {
        return this.getClass().getInterfaces()[0].getCanonicalName().replace("com.thundashop.", "");
    }

    @Override
    public String getStoreId() throws ErrorException {
        return null;
    }
    
}
