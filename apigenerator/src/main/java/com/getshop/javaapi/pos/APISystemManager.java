package com.getshop.javaapi.pos;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APISystemManager {

      public Communicator transport;

      public APISystemManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public void connectSystem(Object token, Object storeId, Object cluster, Object systemType, Object name)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("token",new Gson().toJson(token));
          gs_json_object_data.args.put("storeId",new Gson().toJson(storeId));
          gs_json_object_data.args.put("cluster",new Gson().toJson(cluster));
          gs_json_object_data.args.put("systemType",new Gson().toJson(systemType));
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.method = "connectSystem";
          gs_json_object_data.interfaceName = "core.system.ISystemManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllSystems()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllSystems";
          gs_json_object_data.interfaceName = "core.system.ISystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement loginToSystem(Object systemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("systemId",new Gson().toJson(systemId));
          gs_json_object_data.method = "loginToSystem";
          gs_json_object_data.interfaceName = "core.system.ISystemManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
