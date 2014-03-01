package com.getshop.javaapi;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
public class APIUserManager {

      public Transporter transport;

      public APIUserManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * Add a comment to a specific user.
     *
     * @param userId
     * @param comment
     * @throws ErrorException
     */
     public void addComment(java.lang.String userId, com.thundashop.core.usermanager.data.Comment comment)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.args.put("comment",new Gson().toJson(comment));
          data.method = "addComment";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
     }

     /**
     * Add priviliges to a another admin user.
     *
     * If a user is given a privilege, all the defaults are removed.
     * @param userId
     * @param managerName
     * @param managerFunction
     * @throws ErrorException
     */
     public void addUserPrivilege(java.lang.String userId, java.lang.String managerName, java.lang.String managerFunction)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.args.put("managerName",new Gson().toJson(managerName));
          data.args.put("managerFunction",new Gson().toJson(managerFunction));
          data.method = "addUserPrivilege";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
     }

     /**
     * Create a new user to your webshop.<br>
     * This will fail if you are trying to create a user which is granted more access then you have yourself.<br>
     * If no users has been created, then the user object will automatically be set as an administrator.<br>
     * That is how you create your first user, set the User.type field to 0.
     * @param user The new user to be created. and the password is sent as plain text.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.usermanager.data.User createUser(com.thundashop.core.usermanager.data.User user)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("user",new Gson().toJson(user));
          data.method = "createUser";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.usermanager.data.User>() {}.getType();
          com.thundashop.core.usermanager.data.User object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Delete a registered user.
     * This will fail if you are trying to create a user which is granted more access then you have yourself.
     * @param userId The id for the user to delete.
     * @return
     * @throws ErrorException
     */
     public void deleteUser(java.lang.String userId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.method = "deleteUser";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
     }

     /**
     * Check if a user already exists with the given email.
     * @param email The email used when registering.
     * @return
     * @throws ErrorException
     */
     public boolean doEmailExists(java.lang.String email)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("email",new Gson().toJson(email));
          data.method = "doEmailExists";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Boolean>() {}.getType();
          Boolean object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Find all users with a given search criteria.
     * @param searchCriteria The criteria to search for
     * @return
     * @throws ErrorException
     */
     public java.util.List findUsers(java.lang.String searchCriteria)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("searchCriteria",new Gson().toJson(searchCriteria));
          data.method = "findUsers";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.usermanager.data.User>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Gets the count of how many adminsitrators
     * is available for the page
     * @return
     */
     public int getAdministratorCount()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getAdministratorCount";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Integer>() {}.getType();
          Integer object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns all the groups
     * that has been created for this
     * webpage.
     *
     * @return
     */
     public java.util.List getAllGroups()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getAllGroups";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.usermanager.data.Group>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch all the users registered to this webshop.
     * @return
     * @throws ErrorException
     */
     public java.util.List getAllUsers()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getAllUsers";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.usermanager.data.User>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Find all users that has one or more comments
     * connected to the specified appId.
     *
     * @param appId
     * @return
     */
     public java.util.List getAllUsersWithCommentToApp(java.lang.String appId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("appId",new Gson().toJson(appId));
          data.method = "getAllUsersWithCommentToApp";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.usermanager.data.User>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Gets the count of how many customers
     * is available for the page
     * @return
     */
     public int getCustomersCount()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getCustomersCount";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Integer>() {}.getType();
          Integer object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Gets the count of how many editors
     * is available for the page
     * @return
     */
     public int getEditorCount()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getEditorCount";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Integer>() {}.getType();
          Integer object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch the currently logged on user.
     * @return
     */
     public com.thundashop.core.usermanager.data.User getLoggedOnUser()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getLoggedOnUser";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.usermanager.data.User>() {}.getType();
          com.thundashop.core.usermanager.data.User object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Create a new user to your webshop.<br>
     * This will fail if you are trying to create a user which is granted more access then you have yourself.<br>
     * If no users has been created, then the user object will automatically be set as an administrator.<br>
     * That is how you create your first user, set the User.type field to 0.
     * @param user The new user to be created. and the password is sent as plain text.
     * @return
     * @throws ErrorException
     */
     public java.util.List getStoresConnectedToMe()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getStoresConnectedToMe";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.getshop.data.GetshopStore>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch a user
     * @param id
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.usermanager.data.User getUserById(java.lang.String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "getUserById";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.usermanager.data.User>() {}.getType();
          com.thundashop.core.usermanager.data.User object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch all users with the given user ids.
     * @param userIds A list of user ids.
     * @return
     * @throws ErrorException
     */
     public java.util.List getUserList(java.util.ArrayList userIds)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userIds",new Gson().toJson(userIds));
          data.method = "getUserList";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.usermanager.data.User>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Check if the user is a real star trek hero!
     * @param id The id of the user to check on.
     * @return
     * @throws ErrorException
     */
     public boolean isCaptain(java.lang.String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "isCaptain";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Boolean>() {}.getType();
          Boolean object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Check if this session is logged on or not.
     * @return
     * @throws ErrorException
     */
     public boolean isLoggedIn()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "isLoggedIn";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Boolean>() {}.getType();
          Boolean object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Logon a given user.
     * @param email The username to use when logging on, an also be the users email.
     * @param password The password for this user in plain text.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.usermanager.data.User logOn(java.lang.String username, java.lang.String password)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("username",new Gson().toJson(username));
          data.args.put("password",new Gson().toJson(password));
          data.method = "logOn";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.usermanager.data.User>() {}.getType();
          com.thundashop.core.usermanager.data.User object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Sometimes it is needed for someone to logon using a generated key instead.<br>
     * The key is unique and attached to the user trying to logon.<br>
     * Whenever someone logs on using the key,<br> it will automatically be removed, this it is only valid once.<br>
     * @param logonKey A unique key identifying the user which is trying to logon.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.usermanager.data.User logonUsingKey(java.lang.String logonKey)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("logonKey",new Gson().toJson(logonKey));
          data.method = "logonUsingKey";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.usermanager.data.User>() {}.getType();
          com.thundashop.core.usermanager.data.User object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Logout the currently logged on user.
     * @return
     * @throws ErrorException
     */
     public void logout()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "logout";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
     }

     /**
     * Removes the comment from a user
     * @param userId
     * @param commentId
     * @throws ErrorException
     */
     public void removeComment(java.lang.String userId, java.lang.String commentId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.args.put("commentId",new Gson().toJson(commentId));
          data.method = "removeComment";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
     }

     /**
     * Delete a specified group.
     *
     * @param groupId
     * @throws ErrorException
     */
     public void removeGroup(java.lang.String groupId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("groupId",new Gson().toJson(groupId));
          data.method = "removeGroup";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
     }

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
     public com.thundashop.core.usermanager.data.User requestAdminRight(java.lang.String managerName, java.lang.String managerFunction, java.lang.String applicationInstanceId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("managerName",new Gson().toJson(managerName));
          data.args.put("managerFunction",new Gson().toJson(managerFunction));
          data.args.put("applicationInstanceId",new Gson().toJson(applicationInstanceId));
          data.method = "requestAdminRight";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.usermanager.data.User>() {}.getType();
          com.thundashop.core.usermanager.data.User object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * When the reset code has been sent, you can reset your password with the given reset code.
     * @param resetCode The code sent by sendResetCode call.
     * @param username The username for the user to update, the email address is the most common username.
     * @param newPassword The new password to send as plain text.
     * @return
     * @throws ErrorException
     */
     public void resetPassword(java.lang.Integer resetCode, java.lang.String username, java.lang.String newPassword)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("resetCode",new Gson().toJson(resetCode));
          data.args.put("username",new Gson().toJson(username));
          data.args.put("newPassword",new Gson().toJson(newPassword));
          data.method = "resetPassword";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
     }

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
     public void saveGroup(com.thundashop.core.usermanager.data.Group group)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("group",new Gson().toJson(group));
          data.method = "saveGroup";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
     }

     /**
     * Update a given user.<br>
     * This will fail if you are trying to update a user which is granted more access then you have yourself.
     * @param user You can not change the password, use updatePassword to change the password.
     * @return
     * @throws ErrorException
     */
     public void saveUser(com.thundashop.core.usermanager.data.User user)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("user",new Gson().toJson(user));
          data.method = "saveUser";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
     }

     /**
     * If you need to reset the password for a given user, you need fetch a reset code by calling this call.
     * @param title The title of the message to attach to the reset code.
     * @param text The text to attach to the mail being sent with the reset code.
     * @param username The username to identify the user, the email address is the most common username.
     * @return
     * @throws ErrorException
     */
     public void sendResetCode(java.lang.String title, java.lang.String text, java.lang.String username)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("title",new Gson().toJson(title));
          data.args.put("text",new Gson().toJson(text));
          data.args.put("username",new Gson().toJson(username));
          data.method = "sendResetCode";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
     }

     /**
     *
     * @param userId The id for the user to modify.
     * @param oldPassword The old password as plain text, if you have a userlevel above, the oldpassword will be ignored.
     * @param newPassword The new password as plain text.
     * @throws ErrorException
     */
     public void updatePassword(java.lang.String userId, java.lang.String oldPassword, java.lang.String newPassword)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.args.put("oldPassword",new Gson().toJson(oldPassword));
          data.args.put("newPassword",new Gson().toJson(newPassword));
          data.method = "updatePassword";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
     }

}
