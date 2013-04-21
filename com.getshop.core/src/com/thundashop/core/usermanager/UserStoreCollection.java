/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager;

import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.usermanager.data.User;
import java.util.*;

/**
 *
 * @author hjemme
 */
public class UserStoreCollection {
    private DatabaseSaver databaseSaver;
    private Credentials credentials;
    private String storeId;
    private UserManager userManager;
    
    private HashMap<String, User> users = new HashMap<String, User>();

    public UserStoreCollection(String storeId, DatabaseSaver databaseSaver, Credentials credentials, UserManager userManager) {
        this.storeId = storeId;
        this.databaseSaver = databaseSaver;
        this.credentials = credentials;
        this.userManager = userManager;
    }

    public void addUserDirect(User user) {
        users.put(user.id, user);
    }

    public User addUser(User user) throws ErrorException {
        user.storeId = storeId;
        databaseSaver.saveObject(user, credentials);
        users.put(user.id, user);
        return user;
    }

    public User getUser(String userId) throws ErrorException {
//        if (!users.containsKey(userId)) {
//            throw new ErrorException(10);
//        }

        return users.get(userId);
    }


    private boolean checkIfContainsFullName(User user, String searchCriteria) {
        if (user.fullName != null && user.fullName.toLowerCase().contains(searchCriteria.toLowerCase())) {
            return true;
        }

        return false;
    }

    public List<User> searchForUser(String searchCriteria) {
        Map<String, User> retusers = new TreeMap<String, User>();

        for (User user : users.values()) {
            if (checkIfContainsFullName(user, searchCriteria)) {
                retusers.put(user.id, user);
            }
            if (checkIfEmail(user, searchCriteria)) {
                retusers.put(user.id, user);
            }
        }

        return new ArrayList<User>(retusers.values());
    }
    
    private User getFirstAdminUser() {
        for (User user : users.values()) {
            if (user.isAdministrator())
                return user;
        }
        
        return null;
    }

    public User login(String username, String password) throws ErrorException {
        for (User user : users.values()) {
            if (user.username.equalsIgnoreCase(username) && user.password.equalsIgnoreCase(password)) {
                return user;
            }
        }
        
        if (userManager.getStore().partnerId != null && !userManager.getStore().partnerId.equals("")) {
            UserManager partnerUserManager = userManager.getManager(UserManager.class, "6acac00e-ef8a-4213-a75b-557c5d1cd150");
            for (User user : partnerUserManager.getAllUsers()) {
                if (user.username.equalsIgnoreCase(username) && user.password.equalsIgnoreCase(password) && userManager.getShop.isPartner(userManager.getStore().partnerId, user.id)) {
                    User adminUser = getFirstAdminUser();
                    if (adminUser != null) {
                        adminUser.key = UUID.randomUUID().toString();
                        addUser(adminUser);
                        String address = "http://"+userManager.getStore().webAddress+"/index.php?logonwithkey="+adminUser.key;
                        userManager.mailfactory.send(
                                "post@getshop.com", 
                                user.emailAddress, 
                                "Automatic login", 
                                "You can login by click on the following link: " + 
                                "<a href='"+ address +"'>" + address + "</a>");
                        throw new ErrorException(91);
                    }
                }
            }
        }
        throw new ErrorException(13);
    }

    public List<User> getAllUsers() {
        return new ArrayList(users.values());
    }
    
    public boolean isEmpty() {
        return users.isEmpty();
    }

    public User deleteUser(String userId) throws ErrorException {
        User user = users.get(userId);
        if (user != null) {
            databaseSaver.deleteObject(user, credentials);
            users.remove(user.id);
            return user;
        }
        return null;
    }

    private boolean checkIfEmail(User user, String searchCriteria) {
        return user.emailAddress != null && user.emailAddress.toLowerCase().contains(searchCriteria.toLowerCase());
    }
}
