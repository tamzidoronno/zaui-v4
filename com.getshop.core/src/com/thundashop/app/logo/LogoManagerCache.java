/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.app.logo;

import com.thundashop.app.logomanager.data.SavedLogo;
import com.thundashop.core.common.CachingKey;
import com.thundashop.core.common.ErrorException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author boggi
 */
public class LogoManagerCache implements ILogoManager {

    LogoManager manager;
    private final String addr;

    public LogoManagerCache(LogoManager manager, String addr) {
        this.manager = manager;
        this.addr = addr;
    }
    
    @Override
    public void setLogo(String fileId) throws ErrorException {
        getLogo();
    }

    @Override
    public void deleteLogo() throws ErrorException {
        getLogo();
    }

    @Override
    public SavedLogo getLogo() throws ErrorException {
        SavedLogo result = manager.getLogo();
        
        CachingKey key = new CachingKey();
        
        List<String> keys = new ArrayList();
        key.args = keys;
        key.interfaceName = getInterfaceName();
        key.sessionId = "";
        key.method = "getLogo";
        
        manager.getCacheManager().addToCache(key, result, manager.storeId, addr);
        return null;
    }

    private String getInterfaceName() {
        return this.getClass().getInterfaces()[0].getCanonicalName().replace("com.thundashop.", "");
    }
    
}
