package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIDoorManager {

      public Communicator transport;

      public APIDoorManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * communication with the arx server.
     *
     * @author boggi
     */
     public JsonElement addCard(String gs_multiLevelName, Object personId, Object card)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("personId",new Gson().toJson(personId));
          gs_json_object_data.args.put("card",new Gson().toJson(card));
          gs_json_object_data.method = "addCard";
          gs_json_object_data.interfaceName = "core.arx.IDoorManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * communication with the arx server.
     *
     * @author boggi
     */
     public void clearDoorCache(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "clearDoorCache";
          gs_json_object_data.interfaceName = "core.arx.IDoorManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * communication with the arx server.
     *
     * @author boggi
     */
     public void closeAllForTheDay(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "closeAllForTheDay";
          gs_json_object_data.interfaceName = "core.arx.IDoorManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * communication with the arx server.
     *
     * @author boggi
     */
     public void doorAction(String gs_multiLevelName, Object externalId, Object state)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("externalId",new Gson().toJson(externalId));
          gs_json_object_data.args.put("state",new Gson().toJson(state));
          gs_json_object_data.method = "doorAction";
          gs_json_object_data.interfaceName = "core.arx.IDoorManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * communication with the arx server.
     *
     * @author boggi
     */
     public JsonElement generateDoorLogForAllDoorsFromResult(String gs_multiLevelName, Object resultFromArx)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("resultFromArx",new Gson().toJson(resultFromArx));
          gs_json_object_data.method = "generateDoorLogForAllDoorsFromResult";
          gs_json_object_data.interfaceName = "core.arx.IDoorManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * communication with the arx server.
     *
     * @author boggi
     */
     public JsonElement getAllAccessCategories(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getAllAccessCategories";
          gs_json_object_data.interfaceName = "core.arx.IDoorManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * communication with the arx server.
     *
     * @author boggi
     */
     public JsonElement getAllDoors(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getAllDoors";
          gs_json_object_data.interfaceName = "core.arx.IDoorManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * communication with the arx server.
     *
     * @author boggi
     */
     public JsonElement getAllPersons(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getAllPersons";
          gs_json_object_data.interfaceName = "core.arx.IDoorManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * communication with the arx server.
     *
     * @author boggi
     */
     public JsonElement getLogForAllDoor(String gs_multiLevelName, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getLogForAllDoor";
          gs_json_object_data.interfaceName = "core.arx.IDoorManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * communication with the arx server.
     *
     * @author boggi
     */
     public JsonElement getLogForDoor(String gs_multiLevelName, Object externalId, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("externalId",new Gson().toJson(externalId));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getLogForDoor";
          gs_json_object_data.interfaceName = "core.arx.IDoorManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * communication with the arx server.
     *
     * @author boggi
     */
     public JsonElement getMasterCodes(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getMasterCodes";
          gs_json_object_data.interfaceName = "core.arx.IDoorManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * communication with the arx server.
     *
     * @author boggi
     */
     public JsonElement getPerson(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getPerson";
          gs_json_object_data.interfaceName = "core.arx.IDoorManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * communication with the arx server.
     *
     * @author boggi
     */
     public JsonElement pmsDoorAction(String gs_multiLevelName, Object code, Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "pmsDoorAction";
          gs_json_object_data.interfaceName = "core.arx.IDoorManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * communication with the arx server.
     *
     * @author boggi
     */
     public void saveMasterCodes(String gs_multiLevelName, Object masterCodes)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("masterCodes",new Gson().toJson(masterCodes));
          gs_json_object_data.method = "saveMasterCodes";
          gs_json_object_data.interfaceName = "core.arx.IDoorManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * communication with the arx server.
     *
     * @author boggi
     */
     public JsonElement updatePerson(String gs_multiLevelName, Object person)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("person",new Gson().toJson(person));
          gs_json_object_data.method = "updatePerson";
          gs_json_object_data.interfaceName = "core.arx.IDoorManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
