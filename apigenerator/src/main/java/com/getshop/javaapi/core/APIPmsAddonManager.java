package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIPmsAddonManager {

      public Communicator transport;

      public APIPmsAddonManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author boggi
     */
     public void addProductToGroup(String gs_multiLevelName, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "addProductToGroup";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsAddonManager";
          String result = transport.send(gs_json_object_data);
     }

}
