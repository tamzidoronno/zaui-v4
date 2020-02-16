package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIAmestoManager {

      public Communicator transport;

      public APIAmestoManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author hung
     */
     public void syncAllCostumers(Object hostname)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("hostname",new Gson().toJson(hostname));
          gs_json_object_data.method = "syncAllCostumers";
          gs_json_object_data.interfaceName = "core.amesto.IAmestoManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author hung
     */
     public void syncAllOrders(Object hostname)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("hostname",new Gson().toJson(hostname));
          gs_json_object_data.method = "syncAllOrders";
          gs_json_object_data.interfaceName = "core.amesto.IAmestoManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author hung
     */
     public void syncAllStockQuantity(Object hostname)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("hostname",new Gson().toJson(hostname));
          gs_json_object_data.method = "syncAllStockQuantity";
          gs_json_object_data.interfaceName = "core.amesto.IAmestoManager";
          String result = transport.send(gs_json_object_data);
     }

}
