package com.getshop.javaapi.pos;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIApiManager {

      public Communicator transport;

      public APIApiManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public void activateSystem(Object systemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("systemId",new Gson().toJson(systemId));
          gs_json_object_data.method = "activateSystem";
          gs_json_object_data.interfaceName = "core.api.IApiManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement addLockToKey(Object systemToken, Object customerToken, Object keyId, Object lockId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("systemToken",new Gson().toJson(systemToken));
          gs_json_object_data.args.put("customerToken",new Gson().toJson(customerToken));
          gs_json_object_data.args.put("keyId",new Gson().toJson(keyId));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.method = "addLockToKey";
          gs_json_object_data.interfaceName = "core.api.IApiManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement changeCodeForKey(Object systemToken, Object customerToken, Object keyId, Object code)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("systemToken",new Gson().toJson(systemToken));
          gs_json_object_data.args.put("customerToken",new Gson().toJson(customerToken));
          gs_json_object_data.args.put("keyId",new Gson().toJson(keyId));
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.method = "changeCodeForKey";
          gs_json_object_data.interfaceName = "core.api.IApiManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement createKey(Object systemToken, Object customerToken, Object name)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("systemToken",new Gson().toJson(systemToken));
          gs_json_object_data.args.put("customerToken",new Gson().toJson(customerToken));
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.method = "createKey";
          gs_json_object_data.interfaceName = "core.api.IApiManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void deactivateSystem(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deactivateSystem";
          gs_json_object_data.interfaceName = "core.api.IApiManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteKey(Object systemToken, Object customerToken, Object keyId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("systemToken",new Gson().toJson(systemToken));
          gs_json_object_data.args.put("customerToken",new Gson().toJson(customerToken));
          gs_json_object_data.args.put("keyId",new Gson().toJson(keyId));
          gs_json_object_data.method = "deleteKey";
          gs_json_object_data.interfaceName = "core.api.IApiManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getActivatedSystems()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getActivatedSystems";
          gs_json_object_data.interfaceName = "core.api.IApiManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllSystems()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllSystems";
          gs_json_object_data.interfaceName = "core.api.IApiManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getApiLocks(Object systemToken, Object customerToken)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("systemToken",new Gson().toJson(systemToken));
          gs_json_object_data.args.put("customerToken",new Gson().toJson(customerToken));
          gs_json_object_data.method = "getApiLocks";
          gs_json_object_data.interfaceName = "core.api.IApiManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getApiSystem(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getApiSystem";
          gs_json_object_data.interfaceName = "core.api.IApiManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement removeLockFromKey(Object systemToken, Object customerToken, Object keyId, Object lockId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("systemToken",new Gson().toJson(systemToken));
          gs_json_object_data.args.put("customerToken",new Gson().toJson(customerToken));
          gs_json_object_data.args.put("keyId",new Gson().toJson(keyId));
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.method = "removeLockFromKey";
          gs_json_object_data.interfaceName = "core.api.IApiManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
