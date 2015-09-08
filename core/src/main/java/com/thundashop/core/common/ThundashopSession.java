/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

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
    
    public ThundashopSession() {
        lastActive = new Date();
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
        objects.put(name, object);
    }

    void removeObject(String name) {
        objects.remove(name);
    }
    
}
