package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIDibsManager {

      public Communicator transport;

      public APIDibsManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Dibs management.
     */
     public JsonElement checkForOrdersToCapture()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "checkForOrdersToCapture";
          gs_json_object_data.interfaceName = "core.dibs.IDibsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
