package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIGetShopLockManager {

      public Communicator transport;

      public APIGetShopLockManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public void accessEvent(String gs_multiLevelName, Object id, Object code, Object domain)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.args.put("domain",new Gson().toJson(domain));
          gs_json_object_data.method = "accessEvent";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public void addLockLogs(String gs_multiLevelName, Object logs, Object code)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("logs",new Gson().toJson(logs));
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.method = "addLockLogs";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public void changeZWaveId(String gs_multiLevelName, Object lockId, Object newId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.args.put("newId",new Gson().toJson(newId));
          gs_json_object_data.method = "changeZWaveId";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public void checkIfAllIsOk(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "checkIfAllIsOk";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public void deleteAllDevices(String gs_multiLevelName, Object password, Object source)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.args.put("source",new Gson().toJson(source));
          gs_json_object_data.method = "deleteAllDevices";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public void deleteLock(String gs_multiLevelName, Object code, Object lockId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.method = "deleteLock";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public void finalizeLocks(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "finalizeLocks";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public JsonElement getAllLocks(String gs_multiLevelName, Object serverSource)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("serverSource",new Gson().toJson(serverSource));
          gs_json_object_data.method = "getAllLocks";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public JsonElement getCodeForLock(String gs_multiLevelName, Object lockId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.method = "getCodeForLock";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public JsonElement getCodesInUse(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getCodesInUse";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public JsonElement getDevice(String gs_multiLevelName, Object deviceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("deviceId",new Gson().toJson(deviceId));
          gs_json_object_data.method = "getDevice";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public JsonElement getMasterCodes(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getMasterCodes";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public JsonElement getUpdatesOnLock(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getUpdatesOnLock";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public void openLock(String gs_multiLevelName, Object lockId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.method = "openLock";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public JsonElement pingLock(String gs_multiLevelName, Object lockId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.method = "pingLock";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public JsonElement pushCode(String gs_multiLevelName, Object id, Object door, Object code, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("door",new Gson().toJson(door));
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "pushCode";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public void refreshAllLocks(String gs_multiLevelName, Object source)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("source",new Gson().toJson(source));
          gs_json_object_data.method = "refreshAllLocks";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public void refreshLock(String gs_multiLevelName, Object lockId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.method = "refreshLock";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public void removeAllUnusedLocks(String gs_multiLevelName, Object source)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("source",new Gson().toJson(source));
          gs_json_object_data.method = "removeAllUnusedLocks";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public void removeCodeOnLock(String gs_multiLevelName, Object lockId, Object room)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.args.put("room",new Gson().toJson(room));
          gs_json_object_data.method = "removeCodeOnLock";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public void saveLock(String gs_multiLevelName, Object lock)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("lock",new Gson().toJson(lock));
          gs_json_object_data.method = "saveLock";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public void saveMastercodes(String gs_multiLevelName, Object codes)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("codes",new Gson().toJson(codes));
          gs_json_object_data.method = "saveMastercodes";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public void setMasterCode(String gs_multiLevelName, Object slot, Object code)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("slot",new Gson().toJson(slot));
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.method = "setMasterCode";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public void stopUpdatesOnLock(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "stopUpdatesOnLock";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public void triggerFetchingOfCodes(String gs_multiLevelName, Object ip, Object deviceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("ip",new Gson().toJson(ip));
          gs_json_object_data.args.put("deviceId",new Gson().toJson(deviceId));
          gs_json_object_data.method = "triggerFetchingOfCodes";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * Communicating with the getshop lock.
     * @author boggi
     */
     public void triggerMassUpdateOfLockLogs(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "triggerMassUpdateOfLockLogs";
          gs_json_object_data.interfaceName = "core.getshoplock.IGetShopLockManager";
          String result = transport.send(gs_json_object_data);
     }

}
