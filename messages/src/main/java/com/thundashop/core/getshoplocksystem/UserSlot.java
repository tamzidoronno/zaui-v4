/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class UserSlot implements Serializable {
    public boolean toBeRemoved = false;
    public boolean toBeAdded = false;
    public boolean needToBeRemoved = false;
    public boolean duplicate = false;
    public Date takenInUseDate = null;
    public boolean isCurrentlyUpdating = false;
//    public int codeSize = 6;
    
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
    public int pinCodeAddedToServer = 0;

    /**
     * Will be set to true if the slot is in use 
     * by the group slot.
     */
    public boolean inUse = false;
    
    /**
     * This is the device id that it is used on the server.
     */
    public Integer serverLockSlotId;
    
    public void generateNewCode(int codeSize) {
        previouseCode = code;
        
        code = new LockCode();
        code.generateRandomCode(codeSize);
        takenInUseDate = null;
        takenInUseTextReference = "";
        takenInUseManagerName = "";
        takenInUseReference = "";
    }

    void generateNewUniqueCode(int codeSize, List<Integer> codes) {
        previouseCode = code;
        
        code = new LockCode();
        code.generateNewUniqueCode(codeSize, codes);
        takenInUseDate = null;
        takenInUseTextReference = "";
        takenInUseManagerName = "";
        takenInUseReference = "";
    }

    public void finalize() {
        if (duplicate) {
            toBeAdded = false;
        }
        
        if (code == null) {
            toBeRemoved = false;
            toBeAdded = false;
            return;
        } 
        
        if (code != null) {
            toBeRemoved = false;
        }
        
        if (code.addedDate == null && !duplicate) {
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

    void markCodeForResending(int codeSize) {
        
        if (code == null) {
            generateNewCode(codeSize);
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
    
    int getPincode() {
        if (code != null)
            return code.pinCode;
        
        return -1;
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
    
    void setCodeObject(LockCode code, int codeSize) {
        this.code = code;
        markCodeForResending(codeSize);
    }
    
    boolean hasCode() {
        return code != null;
    }

    void changeCode(int pinCode, String cardId, int codeSize, List<Integer> codesInUse) {
        if (code == null) {
            generateNewUniqueCode(codeSize, codesInUse);
        }
        
        code.changeCode(pinCode, cardId);
        markCodeForResending(codeSize);
        takenInUseDate = null;
    }
    
    void setDates(Date validFrom, Date validTo, int codeSize) {
        if (code == null) {
            generateNewCode(codeSize);
        }
        
        code.validFrom = validFrom;
        code.validTo = validTo;
        
    }

    void setAddedDate(Date date) {
        if (code != null) {
            code.addedDate = date;
        }
    }
    
    void resetSlot() {
        code = null;
    }

}
