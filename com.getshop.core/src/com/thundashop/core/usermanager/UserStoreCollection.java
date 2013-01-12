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
    private HashMap<String, User> users = new HashMap<String, User>();

    public UserStoreCollection(String storeId, DatabaseSaver databaseSaver, Credentials credentials) {
        this.storeId = storeId;
        this.databaseSaver = databaseSaver;
        this.credentials = credentials;
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
        if (user.fullName != null && user.fullName.contains(searchCriteria)) {
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
        }

        return new ArrayList<User>(retusers.values());
    }

    public User login(String username, String password) throws ErrorException {
        for (User user : users.values()) {
            if (user.username.equalsIgnoreCase(username) && user.password.equalsIgnoreCase(password)) {
                return user;
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
}
