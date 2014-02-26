package com.thundashop.core.usermanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.Writing;
import com.thundashop.core.getshop.data.GetshopStore;
import com.thundashop.core.usermanager.data.Comment;
import com.thundashop.core.usermanager.data.Group;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.UserPrivilege;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@GetShopApi

/**
 * This is the main engine for handling users to your webshop.<br>
 * From this area you can create,modify and delete users.<br>
 * Check if they are logged on, reset their password, etc.<br>
 */

public interface IUserManager {
    /**
     * Create a new user to your webshop.<br>
     * This will fail if you are trying to create a user which is granted more access then you have yourself.<br>
     * If no users has been created, then the user object will automatically be set as an administrator.<br>
     * That is how you create your first user, set the User.type field to 0.
     * @param user The new user to be created. and the password is sent as plain text.
     * @return
     * @throws ErrorException 
     */
    public User createUser(User user) throws ErrorException;
    
    public List<GetshopStore> getStoresConnectedToMe() throws ErrorException;
    
    /**
     * Find all users with a given search criteria.
     * @param searchCriteria The criteria to search for
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<User> findUsers(String searchCriteria) throws ErrorException;
    
    /**
     * Logon a given user.
     * @param email The username to use when logging on, an also be the users email.
     * @param password The password for this user in plain text.
     * @return
     * @throws ErrorException 
     */
    public User logOn(String username, String password) throws ErrorException;
    
    
    /**
     * Sometimes it is needed for someone to logon using a generated key instead.<br>
     * The key is unique and attached to the user trying to logon.<br>
     * Whenever someone logs on using the key,<br> it will automatically be removed, this it is only valid once.<br>
     * @param logonKey A unique key identifying the user which is trying to logon.
     * @return
     * @throws ErrorException 
     */
    public User logonUsingKey(String logonKey) throws ErrorException;
    
    /**
     * Logout the currently logged on user.
     * @return
     * @throws ErrorException 
     */
    public void logout() throws ErrorException;
    
    /**
     * Check if this session is logged on or not.
     * @return
     * @throws ErrorException 
     */
    public boolean isLoggedIn() throws ErrorException;
    
    /**
     * Fetch all users with the given user ids.
     * @param userIds A list of user ids.
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<User> getUserList(ArrayList<String> userIds) throws ErrorException;
    
    /**
     * Fetch all the users registered to this webshop.
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<User> getAllUsers() throws ErrorException;
    
    /**
     * Update a given user.<br>
     * This will fail if you are trying to update a user which is granted more access then you have yourself.
     * @param user You can not change the password, use updatePassword to change the password.
     * @return
     * @throws ErrorException 
     */
    public void saveUser(User user) throws ErrorException;
    
    /**
     * If you need to reset the password for a given user, you need fetch a reset code by calling this call.
     * @param title The title of the message to attach to the reset code.
     * @param text The text to attach to the mail being sent with the reset code.
     * @param username The username to identify the user, the email address is the most common username.
     * @return
     * @throws ErrorException 
     */
    public void sendResetCode(String title, String text, String username) throws ErrorException;
    
    /**
     * When the reset code has been sent, you can reset your password with the given reset code.
     * @param resetCode The code sent by sendResetCode call.
     * @param username The username for the user to update, the email address is the most common username.
     * @param newPassword The new password to send as plain text.
     * @return
     * @throws ErrorException 
     */
    public void resetPassword(Integer resetCode, String username, String newPassword) throws ErrorException;
    
    /**
     * Delete a registered user.
     * This will fail if you are trying to create a user which is granted more access then you have yourself.
     * @param userId The id for the user to delete.
     * @return
     * @throws ErrorException 
     */
    public void deleteUser(String userId) throws ErrorException;

    /**
     * Fetch the currently logged on user.
     * @return 
     */
    public User getLoggedOnUser() throws ErrorException;
    
    /**
     * Fetch a user 
     * @param id
     * @return
     * @throws ErrorException 
     */
    
    public User getUserById(String id) throws ErrorException;
    
    /**
     * Check if the user is a real star trek hero!
     * @param id The id of the user to check on.
     * @return
     * @throws ErrorException 
     */
    public boolean isCaptain(String id) throws ErrorException;
    
    /**
     * 
     * @param userId The id for the user to modify.
     * @param oldPassword The old password as plain text, if you have a userlevel above, the oldpassword will be ignored.
     * @param newPassword The new password as plain text.
     * @throws ErrorException 
     */
    public void updatePassword(String userId, String oldPassword, String newPassword) throws ErrorException;
    
    /**
     * Gets the count of how many customers 
     * is available for the page
     * @return 
     */
    @Administrator
    public int getCustomersCount() throws ErrorException;
    
    /**
     * Gets the count of how many editors
     * is available for the page
     * @return 
     */
    @Administrator
    public int getEditorCount() throws ErrorException;
    
    /**
     * Gets the count of how many adminsitrators
     * is available for the page
     * @return 
     */
    @Administrator
    public int getAdministratorCount() throws ErrorException;
    
    /**
     * Create a new group. 
     * A group is a way to collect all users
     * together. If an administrator belongs to a 
     * group, it will only be able to see/modify the
     * users that are within the same group.
     * 
     * @param groupName
     * @param imageId
     * @throws ErrorException 
     */
    @Administrator
    public void saveGroup(Group group) throws ErrorException;
    
    /**
     * Returns all the groups
     * that has been created for this
     * webpage.
     * 
     * @return 
     */
    public List<Group> getAllGroups() throws ErrorException;
    
    /**
     * Check if a user already exists with the given email.
     * @param email The email used when registering.
     * @return
     * @throws ErrorException 
     */
    public boolean doEmailExists(String email) throws ErrorException;
    
    /**
     * Delete a specified group.
     * 
     * @param groupId
     * @throws ErrorException 
     */
    @Administrator
    public void removeGroup(String groupId) throws ErrorException;
    
    /**
     * This function will return a user new admin user that has access to only invoke the function
     * specified in the paramters.
     * 
     * The password field on the user will be in cleartext so it can be saved by the application
     * that request this feature.
     * 
     * @param managerName
     * @param managerFunction
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public User requestAdminRight(String managerName, String managerFunction, String applicationInstanceId) throws ErrorException;
    
    /**
     * Add priviliges to a another admin user.
     * 
     * If a user is given a privilege, all the defaults are removed.
     * @param userId
     * @param managerName
     * @param managerFunction
     * @throws ErrorException 
     */
    @Administrator
    public void addUserPrivilege(String userId, String managerName, String managerFunction) throws ErrorException;
    
    /**
     * Add a comment to a specific user.
     * 
     * @param userId
     * @param comment
     * @throws ErrorException 
     */
    @Editor
    @Writing
    public void addComment(String userId, Comment comment) throws ErrorException;
    
    /**
     * Removes the comment from a user
     * @param userId
     * @param commentId
     * @throws ErrorException 
     */
    @Editor
    @Writing
    public void removeComment(String userId, String commentId) throws ErrorException;

    /**
     * Find all users that has one or more comments
     * connected to the specified appId.
     * 
     * @param appId
     * @return 
     */
    @Editor
    public List<User> getAllUsersWithCommentToApp(String appId) throws ErrorException;
}