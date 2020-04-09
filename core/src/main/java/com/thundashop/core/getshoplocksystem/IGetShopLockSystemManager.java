/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.FilterOptions;
import com.thundashop.core.common.FilteredData;
import com.thundashop.core.common.ForceAsync;
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
    public void createServer(String type, String hostname, String userName, String password, String givenName, String token);
    
    @Administrator
    public List<LockServer> getLockServers();
    
    @Administrator
    public void deleteServer(String serverId);
    
    @Administrator
    public void startFetchingOfLocksFromServer(String serverId);
    
    @Administrator
    public void updateConnectionDetails(String serverId, String hostname, String username, String password, String givenName, String token);
    
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
    public void markCodeAsUpdatedOnLock(String serverId, String lockId, int slotId);
    
    @Administrator
    public void saveLocstarLock(String serverId, LocstarLock lock);
    
    @Administrator
    public LockGroup createNewLockGroup(String name, int maxUsersInGroup, Integer codeSize);   
    
    @Administrator
    public String restCall(String serverId, String path);
    
    @Administrator
    public void pingServers();
    
    @Administrator
    public void updateZwaveRoute(String serverId, String lockId);
    
    @Administrator
    public void forceDeleteSlot(String serverId, String lockId, int slotId);
    
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
    public List<LockGroup> getAllGroupsUnfinalized();    
    
    @Administrator
    public String getNameOfGroup(String groupId);
    
    @Administrator
    public void deleteGroup(String groupId);
    
    @Administrator
    public void lockSettingsChanged(LockSettings lockSettings);
    
    @Administrator
    public void deactivatePrioritingOfLock(String serverId);
    
    @Administrator
    public void triggerCheckOfCodes(String serverId);
    
    @Administrator
    public void toggleActiveServer(String serverId);
    
    @Administrator
    public void triggerCronTab();
    
    /***** Group code stuff *****/
    @Administrator
    public void changeCode(String groupId, int slotId, int pinCode, String cardId);
    
    @Administrator
    public void renewCodeForSlot(String groupId, int slotId);
    
    @Administrator
    public LockCode getNextUnusedCode(String groupId, String reference, String managerName, String textReference);
    
    @Administrator
    public void changeDatesForSlot(String groupId, int slotId, Date startDate, Date endDate);
    
    /***** End group code stuff *****/
    
    @Administrator
    public AccessGroupUserAccess grantAccessDirect(String groupId, AccessGroupUserAccess user);
    
    @Administrator
    public void removeAccess(String id);
    
    @Administrator
    public FilteredData getAllAccessUsers(FilterOptions options);

    @Administrator
    public List<AccessGroupUserAccess> getAllAccessUsersFlat();
    
    @Administrator
    public AccessGroupUserAccess getAccess(String userId);
    
    @Administrator
    public void sendSmsToCustomer(String userId, String textMessage);
    
    @Administrator
    public void sendEmailToCustomer(String userId, String subject, String body);
    
    @Administrator
    public void saveUser(AccessGroupUserAccess user);
    
    @Administrator
    public boolean isSlotTakenInUseInAnyGroups(String serverId, String lockId, int slotId);
    
    @Administrator
    public void setGroupVirtual(String groupId, boolean isVirtual);
    
    public void addTransactionHistory(String tokenId, String lockId, Date timeStamp, int userSlot);
    
    public void addTransactionHistorySeros(String tokenId, String lockId, Date timeStamp, String keyId, int userSlot);
    
    public void addTransactionEntranceDoor(String serverId, String lockId, int code);
    
    public void addTransactionEntranceDoorByToken(String tokenId, int lockIncrementalId, int code);
    
    @Administrator
    public List<AccessHistoryResult> getAccessHistory(String groupId, Date start, Date end, int groupSlotId);
    
    @Administrator
    @ForceAsync
    public List<UserSlot> getCodesInUse(String serverId, String lockId);
    
    @ForceAsync
    public Map<Integer, List<UserSlot>> getCodesByToken(String tokenId);
    
    @Administrator
    public void setCodeSize(int codeSize);
    
    @Administrator
    public int getCodeSize();
    
    @Administrator
    public void renameLock(String serverId, String lockId, String name);
    
    @Administrator
    public boolean canShowAccessLog();
    
    @Administrator
    public FilteredData getAccessLog(String serverId, String lockId, FilterOptions filterOptions);
    
    @Administrator
    public void openLock(String lockId);
    
    @Administrator
    public void closeLock(String lockId);
    
    @Administrator
    public void deleteLock(String serverId, String lockId);
    
    @Administrator
    public void resyncDatabaseWithLoraGateway(String serverId);
    
    @Administrator
    public void deleteSlot(String serverId, String lockId, int slotId);
    
    @Administrator
    public void changeNameOnGorup(String groupdId, String name);
}