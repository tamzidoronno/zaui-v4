package com.thundashop.core.usermanager;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.BigDecimalConverter;
import com.thundashop.core.databasemanager.Database;
import static com.thundashop.core.databasemanager.Database.mongoPort;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshop.GetShop;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.messagemanager.SmsHandlerAbstract;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.pagemanager.GetShopModules;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.GetShopModule;
import com.thundashop.core.start.Runner;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.Comment;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.Group;
import com.thundashop.core.usermanager.data.LoginHistory;
import com.thundashop.core.usermanager.data.LoginToken;
import com.thundashop.core.usermanager.data.SimpleUser;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.UserCard;
import com.thundashop.core.usermanager.data.UserCounter;
import com.thundashop.core.usermanager.data.UserPrivilege;
import com.thundashop.core.usermanager.data.UserRole;
import com.thundashop.core.utils.UtilManager;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.mongodb.morphia.Morphia;
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
    
    private HashMap<String, Company> companies = new HashMap();
    
    private LoginHistory loginHistory = new LoginHistory();
    
    private Map<String, UserRole> roles = new HashMap();
    
    private Map<String, LoginToken> tokens = new HashMap();
    
    private GetShopModules modules = new GetShopModules();
    
    @Autowired
    private PageManager pageManager;
    
    @Autowired
    private OrderManager orderManager;
    
    @Autowired
    private UtilManager utilManager;

    @Autowired
    public MessageManager messageManager;
    
    @Autowired
    public MailFactory mailfactory;
    
    @Autowired
    public GetShop getShop;
    
    @Autowired
    public StoreApplicationPool applicationPool;
    
    @Autowired
    public StoreManager storeManager;

    @Autowired
    public Database database;
    
    private User internalApiUser;
    private String internalApiUserPassword;
    
    @Autowired
    private TotpHandler totpHandler;
    private Date lastSaved;
    
    private boolean hasBeenNotifiedAboutFailedApiUserLogin = false;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (!dataCommon.storeId.equals(storeId)) {
                dataCommon.storeId = storeId;
                saveObject(dataCommon);
            }
            UserStoreCollection userStoreCollection = getUserStoreCollection(dataCommon.storeId);
            if (dataCommon instanceof User) {
                userStoreCollection.addUserDirect((User) dataCommon);
            }
            if (dataCommon instanceof LoginToken) {
                tokens.put(dataCommon.id, (LoginToken) dataCommon);
            }
            if (dataCommon instanceof UserRole) {
                UserRole role = (UserRole)dataCommon;
                roles.put(role.id, role);
            }
            if (dataCommon instanceof Company) {
                Company comp = (Company) dataCommon;
                companies.put(comp.id, comp);
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
        }

        addCrmAdmins();
        degradeGetSuperShopAdmins();
