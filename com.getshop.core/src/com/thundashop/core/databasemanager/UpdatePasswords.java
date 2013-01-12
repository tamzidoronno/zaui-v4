/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.thundashop.core.common.ErrorException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.List;
import java.util.Set;

/**
 *
 * @author boggi
 */
public class UpdatePasswords {
    public static void main(String args[]) throws UnknownHostException, ErrorException {
        Mongo m = new Mongo( "localhost" , 27017);
        DB db = m.getDB("UserManager");
        Set<String> collections = db.getCollectionNames();
        for(String col : collections) {
            DBCursor users = db.getCollection(col).find();
            while(users.hasNext()) {
                DBObject user = users.next();
                if(user.get("password") != null) {
                    System.out.println(user.get("password"));
                    String pass = user.get("password").toString();
                    if(pass.length() == 64) {
                        System.out.println("Already encrypted!");
                        continue;
                    }
                    String password = UpdatePasswords.encryptPassword(user.get("password").toString());
                    System.out.println(password);
                    user.put("password", password);
                    db.getCollection(col).save(user);
                }
            }
        }
    }
    
    public static String encryptPassword(String password) throws ErrorException {
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
}
