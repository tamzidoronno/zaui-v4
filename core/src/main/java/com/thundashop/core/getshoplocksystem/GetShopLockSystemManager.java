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
import com.thundashop.core.getshop.GetShop;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.storemanager.StoreManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.thundashop.constant.GetShopSchedulerBaseType.CHECK_CRON_GETSHOP_LOCK_SYSTEM_MANAGER;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class GetShopLockSystemManager extends ManagerBase implements IGetShopLockSystemManager {
    private HashMap<String, LockServer> lockServers = new HashMap<>();
    private HashMap<String, LockGroup> groups = new HashMap<>();
    private HashMap<String, AccessGroupUserAccess> users = new HashMap<>();
    private HashMap<String, List<UserSlot>> cacheSlotsInUse = new HashMap<>();
    private GetShopLockSystemSettings settings = new GetShopLockSystemSettings();
    
    @Autowired
    private MessageManager messageManager;

    private int lastsavedcounter = 0;
    
    @Autowired
    GetShop getShop;
    
    @Autowired
    StoreManager storeManager;

    @Autowired
    @Qualifier("pingServerExecutor")
    private TaskExecutor pingServerExecutor;
    
    
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
                if (server.loaded()) {
                    saveObject(server);
                }
            }
            if (iData instanceof ZwaveLockServer) {
                ZwaveLockServer server = (ZwaveLockServer)iData;                
                server.setManger(this);
                lockServers.put(iData.id, server);
            }
            if (iData instanceof RcoLockSystem) {
                RcoLockSystem server = (RcoLockSystem)iData;                
                server.setManger(this);
                lockServers.put(iData.id, server);
            }
            if (iData instanceof GetShopLoraServer) {
                GetShopLoraServer server = (GetShopLoraServer)iData;                
                server.setManger(this);
                lockServers.put(iData.id, server);
            }
            if (iData instanceof SerosLockSystem) {
                SerosLockSystem server = (SerosLockSystem)iData;                
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
        
        createScheduler(CHECK_CRON_GETSHOP_LOCK_SYSTEM_MANAGER);
        
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
        
        if (type.equals("rcosystem")) {
            createRcoServer(hostname, userName, password, givenName, token);
        }
        
        if (type.equals("getshoploragateway")) {
            createGetShopLoraGateway(hostname, userName, password, givenName, token);
        }
        
        if (type.equals("seroslocksystem")) {
            createSerosLockServer(hostname, userName, password, givenName, token);
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
    
    private void createRcoServer(String hostname, String userName, String password, String givenName, String token) throws ErrorException {
        RcoLockSystem server = new RcoLockSystem();
        server.setDetails(hostname, userName, password, givenName, token);
        server.setManger(this);
        saveObject(server);
        lockServers.put(server.id, server);
    }

    @Override
    public List<LockServer> getLockServers() {
        lockServers.values().stream().forEach(s -> s.finalizeServer());
        return new ArrayList<>(lockServers.values());
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
            storeManager.invalidateServerBackup(serverId);
        }
    }

    @Override
    public Lock getLock(String serverId, String lockId) {
        LockServer server = lockServers.get(serverId);
        
        if (server != null) {
            if (!hasCachedBuild(serverId, lockId)) {
                getCodesInUse(serverId, lockId);
            }
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
                lock.generateNewCodes(getCodeSizeInternal(null), getAllCodesInUse());
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
    public void forceDeleteSlot(String serverId, String lockId, int slotId) {
        ZwaveLockServer server = (ZwaveLockServer) lockServers.get(serverId);
        server.forceDelete(lockId, slotId);
    }    

    @Override
    public LockGroup createNewLockGroup(String name, int maxUsersInGroup, Integer codeSize) {
        LockGroup group = new LockGroup();
        group.name = name;
        group.numberOfSlotsInGroup = maxUsersInGroup;
        group.codeSize = codeSize;
        saveObject(group);
        getFinalizedGroups().put(group.id, group);
        return group;
    }

    @Override
    public void setLocksToGroup(String groupId, Map<String, List<String>> servers) {
        servers.keySet().removeIf(o -> o == null || o.isEmpty());
        
        LockGroup group = getFinalizedGroups().get(groupId);
        
        releaseSlotsNotConnectedToGroup(group, servers);
        checkIfEnoughSlotsOnAllLocks(servers, group);
        
        for (String serverId : servers.keySet()) {
            LockServer server = getLockServer(serverId);
            
            if (!server.useSlotConcept()) {
                server.setLockstoGroup(groupId, servers.get(serverId));
                continue;
            }
            
            for (String lockId : servers.get(serverId)) {
                Lock lock = server.getLock(lockId);
                addLockToGroup(group, server, lock);
            }
        }
        
        rebuildGroupMatrix(group);
        syncGroup(group, false);
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
    public List<LockGroup> getAllGroupsUnfinalized() {
        List<LockGroup> retGroups = new ArrayList<>(this.groups.values());
        Collections.sort(retGroups, (LockGroup group1, LockGroup group2) -> {
            return group1.name.compareToIgnoreCase(group2.name);
        });
        return retGroups;
        
    }

    @Override
    public List<LockGroup> getAllGroups() {
        List<LockGroup> retGroups = new ArrayList<>(getFinalizedGroups().values());
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
            
            if (server != null && !server.useSlotConcept()) {
                continue;
            }
            
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
        group.rebuildCodeMatrix(servers, getCodeSizeInternal(group.id), getAllCodesInUse());
        saveObject(group);
    }

    @Override
    public String getNameOfGroup(String groupId) {
        return groups.get(groupId).name;
    }

    
    private void releaseSlotsNotConnectedToGroup(LockGroup group, Map<String, List<String>> inServers) {
        List<String> allLockIds = new ArrayList<>();
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
            Map<String, List<String>> servers = new HashMap<>();
            releaseSlotsNotConnectedToGroup(group, servers);
            getFinalizedGroups().remove(group.id);
            deleteObject(group);
        }
    }

    private void syncGroup(LockGroup group, boolean light) {
        getLockServers().stream().forEach(server -> {
            
            if (light && server.useSlotConcept()) {
                return;
            }
            
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
        
        MasterUserSlot slot = getFirstUnusedCodeFromSlot(group);
        
        if (slot == null)
            return null;
        
        slot.takenInUseDate = new Date();
        slot.takenInUseReference = reference;
        slot.takenInUseManagerName = managerName;
        slot.takenInUseTextReference = textReference;
        saveObject(group);
        clearCache(null);
        
        triggerCheckOfCodesForAllServers();
        
        return slot.code;
    }

    private MasterUserSlot getFirstUnusedCodeFromSlot(LockGroup group) {
        MasterUserSlot slot = group.getGroupLockCodes().values()
                .stream()
                .filter(s -> s.takenInUseDate == null)
                .filter(s -> s.allCodesAdded)
                .findAny()
                .orElse(null);
        return slot;
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
    public void saveObject(DataCommon data) throws ErrorException {
        if (data instanceof LockGroup) {
            syncGroup((LockGroup)data, true);
        }
        super.saveObject(data);
    }
    
    @Override
    public void renewCodeForSlot(String groupId, int slotId) {
        LockGroup group = getGroup(groupId);
        if(group == null) {
            return;
        }
        
        MasterUserSlot slot = getFirstUnusedCodeFromSlot(group);
        if (slot == null) {
            MasterUserSlot slotInUse = group.getGroupLockCodes().get(slotId);
            if (slotInUse.takenInUseDate != null) {
                slotInUse.takenInUseDate = null;
                slotInUse.takenInUseReference = "";
                slotInUse.takenInUseManagerName = "";
                slotInUse.takenInUseTextReference = "";
                saveObject(group);
                
            }
        } else {
            group.renewCodeForSlot(slotId, getCodeSizeInternal(groupId), getAllCodesInUse());

            lockServers.values().stream().forEach(server -> {
                server.syncGroupSlot(group, slotId);
            });

            saveObject(group);
        }
        
        clearCache(null);
        
        triggerCheckOfCodesForAllServers();
    }
    
    @Override
    public void changeCode(String groupId, int slotId, int pinCode, String cardId) {
        LockGroup group = getGroup(groupId);
        group.changeCode(slotId, pinCode, cardId, getCodeSizeInternal(groupId), getAllCodesInUse());
        saveObject(group);
        
        lockServers.values().stream().forEach(server -> {
            server.syncGroupSlot(group, slotId);
        });
    }

    @Override
    public void changeDatesForSlot(String groupId, int slotId, Date validFrom, Date validTo) {
        LockGroup group = getGroup(groupId);
        
        if (group != null) {
            group.changeDatesForSlot(slotId, validFrom, validTo, getCodeSizeInternal(groupId));
            
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
        
        if (server instanceof GetShopLoraServer) {
            checkLoraServer((GetShopLoraServer)server);
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
        triggerCheckOfCodesForAllServers();
    }

    private void triggerCheckOfCodesForAllServers() {
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
            if (access.lockCode != null) {
                renewCodeForSlot(access.lockGroupId, access.lockCode.slotId);
            }
            
            deleteObject(access);
        }
    }

    @Override
    public FilteredData getAllAccessUsers(FilterOptions options) {
        ArrayList<AccessGroupUserAccess> data = new ArrayList<>(users.values());
        return pageIt(data, options);
    }
    
    @Override
    public List<AccessGroupUserAccess> getAllAccessUsersFlat() {
        ArrayList<AccessGroupUserAccess> data = new ArrayList<>(users.values());
        return data;
    }

    @Override
    public AccessGroupUserAccess getAccess(String userId) {
        return users.get(userId);
    }

    @Override
    public void sendSmsToCustomer(String userId, String textMessage) {
        AccessGroupUserAccess user = users.get(userId);
        if (user != null) {
            
            String smsMessageId = null;
            
            if (user.prefix == 47) {
                smsMessageId = messageManager.sendSms("sveve", ""+user.phonenumber, textMessage, ""+user.prefix);
            } else {
                smsMessageId = messageManager.sendSms("nexmo", ""+user.phonenumber, textMessage, ""+user.prefix);
            }
            
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
        return getCodeSizeInternal(null);
    }
    
    public int getCodeSizeInternal(String groupId) {
        if (groupId != null && groups.get(groupId) != null && groups.get(groupId).codeSize != null ) {
            return groups.get(groupId).codeSize;
        }
        
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
    public void addTransactionHistorySeros(String tokenId, String lockId, Date timeStamp, String keyId, int userSlot) {
        lockServers.values().stream()
            .forEach(server -> {
                if(server instanceof SerosLockSystem) {
                    SerosLockSystem casted = (SerosLockSystem)server;
                    casted.addTransactionHistorySeros(tokenId, lockId, keyId, timeStamp, userSlot);
                } else {
                    server.addTransactionHistory(tokenId, lockId, timeStamp, userSlot);
                }
            });
    }

    @Override
    public List<AccessHistoryResult> getAccessHistory(String groupId, Date start, Date end, int groupSlotId) {
        LockGroup group = getGroup(groupId);
        
        List<AccessHistoryResult> history = new ArrayList<>();
        Map<String, List<Integer>> groupedLocksAndSlots = new HashMap<>();
        
        List<String> locks = new ArrayList<>();
        for(List<String> connected : group.connectedToLocks.values()) {
            if(connected != null) {
                locks.addAll(connected);
            }
        }
        
        if (group != null) {
            MasterUserSlot masterUserSlot = group.getGroupLockCodes().get(groupSlotId);
            for (UserSlot subSlot : masterUserSlot.subSlots) {
                List<Integer> slots = groupedLocksAndSlots.get(subSlot.connectedToLockId);
                if (slots == null) {
                    slots = new ArrayList<>();
                    groupedLocksAndSlots.put(subSlot.connectedToLockId, slots);
                }

                slots.add(subSlot.slotId);
            }
        }
        
        for (String lockId : locks) {
            BasicDBObject query = new BasicDBObject();
            DBObject dateQuery = BasicDBObjectBuilder.start("$gte", start).add("$lte", end).get();
                
            query.put("className", AccessHistory.class.getCanonicalName());
            query.put("accessTime", dateQuery);
            query.put("lockId", lockId);
            
            List<Integer> userSlots = groupedLocksAndSlots.get(lockId);
            if(userSlots == null) {
                userSlots = new ArrayList<>();
                userSlots.add(groupSlotId);
            }
            
            query.put("userSlot", new BasicDBObject("$in", userSlots));
//
            List<AccessHistoryResult> hist = database.query(getClass().getSimpleName(), getStoreId(), query).stream()
                .map(o -> (AccessHistory)o)
                .map(o -> o.toResult(getDoorName(o.serverId, o.lockId), ""))
                .collect(Collectors.toList());
            
            history.addAll(hist);
        }
        
        Collections.sort(history, new Comparator<AccessHistoryResult>() {
            public int compare(AccessHistoryResult o1, AccessHistoryResult o2) {
                return o2.time.compareTo(o1.time);
            }
          });
        
        return history;
    }

    private String getDoorName(String serverId, String lockId) {
        LockServer server = getLockServer(serverId);
        if (server == null)
            return "N/A";
        
        Lock lock = server.getLock(lockId);
        
        if (lock == null)
            return "N/A";
        
        return server.getGivenName()+ " " + lock.name;
    }

    /**
     * TODO: Refactor this, there should not be save specific lock types.
     */ 
    @Override
    public void saveLocstarLock(String serverId, LocstarLock lock) {
        LockServer server = lockServers.get(serverId);
       
        if (server != null && server instanceof ZwaveLockServer) {
            ((ZwaveLockServer)server).saveLocstarLock(lock);
            server.save();
        }
    }

    @Override
    public void updateZwaveRoute(String serverId, String lockId) {
        LockServer server = lockServers.get(serverId);
       
        if (server != null && server instanceof ZwaveLockServer) {
            ((ZwaveLockServer)server).updateRouting(lockId);
            server.save();
        }
        
        
    }

    @Override
    public List<UserSlot> getCodesInUse(String serverId, String lockId) {
        return fetchCodesInternal(serverId, lockId, false);
    }

    private List<UserSlot> fetchCodesInternal(String serverId, String lockId, boolean fetchAll) {
        if (hasCachedBuild(serverId, lockId)) {
            return cacheSlotsInUse.get(serverId+"_"+lockId);
        }
        
        List<UserSlot> slotsToReturn = new ArrayList<>();
        
        for (LockGroup group : groups.values()) {
            group.finalize(lockServers);
            
            if (!group.isConnectedToLock(serverId, lockId)) {
                continue;
            }
            
            List<MasterUserSlot> add = new ArrayList<>();
            if(fetchAll) {
                add = group.getGroupLockCodes().values().stream().collect(Collectors.toList());
            } else {
                add = group.getGroupLockCodes().values()
                        .stream()
                        .filter(masterSlot -> masterSlot.takenInUseDate != null || group.isVirtual)
                        .collect(Collectors.toList());
            }
            
            slotsToReturn.addAll(add);
        };
        
        Lock lock = lockServers.get(serverId).getLock(lockId);
        lock.getSlotsUnfinalized().stream().forEach(o -> o.inUse = false);
        
        for (UserSlot islot : slotsToReturn) {
            MasterUserSlot mslot = (MasterUserSlot)islot;
            for (UserSlot slot : mslot.subSlots) {
                if (slot.connectedToLockId != null && slot.connectedToLockId.equals(lockId)) {
                    slot.inUse = true;
                    lock.markSlotInUse(slot.slotId);
                }
            }
        }
        
        cacheSlotsInUse.put(serverId+"_"+lockId, slotsToReturn);
        
        return slotsToReturn;
    }

    @Override
    public void setCodeSize(int codeSize) {
        settings.setCodeSize(codeSize);
        
        groups.values().stream().forEach(group -> {
            group.getGroupLockCodes().values()
            .forEach(lockGroup -> {
                int groupCodeSize = codeSize;

                if (group.codeSize != null) {
                    groupCodeSize = group.codeSize;
                }

                int check = groupCodeSize;

                if (lockGroup.code != null && (lockGroup.code.getCodeLength() != codeSize || lockGroup.code.getCodeLength() != check)) {
                    logPrint("A invalid code");
                    renewCodeForSlot(group.id, lockGroup.slotId);
                }
            });
        });
        
        saveObject(settings);
    }

    @Override
    public void openLock(String lockId) {
        lockServers.values()
                .stream()
                .forEach(server -> {
                    server.openLock(lockId);
                });
    }

    public void pulseOpenLock(String lockId) {
        lockServers.values()
                .stream()
                .forEach(server -> {
                    server.pulseOpenLock(lockId);
                });
    }    
    
    @Override
    public void closeLock(String lockId) {
        lockServers.values()
            .stream()
            .forEach(server -> {
                server.closeLock(lockId);
            });
    }

    @Override
    public void renameLock(String serverId, String lockId, String name) {
        getLockServer(serverId).renameLock(lockId, name);
    }

    public List<AccessEvent> getAccessEvents() {
        List<AccessEvent> retList = new ArrayList<>();
        lockServers.values()
                .stream()
                .forEach(server -> {
                    retList.addAll(server.getAccessEvents());
                });
        
        return retList;
    }

    @Override
    public void addTransactionEntranceDoor(String serverId, String lockId, int code) {
        LockServer lockServer = getLockServer(serverId);
        
        if (lockServer != null) {
            lockServer.addAccessHistoryEntranceDoor(lockId, code, new Date());
        }
    }

    @Override
    public boolean canShowAccessLog() {
        return lockServers.values()
                .stream()
                .filter(o -> o.hasAccessLogFeature())
                .count() > 0;
    }

    @Override
    public void pingServers() {
        lastsavedcounter++;
        if(lastsavedcounter >= 20) {
            for(LockServer server : lockServers.values()) {
                if (this.storeId.equals("1fcbd648-f0ee-4269-ab35-65eb7d9f67aa")){ //Gkroen
                    try{
                        server.save();
                    }catch (Exception e){
                        logPrint("Failed saving lock server " + server.getClass() + ", id: " +server.getId());
                        logPrint( server.toString());
                        logPrintException(e);
                    }
                }else{
                    server.save();
                }
            }
            lastsavedcounter = 0;
        }
        for(LockServer server : lockServers.values()) {
            getShop.updateServerStatus(server, storeId);
            PingThread task = new PingThread(server, storeId);
            pingServerExecutor.execute(task);
        }
    }

    @Override
    public FilteredData getAccessLog(String serverId, String lockId, FilterOptions filterOptions) {
        BasicDBObject query = new BasicDBObject();
        query.put("className", AccessHistory.class.getCanonicalName());
        query.put("lockId", lockId);
        query.put("serverId", serverId);

        List<LockGroup> connectedToGroups = getAllGroups().stream()
                .filter(o -> o.isConnectedToLock(serverId, lockId))
                .collect(Collectors.toList());

        String doorName = getDoorName(serverId, lockId);        

        List<AccessHistoryResult> hist = database.query(getClass().getSimpleName(), getStoreId(), query).stream()
            .map(o -> (AccessHistory)o)
            .map(o -> o.toResult(doorName, getName(o, connectedToGroups)))
            .collect(Collectors.toList());

        return pageIt(hist, filterOptions);
    }

    private String getName(AccessHistory history, List<LockGroup> connectedToGroups) {        
        for (LockGroup connectedToLockGroup : connectedToGroups) {
            for (Integer slotId : connectedToLockGroup.getGroupLockCodes().keySet()) {
                MasterUserSlot masterUserSlot = connectedToLockGroup.getGroupLockCodes().get(slotId);
                for (UserSlot subSlot : masterUserSlot.subSlots) {
                    if (subSlot.slotId == history.userSlot && subSlot.connectedToLockId.equals(history.lockId)) {
                        logPrint("Got match on " + masterUserSlot.slotId + " | " + connectedToLockGroup.name);
                        return getNameForLastUserWithAccessTo(connectedToLockGroup.id, slotId, history.accessTime);
                    }
                }
            }
        }
                
        return "";
    }


    private String getNameForLastUserWithAccessTo(String groupId, Integer slotId, Date accessTime) {
        List<AccessGroupUserAccess> accessUsers = users.values().stream()
                .filter(o -> o.lockGroupId.equals(groupId) && o.lockCode != null && o.lockCode.slotId == slotId)
                .filter(o -> o.rowCreatedDate.before(accessTime))
                .collect(Collectors.toList());
        
        if (accessUsers.isEmpty()) {
            return "N/A";
        }
        
        accessUsers.sort((AccessGroupUserAccess o1, AccessGroupUserAccess o2) -> {
            return o2.rowCreatedDate.compareTo(o1.rowCreatedDate);
        });
        
        return accessUsers.get(0).fullName;
    }

    @Override
    public Map<Integer, List<UserSlot>> getCodesByToken(String tokenId) {
        if (tokenId == null || tokenId.isEmpty())
            return new HashMap<>();
        
        LockServer server = getServerByToken(tokenId);
        
        if (server == null) {
            return new HashMap<>();
        }
        
        
        HashMap<Integer, List<UserSlot>> retMap = new HashMap<>();
        
        for (Lock lock : server.getLocks()) {
            boolean fetchAll = tokenId.equals("349792c3-0d08-4d96-8c3e-841ffb2aaf54") || tokenId.equals("8f15a818-80e9-49f9-966b-479e9b642739");
            retMap.put(lock.lockIncrementalId, fetchCodesInternal(server.getId(), lock.id, fetchAll));
        }
        
        return retMap;
    }

    private LockServer getServerByToken(String tokenId) {
        LockServer server = lockServers.values().stream()
                .filter(o -> o.getAccessToken() != null && o.getAccessToken().equals(tokenId))
                .findFirst()
                .orElse(null);
        return server;
    }

    void clearCache(LockServerBase server) {
        cacheSlotsInUse.clear();
    }

    @Override
    public void addTransactionEntranceDoorByToken(String tokenId, int lockIncrementalId, int code) {
        LockServer server = getServerByToken(tokenId);
        
        if (server != null && server instanceof GetShopLoraServer) {
            ((GetShopLoraServer)server).doorAccess(lockIncrementalId, code, new Date());
            return;
        }
        
        if (server != null) {
            Lock lock = server.getLocks().stream()
                    .filter(o -> o.lockIncrementalId != null && o.lockIncrementalId.equals(lockIncrementalId))
                    .findFirst()
                    .orElse(null);
            
            if (lock != null) {
                addTransactionEntranceDoor(server.getId(), lock.id, code);
            }
        }
    }

    @Override
    public void deleteLock(String serverId, String lockId) {
        LockServer server = getLockServer(serverId);
        if (server != null) {
            server.deleteLock(lockId);
            server.save();
        }
    }

    public List<LockServer> getLockServersUnfinalized() {
        return new ArrayList<>(lockServers.values());
    }

    private List<Integer> getAllCodesInUse() {
        List<Integer> codes = new ArrayList<>();
        List<LockGroup> tmpgroups = getAllGroupsUnfinalized();
        for(LockGroup grp : tmpgroups) {
            for(MasterUserSlot slot : grp.getGroupLockCodes().values()) {
                codes.add(slot.getPincode());
            }
        }
        return codes;
    }

    private void createGetShopLoraGateway(String hostname, String userName, String password, String givenName, String token) {
        GetShopLoraServer server = new GetShopLoraServer();
        server.setDetails(hostname, userName, password, givenName, token);
        server.setManger(this);
        saveObject(server);
        lockServers.put(server.id, server);
    }

    private boolean hasCachedBuild(String serverId, String lockId) {
        return (cacheSlotsInUse.get(serverId+"_"+lockId) != null);
    }

    private void checkLoraServer(GetShopLoraServer getShopLoraServer) {
        List<String> lockIds = getShopLoraServer.getLocks()
                .stream()
                .map(o -> o.id)
                .collect(Collectors.toList());
        
        for (String lockId : lockIds) {
            Lock lock = getLock(getShopLoraServer.id, lockId);
            getShopLoraServer.transferToLoraGateWay((GetShopLock)lock);
        }
    }   

    @Override
    public void resyncDatabaseWithLoraGateway(String serverId) {
        LockServer server = getLockServer(serverId);
        if (server != null) {
            if (server instanceof GetShopLoraServer) {
                ((GetShopLoraServer)server).resyncDatabaseWithLoraGateway();
            }
        }
    }
    
    @Override
    public void deleteSlot(String serverId, String lockId, int slotId) {
        LockServer server = getLockServer(serverId);
        if (server != null) {
            Lock lock = server.getLock(lockId);
            lock.deleteSlot(slotId);
        }
    }

    private void createSerosLockServer(String hostname, String userName, String password, String givenName, String token) {
        SerosLockSystem server = new SerosLockSystem();
        server.setDetails(hostname, userName, password, givenName, token);
        server.setManger(this);
        saveObject(server);
        lockServers.put(server.id, server);
    }

    @Override
    public void changeNameOnGorup(String groupdId, String name) {
        LockGroup group = getGroup(groupdId);
        if (group != null) {
            group.name = name;
            saveObject(group);
        }
    }

}