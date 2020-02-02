package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIDBBackupManager {

      public Communicator transport;

      public APIDBBackupManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public JsonElement getChanges(Object className)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("className",new Gson().toJson(className));
          gs_json_object_data.method = "getChanges";
          gs_json_object_data.interfaceName = "core.dbbackupmanager.IDBBackupManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getChangesById(Object className, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("className",new Gson().toJson(className));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getChangesById";
          gs_json_object_data.interfaceName = "core.dbbackupmanager.IDBBackupManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getDiff(Object className, Object id1, Object id2)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("className",new Gson().toJson(className));
          gs_json_object_data.args.put("id1",new Gson().toJson(id1));
          gs_json_object_data.args.put("id2",new Gson().toJson(id2));
          gs_json_object_data.method = "getDiff";
          gs_json_object_data.interfaceName = "core.dbbackupmanager.IDBBackupManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
