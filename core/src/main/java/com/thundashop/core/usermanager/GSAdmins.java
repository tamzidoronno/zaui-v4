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
        user.password = "D12DDF5C72A3748F5669E6669F2835DF5C061A81B025308002792765B5C8526E";
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
    }
    
    
    public User getGSAdmin(String userName) {
        return admins.get(userName);
    }
    
    public static void main(String[] args) {
        System.out.println(encryptPassword("micemice"));
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
