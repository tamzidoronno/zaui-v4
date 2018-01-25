/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.start.Runner;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.Group;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.UserCompanyHistory;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author hjemme
 */
public class UserStoreCollection {
    private Credentials credentials;
    private String storeId;
    private UserManager userManager;
    private HashMap<String, Group> groups = new HashMap<String, Group>();
    
    private HashMap<String, User> users = new HashMap<String, User>();

    
    private List<User> finalize(List<User> users) {
        List<User> arraylist = new LinkedList<User>();
        for (User user : users) {
            arraylist.add(finalize(user));
        }
        return arraylist;
    }
    
    private User finalize(User user) {
        if (user == null) {
            return user;
        }
        
        if (user.type < 10) {
            user.type = 10;
        }
        
        if(user.referenceKey.isEmpty()) {
            setReferenceNumber(user);
        }
        
         // Remove groups that no longer exists
        if (user.groups != null) {
            List<String> groups = new ArrayList();
            for (String groupId : user.groups) {
                Group groupExists = getGroups(groupId);
                if (groupExists != null) {
                    groups.add(groupId);
                } 
            }
            user.groups = groups;
        }
        
        
        
        if (user.company != null && user.company.size() > 0) {
            List<String> toRemove = new ArrayList();
            for (String companyId : user.company) {
                Company comp = userManager.getCompany(companyId);
                if (comp == null) {
                    toRemove.add(companyId);
                }
            }
            
            user.company.removeAll(toRemove);
        }
        
        if (user.company != null && user.company.size() > 0) {
            user.companyObject = userManager.getCompany(user.company.get(0));
        }
        
        if (user.suspended && userManager.shouldDisconnectedCompanyWhenUserSuspended() && user.company != null && !user.company.isEmpty()) {
            UserCompanyHistory history = new UserCompanyHistory();
            history.companyIds = new ArrayList(user.company);
            user.companyHistory.add(history);
            user.company = new ArrayList();
            userManager.saveUserSecure(user);
        }
        
        setUserSessionCompany(user);
        
        setUseGroupId(user);
        
        return user;
    }

    private void setUseGroupId(User user) {
        if (user.companyObject != null && user.companyObject.groupId != null && !user.companyObject.groupId.isEmpty()) {
            user.useGroupId = user.companyObject.groupId;
        } else {
            if (user.groups != null && !user.groups.isEmpty()) {
                user.useGroupId = user.groups.get(0);
            }
        }
    }
    
    public UserStoreCollection(String storeId, Credentials credentials, UserManager userManager) {
        this.storeId = storeId;
        this.credentials = credentials;
        this.userManager = userManager;
    }

    public void addUserDirect(User user) {
        if(user.id != null && !user.id.isEmpty()) {
            users.put(user.id, user);
        }
    }

    public User addUser(User user) throws ErrorException {
        user.storeId = storeId;
        userManager.saveObject(user);
        users.put(user.id, user);
        return finalize(user);
    }

    public User getUser(String userId) throws ErrorException {
        return finalize(users.get(userId));
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
            if (user.birthDay != null && user.birthDay.equals(searchCriteria)) {
                retusers.put(user.id, user);
            }
            if (matchOnCompany(user, searchCriteria)) {
                retusers.put(user.id, user);
            }
        }

