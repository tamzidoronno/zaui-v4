/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.thundashop.core.usermanager.data.User;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author ktonder
 */
public class ThundashopSession  implements Serializable {
    private HashMap<String, String> objects = new HashMap<String, String>();
    private Date lastActive;
    private HashMap<String, Date> added = new HashMap();
    private String sessionId;

    public ThundashopSession() {
        lastActive = new Date();
    }
    
    public ThundashopSession(String sessionId) {
        lastActive = new Date();
        this.sessionId = sessionId;
    }

    public String getObject(String name) {
        return objects.get(name);
    }
    
    public void updateLastActive() {
        lastActive = new Date();
    }
    
    public boolean hasExpired() {
        if(lastActive == null) {
            return true;
        }
        
        Date curTime = new Date();
        long timePassed = lastActive.getTime();
        long diff = curTime.getTime()-timePassed;
        if(diff > (1000*60*60*12)) {
            return true;
        }
        return false;
    }
    
    public void addObject(String name, String object) {
        added.put(name, new Date());
        objects.put(name, object);
    }

    void removeObject(String name) {
        objects.remove(name);
    }

    public Date getAdded(String name) {
        return added.get(name);
    }

    Integer getTimeout(User user) {
        long timePassed = System.currentTimeMillis() - lastActive.getTime();
        
        if (user.sessionTimeOut <= 0) {
            return Integer.MAX_VALUE;
        } 
        
        long sessionTimeOutTime = user.sessionTimeOut * 60 * 1000;
        long timeLeft = (sessionTimeOutTime - timePassed) / 1000;
        return (int)timeLeft;
    }
    
}
