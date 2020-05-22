package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIGetShopLockSystemManager {

      public Communicator transport;

      public APIGetShopLockSystemManager(Communicator transport){
           this.transport = transport;
      }

     /***** End group code stuff *****/
     public void addTransactionEntranceDoor(Object serverId, Object lockId, Object code)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.method = "addTransactionEntranceDoor";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /***** End group code stuff *****/
     public void addTransactionEntranceDoorByToken(Object tokenId, Object lockIncrementalId, Object code)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tokenId",new Gson().toJson(tokenId));
          gs_json_object_data.args.put("lockIncrementalId",new Gson().toJson(lockIncrementalId));
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.method = "addTransactionEntranceDoorByToken";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /***** End group code stuff *****/
     public void addTransactionHistory(Object tokenId, Object lockId, Object timeStamp, Object userSlot)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tokenId",new Gson().toJson(tokenId));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.args.put("timeStamp",new Gson().toJson(timeStamp));
          gs_json_object_data.args.put("userSlot",new Gson().toJson(userSlot));
          gs_json_object_data.method = "addTransactionHistory";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /***** End group code stuff *****/
     public void addTransactionHistorySeros(Object tokenId, Object lockId, Object timeStamp, Object keyId, Object userSlot)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tokenId",new Gson().toJson(tokenId));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.args.put("timeStamp",new Gson().toJson(timeStamp));
          gs_json_object_data.args.put("keyId",new Gson().toJson(keyId));
          gs_json_object_data.args.put("userSlot",new Gson().toJson(userSlot));
          gs_json_object_data.method = "addTransactionHistorySeros";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /***** End group code stuff *****/
     public JsonElement canShowAccessLog()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "canShowAccessLog";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /***** Group code stuff *****/
     public void changeCode(Object groupId, Object slotId, Object pinCode, Object cardId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.args.put("slotId",new Gson().toJson(slotId));
          gs_json_object_data.args.put("pinCode",new Gson().toJson(pinCode));
          gs_json_object_data.args.put("cardId",new Gson().toJson(cardId));
          gs_json_object_data.method = "changeCode";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /***** Group code stuff *****/
     public void changeDatesForSlot(Object groupId, Object slotId, Object startDate, Object endDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.args.put("slotId",new Gson().toJson(slotId));
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("endDate",new Gson().toJson(endDate));
          gs_json_object_data.method = "changeDatesForSlot";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /***** End group code stuff *****/
     public void changeNameOnGorup(Object groupdId, Object name)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupdId",new Gson().toJson(groupdId));
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.method = "changeNameOnGorup";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /***** End group code stuff *****/
     public void closeLock(Object lockId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.method = "closeLock";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement createNewLockGroup(Object name, Object maxUsersInGroup, Object codeSize)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.args.put("maxUsersInGroup",new Gson().toJson(maxUsersInGroup));
          gs_json_object_data.args.put("codeSize",new Gson().toJson(codeSize));
          gs_json_object_data.method = "createNewLockGroup";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void createServer(Object type, Object hostname, Object userName, Object password, Object givenName, Object token)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.args.put("hostname",new Gson().toJson(hostname));
          gs_json_object_data.args.put("userName",new Gson().toJson(userName));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.args.put("givenName",new Gson().toJson(givenName));
          gs_json_object_data.args.put("token",new Gson().toJson(token));
          gs_json_object_data.method = "createServer";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     *
     * @param groupId
     * @param lockIds Key = serverId, and value is a list of lockids for the server.
     */
     public void deactivatePrioritingOfLock(Object serverId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.method = "deactivatePrioritingOfLock";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     *
     * @param groupId
     * @param lockIds Key = serverId, and value is a list of lockids for the server.
     */
     public void deleteGroup(Object groupId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.method = "deleteGroup";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /***** End group code stuff *****/
     public void deleteLock(Object serverId, Object lockId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.method = "deleteLock";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteServer(Object serverId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.method = "deleteServer";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /***** End group code stuff *****/
     public void deleteSlot(Object serverId, Object lockId, Object slotId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.args.put("slotId",new Gson().toJson(slotId));
          gs_json_object_data.method = "deleteSlot";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void forceDeleteSlot(Object serverId, Object lockId, Object slotId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.args.put("slotId",new Gson().toJson(slotId));
          gs_json_object_data.method = "forceDeleteSlot";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void generateNewCodesForLock(Object serverId, Object lockId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.method = "generateNewCodesForLock";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /***** End group code stuff *****/
     public JsonElement getAccess(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getAccess";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /***** End group code stuff *****/
     public JsonElement getAccessHistory(Object groupId, Object start, Object end, Object groupSlotId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("groupSlotId",new Gson().toJson(groupSlotId));
          gs_json_object_data.method = "getAccessHistory";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /***** End group code stuff *****/
     public JsonElement getAccessLog(Object serverId, Object lockId, Object filterOptions)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.args.put("filterOptions",new Gson().toJson(filterOptions));
          gs_json_object_data.method = "getAccessLog";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /***** End group code stuff *****/
     public JsonElement getAllAccessUsers(Object options)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("options",new Gson().toJson(options));
          gs_json_object_data.method = "getAllAccessUsers";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /***** End group code stuff *****/
     public JsonElement getAllAccessUsersFlat()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllAccessUsersFlat";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     *
     * @param groupId
     * @param lockIds Key = serverId, and value is a list of lockids for the server.
     */
     public JsonElement getAllGroups()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllGroups";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     *
     * @param groupId
     * @param lockIds Key = serverId, and value is a list of lockids for the server.
     */
     public JsonElement getAllGroupsUnfinalized()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllGroupsUnfinalized";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /***** End group code stuff *****/
     public JsonElement getCodeSize()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getCodeSize";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /***** End group code stuff *****/
     public JsonElement getCodesByToken(Object tokenId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tokenId",new Gson().toJson(tokenId));
          gs_json_object_data.method = "getCodesByToken";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /***** End group code stuff *****/
     public JsonElement getCodesInUse(Object serverId, Object lockId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.method = "getCodesInUse";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     *
     * @param groupId
     * @param lockIds Key = serverId, and value is a list of lockids for the server.
     */
     public JsonElement getGroup(Object groupId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.method = "getGroup";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getLock(Object serverId, Object lockId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.method = "getLock";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getLockServers()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getLockServers";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     *
     * @param groupId
     * @param lockIds Key = serverId, and value is a list of lockids for the server.
     */
     public JsonElement getNameOfGroup(Object groupId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.method = "getNameOfGroup";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /***** Group code stuff *****/
     public JsonElement getNextUnusedCode(Object groupId, Object reference, Object managerName, Object textReference)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.args.put("reference",new Gson().toJson(reference));
          gs_json_object_data.args.put("managerName",new Gson().toJson(managerName));
          gs_json_object_data.args.put("textReference",new Gson().toJson(textReference));
          gs_json_object_data.method = "getNextUnusedCode";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /***** End group code stuff *****/
     public JsonElement grantAccessDirect(Object groupId, Object user)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.args.put("user",new Gson().toJson(user));
          gs_json_object_data.method = "grantAccessDirect";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /***** End group code stuff *****/
     public JsonElement isSlotTakenInUseInAnyGroups(Object serverId, Object lockId, Object slotId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.args.put("slotId",new Gson().toJson(slotId));
          gs_json_object_data.method = "isSlotTakenInUseInAnyGroups";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     *
     * @param groupId
     * @param lockIds Key = serverId, and value is a list of lockids for the server.
     */
     public void lockSettingsChanged(Object lockSettings)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("lockSettings",new Gson().toJson(lockSettings));
          gs_json_object_data.method = "lockSettingsChanged";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void markCodeAsUpdatedOnLock(Object serverId, Object lockId, Object slotId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.args.put("slotId",new Gson().toJson(slotId));
          gs_json_object_data.method = "markCodeAsUpdatedOnLock";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void markCodeForDeletion(Object serverId, Object lockId, Object slotId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.args.put("slotId",new Gson().toJson(slotId));
          gs_json_object_data.method = "markCodeForDeletion";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void markCodeForResending(Object serverId, Object lockId, Object slotId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.args.put("slotId",new Gson().toJson(slotId));
          gs_json_object_data.method = "markCodeForResending";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /***** End group code stuff *****/
     public void openLock(Object lockId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.method = "openLock";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void pingServers()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "pingServers";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void prioritizeLockUpdate(Object serverId, Object lockId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.method = "prioritizeLockUpdate";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /***** End group code stuff *****/
     public void removeAccess(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "removeAccess";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /***** End group code stuff *****/
     public void renameLock(Object serverId, Object lockId, Object name)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.method = "renameLock";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /***** Group code stuff *****/
     public void renewCodeForSlot(Object groupId, Object slotId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.args.put("slotId",new Gson().toJson(slotId));
          gs_json_object_data.method = "renewCodeForSlot";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement restCall(Object serverId, Object path)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.args.put("path",new Gson().toJson(path));
          gs_json_object_data.method = "restCall";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /***** End group code stuff *****/
     public void resyncDatabaseWithLoraGateway(Object serverId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.method = "resyncDatabaseWithLoraGateway";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveLocstarLock(Object serverId, Object lock)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.args.put("lock",new Gson().toJson(lock));
          gs_json_object_data.method = "saveLocstarLock";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /***** End group code stuff *****/
     public void saveUser(Object user)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("user",new Gson().toJson(user));
          gs_json_object_data.method = "saveUser";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /***** End group code stuff *****/
     public void sendEmailToCustomer(Object userId, Object subject, Object body)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("subject",new Gson().toJson(subject));
          gs_json_object_data.args.put("body",new Gson().toJson(body));
          gs_json_object_data.method = "sendEmailToCustomer";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /***** End group code stuff *****/
     public void sendSmsToCustomer(Object userId, Object textMessage)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("textMessage",new Gson().toJson(textMessage));
          gs_json_object_data.method = "sendSmsToCustomer";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /***** End group code stuff *****/
     public void setCodeSize(Object codeSize)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("codeSize",new Gson().toJson(codeSize));
          gs_json_object_data.method = "setCodeSize";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /***** End group code stuff *****/
     public void setGroupVirtual(Object groupId, Object isVirtual)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.args.put("isVirtual",new Gson().toJson(isVirtual));
          gs_json_object_data.method = "setGroupVirtual";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     *
     * @param groupId
     * @param lockIds Key = serverId, and value is a list of lockids for the server.
     */
     public void setLocksToGroup(Object groupId, Object lockIds)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.args.put("lockIds",new Gson().toJson(lockIds));
          gs_json_object_data.method = "setLocksToGroup";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void startFetchingOfLocksFromServer(Object serverId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.method = "startFetchingOfLocksFromServer";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     *
     * @param groupId
     * @param lockIds Key = serverId, and value is a list of lockids for the server.
     */
     public void toggleActiveServer(Object serverId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.method = "toggleActiveServer";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     *
     * @param groupId
     * @param lockIds Key = serverId, and value is a list of lockids for the server.
     */
     public void triggerCheckOfCodes(Object serverId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.method = "triggerCheckOfCodes";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     *
     * @param groupId
     * @param lockIds Key = serverId, and value is a list of lockids for the server.
     */
     public void triggerCronTab()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "triggerCronTab";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void updateConnectionDetails(Object serverId, Object hostname, Object username, Object password, Object givenName, Object token)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.args.put("hostname",new Gson().toJson(hostname));
          gs_json_object_data.args.put("username",new Gson().toJson(username));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.args.put("givenName",new Gson().toJson(givenName));
          gs_json_object_data.args.put("token",new Gson().toJson(token));
          gs_json_object_data.method = "updateConnectionDetails";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void updateZwaveRoute(Object serverId, Object lockId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("serverId",new Gson().toJson(serverId));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.method = "updateZwaveRoute";
          gs_json_object_data.interfaceName = "core.getshoplocksystem.IGetShopLockSystemManager";
          String result = transport.send(gs_json_object_data);
     }

}
