/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import java.util.ArrayList;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void lockSettingsChanged(LockSettings lockSettings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deactivatePrioritingOfLock() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Lock getLock(String id) {
        setAllCodesAdded();
        return locks.get(id);
    }

    private void createLock(int deviceId) {
        LockBoxLock lockAlreadAdded = locks.values()
                .stream()
                .filter(l -> l.deviceId == deviceId)
                .findAny()
                .orElse(null);
        
        if (lockAlreadAdded != null) {
            return;
        }
        
        lockAlreadAdded = new LockBoxLock();
        lockAlreadAdded.maxnumberOfCodes = 1000;
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
    
}
