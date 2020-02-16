package com.getshop.javaapi.pos;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIPrintManager {

      public Communicator transport;

      public APIPrintManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * This function is multithreaded
     * It will not cause the rest of the system to hang and
     * is though of being called multiple times during a second.
     *
     * Designed for invoiking 4 times a second.
     *
     * @param printerId
     * @return
     */
     public JsonElement getPrintJobs(Object printerId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("printerId",new Gson().toJson(printerId));
          gs_json_object_data.method = "getPrintJobs";
          gs_json_object_data.interfaceName = "core.printmanager.IPrintManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
