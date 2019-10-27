/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.thundashop.core.usermanager.data.User;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author ktonder
 */
public class Session {
    public User currentUser;
    public String storeId;
    public String id;
    private HashMap<String, Object> storedSessionObjects = new HashMap();
    public Date lastActive;
    public String language;
    
    public void put(String key, Object object) {
        storedSessionObjects.put(key, object);
    }
    
    public Object remove(String key) {
        return storedSessionObjects.remove(key);
    }
    
    public Object get(String key) {
        return storedSessionObjects.get(key);
    }

    public boolean hasExpired() {
        if (lastActive == null) {
            return false;
        }
        
        long lastActiveTime = lastActive.getTime();
        long currentTime = System.currentTimeMillis();
        
        // 12 hours expiration time.
        boolean expired = (currentTime - lastActiveTime) > (12*60*60*1000);
        
        return expired;
        
    }
}
