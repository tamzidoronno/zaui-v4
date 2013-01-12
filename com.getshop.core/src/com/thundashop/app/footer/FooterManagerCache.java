/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.app.footer;

import com.thundashop.app.footermanager.data.Configuration;
import com.thundashop.core.common.CachingKey;
import com.thundashop.core.common.ErrorException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author boggi
 */
public class FooterManagerCache implements IFooterManager {
    private final FooterManager manager;
    private final String addr;

    public FooterManagerCache(FooterManager manager, String addr) {
        this.addr = addr;
        this.manager = manager;
    }

    @Override
    public Configuration setLayout(Integer numberOfColumns) throws ErrorException {
        getConfiguration();
        return null;
    }

    @Override
    public Configuration getConfiguration() throws ErrorException {
        Configuration result = manager.getConfiguration();
        
        CachingKey key = new CachingKey();
        
        List<String> keys = new ArrayList();
        key.args = keys;
        key.interfaceName = getInterfaceName();
        key.sessionId = "";
        key.method = "getConfiguration";
        
        manager.getCacheManager().addToCache(key, result, manager.storeId, addr);
        return null;
    }

    @Override
    public Configuration setType(Integer column, Integer type) throws ErrorException {
        getConfiguration();
        return null;
    }

    private String getInterfaceName() {
        return this.getClass().getInterfaces()[0].getCanonicalName().replace("com.thundashop.", "");
    }
    
}
