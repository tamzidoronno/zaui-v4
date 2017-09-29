/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class Lock {
    public String typeOfLock = "";
    
    public String connectedToServerId = "";
    
    public String id = UUID.randomUUID().toString();
    
    private Map<Integer, UserSlot> userSlots = new HashMap();
    
    private Date dontUpdateUntil = null;
    
    @Transient
    private List<UserSlot> toRemove = new ArrayList();
    
    @Transient
    private List<UserSlot> toUpdate = new ArrayList();
    
    @Transient
    private List<UserSlot> inUse = new ArrayList();
    
    
    public int maxnumberOfCodes = 20;
    public String name;

    public List<UserSlot> getUserSlots() {
        return new ArrayList(userSlots.values());
    }

    public void setUserSlots(List<UserSlot> userSlots) {
        userSlots.stream().forEach(s -> {
            if (this.userSlots.get(s.slotId) == null) {
                this.userSlots.put(s.slotId, s);
            }
        });
    }
    
    public void initializeUserSlots() {
        for (int i=1; i<=maxnumberOfCodes; i++) {
            if (userSlots.get(i) == null) {
                UserSlot slot = new UserSlot();
                slot.connectedToServerId = connectedToServerId;
                slot.connectedToLockId = id;
                slot.slotId = i;
                userSlots.put(slot.slotId, slot);
            }
        }
    }

    public void finalize() {
        typeOfLock = getClass().getSimpleName();
 
        toUpdate.clear();
        toRemove.clear();
        inUse.clear();
        
        userSlots.values().stream()
            .forEach(s -> { 
                s.finalize();
                
                if (s.toBeAdded && !s.toBeRemoved) {
                    toUpdate.add(s);
                }

                if (s.toBeRemoved) {
                    toRemove.add(s);
                }

                if (s.takenInUseDate != null) {
                    inUse.add(s);
                }
        });
    }

    void generateNewCodes() {
        userSlots.values().stream()
                .forEach(s -> s.generateNewCode());
    }

    public List<UserSlot> getToRemove() {
        return toRemove;
    }

    public List<UserSlot> getToUpdate() {
        return toUpdate;
    }

    public void removeCode(int slotId) {
        if (userSlots.get(slotId) != null) {
            userSlots.get(slotId).remove();
        }   
    }
    
    public LockCode codeRemovedFromLock(int slotId) {
        if (userSlots.get(slotId) != null) {
            LockCode toReturn = userSlots.get(slotId).code;
            userSlots.get(slotId).code = null;
            userSlots.get(slotId).needToBeRemoved = false;
            toReturn.removedDate = new Date();
            return toReturn;
        }
        
        return null;
    }

    void generateNewCode(int slotId) {
        if (userSlots.get(slotId) != null) {
            userSlots.get(slotId).generateNewCode();
        }
    }

    void codeAddedSuccessfully(int slotId) {
        if (userSlots.get(slotId) != null) {
            userSlots.get(slotId).codeAddedSuccesfully();
        }
    }
    
    void markCodeForDeletion(int slotId) {
        if (userSlots.get(slotId) != null) {
            userSlots.get(slotId).markCodeForDeletion();
        }
    }
    
    void markCodeForResending(int slotId) {
        if (userSlots.get(slotId) != null) {
            userSlots.get(slotId).markCodeForResending();
        }
    }

    public List<UserSlot> getAllUnusedUserSlots(String lockGroupId) {
        return userSlots.values()
                .stream()
                .filter(userslot -> !userslot.isUserSlotTakenByGroup() || userslot.belongsToGroup(lockGroupId))
                .collect(Collectors.toList());
    }
    
    public List<UserSlot> getAllSlotsAssignedToGroup(String lockGroupId) {
        return userSlots.values()
                .stream()
                .filter(userslot -> userslot.belongsToGroup(lockGroupId))
                .collect(Collectors.toList());
    }

    public void claimSlotForGroup(int slotId, String groupId) {
        if (userSlots.get(slotId) != null) {
            userSlots.get(slotId).claimGroupId(groupId);
        }
    }

    public void releaseAllSlotsForGroup(String groupId) {
        userSlots.values().stream().forEach(slot -> {
            if (slot.belongsToGroup(groupId)) {
                slot.reaseFromGroup();
            }
        });
    }

    public void setCodeObject(int slotId, LockCode code) {
        if (userSlots.get(slotId) != null) {
            userSlots.get(slotId).setCodeObject(code);
        }
    }
    
    public boolean canUpdate() {
        Date now = new Date();
        
        if (dontUpdateUntil != null && dontUpdateUntil.after(now)) {
            return false;
        }
        
        return true;
    }

    public void delayUpdateForFiveMinutes() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 5);
        dontUpdateUntil = cal.getTime();
    }
}
