/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.thundashop.core.databasemanager.Database;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author ktonder
 * 
 * @param <K1>
 * @param <DATAOBJECT>
 * @param <LIGHTWEIGHTOBJECT>
 */
public abstract class SmartDataMap<K1, DATAOBJECT extends DataCommon, LIGHTWEIGHTOBJECT extends LightWeight> {
    private Database database;
    private SmartDataMapData data;
    private final String storeId;
    public HashMap<String, DATAOBJECT> cachedBookings = new HashMap();
    public HashMap<String, LIGHTWEIGHTOBJECT> lightWeightObjects = new HashMap();
    private final Class manager;
   
    public String getDataMapId() {
        return getClass().getCanonicalName();
    }
    
    public SmartDataMap(Database database, String storeId, Class manager, Class<DATAOBJECT> type) {
        this.database = database;
        this.storeId = storeId;
        this.manager = manager;
        checkAndInitialize(type);
    }
    
    public void add(LIGHTWEIGHTOBJECT light) {
        lightWeightObjects.put(light.id, light);
    }
    
    public int size() {
        return lightWeightObjects.size();
    }

    public boolean isEmpty() {
        return lightWeightObjects.isEmpty();
    }

    public boolean containsKey(Object key) {
        return lightWeightObjects.values().stream()
                .filter(o -> o.mainObjectId.equals(key))
                .count() > 0;
    }

    public DATAOBJECT get(String key) {
        if (cachedBookings.get(key) == null) {
            DATAOBJECT order = database.get(key, manager, storeId);
            if (order != null) {
                cachedBookings.put(key, order);
            }
        }
        
        return cachedBookings.get(key);
    }

    public DATAOBJECT put(String key, DATAOBJECT value) {
        // TODO. create lightweight
        cachedBookings.put(key, value);
        return value;
    }

    public DATAOBJECT remove(String key) {
        DATAOBJECT toRemove = cachedBookings.remove(key);
        List<String> idsToRemove = lightWeightObjects.values()
                .stream()
                .filter(b -> b.mainObjectId.equals(key))
                .map(b -> b.id)
                .collect(Collectors.toList());
        
        idsToRemove.stream()
                .forEach(id -> {
                    lightWeightObjects.remove(id);
                });
        
        return toRemove;
    }


    public Set<String> keySet() {
        loadAll();
        return cachedBookings.keySet();
    }

    public Collection<DATAOBJECT> values() {
        loadAll();
        return cachedBookings.values();
    }

    public void save(DATAOBJECT booking) {
        cachedBookings.put(booking.id, booking);
        LIGHTWEIGHTOBJECT lightWeight = getLightWeight(booking.id);
        LIGHTWEIGHTOBJECT newLightWeight = (LIGHTWEIGHTOBJECT) booking.createLightWeight();
        
        if (newLightWeight == null)
            return;
        
        newLightWeight.storeId = storeId;
        
        if (lightWeight != null) {
            newLightWeight.id = lightWeight.id;
        }
        
        database.save(manager, newLightWeight);
        lightWeightObjects.put(newLightWeight.id, newLightWeight);   
    }

    private void loadAll() {
        lightWeightObjects.values().stream().forEach(lightweight -> get(lightweight.mainObjectId));
    }

    protected void checkAndInitialize(Class<DATAOBJECT> lightWeightClass) {
        if (data == null) {
            data = database.get(getDataMapId(), manager, storeId);
        }
        
        if (data == null) {
            data = new SmartDataMapData();
            data.id = getDataMapId();
            data.storeId = storeId;
            database.save(manager, data);
        }
     
        if (data.currentVersion != getVersionNumber()) {
            data = new SmartDataMapData();
            data.id = getDataMapId();
            data.storeId = storeId;
            data.currentVersion = getVersionNumber();
            rebuildLightWeighters(lightWeightClass);
            database.save(manager, data);
        }
    }
    
    public abstract int getVersionNumber();   

    private void rebuildLightWeighters(Class<DATAOBJECT> type) {
        database.getAll(manager, storeId).forEach(data -> {
            if (type.isInstance(data)) {
                LightWeight lightWeight = data.createLightWeight();
                if (lightWeight != null) {
                    lightWeight.storeId = storeId;
                    database.save(manager, lightWeight);
                    lightWeightObjects.put(lightWeight.id, (LIGHTWEIGHTOBJECT)lightWeight);
                }
            }
        });
    }
    
    public void clear() {
        lightWeightObjects.clear();
        cachedBookings.clear();
    }

    private LIGHTWEIGHTOBJECT getLightWeight(String id) {
        return lightWeightObjects.values()
                .stream()
                .filter(l -> l.mainObjectId.equals(id))
                .findFirst()
                .orElse(null);
    }
}