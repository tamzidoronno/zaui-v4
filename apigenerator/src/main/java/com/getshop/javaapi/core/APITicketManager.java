package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APITicketManager {

      public Communicator transport;

      public APITicketManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public void addAttachmentToTicket(Object ticketId, Object ticketAttachmentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketId",new Gson().toJson(ticketId));
          gs_json_object_data.args.put("ticketAttachmentId",new Gson().toJson(ticketAttachmentId));
          gs_json_object_data.method = "addAttachmentToTicket";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addSubTask(Object ticketId, Object title)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketId",new Gson().toJson(ticketId));
          gs_json_object_data.args.put("title",new Gson().toJson(title));
          gs_json_object_data.method = "addSubTask";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addTicketContent(Object ticketId, Object content)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketId",new Gson().toJson(ticketId));
          gs_json_object_data.args.put("content",new Gson().toJson(content));
          gs_json_object_data.method = "addTicketContent";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void assignTicketToUser(Object ticketId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketId",new Gson().toJson(ticketId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "assignTicketToUser";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void changeStateOfTicket(Object ticketId, Object state)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketId",new Gson().toJson(ticketId));
          gs_json_object_data.args.put("state",new Gson().toJson(state));
          gs_json_object_data.method = "changeStateOfTicket";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void changeType(Object ticketId, Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketId",new Gson().toJson(ticketId));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "changeType";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement createLightTicket(Object title)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("title",new Gson().toJson(title));
          gs_json_object_data.method = "createLightTicket";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement createLightTicketOfClonedSetupTicket(Object ticket)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticket",new Gson().toJson(ticket));
          gs_json_object_data.method = "createLightTicketOfClonedSetupTicket";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement createLightTicketWithPassword(Object title, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("title",new Gson().toJson(title));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "createLightTicketWithPassword";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement createSetupTicket(Object title)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("title",new Gson().toJson(title));
          gs_json_object_data.method = "createSetupTicket";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void deleteSubTask(Object ticketId, Object subTaskId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketId",new Gson().toJson(ticketId));
          gs_json_object_data.args.put("subTaskId",new Gson().toJson(subTaskId));
          gs_json_object_data.method = "deleteSubTask";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteTicket(Object ticketId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketId",new Gson().toJson(ticketId));
          gs_json_object_data.method = "deleteTicket";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllTickets(Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getAllTickets";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAttachment(Object attachmentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("attachmentId",new Gson().toJson(attachmentId));
          gs_json_object_data.method = "getAttachment";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getLastTicketContent(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getLastTicketContent";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getPushOverSettings(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getPushOverSettings";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getSetupTicketsLights()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getSetupTicketsLights";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getStatistics(Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getStatistics";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getStoreStatistics(Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getStoreStatistics";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTicket(Object ticketId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketId",new Gson().toJson(ticketId));
          gs_json_object_data.method = "getTicket";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTicketByToken(Object storeId, Object ticketToken)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("storeId",new Gson().toJson(storeId));
          gs_json_object_data.args.put("ticketToken",new Gson().toJson(ticketToken));
          gs_json_object_data.method = "getTicketByToken";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTicketContents(Object ticketId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketId",new Gson().toJson(ticketId));
          gs_json_object_data.method = "getTicketContents";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTicketIdByToken(Object ticketToken)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketToken",new Gson().toJson(ticketToken));
          gs_json_object_data.method = "getTicketIdByToken";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTicketLights()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getTicketLights";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void markAsRepied(Object ticketId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketId",new Gson().toJson(ticketId));
          gs_json_object_data.method = "markAsRepied";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void markTicketAsRead(Object ticketId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketId",new Gson().toJson(ticketId));
          gs_json_object_data.method = "markTicketAsRead";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void markTicketAsUnread(Object ticketId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketId",new Gson().toJson(ticketId));
          gs_json_object_data.method = "markTicketAsUnread";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void reconnectTicket(Object ticketId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketId",new Gson().toJson(ticketId));
          gs_json_object_data.method = "reconnectTicket";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void savePushOverSettings(Object pushOver, Object pushOverToken)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pushOver",new Gson().toJson(pushOver));
          gs_json_object_data.args.put("pushOverToken",new Gson().toJson(pushOverToken));
          gs_json_object_data.method = "savePushOverSettings";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveTicket(Object ticket)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticket",new Gson().toJson(ticket));
          gs_json_object_data.method = "saveTicket";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement searchTicket(Object keyWord)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("keyWord",new Gson().toJson(keyWord));
          gs_json_object_data.method = "searchTicket";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void toggleIgnoreTicket(Object ticketId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketId",new Gson().toJson(ticketId));
          gs_json_object_data.method = "toggleIgnoreTicket";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void toggleSubTask(Object ticketId, Object subTaskId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketId",new Gson().toJson(ticketId));
          gs_json_object_data.args.put("subTaskId",new Gson().toJson(subTaskId));
          gs_json_object_data.method = "toggleSubTask";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void updateContent(Object ticketId, Object contentId, Object content)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketId",new Gson().toJson(ticketId));
          gs_json_object_data.args.put("contentId",new Gson().toJson(contentId));
          gs_json_object_data.args.put("content",new Gson().toJson(content));
          gs_json_object_data.method = "updateContent";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void updateEvent(Object ticketId, Object event)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketId",new Gson().toJson(ticketId));
          gs_json_object_data.args.put("event",new Gson().toJson(event));
          gs_json_object_data.method = "updateEvent";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void updateLightTicketState(Object ticketToken, Object state)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketToken",new Gson().toJson(ticketToken));
          gs_json_object_data.args.put("state",new Gson().toJson(state));
          gs_json_object_data.method = "updateLightTicketState";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void updateTicket(Object ticketToken, Object light)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketToken",new Gson().toJson(ticketToken));
          gs_json_object_data.args.put("light",new Gson().toJson(light));
          gs_json_object_data.method = "updateTicket";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void uploadAttachment(Object attachment)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("attachment",new Gson().toJson(attachment));
          gs_json_object_data.method = "uploadAttachment";
          gs_json_object_data.interfaceName = "core.ticket.ITicketManager";
          String result = transport.send(gs_json_object_data);
     }

}
