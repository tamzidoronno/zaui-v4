package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIGmailApiManager {

      public Communicator transport;

      public APIGmailApiManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public void assignMessageToUser(Object messageId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("messageId",new Gson().toJson(messageId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "assignMessageToUser";
          gs_json_object_data.interfaceName = "core.googleapi.IGmailApiManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void changeTypeOnMessage(Object messageId, Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("messageId",new Gson().toJson(messageId));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "changeTypeOnMessage";
          gs_json_object_data.interfaceName = "core.googleapi.IGmailApiManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Will create an emaploye with the name and email address
     * and rescan for changes.
     *
     * @param companyId
     */
     public void connectMessageToCompany(Object msgId, Object companyId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("msgId",new Gson().toJson(msgId));
          gs_json_object_data.args.put("companyId",new Gson().toJson(companyId));
          gs_json_object_data.method = "connectMessageToCompany";
          gs_json_object_data.interfaceName = "core.googleapi.IGmailApiManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void fetchAllMessages()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "fetchAllMessages";
          gs_json_object_data.interfaceName = "core.googleapi.IGmailApiManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllUnassignedMessages()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllUnassignedMessages";
          gs_json_object_data.interfaceName = "core.googleapi.IGmailApiManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getEmails(Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getEmails";
          gs_json_object_data.interfaceName = "core.googleapi.IGmailApiManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getLightMessages(Object companyId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("companyId",new Gson().toJson(companyId));
          gs_json_object_data.method = "getLightMessages";
          gs_json_object_data.interfaceName = "core.googleapi.IGmailApiManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Will create an emaploye with the name and email address
     * and rescan for changes.
     *
     * @param companyId
     */
     public JsonElement getMessageLight(Object msgId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("msgId",new Gson().toJson(msgId));
          gs_json_object_data.method = "getMessageLight";
          gs_json_object_data.interfaceName = "core.googleapi.IGmailApiManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Will create an emaploye with the name and email address
     * and rescan for changes.
     *
     * @param companyId
     */
     public JsonElement getMessageParts(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getMessageParts";
          gs_json_object_data.interfaceName = "core.googleapi.IGmailApiManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getMyUnsolvedMessages()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getMyUnsolvedMessages";
          gs_json_object_data.interfaceName = "core.googleapi.IGmailApiManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Will create an emaploye with the name and email address
     * and rescan for changes.
     *
     * @param companyId
     */
     public void markAsArchived(Object msgId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("msgId",new Gson().toJson(msgId));
          gs_json_object_data.method = "markAsArchived";
          gs_json_object_data.interfaceName = "core.googleapi.IGmailApiManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void reScanCompanyConnection()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "reScanCompanyConnection";
          gs_json_object_data.interfaceName = "core.googleapi.IGmailApiManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Will create an emaploye with the name and email address
     * and rescan for changes.
     *
     * @param companyId
     */
     public void replyEmail(Object msgId, Object content)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("msgId",new Gson().toJson(msgId));
          gs_json_object_data.args.put("content",new Gson().toJson(content));
          gs_json_object_data.method = "replyEmail";
          gs_json_object_data.interfaceName = "core.googleapi.IGmailApiManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Will create an emaploye with the name and email address
     * and rescan for changes.
     *
     * @param companyId
     */
     public void updateTimeSpentOnMessage(Object msgId, Object timeSpent, Object completed)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("msgId",new Gson().toJson(msgId));
          gs_json_object_data.args.put("timeSpent",new Gson().toJson(timeSpent));
          gs_json_object_data.args.put("completed",new Gson().toJson(completed));
          gs_json_object_data.method = "updateTimeSpentOnMessage";
          gs_json_object_data.interfaceName = "core.googleapi.IGmailApiManager";
          String result = transport.send(gs_json_object_data);
     }

}
