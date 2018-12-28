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
import com.thundashop.core.getshoplock.GetshopLockCom;
import com.thundashop.core.getshoplocksystem.zwavejobs.ZwaveJobPriotizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
    
    private boolean activated = true;
    
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
            
            if (oldLock == null) {
                locks.put(lock.id, lock);
                return;
            } 

            oldLock.maxnumberOfCodes = lock.maxnumberOfCodes;
            oldLock.name = lock.name;
            lock.id = oldLock.id;
            
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
            saveMe();
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

    public void toggleActivated() {
        this.activated = !this.activated;
        
        if (!this.activated) {
            stopCurrentJob();
        }
    }
    
    private synchronized void startNextThread(boolean stopOldThread) {
       
        if (stopOldThread) {
            currentThread = null;
        }
        
        if (!this.activated) {
            return;
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
            lock.delayUpdateForMinutes(120);
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
            UserSlot slotToUpdate = null;
            
            if (lockToWorkWith.getToUpdate().size() == 1) {
                slotToUpdate = lockToWorkWith.getToUpdate().get(0);
            } else {
                slotToUpdate = getRandomSlot(lockToWorkWith);
            }
            
            return new ZwaveAddCodeThread(this, slotToUpdate, lockToWorkWith, storeId);
        }
        
        return null;
    }

    private UserSlot getRandomSlot(LocstarLock lockToWorkWith) {
        UserSlot slotToUpdate;
        int min = 0;
        int max = lockToWorkWith.getToUpdate().size() - 1;
        Random rn = new Random();
        int slot = rn.nextInt(max - min + 1) + min;
        slotToUpdate = lockToWorkWith.getToUpdate().get(slot);
        return slotToUpdate;
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

    @Override
    public void markCodeAsUpdatedOnLock(String lockId, int slotId) {
        super.markCodeAsUpdatedOnLock(lockId, slotId);
    }

    @Override
    public void addTransactionHistory(String tokenId, String deviceId, Date accessTime, int userSlot) {
        if (checkToken(tokenId))
            return;
        
        Lock lock = getLockByDeviceId(deviceId);
        
        if (lock != null) {
            addAccessHistory(lock.id, userSlot, accessTime);
        }
        
        getManager().getAllGroups().stream()
                    .filter(o -> o.isConnectedToLock(lock.id))
                    .forEach(group -> {
                        AccessEvent event = new AccessEvent();
                        event.groupId = group.id;
                        event.lockId = lock.id;
                        event.date = accessTime;
                        addEvent(event);
                    });
    }
        

    private Lock getLockByDeviceId(String deviceId) {
        int deviceIdInt = Integer.parseInt(deviceId);
        
        return locks.values().stream()
                .filter(l -> l.zwaveDeviceId == deviceIdInt)
                .findFirst()
                .orElse(null);
    }

    public void saveLocstarLock(LocstarLock lock) {
        locks.put(lock.id, lock);
    }

    @Override
    public void openLock(String lockId) {
        Lock lock = getLock(lockId);
        if (lock != null) {
            String address = "ZWave.zway/Run/devices["+lock.id+"].instances[0].commandClasses[98].Set(1,0,0,1,1)";
            httpLoginRequestZwaveServer(address);
        }
    }

    @Override
    public void pulseOpenLock(String lockId) {
        Lock lock = getLock(lockId);
        if (lock != null) {
            String address = "ZWave.zway/Run/devices["+lock.id+"].instances[0].commandClasses[98].Set(0)";
            httpLoginRequestZwaveServer(address);
        }
    }

    @Override
    public void closeLock(String lockId) {
        Lock lock = getLock(lockId);
        if (lock != null) {
            String address = "ZWave.zway/Run/devices["+lock.id+"].instances[0].commandClasses[98].Set(1,255,255,1,1)";
            httpLoginRequestZwaveServer(address);
        }
    }

    @Override
    public void addAccessHistoryEntranceDoor(String lockId, int code, Date date) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void updateRouting(String lockId) {
        LocstarLock lock = locks.get(lockId);
        
        String ids = "";
        if(lock.routing.size() > 0) { ids += lock.routing.get(0) + ","; } else { ids += "0,"; }
        if(lock.routing.size() > 1) { ids += lock.routing.get(1) + ","; } else { ids += "0,"; }
        if(lock.routing.size() > 2) { ids += lock.routing.get(2) + ","; } else { ids += "0,"; }
        if(lock.routing.size() > 3) { ids += lock.routing.get(3); } else { ids += "0"; }
        
        LocstarLock lstrlock = (LocstarLock) lock;
        String address = "JS/Run/zway.SetPriorityRoute("+lstrlock.zwaveDeviceId+","+ids+",3)";
        httpLoginRequestZwaveServer(address);
    }

    @Override
    public boolean hasAccessLogFeature() {
        return true;
    }

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public void setLastPing(Date date) {
        this.lastPing = date;
    }

    @Override
    public Date getLastPing() {
        return lastPing;
    }


}