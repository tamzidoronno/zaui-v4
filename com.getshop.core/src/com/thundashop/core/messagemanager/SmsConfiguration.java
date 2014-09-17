/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.messagemanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Setting;
import com.thundashop.core.pagemanager.PageManager;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@GetShopSession
public class SmsConfiguration {
    private String username;
    private String apiId;
    private String password;
    private String numberprefix;
    
    @Autowired
    private PageManager pageManager;
    
    public void setup(String storeId) throws ErrorException {
        
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
