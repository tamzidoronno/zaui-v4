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

import static java.lang.Thread.currentThread;

/**
 *
 * @author ktonder
 */
public class ZwaveAddCodeThread extends ZwaveThread {

    private static final Logger logger = LoggerFactory.getLogger(ZwaveAddCodeThread.class);

    private UserSlot slot;
    
    public ZwaveAddCodeThread(ZwaveLockServer server, UserSlot slot, LocstarLock lock, String storeId) {
        super(server, lock, 5, storeId);
        this.slot = slot;
        this.slot.isAddedToLock = "unkown";
    }

    @Override
    public boolean execute(int attempt) throws ZwaveThreadExecption {
        
        if (slot.isAddedToLock.equals("unkown")) {
            String result = isCodeAdded();
            if (result.equals("unkown")) {
                logger.info("sid-{} Unknown status of added code on attempt, not added: {} , slot: {} , pinCode: {} cardId: {}",
                        storeId, attempt, slot.slotId, slot.code.pinCode, slot.code.cardId);
                return false;
            }
        }
        
        if (slot.isAddedToLock.equals("yes") && !removeCodeFromSlot()) {
            return false;
        }

        server.httpLoginRequestZwaveServer(getAddressForSettingCode());
        waitForEmptyQueue();

        if (isCodeAdded().equals("yes")) {
            logger.info("sid-{} Successfully added code on attempt: {} , slot: {} , pinCode: {} , cardId: {}",
                    storeId, attempt, slot.slotId, slot.code.pinCode, slot.code.cardId);
            successfullyCompleted = true;
            server.codeAddedSuccessfully(lock.id, slot.slotId);
            return true;
        } else {
            logger.info("sid-{} Failed to add code on attempt: {}, slot: {} , pinCode: {} , cardId: {}",
                    storeId, attempt, slot.slotId, slot.code.pinCode, slot.code.cardId);
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

        String addressForFetchingLog = getAddressForFetchingLog();
        String result = server.httpLoginRequestZwaveServer(addressForFetchingLog);

        try {

            if (result.equals("null") || result.isEmpty() || "401".equals(result)) {
                slot.isAddedToLock = "unkown";
                return slot.isAddedToLock;
            }

            Gson gson = new Gson();
            JsonElement element = gson.fromJson(result, JsonElement.class);

            if (element != null && element.getAsJsonObject() != null && element.getAsJsonObject().get("hasCode") != null) {
                JsonElement hasCodeElement = element.getAsJsonObject().get("hasCode");
                if (hasCodeElement.getAsJsonObject() != null) {
                    boolean added = hasCodeElement.getAsJsonObject().get("value").getAsBoolean();
                    if (added) {
                        slot.isAddedToLock = "yes";
                    } else {
                        slot.isAddedToLock = "no";
                    }
                }
            }

        } catch (RuntimeException e) {
            // This catch block is only for logging purpose.
            logger.error("sid-{}", storeId, e);
            String errStr = currentThread().getId() + " " + currentThread().getName()
                    + " Error while calling zwave server"
                    + " addressForFetchingLog " + addressForFetchingLog
                    + " zwave server result: " + result;
            throw new RuntimeException(errStr, e);
        }

        return slot.isAddedToLock;
    }

    private String getFetchingOfCodes() {
        return "ZWave.zway/Run/devices["+lock.zwaveDeviceId+"].instances[0].commandClasses[99].Get("+slot.slotId+")";
    }
    
    private String getAddressForSettingCode() {
        return "ZWaveAPI/Run/devices["+lock.zwaveDeviceId+"].UserCode.Set("+slot.slotId+","+slot.code.pinCode+",1)";
    }

    private String getAddressForFetchingLog() {
        return "ZWave.zway/Run/devices["+lock.zwaveDeviceId+"].UserCode.data["+slot.slotId+"]";
    }

    private boolean removeCodeFromSlot() {
        ZwaveRemoveCodeThread removeJob = new ZwaveRemoveCodeThread(server, slot, lock, true, storeId);
        return removeJob.execute(20);
    }

}
