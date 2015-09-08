/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.scope;

import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Session;
import com.thundashop.core.storemanager.StoreManager;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */

@Component
@GetShopSession
public class GetShopDataMapRepository<K, V> extends GetShopSessionObject {
    List<GetShopDataMap<K, V>> maps = new ArrayList();
    
    @Autowired
    private StoreManager storeManager;
    
    @Autowired
    private StoreApplicationPool storeApplicationPool;
    
    @Autowired
    public DatabaseSaver databaseSaver;
    
    public GetShopDataMap<K, V> createNew(ManagerBase parentManager) {
        GetShopDataMap<K, V> retMap = new GetShopDataMap<K, V>();
        retMap.storeId = storeId;
        retMap.storeManager = storeManager;
        retMap.storeApplicationPool = storeApplicationPool;
        retMap.databaseSaver = databaseSaver;
        retMap.parentManager = parentManager;
        maps.add(retMap);
        return retMap;
    }
    
    @Override
    public void setSession(Session session) {
        for (GetShopDataMap<K, V> map : maps) {
            map.setSession(session);
        }
    }

    @Override
    public void clearSession() {
        for (GetShopDataMap<K, V> map : maps) {
            map.clearSession();
        }
    }
}
