package com.getshop.javaapi;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
public class APIStoreManager {

      public Transporter transport;

      public APIStoreManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * When an administrator has logged on, it can call on this call to connect its store to a partner.
     */
     public void connectStoreToPartner(java.lang.String partner)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("partner",new Gson().toJson(partner));
          data.method = "connectStoreToPartner";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
     }

     /**
     * Create a new store / webshop with a given name.
     * @param hostname The hostname to the webshop.
     * @param email The email to identify the first user with,
     * @param password The password to logon the first user added to this webshop.
     * @param notify Notify the web shop owner by email about this new store.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.storemanager.data.Store createStore(java.lang.String hostname, java.lang.String email, java.lang.String password, boolean notify)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("hostname",new Gson().toJson(hostname));
          data.args.put("email",new Gson().toJson(email));
          data.args.put("password",new Gson().toJson(password));
          data.args.put("notify",new Gson().toJson(notify));
          data.method = "createStore";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.storemanager.data.Store>() {}.getType();
          com.thundashop.core.storemanager.data.Store object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Enable extended support for this webshop.
     * Extended mode is a more advanced version of the ui where there is no limitation to what can be created / made.
     * @param toggle True or false depending if this webshop should have access to the extended mode.
     * @param password A password given by getshop to toggle this option.
     * @return
     * @throws ErrorException
     */
     public void delete()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "delete";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
     }

     /**
     * Enable extended support for this webshop.
     * Extended mode is a more advanced version of the ui where there is no limitation to what can be created / made.
     * @param toggle True or false depending if this webshop should have access to the extended mode.
     * @param password A password given by getshop to toggle this option.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.storemanager.data.Store enableExtendedMode(boolean toggle, java.lang.String password)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("toggle",new Gson().toJson(toggle));
          data.args.put("password",new Gson().toJson(password));
          data.method = "enableExtendedMode";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.storemanager.data.Store>() {}.getType();
          com.thundashop.core.storemanager.data.Store object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Enable support to send sms for this webshop.
     * This option is not free since there is a cost for each sms sent.
     * @param toggle true or false depending on if this webshop should have access to sms.
     * @param password A password given by getshop to toggle this option.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.storemanager.data.Store enableSMSAccess(boolean toggle, java.lang.String password)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("toggle",new Gson().toJson(toggle));
          data.args.put("password",new Gson().toJson(password));
          data.method = "enableSMSAccess";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.storemanager.data.Store>() {}.getType();
          com.thundashop.core.storemanager.data.Store object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get the store added to this session.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.storemanager.data.Store getMyStore()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getMyStore";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.storemanager.data.Store>() {}.getType();
          com.thundashop.core.storemanager.data.Store object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch the store id identified to this user.
     * @return The store id
     * @throws ErrorException
     */
     public java.lang.String getStoreId()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getStoreId";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.lang.String>() {}.getType();
          java.lang.String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Initializing this api. This will identify the webshop and will act as the root for everything in this api.
     * @param initSessionId The session id to identify to this user.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.storemanager.data.Store initializeStore(java.lang.String webAddress, java.lang.String initSessionId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("webAddress",new Gson().toJson(webAddress));
          data.args.put("initSessionId",new Gson().toJson(initSessionId));
          data.method = "initializeStore";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.storemanager.data.Store>() {}.getType();
          com.thundashop.core.storemanager.data.Store object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Check if a web shop address has already been taken.
     * @param address The address to check for.
     * @throws ErrorException
     */
     public boolean isAddressTaken(java.lang.String address)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("address",new Gson().toJson(address));
          data.method = "isAddressTaken";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Boolean>() {}.getType();
          Boolean object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Remove an already added domain name.
     * @param domainName The domain name to remove.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.storemanager.data.Store removeDomainName(java.lang.String domainName)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("domainName",new Gson().toJson(domainName));
          data.method = "removeDomainName";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.storemanager.data.Store>() {}.getType();
          com.thundashop.core.storemanager.data.Store object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Update the current store with new configuration data.
     * @param config The configuration data to update.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.storemanager.data.Store saveStore(com.thundashop.core.storemanager.data.StoreConfiguration config)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("config",new Gson().toJson(config));
          data.method = "saveStore";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.storemanager.data.Store>() {}.getType();
          com.thundashop.core.storemanager.data.Store object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * This option will enable / disable the deepfreeze mode.
     * if a websolution is set to deepfreeze, it will automatically be
     * reverted to the original state each hour. No options will be stored.
     *
     * @param mode - true / false
     */
     public void setDeepFreeze(boolean mode, java.lang.String password)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("mode",new Gson().toJson(mode));
          data.args.put("password",new Gson().toJson(password));
          data.method = "setDeepFreeze";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
     }

     /**
     * This will set the readintroduction variable in the Store object to true.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.storemanager.data.Store setIntroductionRead()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "setIntroductionRead";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.storemanager.data.Store>() {}.getType();
          com.thundashop.core.storemanager.data.Store object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Set a new domain name to this store / webshop
     * @param domainName The domain name to identify this shop with.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.storemanager.data.Store setPrimaryDomainName(java.lang.String domainName)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("domainName",new Gson().toJson(domainName));
          data.method = "setPrimaryDomainName";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.storemanager.data.Store>() {}.getType();
          com.thundashop.core.storemanager.data.Store object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Is this a very important shop or not?
     *
     * @param toggle True / False
     * @param password And internal password needed to toggle this option.
     * @throws ErrorException
     */
     public void setVIS(boolean toggle, java.lang.String password)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("toggle",new Gson().toJson(toggle));
          data.args.put("password",new Gson().toJson(password));
          data.method = "setVIS";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
     }

}
