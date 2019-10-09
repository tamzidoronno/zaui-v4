/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class GetShopLock extends Lock {
    
    
    public Map<Integer, GetShopUserSlot> lockUserSlots = new HashMap();
    
    public int deviceId = 0;
    public int zwaveDeviceId = 0;

    UserSlot getSlotByServerSlotId(int serverLockSlotId) {
        return getUserSlots()
                .stream()
                .filter(s -> s.serverLockSlotId != null && s.serverLockSlotId == serverLockSlotId)
                .findAny()
                .orElse(null);
    }
}
