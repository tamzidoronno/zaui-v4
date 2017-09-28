/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IGetShopLockSystemManager {
    
    @Administrator
    public void createServer(String type, String hostname, String userName, String password, String givenName);
    
    @Administrator
    public List<LockServer> getLockServers();
    
    @Administrator
    public void deleteServer(String serverId);
    
    @Administrator
    public void startFetchingOfLocksFromServer(String serverId);
    
    @Administrator
    public void updateConnectionDetails(String serverId, String hostname, String username, String password, String givenName);
    
    @Administrator
    public Lock getLock(String serverId, String lockId);
    
    @Administrator
    public void generateNewCodesForLock(String serverId, String lockId);
    
    @Administrator
    public void prioritizeLockUpdate(String serverId, String lockId);
    
    @Administrator
    public void markCodeForDeletion(String serverId, String lockId, int slotId);
    
    @Administrator
    public void markCodeForResending(String serverId, String lockId, int slotId);
    
    @Administrator
    public void createNewLockGroup(String name, int maxUsersInGroup);   
    
    /**
     * 
     * 
     * @param groupId
     * @param lockIds Key = serverId, and value is a list of lockids for the server.
     */
    @Administrator
    public void setLocksToGroup(String groupId, Map<String,List<String>> lockIds);
    
    @Administrator
    public LockGroup getGroup(String groupId);
    
    @Administrator
    public List<LockGroup> getAllGroups();
    
    
    @Administrator
    public String getNameOfGroup(String groupId);
    
    @Administrator
    public void deleteGroup(String groupId);
    
    @Administrator
    public void lockSettingsChanged(LockSettings lockSettings);
    
    @Administrator
    public void deactivatePrioritingOfLock(String serverId);
    
    /***** Group code stuff *****/
    @Administrator
    public void changeCode(String groupId, int slotId, int pinCode, String cardId);
    
    @Administrator
    public void renewCodeForSlot(String groupId, int slotId);
    
    @Administrator
    public LockCode getNextUnusedCode(String groupId);
    
    @Administrator
    public void changeDatesForSlot(String groupId, int slotId, Date startDate, Date endDate);
    /***** End group code stuff *****/
}