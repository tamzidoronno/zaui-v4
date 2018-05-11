/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FilterOptions;
import com.thundashop.core.common.FilteredData;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MailMessage;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.messagemanager.SmsMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class GetShopLockSystemManager extends ManagerBase implements IGetShopLockSystemManager {
    private HashMap<String, LockServer> lockServers = new HashMap();
    private HashMap<String, LockGroup> groups = new HashMap();
    private HashMap<String, AccessGroupUserAccess> users = new HashMap();
    private HashMap<String, SmsMessage> smsMessage = new HashMap();
    private HashMap<String, MailMessage> mailMessage = new HashMap();

    @Autowired
    private MessageManager messageManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon iData : data.data) {
            if (iData instanceof LockGroup) {
                groups.put(iData.id, (LockGroup)iData);
            }
            if (iData instanceof GetShopLockBoxServer) {
                GetShopLockBoxServer server = (GetShopLockBoxServer)iData;                
                server.setManger(this);
                lockServers.put(iData.id, server);
            }
            if (iData instanceof ZwaveLockServer) {
                ZwaveLockServer server = (ZwaveLockServer)iData;                
                server.setManger(this);
                lockServers.put(iData.id, server);
            }
            if (iData instanceof AccessGroupUserAccess) {
                AccessGroupUserAccess access = (AccessGroupUserAccess)iData;                
                users.put(access.id, access);
            }
        }
        
        lockServers.values().stream().forEach(l -> {
            triggerCheckOfCodes(l.getId());
        });
        
        createScheduler("checkCronGetShopLockSystemManager", "*/5 * * * *", ZwaveTriggerCheckCron.class);
    }
    
    private HashMap<String, LockGroup> getFinalizedGroups() {
        groups.values().stream()
                .forEach(g -> {
                    g.finalize(lockServers);
                });
        
        return groups;
    }
    
    @Override
    public void createServer(String type, String hostname, String userName, String password, String givenName) {
        if (type.equals("zwaveserver")) {
            createZwaveServer(hostname, userName, password, givenName);
        }
        
        if (type.equals("getshoplockbox")) {
            createGetShopLockBoxServer(hostname, userName, password, givenName);
        }
    }   

    private void createZwaveServer(String hostname, String userName, String password, String givenName) throws ErrorException {
        ZwaveLockServer server = new ZwaveLockServer();
        server.setDetails(hostname, userName, password, givenName);
        server.setManger(this);
        saveObject(server);
        lockServers.put(server.id, server);
    }
    
    private void createGetShopLockBoxServer(String hostname, String userName, String password, String givenName) throws ErrorException {
        GetShopLockBoxServer server = new GetShopLockBoxServer();
        server.setDetails(hostname, userName, password, givenName);
        server.setManger(this);
        saveObject(server);
        lockServers.put(server.id, server);
    }

    @Override
    public List<LockServer> getLockServers() {
        lockServers.values().stream().forEach(s -> s.finalizeServer());
        return new ArrayList(lockServers.values());
    }

    @Override
    public void deleteServer(String serverId) {
        LockServer server = lockServers.remove(serverId);
        if (server  != null) {
            deleteObject((DataCommon)server);
        }
    }

    @Override
    public void startFetchingOfLocksFromServer(String serverId) {
        LockServer server = lockServers.get(serverId);
        if (server != null) {
            server.fetchLocksFromServer();
        }
    }

    @Override
    public void updateConnectionDetails(String serverId, String hostname, String username, String password, String givenName) {
        LockServer server = lockServers.get(serverId);
        if (server != null) {
            server.setDetails(hostname, username, password, givenName);
        }
    }

    @Override
    public Lock getLock(String serverId, String lockId) {
        LockServer server = lockServers.get(serverId);
        
        if (server != null) {
            Lock lock = server.getLock(lockId);
            lock.finalize();
            
            return lock;
        }
        
        return null;
    }
    
    @Override
    public void generateNewCodesForLock(String serverId, String lockId) {
        LockServer server = lockServers.get(serverId);
        
        if (server != null) {
            Lock lock = server.getLock(lockId);
            if (lock != null) {
                lock.generateNewCodes();
                server.save();
            }
        }
    }

    @Override
    public void prioritizeLockUpdate(String serverId, String lockId) {
        LockServer server = lockServers.get(serverId);
        
        if (server != null) {
            server.prioritizeLock(lockId);
        }
    }

    @Override
    public void markCodeForDeletion(String serverId, String lockId, int slotId) {
        LockServer server = lockServers.get(serverId);
        
        if (server != null) {
            server.markCodeForDeletion(lockId, slotId);
        }
    }

    @Override
    public void markCodeForResending(String serverId, String lockId, int slotId) {
        LockServer server = lockServers.get(serverId);
        
        if (server != null) {
            server.markCodeForResending(lockId, slotId);
        }
    }

    @Override
    public void createNewLockGroup(String name, int maxUsersInGroup) {
        LockGroup group = new LockGroup();
        group.name = name;
        group.numberOfSlotsInGroup = maxUsersInGroup;
        saveObject(group);
        getFinalizedGroups().put(group.id, group);
    }

    @Override
    public void setLocksToGroup(String groupId, Map<String, List<String>> servers) {
        LockGroup group = getFinalizedGroups().get(groupId);
        
        releaseSlotsNotConnectedToGroup(group, servers);
        checkIfEnoughSlotsOnAllLocks(servers, group);
        
        for (String serverId : servers.keySet()) {
            LockServer server = getLockServer(serverId);
            for (String lockId : servers.get(serverId)) {
                Lock lock = server.getLock(lockId);
                addLockToGroup(group, server, lock);
            }
        }
        
        rebuildGroupMatrix(group);
        syncGroup(group);
    }

    @Override
    public LockGroup getGroup(String groupId) {
        return getFinalizedGroups().get(groupId);
    }

    @Override
    public List<LockGroup> getAllGroups() {
        List<LockGroup> retGroups = new ArrayList(getFinalizedGroups().values());
        Collections.sort(retGroups, (LockGroup group1, LockGroup group2) -> {
            return group1.name.compareToIgnoreCase(group2.name);
        });
        return retGroups;
    }

    private LockServer getLockServer(String serverId) {
        return getLockServers()
                .stream()
                .filter(s -> s.getId().equals(serverId))
                .findFirst()
                .orElse(null);
    }
    
    private void checkIfEnoughSlotsOnAllLocks(Map<String, List<String>> servers, LockGroup group) throws ErrorException {
        for (String serverId : servers.keySet()) {
            LockServer server = getLockServer(serverId);
            List<String> lockIds = servers.get(serverId);
            for (String lockId : lockIds) {
                Lock lock = server.getLock(lockId);
                List<UserSlot> slots = lock.getAllUnusedUserSlots(group.id);
                
                if (slots.size() < group.numberOfSlotsInGroup) {
                    throw new ErrorException(1041);
                }
            }
        }
    }

    private void addLockToGroup(LockGroup group, LockServer server, Lock lock) {
        List<UserSlot> slots = lock.getAllUnusedUserSlots(group.id);
        
        List<UserSlot> slotsAlreadyInUse = slots.stream()
                .filter(s -> s.belongsToGroup(group.id))
                .collect(Collectors.toList());
        
        List<UserSlot> freeSlots = slots.stream()
                .filter(s -> !s.isUserSlotTakenByGroup())
                .collect(Collectors.toList());
        
        int j = 0;
        for (int i=slotsAlreadyInUse.size(); i<group.numberOfSlotsInGroup; i++) {
            UserSlot slot = freeSlots.get(j);
            j++;
            
            server.claimLockGroupUseOfSlot(lock.id, slot.slotId, group.id);
        }
    }

    private void rebuildGroupMatrix(LockGroup group) {
        List<LockServer> servers = getLockServers();
        group.rebuildCodeMatrix(servers, getCodeSize());
        saveObject(group);
    }

    @Override
    public String getNameOfGroup(String groupId) {
        return groups.get(groupId).name;
    }

    
    private void releaseSlotsNotConnectedToGroup(LockGroup group, Map<String, List<String>> inServers) {
        List<String> allLockIds = new ArrayList();
        inServers.values().stream().forEach(list -> allLockIds.addAll(list));
        
        getLockServers().stream().forEach(server -> {
            server.getLocks().stream().forEach(lock -> { 
                if (!allLockIds.contains(lock.id)) {
                    server.releaseFromGroup(lock.id, group.id);
                }
            });
        });
    }

    @Override
    public void deleteGroup(String groupId) {
        LockGroup group = getGroup(groupId);
        if (group != null) {
            Map<String, List<String>> servers = new HashMap();
            releaseSlotsNotConnectedToGroup(group, servers);
            getFinalizedGroups().remove(group.id);
            deleteObject(group);
        }
    }

    private void syncGroup(LockGroup group) {
        getLockServers().stream().forEach(server -> {
            server.syncGroup(group);
        });
    }
    
    @Override
    public void lockSettingsChanged(LockSettings lockSettings) {
        lockServers.values().stream().forEach(server -> {
            server.lockSettingsChanged(lockSettings);
        });
    }

    @Override
    public void deactivatePrioritingOfLock(String serverId) {
        LockServer server = getLockServer(serverId);
        if (server != null) {
            server.deactivatePrioritingOfLock();
        }
    }

    @Override
    public LockCode getNextUnusedCode(String groupId, String reference, String managerName, String textReference) {
        LockGroup group = getFinalizedGroups().get(groupId);
        if (group == null) {
            throw new ErrorException(1042);
        }
        
        MasterUserSlot slot = group.getGroupLockCodes().values()
                .stream()
                .filter(s -> s.takenInUseDate == null)
                .filter(s -> s.allCodesAdded)
                .findAny()
                .orElse(null);
        
        if (slot == null)
            return null;
        
        slot.takenInUseDate = new Date();
        slot.takenInUseReference = reference;
        slot.takenInUseManagerName = managerName;
        slot.takenInUseTextReference = textReference;
        saveObject(group);
        
        return slot.code;
    }
    //358047
    
    @Override
    public void renewCodeForSlot(String groupId, int slotId) {
        LockGroup group = getGroup(groupId);
        group.renewCodeForSlot(slotId);
        
        lockServers.values().stream().forEach(server -> {
            server.syncGroupSlot(group, slotId);
        });
        
        saveObject(group);
        
    }
    
    @Override
    public void changeCode(String groupId, int slotId, int pinCode, String cardId) {
        LockGroup group = getGroup(groupId);
        group.changeCode(slotId, pinCode, cardId);
        saveObject(group);
        
        lockServers.values().stream().forEach(server -> {
            server.syncGroupSlot(group, slotId);
        });
    }

    @Override
    public void changeDatesForSlot(String groupId, int slotId, Date validFrom, Date validTo) {
        LockGroup group = getGroup(groupId);
        
        if (group != null) {
            group.changeDatesForSlot(slotId, validFrom, validTo);
            
            lockServers.values().stream().forEach(server -> {
                server.syncGroupSlot(group, slotId);
            });
            
            saveObject(group);
        }
    }

    @Override
    public void triggerCheckOfCodes(String serverId) {
        LockServer server = getLockServer(serverId);
        if (server instanceof ZwaveLockServer) {
            ((ZwaveLockServer)server).startUpdatingOfLocks();
        }
    }
    
    @Override
    public void toggleActiveServer(String serverId) {
        LockServer server = getLockServer(serverId);
        if (server instanceof ZwaveLockServer) {
            ((ZwaveLockServer)server).toggleActivated();
        }
    }

    @Override
    public void triggerCronTab() {
        lockServers.values().stream()
                .forEach(s -> triggerCheckOfCodes(s.getId()));
    }

    @Override
    public AccessGroupUserAccess grantAccessDirect(String groupId, AccessGroupUserAccess user) {
        LockCode code = getNextUnusedCode(groupId, "", GetShopLockSystemManager.class.getSimpleName(), "");
        if (code != null) {
            user.lockCode = code;
            user.lockGroupId = groupId;
            saveObject(user);
            users.put(user.id, user);
            
            return user;
        }
        
        return null;
    }

    @Override
    public void removeAccess(String id) {
        AccessGroupUserAccess access = users.remove(id);
        if (access != null) {
            renewCodeForSlot(access.lockGroupId, access.lockCode.slotId);
            deleteObject(access);
        }
    }

    @Override
    public FilteredData getAllAccessUsers(FilterOptions options) {
        ArrayList<AccessGroupUserAccess> data = new ArrayList(users.values());
        return pageIt(data, options);
    }

    @Override
    public AccessGroupUserAccess getAccess(String userId) {
        return users.get(userId);
    }

    @Override
    public void sendSmsToCustomer(String userId, String textMessage) {
        AccessGroupUserAccess user = users.get(userId);
        if (user != null) {
            String smsMessageId = messageManager.sendSms("nexmo", ""+user.phonenumber, textMessage, ""+user.prefix);
            user.smsMessages.add(smsMessageId);
            saveObject(user);
        }
    }

    @Override
    public void sendEmailToCustomer(String userId, String subject, String body) {
        AccessGroupUserAccess user = users.get(userId);
        if (user != null) {
            String smsMessageId = messageManager.sendMail(user.email, user.fullName, subject, body, "", "");
            user.emailMessages.add(smsMessageId);
            saveObject(user);
        }
    }

    @Override
    public void saveUser(AccessGroupUserAccess user) {
        saveObject(user);
        users.put(user.id, user);
    }

    @Override
    public void markCodeAsUpdatedOnLock(String serverId, String lockId, int slotId) {
         LockServer server = lockServers.get(serverId);
        
        if (server != null) {
            server.markCodeAsUpdatedOnLock(lockId, slotId);
        }
    }

    int getCodeSize() {
        // Do this better, dont make it hardcoded bu make codesize configurable.
        if (storeId.equals("7f2c47a4-7ec9-41e2-a070-1e9e8fcf4e38")) {
            return 4;
        }
        
        return 6;
    }

    public boolean isActivated() {
        if(groups.isEmpty()) {
            return false;
        }
        return true;
    }
}