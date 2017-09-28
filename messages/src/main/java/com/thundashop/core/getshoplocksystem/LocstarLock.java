/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class LocstarLock extends Lock {
    public int zwaveDeviceId;
    public boolean prioritizeLockUpdate = false;

    public Integer getJobSize() {
        return getToRemove().size() + getToUpdate().size();
    }

    public boolean shouldPrioty() {
        finalize();
        
        if (getJobSize() > 0 && prioritizeLockUpdate)
            return true;
        
        return false;
    }

}