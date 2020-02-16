package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIAccountingManager {

      public Communicator transport;

      public APIAccountingManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * For handling connection to accounting systems.<br>
     */
     public JsonElement createCombinedOrderFile(Object newUsersOnly)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("newUsersOnly",new Gson().toJson(newUsersOnly));
          gs_json_object_data.method = "createCombinedOrderFile";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public JsonElement createCreditorFile(Object newOnly)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("newOnly",new Gson().toJson(newOnly));
          gs_json_object_data.method = "createCreditorFile";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public JsonElement createOrderFile()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "createOrderFile";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public JsonElement createUserFile(Object newOnly)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("newOnly",new Gson().toJson(newOnly));
          gs_json_object_data.method = "createUserFile";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public void deleteFile(Object fileId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.method = "deleteFile";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public JsonElement downloadOrderFileNewType(Object configId, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("configId",new Gson().toJson(configId));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "downloadOrderFileNewType";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public void forceTransferFiles()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "forceTransferFiles";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public JsonElement getAccountingConfig(Object configId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("configId",new Gson().toJson(configId));
          gs_json_object_data.method = "getAccountingConfig";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public JsonElement getAccountingManagerConfig()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAccountingManagerConfig";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public JsonElement getAllConfigs()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllConfigs";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public JsonElement getAllFiles(Object showAllFiles)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("showAllFiles",new Gson().toJson(showAllFiles));
          gs_json_object_data.method = "getAllFiles";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public JsonElement getAllFilesNotTransferredToAccounting()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllFilesNotTransferredToAccounting";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public JsonElement getFile(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getFile";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public JsonElement getFileById(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getFileById";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public JsonElement getFileByIdResend(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getFileByIdResend";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public JsonElement getLatestLogEntries()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getLatestLogEntries";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public JsonElement getNewFile(Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "getNewFile";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public JsonElement getStats(Object configId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("configId",new Gson().toJson(configId));
          gs_json_object_data.method = "getStats";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public void markAsTransferredToAccounting(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "markAsTransferredToAccounting";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public void removeTransferConfig(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "removeTransferConfig";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public void resetAllAccounting()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "resetAllAccounting";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public void saveConfig(Object config)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("config",new Gson().toJson(config));
          gs_json_object_data.method = "saveConfig";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public void setAccountingManagerConfig(Object config)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("config",new Gson().toJson(config));
          gs_json_object_data.method = "setAccountingManagerConfig";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public void transferAllToNewSystem()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "transferAllToNewSystem";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public void transferFiles(Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "transferFiles";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public void transferFilesToAccounting()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "transferFilesToAccounting";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * For handling connection to accounting systems.<br>
     */
     public JsonElement transferSingleOrders(Object configId, Object incOrderIds)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("configId",new Gson().toJson(configId));
          gs_json_object_data.args.put("incOrderIds",new Gson().toJson(incOrderIds));
          gs_json_object_data.method = "transferSingleOrders";
          gs_json_object_data.interfaceName = "core.accountingmanager.IAccountingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
