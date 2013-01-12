/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager;

import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.usermanager.data.User;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author hjemme
 */
@Component
@Scope("prototype")
public class UserManager extends ManagerBase implements IUserManager {

    public SessionFactory sessionFactory = new SessionFactory();
    public ConcurrentHashMap<String, UserStoreCollection> userStoreCollections = new ConcurrentHashMap<String, UserStoreCollection>();

    @Autowired
    public UserManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }
    @Autowired
    public MailFactory mailfactory;

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            try {
                UserStoreCollection userStoreCollection = getUserStoreCollection(dataCommon.storeId);
                if (dataCommon instanceof User) {
                    userStoreCollection.addUserDirect((User) dataCommon);
                }
                if (dataCommon instanceof SessionFactory) {
                    sessionFactory = (SessionFactory) dataCommon;
                }
            } catch (ErrorException ex) {
                ex.printStackTrace();
                // Should never ever happend.
            }
        }
    }

    private UserStoreCollection getUserStoreCollection(String storeId) throws ErrorException {
        if (storeId == null) {
            throw new ErrorException(64);
        }

        if (!userStoreCollections.containsKey(storeId)) {
            UserStoreCollection collection = new UserStoreCollection(storeId, databaseSaver, credentials);
            userStoreCollections.put(storeId, collection);
        }

        return userStoreCollections.get(storeId);
    }

    private void setAutoGeneratedUsername(User user) throws ErrorException {
        if (user.username == null || user.username.trim().length() == 0) {
            int i = 10000;
            boolean exists = true;
            while (exists) {
                i++;
                List<User> users = getAllUsers();
                user.username = "" + (users.size() + i);
                exists = false;
                for (User usr : users) {
                    if (usr.username.equals(user.username)) {
                        exists = true;
                    }
                }
            }
        }
    }
    
    @Override
    public User createUser(User user) throws ErrorException {

        if (getSession().currentUser == null && user.type > User.Type.CUSTOMER) {
            throw new ErrorException(26);
        }
        if (getSession().currentUser != null && getSession().currentUser.type < user.type) {
            throw new ErrorException(26);
        } 
        
        setAutoGeneratedUsername(user);
        
        UserStoreCollection users = getUserStoreCollection(storeId);
        if (users.isEmpty()) {
            user.type = User.Type.ADMINISTRATOR;
            user.key = "firstlogon";
        }
        users.addUser(user);
        user.password = encryptPassword(user.password);
        
        databaseSaver.saveObject(user, credentials);
        
        throwEvent(Events.USER_CREATED, user.id);
        
        return user;
    }

    @Override
    public List<User> findUsers(String searchCriteria) throws ErrorException {
        UserStoreCollection users = getUserStoreCollection(storeId);
        return users.searchForUser(searchCriteria);
    }

    @Override
    public User logOn(String username, String password) throws ErrorException {
        password = encryptPassword(password);
        return logonEncrypted(username, password);
    }
    
    private User logonEncrypted(String username, String password) throws ErrorException {
        UserStoreCollection collection = getUserStoreCollection(storeId);
        User user = collection.login(username, password);

        //Check if the user has expired.
        if (user.expireDate != null && new Date().after(user.expireDate)) {
            throw new ErrorException(80);
        }

        sessionFactory.addToSession(getSession().id, "user", user.id);
        saveSessionFactory();

        user.lastLoggedIn = new Date();
        user.loggedInCounter++;
        databaseSaver.saveObject(user, credentials);

        return user;
    }

    @Override
    public void logout() throws ErrorException {
        sessionFactory.removeFromSession(getSession().id);
    }

    @Override
    public boolean isLoggedIn() throws ErrorException {
        User user = getSession().currentUser;
        if (user != null) {
            return true;
        }
        return false;
    }

    @Override
    public List<User> getUserList(ArrayList<String> userIds) throws ErrorException {
        UserStoreCollection collection = getUserStoreCollection(storeId);
        List<User> retUsers = new ArrayList();
        for (String userId : userIds) {
            retUsers.add(collection.getUser(userId));
        }
        return retUsers;
    }

    @Override
    public List<User> getAllUsers() throws ErrorException {
        UserStoreCollection collection = getUserStoreCollection(storeId);
        return collection.getAllUsers();
    }

    @Override
    public void saveUser(User user) throws ErrorException {
        
        UserStoreCollection collection = getUserStoreCollection(storeId);
        User savedUser = collection.getUser(user.id);
        
        if (getSession().currentUser == null && user.type > User.Type.CUSTOMER) {
            throw new ErrorException(26);
        }
        if (getSession().currentUser.type < user.type) {
            throw new ErrorException(26);
        }

        if (getSession().currentUser.type == user.type) {
            if(!getSession().currentUser.id.equals(user.id)) {
                ErrorException ex = new ErrorException(26);
                ex.additionalInformation = "A user with the same user rights cannot modify each other.";
                throw ex;
            }
        }
        
        //Reset the password.
        user.password = savedUser.password;
        
        collection.addUser(user);
    }

    @Override
    public void sendResetCode(String title, String text, String email) throws ErrorException {
        if (email == null || title == null || text == null) {
            throw new ErrorException(77);
        }

        UserStoreCollection users = getUserStoreCollection(storeId);
        List<User> allUsers = users.getAllUsers();
        User toReset = null;
        for (User user : allUsers) {
            if (user.emailAddress.equalsIgnoreCase(email)) {
                toReset = user;
            }
        }

        if (toReset == null) {
            throw new ErrorException(76);
        }

        toReset.resetCode = (int) (Math.random() * 1000000);

        text = text + " : " + toReset.resetCode;
        if (mailfactory != null) {
            mailfactory.send("recover@getshop.com", email, title, text);
        }
    }

    @Override
    public void resetPassword(Integer resetCode, String email, String newPassword) throws ErrorException {
        UserStoreCollection users = getUserStoreCollection(storeId);
        List<User> allUsers = users.getAllUsers();
        User toReset = null;
        for (User user : allUsers) {
            if (user.emailAddress.equalsIgnoreCase(email)) {
                toReset = user;
            }
        }
        if (toReset == null) {
            throw new ErrorException(76);
        } else {
            if (toReset.resetCode != resetCode) {
                throw new ErrorException(78);
            }

            toReset.password = encryptPassword(newPassword);
            databaseSaver.saveObject(toReset, credentials);
        }
    }

    @Override
    public void deleteUser(String userId) throws ErrorException {
        User user = getUserById(userId);

        if (getSession().currentUser == null && user.type > User.Type.CUSTOMER) {
            throw new ErrorException(26);
        }
        if (getSession().currentUser.type < user.type) {
            throw new ErrorException(26);
        }

        UserStoreCollection users = getUserStoreCollection(storeId);
        user = users.deleteUser(userId);
        if (user != null) {
            throwEvent(Events.USER_DELETED, user.id);
        }
    }

    @Override
    public User getLoggedOnUser() throws ErrorException {
        Object id = sessionFactory.getObject(getSession().id, "user");
        UserStoreCollection collection = getUserStoreCollection(storeId);
        return collection.getUser((String) id);
    }

    private void saveSessionFactory() throws ErrorException {
        sessionFactory.storeId = storeId;
        databaseSaver.saveObject(sessionFactory, credentials);
    }

    @Override
    public User getUserById(String id) throws ErrorException {
        UserStoreCollection storeCollection = getUserStoreCollection(storeId);
        return storeCollection.getUser(id);
    }

    @Override
    public boolean isCaptain(String id) throws ErrorException {
        User user = getUserById(id);
        return user.fullName.equalsIgnoreCase("Jean-Luc Picard");
    }

    @Override
    public User logonUsingKey(String logonKey) throws ErrorException {
        User user = getUserByKey(logonKey);

        if (user == null) {
            throw new ErrorException(26);
        }
        logonEncrypted(user.username, user.password);

        user.key = null;
        databaseSaver.saveObject(user, credentials);
        return user;
    }

    private User getUserByKey(String logonKey) throws ErrorException {
        if (logonKey == null || logonKey.trim().length() == 0) {
            ErrorException error = new ErrorException(26);
            error.additionalInformation = "The key is empty!";
            throw error;
        }

        UserStoreCollection storeCollection = getUserStoreCollection(storeId);
        List<User> allUsers = storeCollection.getAllUsers();
        for (User user : allUsers) {
            if (user.hasKey(logonKey)) {
                return user;
            }
        }

        return null;
    }

    private String encryptPassword(String password) throws ErrorException {
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

    @Override
    public void updatePassword(String userId, String oldPassword, String newPassword) throws ErrorException {
        oldPassword = encryptPassword(oldPassword);
        newPassword = encryptPassword(newPassword);
        
        UserStoreCollection collection = getUserStoreCollection(storeId);
        User user = collection.getUser(userId);
        
        if(user.password.equals(oldPassword) || getSession().currentUser.type > user.type) {
            user.password = newPassword;
            collection.addUser(user);
        } else {
            throw new ErrorException(89);
        }
    }
}
