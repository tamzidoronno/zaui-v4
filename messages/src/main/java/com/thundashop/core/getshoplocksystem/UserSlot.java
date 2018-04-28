/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import java.io.Serializable;
import java.util.Date;
import org.mongodb.morphia.annotations.Transient;

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
    public int codeSize = 6;
    
    public String comment;
    
    public int slotId;
    public LockCode code = null;
    
    public LockCode previouseCode = null;
    
    private String belongsToGroupId = "";
    
    public String connectedToServerId;
    public String connectedToLockId;
    
    public String takenInUseTextReference = "";
    public String takenInUseManagerName = "";
    public String takenInUseReference = "";
    
    
    @Transient
    public String isAddedToLock = "unkown";

    public void generateNewCode() {
        previouseCode = code;
        
        code = new LockCode();
        code.generateRandomCode(codeSize);
        takenInUseDate = null;
        takenInUseTextReference = "";
        takenInUseManagerName = "";
        takenInUseReference = "";
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
        previouseCode = code;
        
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

    boolean isSameCode(LockCode code) {
        if (this.code == null)
            return false;
        
        if (code == null)
            return false;
        
        if (this.code.pinCode == code.pinCode && this.code.cardId.equals(code.cardId)) {
            return true;
        }
        
        return false;
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
