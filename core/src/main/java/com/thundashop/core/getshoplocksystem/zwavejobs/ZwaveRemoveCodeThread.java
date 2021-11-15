/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem.zwavejobs;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.thundashop.core.getshoplocksystem.LocstarLock;
import com.thundashop.core.getshoplocksystem.UserSlot;
import com.thundashop.core.getshoplocksystem.ZwaveLockServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ktonder
 */
public class ZwaveRemoveCodeThread extends ZwaveThread {

    private static final Logger logger = LoggerFactory.getLogger(ZwaveRemoveCodeThread.class);

    private final UserSlot slot;
    private final boolean silent;

    public ZwaveRemoveCodeThread(ZwaveLockServer server, UserSlot slot, LocstarLock lock, boolean silent, String storeId) {
        super(server, lock, 10, storeId);
        this.silent = silent;
        this.slot = slot;
        this.slot.isAddedToLock = "unkown";
    }

    public void directExecute() {
        server.httpLoginRequestZwaveServer(getAddressForRemovingCode());
    }
    
    @Override
    public boolean execute(int attempt) {
        waitForEmptyQueue();
        server.httpLoginRequestZwaveServer(getAddressForRemovingCode());
        waitForEmptyQueue();
        
        if (isCodeAdded().equals("no")) {
            if (slot.previouseCode != null) {
                logger.info("sid-{} Code was successfully removed, code: {} : {} . Its been on the lock since: {}",
                        storeId, slot.previouseCode.pinCode, slot.slotId, slot.previouseCode.addedDate);
            } else {
                logger.info("sid-{} Code was successfully removed, slotId: {}", storeId, slot.slotId);
            }
            
            if (!silent) {
                server.codeRemovedFromLock(lock.id, slot);
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if code is already added, if it is throw an exception. If its not able to check this properly due
     * to server connection etc, return false.
     */
    private String isCodeAdded() {
        server.httpLoginRequestZwaveServer(getFetchingOfCodes());
        waitForEmptyQueue();
        
        String result = server.httpLoginRequestZwaveServer(getAddressForFetchingLog());

        if (result.equals("null") || result.isEmpty()) {
            slot.isAddedToLock = "unkown";
            return slot.isAddedToLock;
        }

        Gson gson = new Gson();
        JsonElement element = gson.fromJson(result, JsonElement.class);

        if (element != null && element.getAsJsonObject() != null && element.getAsJsonObject().get("hasCode") != null) {
            JsonElement hasCodeElement = element.getAsJsonObject().get("hasCode");
            if (hasCodeElement.getAsJsonObject() != null ) {
                boolean added = hasCodeElement.getAsJsonObject().get("value").getAsBoolean();
                if (added) {
                    slot.isAddedToLock = "yes";
                } else {
                    slot.isAddedToLock = "no";
                }
            }
        }
        
        return slot.isAddedToLock;
    }

    private String getFetchingOfCodes() {
        return "ZWave.zway/Run/devices["+lock.zwaveDeviceId+"].instances[0].commandClasses[99].Get("+slot.slotId+")";
    }
    
    private String getAddressForRemovingCode() {
        return "ZWaveAPI/Run/devices["+lock.zwaveDeviceId+"].UserCode.Set("+slot.slotId+","+slot.code.pinCode+",0)";
    }

    private String getAddressForFetchingLog() {
        return "ZWave.zway/Run/devices["+lock.zwaveDeviceId+"].UserCode.data["+slot.slotId+"]";
    }

}
