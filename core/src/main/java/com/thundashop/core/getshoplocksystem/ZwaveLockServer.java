/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import com.thundashop.core.getshoplocksystem.zwavejobs.ZwaveThread;
import com.thundashop.core.getshoplocksystem.zwavejobs.ZwaveAddCodeThread;
import com.thundashop.core.getshoplocksystem.zwavejobs.ZwaveRemoveCodeThread;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.thundashop.core.common.ExcludeFromJson;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.getshoplocksystem.zwavejobs.ZwaveJobPriotizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mongodb.morphia.annotations.Transient;

/**
 * This is a representation for a razberry zwave server.
 * 
 * @author ktonder
 */
public class ZwaveLockServer extends LockServerBase implements LockServer {
    private Map<String, LocstarLock> locks = new HashMap();

    @Transient
    @ExcludeFromJson
    private ZwaveThread currentThread; 
    
    public ZwaveLockServer() {
    }

    @Override
    public List<Lock> getLocks() {
        return new ArrayList(locks.values());
    }

    @Override
    public void fetchLocksFromServer() {
        String address = "ZWave.zway/Run/devices";
        String result = httpLoginRequestZwaveServer(address);
        
        List<LocstarLock> fetchedLocks = createDevicesFromServerResult(result);
        fetchedLocks.stream().forEach(lock -> {
            
            LocstarLock oldLock = (LocstarLock) locks.values().stream()
                    .filter(l -> ((LocstarLock)l).zwaveDeviceId == lock.zwaveDeviceId)
                    .findFirst()
                    .orElse(null);
            
            if (oldLock != null) {
                lock.setUserSlots(oldLock.getUserSlots());
                lock.id = oldLock.id;
            } 

            locks.put(lock.id, lock);
        });
        
        saveMe();
    }

    private List<LocstarLock> createDevicesFromServerResult(String result) throws JsonSyntaxException {
        List<LocstarLock> locks = new ArrayList();
        
        Gson gson = new Gson();
        JsonElement element = gson.fromJson(result, JsonElement.class);
        if (element == null || !element.isJsonObject()) {
            lostConnection();
            return locks;
        }
        
        JsonObject jsonObj = element.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObj.entrySet()) {
            String deviceId = entry.getKey();
            JsonElement innerElement = entry.getValue();
            if (!innerElement.isJsonObject()) {
                continue;
            }

            if (innerElement.getAsJsonObject().get("instances") != null) {
                JsonElement instances = innerElement.getAsJsonObject().get("instances");
                if (instances.getAsJsonObject().get("0") != null) {
                    JsonElement instance = instances.getAsJsonObject().get("0");
                    
                    if (instance.getAsJsonObject() != null
                            && instance.getAsJsonObject().get("commandClasses") != null 
                            && instance.getAsJsonObject().get("commandClasses").getAsJsonObject().get("99") != null) {
                        
                        JsonObject userCode = instance.getAsJsonObject()
                                .get("commandClasses").getAsJsonObject()
                                .get("99").getAsJsonObject()
                                .get("data").getAsJsonObject();
                        
                        JsonObject data = innerElement.getAsJsonObject()
                                .get("data").getAsJsonObject();
                        
                        String name = "";
                        if (data.get("givenName").getAsJsonObject() != null && !data.get("givenName").getAsJsonObject().get("value").isJsonNull()) {
                            name = data.get("givenName").getAsJsonObject()
                                .get("value").getAsString();
                            }
                        
                        LocstarLock lock = new LocstarLock();
                        if (userCode.get("maxUsers").getAsJsonObject().get("value").isJsonNull()) {
                            lock.maxnumberOfCodes = 20;
                        } else {
                            lock.maxnumberOfCodes = userCode.get("maxUsers").getAsJsonObject().get("value").getAsInt();
                        }
                        lock.zwaveDeviceId = Integer.parseInt(deviceId);
                        lock.name = name;
                        lock.connectedToServerId  = id;
                        lock.initializeUserSlots();
                        locks.add(lock);
                    }   
                }       
            }
        }
        
