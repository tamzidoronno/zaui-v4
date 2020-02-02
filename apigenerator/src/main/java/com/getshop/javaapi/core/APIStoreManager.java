package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIStoreManager {

      public Communicator transport;

      public APIStoreManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Initializing this api. This will identify the webshop and will act as the root for everything in this api.
     * @param initSessionId The session id to identify to this user.
     * @return
     * @throws ErrorException
     */
     public void acceptGDPR()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "acceptGDPR";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * On registration, generate a new id this store, which will become a part of the hostname.
     * @return
     * @throws ErrorException
     */
     public void acceptSlave(Object slaveStoreId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("slaveStoreId",new Gson().toJson(slaveStoreId));
          gs_json_object_data.method = "acceptSlave";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
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
     public JsonElement autoCreateStore(Object hostname)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("hostname",new Gson().toJson(hostname));
          gs_json_object_data.method = "autoCreateStore";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * On registration, generate a new id this store, which will become a part of the hostname.
     * @return
     * @throws ErrorException
     */
     public void changeTimeZone(Object timezone)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("timezone",new Gson().toJson(timezone));
          gs_json_object_data.method = "changeTimeZone";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
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
     public JsonElement createStore(Object hostname, Object email, Object password, Object notify)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("hostname",new Gson().toJson(hostname));
          gs_json_object_data.args.put("email",new Gson().toJson(email));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.args.put("notify",new Gson().toJson(notify));
          gs_json_object_data.method = "createStore";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "delete";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Enable extended support for this webshop.
     * Extended mode is a more advanced version of the ui where there is no limitation to what can be created / made.
     * @param toggle True or false depending if this webshop should have access to the extended mode.
     * @param password A password given by getshop to toggle this option.
     * @return
     * @throws ErrorException
     */
     public JsonElement enableExtendedMode(Object toggle, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("toggle",new Gson().toJson(toggle));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "enableExtendedMode";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement enableSMSAccess(Object toggle, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("toggle",new Gson().toJson(toggle));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "enableSMSAccess";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * On registration, generate a new id this store, which will become a part of the hostname.
     * @return
     * @throws ErrorException
     */
     public JsonElement generateStoreId()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "generateStoreId";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * On registration, generate a new id this store, which will become a part of the hostname.
     * @return
     * @throws ErrorException
     */
     public JsonElement getAllEnvironments()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllEnvironments";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * On registration, generate a new id this store, which will become a part of the hostname.
     * @return
     * @throws ErrorException
     */
     public JsonElement getCriticalMessage()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getCriticalMessage";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Save whatever data you want to the store.
     * @param key The key to save to
     * @param value The value to save
     * @param secure Secure? Need to be administrator to read it?
     */
     public JsonElement getCurrentSession()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getCurrentSession";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Save whatever data you want to the store.
     * @param key The key to save to
     * @param value The value to save
     * @param secure Secure? Need to be administrator to read it?
     */
     public JsonElement getKey(Object key)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("key",new Gson().toJson(key));
          gs_json_object_data.method = "getKey";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Save whatever data you want to the store.
     * @param key The key to save to
     * @param value The value to save
     * @param secure Secure? Need to be administrator to read it?
     */
     public JsonElement getKeySecure(Object key, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("key",new Gson().toJson(key));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "getKeySecure";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * On registration, generate a new id this store, which will become a part of the hostname.
     * @return
     * @throws ErrorException
     */
     public JsonElement getMultiLevelNames()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getMultiLevelNames";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get the store added to this session.
     * @return
     * @throws ErrorException
     */
     public JsonElement getMyStore()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getMyStore";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * On registration, generate a new id this store, which will become a part of the hostname.
     * @return
     * @throws ErrorException
     */
     public JsonElement getSelectedCurrency()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getSelectedCurrency";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * On registration, generate a new id this store, which will become a part of the hostname.
     * @return
     * @throws ErrorException
     */
     public JsonElement getSlaves()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getSlaves";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch the store id identified to this user.
     * @return The store id
     * @throws ErrorException
     */
     public JsonElement getStoreId()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getStoreId";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Initializing this api. This will identify the webshop and will act as the root for everything in this api.
     * @param initSessionId The session id to identify to this user.
     * @return
     * @throws ErrorException
     */
     public JsonElement initializeStore(Object webAddress, Object initSessionId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("webAddress",new Gson().toJson(webAddress));
          gs_json_object_data.args.put("initSessionId",new Gson().toJson(initSessionId));
          gs_json_object_data.method = "initializeStore";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Initializing this api. This will identify the webshop and will act as the root for everything in this api.
     * @param initSessionId The session id to identify to this user.
     * @return
     * @throws ErrorException
     */
     public JsonElement initializeStoreByStoreId(Object storeId, Object initSessionId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("storeId",new Gson().toJson(storeId));
          gs_json_object_data.args.put("initSessionId",new Gson().toJson(initSessionId));
          gs_json_object_data.method = "initializeStoreByStoreId";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Initializing this api. This will identify the webshop and will act as the root for everything in this api.
     * @param initSessionId The session id to identify to this user.
     * @return
     * @throws ErrorException
     */
     public JsonElement initializeStoreWithModuleId(Object webAddress, Object initSessionId, Object moduleId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("webAddress",new Gson().toJson(webAddress));
          gs_json_object_data.args.put("initSessionId",new Gson().toJson(initSessionId));
          gs_json_object_data.args.put("moduleId",new Gson().toJson(moduleId));
          gs_json_object_data.method = "initializeStoreWithModuleId";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Check if a web shop address has already been taken.
     * @param address The address to check for.
     * @throws ErrorException
     */
     public JsonElement isAddressTaken(Object address)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("address",new Gson().toJson(address));
          gs_json_object_data.method = "isAddressTaken";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Initializing this api. This will identify the webshop and will act as the root for everything in this api.
     * @param initSessionId The session id to identify to this user.
     * @return
     * @throws ErrorException
     */
     public JsonElement isPikStore()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "isPikStore";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * On registration, generate a new id this store, which will become a part of the hostname.
     * @return
     * @throws ErrorException
     */
     public JsonElement isProductMode()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "isProductMode";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * On registration, generate a new id this store, which will become a part of the hostname.
     * @return
     * @throws ErrorException
     */
     public void receiveSyncData(Object json)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("json",new Gson().toJson(json));
          gs_json_object_data.method = "receiveSyncData";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Remove an already added domain name.
     * @param domainName The domain name to remove.
     * @return
     * @throws ErrorException
     */
     public JsonElement removeDomainName(Object domainName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("domainName",new Gson().toJson(domainName));
          gs_json_object_data.method = "removeDomainName";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Save whatever data you want to the store.
     * @param key The key to save to
     * @param value The value to save
     * @param secure Secure? Need to be administrator to read it?
     */
     public void removeKey(Object key)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("key",new Gson().toJson(key));
          gs_json_object_data.method = "removeKey";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Save whatever data you want to the store.
     * @param key The key to save to
     * @param value The value to save
     * @param secure Secure? Need to be administrator to read it?
     */
     public void saveKey(Object key, Object value, Object secure)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("key",new Gson().toJson(key));
          gs_json_object_data.args.put("value",new Gson().toJson(value));
          gs_json_object_data.args.put("secure",new Gson().toJson(secure));
          gs_json_object_data.method = "saveKey";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Update the current store with new configuration data.
     * @param config The configuration data to update.
     * @return
     * @throws ErrorException
     */
     public JsonElement saveStore(Object config)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("config",new Gson().toJson(config));
          gs_json_object_data.method = "saveStore";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * On registration, generate a new id this store, which will become a part of the hostname.
     * @return
     * @throws ErrorException
     */
     public void seenCriticalMessage(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "seenCriticalMessage";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * On registration, generate a new id this store, which will become a part of the hostname.
     * @return
     * @throws ErrorException
     */
     public void setDefaultMultilevelName(Object multilevelname)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("multilevelname",new Gson().toJson(multilevelname));
          gs_json_object_data.method = "setDefaultMultilevelName";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * On registration, generate a new id this store, which will become a part of the hostname.
     * @return
     * @throws ErrorException
     */
     public void setImageIdToFavicon(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "setImageIdToFavicon";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This will set the readintroduction variable in the Store object to true.
     * @return
     * @throws ErrorException
     */
     public JsonElement setIntroductionRead()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "setIntroductionRead";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Setting this store to be a template or not.
     *
     * @param storeId
     * @param isTemplate
     */
     public void setIsTemplate(Object storeId, Object isTemplate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("storeId",new Gson().toJson(storeId));
          gs_json_object_data.args.put("isTemplate",new Gson().toJson(isTemplate));
          gs_json_object_data.method = "setIsTemplate";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * On registration, generate a new id this store, which will become a part of the hostname.
     * @return
     * @throws ErrorException
     */
     public void setMasterStoreId(Object masterStoreId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("masterStoreId",new Gson().toJson(masterStoreId));
          gs_json_object_data.method = "setMasterStoreId";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Set a new domain name to this store / webshop
     * @param domainName The domain name to identify this shop with.
     * @return
     * @throws ErrorException
     */
     public JsonElement setPrimaryDomainName(Object domainName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("domainName",new Gson().toJson(domainName));
          gs_json_object_data.method = "setPrimaryDomainName";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * A user can set a different language for its session.
     * @param id
     * @throws ErrorException
     */
     public void setSessionLanguage(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "setSessionLanguage";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * On registration, generate a new id this store, which will become a part of the hostname.
     * @return
     * @throws ErrorException
     */
     public void setStoreIdentifier(Object identifier)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("identifier",new Gson().toJson(identifier));
          gs_json_object_data.method = "setStoreIdentifier";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * On registration, generate a new id this store, which will become a part of the hostname.
     * @return
     * @throws ErrorException
     */
     public JsonElement supportsCreateOrderOnDemand()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "supportsCreateOrderOnDemand";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * On registration, generate a new id this store, which will become a part of the hostname.
     * @return
     * @throws ErrorException
     */
     public void syncData(Object environment, Object username, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("environment",new Gson().toJson(environment));
          gs_json_object_data.args.put("username",new Gson().toJson(username));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "syncData";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Save whatever data you want to the store.
     * @param key The key to save to
     * @param value The value to save
     * @param secure Secure? Need to be administrator to read it?
     */
     public void toggleDeactivation(Object password, Object deactivated)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.args.put("deactivated",new Gson().toJson(deactivated));
          gs_json_object_data.method = "toggleDeactivation";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * On registration, generate a new id this store, which will become a part of the hostname.
     * @return
     * @throws ErrorException
     */
     public void toggleIgnoreBookingErrors(Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "toggleIgnoreBookingErrors";
          gs_json_object_data.interfaceName = "core.storemanager.IStoreManager";
          String result = transport.send(gs_json_object_data);
     }

}
