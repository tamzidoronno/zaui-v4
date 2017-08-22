/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author ktonder
 */
@GetShopIgnoreBackup
public class SessionFactory extends DataCommon {
    private ConcurrentHashMap<String, ThundashopSession> sessions = new ConcurrentHashMap<String, ThundashopSession>();
    public boolean ready;
    
    private ThundashopSession getSession(String sessionId) {
        ThundashopSession session = sessions.get(sessionId);
        if (session == null) {
            session = new ThundashopSession(sessionId);
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
        
        cleanUp();
    }
    
    public Date getWhenAdded(String sessionId, String name) {
        ThundashopSession session = getSession(sessionId);
        
        if (session != null) {
            return session.getAdded(name);
        }
        
        return null;
    }
    
    public <T> T getObject(String sessionId, String name) throws ErrorException {
        T object = getObjectPingLess(sessionId, name);
        
        ping(sessionId);
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

    private void cleanUp() {
        List<String> removeSessions = new ArrayList();
        for (String sessionId : sessions.keySet()) {
            ThundashopSession session = sessions.get(sessionId);
            if (session.hasExpired()) {
                removeSessions.add(sessionId);
            }
        }
        
        for (String sessionId : removeSessions) {
            sessions.remove(sessionId);
        }
    }

    private void ping(String sessionId) {
        ThundashopSession session = sessions.get(sessionId);
        if (session != null) {
            session.updateLastActive();
        }
    }

    public <T> T getObjectPingLess(String sessionId, String name) throws ErrorException {
        checkSessionIsNotEmpty(sessionId);
        Object object = null;
       
        ThundashopSession session = getSession(sessionId);
        if (session != null) {
            object = session.getObject(name);
        }
        
        if (name != null && name.equals("user") && session != null && session.getObject("impersonatedUser") != null) {
            object = session.getObject("impersonatedUser");
        }
        
        return (T) object;
    }

    public Integer getTimeout(User user, String sessionId) {
        ThundashopSession session = getSession(sessionId);
        
        if (session != null) {
            return session.getTimeout(user);
        }
        
        return null;
    }
    
    public String getOriginalUserId(String sessionId) {
        checkSessionIsNotEmpty(sessionId);
        ThundashopSession session = getSession(sessionId);
        return session.getObject("user");
    }

    public void cleanSession() {
        sessions.clear();
    }

    public ConcurrentHashMap<String, ThundashopSession> getAllSessions() {
        return sessions;
    }

}