/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.usermanager.data.User;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class GSAdmins extends ManagerBase {
    public HashMap<String, User> admins = new HashMap();

    public GSAdmins() {
        User user = new User();
        user.id = "gskai";
        user.fullName = "Kai Tønder";
        user.username = user.id;
        user.cellPhone = "48311484";
        user.prefix = "47";
        user.type = 100;
        user.password = "839AEB0CCC08E57D476A51F850C58AE49C0A5809CAFC3233D7FBC4A018C9C45C";
        admins.put(user.id, user);
        
        User user2 = new User();
        user2.id = "gspal";
        user2.fullName = "Pål Tønder";
        user2.cellPhone = "46774240";
        user2.username = user2.id;
        user2.prefix = "47";
        user2.type = 100;
        user2.password = "839AEB0CCC08E57D476A51F850C58AE49C0A5809CAFC3233D7FBC4A018C9C45C";
        
        admins.put(user2.id, user2);
        
        User user3 = new User();
        user3.id = "gsmilan";
        user3.fullName = "Milan Sarosac";
        user3.cellPhone = "5366909";
        user3.username = user3.id;
        user3.prefix = "36";
        user3.type = 100;
        user3.password = "54D72554F7E5A8B305B8F37907E3F4B3CB1A1A290AC452F7121F6CB2E05F0D9E";
        
        admins.put(user3.id, user3);
    }
    
    
    public User getGSAdmin(String userName) {
        return admins.get(userName);
    }
    
    public static void main(String[] args) {
    }
    
    private static String encryptPassword(String password) throws ErrorException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (Exception ex) {
            throw new ErrorException(88);
        }
    }

    public List<User> getAllAdmins() {
        return new ArrayList(admins.values());
    }
}
