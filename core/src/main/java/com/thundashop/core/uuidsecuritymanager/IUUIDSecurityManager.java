package com.thundashop.core.uuidsecuritymanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;

/**
 * Manager for handling security for uuids in getshop.
 */
@GetShopApi
public interface IUUIDSecurityManager {
    public boolean hasAccess(String uuid, boolean read, boolean write);
    
    @Administrator
    public void grantAccess(String userId, String uuid, boolean read, boolean write);
}
