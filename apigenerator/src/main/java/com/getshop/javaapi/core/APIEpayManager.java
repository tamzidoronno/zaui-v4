package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIEpayManager {

      public Communicator transport;

      public APIEpayManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Bambora payment management.
     */
     public JsonElement checkForOrdersToCapture()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "checkForOrdersToCapture";
          gs_json_object_data.interfaceName = "core.epay.IEpayManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
