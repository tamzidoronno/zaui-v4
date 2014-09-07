/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.google.code.morphia.Morphia;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.usermanager.data.User;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;


/**
 *
 * @author ktonder
 */
public class MergeUsersToMakeThemUnique {
    private ArrayList<User> users = new ArrayList();
    
    public void run() throws UnknownHostException {
        Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("UserManager");
        DBCollection collection = db.getCollection("col_2fac0e57-de1d-4fdf-b7e4-5f93e3225445");
        getUsers(collection);
        mergeUsers(collection);
    }
    
    private void getUsers(DBCollection collection) {
        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);

        
        DBCursor allDocs = collection.find();
        while (allDocs.hasNext()) {
            DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, allDocs.next());
            if (dataCommon instanceof User) {
                User user = (User)dataCommon;
                if (user.deleted != null) {
                    continue;
                }
                if (user.type < 50) {
                    users.add(user);
                }
            }
            
        }
    }
    
    public static void main(String args[]) throws UnknownHostException {
        MergeUsersToMakeThemUnique merger = new MergeUsersToMakeThemUnique();
        merger.run();
    }

    private void mergeUsers(DBCollection collection) {
        TreeSet<String> uniqueEmails = new TreeSet();
        
        for (User user : users) {
            uniqueEmails.add(user.emailAddress);
        }
        
        for (String email : uniqueEmails) {
            if (email == null) {
                continue;
            }
            
            List<User> sameAccounts = getSameAccounts(email);
            System.out.println("User accounts: " + sameAccounts.size() + ", email: " + email);
            for (User user : sameAccounts) {
                System.out.println("  - " + user.fullName);
            }
        }
        
        System.out.println("Uniqe emails: " + uniqueEmails.size());
        System.out.println("Accounts emails: " + users.size());
    }

    private List<User> getSameAccounts(String email) {
        List<User> retUsers = new ArrayList();
        for (User user : users) {
            if (user.emailAddress != null && user.emailAddress.equals(email)) {
                retUsers.add(user);
            }
        }
        
        return retUsers;
    }
}
