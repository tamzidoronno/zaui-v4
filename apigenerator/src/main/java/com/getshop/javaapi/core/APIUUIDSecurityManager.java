package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIUUIDSecurityManager {

      public Communicator transport;

      public APIUUIDSecurityManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Manager for handling security for uuids in getshop.
     */
     public void grantAccess(Object userId, Object uuid, Object read, Object write)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("uuid",new Gson().toJson(uuid));
          gs_json_object_data.args.put("read",new Gson().toJson(read));
          gs_json_object_data.args.put("write",new Gson().toJson(write));
          gs_json_object_data.method = "grantAccess";
          gs_json_object_data.interfaceName = "core.uuidsecuritymanager.IUUIDSecurityManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Manager for handling security for uuids in getshop.
     */
     public JsonElement hasAccess(Object uuid, Object read, Object write)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("uuid",new Gson().toJson(uuid));
          gs_json_object_data.args.put("read",new Gson().toJson(read));
          gs_json_object_data.args.put("write",new Gson().toJson(write));
          gs_json_object_data.method = "hasAccess";
          gs_json_object_data.interfaceName = "core.uuidsecuritymanager.IUUIDSecurityManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
