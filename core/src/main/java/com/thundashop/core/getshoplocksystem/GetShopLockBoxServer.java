/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import com.thundashop.core.getshoplock.GetshopLockCom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class GetShopLockBoxServer extends LockServerBase implements LockServer {
    public Map<String, LockBoxLock> locks = new HashMap();

    @Override
    public List<Lock> getLocks() {
        setAllCodesAdded();
        
        return new ArrayList(locks.values());
    }

    @Override
    public void fetchLocksFromServer() {
        createLock(1);
        createLock(2);
        saveMe();
    }

    @Override
    public void save() {
        saveMe();
    }

    @Override
    protected void saveMe() {
        setAllCodesAdded();
        super.saveMe(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void prioritizeLock(String lockId) {
    }

    @Override
    public void lockSettingsChanged(LockSettings lockSettings) {
    }

    @Override
    public void deactivatePrioritingOfLock() {
    }

    @Override
    public Lock getLock(String id) {
        setAllCodesAdded();
        return locks.get(id);
    }

    private void createLock(int deviceId) {
        LockBoxLock lockAlreadAdded = locks.values()
                .stream()
                .filter(l -> l.name.equals("Lock " + deviceId))
                .findAny()
                .orElse(null);
        
        if (lockAlreadAdded != null) {
            lockAlreadAdded.maxnumberOfCodes = 2000;
            lockAlreadAdded.initializeUserSlots();
            saveMe();
            return;
        }
        
        lockAlreadAdded = new LockBoxLock();
        lockAlreadAdded.maxnumberOfCodes = 2000;
        lockAlreadAdded.name = "Lock " + deviceId;
        lockAlreadAdded.connectedToServerId  = id;
        lockAlreadAdded.id = UUID.randomUUID().toString();
        lockAlreadAdded.initializeUserSlots();
        locks.put(lockAlreadAdded.id, lockAlreadAdded);
    }

    @Override
    public void finalizeServer() {
        setAllCodesAdded();
    }

    private void setAllCodesAdded() {
        locks.values().stream().forEach(l -> {
            l.getUserSlots().stream().forEach(slot -> {
                if (slot.code != null) {
                    slot.codeAddedSuccesfully();
                }
            });
        });
    }

    @Override
    public void markCodeAsUpdatedOnLock(String lockId, int slotId) {
        // Nothing to do yet.
    }

    @Override
    public void addTransactionHistory(String tokenId, String lockId, Date accessTime, int userSlot) {
        if (checkToken(tokenId))
            return;
        
        Lock lock = getLock(lockId);
        
        if (lock != null) {
            addAccessHistory(lockId, userSlot, accessTime);
        }
    }
    
    @Override
    public void openLock(String lockId) {
        Lock lock = getLock(lockId);
        if (lock != null) {
            String postfix = "?username="+username+"&password="+password+"&deviceid=1&forceopen=on";
            String address = "http://"+hostname+":18080/" + postfix;
            try {
                httpLoginRequest(address, username, password);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void closeLock(String lockId) {
        Lock lock = getLock(lockId);
        if (lock != null) {
            String postfix = "?username="+username+"&password="+password+"&deviceid=1&forceopen=off";
            String address = "http://"+hostname+":18080/" + postfix;
            try {
                httpLoginRequest(address, username, password);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private String httpLoginRequest(String address, String username, String password) throws Exception {
        return GetshopLockCom.httpLoginRequest(address, username, password);
    }

    @Override
    public void addAccessHistoryEntranceDoor(String lockId, int code, Date date) {
        Lock lock = getLock(lockId);
        
        lock.getUserSlots().stream()
                .filter(slot -> slot.code != null && slot.code.pinCode == code)
                .forEach(slot -> {
                    addAccessHistory(lockId, slot.slotId, date);
                });
    }

    @Override
    public boolean hasAccessLogFeature() {
        return true;
    }

    @Override
    public void pulseOpenLock(String lockId) {
    }
}