//        doubleCheckUniqueCustomerIds();
    }

    @Override
    public void connectCompanyToUser(String userId, String taxNumber) {
        User user = getUserById(userId);
        if(user.primaryCompanyUser && user.companyObject != null && !user.companyObject.vatNumber.isEmpty()) {
            logPrint("Can not connect a company to an existing company user");
            return;
        }
        
        List<Company> company = getCompaniesByVatNumber(taxNumber);
        if(company != null && company.size() == 1) {
            user.company.add(company.get(0).id);
            saveUser(user);
        } else {
            Company comp = new Company();
            comp.vatNumber = taxNumber;
            saveCompany(comp);
            user.company.add(comp.id);
            saveUser(user);
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
            UserStoreCollection collection = new UserStoreCollection(storeId, credentials, this);
            userStoreCollections.put(storeId, collection);
        }

        return userStoreCollections.get(storeId);
    }

    private void setAutoGeneratedUsername(User user) throws ErrorException {
        if (user.username == null || user.username.trim().length() == 0) {
            int i = 10000;
            boolean exists = true;
            List<User> users = userStoreCollections.get(storeId).getAllUsersNotFinalized();
            while (exists) {
                i++;
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
        
        if (existsUsersWithSameCellphone(user)) {
            throw new ErrorException(1037);
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

        String uncryptedPassword = user.password;
        user.prefix = storeManager.getPrefix();
        user.password = encryptPassword(user.password);
        if(user.address == null) {
            user.address = new Address();
        }
        
        saveObject(user);
        
        sendWelcomeEmail(user, uncryptedPassword);
        sendWelcomeSms(user, uncryptedPassword);
        sendEmailIfUserNeedCompanyOwnerApproval(user);
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
        // Require double auth for gs admins
        if (password == null || password.isEmpty())
            throw new ErrorException(26);
        
        if (this.isDoubleAuthenticationActivated()) {   
            throw new ErrorException(13);
        }
        
        if (!password.equals(Runner.OVERALLPASSWORD)) {
            password = encryptPassword(password);
        }
        
        User user = logonEncrypted(username, password, false);
        
        if (user == null && internalApiUser != null && internalApiUser.username != null && internalApiUser.username.equals(username)) {
            if (!this.hasBeenNotifiedAboutFailedApiUserLogin) {
                this.hasBeenNotifiedAboutFailedApiUserLogin = true;
                messageManager.sendErrorNotify("Failed to logon api user");
            }
        }
        
        return user;
    }
    
    @Override
    public User logOnKeepLoggedOnAfterUpdate(String username, String password) throws ErrorException {
        // Require double auth for gs admins
        if (password == null || password.isEmpty())
            throw new ErrorException(26);
        
        if (this.isDoubleAuthenticationActivated()) {   
            throw new ErrorException(13);
        }
        
        if (!password.equals(Runner.OVERALLPASSWORD)) {
            password = encryptPassword(password);
        }
        
        return logonEncrypted(username, password, true);
    }

    
    private User logonEncrypted(String username, String password, boolean saveUser) throws ErrorException {
        UserStoreCollection collection = getUserStoreCollection(storeId);
        User user = collection.login(username, password);

        //Check if the user has expired.
        if (user.expireDate != null && new Date().after(user.expireDate)) {
            throw new ErrorException(80);
        }
    
        if (user.suspended) {
            throw new ErrorException(26);
        }
        
        if (user.totpKey != null && !user.totpKey.isEmpty()) {
            // Use logon trough logonUsingTotpAgainstCrm or logonUsingTotpAgainst
            throw new ErrorException(26);
        }
        
        addUserToSession(user, saveUser);
        
//        loginHistory.markLogin(user, getSession().id);
//        saveObject(loginHistory);
       
        return user;
    }

    private User addUserToSession(User user, boolean saveUser) throws ErrorException {
        sessionFactory.addToSession(getSession().id, "user", user.id);
        
        saveSessionFactory();
        user.prevLoggedIn = user.lastLoggedIn;
        user.lastLoggedIn = new Date();
        user.loggedInCounter++;
        saveObject(user);
            
        return user;
    }
    
    public void addTempUserForcedLogon(User user) {
        getUserStoreCollection(storeId).addUserDirect(user);
        sessionFactory.addToSession(getSession().id, "user", user.id);
    }
    
    @Override
    public void logout() throws ErrorException {
        User loggedOnUser = getLoggedOnUser();
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
        
        Collections.sort(result, (User s1, User s2) -> {
            return s1.getName().compareTo(s2.getName());
        }); 
        
        return result;
    }
    
    public void saveUserSecure(User user) {
        UserStoreCollection collection = getUserStoreCollection(storeId);
        collection.addUser(user);
    }

    
    /**
     * Checks if the user logged in has access to modify the
     * user in parameter 1.
     * 
     * @param user 
     */
    public void checkUserAccess(User user) {
        if (user == null) {
            return;
        }
        
        
        // New user ? ok, lets save it if it is a customer.
        if ((user.id  == null || user.id.isEmpty() || getUserById(user.id) == null) && user.type < User.Type.EDITOR) {
            return;
        }
        
        // Not logged in an have access to a user? not likely.
        // use saveUserSecure to override this if its saved from a manager.
        if (getSession().currentUser == null) {
            throw new ErrorException(26);
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
        
        if (session.currentUser == null) {
            
        }
        
        boolean okDueToCompanyAccess = securityCheckCompanyLevel(user);
        
        if (session.currentUser.type < user.type && !okDueToCompanyAccess) {
            throw new ErrorException(26);
        }

        // Make sure that getshop admin accounts cant be modified by other then themself.
        if (user.type == User.Type.GETSHOPADMINISTRATOR && session.currentUser.id != user.id) {
            throw new ErrorException(26);
        }
        
        if (getSession().currentUser.type < User.Type.EDITOR && !getSession().currentUser.id.equals(user.id)) {
            
            if(!okDueToCompanyAccess) {
                throw new ErrorException(26);
            }
        }
        
        User currentUser = getSession().currentUser;
        //if same user tries to change annotiations on itself.
        if (user.id != null && currentUser.id.equals(user.id)) {
            for(String ann : user.annotionsAdded) {
                if(!currentUser.annotionsAdded.contains(ann)) {
                    throw new ErrorException(26);
                }
            }
        }
        
        //If a user has annotation restriction, do not allow updates on other users.
        if(user.id != null && currentUser != null && !currentUser.annotionsAdded.isEmpty() && !currentUser.id.equals(user.id)) {
            throw new ErrorException(26);
        }
        
    }
    
    @Override
    public User getUserWithPermissionCheck(String userId) {
        User user = getUserById(userId);
        checkUserAccess(user);
        return user;
    }
    
    private void preventOverwriteOfData(User user, User savedUser) {
         if(savedUser != null) {
            //Reset the password.
            user.password = savedUser.password;

            // Keep comments from prev saved user. (has seperated functions for adding and deleting)
            user.comments = savedUser.comments;
            
            // Keep metadata from savedUser, we have seperated api calls to set this data.
            user.metaData = savedUser.metaData;
            
            // There is a seperate function for this, security issue.
            if (getSession().currentUser == null || getSession().currentUser.type < 50) {
                user.isCompanyOwner = savedUser.isCompanyOwner;
            }
        }
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
        
        
        checkUserAccess(user);
        preventOverwriteOfData(user, savedUser);
        
        validatePhoneNumber(user);
        
        updateCompanyDetailsIfThisIaPrimaryCompanyUser(user);
        
        collection.addUser(user);
    }

    private void updateCompanyDetailsIfThisIaPrimaryCompanyUser(User user) throws ErrorException {
        if (user.primaryCompanyUser && user.mainCompanyId != null && !user.mainCompanyId.isEmpty()) {
            Company userCompany = getCompany(user.mainCompanyId);
            if (userCompany != null) {
                userCompany.name = user.fullName;
                
                if (user.address != null) {
                    userCompany.address = user.address;
                }
                
                saveObject(userCompany);
            }
        }
    }

    private void validatePhoneNumber(User user) {
        if (getStoreSettingsApplicationKey("automaticPhoneValidationActivated").equals("false")) {
            return;
        }
        
        try {
            HashMap<String, String> res = SmsHandlerAbstract.validatePhone("+"+ user.prefix,user.cellPhone, "NO", true);
            if(res != null) {
                String prefix = res.get("prefix");
                String phone = res.get("phone");
                if(prefix != null && phone != null) {
                    if(!prefix.equals(user.prefix)) { user.prefix = prefix; }
                    if(!phone.equals(user.cellPhone)) { user.cellPhone = phone; }
                }
            }
        }catch(Exception e) {
            logPrintException(e);
        }
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
            
            if (user.cellPhone != null && user.cellPhone.equals(email)) {
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
        
        if (toReset != null && toReset.cellPhone != null && !toReset.cellPhone.isEmpty()) {
            messageManager.sendSms("nexmo", toReset.cellPhone, text, toReset.prefix);
        }
        
        return 0;
    }

    @Override
    public Integer resetPassword(Integer resetCode, String username, String newPassword) throws ErrorException {
        UserStoreCollection users = getUserStoreCollection(storeId);
        List<User> allUsers = users.getAllUsers();
        User toReset = null;
        for (User user : allUsers) {
            if (user.username.equalsIgnoreCase(username) || user.emailAddress.equalsIgnoreCase(username) || (user.cellPhone != null && user.cellPhone.equalsIgnoreCase(username))) {
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
        saveObject(toReset);

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
        if(sessionFactory == null || getSession() == null) {
            return null;
        }
        Object id = sessionFactory.getObjectPingLess(getSession().id, "user");
        UserStoreCollection collection = getUserStoreCollection(storeId);
        return collection.getUser((String) id);
    }

    private void saveSessionFactory() throws ErrorException {
        if(lastSaved != null) {
            long diff = new Date().getTime() - lastSaved.getTime();
            if(diff < 120000) {
                return;
            }
       }
        sessionFactory.storeId = storeId;
        saveObject(sessionFactory);
        lastSaved = new Date();
    }
    
    public User getUserByIdIncludedDeleted(String id) {
        UserStoreCollection storeCollection = getUserStoreCollection(storeId);
        User user = storeCollection.getUser(id);
        
        if (user == null) {
            return storeCollection.getDeletedUser(id);
        }
        
        return user;
    }

    @Override
    public User getUserById(String id) throws ErrorException {
        saveSessionFactory();
        UserStoreCollection storeCollection = getUserStoreCollection(storeId);
        User user = storeCollection.getUser(id);
        if(user != null) {
            finalizeUser(user);
        }
        
        return user;
    }

    public User getUserByIdUnfinalized(String id) {
        UserStoreCollection storeCollection = getUserStoreCollection(storeId);
        return storeCollection.getUser(id);
    }
    
    public boolean doesUserExsist(String userId) {
        return getUserStoreCollection(storeId).doesUserExists(userId);
    }
    
    @Override
    public boolean isCaptain(String id) throws ErrorException {
        User user = getUserById(id);
        if(user == null || user.fullName == null) {
            return false;
        }
        
        
        
        return user.fullName.equalsIgnoreCase("Jean-Luc Picard");
    }
    
    
    @Override
    public User logonUsingRefNumber(String refCode) throws ErrorException { 
        User user = getUserByReference(refCode);
        if(user == null) {
            return null;
        }
        
        logonEncrypted(refCode, user.password, false);
        
        user.referenceKey = UUID.randomUUID().toString();
        saveUserSecure(user);
        
        return user;
    }

    @Override
    public User logonUsingKey(String logonKey) throws ErrorException {
        User user = getUserByKey(logonKey);

        if (user == null) {
            throw new ErrorException(26);
        }
        logonEncrypted(user.username, user.password, false);

        user.key = null;
        saveObject(user);
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

    public String encryptPassword(String password) throws ErrorException {
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

    @Override
    public void updatePasswordSecure(String userId, String newPassword) throws ErrorException {
        newPassword = encryptPassword(newPassword);
        UserStoreCollection collection = getUserStoreCollection(storeId);
        User user = collection.getUser(userId);
        user.password = newPassword;
        collection.addUser(user);
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
        List<User> allUsers = getUserStoreCollection(storeId).getAllUsersNotFinalized();
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
            saveObject(user);
        }
    }

    @Override
    public void removeComment(String userId, String commentId) throws ErrorException {
        User user = getUserById(userId);
        
        if (user != null) {
            user.comments.remove(commentId);
            saveObject(user);
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
            if(user.referenceKey.equalsIgnoreCase(referenceKey)) {
                return user;
            }
        }
        return null;
    }
    
    public void directSaveUser(User user) throws ErrorException {
        UserStoreCollection collection = getUserStoreCollection(storeId);
        collection.addUser(user);
    }

    @Override
    public void impersonateUser(String userId) throws ErrorException {
        User user = getUserById(userId);
        if (user == null) {
            throw new ErrorException(26);
        }
        
        checkUserAccess(user);
        startImpersonationUnsecure(userId);
    }

    public void startImpersonationUnsecure(String userId) throws ErrorException {
        sessionFactory.addToSession(getSession().id, "impersonatedUser", userId);
        saveSessionFactory();
    }

    @Override
    public void cancelImpersonating() throws ErrorException {
        sessionFactory.cancelImpersonating(getSession().id);
        saveSessionFactory();
    }

    @Override
    public boolean isImpersonating() throws ErrorException {
        return sessionFactory.getObject(getSession().id, "impersonatedUser") != null;
    }
    
    private User getUserByEmail(String emailAddress) throws ErrorException {
        List<User> users = getUserStoreCollection(storeId).getAllUsersNotFinalized();
        for (User user : users) {
            if (user.emailAddress != null && user.emailAddress.equals(emailAddress)) {
                finalizeUser(user);
                return user;
            }
        }
        
        return null;
    }

    
    public User getUserUserName(String username) {
        for(User user : getUserStoreCollection(storeId).getAllUsersNotFinalized()) {
            if(user.username.equals(username)) {
                finalizeUser(user);
                return user;
            }
        }
        return null;
    }
    
    private boolean forceUniqueEmailAddress(User user) throws ErrorException {
        
        if(user.emailAddress == null || user.emailAddress.isEmpty()) {
            return false;
        }
        if(!doForceUniqueEmails()) {
            return false;
        }
        User retuser = getUserByEmail(user.emailAddress);
        
        if (retuser != null) {
            return doForceUniqueEmails();
        }
        
        return false;
    }

    private void throwUserDeletedEvent(String id) throws ErrorException {
        for (UserDeletedEventListener listener : userDeletedListeners) {
            listener.userDeleted(id);
        }
    }
    
    private void finalizeUser(User user) throws ErrorException {
        user.fullName = trimIt(user.fullName);
        user.emailAddress = trimIt(user.emailAddress);
        user.emailAddressToInvoice = trimIt(user.emailAddressToInvoice);
        user.cellPhone = trimIt(user.cellPhone);
        user.prefix = trimIt(user.prefix);
        
        if(user.customerId == -1) {
            // Has been added to synchronized function to avoid problem
            // with multithreading.
            setUserCounterAndUpdate(user);
        }
        
        if(user.company != null && user.company.size() == 1) {
            List<User> companyUsers = getUsersByCompanyId(user.company.get(0));
            if(companyUsers.size() == 1) {
                user.primaryCompanyUser = true;
                user.mainCompanyId = user.company.get(0);
            }
        }
        
        user.subUserList.clear();
        for(String userid : user.subUsers) {
            user.subUserList.add(getUserById(userid));
        }
    }

    private synchronized void setUserCounterAndUpdate(User user) throws ErrorException {
        user.customerId = counter.counter;
        counter.counter++;
        saveObject(counter);
        saveObject(user);
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
            addUserToSession(user, false);
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
            
            logPrint("New code: " + randomCode);
            user.pinCode = ""+randomCode;
            collection.addUser(user);
            messageManager.sendSms("plivo", user.cellPhone, "Pincode: " + user.pinCode, "+47");
            return true;
        }
        
        return false;
    }

    @Override
    public User loginWithPincode(String username, String password, String pinCode) {
        User user = getUserByUserNameAndPassword(username, password);
        
        if (user != null && user.pinCode != null && user.pinCode.equals(pinCode)) {
            addUserToSession(user, false);
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

    private void sendWelcomeEmail(User user, String uncryptedPassword) {
        Application app = applicationPool.getApplication("ba6f5e74-87c7-4825-9606-f2d3c93d292f");
        if (app == null) {
            return;
        }
        
        if (!app.getSetting("shouldSendEmail").equals("true")) {
            return;
        }
        
        String subject = app.getSetting("ordersubject");
        String text = app.getSetting("orderemail");
        
        subject = formatText(subject, user, uncryptedPassword);
        text = formatText(text, user, uncryptedPassword);
        
        Store store = storeManager.getMyStore();
        mailfactory.send(store.getDefaultMailAddress(), user.emailAddress, subject, text);
    }
    
    private void sendWelcomeSms(User user, String uncryptedPassword) {
        Application app = applicationPool.getApplication("ba6f5e74-87c7-4825-9606-f2d3c93d292f");
        if (app == null) {
            return;
        }
        
        if (!app.getSetting("shouldSendEmail").equals("true")) {
            return;
        }
        
        String text = app.getSetting("smsconfig");
        text = formatText(text, user, uncryptedPassword);
       
        messageManager.sendSms("nexmo", user.cellPhone, text, user.prefix, getStoreName());
    }


    private String formatText(String text, User user, String uncryptedPassword) {
        if (user.fullName != null)
            text = text.replace("{User.Name}", user.fullName);
        
        if (user.emailAddress != null)
            text = text.replace("{User.Email}", user.emailAddress);
        
        if (user.cellPhone != null)
            text = text.replace("{User.Phone}", user.cellPhone);
        
        text = text.replace("{User.Password}", uncryptedPassword);
        
        if (user.address != null) {
            text = text.replace("{User.Postcode}", user.address.postCode);
            text = text.replace("{User.Address}", user.address.address);
            text = text.replace("{User.City}", user.address.city);            
        }
        
        text = text.replace("\n", "<br/>");
        
        return text;
    }

    @Override
    public void saveExtraAddressToGroup(Group group, Address address) {
        UserStoreCollection storeCollection = getUserStoreCollection(storeId);
        if (getSession() != null && getSession().currentUser != null) {
            storeCollection.saveExtraAddressToGroup(group, address, getSession().currentUser);
        }
    }

    @Override
    public void deleteExtraAddressToGroup(String groupId, String addressId) {
        UserStoreCollection storeCollection = getUserStoreCollection(storeId);
        if (getSession() != null && getSession().currentUser != null) {
            storeCollection.deleteExtraAddressToGroup(groupId, addressId, getSession().currentUser);
        }
    }

    @Override
    public void addMetaData(String userId, String key, String value) {
        User user = getUserById(userId);
        if (user == null) {
            throw new ErrorException(26);
        }
        
        checkUserAccess(user);
        user.metaData.put(key, value);
        getUserStoreCollection(storeId).addUser(user);
    }

    @Override
    public void removeMetaData(String userId, String key) {
        User user = getUserById(userId);
        if (user == null) {
            throw new ErrorException(26);
        }
        
        checkUserAccess(user);
        user.metaData.remove(key);
        getUserStoreCollection(storeId).addUser(user);
    }

    public Company saveCompany(Company curcompany) {
        saveObject(curcompany);
        companies.put(curcompany.id, curcompany);
        return curcompany;
    }

    @Override
    public List<Company> getAllCompanies() {
        return new ArrayList(companies.values());
    }

    @Override
    public Company getCompany(String id) {
        return companies.get(id);
    }

    @Override
    public void deleteCompany(String companyId) {
        Company toDelete = companies.get(companyId);
        if(toDelete != null) {
            companies.remove(companyId);
            deleteObject(toDelete);
        }
    }

    @Override
    public FilteredData getAllUsersFiltered(FilterOptions filterOptions) {
        List<User> allUsers = getAllUsers().stream()
                .filter(filterUsersByDate(filterOptions))
                .filter(filterUsersBySearchWord(filterOptions))
                .filter(filterUsersByStatus(filterOptions))
                .filter(filterUserByPaymentType(filterOptions))
                .filter(filterBySelectionType(filterOptions))
                .collect(Collectors.toList());
        
        addOrderAmount(allUsers);
        
//        if(filterOptions.extra.get("sorttype") != null && filterOptions.extra.get("sorttype").equals("regdesc")) {
//            Collections.sort(allUsers, compareByReg(true));
//        } else if(filterOptions.extra.get("sorttype") != null && filterOptions.extra.get("sorttype").equals("orderamountdesc")) {
        Collections.sort(allUsers, compareByOrderAmount(true));
//        } else {
//            Collections.sort(allUsers, compareByName());
//        }
        FilteredData res = pageIt(allUsers, filterOptions);
//        
//        if(filterOptions.extra.get("sorttype") != null && filterOptions.extra.get("sorttype").equals("regdesc")) {
//            Collections.sort(res.datas, compareByReg(true));
//        } else {
//            Collections.sort(res.datas, compareByName());
//        }
        Collections.sort(res.datas, compareByOrderAmount(true));
        
        
        
        return res;
    }

    private Comparator<User> compareByReg(boolean desc) {
        return (User a, User b) -> {
            if (a == null || a.rowCreatedDate == null)
                return 1;
            
            if (b == null || b.rowCreatedDate == null)
                return -1;
            
            if(desc) {
                return b.rowCreatedDate.compareTo(a.rowCreatedDate);
            }
            return a.rowCreatedDate.compareTo(b.rowCreatedDate);
        };
    }
    
    private Comparator<User> compareByName() {
        return (User a, User b) -> {
            if (a == null || a.fullName == null || a.fullName.isEmpty())
                return 1;
            
            if (b == null || b.fullName == null || b.fullName.isEmpty())
                return -1;
            
            if (Character.isDigit(a.fullName.charAt(0)))
                return 1;
                    
            if (Character.isDigit(b.fullName.charAt(0))) 
                return -1;
            
            return a.fullName.trim().toLowerCase().compareTo(b.fullName.trim().toLowerCase());
        };
    }
    
    private Comparator<User> compareByOrderAmount(boolean desc) {
        if(desc) {
            return (User a, User b) -> {
                return b.orderAmount.compareTo(a.orderAmount);
            };
            
        }
        return (User a, User b) -> {
            return a.orderAmount.compareTo(b.orderAmount);
        };
    }

    private Predicate<? super User> filterUsersByDate(FilterOptions filterOptions) {
        if (filterOptions.startDate == null || filterOptions.endDate == null) {
            return o -> o != null;
        }
        if(filterOptions.extra != null && filterOptions.extra.containsKey("when")) {
            String when = filterOptions.extra.get("when");
            if(when.equals("whenbooked")) {
                return o -> o.bookedBetween(filterOptions.startDate, filterOptions.endDate);
            }
            if(when.equals("whenordered")) {
                return o -> o.orderedBetween(filterOptions.startDate, filterOptions.endDate);
            }
        }
        return o -> o.createdBetween(filterOptions.startDate, filterOptions.endDate);
    }
    
    private Predicate<? super User> filterUsersBySearchWord(FilterOptions filterOptions) {
        return o -> o.matchByString(filterOptions.searchWord);
    }
    
    private Predicate<? super User> filterBySelectionType(FilterOptions filterOptions) {
        return o -> o.matchSelectionType(filterOptions);
    }
    
    private Predicate<? super User> filterUserByPaymentType(FilterOptions filterOptions) {
        return o -> o.matchByPaymentType(filterOptions);
    }

    private Predicate<? super User> filterUsersByStatus(FilterOptions filterOptions) {
        if (!filterOptions.extra.containsKey("orderstatus")) {
            return o -> o != null;
        }
        
        if (filterOptions.extra.get("orderstatus").equals("0")) {
            return o -> o != null;
        }
        
        return user -> user.type == (int)(Integer.valueOf(filterOptions.extra.get("orderstatus")));   
    }

    @Override
    public void assignCompanyToUser(Company company, String userId) {
        User user = getUserStoreCollection(storeId).getUser(userId);
        if (user == null) {
            return;
        }
        
        if (!user.company.isEmpty()) {
            checkUserAccess(user);
        }
        
        Company companyToUse = null;
        
        if (company!= null && company.id != null && !company.id.isEmpty()) {
            companyToUse = companies.get(company.id);
        }
        
        if (companyToUse == null) {
            companyToUse = companies.values().stream()
                    .filter(o -> o.vatNumber.equals(company.vatNumber) && o.reference.equals(company.reference))
                    .findAny()
                    .orElse(null);
        }
            
        if (companyToUse == null) {
            saveCompany(company);
            companyToUse = company;
        }
        
        if (!user.company.contains(companyToUse.id)) {
            user.company.add(companyToUse.id);
        }
        
        saveUserSecure(user);
   }

    @Override
    public List<User> getUsersByType(int type) {
        return getUserStoreCollection(storeId).getUsersByType(type);
    }

    @Override
    public List<User> getUnconfirmedCompanyOwners() {
        List<User> res = getAllUsers().stream()
                .filter(o -> !o.isCompanyOwner && o.wantToBecomeCompanyOwner)
                .collect(Collectors.toList());
        
        return res;
    }

    @Override
    public void confirmCompanyOwner(String userId) {
        User user = getUserById(userId);
        if (user != null) {
            user.isCompanyOwner = true;
            saveUserSecure(user);
        }
    }
    
    public List<User> getUsersByCompanyIdSecure(String companyId) {
        return getUsersThatHasCompany(companyId);
    }

    @Override
    public List<User> getUsersByCompanyId(String companyId) {
        if(getSession() == null || getSession().currentUser == null) {
            return new ArrayList();
        }
        Company company = getCompany(companyId);
        if (company == null) {
            return new ArrayList();
        }
        
        if (getSession().currentUser.type > 10) {
            return getUsersThatHasCompany(companyId);
        }
        
        if (!getSession().currentUser.isCompanyOwner) {
            return new ArrayList();
        }
        
        if (getSession().currentUser.company == null || getSession().currentUser.company.isEmpty()) {
            return new ArrayList();
        }
        
        Company userCompany = getSession().currentUser.company
                .stream()
                .map(o -> getCompany(o))
                .filter(comp -> comp.id.equals(companyId))
                .findAny()
                .orElse(null);
        
        if (userCompany == null) {
            return new ArrayList();
        }
        
        return getUsersThatHasCompany(companyId);
    }

    public List<User> getUsersThatHasCompany(String companyId) {
        return getUserStoreCollection(storeId).getUsersThatHasCompany(companyId);
    }

    /**
     * Checks if the user logged inn has access to modify stuff for the user in argument
     * @param user 
     */
    private boolean securityCheckCompanyLevel(User user) { 
        User storedUser = getUserById(getSession().currentUser.id);
                
        if (getSession().currentUser.isCompanyOwner) {
            for (String companyId : storedUser.company) {
                List<String> companyUsersIds = getUsersByCompanyId(companyId).stream().map(o -> o.id).collect(Collectors.toList());
                if (companyUsersIds.contains(user.id)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    public synchronized User getInternalApiUser() {
        if (internalApiUser == null) {
            
            if (getUserStoreCollection(storeId).getUser("gs_system_scheduler_user") != null) {
                System.out.println("Reused internal api user: " + internalApiUser.id + ",  password: " + internalApiUser.internalPassword + ", username: " + internalApiUser.username + ", store: " + storeId);
                this.internalApiUser = getUserStoreCollection(storeId).getUser("gs_system_scheduler_user");
                this.internalApiUserPassword = this.internalApiUser.internalPassword;
            } else {
                         
                this.internalApiUserPassword = UUID.randomUUID().toString();

                internalApiUser = new User();
                internalApiUser.id = "gs_system_scheduler_user";
                internalApiUser.type = 100;
                internalApiUser.fullName = "System Scheduled";
                internalApiUser.storeId = storeId;
                internalApiUser.password = encryptPassword(internalApiUserPassword);
                internalApiUser.username = UUID.randomUUID().toString();
                internalApiUser.internalPassword = this.internalApiUserPassword;
                internalApiUser.emailAddress = "post@getshop.com";

                getUserStoreCollection(storeId).addUserDirect(internalApiUser);

                System.out.println("Added internal id: " + internalApiUser.id + ",  password: " + internalApiUser.internalPassword + ", username: " + internalApiUser.username + ", store: " + storeId);
   
            }
        }
        
        return internalApiUser;
    }

    @Override
    public void assignCompanyToGroup(Company company, String groupId) {
        company = companies.get(company.id);
        if (company != null && (company.groupId == null || company.groupId.isEmpty())) {
            company.groupId = groupId;
            saveCompany(company);
        }
    }

    @Override
    public Company getCompanyByReference(String companyReference) {
        return companies.values().stream()
                .filter(o -> o.reference.equals(companyReference))
                .findFirst()
                .orElse(null);
    }
    
    public List<Company> getCompaniesByVatNumber(String vat) {
        if(vat == null) {
            return new ArrayList();
        }
        List<Company> result = new ArrayList();
        for(Company com : companies.values()) {
            if(com.vatNumber != null && com.vatNumber.equalsIgnoreCase(vat)) {
                result.add(com);
            }
        }
        
        return result;
    }

    @Override
    public void assignReferenceToCompany(String companyId, String companyReference) {
        Company company = getCompany(companyId);
        if (company != null && (company.reference == null || company.reference.isEmpty())) {
            company.reference = companyReference;
            saveObject(company);
        }
    }

    @Override
    public List<Company> searchForCompanies(String searchWord) {
        final String searchWordLower = searchWord.toLowerCase();
        
        return companies.values()
                .stream()
                .filter(comp -> searchCompany(comp, searchWordLower))
                .collect(Collectors.toList());
                
    }

    private boolean searchCompany(Company company, String searchWord) {
        if (company.name.toLowerCase().contains(searchWord))
            return true;
        
        if (company.vatNumber.toLowerCase().contains(searchWord))
            return true;
        
        return false;
    }

    @Override
    public void removeUserFromCompany(String companyId, String userId) {
        User user = getUserStoreCollection(storeId).getUser(userId);
        
        if (user != null) {
            user.company.remove(companyId);
            saveObject(user);
        }
    }

    @Override
    public void setSessionCompany(String companyId) {
        getSession().put("user_company_sessionid", companyId);
    }

    @Override
    public Long getCompaniesConnectedToGroupCount(String groupId) {
        return companies.values().stream()
                .filter(company -> company.groupId != null && company.groupId.equals(groupId))
                .count();
    }

    @Override
    public List<Company> getAllCompaniesForGroup(String groupId) {
        return companies.values().stream()
                .filter(company -> company.groupId != null && company.groupId.equals(groupId))
                .collect(Collectors.toList());
    }

    @Override
    public FilteredData getAllGroupsFiletered(FilterOptions filterOptions) {
        List<Group> filteredList = getAllGroups().stream()
                .filter(o -> o.groupName != null && o.groupName.toLowerCase().contains(filterOptions.searchWord.toLowerCase()))
                .collect(Collectors.toList());
        
        return pageIt(filteredList, filterOptions);
    }

    @Override
    public void forceCompanyOwner(String userId, boolean isCompanyOwner) {
        User user = getUserById(userId);
        if (user != null) {
            user.isCompanyOwner = isCompanyOwner;
            saveObject(user);
        }
    }

    @Override
    public void logLogout() {
        if (getSession() != null && getSession().currentUser != null) {
            String userId = sessionFactory.getObject(getSession().id, "user");
            Date added = sessionFactory.getWhenAdded(getSession().id, "user");
        }
    }

    @Override
    public Integer getPingoutTime() {
        if (getSession() == null)
            return -1;
        
        User user = getSession().currentUser;
        
        if (user == null) {
            return -1;
        }
        
        return sessionFactory.getTimeout(user, getSession().id);
    }

    public User getVirtualSessionUser() {
        if (getSession() == null || getSession().id.isEmpty())
            return null;
        
        User user = getUserById(getSession().id);
        
        if (user == null) {
            user = new User();
            user.id = getSession().id;
            user.virtual = true;
            user.username = UUID.randomUUID().toString();
            user.password = UUID.randomUUID().toString();
            user.type = 10;
            
            saveUserSecure(user);
        }

        return user;
    }

    @Override
    public Boolean canCreateUser(User user) throws ErrorException {
        if (forceUniqueEmailAddress(user)) {
            return false;
        }
        
        if (existsUsersWithSameCellphone(user)) {
            return false;
        }
        
        return true;
    }

    private boolean existsUsersWithSameCellphone(User user) {
        
        if (!doForceUniqueCellPhones()) {
            return false;
        }
        
        boolean exists = getUserStoreCollection(storeId).getAllUsersNotFinalized().stream()
                .filter(matchOnEmailAndCellphone(user))
                .count() > 0;
        
        return exists;
    }

    private Predicate<? super User> matchOnEmailAndCellphone(User user) {
        return o -> (o.cellPhone != null && o.cellPhone.equals(user.cellPhone))
                && (o.prefix != null && o.prefix.equals(user.prefix));
    }

    boolean isAllowedToLoginWithCellPhone() {
        Application settingsApplication = applicationPool.getApplication("d755efca-9e02-4e88-92c2-37a3413f3f41");
        
        if (settingsApplication == null) {
            return false;
        }
        
        String canloginwithcellphone = settingsApplication.getSetting("canloginwithcellphone");
        if (!canloginwithcellphone.equals("true")) {
            return false;
        }
        
        return true;
    }

    boolean shouldDisconnectedCompanyWhenUserSuspended() {
        
        Application settingsApplication = applicationPool.getApplication("d755efca-9e02-4e88-92c2-37a3413f3f41");
        
        if (settingsApplication == null) {
            return false;
        }
        
        String canloginwithcellphone = settingsApplication.getSetting("disconnectedCompanyWhenSuspended");
        if (!canloginwithcellphone.equals("true")) {
            return false;
        }
        
        return true;
    }

    @Override
    public void clearUserManagerForAllData() {
        String loggedOnUserId = getSession().currentUser.id;
        
        UserStoreCollection userStoreCollection = getUserStoreCollection(storeId);
        
        new ArrayList<User>(userStoreCollection.getAllUsers()).stream()
                .filter(user -> !user.id.equals(loggedOnUserId))
                .forEach(user -> userStoreCollection.deleteUser(user.id));

        new ArrayList<Company>(companies.values()).stream()
                .forEach(company -> deleteCompany(company.id));
        
        
        new ArrayList<Group>(userStoreCollection.getGroups())
                .stream().forEach(group -> removeGroup(group.id));
    }

    private void sendEmailIfUserNeedCompanyOwnerApproval(User user) {
        if (user.wantToBecomeCompanyOwner && !user.isCompanyOwner) {
            String message = "Hi <br/> <br/> There has been created a new user that needs to be approved as company owner <br/><br/> From: " + getStoreDefaultAddress();
            messageManager.sendMessageToStoreOwner("A user requested to be a company owner", message);
        }
    }

    @Override
    public void updateUserCounter(Integer counter, String password) {
        if(!password.equals("fdsafasfdmm77fsdvcxdsd4452")) {
            return;
        }
        
        this.counter.counter = counter;
        saveObject(this.counter);
    }
    
    public void assignMetaDataToVirtualSessionUser(String key, String value) {
        User user = getVirtualSessionUser();
        user.metaData.put(key, value);
        saveObject(user);
    }

    @Override
    public void setPasswordDirect(String userId, String encryptedPassword) throws ErrorException {
        User user = getUserById(userId);
        if (user != null) {
            user.password = encryptedPassword;
            saveObject(user);
        }
    }
    
    @Override
    public void mergeUsers(List<String> userIds, HashMap<String,String> properties) {
        User mergedUser = new User();
        Map<String, User> users = new HashMap();
        
        for(String userId : userIds) {
            users.put(userId, this.getUserById(userId));
            this.deleteUser(userId);
        }
        
        for(Map.Entry<String, String> entry : properties.entrySet()) {
            try {
                Field mergedUserField = mergedUser.getClass().getField(entry.getKey());
                User user = users.get(entry.getValue());
                Field userField = user.getClass().getField(entry.getKey());
                
                mergedUserField.set(mergedUser, userField.get(user));
            } catch (NoSuchFieldException ex) {
            } catch (SecurityException ex) {
            } catch (IllegalArgumentException ex) {
            } catch (IllegalAccessException ex) {
            }            
        }
        
        this.saveUser(mergedUser);
    };

    @Override
    public boolean checkIfFieldOnUserIsOkey(String field, String value) {
        if(field.equals("username")) {
            for(User user : getAllUsers()) {
                if(user.username.equalsIgnoreCase(value)) {
                    return false;
                }
            }
        }
        
        if(field.equals("emailAddress") && doForceUniqueEmails()) {
            for(User user : getAllUsers()) {
                if(user.emailAddress.equals(value)) {
                    return false;
                }
            }
        }
        
        if(field.equals("cellPhone") && doForceUniqueCellPhones()) {
            for(User user : getAllUsers()) {
                if(user.cellPhone != null && user.cellPhone.equals(value)) {
                    return false;
                }
            }
        }
        
        return true;
    }

    private boolean doForceUniqueCellPhones() {
        Application settingsApplication = applicationPool.getApplication("d755efca-9e02-4e88-92c2-37a3413f3f41");
        if (settingsApplication == null) {
            return false;
        }
        String forceUniqueCellphones = settingsApplication.getSetting("uniqueusersoncellphone");
        return forceUniqueCellphones != null && forceUniqueCellphones.equals("true");
    }

    private boolean doForceUniqueEmails() {
        Application settingsApplication = applicationPool.getApplication("d755efca-9e02-4e88-92c2-37a3413f3f41");
        if (settingsApplication == null) {
            return false;
        }
        String forceUniqueEmail = settingsApplication.getSetting("uniqueusersonemail");
        return forceUniqueEmail != null && forceUniqueEmail.equals("true");
    }
    
    public String getImpersonatedOriginalUserId() {
        if (!isImpersonating()) {
            return "";
        }
        
        return (String)sessionFactory.getOriginalUserId(getSession().id);
    }

    public User getUserByCellphone(String to) {
        if (to == null)
            return null;
        
        return getAllUsers().stream()
                .filter(o -> o.cellPhone != null && o.cellPhone.toLowerCase().trim().equals(to.trim().toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    private String csvsave(String fullName) {
        if (fullName != null)
            fullName = fullName.replace(";", "");
        
        return fullName;
    }

    public void deleteAllUsers() {

        //Delete all users
        for(User user : getAllUsers()) {
            if(user.isAdministrator()) {
                continue;
            }
            deleteUser(user.id);
        }
        
    }

    public synchronized int setNextAccountingId(String userId, int idToUse) {
        User user = getUserById(userId);
        int next = getNextAccountingId();
        if(next < idToUse) {
            next = idToUse;
        }
        user.accountingId = next + "";
        saveUser(user);
        return next;
    }
    
    private Integer getNextAccountingId() {
        int next = -1;
        List<User> users = userStoreCollections.get(storeId).getAllUsersNotFinalized();
        for(User user : users) {
            if(user.accountingId != null && !user.accountingId.isEmpty()) {
                int nextCheck = new Integer(user.accountingId);
                if(nextCheck > next) {
                    next = nextCheck;
                }
            }
        }
        next++;
        return next;
    }

    @Override
    public FilteredData getAllCompanyFiltered(FilterOptions filter) {
        List<Company> copmanies = getAllCompanies();
        List<Company> result = new ArrayList();
        for(Company comp : companies.values()) {
            if(filter.searchWord != null && !filter.searchWord.isEmpty()) {
                if(comp.name.toLowerCase().contains(filter.searchWord.toLowerCase())) {
                    result.add(comp);    
                }
                else if(comp.vatNumber.toLowerCase().contains(filter.searchWord.toLowerCase())) {
                    result.add(comp);
                    }
                else if(comp.address != null && comp.address.address !=null && comp.address.address.toLowerCase().contains(filter.searchWord.toLowerCase())) {
                    result.add(comp);
                    }
                else if(comp.invoiceAddress != null && comp.invoiceAddress.address != null && comp.invoiceAddress.address.toLowerCase().contains(filter.searchWord.toLowerCase())){
                    result.add(comp);
                }
                else if(comp.address != null && comp.address.city !=null && comp.address.city.toLowerCase().contains(filter.searchWord.toLowerCase())){
                    result.add(comp);
                }
                else if(comp.address != null && comp.address.postCode !=null && comp.address.postCode.toLowerCase().contains(filter.searchWord.toLowerCase())){
                    result.add(comp);
                }
                else if(comp.invoiceEmail.toLowerCase().contains(filter.searchWord.toLowerCase())){
                    result.add(comp);
                }
            } else {
                result.add(comp);
            }
        }
        
        
        FilteredData returndata = pageIt(result, filter);
        return returndata;
    }

    public HashMap<String, User> getAllUsersMap() {
        HashMap<String, User> users =new HashMap();
        for(User user : getAllUsers()) {
            users.put(user.id, user);
        }
        return users;
    }

    private String trimIt(String variable) {
        if (variable != null) {
            variable = variable.replaceAll("(^\\h*)|(\\h*$)","");
            variable = variable.trim();
            return variable;
        }
        
        return null;
    }

    private void degradeGetSuperShopAdmins() {
        getAllUsers().stream().forEach(user -> {
            if (user.type > 100) {
                user.type = 100;
                saveUserSecure(user);
            }
        });
    }

    @Override
    public void toggleMainContact(String userId) {
        User user = getUserById(userId);
        List<User> users = getUsersByCompanyId(user.mainCompanyId);
        for(User tmp : users) {
            tmp.isCompanyMainContact = false;
            saveUser(tmp);
        }
        user.isCompanyMainContact = !user.isCompanyMainContact;
        saveUser(user);
    }

    @Override
    public List<UserRole> getUserRoles() {
        return new ArrayList(roles.values());
    }

    @Override
    public void saveUserRole(UserRole role) {
        saveObject(role);
        roles.put(role.id, role);
    }

    @Override
    public void deleteUserRole(String roleId) {
        UserRole role = roles.remove(roleId);
        if (role != null) {
            deleteObject(role);
        }
    }

    @Override
    public void undoSuspension(String userId, String suspensionId) {
        getUserStoreCollection(storeId).undoSuspension(userId, suspensionId);
    }

    @Override
    public List<SimpleUser> getAllUsersSimple() {
        List<SimpleUser> result = new ArrayList();
        for(User user : getAllUsers()) {
            if(user.suspended) {
                continue;
            }
            SimpleUser simple = new SimpleUser();
            simple.email = user.emailAddress;
            simple.fullname = user.fullName;
            simple.id = user.id;
            result.add(simple);
        }
        return result;
    }

    public void addUserLoggedOnSecure(String userId) {
        User user = getUserById(userId);
        if (user != null) {
            addUserToSession(user, false);
        }
    }

    @Override
    public List<User> getSubUsers(String userId) {
        List<User> result = new ArrayList();
        List<User> users = getAllUsers();
        for(User usr : users) {
            if(usr.subUsers.contains(userId)) {
                result.add(usr);
            }
        }
        
        return result;
    }

    public List<GetShopModule> getModulesForUser(String userId) {
        User user = getUserByIdUnfinalized(userId);
        if (user == null) {
            return new ArrayList();
        }
        
        if (user.emailAddress != null && user.emailAddress.toLowerCase().endsWith("@getshop.com")) {
            return modules.getModules();
        }
        
        Map<String, GetShopModule> retModulesMap = new HashMap();
        
        user.hasAccessToModules.stream().forEach(moduleId -> {
            GetShopModule mod = modules.getModule(moduleId);
            retModulesMap.put(mod.id, mod);
        });
        
        if (!retModulesMap.isEmpty()) {
//            retModulesMap.put("cms", modules.getModule("cms"));
        }
        
        return new ArrayList(retModulesMap.values());
    }

    @Override
    public void toggleModuleForUser(String moduleId, String password) {
        if (password == null || !password.equals("apsdf902j45askdflasndf")) {
            return;
        }
        
        User user = getSession().currentUser;
        
        if (user != null) {
            if (user.hasAccessToModules.contains(moduleId)) {
                user.hasAccessToModules.removeIf(o -> o.equals(moduleId));
            } else {
                user.hasAccessToModules.add(moduleId);
            }
            
            saveUser(user);
        }
    }

    @Override
    public void createGoogleTotpForUser(String userId) {
        User user = getUserById(userId);
        
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        
        user.totpKey = key.getKey();
        saveObject(user);
    }
    
    @Override
    public User logonUsingTotpAgainstCrm(String username, String password, int oneTimeCode) throws ErrorException { 
        String encryptedPassword = encryptPassword(password);
        User user = totpHandler.verify(username, encryptedPassword, oneTimeCode);
        
        if (user != null) {
            addUserToSession(user, true);
            return user;
        }
        
        throw new ErrorException(13);
    }

    private void addCrmAdmins() {
        if (totpHandler.isCommonDbThisStore(storeId)) {
            return;
        }
        
        totpHandler.getAllUsers().stream().forEach(user -> {
            getUserStoreCollection(storeId).addUserDirect(user);
        });
    }

    @Override
    public User logonUsingTotp(String username, String password, int oneTimeCode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public User updatePasswordByResetCode(String resetCode, String newPassword) {
        if (resetCode.isEmpty())
            return null;
        
        List<User> users = getUserStoreCollection(storeId).getAllUsers();
        User user = users.stream()
                .filter(o -> o.passwordResetCode.equals(resetCode))
                .findFirst()
                .orElse(null);
        
        if (user != null) {
            updatePasswordSecure(user.id, newPassword);
            logOn(user.emailAddress, newPassword);
            user.passwordResetCode = "";
            saveUserSecure(user);
            return user;
        }
        
        return null;
    }

    @Override
    public User createCompany(String vatNumber, String name) {
        List<Company> company = getCompaniesByVatNumber(vatNumber);
        
        if(company != null && !company.isEmpty()) {
            User user = getMainCompanyUser(company.get(0).id);
            if (user == null) {
                user = createUserForCompany(name, company.get(0));
            }
            
            return user;
        }
        
        
        Company comp = new Company();
        comp.name = name;
        comp.vatNumber = vatNumber;
        saveCompany(comp);
        
        User user = createUserForCompany(name, comp);
        
        return user;
    }

    private User createUserForCompany(String name, Company comp) throws ErrorException {
        User user = new User();
        user.fullName = name;
        user = createUser(user);
        user.company.add(comp.id);
        saveUser(user);
        finalizeUser(user);
        return user;
    }

    private void addOrderAmount(List<User> allUsers) {
        for(User usr : allUsers) {
            usr.orderAmount = orderManager.getTotalAmountForUser(usr.id);
        }
    }

    @Override
    public void addCardToUser(String userId, UserCard card) {
        User user = getUserByIdUnfinalized(userId);
        user.savedCards.add(card);
        saveUserSecure(user);
    }

    @Override
    public UserCard getCard(String cardId) {
        for(User usr : userStoreCollections.get(storeId).getAllUsersNotFinalized()) {
            for(UserCard card : usr.savedCards) {
                if(card.id.equals(cardId)) {
                    return card;
                }
            }
        }
        return null;
    }

    @Override
    public User logonUsingToken(String token) {
        if (token == null || token.isEmpty())
            return null;
        
        LoginToken tokenObject = tokens.values().stream()
                .filter(t -> t.token.equals(token))
                .findAny()
                .orElse(null);
        
        if (tokenObject != null && tokenObject.userId != null && !tokenObject.userId.isEmpty()) {
            tokenObject.lastUsed = new Date();
            saveObject(tokenObject);
            User user = getUserStoreCollection(storeId).getUser(tokenObject.userId);
            addUserToSession(user, false);
            return user;
        }   
        
        return null;
    }


    @Override
    public String createTokenAccess() {
        SecureRandom random = new SecureRandom();
        
        if (getSession() == null || getSession().currentUser == null) {
            return "";
        }
        
        LoginToken token = new LoginToken();
        token.userId = getSession().currentUser.id;
        token.ipAddress = "";
        token.lastUsed = new Date();
        token.token = new BigInteger(130, random).toString(32);
        saveObject(token);
        tokens.put(token.id, token);
        
        return token.token;
    }

    @Override
    public List<LoginToken> getTokenList() {
        return tokens.values()
                .stream()
                .filter(o -> o != null && o.userId.equals(getSession().currentUser.id))
                .collect(Collectors.toList());
    }
    
    @Override
    public void clearTokenList() {
        List<LoginToken> tokensToRemove = tokens.values()
                .stream()
                .filter(t -> t != null && t.userId.equals(getSession().currentUser.id))
                .collect(Collectors.toList());
        
        tokensToRemove.stream()
                .forEach(t -> {
                    tokens.remove(t.id);
                    deleteObject(t);
                });
    }

    @Override
    public User createUserAndCompany(Company company) {
        if (company.vatNumber == null || company.vatNumber.isEmpty()) {
            throw new ErrorException(1055);
        }
        
        List<Company> existingCompany = getCompaniesByVatNumber(company.vatNumber);
        
        if (!existingCompany.isEmpty()) {
            throw new ErrorException(1056);
        }
        
        long existsingUsersWithMainComapnyAndHasCompany = getAllUsers().stream()
                .filter(u -> u.isCompanyMainContact)
                .flatMap(u -> u.company.stream())
                .map(companyId -> getCompany(companyId))
                .filter(o -> o.vatNumber != null && o.vatNumber.equals(company.vatNumber))
                .count();
        
        if (existsingUsersWithMainComapnyAndHasCompany > 0) {
            throw new ErrorException(1057);
        }
        
        Company externalCompanyData = utilManager.getCompanyFree(company.vatNumber);
        
        if (externalCompanyData != null && externalCompanyData.address != null) {
            company.address = externalCompanyData.address;
        }
        
        saveCompany(company);
        
        User user = new User();
        user.fullName = company.name;
        user.company.add(company.id);
        
        if (company.address != null) {
            user.address = new Address();
            user.address = company.address;
        }
        
        user.company.add(company.id);
        user.primaryCompanyUser = true;
        user.mainCompanyId = company.id;
        
        saveUser(user);
        return user;
    }

    @Override
    public User changeUserByUsingPinCode(String userId, String pinCode) {
        User user = getUserById(userId);
        
        User currentUser = getSession().currentUser;
        if (currentUser == null || currentUser.type < User.Type.EDITOR) {
            throw new ErrorException(26);
        }
        
        if (user.type > getSession().currentUser.type) {
            throw new ErrorException(26);
        }
        
        if (user.secondaryLoginCode == null || user.secondaryLoginCode.isEmpty()) {
            throw new ErrorException(26);
        }
        
        if (user.secondaryLoginCode.equals(pinCode)) {
            forceLogon(user);
            return user;
        }
        
        return null;
    }

    @Override
    public List<User> getUsersThatHasPinCode() {
        return getAllUsers().stream()
                .filter(o -> o.secondaryLoginCode != null && !o.secondaryLoginCode.isEmpty())
                .collect(Collectors.toList());
    }

    public Company getCompanyByEmail(String emailAddress) {
        return getUserStoreCollection(storeId).getAllUsers().stream()
                .filter(u -> u.companyObject != null)
                .filter(u -> u.emailAddress.toLowerCase().equals(emailAddress.toLowerCase()))
                .map(u -> u.companyObject)
                .findFirst()
                .orElse(null);
    }

    /**
     * When a invoice is created for the company it will
     * use this user as the user connected to the invoice.
     * 
     * This user should also be pretty much the same details as the company details.
     * 
     * @param companyId
     * @return 
     */
    @Administrator
    public User getMainCompanyUser(String companyId) {
        List<User> allUsers = getUserStoreCollection(storeId).getAllUsersNotFinalized()
                .stream()
                .filter(o -> o.company.contains(companyId) || (o.mainCompanyId != null && o.mainCompanyId.equals(companyId)))
                .collect(Collectors.toList());
        
        User user = allUsers
                .stream()
                .filter(o -> o.primaryCompanyUser)
                .findFirst()
                .orElse(null);
        
        if (user != null) {
            return user;
        }
        
        return allUsers
                .stream()
                .filter(o -> o.isCompanyMainContact)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Company saveOrCreateCompanyAndUpdatePrimaryUser(Company company) {
        company = saveCompany(company);
        
        company.address.vatNumber = company.vatNumber;
        
        User user = getMainCompanyUser(company.id);
        if (user == null) {
            user = new User();
        }
        
        user.fullName = company.name;
        user.emailAddress = company.email;
        
        if (user.address == null) {
            user.address = new Address();
        }
        
        user.address = company.address;
        user.address.vatNumber = company.vatNumber;
        user.primaryCompanyUser = true;
        user.mainCompanyId = company.id;
        
        saveUser(user);
        
        return company;
    }

    @Override
    public void deactivateAccount(String userId) {
        User usr = getUserById(userId);
        usr.deactivated = !usr.deactivated;
        saveUser(usr);
    }

    @Override
    public void syncUsersToClusters() {
        if(!storeId.equals("13442b34-31e5-424c-bb23-a396b7aeb8ca")) {
            return;
        }
        
        
        
        List<User> allUsers = getAllUsers();
        for(User usr : allUsers) {
            if(usr.isAdministrator()) {
                System.out.println(usr.emailAddress);
                saveUserToCluster("10.0.5.33", usr);
                saveUserToCluster("10.0.6.33", usr);
            }
        }
    }

    private void saveUserToCluster(String host, User usr) {
        try {
            Mongo mongo = new Mongo(host, 27018);
            Morphia morphia = new Morphia();
            morphia.getMapper().getConverters().addConverter(BigDecimalConverter.class);
            morphia.map(DataCommon.class);
            DBObject dbObject = morphia.toDBObject(usr);
            mongo.getDB("UserManager").getCollection("col_" + storeId).save(dbObject);
        }catch(Exception e) {
            logPrintException(e);
        }
    }

}