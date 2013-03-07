package com.thundashop.api.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import com.thundashop.core.common.JsonObject2;
import java.util.ArrayList;
import java.util.List;
import com.thundashop.core.usermanager.data.User;

public class APIUserManager {

      private Transporter transport;

      public APIUserManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * Create a new user to your webshop.<br>
     * This will fail if you are trying to create a user which is granted more access then you have yourself.<br>
     * If no users has been created, then the user object will automatically be set as an administrator.<br>
     * That is how you create your first user, set the User.type field to 0.
     * @param user The new user to be created. and the password is sent as plain text.
     * @return User
     * @throws ErrorException 
     */

     public User createUser(User user)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("user",new Gson().toJson(user));
          data.method = "createUser";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<User>() {}.getType();
          User object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Delete a registered user.
     * This will fail if you are trying to create a user which is granted more access then you have yourself.
     * @param userId The id for the user to delete.
     * @return void
     * @throws ErrorException 
     */

     public void deleteUser(String userId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.method = "deleteUser";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
     }

     /**
     * Find all users with a given search criteria.
     * @param searchCriteria The criteria to search for
     * @return List
     * @throws ErrorException 
     */

     public List findUsers(String searchCriteria)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("searchCriteria",new Gson().toJson(searchCriteria));
          data.method = "findUsers";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<List>() {}.getType();
          List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch all the users registered to this webshop.
     * @return List
     * @throws ErrorException 
     */

     public List getAllUsers()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "getAllUsers";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<List>() {}.getType();
          List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch the currently logged on user.
     * @return User
     */

     public User getLoggedOnUser()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "getLoggedOnUser";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<User>() {}.getType();
          User object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch a user 
     * @param id
     * @return User
     * @throws ErrorException 
     */

     public User getUserById(String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "getUserById";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<User>() {}.getType();
          User object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch all users with the given user ids.
     * @param userIds A list of user ids.
     * @return List
     * @throws ErrorException 
     */

     public List getUserList(ArrayList<String> userIds)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("userIds",new Gson().toJson(userIds));
          data.method = "getUserList";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<List>() {}.getType();
          List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Check if the user is a real star trek hero!
     * @param id The id of the user to check on.
     * @return boolean
     * @throws ErrorException 
     */

     public boolean isCaptain(String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
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
     * @return boolean
     * @throws ErrorException 
     */

     public boolean isLoggedIn()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
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
     * @return User
     * @throws ErrorException 
     */

     public User logOn(String username, String password)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("username",new Gson().toJson(username));
          data.args.put("password",new Gson().toJson(password));
          data.method = "logOn";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<User>() {}.getType();
          User object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Sometimes it is needed for someone to logon using a generated key instead.<br>
     * The key is unique and attached to the user trying to logon.<br>
     * Whenever someone logs on using the key,<br> it will automatically be removed, this it is only valid once.<br>
     * @param logonKey A unique key identifying the user which is trying to logon.
     * @return User
     * @throws ErrorException 
     */

     public User logonUsingKey(String logonKey)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("logonKey",new Gson().toJson(logonKey));
          data.method = "logonUsingKey";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<User>() {}.getType();
          User object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Logout the currently logged on user.
     * @return void
     * @throws ErrorException 
     */

     public void logout()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "logout";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
     }

     /**
     * When the reset code has been sent, you can reset your password with the given reset code.
     * @param resetCode The code sent by sendResetCode call.
     * @param email The email for the user to update.
     * @param newPassword The new password to send as plain text.
     * @return void
     * @throws ErrorException 
     */

     public void resetPassword(Integer resetCode, String email, String newPassword)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("resetCode",new Gson().toJson(resetCode));
          data.args.put("email",new Gson().toJson(email));
          data.args.put("newPassword",new Gson().toJson(newPassword));
          data.method = "resetPassword";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
     }

     /**
     * Update a given user.<br>
     * This will fail if you are trying to update a user which is granted more access then you have yourself.
     * @param user You can not change the password, use updatePassword to change the password.
     * @return void
     * @throws ErrorException 
     */

     public void saveUser(User user)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("user",new Gson().toJson(user));
          data.method = "saveUser";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
     }

     /**
     * If you need to reset the password for a given user, you need fetch a reset code by calling this call.
     * @param title The title of the message to attach to the reset code.
     * @param text The text to attach to the mail being sent with the reset code.
     * @param email The email to identify the user.
     * @return void
     * @throws ErrorException 
     */

     public void sendResetCode(String title, String text, String email)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("title",new Gson().toJson(title));
          data.args.put("text",new Gson().toJson(text));
          data.args.put("email",new Gson().toJson(email));
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

     public void updatePassword(String userId, String oldPassword, String newPassword)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.args.put("oldPassword",new Gson().toJson(oldPassword));
          data.args.put("newPassword",new Gson().toJson(newPassword));
          data.method = "updatePassword";
          data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(data);
     }

}
