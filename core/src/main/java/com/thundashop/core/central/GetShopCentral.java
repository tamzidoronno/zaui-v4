/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.central;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.gsd.GdsManager;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class GetShopCentral extends ManagerBase implements IGetShopCentral {
    private HashMap<String, CentralAccessToken> accessTokens = new HashMap();
    
    @Autowired
    private UserManager userManager;

    @Autowired
    private GdsManager gdsManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon iData : data.data) {
            if (iData instanceof CentralAccessToken) {
                accessTokens.put(iData.id, (CentralAccessToken)iData);
            }
        }
    }
    
    @Override
    public List<CentralAccessToken> getTokens() {
        return new ArrayList(accessTokens.values());
    }

    private CentralAccessToken getAccessToken(String tokenId) {
        return accessTokens.values()
                .stream()
                .filter(o -> o.accessToken.equals(tokenId))
                .findAny()
                .orElse(null);
    }

    @Override
    public Store validateAccessToken(String token) {
        if (isValidAccessToken(token)) {
            return getStore();
        }
        
        return null;
    }

    @Override
    public String login(String token, User user) {
        if (!isValidAccessToken(token)) {
            return "access_denied";
        }
        
        gdsManager.createCentralDevice(token);
        
        user.referenceKey = UUID.randomUUID().toString();
        userManager.saveUserSecure(user);
        return user.referenceKey;
    }

    private boolean isValidAccessToken(String token) {
        return getAccessToken(token) != null;
    }


    @Override
    public void createNewAccessToken(String name) {
        CentralAccessToken token = new CentralAccessToken();
        token.description = name;
        saveObject(token);
        accessTokens.put(token.id, token);
        
        User user = new User();
        user.fullName = "Integration " + name;
        user.type = User.Type.ADMINISTRATOR;
        user.password = UUID.randomUUID().toString();
        user.emailAddress = UUID.randomUUID().toString();
        user.externalTokenId = token.id;
        
        userManager.directSaveUser(user);
    }

    @Override
    public boolean loginByToken(String token) {
        if (!isValidAccessToken(token)) {
            return false;
        }
        
        CentralAccessToken accessToken = getAccessToken(token);
        User user = userManager.getAllUsers()
                .stream()
                .filter(u -> u.externalTokenId.equals(accessToken.id))
                .findFirst()
                .orElse(null);
        
        if (user != null) {
            String refKey = login(token, user);
            userManager.logonUsingRefNumber(refKey);
            return true;
        }
        
        return false;
    }

    public boolean hasBeenConnectedToCentral() {
        return gdsManager.getCentralDevice() != null;
    }

    public Date hasBeenConnectedToCentralSince() {
        return gdsManager.getCentralDeviceCreatedDate();
    }

    @Override
    public boolean isConnectedToACentral() {
        return hasBeenConnectedToCentral();
    }
    
}
