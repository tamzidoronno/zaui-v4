package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIComfortManager {

      public Communicator transport;

      public APIComfortManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author pal
     */
     public void createState(Object name)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.method = "createState";
          gs_json_object_data.interfaceName = "core.comfortmanager.IComfortManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author pal
     */
     public void deleteState(Object stateId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("stateId",new Gson().toJson(stateId));
          gs_json_object_data.method = "deleteState";
          gs_json_object_data.interfaceName = "core.comfortmanager.IComfortManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author pal
     */
     public JsonElement getAllLogEntries()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllLogEntries";
          gs_json_object_data.interfaceName = "core.comfortmanager.IComfortManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author pal
     */
     public JsonElement getAllStates()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllStates";
          gs_json_object_data.interfaceName = "core.comfortmanager.IComfortManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author pal
     */
     public JsonElement getComfortRoom(Object bookingItemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("bookingItemId",new Gson().toJson(bookingItemId));
          gs_json_object_data.method = "getComfortRoom";
          gs_json_object_data.interfaceName = "core.comfortmanager.IComfortManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author pal
     */
     public JsonElement getState(Object stateId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("stateId",new Gson().toJson(stateId));
          gs_json_object_data.method = "getState";
          gs_json_object_data.interfaceName = "core.comfortmanager.IComfortManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author pal
     */
     public void saveComfortRoom(Object room)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("room",new Gson().toJson(room));
          gs_json_object_data.method = "saveComfortRoom";
          gs_json_object_data.interfaceName = "core.comfortmanager.IComfortManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author pal
     */
     public void saveState(Object state)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("state",new Gson().toJson(state));
          gs_json_object_data.method = "saveState";
          gs_json_object_data.interfaceName = "core.comfortmanager.IComfortManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author pal
     */
     public void test()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "test";
          gs_json_object_data.interfaceName = "core.comfortmanager.IComfortManager";
          String result = transport.send(gs_json_object_data);
     }

}
