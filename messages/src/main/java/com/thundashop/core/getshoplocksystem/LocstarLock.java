/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class LocstarLock extends Lock {
    public int zwaveDeviceId;
    public boolean prioritizeLockUpdate = false;
    
    @Transient
    public boolean currentlyUpdating = false;
    
    public int currentlyAttempt;
    public boolean dead = false;
    public Date markedDateAtDate;
    
    public List<Integer> routing = new ArrayList();
    
    public Integer getJobSize() {
        return getToRemove().size() + getToUpdate().size();
    }

    public boolean shouldPrioty() {
        finalize();
        
        if (prioritizeLockUpdate) {
            return true;
        }
        
        if (getJobSize() > 0 && prioritizeLockUpdate)
            return true;
        
        return false;
    }

}