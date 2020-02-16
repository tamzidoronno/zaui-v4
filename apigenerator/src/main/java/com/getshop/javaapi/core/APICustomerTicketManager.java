package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APICustomerTicketManager {

      public Communicator transport;

      public APICustomerTicketManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public void addAttachmentToTicket(Object storeId, Object ticketToken, Object ticketAttachmentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("storeId",new Gson().toJson(storeId));
          gs_json_object_data.args.put("ticketToken",new Gson().toJson(ticketToken));
          gs_json_object_data.args.put("ticketAttachmentId",new Gson().toJson(ticketAttachmentId));
          gs_json_object_data.method = "addAttachmentToTicket";
          gs_json_object_data.interfaceName = "core.ticket.ICustomerTicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addContent(Object storeId, Object secureTicketId, Object content)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("storeId",new Gson().toJson(storeId));
          gs_json_object_data.args.put("secureTicketId",new Gson().toJson(secureTicketId));
          gs_json_object_data.args.put("content",new Gson().toJson(content));
          gs_json_object_data.method = "addContent";
          gs_json_object_data.interfaceName = "core.ticket.ICustomerTicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement cloneSetupTicket(Object ticketId, Object storeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketId",new Gson().toJson(ticketId));
          gs_json_object_data.args.put("storeId",new Gson().toJson(storeId));
          gs_json_object_data.method = "cloneSetupTicket";
          gs_json_object_data.interfaceName = "core.ticket.ICustomerTicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement createTicket(Object ticketLight)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ticketLight",new Gson().toJson(ticketLight));
          gs_json_object_data.method = "createTicket";
          gs_json_object_data.interfaceName = "core.ticket.ICustomerTicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * A customer (black man) had a problem where he was not able to find the tickets replied to,
     * so he just complained about not getting any answers even if we had answered all his question.
     * Something had to be done.
     * @return
     */
     public JsonElement getNiggerFriendlyTicketNotifications(Object storeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("storeId",new Gson().toJson(storeId));
          gs_json_object_data.method = "getNiggerFriendlyTicketNotifications";
          gs_json_object_data.interfaceName = "core.ticket.ICustomerTicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getPredefinedTickets()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getPredefinedTickets";
          gs_json_object_data.interfaceName = "core.ticket.ICustomerTicketManager";
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
          gs_json_object_data.interfaceName = "core.ticket.ICustomerTicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTicket(Object storeId, Object secureTicketId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("storeId",new Gson().toJson(storeId));
          gs_json_object_data.args.put("secureTicketId",new Gson().toJson(secureTicketId));
          gs_json_object_data.method = "getTicket";
          gs_json_object_data.interfaceName = "core.ticket.ICustomerTicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTicketContents(Object storeId, Object ticketToken)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("storeId",new Gson().toJson(storeId));
          gs_json_object_data.args.put("ticketToken",new Gson().toJson(ticketToken));
          gs_json_object_data.method = "getTicketContents";
          gs_json_object_data.interfaceName = "core.ticket.ICustomerTicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * A customer (black man) had a problem where he was not able to find the tickets replied to,
     * so he just complained about not getting any answers even if we had answered all his question.
     * Something had to be done.
     * @return
     */
     public JsonElement getTicketReportForCustomer(Object start, Object end, Object storeId, Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("storeId",new Gson().toJson(storeId));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "getTicketReportForCustomer";
          gs_json_object_data.interfaceName = "core.ticket.ICustomerTicketManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * A customer (black man) had a problem where he was not able to find the tickets replied to,
     * so he just complained about not getting any answers even if we had answered all his question.
     * Something had to be done.
     * @return
     */
     public void markTicketAsRead(Object tokenId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tokenId",new Gson().toJson(tokenId));
          gs_json_object_data.method = "markTicketAsRead";
          gs_json_object_data.interfaceName = "core.ticket.ICustomerTicketManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void reOpenTicket(Object storeId, Object ticketToken)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("storeId",new Gson().toJson(storeId));
          gs_json_object_data.args.put("ticketToken",new Gson().toJson(ticketToken));
          gs_json_object_data.method = "reOpenTicket";
          gs_json_object_data.interfaceName = "core.ticket.ICustomerTicketManager";
          String result = transport.send(gs_json_object_data);
     }

}
