package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APISendRegningManager {

      public Communicator transport;

      public APISendRegningManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author boggi
     */
     public JsonElement sendOrder(Object orderId, Object email)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("email",new Gson().toJson(email));
          gs_json_object_data.method = "sendOrder";
          gs_json_object_data.interfaceName = "core.sendregning.ISendRegningManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
