/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.usermanager.data.User;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class TotpHandler {
    private String storeIdToCheckUsersAgainst = "13442b34-31e5-424c-bb23-a396b7aeb8ca";
    
    @Autowired
    private Database database;
    
    private List<User> users = new ArrayList();

    public TotpHandler() {
    }
    
    public User verify(String username, String password, int code) {
        User user = getUserByUserNameAndPassword(username, password);
        
        if (user == null) {
            return null;
        }
        
        if (user.lastTotpVerificationCodeUsed == code) {
            return null;
        }
        
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        
        if (gAuth.authorize(user.totpKey, code)) {
            user.lastTotpVerificationCodeUsed = code;
            return getSecureUser(user);
        }

        return null;
    }
    
    private synchronized void loadUsers() {
        if (!users.isEmpty()) {
            return;
        }
        
        database.getAll("UserManager", storeIdToCheckUsersAgainst).forEach(data -> {
            if (data instanceof User) {
                User user = ((User)data);
                
                if (user.type == 100) {
                    users.add(user);
                }
            }
        });
    }
    
    public List<User> getAllUsers() {
        loadUsers();
        
        List<User> retUsers = new ArrayList();

        users.stream().forEach(user -> {
            if (user == null) {
                return;
            }
            
            User retUser = user.jsonClone();
            
            retUser.totpKey = UUID.randomUUID().toString();
            retUser.password = UUID.randomUUID().toString();
            retUser.referenceKey = UUID.randomUUID().toString();
            retUser.pinCode = UUID.randomUUID().toString();
            retUser.deepFreeze = true;
            
            retUsers.add(retUser);
        });
        
        
        return retUsers;
    }
    
    private User getUserByUserNameAndPassword(String username, String encryptedPassword) {
        loadUsers();
        
        for (User user : users) {
            if (user.emailAddress != null && user.emailAddress.equals(username) && user.password != null && user.password.equals(encryptedPassword)) {
                return user;
            }
        }
        
        return null;
    }

    boolean isCommonDbThisStore(String storeId) {
        return storeIdToCheckUsersAgainst.equals(storeId);
    }

    private User getSecureUser(User user) {
        for (User iuser : getAllUsers()) {
            if (iuser.id.equals(user.id)) {
                return iuser;
            }
        }
        
        return null;
    }
}