        return locks;
    }

    @Override
    public Lock getLock(String lockId) {
        return locks.get(lockId);
    }

    @Override
    public void save() {
        saveMe();
    }
    
    public void finalizeServer() {
        locks.values()
                .stream()
                .forEach(l -> l.finalize());
    }
    
    public void threadDone(ZwaveThread thread) {
        if (currentThread == null) {
            return;
        }
        
        if (!thread.successfullyCompleted) {
            threadFailed(thread);
        } else {
            LocstarLock lock = locks.get(thread.getLockId());
            if (lock != null && lock.prioritizeLockUpdate && lock.getJobSize() == 0) {
                lock.prioritizeLockUpdate = false;
                saveMe();
            }
        }
        
        startNextThread(true);
    }
    
    public void stopCurrentJob() {
        if (currentThread != null) {
            currentThread.stop();
        }
    }

    private synchronized void startNextThread(boolean stopOldThread) {
        if (stopOldThread) {
            currentThread = null;
        }
        
        if (currentThread == null) {
            ZwaveJobPriotizer jobMaker = new ZwaveJobPriotizer(new ArrayList(locks.values()));
            LocstarLock lockToWorkWith = jobMaker.getNextLock();
            
            if (lockToWorkWith != null) {
                ZwaveThread nextThread = createJob(lockToWorkWith);
                if (nextThread != null) {
                    currentThread = nextThread; 
                    new Thread(nextThread).start();
                }            
            } else {
                GetShopLogHandler.logPrintStatic("No more jobs to do, or waiting because of failed locks.", storeId);
            }
        } 
    }
    
    @Override
    public void startUpdatingOfLocks() {
        startNextThread(false);
    }
 
    @Override
    public void prioritizeLock(String lockId) {
       
        
        if (locks.get(lockId) == null) {
            return;
        }
        
        deactivatePrioritingOfLock();
        
        LocstarLock lock = locks.get(lockId);
        lock.prioritizeLockUpdate = true;
        startNextThread(false);
        saveMe();
    }

    public void threadFailed(ZwaveThread thread) {
        String lockId = thread.getLockId();
        Lock lock = getLock(lockId);
        if (lock != null) {
            lock.delayUpdateForFiveMinutes();
        }
        saveMe();
    }

    @Override
    public void codeRemovedFromLock(String id, UserSlot slot) {
        super.codeRemovedFromLock(id, slot); 
        LocstarLock lock = (LocstarLock) getLock(id);
        startNextThread(false);
    }

    @Override
    public void markCodeForDeletion(String lockId, int slotId) {
        super.markCodeForDeletion(lockId, slotId);
        startNextThread(false);
        saveMe();
    }

    @Override
    public void markCodeForResending(String lockId, int slotId) {
        super.markCodeForResending(lockId, slotId);
        startNextThread(false);
        saveMe();
    }

    @Override
    public void lockSettingsChanged(LockSettings lockSettings) {
        LocstarLock lock = (LocstarLock) getLock(lockSettings.lockId);
        if ( lock != null) {
            if (lockSettings.zwaveDeviceId != null) {
                lock.zwaveDeviceId = lockSettings.zwaveDeviceId;
                saveMe();
            }
        }
    }

    private ZwaveThread createJob(LocstarLock lockToWorkWith) {
        
        lockToWorkWith.finalize();
        
        if (!lockToWorkWith.getToRemove().isEmpty()) {
            return new ZwaveRemoveCodeThread(this, lockToWorkWith.getToRemove().get(0), lockToWorkWith, false, storeId);
        }
        
        if (!lockToWorkWith.getToUpdate().isEmpty()) {
            return new ZwaveAddCodeThread(this, lockToWorkWith.getToUpdate().get(0), lockToWorkWith, storeId);
        }
        
        return null;
    }

    @Override
    public void deactivatePrioritingOfLock() {
        if (currentThread != null) {
            currentThread.stop();
        }
         
        locks.values().stream()
            .forEach(l -> {
                l.prioritizeLockUpdate = false;
            });
        
    }

}