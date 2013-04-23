package com.thundashop.api.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.storemanager.data.StoreConfiguration;

public class APIStoreManager {

      private Transporter transport;

      public APIStoreManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * Create a new store / webshop with a given name.
     * @param hostname The hostname to the webshop.
     * @param email The email to identify the first user with,
     * @param password The password to logon the first user added to this webshop.
     * @return Store
     * @throws ErrorException 
     */

     public Store createStore(String hostname, String email, String password)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("hostname",new Gson().toJson(hostname));
          data.args.put("email",new Gson().toJson(email));
          data.args.put("password",new Gson().toJson(password));
          data.method = "createStore";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Store>() {}.getType();
          Store object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Enable extended support for this webshop.
     * Extended mode is a more advanced version of the ui where there is no limitation to what can be created / made.
     * @param toggle True or false depending if this webshop should have access to the extended mode.
     * @param password A password given by getshop to toggle this option.
     * @return Store
     * @throws ErrorException 
     */

     public Store enableExtendedMode(boolean toggle, String password)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("toggle",new Gson().toJson(toggle));
          data.args.put("password",new Gson().toJson(password));
          data.method = "enableExtendedMode";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Store>() {}.getType();
          Store object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Enable support to send sms for this webshop.
     * This option is not free since there is a cost for each sms sent.
     * @param toggle true or false depending on if this webshop should have access to sms.
     * @param password A password given by getshop to toggle this option.
     * @return Store
     * @throws ErrorException 
     */

     public Store enableSMSAccess(boolean toggle, String password)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("toggle",new Gson().toJson(toggle));
          data.args.put("password",new Gson().toJson(password));
          data.method = "enableSMSAccess";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Store>() {}.getType();
          Store object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get the store added to this session.
     * @return Store
     * @throws ErrorException 
     */

     public Store getMyStore()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "getMyStore";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Store>() {}.getType();
          Store object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch the store id identified to this user.
     * @return String
     * @throws ErrorException 
     */

     public String getStoreId()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "getStoreId";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<String>() {}.getType();
          String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Initializing this api. This will identify the webshop and will act as the root for everything in this api.
     * @param initSessionId The session id to identify to this user.
     * @return Store
     * @throws ErrorException 
     */

     public Store initializeStore(String webAddress, String initSessionId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("webAddress",new Gson().toJson(webAddress));
          data.args.put("initSessionId",new Gson().toJson(initSessionId));
          data.method = "initializeStore";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Store>() {}.getType();
          Store object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Check if a web shop address has already been taken.
     * @param address The address to check for.
     * @throws ErrorException 
     */

     public boolean isAddressTaken(String address)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
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
     * @return Store
     * @throws ErrorException 
     */

     public Store removeDomainName(String domainName)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("domainName",new Gson().toJson(domainName));
          data.method = "removeDomainName";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Store>() {}.getType();
          Store object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Update the current store with new configuration data.
     * @param config The configuration data to update.
     * @return Store
     * @throws ErrorException 
     */

     public Store saveStore(StoreConfiguration config)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("config",new Gson().toJson(config));
          data.method = "saveStore";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Store>() {}.getType();
          Store object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * This will set the readintroduction variable in the Store object to true.
     * @return Store
     * @throws ErrorException 
     */

     public Store setIntroductionRead()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "setIntroductionRead";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Store>() {}.getType();
          Store object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Set a new domain name to this store / webshop
     * @param domainName The domain name to identify this shop with.
     * @return Store
     * @throws ErrorException 
     */

     public Store setPrimaryDomainName(String domainName)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("domainName",new Gson().toJson(domainName));
          data.method = "setPrimaryDomainName";
          data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Store>() {}.getType();
          Store object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

}
