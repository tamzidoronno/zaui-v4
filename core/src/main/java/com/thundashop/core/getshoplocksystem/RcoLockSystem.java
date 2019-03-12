/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class RcoLockSystem extends LockServerBase implements LockServer {
    private HashMap<String, RcoLock> locks = new HashMap();
    
    @Override
    public Lock getLock(String id) {
        return locks.get(id);
    }

    @Override
    public List<Lock> getLocks() {
        return new ArrayList(locks.values());
    }

    @Override
    public void fetchLocksFromServer() {
        createLocks();
        saveMe();
    }

    @Override
    public void save() {
        saveMe();
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
    public void finalizeServer() {
    }
    
    @Override
    public void addTransactionHistory(String tokenId, String lockId, Date accessTime, int userSlot) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void openLock(String lockId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void closeLock(String lockId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addAccessHistoryEntranceDoor(String lockId, int code, Date date) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void createLocks() {
        for (int i=1; i<=30; i++) {
            final int deviceId = i;
            RcoLock lock = locks.values()
                    .stream()
                    .filter(l -> l.deviceId == deviceId)
                    .findFirst()
                    .orElse(new RcoLock());

            lock.connectedToServerId = this.id;
            lock.deviceId = i;
            lock.name = "RCO Lock "+i;
            lock.typeOfLock = "RCO";
            lock.maxnumberOfCodes = 3;
            lock.initializeUserSlots();
            locks.put(lock.id, lock);
        }
    }

    @Override
    public boolean hasAccessLogFeature() {
        return false;
    }

    @Override
    public void pulseOpenLock(String lockId) {
    }

    @Override
    public String getHostname() {
        return hostname;
    }

    
}
