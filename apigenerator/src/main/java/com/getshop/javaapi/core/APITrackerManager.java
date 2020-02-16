package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APITrackerManager {

      public Communicator transport;

      public APITrackerManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public JsonElement getActivities(Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getActivities";
          gs_json_object_data.interfaceName = "core.trackermanager.ITrackerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void logTracking(Object applicationName, Object type, Object value, Object textDescription)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("applicationName",new Gson().toJson(applicationName));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.args.put("value",new Gson().toJson(value));
          gs_json_object_data.args.put("textDescription",new Gson().toJson(textDescription));
          gs_json_object_data.method = "logTracking";
          gs_json_object_data.interfaceName = "core.trackermanager.ITrackerManager";
          String result = transport.send(gs_json_object_data);
     }

}
