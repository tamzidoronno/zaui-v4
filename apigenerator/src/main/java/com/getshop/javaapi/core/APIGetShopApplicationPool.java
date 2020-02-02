package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIGetShopApplicationPool {

      public Communicator transport;

      public APIGetShopApplicationPool(Communicator transport){
           this.transport = transport;
      }

     /**
     * Save an application
     *
     * @param application
     */
     public void deleteApplication(Object applicationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("applicationId",new Gson().toJson(applicationId));
          gs_json_object_data.method = "deleteApplication";
          gs_json_object_data.interfaceName = "core.applications.IGetShopApplicationPool";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Get an application by an given id.
     *
     * @param applicationId
     * @return
     */
     public JsonElement get(Object applicationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("applicationId",new Gson().toJson(applicationId));
          gs_json_object_data.method = "get";
          gs_json_object_data.interfaceName = "core.applications.IGetShopApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a list of all available applications.
     *
     * @return
     */
     public JsonElement getApplications()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getApplications";
          gs_json_object_data.interfaceName = "core.applications.IGetShopApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Save an application
     *
     * @param application
     */
     public void saveApplication(Object application)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("application",new Gson().toJson(application));
          gs_json_object_data.method = "saveApplication";
          gs_json_object_data.interfaceName = "core.applications.IGetShopApplicationPool";
          String result = transport.send(gs_json_object_data);
     }

}
