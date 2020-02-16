package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APISimpleEventManager {

      public Communicator transport;

      public APISimpleEventManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public void addUserToEvent(Object pageId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "addUserToEvent";
          gs_json_object_data.interfaceName = "core.simpleeventmanager.ISimpleEventManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteEvent(Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "deleteEvent";
          gs_json_object_data.interfaceName = "core.simpleeventmanager.ISimpleEventManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllEvents(Object listPageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("listPageId",new Gson().toJson(listPageId));
          gs_json_object_data.method = "getAllEvents";
          gs_json_object_data.interfaceName = "core.simpleeventmanager.ISimpleEventManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getEventById(Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "getEventById";
          gs_json_object_data.interfaceName = "core.simpleeventmanager.ISimpleEventManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getEventByPageId(Object pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "getEventByPageId";
          gs_json_object_data.interfaceName = "core.simpleeventmanager.ISimpleEventManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getEventsInFuture(Object listPageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("listPageId",new Gson().toJson(listPageId));
          gs_json_object_data.method = "getEventsInFuture";
          gs_json_object_data.interfaceName = "core.simpleeventmanager.ISimpleEventManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void saveEvent(Object simpleEvent)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("simpleEvent",new Gson().toJson(simpleEvent));
          gs_json_object_data.method = "saveEvent";
          gs_json_object_data.interfaceName = "core.simpleeventmanager.ISimpleEventManager";
          String result = transport.send(gs_json_object_data);
     }

}
