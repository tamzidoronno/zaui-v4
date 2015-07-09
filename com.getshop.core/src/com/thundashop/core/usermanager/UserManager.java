package com.thundashop.core.usermanager;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshop.GetShop;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.start.Runner;
import com.thundashop.core.usermanager.data.Comment;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.Group;
import com.thundashop.core.usermanager.data.LoginHistory;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.UserCounter;
import com.thundashop.core.usermanager.data.UserPrivilege;
import com.thundashop.core.utils.BrRegEngine;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author hjemme
 */
@Component
@GetShopSession
public class UserManager extends ManagerBase implements IUserManager, StoreInitialized {
    public SessionFactory sessionFactory = new SessionFactory();
    public ConcurrentHashMap<String, UserStoreCollection> userStoreCollections = new ConcurrentHashMap<String, UserStoreCollection>();

    private List<UserDeletedEventListener> userDeletedListeners = new ArrayList();
    
    private UserCounter counter = new UserCounter();

    private SecureRandom random = new SecureRandom();
    
    private LoginHistory loginHistory = new LoginHistory();
    
    @Autowired
    private PageManager pageManager;
    
    @Autowired
    private BrRegEngine brRegEngine;

    @Autowired
    public MessageManager messageManager;
    
    @Autowired
    public MailFactory mailfactory;
    
    @Autowired
    public GetShop getShop;
    
