package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIPmsEventManager {

      public Communicator transport;

      public APIPmsEventManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Event calendar management.<br>
     */
     public JsonElement createEvent(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "createEvent";
          gs_json_object_data.interfaceName = "core.pmseventmanager.IPmsEventManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Event calendar management.<br>
     */
     public void deleteEntry(String gs_multiLevelName, Object entryId, Object day)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("entryId",new Gson().toJson(entryId));
          gs_json_object_data.args.put("day",new Gson().toJson(day));
          gs_json_object_data.method = "deleteEntry";
          gs_json_object_data.interfaceName = "core.pmseventmanager.IPmsEventManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Event calendar management.<br>
     */
     public void deleteEvent(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteEvent";
          gs_json_object_data.interfaceName = "core.pmseventmanager.IPmsEventManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Event calendar management.<br>
     */
     public JsonElement getEntry(String gs_multiLevelName, Object entryId, Object day)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("entryId",new Gson().toJson(entryId));
          gs_json_object_data.args.put("day",new Gson().toJson(day));
          gs_json_object_data.method = "getEntry";
          gs_json_object_data.interfaceName = "core.pmseventmanager.IPmsEventManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Event calendar management.<br>
     */
     public JsonElement getEntryShort(String gs_multiLevelName, Object shortId, Object day)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("shortId",new Gson().toJson(shortId));
          gs_json_object_data.args.put("day",new Gson().toJson(day));
          gs_json_object_data.method = "getEntryShort";
          gs_json_object_data.interfaceName = "core.pmseventmanager.IPmsEventManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Event calendar management.<br>
     */
     public JsonElement getEvent(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getEvent";
          gs_json_object_data.interfaceName = "core.pmseventmanager.IPmsEventManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Event calendar management.<br>
     */
     public JsonElement getEventEntries(String gs_multiLevelName, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getEventEntries";
          gs_json_object_data.interfaceName = "core.pmseventmanager.IPmsEventManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Event calendar management.<br>
     */
     public JsonElement getEventList(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getEventList";
          gs_json_object_data.interfaceName = "core.pmseventmanager.IPmsEventManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Event calendar management.<br>
     */
     public JsonElement getEventListWithDeleted(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getEventListWithDeleted";
          gs_json_object_data.interfaceName = "core.pmseventmanager.IPmsEventManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Event calendar management.<br>
     */
     public JsonElement getEvents(String gs_multiLevelName, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getEvents";
          gs_json_object_data.interfaceName = "core.pmseventmanager.IPmsEventManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Event calendar management.<br>
     */
     public JsonElement isChecked(String gs_multiLevelName, Object pmsBookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingId",new Gson().toJson(pmsBookingId));
          gs_json_object_data.method = "isChecked";
          gs_json_object_data.interfaceName = "core.pmseventmanager.IPmsEventManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Event calendar management.<br>
     */
     public void saveEntry(String gs_multiLevelName, Object entry, Object day)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("entry",new Gson().toJson(entry));
          gs_json_object_data.args.put("day",new Gson().toJson(day));
          gs_json_object_data.method = "saveEntry";
          gs_json_object_data.interfaceName = "core.pmseventmanager.IPmsEventManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Event calendar management.<br>
     */
     public void saveEvent(String gs_multiLevelName, Object event)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("event",new Gson().toJson(event));
          gs_json_object_data.method = "saveEvent";
          gs_json_object_data.interfaceName = "core.pmseventmanager.IPmsEventManager";
          String result = transport.send(gs_json_object_data);
     }

}
