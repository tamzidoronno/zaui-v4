package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIStoreApplicationInstancePool {

      public Communicator transport;

      public APIStoreApplicationInstancePool(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public JsonElement createNewInstance(Object applicationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("applicationId",new Gson().toJson(applicationId));
          gs_json_object_data.method = "createNewInstance";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationInstancePool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement createNewInstanceWithId(Object applicationId, Object instanceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("applicationId",new Gson().toJson(applicationId));
          gs_json_object_data.args.put("instanceId",new Gson().toJson(instanceId));
          gs_json_object_data.method = "createNewInstanceWithId";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationInstancePool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getApplicationInstance(Object applicationInstanceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("applicationInstanceId",new Gson().toJson(applicationInstanceId));
          gs_json_object_data.method = "getApplicationInstance";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationInstancePool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getApplicationInstanceWithModule(Object applicationInstanceId, Object moduleName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("applicationInstanceId",new Gson().toJson(applicationInstanceId));
          gs_json_object_data.args.put("moduleName",new Gson().toJson(moduleName));
          gs_json_object_data.method = "getApplicationInstanceWithModule";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationInstancePool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getApplicationInstances(Object applicationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("applicationId",new Gson().toJson(applicationId));
          gs_json_object_data.method = "getApplicationInstances";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationInstancePool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement setApplicationSettings(Object settings)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("settings",new Gson().toJson(settings));
          gs_json_object_data.method = "setApplicationSettings";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationInstancePool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
