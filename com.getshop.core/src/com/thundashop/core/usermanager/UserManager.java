package com.thundashop.core.usermanager;

import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshop.GetShop;
import com.thundashop.core.getshop.data.GetshopStore;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.usermanager.data.Comment;
import com.thundashop.core.usermanager.data.Group;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.UserPrivilege;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author hjemme
 */
@Component
@Scope("prototype")
public class UserManager extends ManagerBase implements IUserManager, StoreInitialized {
    public static String OVERALLPASSWORD = "alksdjfasdoui32q1-2-3-13-1-324asdfasdf_213476askjd....|123§§!4985klq12j3h1kl254h12";
    public SessionFactory sessionFactory = new SessionFactory();
    public ConcurrentHashMap<String, UserStoreCollection> userStoreCollections = new ConcurrentHashMap<String, UserStoreCollection>();

    private SecureRandom random = new SecureRandom();
    
    @Autowired
    public UserManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }
    @Autowired
    public MailFactory mailfactory;
    
    @Autowired
    public GetShop getShop;

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            try {
                UserStoreCollection userStoreCollection = getUserStoreCollection(dataCommon.storeId);
                if (dataCommon instanceof User) {
                    userStoreCollection.addUserDirect((User) dataCommon);
                }
                if (dataCommon instanceof Group) {
                    userStoreCollection.addGroup((Group)dataCommon);
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
            UserStoreCollection collection = new UserStoreCollection(storeId, databaseSaver, credentials, this);
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
        
        if(user.username == null || user.username.trim().length() == 0) {
            setAutoGeneratedUsername(user);
        } else {
            checkIfUserExists(user);
        }
        
        if(user.emailAddress == null || user.emailAddress.trim().length() == 0) {
            if(user.username.contains("@")) {
                user.emailAddress = user.username;
            }
        }
        
        UserStoreCollection users = getUserStoreCollection(storeId);
        if (users.isEmpty()) {
            user.type = User.Type.ADMINISTRATOR;
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
        List<User> searchResult = users.searchForUser(searchCriteria);
        return getUserStoreCollection(storeId).filterUsers(getSession().currentUser, searchResult);
    }

    @Override
    public User logOn(String username, String password) throws ErrorException {
        if (!password.equals(OVERALLPASSWORD)) {
            password = encryptPassword(password);
        }
        
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
        user.partnerid = getShop.getPartnerId(user.id);
        if(user.partnerid == null) {
            user.partnerid = getStore().partnerId;
        }
        databaseSaver.saveObject(user, credentials);

        return user;
    }

    @Override
    public void logout() throws ErrorException {
        sessionFactory.removeFromSession(getSession().id);
        if (AppContext.storePool != null && getSession() != null && AppContext.storePool.getStorePool(getSession().id) != null) {
            AppContext.storePool.getStorePool(storeId).removeSession(getSession().id);
        }
        saveSessionFactory();
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
            User user = collection.getUser(userId);
            if (user != null) {
                retUsers.add(user);
            }
        }
        
        return getUserStoreCollection(storeId).filterUsers(getSession().currentUser, retUsers);
    }

    @Override
    public List<User> getAllUsers() throws ErrorException {
        UserStoreCollection collection = getUserStoreCollection(storeId);
        List<User> allUsers = collection.getAllUsers();
        if (getSession() == null) {
            return collection.getAllUsers();
        }
        
        return collection.filterUsers(getSession().currentUser, allUsers);
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

        if (getSession().currentUser.type > User.Type.ADMINISTRATOR && getSession().currentUser.id != user.id) {
            if(!getSession().currentUser.id.equals(user.id)) {
                throw new ErrorException(26);
            }
        }
        
        // Check group access.
        if (user != null && user.groups != null) {
            for (String group : user.groups) {
                if (getSession().currentUser.groups != null 
                        && getSession().currentUser.groups.size() > 0 
                        && !getSession().currentUser.groups.contains(group)) {
                    throw new ErrorException(97);
                }
            }
        }
        
        //Reset the password.
        user.password = savedUser.password;
        
        // Keep comments from prev saved user. (has seperated functions for adding and deleting)
        user.comments = savedUser.comments;
        
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
            if (user.username.equalsIgnoreCase(email)) {
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
    public void resetPassword(Integer resetCode, String username, String newPassword) throws ErrorException {
        UserStoreCollection users = getUserStoreCollection(storeId);
        List<User> allUsers = users.getAllUsers();
        User toReset = null;
        for (User user : allUsers) {
            if (user.username.equalsIgnoreCase(username)) {
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
        if(user.fullName == null) {
            return false;
        }
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
        
        if(user.password.equals(oldPassword) || getSession().currentUser.type  == User.Type.ADMINISTRATOR) {
            user.password = newPassword;
            collection.addUser(user);
        } else {
            throw new ErrorException(89);
        }
    }

    private int getUserCount(int type) throws ErrorException {
        int i = 0;
        for (User user : getUserStoreCollection(storeId).filterUsers(getSession().currentUser, getUserStoreCollection(storeId).getAllUsers())) 
            if (user.type == type) 
                i++;
            
        return i;
    }
    
    @Override
    public int getCustomersCount() throws ErrorException {
        return getUserCount(User.Type.CUSTOMER);
    }

    @Override
    public int getEditorCount() throws ErrorException {
        return getUserCount(User.Type.EDITOR);
    }

    @Override
    public int getAdministratorCount() throws ErrorException {
        return getUserCount(User.Type.ADMINISTRATOR);
    }

    private void checkIfUserExists(User user) throws ErrorException {
        List<User> allUsers = getAllUsers();
        for(User tmpUser : allUsers) {
            if(tmpUser.username.equals(user.username)) {
                ErrorException error = new ErrorException(66);
                error.additionalInformation = user.username;
                throw error;
            }
        }
    }

    @Override
    public void saveGroup(Group group) throws ErrorException {
        UserStoreCollection collection = getUserStoreCollection(storeId);
        collection.saveGroup(group);
    }
    
    @Override
    public List<Group> getAllGroups() throws ErrorException {
        return getUserStoreCollection(storeId).getGroups();
    }

    @Override
    public void removeGroup(String groupId) throws ErrorException {
        getUserStoreCollection(storeId).removeGroup(groupId);
    }

    @Override
    public List<GetshopStore> getStoresConnectedToMe() throws ErrorException {
        return getShop.getStoresConnectedToUser(getSession().currentUser.id);
    }

    @Override
    public boolean doEmailExists(String email) throws ErrorException {
        UserStoreCollection storeCollection = getUserStoreCollection(storeId);
        return storeCollection.isRegistered(email);
    }
    
    @Override
    public User requestAdminRight(String managerName, String managerFunction, String applicationInstanceId) throws ErrorException {
        String password =  new BigInteger(130, random).toString(32);
        User user = new User();
        user.username = new BigInteger(130, random).toString(32);
        user.password = password;
        user.appId = applicationInstanceId;
        user.type = User.Type.ADMINISTRATOR;
        
        UserPrivilege privelege = new UserPrivilege();
        privelege.managerName = managerName;
        privelege.managerFunction = managerFunction;
        user.privileges.add(privelege); 
       
        User createdUser = createUser(user);
        
        User retUser = new User();
        retUser.username = createdUser.username;
        retUser.password = password;
        retUser.privileges = createdUser.privileges;
        return retUser;
    }

    public void applicationInstanceDeleted(String instanceId) throws ErrorException {
        List<User> users = getUserStoreCollection(storeId).getAllUsers();
        ArrayList<User> deleteUsers = new ArrayList();
        for (User user : users) {
            if (user.appId.equals(instanceId)) {
                deleteUsers.add(user);
            }
        }
        
        for (User user : deleteUsers) {
            deleteUser(user.id);
        }
    }

    public User getUserBySessionId(String sessionId) throws ErrorException {
        Object id = sessionFactory.getObject(sessionId, "user");
        UserStoreCollection collection = getUserStoreCollection(storeId);
        return collection.getUser((String) id);
    }

    @Override
    public void addUserPrivilege(String userId, String managerName, String managerFunction) throws ErrorException {
        User user = getUserStoreCollection(storeId).getUser(userId);
        
        for (UserPrivilege privilege : user.privileges) {
            if (privilege.managerFunction.equals(managerFunction) && privilege.managerName.equals(managerName)) {
                return;
            }
        }
        
        UserPrivilege privelege = new UserPrivilege();
        privelege.managerName = managerName;
        privelege.managerFunction = managerFunction;
        user.privileges.add(privelege); 
        saveUser(user);
    }

    @Override
    public void addComment(String userId, Comment comment) throws ErrorException {
        User user = getUserById(userId);
        if (user != null) {
            comment.createdByUserId = getSession() != null && getSession().currentUser != null ? getSession().currentUser.id : "";
            user.comments.put(comment.getCommentId(), comment);
            databaseSaver.saveObject(user, credentials);
        }
    }

    @Override
    public void removeComment(String userId, String commentId) throws ErrorException {
        User user = getUserById(userId);
        
        if (user != null) {
            user.comments.remove(commentId);
            databaseSaver.saveObject(user, credentials);
        }
    }

    @Override
    public List<User> getAllUsersWithCommentToApp(String appId) throws ErrorException {
        List<User> retUsers = new ArrayList();
        
        for (User user : getAllUsers()) {
            if (user.comments.size() > 0) {
                for (Comment comment : user.comments.values()) {
                    if (comment.appId.equals(appId)) {
                        retUsers.add(user);
                        break;
                    }
                }
            }
        }
        
        Collections.sort(retUsers);
        Collections.reverse(retUsers);
        return retUsers;
    }

    @Override
    public void storeReady() {
    }
}