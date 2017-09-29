/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem.zwavejobs;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.thundashop.core.getshoplocksystem.LocstarLock;
import com.thundashop.core.getshoplocksystem.UserSlot;
import com.thundashop.core.getshoplocksystem.ZwaveLockServer;

/**
 *
 * @author ktonder
 */
public class ZwaveRemoveCodeThread extends ZwaveThread {

    private final UserSlot slot;
    private final boolean silent;

    public ZwaveRemoveCodeThread(ZwaveLockServer server, UserSlot slot, LocstarLock lock, boolean silent) {
        super(server, lock, 10);
        this.silent = silent;
        this.slot = slot;
    }

    @Override
    public boolean execute(int attempt) {
        waitForEmptyQueue();
        server.httpLoginRequestZwaveServer(getAddressForRemovingCode());
        waitForEmptyQueue();
        
        if (!isCodeAdded()) {
            logEntry("Code was successfully removd, code: " + slot.code + ", slotId: " + slot.slotId);
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
    private boolean isCodeAdded() {
        String result = server.httpLoginRequestZwaveServer(getAddressForFetchingLog());
        int lastUpdatedTime = 0;
        
        if (!result.equals("null") && !result.isEmpty()) {
            lastUpdatedTime = getLastUpdatedTime(result);
        }
        
        
        for (int i=0; i<10; i++) {
            server.httpLoginRequestZwaveServer(getFetchingOfCodes());
            waitForEmptyQueue();
            
            result = server.httpLoginRequestZwaveServer(getAddressForFetchingLog());
            
            int newUpdateTime = 0;
            
            if (!result.equals("null") && !result.isEmpty()) {
                newUpdateTime = getLastUpdatedTime(result);
            }
            
            if (newUpdateTime == lastUpdatedTime)
                continue;
            
            Gson gson = new Gson();
            JsonElement element = gson.fromJson(result, JsonElement.class);
            
            if (element != null && element.getAsJsonObject() != null && element.getAsJsonObject().get("hasCode") != null) {
                JsonElement hasCodeElement = element.getAsJsonObject().get("hasCode");
                if (hasCodeElement.getAsJsonObject() != null ) {
                    return hasCodeElement.getAsJsonObject().get("value").getAsBoolean();
                }
            }
        }
        
        return false;
    }
    
    private int getLastUpdatedTime(String result) throws JsonSyntaxException {
        Gson gson = new Gson();
        JsonElement element = gson.fromJson(result, JsonElement.class);
        if (element.getAsJsonObject() != null && element.getAsJsonObject().get("updateTime") != null) {
            return element.getAsJsonObject().get("updateTime").getAsInt();
        }
        
        return 0;
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
