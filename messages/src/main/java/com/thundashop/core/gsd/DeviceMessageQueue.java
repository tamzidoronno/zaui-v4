/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.gsd;

import com.thundashop.core.common.DataCommon;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author ktonder
 */
public class DeviceMessageQueue extends DataCommon {
    public Date lastPulled;
    public String deviceId = "";
    public ConcurrentLinkedQueue<GetShopDeviceMessage> messages = new ConcurrentLinkedQueue();

    void clear() {
        messages.clear();
    }

    boolean isEmpty() {
        return messages.isEmpty();
    }

    void markMessagesPulled() {
        lastPulled = new Date();
    }
}
