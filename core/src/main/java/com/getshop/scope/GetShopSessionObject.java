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

    public void setSession(Session session) {
        this.session = session;
    }
    
    public Session getSession() {
        return session;
    }
    
    public abstract void clearSession();
}
