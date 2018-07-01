/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import com.getshop.scope.GetShopSession;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FilterOptions;
import com.thundashop.core.common.FilteredData;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MailMessage;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.messagemanager.SmsMessage;
import com.thundashop.core.webmanager.WebManager;
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
    private GetShopLockSystemSettings settings = new GetShopLockSystemSettings();
    
    @Autowired
    private MessageManager messageManager;
    
    @Autowired
    private WebManager webManager;
    
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
            if (iData instanceof GetShopLockSystemSettings) {
                settings = (GetShopLockSystemSettings)iData;
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
    public void createServer(String type, String hostname, String userName, String password, String givenName, String token) {
        if (type.equals("zwaveserver")) {
            createZwaveServer(hostname, userName, password, givenName, token);
        }
        
        if (type.equals("getshoplockbox")) {
            createGetShopLockBoxServer(hostname, userName, password, givenName, token);
        }
    }   

    private void createZwaveServer(String hostname, String userName, String password, String givenName, String token) throws ErrorException {
        ZwaveLockServer server = new ZwaveLockServer();
        server.setDetails(hostname, userName, password, givenName, token);
        server.setManger(this);
        saveObject(server);
        lockServers.put(server.id, server);
    }
    
    private void createGetShopLockBoxServer(String hostname, String userName, String password, String givenName, String token) throws ErrorException {
        GetShopLockBoxServer server = new GetShopLockBoxServer();
        server.setDetails(hostname, userName, password, givenName, token);
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
    public void updateConnectionDetails(String serverId, String hostname, String username, String password, String givenName, String token) {
        LockServer server = lockServers.get(serverId);
        if (server != null) {
            server.setDetails(hostname, username, password, givenName, token);
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
    public LockGroup createNewLockGroup(String name, int maxUsersInGroup) {
        LockGroup group = new LockGroup();
        group.name = name;
        group.numberOfSlotsInGroup = maxUsersInGroup;
        saveObject(group);
        getFinalizedGroups().put(group.id, group);
        return group;
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
        LockGroup group = groups.get(groupId);
        if (group == null)
            return null;
        
        group.finalize(lockServers);
        
        return group;
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
    
    public LockCode getCodeAndMarkAsInUse(String groupId, String reference, String managerName, String textReference, String pinCode) {
        LockGroup group = getFinalizedGroups().get(groupId);
        if (group == null) {
            throw new ErrorException(1042);
        }
        
        int ipinCode = Integer.parseInt(pinCode);
        MasterUserSlot slot = group.getGroupLockCodes().values()
                .stream()
                .filter(s -> s.takenInUseDate == null)
                .filter(s -> s.allCodesAdded)
                .filter(s -> s.code != null)
                .filter(s -> s.code.pinCode == ipinCode)
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

    @Override
    public int getCodeSize() {
        return settings.getCodeSize();
    }

    @Override
    public boolean isSlotTakenInUseInAnyGroups(String serverId, String lockId, int slotId) {
        
        for (LockGroup u : groups.values()) {
            HashMap<Integer, MasterUserSlot> lockCodes = u.getGroupLockCodes();
            for (Integer key : lockCodes.keySet()) {
                MasterUserSlot master = lockCodes.get(key);
                if (master.takenInUseDate == null && !u.isVirtual) {
                    continue;
                }
                
                for (UserSlot slot : master.subSlots) {
                    if (slot.connectedToLockId.equals(lockId) && slot.connectedToServerId.equals(serverId) && slot.slotId == slotId) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    @Override
    public void setGroupVirtual(String groupId, boolean isVirtual) {
        LockGroup group = getGroup(groupId);
        group.isVirtual = isVirtual;
        saveObject(group);
    }
    
    public boolean isActivated() {
        if(groups.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public String restCall(String serverId, String path) {
        LockServer server = lockServers.get(serverId);
        if(server instanceof ZwaveLockServer) {
            ZwaveLockServer zwaveserver = (ZwaveLockServer) server;
            String res = zwaveserver.httpLoginRequestZwaveServer(path);
            return res;
        }
        return "";
    }

    @Override
    public void addTransactionHistory(String tokenId, String lockId, Date timeStamp, int userSlot) {
        lockServers.values().stream()
                .forEach(server -> {
                    server.addTransactionHistory(tokenId, lockId, timeStamp, userSlot);
                });
        
        
    }

    @Override
    public List<AccessHistoryResult> getAccessHistory(String groupId, Date start, Date end, int groupSlotId) {
        LockGroup group = getGroup(groupId);
        
        List<AccessHistoryResult> history = new ArrayList();
        Map<String, List<Integer>> groupedLocksAndSlots = new HashMap();
        
        if (group != null) {
            MasterUserSlot masterUserSlot = group.getGroupLockCodes().get(groupSlotId);
            for (UserSlot subSlot : masterUserSlot.subSlots) {
                List<Integer> slots = groupedLocksAndSlots.get(subSlot.connectedToLockId);
                if (slots == null) {
                    slots = new ArrayList();
                    groupedLocksAndSlots.put(subSlot.connectedToLockId, slots);
                }

                slots.add(subSlot.slotId);
            }
        }
        
        for (String lockId : groupedLocksAndSlots.keySet()) {
            BasicDBObject query = new BasicDBObject();
            DBObject dateQuery = BasicDBObjectBuilder.start("$gte", start).add("$lte", end).get();
                
            query.put("className", AccessHistory.class.getCanonicalName());
            query.put("accessTime", dateQuery);
            query.put("lockId", lockId);
            query.put("userSlot", new BasicDBObject("$in", groupedLocksAndSlots.get(lockId)));
//        
            List<AccessHistoryResult> hist = database.query(getClass().getSimpleName(), getStoreId(), query).stream()
                .map(o -> (AccessHistory)o)
                .map(o -> o.toResult(getDoorName(o.serverId, o.lockId)))
                .collect(Collectors.toList());
            
            history.addAll(hist);
        }
        
        return history;
    }

    private String getDoorName(String serverId, String lockId) {
        LockServer server = getLockServer(serverId);
        if (server == null)
            return "N/A";
        
        Lock lock = server.getLock(lockId);
        
        if (lock == null)
            return "N/A";
        
        return lock.name;
    }

    @Override
    public void saveLocstarLock(String serverId, LocstarLock lock) {
        LockServer server = lockServers.get(serverId);
       
        if (server != null) {
            server.saveLocstarLock(lock);
            server.save();
        }
    }

    @Override
    public void updateZwaveRoute(String serverId, String lockId) {
        Lock lock = getLock(serverId, lockId);
        String ids = "";
        if(lock.routing.size() > 0) { ids += lock.routing.get(0) + ","; } else { ids += "0,"; }
        if(lock.routing.size() > 1) { ids += lock.routing.get(1) + ","; } else { ids += "0,"; }
        if(lock.routing.size() > 2) { ids += lock.routing.get(2) + ","; } else { ids += "0,"; }
        if(lock.routing.size() > 3) { ids += lock.routing.get(3); } else { ids += "0"; }
        
        LocstarLock lstrlock = (LocstarLock) lock;
        
        String path = "JS/Run/zway.SetPriorityRoute("+lstrlock.zwaveDeviceId+","+ids+",3)";
        restCall(serverId, path);
    }

    @Override
    public List<UserSlot> getCodesInUse(String serverId, String lockId) {
        List<UserSlot> slotsToReturn = new ArrayList();
        
        for (LockGroup group : groups.values()) {
            group.finalize(lockServers);
            
            if (!group.isConnectedToLock(serverId, lockId)) {
                continue;
            }
            
            List<MasterUserSlot> add = group.getGroupLockCodes().values()
                    .stream()
                    .filter(masterSlot -> masterSlot.takenInUseDate != null || group.isVirtual)
                    .collect(Collectors.toList());
            
            slotsToReturn.addAll(add);
        };
        
        return slotsToReturn;
    }

    @Override
    public void setCodeSize(int codeSize) {
        settings.setCodeSize(codeSize);
        saveObject(settings);
    }
}