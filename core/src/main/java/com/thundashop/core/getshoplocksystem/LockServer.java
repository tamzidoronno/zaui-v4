/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public interface LockServer {

    public List<Lock> getLocks();
    
    public void fetchLocksFromServer();
    
    public void setManger(GetShopLockSystemManager manager);
    
    public void setDetails(String hostname, String userName, String password, String givenName, String token);

    public Lock getLock(String lockId);
    public String getHostname();
    public void setLastPing(Date date);
    public Date getLastPing();

    public void save();
    
    public void prioritizeLock(String lockId);

    public void markCodeForDeletion(String lockId, int slotId);
    
    public void markCodeForResending(String lockId, int slotId);

    public String getId();

    public void claimLockGroupUseOfSlot(String lockId, int slotId, String groupId);

    public void releaseFromGroup(String lockId, String groupId);

    public void syncGroupSlot(LockGroup group, int slotId);
            
    public void syncGroup(LockGroup group);

    public void lockSettingsChanged(LockSettings lockSettings);

    public void deactivatePrioritingOfLock();

    public void finalizeServer();

    public void markCodeAsUpdatedOnLock(String lockId, int slotId);

    public void addTransactionHistory(String tokenId, String lockId, Date accessTime, int userSlot);

    public void openLock(String lockId);

    public void pulseOpenLock(String lockId);

    public void closeLock(String lockId);

    public void renameLock(String lockId, String name);
    
    public List<AccessEvent> getAccessEvents();

    public void addAccessHistoryEntranceDoor(String lockId, int code, Date date);

    public String getGivenName();
    
    public String getAccessToken();
    
    public boolean hasAccessLogFeature();

    public void deleteLock(String lockId);

    public boolean useSlotConcept();

    public void setLockstoGroup(String groupId, List<String> lockIds);

    public List<String> getLocksForGroup(String id);

    public String toString();
}
