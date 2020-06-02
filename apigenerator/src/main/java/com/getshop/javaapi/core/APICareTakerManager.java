package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APICareTakerManager {

      public Communicator transport;

      public APICareTakerManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Caretaker action for hotel.
     * @author boggi
     */
     public void addRepeatingTask(String gs_multiLevelName, Object repeatingData)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("repeatingData",new Gson().toJson(repeatingData));
          gs_json_object_data.method = "addRepeatingTask";
          gs_json_object_data.interfaceName = "core.pmsmanager.ICareTakerManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Caretaker action for hotel.
     * @author boggi
     */
     public void assignTask(String gs_multiLevelName, Object taskId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("taskId",new Gson().toJson(taskId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "assignTask";
          gs_json_object_data.interfaceName = "core.pmsmanager.ICareTakerManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Caretaker action for hotel.
     * @author boggi
     */
     public void checkForTasksToCreate(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "checkForTasksToCreate";
          gs_json_object_data.interfaceName = "core.pmsmanager.ICareTakerManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Caretaker action for hotel.
     * @author boggi
     */
     public void completeTask(String gs_multiLevelName, Object taskId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("taskId",new Gson().toJson(taskId));
          gs_json_object_data.method = "completeTask";
          gs_json_object_data.interfaceName = "core.pmsmanager.ICareTakerManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Caretaker action for hotel.
     * @author boggi
     */
     public void deleteRepeatingTask(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteRepeatingTask";
          gs_json_object_data.interfaceName = "core.pmsmanager.ICareTakerManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Caretaker action for hotel.
     * @author boggi
     */
     public JsonElement getCareTakerList(String gs_multiLevelName, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getCareTakerList";
          gs_json_object_data.interfaceName = "core.pmsmanager.ICareTakerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Caretaker action for hotel.
     * @author boggi
     */
     public JsonElement getCaretakers(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "getCaretakers";
          gs_json_object_data.interfaceName = "core.pmsmanager.ICareTakerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Caretaker action for hotel.
     * @author boggi
     */
     public JsonElement getRepeatingTasks(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "getRepeatingTasks";
          gs_json_object_data.interfaceName = "core.pmsmanager.ICareTakerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Caretaker action for hotel.
     * @author boggi
     */
     public JsonElement getRoomOverview(String gs_multiLevelName, Object defectsOnly)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("defectsOnly",new Gson().toJson(defectsOnly));
          gs_json_object_data.method = "getRoomOverview";
          gs_json_object_data.interfaceName = "core.pmsmanager.ICareTakerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
