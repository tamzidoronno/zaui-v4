package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APITimeRegisteringManager {

      public Communicator transport;

      public APITimeRegisteringManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Time registering management system.<br>
     */
     public void deleteTimeUnsecure(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteTimeUnsecure";
          gs_json_object_data.interfaceName = "core.timeregisteringmanager.ITimeRegisteringManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Time registering management system.<br>
     */
     public JsonElement getAllTimesRegistered()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllTimesRegistered";
          gs_json_object_data.interfaceName = "core.timeregisteringmanager.ITimeRegisteringManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Time registering management system.<br>
     */
     public JsonElement getMyHours()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getMyHours";
          gs_json_object_data.interfaceName = "core.timeregisteringmanager.ITimeRegisteringManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Time registering management system.<br>
     */
     public JsonElement getRegisteredHoursForUser(Object userId, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getRegisteredHoursForUser";
          gs_json_object_data.interfaceName = "core.timeregisteringmanager.ITimeRegisteringManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Time registering management system.<br>
     */
     public void registerTime(Object start, Object end, Object comment)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("comment",new Gson().toJson(comment));
          gs_json_object_data.method = "registerTime";
          gs_json_object_data.interfaceName = "core.timeregisteringmanager.ITimeRegisteringManager";
          String result = transport.send(gs_json_object_data);
     }

}