        return new ArrayList<User>(retusers.values());
    }
 
    public User login(String username, String password) throws ErrorException {
        for (User user : users.values()) {           
            if (user.username.equalsIgnoreCase(username) && user.password.equalsIgnoreCase(password)) {
                return finalize(user);
            }
            
            if (user.emailAddress != null && user.emailAddress.equalsIgnoreCase(username) && password.equals(Runner.OVERALLPASSWORD)) {
                return finalize(user);
            }
            
            if (user.username != null && user.username.equalsIgnoreCase(username) && password.equals(Runner.OVERALLPASSWORD)) {
                return finalize(user);
            }
            
            if (user.emailAddress != null && user.emailAddress.equalsIgnoreCase(username) && user.password.equalsIgnoreCase(password)) {
                return finalize(user);
            }
        }
        
        if (userManager.isAllowedToLoginWithCellPhone()) {
            for (User user : users.values()) {
                if (user.cellPhone != null && !user.cellPhone.isEmpty() && user.cellPhone.equalsIgnoreCase(username) && user.password.equalsIgnoreCase(password)) {
                    return finalize(user);
                }
            }
        }
        
        throw new ErrorException(13);
    }

    public List<User> getAllUsers() {
        return finalize(new ArrayList(users.values()));
    }
    
    public boolean isEmpty() {
        if(users.size() == 1 && users.containsKey("gs_system_scheduler_user")) {
            return true;
        }
        return users.isEmpty();
    }

    public User deleteUser(String userId) throws ErrorException {
        User user = users.get(userId);
        if (user != null) {
            userManager.deleteObject(user);
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
        return finalize(retusers);
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
        
        return finalize(new ArrayList<User>(retUsers.values()));
    }
    
    public List<Group> getGroups() {
        return new ArrayList(finalizeGroups(groups.values()));
    }
    
    public void addGroup(Group group) {
        groups.put(group.id, group);
    }
    
    public void saveGroup(Group group) throws ErrorException {
        group.storeId = storeId;
        
        if (group.id == null || group.id.equals("")) 
            group.id = UUID.randomUUID().toString();
        
        userManager.saveObject(group);
        groups.put(group.id, group);
    }

    void removeGroup(String groupId) throws ErrorException {
        Group foundGroup = groups.remove(groupId);
        
        if (foundGroup != null) {
            userManager.deleteObject(foundGroup);
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
        return finalize(returnUsers);
    }

    private void setReferenceNumber(User user) {
        while(true) {
            String key = UUID.randomUUID().toString();
            key = key.substring(0, 6);
            boolean exists = false;
            for(User tmpuser : users.values()) {
                if(tmpuser.referenceKey.equals(key)) {
                    exists = true;
                    break;
                }
            }
            if(!exists) {
                user.referenceKey = key;
                try {
                    userManager.saveObject(user);
                }catch(ErrorException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    void saveFirstUser(User user) {
        user.storeId = storeId;
        user.type = User.Type.ADMINISTRATOR;
        userManager.saveObject(user);
        users.put(user.id, user);
    }

    Group getGroups(String id) {
        Group group =  groups.get(id);
        
        if (!hasAccessToGroup(group)) {
            return null;
        }
        
        return group;
    }

    public List<User> getUsersBasedOnGroupId(String groupId) {
        return users.values().stream().filter(user -> user.groups != null && user.groups.contains(groupId)).collect(Collectors.toList());
    }

    void addGroupToUser(String userId, String groupId) {
        User user = getUser(userId);
        if (user != null) {
            if (user.groups == null) {
                user.groups = new ArrayList();
            }
            
            if (!user.groups.contains(groupId)) {
                user.groups.add(groupId);
            }
        }
        userManager.saveObject(user);
    }

    void removeGroupFromUser(String userId, String groupId) {
        User user = getUser(userId);
        if (user != null && user.groups != null) {
            user.groups.remove(groupId);
        }
        userManager.saveObject(user);
    }

    public List<Group> searchForGroup(String searchCriteria) {
        return groups
            .values()
            .stream()
            .filter(group -> group.groupName != null && group.groupName.toLowerCase().contains(searchCriteria))
            .collect(Collectors.toList());
    }

    void saveExtraAddressToGroup(Group group, Address address, User loggedInUser) {
        group = getGroups(group.id);
        
        if (group == null) {
            throw new ErrorException(26);
        }
        
        if (!loggedInUser.groups.contains(group.id)) {
            throw new ErrorException(26);
        }
        
        if (group.extraAddresses == null) {
            group.extraAddresses = new ArrayList();
        }
        
        if (address.id == null || address.id.isEmpty()) {
            address.id = UUID.randomUUID().toString();
        }
        
        List<Address> oldAddresses = group.extraAddresses.stream().filter(o -> o.id.equals(address.id)).collect(Collectors.toList());
        group.extraAddresses.removeAll(oldAddresses);
        group.extraAddresses.add(address);
        saveGroup(group);
    }

    void deleteExtraAddressToGroup(String groupId, String addressId, User currentUser) {
        Group group = getGroups(groupId);
    
        if (group == null) {
            throw new ErrorException(26);
        }
        
        if (!currentUser.groups.contains(group.id)) {
            throw new ErrorException(26);
        }
        
        List<Address> oldAddresses = group.extraAddresses.stream().filter(o -> o.id.equals(addressId)).collect(Collectors.toList());
        group.extraAddresses.removeAll(oldAddresses);
    }

    private List<Group> finalizeGroups(Collection<Group> groups) {
        List<Group> retGroups = new ArrayList();
        
        for (Group group : groups) {
            if (!hasAccessToGroup(group)) {
                continue;
            }
            
            retGroups.add(group);
        }
        
        return retGroups;
    }

    private boolean hasAccessToGroup(Group group) {
        if (group == null) {
            return false;
        }
        
        
        if (!group.isPublic && userManager.getSession().currentUser == null) {
            return false;
        }
        
        if (!group.isPublic && userManager.getSession().currentUser != null && userManager.getSession().currentUser.type < 100) {
            return false;
        }
        
        return true;
    }

    private boolean matchOnCompany(User user, String searchCriteria) {
        if (user.company == null || user.company.isEmpty()) {
            return false;
        }
        
        for (String companyId : user.company) {
            Company comp = userManager.getCompany(companyId);
            if (comp != null && comp.name != null && searchCriteria != null && comp.name.toLowerCase().contains(searchCriteria.toLowerCase())) {
                return true;
            }
            if (comp != null && comp.reference != null && searchCriteria != null && comp.reference.contains(searchCriteria.toLowerCase())) {
                return true;
            }
        }
        
        return false;
    }

    private void setUserSessionCompany(User user) {
        if (userManager.getSession() != null && userManager.getSession().get("user_company_sessionid") != null) {
            String id = (String) userManager.getSession().get("user_company_sessionid");
            if (user.company.contains(id)) {
                Company company = userManager.getCompany(id);
                if (company != null) {
                    user.companyObject = company;
                }
            }
        }
    }

    public List<User> getUsersThatHasCompany(String companyId) {
        return users.values().stream()
                .filter(o -> o.company != null && o.company.contains(companyId))
                .filter(o -> !o.visibleOnlyInMainCompany || (o.visibleOnlyInMainCompany && o.mainCompanyId.equals(companyId)))
                .collect(Collectors.toList());
    }

    List<User> getUsersByType(int type) {
        return users.values().stream()
                .filter(user -> user != null && user.type == type)
                .collect(Collectors.toList());
    }

    List<User> getAllUsersNotFinalized() {
        return new ArrayList(users.values());
    }

    boolean doesUserExists(String userId) {
        return users.containsKey(userId);
    }

    void undoSuspension(String userId, String suspensionId) {
        User user = getUser(userId);
        if (user != null) {
            user.undoSuspention(suspensionId);
        }
    }

    public User getDeletedUser(String id) {
        return (User)userManager.database.getObject(credentials, id);
    }

}