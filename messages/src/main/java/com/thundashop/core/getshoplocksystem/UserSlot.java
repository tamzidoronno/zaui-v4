/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class UserSlot implements Serializable {
    public boolean toBeRemoved = false;
    public boolean toBeAdded = false;
    public boolean needToBeRemoved = false;
    public Date takenInUseDate = null;
    public boolean isCurrentlyUpdating = false;
    
    public String comment;
    
    public int slotId;
    public LockCode code = null;
    
    private String belongsToGroupId = "";
    
    public String connectedToServerId;
    public String connectedToLockId;
    
    

    public void generateNewCode() {
        code = new LockCode();
        code.generateRandomCode();
        takenInUseDate = null;
    }

    public void finalize() {
        if (code == null) {
            toBeRemoved = false;
            toBeAdded = false;
            return;
        } 
        
        if (code != null) {
            toBeRemoved = false;
        }
        
        if (code.addedDate == null) {
            toBeAdded = true;
        }
        
        if (needToBeRemoved && code.removedDate == null) {
            toBeRemoved = true;
        }
        if (code != null) {
            code.slotId = slotId;
        }
    }

    public void remove() {
        needToBeRemoved = true;
    }

    void codeAddedSuccesfully() {
        code.addedDate = new Date();
        needToBeRemoved = false;
        toBeAdded = false;
        toBeRemoved = false;
        finalize();
    }

    void markCodeForDeletion() {
        
        if (code != null && code.addedDate == null) {
            code = null;
        }
        
        needToBeRemoved = true;
        toBeAdded = false;
        toBeRemoved = false;
        finalize();
    }

    void markCodeForResending() {
        
        if (code == null) {
            generateNewCode();
        }
        
        code.addedDate = null;
        needToBeRemoved = false;
        toBeAdded = false;
        toBeRemoved = false;
    }

    public boolean isUserSlotTakenByGroup() {
        return belongsToGroupId != null && !belongsToGroupId.isEmpty();
    }

    boolean belongsToGroup(String lockGroupId) {
        return belongsToGroupId != null && belongsToGroupId.equals(lockGroupId);
    }

    void claimGroupId(String groupId) {
        belongsToGroupId = groupId;
    }

    void reaseFromGroup() {
        belongsToGroupId = "";
        markCodeForDeletion();
    }

    void setCodeObject(LockCode code) {
        this.code = code;
        markCodeForResending();
    }
    
    boolean hasCode() {
        return code != null;
    }

    void changeCode(int pinCode, String cardId) {
        if (code == null) {
            generateNewCode();
        }
        
        code.changeCode(pinCode, cardId);
        markCodeForResending();
        takenInUseDate = null;
    }
    
    void setDates(Date validFrom, Date validTo) {
        if (code == null) {
            generateNewCode();
        }
        
        code.validFrom = validFrom;
        code.validTo = validTo;
        
    }

}
