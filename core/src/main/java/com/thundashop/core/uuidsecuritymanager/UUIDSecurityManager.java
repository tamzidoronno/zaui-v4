
package com.thundashop.core.uuidsecuritymanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Session;
import com.thundashop.core.usermanager.data.User;
import org.apache.axis.handlers.SimpleSessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class UUIDSecurityManager extends ManagerBase implements IUUIDSecurityManager {
    
    @Autowired
    SecurityPool pool;

    @Override
    public boolean hasAccess(String uuid, boolean read, boolean write) {
        return pool.hasAccessToUUID(uuid, getLoggedOnUser(), read, write);
    }
    
    private User getLoggedOnUser() {
        Session cursession = getSession();
        if(cursession != null && cursession.currentUser != null) {
            return cursession.currentUser;
        }
        
        return null;
    }

    @Override
    public void grantAccess(String userId, String uuid, boolean read, boolean write) {
        pool.updateAccessToUUID(uuid, userId, read, write);
    }
    
}
