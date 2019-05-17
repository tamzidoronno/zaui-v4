/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.gsd;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class DeviceMessageQueue extends DataCommon {
    public Date lastPulled;
    public String deviceId = "";
    private List<GetShopDeviceMessage> messages = new ArrayList();

    synchronized void clear() {
        messages.clear();
    }

    synchronized boolean isEmpty() {
        return messages.isEmpty();
    }

    synchronized void markMessagesPulled() {
        lastPulled = new Date();
    }

    synchronized void addMessage(GetShopDeviceMessage messageToUse) {
        messages.add(messageToUse);
    }

    synchronized List<GetShopDeviceMessage> getMessages() {
        return new ArrayList(messages);
    }
}