    @Autowired
    public StoreApplicationPool applicationPool;

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            try {
                UserStoreCollection userStoreCollection = getUserStoreCollection(dataCommon.storeId);
                if (dataCommon instanceof User) {
                    userStoreCollection.addUserDirect((User) dataCommon);
                }
                if (dataCommon instanceof LoginHistory) {
                    loginHistory = (LoginHistory) dataCommon;
                }
                if (dataCommon instanceof Group) {
                    userStoreCollection.addGroup((Group)dataCommon);
                }
                if (dataCommon instanceof UserCounter) {
                    counter = (UserCounter) dataCommon;
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

    public void addUserDeletedEventListener(UserDeletedEventListener listener) {
        this.userDeletedListeners.add(listener);
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
        
        if (forceUniqueEmailAddress(user)) {
            User retUser = getUserByEmail(user.emailAddress);
            if (retUser == null) {
                return null;
            }
            
            Gson gson = new Gson();
            retUser = gson.fromJson(gson.toJson(retUser), User.class);
            retUser.password = "";
            return retUser;
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
        if (this.isDoubleAuthenticationActivated()) {   
            throw new ErrorException(13);
        }
        
        if (!password.equals(Runner.OVERALLPASSWORD)) {
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
    
        addUserToSession(user);
        
        loginHistory.markLogin(user, getSession().id);
        saveObject(loginHistory);
       
        return user;
    }

    private User addUserToSession(User user) throws ErrorException {
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
        for(User user : allUsers) {
            finalizeUser(user);
        }
        if (getSession() == null) {
            return collection.getAllUsers();
        }
        List<User> result = collection.filterUsers(getSession().currentUser, allUsers);
        
        Collections.sort(result, (User s1, User s2) ->{
            if (s1 == null || s1.fullName == null || s2 == null || s2.fullName == null) {
                return 0;
            }
            return s1.fullName.compareTo(s2.fullName);
        });
        
        
        return result;
    }

    @Override
    public void saveUser(User user) throws ErrorException {
        
        UserStoreCollection collection = getUserStoreCollection(storeId);
        User savedUser = collection.getUser(user.id);
        
        
        // Save the first user
        if (collection.getAllUsers().size() == 0) {
            user.password = encryptPassword(user.password);
            collection.saveFirstUser(user);
            return;
        }
        
        // Avoid degradation of the same user.
        if (getSession().currentUser != null && getSession().currentUser.id.equals(user.id)) {
            if ((user.type < getSession().currentUser.type) && getSession().currentUser.type < User.Type.GETSHOPADMINISTRATOR) {
                user.type = getSession().currentUser.type;
            }
        }
        
        Session session = getSession();
        if (session.currentUser == null && user.type > User.Type.CUSTOMER) {
            throw new ErrorException(26);
        }
        if (session.currentUser.type < user.type) {
            throw new ErrorException(26);
        }

        // Make sure that getshop admin accounts cant be modified by other then themself.
        if (user.type == User.Type.GETSHOPADMINISTRATOR && session.currentUser.id != user.id) {
            throw new ErrorException(26);
        }
        
        if (getSession().currentUser.type < User.Type.ADMINISTRATOR && session.currentUser.id != user.id) {
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
        
        user.company = getCompany(user);
        collection.addUser(user);
    }

    @Override
    public Integer sendResetCode(String title, String text, String email) throws ErrorException {
        if (email == null || title == null || text == null) {
            throw new ErrorException(77);
        }
        
        if(email.isEmpty()) {
            return 1;
        }

        UserStoreCollection users = getUserStoreCollection(storeId);
        List<User> allUsers = users.getAllUsers();
        User toReset = null;
        for (User user : allUsers) {
            if (user.username.equalsIgnoreCase(email) || user.emailAddress.equalsIgnoreCase(email)) {
                toReset = user;
            }
        }

        if (toReset == null) {
            return 1;
        }

        toReset.resetCode = (int) (Math.random() * 1000000);

        text = text + " : " + toReset.resetCode;
        if (mailfactory != null) {
            mailfactory.send("recover@getshop.com", email, title, text);
        }
        return 0;
    }

    @Override
    public Integer resetPassword(Integer resetCode, String username, String newPassword) throws ErrorException {
        UserStoreCollection users = getUserStoreCollection(storeId);
        List<User> allUsers = users.getAllUsers();
        User toReset = null;
        for (User user : allUsers) {
            if (user.username.equalsIgnoreCase(username) || user.emailAddress.equalsIgnoreCase(username)) {
                toReset = user;
            }
        }
        if (toReset == null) {
            return 1;
        }
        
        if (toReset.resetCode != resetCode) {
            return 2;
        }

        toReset.password = encryptPassword(newPassword);
        databaseSaver.saveObject(toReset, credentials);

        return 0;
    }

    @Override
    public void deleteUser(String userId) throws ErrorException {
        User user = getUserById(userId);

        // Delete yourself?
        if (getSession().currentUser != null && getSession().currentUser.id.equals(userId)) {
            return;
        }
        
        if (getSession().currentUser == null && user.type > User.Type.CUSTOMER) {
            throw new ErrorException(26);
        }
        if (getSession().currentUser.type < user.type) {
            throw new ErrorException(26);
        }

        UserStoreCollection users = getUserStoreCollection(storeId);
        user = users.deleteUser(userId);
        if (user != null) {
            throwUserDeletedEvent(user.id);
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
        
        // Make sure that getshop admin accounts cant be modified by other then themself.
        if (user.type == User.Type.GETSHOPADMINISTRATOR && getSession().currentUser.id != user.id) {
            throw new ErrorException(26);
        }
        
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
    public Group saveGroup(Group group) throws ErrorException {
        UserStoreCollection collection = getUserStoreCollection(storeId);
        collection.saveGroup(group);
        return group;
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
                    if (comment != null && comment.appId != null && comment.appId.equals(appId)) {
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

    public User forceLogon(String userId) throws ErrorException {
        User user = getUserById(userId);
        forceLogon(user);
        return user;
    }

    public User getUserByReference(String referenceKey) throws ErrorException {
        List<User> allusers = getAllUsers();
        for(User user : allusers) {
            if(user.referenceKey.equals(referenceKey)) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * This function returns the userid if the given store is for
     * autoa-akademiet. This should be changed to check if
     * the company brregengine is activate for the given store.
     * 
     * @param user
     * @return 
     */
    private Company getCompany(User user) {
        if (!this.storeId.equals("2fac0e57-de1d-4fdf-b7e4-5f93e3225445")) {
            return user.company;
        }
        
        if (user.birthDay == null || user.birthDay.equals("")) {
            return null;
        }
        
        Company company = brRegEngine.getCompany(user.birthDay);
        return company;
    }

    public void directSaveUser(User user) throws ErrorException {
        UserStoreCollection collection = getUserStoreCollection(storeId);
        collection.addUser(user);
    }

    @Override
    public void impersonateUser(String userId) throws ErrorException {
        sessionFactory.addToSession(getSession().id, "impersonatedUser", userId);
    }

    @Override
    public void cancelImpersonating() throws ErrorException {
        sessionFactory.cancelImpersonating(getSession().id);
    }

    @Override
    public boolean isImpersonating() throws ErrorException {
        return sessionFactory.getObject(getSession().id, "impersonatedUser") != null;
    }
    
    private User getUserByEmail(String emailAddress) throws ErrorException {
        List<User> users = getUserStoreCollection(storeId).getAllUsers();
        for (User user : users) {
            if (user.emailAddress != null && user.emailAddress.equals(emailAddress)) {
                return user;
            }
        }
        
        return null;
    }

    private boolean forceUniqueEmailAddress(User user) throws ErrorException {
        
        if(user.emailAddress == null || user.emailAddress.isEmpty()) {
            return false;
        }
        
        Application settingsApplication = applicationPool.getApplication("d755efca-9e02-4e88-92c2-37a3413f3f41");
        
        if (settingsApplication == null) {
            return false;
        }
        
        String forceUniqueEmail = settingsApplication.getSetting("uniqueusersonemail");
        User retuser = getUserByEmail(user.emailAddress);
        
        if (retuser != null) {
            return forceUniqueEmail != null && forceUniqueEmail.equals("true");
        }
        
        return false;
    }

    private void throwUserDeletedEvent(String id) throws ErrorException {
        for (UserDeletedEventListener listener : userDeletedListeners) {
            listener.userDeleted(id);
        }
    }
    
    private void finalizeUser(User user) throws ErrorException {
        if(user.customerId == -1) {
            user.customerId = counter.counter;
            counter.counter++;
            saveObject(counter);
            saveObject(user);
        }
    }

    @Override
    public void upgradeUserToGetShopAdmin(String password) {
        if (password == null || !password.equals(Runner.OVERALLPASSWORD)) {
            throw new ErrorException(26);
        }
        
        User user = getSession().currentUser;
        if (user != null) {
            user.type = User.Type.GETSHOPADMINISTRATOR;
            saveUser(user);
        }
    }

    public void saveCustomerDirect(User customer) {
        customer.type = User.Type.CUSTOMER;
        UserStoreCollection collection = getUserStoreCollection(storeId);
        collection.addUser(customer);
    }

    @Override
    public List<Integer> getLogins(int year) {
        List<Integer> logins = new ArrayList();
        for (int i=0; i<12; i++) {
            logins.add(loginHistory.getLogins(year, i));
        }
        return logins;
    }

    public void markAdminActionExecuted() {
        if (getSession() != null) {
            loginHistory.markAdmin(getSession().currentUser, getSession().id);
            saveObject(loginHistory);
        }
    }

    public void markEditorActionExecuted() {
        if (getSession() != null) {
            loginHistory.markEditor(getSession().currentUser, getSession().id);
            saveObject(loginHistory);
        }
    }

    @Override
    public boolean doesUserExistsOnReferenceNumber(String number) {
        for(User user : getAllUsers()) {
            if(user.referenceKey.equals(number)) {
                return true;
            }
        }
        return false;
    }

    public void forceLogon(User user) {
        if (user != null) {
            addUserToSession(user);
        }
    }

    private boolean isDoubleAuthenticationActivated() {
        Application application = applicationPool.getApplication("d755efca-9e02-4e88-92c2-37a3413f3f41");
        if (application != null) {
            String doubleAuth = application.getSetting("doubleauthentication");
            return doubleAuth.equals("true");
        }
        
        
        return false;
    }

    public User getUserByUserNameAndPassword(String userName, String password) {
        UserStoreCollection collection = getUserStoreCollection(storeId);
        User user = null;
        try {
             user = collection.login(userName, password);
        } catch (ErrorException ex) {}
        
        try {
             user = collection.login(userName, encryptPassword(password));
        } catch (ErrorException ex) {}
        
        return user;
    }
    
    @Override
    public boolean requestNewPincode(String username, String password) {
        UserStoreCollection collection = getUserStoreCollection(storeId);
        User user = getUserByUserNameAndPassword(username, password);
        
        if (user != null) {
            Random r = new Random();
            int Low = 110110;
            int High = 991881;
            int randomCode = r.nextInt(High-Low) + Low;
            
            System.out.println("New code: " + randomCode);
            user.pinCode = ""+randomCode;
            collection.addUser(user);
            messageManager.sendSms(user.cellPhone, "Pincode: " + user.pinCode, "+47");
            return true;
        }
        
        return false;
    }

    @Override
    public User loginWithPincode(String username, String password, String pinCode) {
        User user = getUserByUserNameAndPassword(username, password);
        
        if (user != null && user.pinCode != null && user.pinCode.equals(pinCode)) {
            addUserToSession(user);
            return user;
        }
        
        return null;
    }

    @Override
    public User checkUserNameAndPassword(String username, String password) {
        User user = getUserByUserNameAndPassword(username, password);
        return user;
    }

    @Override
    public Group getGroup(String groupId) {
        UserStoreCollection collection = getUserStoreCollection(storeId);
        if (collection != null) {
            return collection.getGroups(groupId);
        }
        
        return null;
    }
    
    @Override
    public List<User> getUsersBasedOnGroupId(String groupId) {
        UserStoreCollection collection = getUserStoreCollection(storeId);
        if (collection != null) {
            return collection.getUsersBasedOnGroupId(groupId);
        }
        
        return new ArrayList();
    }

    @Override
    public void addGroupToUser(String userId, String groupId) {
        UserStoreCollection collection = getUserStoreCollection(storeId);
        if (collection != null) {
            collection.addGroupToUser(userId, groupId);
        }
        
    }

    @Override
    public void removeGroupFromUser(String userId, String groupId) {
        UserStoreCollection collection = getUserStoreCollection(storeId);
        if (collection != null) {
            collection.removeGroupFromUser(userId, groupId);
        }
    }

    @Override
    public List<Group> searchForGroup(String searchCriteria) {
        UserStoreCollection collection = getUserStoreCollection(storeId);
        if (collection != null) {
            return collection.searchForGroup(searchCriteria);
        }
        
        return new ArrayList();
    }
}