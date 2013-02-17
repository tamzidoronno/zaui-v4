/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class HookupHandler {
    private Map<String, Boolean> responded = new HashMap();
    private AddressList addressList;
    private HookupMessage message;
    
    public HookupHandler(HookupMessage message) {
        this.message = message;
    }
    
    public synchronized void setAddressList(AddressList addressList) {
        this.addressList = addressList;
        for (String address : addressList.getIpaddressesThatAreNotMine()) {
            responded.put(address, false);
        }
    }

    public synchronized void serverResponded(Paused paused) {
        responded.put(paused.ipaddress, true);
        
        for (String ip : responded.keySet()) {
            Boolean test = responded.get(ip);
        } 
    }
    
    public synchronized String getClientServerAddress() {
        return message.ownerIpaddress;
    }
    
    public synchronized boolean ready() {
        for (Boolean bool : responded.values()) {
            if (bool.equals(false))
                return false;
        }
        
        return true;
    }
    
}
