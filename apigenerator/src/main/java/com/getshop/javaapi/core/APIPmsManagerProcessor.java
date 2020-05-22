package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIPmsManagerProcessor {

      public Communicator transport;

      public APIPmsManagerProcessor(Communicator transport){
           this.transport = transport;
      }

     /**
     * Property management system processor.<br>
     */
     public void doProcessing(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "doProcessing";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManagerProcessor";
          String result = transport.send(gs_json_object_data);
     }

}
