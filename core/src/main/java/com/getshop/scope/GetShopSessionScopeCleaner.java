/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.scope;

import com.thundashop.core.common.AppContext;

/**
 *
 * @author ktonder
 */
public class GetShopSessionScopeCleaner implements Runnable {

    @Override
    public void run() {
        while(true) {
            if (AppContext.appContext != null) {
                GetShopSessionScope scope = AppContext.appContext.getBean(GetShopSessionScope.class);
                if (scope != null) {
                    scope.cleanupThreadSessions();
                }
            }
            
            try { Thread.sleep(120000); } catch (Exception ex) {
                break;
            }
        }
    }
    
}
