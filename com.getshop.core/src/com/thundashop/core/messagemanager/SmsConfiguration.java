/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.messagemanager;

import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Setting;
import com.thundashop.core.common.StoreHandler;
import com.thundashop.core.pagemanager.IPageManager;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@Scope("prototype")
public class SmsConfiguration {
    private String username;
    private String apiId;
    private String password;
    private String numberprefix;
    public void setup(String storeId) throws ErrorException {
        StoreHandler storeHandler = AppContext.storePool.getStorePool(storeId);
        
        if (storeHandler == null) {
            throw new ErrorException(26);
        }
        
        IPageManager pageManager = storeHandler.getManager(IPageManager.class);
        HashMap<String, Setting> confSettings = pageManager.getSecuredSettingsInternal("Clickatell");
        
        if (confSettings == null) {
            throw new ErrorException(26);
        }
        
        if (confSettings.get("username") == null) {
            throw new ErrorException(26);
        }
        
        username = confSettings.get("username").value;
        password  = confSettings.get("password").value;
        apiId = confSettings.get("apiid").value;
        numberprefix = confSettings.get("numberprefix").value;
    }

    public String getUsername() {
        return username;
    }

    public String getApiId() {
        return apiId;
    }

    public String getPassword() {
        return password;
    }

    public String getNumberprefix() {
        return numberprefix;
    }
   
}
