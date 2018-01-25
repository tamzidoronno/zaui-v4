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
    private HashMap<Integer, MasterUserSlot> groupLockCodes = new HashMap();
    public Map<String, List<String>> connectedToLocks = new HashMap();
    
    public int numberOfSlotsInGroup = 5;
    public String name;

    public void rebuildCodeMatrix(List<LockServer> servers) {
        
        int j = 0;
        
        for (int i=1; i<=numberOfSlotsInGroup; i++) {
            MasterUserSlot masterSlot = groupLockCodes.get(i);
            
            if (masterSlot == null) {
                masterSlot = new MasterUserSlot();
            }
            
            masterSlot.slotId = i;
            
            if (!masterSlot.hasCode()) {
                masterSlot.generateNewCode();
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

    public void changeCode(int slotId, int pinCode, String cardId) {
        MasterUserSlot slot = groupLockCodes.get(slotId);
        slot.changeCode(pinCode, cardId);
    }

    void renewCodeForSlot(int slotId) {
        MasterUserSlot slot = groupLockCodes.get(slotId);
        slot.generateNewCode();
    }

    void changeDatesForSlot(int slotId, Date validFrom, Date validTo) {
        MasterUserSlot slot = groupLockCodes.get(slotId);
        slot.setDates(validFrom, validTo);
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
                    
                    if (slot.needToBeRemoved || slot.toBeAdded || slot.toBeRemoved ) {
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
}
