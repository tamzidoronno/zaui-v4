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
}
