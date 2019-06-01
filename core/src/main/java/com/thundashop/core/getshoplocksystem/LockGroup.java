/*

 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class LockGroup extends DataCommon {
    /**
     * Key = slotid
     */
    private HashMap<Integer, MasterUserSlot> groupLockCodes = new HashMap();
    
    /**
     * Key = lock server id.
     * Value (List<String>) = List of all locks on that server.
     */
    public Map<String, List<String>> connectedToLocks = new HashMap();
    
    public int numberOfSlotsInGroup = 5;
    public String name;
    public boolean isVirtual = false;
    
    public Integer codeSize;
    
    public void rebuildCodeMatrix(List<LockServer> servers, int codeSize) {
        
        int j = 0;
        
        for (int i=1; i<=numberOfSlotsInGroup; i++) {
            MasterUserSlot masterSlot = groupLockCodes.get(i);
            
            if (masterSlot == null) {
                masterSlot = new MasterUserSlot();
            }
            
            masterSlot.slotId = i;
            
            if (!masterSlot.hasCode()) {
                masterSlot.generateNewCode(codeSize);
            }
            
            connectedToLocks.clear();
            masterSlot.subSlots.clear();
            
            for (LockServer server : servers) {
                ArrayList<String> lockIds = new ArrayList();
                
                for (Lock lock : server.getLocks()) {
                    List<UserSlot> slots = lock.getAllSlotsAssignedToGroup(id);
                    if (slots.isEmpty()) {
                        continue;
                    }
                    
                    lockIds.add(lock.id);
                    
                    if (!isInSubSlot(masterSlot.subSlots, slots.get(j))) {
                        masterSlot.subSlots.add(slots.get(j));
                    }
                }
                
                connectedToLocks.put(server.getId(), lockIds);
            }
            
            groupLockCodes.put(i, masterSlot);
            j++;
        }
    }

    public void changeCode(int slotId, int pinCode, String cardId, int codeSize) {
        MasterUserSlot slot = groupLockCodes.get(slotId);
        slot.changeCode(pinCode, cardId, codeSize);
    }

    void renewCodeForSlot(int slotId, int codeSize) {
        MasterUserSlot slot = groupLockCodes.get(slotId);
        slot.generateNewCode(codeSize);
    }

    void changeDatesForSlot(int slotId, Date validFrom, Date validTo, int codeSize) {
        MasterUserSlot slot = groupLockCodes.get(slotId);
        slot.setDates(validFrom, validTo, codeSize);
    }

    public HashMap<Integer, MasterUserSlot> getGroupLockCodes() {
        
        groupLockCodes.values().stream()
                .forEach(slot -> {
                    slot.finalize();
                });
        
        return groupLockCodes;
    }

    
    public void finalize(HashMap<String, LockServer> lockServers) {
        groupLockCodes.values().stream()
                .forEach(masterGroup -> {
                    checkIfAllSlotsHasBeenUpdated(masterGroup, lockServers);
                });
    }

    private void checkIfAllSlotsHasBeenUpdated(MasterUserSlot masterUserSlot, HashMap<String, LockServer> lockServers) {
        if (masterUserSlot.subSlots.isEmpty()) {
            masterUserSlot.allCodesAdded = false;
            return;
        }
        
        masterUserSlot.allCodesAdded = true;
        masterUserSlot.slotsNotOk.clear();
        
        for (UserSlot subSlot : masterUserSlot.subSlots) {
            LockServer server = lockServers.get(subSlot.connectedToServerId);
            if (server != null) {
                Lock lock = server.getLock(subSlot.connectedToLockId);
                if (lock != null) {
                    UserSlot slot = lock.getUserSlot(subSlot.slotId);
                    
                    boolean isGetShopLockBoxServer = (server instanceof GetShopLockBoxServer);
                    if (slot.needToBeRemoved || slot.toBeAdded || slot.toBeRemoved || (slot.duplicate && !isGetShopLockBoxServer) ) {
                        masterUserSlot.slotsNotOk.add(slot);
                        masterUserSlot.allCodesAdded = false;
                        masterUserSlot.connectedToLockId = lock.id;
                        masterUserSlot.connectedToServerId = server.getId();
                    }
                }
            }
        }
    }

    private boolean isInSubSlot(List<UserSlot> subSlots, UserSlot slotToCheck) {
        for (UserSlot slot : subSlots) {
            if (slot.slotId == slotToCheck.slotId && slot.connectedToLockId.equals(slotToCheck.connectedToLockId)) {
                return true;
            }
        }
        
        return false;
    }

    boolean isConnectedToLock(String serverId, String lockId) {
        if (connectedToLocks.get(serverId) == null) {
            return false;
        }
        
        return connectedToLocks.get(serverId).contains(lockId);
    }
    
    boolean isConnectedToLock(String lockId) {
        for (List<String> lockIds : connectedToLocks.values()) {
            if (lockIds != null && lockIds.contains(lockId))
                return true;
        }
        
        return false;
    }
}
