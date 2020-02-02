package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APISalesManager {

      public Communicator transport;

      public APISalesManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Manager for handling sales for getshop.
     */
     public JsonElement findCustomer(Object key, Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("key",new Gson().toJson(key));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "findCustomer";
          gs_json_object_data.interfaceName = "core.sales.ISalesManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Manager for handling sales for getshop.
     */
     public JsonElement getCustomer(Object orgId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orgId",new Gson().toJson(orgId));
          gs_json_object_data.method = "getCustomer";
          gs_json_object_data.interfaceName = "core.sales.ISalesManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Manager for handling sales for getshop.
     */
     public JsonElement getEvent(Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "getEvent";
          gs_json_object_data.interfaceName = "core.sales.ISalesManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Manager for handling sales for getshop.
     */
     public JsonElement getEventsForCustomer(Object orgId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orgId",new Gson().toJson(orgId));
          gs_json_object_data.method = "getEventsForCustomer";
          gs_json_object_data.interfaceName = "core.sales.ISalesManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Manager for handling sales for getshop.
     */
     public JsonElement getEventsForDay(Object day)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("day",new Gson().toJson(day));
          gs_json_object_data.method = "getEventsForDay";
          gs_json_object_data.interfaceName = "core.sales.ISalesManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Manager for handling sales for getshop.
     */
     public JsonElement getLatestCustomer()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getLatestCustomer";
          gs_json_object_data.interfaceName = "core.sales.ISalesManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Manager for handling sales for getshop.
     */
     public JsonElement getLatestEvent()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getLatestEvent";
          gs_json_object_data.interfaceName = "core.sales.ISalesManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Manager for handling sales for getshop.
     */
     public void saveCustomer(Object customer)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("customer",new Gson().toJson(customer));
          gs_json_object_data.method = "saveCustomer";
          gs_json_object_data.interfaceName = "core.sales.ISalesManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Manager for handling sales for getshop.
     */
     public void saveEvent(Object event)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("event",new Gson().toJson(event));
          gs_json_object_data.method = "saveEvent";
          gs_json_object_data.interfaceName = "core.sales.ISalesManager";
          String result = transport.send(gs_json_object_data);
     }

}
