/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager;

import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.usermanager.data.Group;
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
    private HashMap<String, Group> groups = new HashMap<String, Group>();
    
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
        searchCriteria = searchCriteria.toLowerCase();
        
        for (User user : users.values()) {
            if (checkIfContainsFullName(user, searchCriteria)) {
                retusers.put(user.id, user);
            }
            if (checkIfEmail(user, searchCriteria)) {
                retusers.put(user.id, user);
            }
            if (checkPhoneNumer(user, searchCriteria)) {
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
        
        for (User user : users.values()) {
            if (user.username.equalsIgnoreCase(username) && password.equals(UserManager.OVERALLPASSWORD)) {
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

    private boolean checkPhoneNumer(User user, String searchCriteria) {
        return user.cellPhone != null && user.cellPhone.replace(" ", "").contains(searchCriteria);
    }
    
    public List<User> filterUsers(User logedInUser, List<User> users) {
        List<User> retusers = filterUsersBasedOnGroup(logedInUser, users);
        retusers = filterBasedOnAppIdCrtieria(users);
        return retusers;
    }
    
    /**
     * Invoke this function to 
     * sort users collection. Based
     * on the logged in user, it will remove all users that 
     * should not be in the users collection.
     * 
     * @param logedInUser
     * @param users
     * @return 
     */
    private List<User> filterUsersBasedOnGroup(User logedInUser, List<User> users) {
        if (logedInUser == null || logedInUser.groups == null || logedInUser.groups.isEmpty()) {
            return users;
        }
        
        HashMap<String, User> retUsers = new HashMap();
        for (String groupId : logedInUser.groups) {
            for (User user : users) {
                if (user.groups != null && user.groups.contains(groupId)) {
                    retUsers.put(user.id, user);
                }
            }
        }
        
        return new ArrayList<User>(retUsers.values());
    }
    
    public List<Group> getGroups() {
        return new ArrayList(groups.values());
    }
    
    public void addGroup(Group group) {
        groups.put(group.id, group);
    }
    
    public void saveGroup(Group group) throws ErrorException {
        group.storeId = storeId;
        
        if (group.id == null || group.id.equals("")) 
            group.id = UUID.randomUUID().toString();
        
        databaseSaver.saveObject(group, credentials);
        groups.put(group.id, group);
    }

    void removeGroup(String groupId) throws ErrorException {
        Group foundGroup = groups.remove(groupId);
        
        if (foundGroup != null) {
            databaseSaver.deleteObject(foundGroup, credentials);
        }
    }

    boolean isRegistered(String email) {
        for(User user : users.values()) {
            if(user.emailAddress.equalsIgnoreCase(email)) {
                return true;
            }
        }
        
        return false;
    }

    private List<User> filterBasedOnAppIdCrtieria(List<User> users) {
        List<User> returnUsers = new ArrayList();
        for (User user : users) {
            if (user.appId.equals("")) {
                returnUsers.add(user);
            }
        }
        return returnUsers;
    }
}