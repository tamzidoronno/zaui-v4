package com.getshop.javaapi.pos;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIUserManager {

      public Communicator transport;

      public APIUserManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Add a comment to a specific user.
     *
     * @param userId
     * @param comment
     * @throws ErrorException
     */
     public void addComment(Object userId, Object comment)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("comment",new Gson().toJson(comment));
          gs_json_object_data.method = "addComment";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void addGroupToUser(Object userId, Object groupId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.method = "addGroupToUser";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void addMetaData(Object userId, Object key, Object value)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("key",new Gson().toJson(key));
          gs_json_object_data.args.put("value",new Gson().toJson(value));
          gs_json_object_data.method = "addMetaData";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
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
     public void addUserPrivilege(Object userId, Object managerName, Object managerFunction)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("managerName",new Gson().toJson(managerName));
          gs_json_object_data.args.put("managerFunction",new Gson().toJson(managerFunction));
          gs_json_object_data.method = "addUserPrivilege";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void assignCompanyToGroup(Object company, Object groupId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("company",new Gson().toJson(company));
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.method = "assignCompanyToGroup";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void assignCompanyToUser(Object company, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("company",new Gson().toJson(company));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "assignCompanyToUser";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void assignMetaDataToVirtualSessionUser(Object key, Object value)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("key",new Gson().toJson(key));
          gs_json_object_data.args.put("value",new Gson().toJson(value));
          gs_json_object_data.method = "assignMetaDataToVirtualSessionUser";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void assignReferenceToCompany(Object companyId, Object companyReference)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("companyId",new Gson().toJson(companyId));
          gs_json_object_data.args.put("companyReference",new Gson().toJson(companyReference));
          gs_json_object_data.method = "assignReferenceToCompany";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Check if its possible to create the user
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement canCreateUser(Object user)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("user",new Gson().toJson(user));
          gs_json_object_data.method = "canCreateUser";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Cancel the impersonation of a user.
     *
     * @throws ErrorException
     */
     public void cancelImpersonating()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "cancelImpersonating";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement changeUserByUsingPinCode(Object userId, Object pinCode)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("pinCode",new Gson().toJson(pinCode));
          gs_json_object_data.method = "changeUserByUsingPinCode";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement checkIfFieldOnUserIsOkey(Object field, Object value)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("field",new Gson().toJson(field));
          gs_json_object_data.args.put("value",new Gson().toJson(value));
          gs_json_object_data.method = "checkIfFieldOnUserIsOkey";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement checkUserNameAndPassword(Object username, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("username",new Gson().toJson(username));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "checkUserNameAndPassword";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void clearTokenList()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "clearTokenList";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void clearUserManagerForAllData()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "clearUserManagerForAllData";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void confirmCompanyOwner(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "confirmCompanyOwner";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Logon a given user.
     * @param email The username to use when logging on, an also be the users email.
     * @param password The password for this user in plain text.
     * @return
     * @throws ErrorException
     */
     public void connectCompanyToUser(Object userId, Object taxNumber)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("taxNumber",new Gson().toJson(taxNumber));
          gs_json_object_data.method = "connectCompanyToUser";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement createCompany(Object vatNumber, Object name)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("vatNumber",new Gson().toJson(vatNumber));
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.method = "createCompany";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void createGoogleTotpForUser(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "createGoogleTotpForUser";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement createTokenAccess()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "createTokenAccess";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement createUser(Object user)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("user",new Gson().toJson(user));
          gs_json_object_data.method = "createUser";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement createUserAndCompany(Object company)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("company",new Gson().toJson(company));
          gs_json_object_data.method = "createUserAndCompany";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void deleteCompany(Object companyId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("companyId",new Gson().toJson(companyId));
          gs_json_object_data.method = "deleteCompany";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void deleteExtraAddressToGroup(Object groupId, Object addressId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.args.put("addressId",new Gson().toJson(addressId));
          gs_json_object_data.method = "deleteExtraAddressToGroup";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Delete a registered user.
     * This will fail if you are trying to create a user which is granted more access then you have yourself.
     * @param userId The id for the user to delete.
     * @return
     * @throws ErrorException
     */
     public void deleteUser(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "deleteUser";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void deleteUserRole(Object roleId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("roleId",new Gson().toJson(roleId));
          gs_json_object_data.method = "deleteUserRole";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Check if a user already exists with the given email.
     * @param email The email used when registering.
     * @return
     * @throws ErrorException
     */
     public JsonElement doEmailExists(Object email)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("email",new Gson().toJson(email));
          gs_json_object_data.method = "doEmailExists";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement doesUserExistsOnReferenceNumber(Object number)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("number",new Gson().toJson(number));
          gs_json_object_data.method = "doesUserExistsOnReferenceNumber";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Find all users with a given search criteria.
     * @param searchCriteria The criteria to search for
     * @return
     * @throws ErrorException
     */
     public JsonElement findUsers(Object searchCriteria)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("searchCriteria",new Gson().toJson(searchCriteria));
          gs_json_object_data.method = "findUsers";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void forceCompanyOwner(Object userId, Object isCompanyOwner)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("isCompanyOwner",new Gson().toJson(isCompanyOwner));
          gs_json_object_data.method = "forceCompanyOwner";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Gets the count of how many adminsitrators
     * is available for the page
     * @return
     */
     public JsonElement getAdministratorCount()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAdministratorCount";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement getAllCompanies()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllCompanies";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement getAllCompaniesForGroup(Object groupId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.method = "getAllCompaniesForGroup";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch all the companies registered to this webshop.
     * @return
     * @throws ErrorException
     */
     public JsonElement getAllCompanyFiltered(Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getAllCompanyFiltered";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns all the groups
     * that has been created for this
     * webpage.
     *
     * @return
     */
     public JsonElement getAllGroups()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllGroups";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Gets a set of filetered groups
     *
     * @param filter
     * @return
     */
     public JsonElement getAllGroupsFiletered(Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getAllGroupsFiletered";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch all the users registered to this webshop.
     * @return
     * @throws ErrorException
     */
     public JsonElement getAllUsers()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllUsers";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch all the users registered to this webshop.
     * @return
     * @throws ErrorException
     */
     public JsonElement getAllUsersFiltered(Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getAllUsersFiltered";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Find all users that has one or more comments
     * connected to the specified appId.
     *
     * @param appId
     * @return
     */
     public JsonElement getAllUsersSimple()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllUsersSimple";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Find all users that has one or more comments
     * connected to the specified appId.
     *
     * @param appId
     * @return
     */
     public JsonElement getAllUsersWithCommentToApp(Object appId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("appId",new Gson().toJson(appId));
          gs_json_object_data.method = "getAllUsersWithCommentToApp";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement getCompaniesConnectedToGroupCount(Object groupId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.method = "getCompaniesConnectedToGroupCount";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement getCompany(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getCompany";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement getCompanyByReference(Object companyReference)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("companyReference",new Gson().toJson(companyReference));
          gs_json_object_data.method = "getCompanyByReference";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Gets the count of how many customers
     * is available for the page
     * @return
     */
     public JsonElement getCustomersCount()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getCustomersCount";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Gets the count of how many editors
     * is available for the page
     * @return
     */
     public JsonElement getEditorCount()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getEditorCount";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement getGroup(Object groupId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.method = "getGroup";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch the currently logged on user.
     * @return
     */
     public JsonElement getLoggedOnUser()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getLoggedOnUser";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Logon a given user.
     * @param email The username to use when logging on, an also be the users email.
     * @param password The password for this user in plain text.
     * @return
     * @throws ErrorException
     */
     public JsonElement getMainCompanyUser(Object companyId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("companyId",new Gson().toJson(companyId));
          gs_json_object_data.method = "getMainCompanyUser";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement getPingoutTime()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getPingoutTime";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Logon a given user.
     * @param email The username to use when logging on, an also be the users email.
     * @param password The password for this user in plain text.
     * @return
     * @throws ErrorException
     */
     public JsonElement getSubUsers(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getSubUsers";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement getTokenList()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getTokenList";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement getUnconfirmedCompanyOwners()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getUnconfirmedCompanyOwners";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch a user
     * @param id
     * @return
     * @throws ErrorException
     */
     public JsonElement getUserById(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getUserById";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch all users with the given user ids.
     * @param userIds A list of user ids.
     * @return
     * @throws ErrorException
     */
     public JsonElement getUserList(Object userIds)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userIds",new Gson().toJson(userIds));
          gs_json_object_data.method = "getUserList";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement getUserRoles()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getUserRoles";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement getUserWithPermissionCheck(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getUserWithPermissionCheck";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement getUsersBasedOnGroupId(Object groupId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.method = "getUsersBasedOnGroupId";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement getUsersByCompanyId(Object companyId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("companyId",new Gson().toJson(companyId));
          gs_json_object_data.method = "getUsersByCompanyId";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch all the users registered to this webshop.
     * @return
     * @throws ErrorException
     */
     public JsonElement getUsersByType(Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "getUsersByType";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement getUsersThatHasPinCode()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getUsersThatHasPinCode";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Switch the context of what user you are logged in as.
     *
     * @throws ErrorException
     */
     public void impersonateUser(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "impersonateUser";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Check if the user is a real star trek hero!
     * @param id The id of the user to check on.
     * @return
     * @throws ErrorException
     */
     public JsonElement isCaptain(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "isCaptain";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement isImpersonating()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "isImpersonating";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Check if this session is logged on or not.
     * @return
     * @throws ErrorException
     */
     public JsonElement isLoggedIn()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "isLoggedIn";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void logLogout()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "logLogout";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Logon a given user.
     * @param email The username to use when logging on, an also be the users email.
     * @param password The password for this user in plain text.
     * @return
     * @throws ErrorException
     */
     public JsonElement logOn(Object username, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("username",new Gson().toJson(username));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "logOn";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement loginWithPincode(Object username, Object password, Object pinCode)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("username",new Gson().toJson(username));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.args.put("pinCode",new Gson().toJson(pinCode));
          gs_json_object_data.method = "loginWithPincode";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement logonUsingKey(Object logonKey)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("logonKey",new Gson().toJson(logonKey));
          gs_json_object_data.method = "logonUsingKey";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement logonUsingRefNumber(Object refCode)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("refCode",new Gson().toJson(refCode));
          gs_json_object_data.method = "logonUsingRefNumber";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement logonUsingToken(Object token)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("token",new Gson().toJson(token));
          gs_json_object_data.method = "logonUsingToken";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement logonUsingTotp(Object username, Object password, Object oneTimeCode)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("username",new Gson().toJson(username));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.args.put("oneTimeCode",new Gson().toJson(oneTimeCode));
          gs_json_object_data.method = "logonUsingTotp";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement logonUsingTotpAgainstCrm(Object username, Object password, Object oneTimeCode)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("username",new Gson().toJson(username));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.args.put("oneTimeCode",new Gson().toJson(oneTimeCode));
          gs_json_object_data.method = "logonUsingTotpAgainstCrm";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Logout the currently logged on user.
     * @return
     * @throws ErrorException
     */
     public void logout()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "logout";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void mergeUsers(Object userIds, Object properties)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userIds",new Gson().toJson(userIds));
          gs_json_object_data.args.put("properties",new Gson().toJson(properties));
          gs_json_object_data.method = "mergeUsers";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Removes the comment from a user
     * @param userId
     * @param commentId
     * @throws ErrorException
     */
     public void removeComment(Object userId, Object commentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("commentId",new Gson().toJson(commentId));
          gs_json_object_data.method = "removeComment";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Delete a specified group.
     *
     * @param groupId
     * @throws ErrorException
     */
     public void removeGroup(Object groupId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.method = "removeGroup";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void removeGroupFromUser(Object userId, Object groupId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.method = "removeGroupFromUser";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void removeMetaData(Object userId, Object key)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("key",new Gson().toJson(key));
          gs_json_object_data.method = "removeMetaData";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void removeUserFromCompany(Object companyId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("companyId",new Gson().toJson(companyId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "removeUserFromCompany";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
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
     public JsonElement requestAdminRight(Object managerName, Object managerFunction, Object applicationInstanceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("managerName",new Gson().toJson(managerName));
          gs_json_object_data.args.put("managerFunction",new Gson().toJson(managerFunction));
          gs_json_object_data.args.put("applicationInstanceId",new Gson().toJson(applicationInstanceId));
          gs_json_object_data.method = "requestAdminRight";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement requestNewPincode(Object username, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("username",new Gson().toJson(username));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "requestNewPincode";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * When the reset code has been sent, you can reset your password with the given reset code.
     * @param resetCode The code sent by sendResetCode call.
     * @param username The username for the user to update, the email address is the most common username.
     * @param newPassword The new password to send as plain text.
     * @return 0 Password updated, 1 user does not exists, 2 Incorrect reset code.
     * @throws ErrorException
     */
     public JsonElement resetPassword(Object resetCode, Object username, Object newPassword)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("resetCode",new Gson().toJson(resetCode));
          gs_json_object_data.args.put("username",new Gson().toJson(username));
          gs_json_object_data.args.put("newPassword",new Gson().toJson(newPassword));
          gs_json_object_data.method = "resetPassword";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement saveCompany(Object company)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("company",new Gson().toJson(company));
          gs_json_object_data.method = "saveCompany";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void saveExtraAddressToGroup(Object group, Object address)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("group",new Gson().toJson(group));
          gs_json_object_data.args.put("address",new Gson().toJson(address));
          gs_json_object_data.method = "saveExtraAddressToGroup";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
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
     public JsonElement saveGroup(Object group)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("group",new Gson().toJson(group));
          gs_json_object_data.method = "saveGroup";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement saveOrCreateCompanyAndUpdatePrimaryUser(Object company)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("company",new Gson().toJson(company));
          gs_json_object_data.method = "saveOrCreateCompanyAndUpdatePrimaryUser";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Update a given user.<br>
     * This will fail if you are trying to update a user which is granted more access then you have yourself.
     * @param user You can not change the password, use updatePassword to change the password.
     * @return
     * @throws ErrorException
     */
     public void saveUser(Object user)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("user",new Gson().toJson(user));
          gs_json_object_data.method = "saveUser";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void saveUserRole(Object role)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("role",new Gson().toJson(role));
          gs_json_object_data.method = "saveUserRole";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement searchForCompanies(Object searchWord)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("searchWord",new Gson().toJson(searchWord));
          gs_json_object_data.method = "searchForCompanies";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement searchForGroup(Object searchCriteria)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("searchCriteria",new Gson().toJson(searchCriteria));
          gs_json_object_data.method = "searchForGroup";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If you need to reset the password for a given user, you need fetch a reset code by calling this call.
     * @param title The title of the message to attach to the reset code.
     * @param text The text to attach to the mail being sent with the reset code.
     * @param username The username to identify the user, the email address is the most common username.
     * @return 0 Everything is ok, email sent, 1. user not found.
     * @throws ErrorException
     */
     public JsonElement sendResetCode(Object title, Object text, Object username)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("title",new Gson().toJson(title));
          gs_json_object_data.args.put("text",new Gson().toJson(text));
          gs_json_object_data.args.put("username",new Gson().toJson(username));
          gs_json_object_data.method = "sendResetCode";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void setSessionCompany(Object companyId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("companyId",new Gson().toJson(companyId));
          gs_json_object_data.method = "setSessionCompany";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Logon a given user.
     * @param email The username to use when logging on, an also be the users email.
     * @param password The password for this user in plain text.
     * @return
     * @throws ErrorException
     */
     public void toggleMainContact(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "toggleMainContact";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void toggleModuleForUser(Object moduleId, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("moduleId",new Gson().toJson(moduleId));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "toggleModuleForUser";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void undoSuspension(Object userId, Object suspensionId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("suspensionId",new Gson().toJson(suspensionId));
          gs_json_object_data.method = "undoSuspension";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @param userId The id for the user to modify.
     * @param oldPassword The old password as plain text, if you have a userlevel above, the oldpassword will be ignored.
     * @param newPassword The new password as plain text.
     * @throws ErrorException
     */
     public void updatePassword(Object userId, Object oldPassword, Object newPassword)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("oldPassword",new Gson().toJson(oldPassword));
          gs_json_object_data.args.put("newPassword",new Gson().toJson(newPassword));
          gs_json_object_data.method = "updatePassword";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement updatePasswordByResetCode(Object resetCode, Object newPassword)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("resetCode",new Gson().toJson(resetCode));
          gs_json_object_data.args.put("newPassword",new Gson().toJson(newPassword));
          gs_json_object_data.method = "updatePasswordByResetCode";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @param userId The id for the user to modify.
     * @param oldPassword The old password as plain text, if you have a userlevel above, the oldpassword will be ignored.
     * @param newPassword The new password as plain text.
     * @throws ErrorException
     */
     public void updatePasswordSecure(Object userId, Object newPassword)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("newPassword",new Gson().toJson(newPassword));
          gs_json_object_data.method = "updatePasswordSecure";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Logon a given user.
     * @param email The username to use when logging on, an also be the users email.
     * @param password The password for this user in plain text.
     * @return
     * @throws ErrorException
     */
     public void updateUserCounter(Object counter, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("counter",new Gson().toJson(counter));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "updateUserCounter";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If an administrator is impersonating a lower user,
     * this function will return true.
     *
     * @return
     * @throws ErrorException
     */
     public void upgradeUserToGetShopAdmin(Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "upgradeUserToGetShopAdmin";
          gs_json_object_data.interfaceName = "core.usermanager.IUserManager";
          String result = transport.send(gs_json_object_data);
     }

}
