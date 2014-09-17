/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.getshop.scope.GetShopSession;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class SessionFactory extends DataCommon {
    private ConcurrentHashMap<String, ThundashopSession> sessions = new ConcurrentHashMap<String, ThundashopSession>();
    public boolean ready;
    
    private ThundashopSession getSession(String sessionId) {
        ThundashopSession session = sessions.get(sessionId);
        if (session == null) {
            session = new ThundashopSession();
        }
        
        return session;
    }
    
    private void checkSessionIsNotEmpty(String sessionId) throws ErrorException {
        if (sessionId == null || sessionId.equalsIgnoreCase("")) {
            throw new ErrorException(20);
        }
    }
    
    public void addToSession(String sessionId, String name, String object) throws ErrorException {
        checkSessionIsNotEmpty(sessionId);
        
        ThundashopSession session = getSession(sessionId);
        session.addObject(name, object);
        sessions.put(sessionId, session);
    }
    
    public <T> T getObject(String sessionId, String name) throws ErrorException {
        checkSessionIsNotEmpty(sessionId);
        Object object = null;
       
        ThundashopSession session = getSession(sessionId);
        if (session != null) {
            object = session.getObject(name);
        }
        
        if (name != null && name.equals("user") && session != null && session.getObject("impersonatedUser") != null) {
            object = session.getObject("impersonatedUser");
        }
        
        return (T)object;
    }
    
    public void cancelImpersonating(String sessionId) throws ErrorException {
        checkSessionIsNotEmpty(sessionId);
       
        ThundashopSession session = getSession(sessionId);
        if (session != null) {
            session.removeObject("impersonatedUser");
        }
    }
    
    public void removeFromSession(String sessionId) {
        synchronized(sessions) {
            sessions.remove(sessionId);
        }
    }

    public boolean exists(String sessionId) {
        ThundashopSession session = sessions.get(sessionId);
        if(session == null) {
            return false;
        } else {
            session.updateLastActive();
        }
        return true;
    }
    
    /**
     * Cleaning the session dead sessions.
     */
    public void prepareForSaving() {
        List<String> removeKeys = new ArrayList<String>();
        for(String sessionId : sessions.keySet()) {
            ThundashopSession session = getSession(sessionId);
            if(session.hasExpired()) {
                removeKeys.add(sessionId);
            }
        }
        
        for (String key : removeKeys)
            sessions.remove(key);
    }

    
}
