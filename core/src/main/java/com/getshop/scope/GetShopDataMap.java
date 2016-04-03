/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.scope;

import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Session;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.storemanager.StoreManager;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;



public class GetShopDataMap<K, V>  implements Map<K,V>  {
    public Map<K, V> dataMap = new HashMap();
    public String storeId = "";
    StoreManager storeManager;
    StoreApplicationPool storeApplicationPool = null;
    public Database database;
    private long currentThreadId = -100;
    private String storeMainLanguage = "";
    
    private Session session;
    public ManagerBase parentManager;
 
    
    @Override
    public int size() {
        return dataMap.size();
    }

    @Override
    public boolean isEmpty() {
        return dataMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return dataMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return dataMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
       V data = dataMap.get(key);
       updateTranslationObject(data);
       return data;
    }

    @Override
    public V put(K key, V value) {
        updateTranslationObject(value);
        return dataMap.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return dataMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (V value : m.values()){
            updateTranslationObject(value);
        }
        
        dataMap.putAll(m);
    }

    @Override
    public void clear() {
        dataMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return dataMap.keySet();
    }

    @Override
    public Collection<V> values() {
        for (V v : dataMap.values()) {
            updateTranslationObject(v);
        }
        
        return dataMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return dataMap.entrySet();
    }

    void setSession(Session session) {
        this.session = session;
    }

    void clearSession() {
        this.session = null;
    }

    private void updateTranslationObject(V data) {
        if (!(data instanceof DataCommon)) {
            return;
        }
        
        getStoreMainLanguage();
    
        if (storeMainLanguage == null || storeMainLanguage.equals("")) {
            return;
        }
        
        String currentLanguage;
        if (session == null || session.language == null || session.language.equals("")) {
            currentLanguage = storeMainLanguage;
        } else {
            currentLanguage =  session.language; 
        }
        
        
        DataCommon dataCommon = (DataCommon)data;
        boolean saved = dataCommon.updateTranslation(currentLanguage);
        
        if (saved) {
            if (parentManager != null 
                    && database != null 
                    && dataCommon.storeId != null 
                    && dataCommon.id != null 
                    && !dataCommon.storeId.equals("") 
                    && !dataCommon.id.equals("")) {
                database.save(dataCommon, parentManager.getCredentials());
            }
        }
    }

    private void getStoreMainLanguage() {
        if (currentThreadId == Thread.currentThread().getId()) {
            return;
        }
        
        Application application = storeApplicationPool.getApplication("d755efca-9e02-4e88-92c2-37a3413f3f41");
        
        if (application != null) {
            storeMainLanguage = application.getSetting("language");
        }
        
        currentThreadId = Thread.currentThread().getId();
    }
}