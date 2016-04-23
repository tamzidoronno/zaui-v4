/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.scope;

import com.thundashop.core.common.Session;

/**
 *
 * @author ktonder
 */
public abstract class GetShopSessionObject {
    public String storeId = "";
    private Session session;
    private boolean sessionUsed = false;

    public void setSession(Session session) {
        sessionUsed = false;
        this.session = session;
    }
    
    public Session getSessionSilent() {
        return session;
    }
    
    public Session getSession() {
        if (1 == 1)
            throw new NullPointerException();
        
        sessionUsed = true;
        return session;
    }

    public boolean isSessionUsed() {
        return sessionUsed;
    }
    
    public abstract void clearSession();
}
