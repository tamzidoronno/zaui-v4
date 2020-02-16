package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIScormManager {

      public Communicator transport;

      public APIScormManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public void deleteScormPackage(Object packageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("packageId",new Gson().toJson(packageId));
          gs_json_object_data.method = "deleteScormPackage";
          gs_json_object_data.interfaceName = "core.scormmanager.IScormManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllPackages()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllPackages";
          gs_json_object_data.interfaceName = "core.scormmanager.IScormManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getMandatoryPackages(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getMandatoryPackages";
          gs_json_object_data.interfaceName = "core.scormmanager.IScormManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getMyScorm(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getMyScorm";
          gs_json_object_data.interfaceName = "core.scormmanager.IScormManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getPackage(Object packageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("packageId",new Gson().toJson(packageId));
          gs_json_object_data.method = "getPackage";
          gs_json_object_data.interfaceName = "core.scormmanager.IScormManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getScormCertificateContent(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getScormCertificateContent";
          gs_json_object_data.interfaceName = "core.scormmanager.IScormManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getScormForCurrentUser(Object scormId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("scormId",new Gson().toJson(scormId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getScormForCurrentUser";
          gs_json_object_data.interfaceName = "core.scormmanager.IScormManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement needUpdate(Object username, Object scormid, Object isCompleted, Object isPassed, Object isFailed)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("username",new Gson().toJson(username));
          gs_json_object_data.args.put("scormid",new Gson().toJson(scormid));
          gs_json_object_data.args.put("isCompleted",new Gson().toJson(isCompleted));
          gs_json_object_data.args.put("isPassed",new Gson().toJson(isPassed));
          gs_json_object_data.args.put("isFailed",new Gson().toJson(isFailed));
          gs_json_object_data.method = "needUpdate";
          gs_json_object_data.interfaceName = "core.scormmanager.IScormManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void saveScormCertificateContent(Object content)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("content",new Gson().toJson(content));
          gs_json_object_data.method = "saveScormCertificateContent";
          gs_json_object_data.interfaceName = "core.scormmanager.IScormManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveSetup(Object scormPackage)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("scormPackage",new Gson().toJson(scormPackage));
          gs_json_object_data.method = "saveSetup";
          gs_json_object_data.interfaceName = "core.scormmanager.IScormManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void syncMoodle()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "syncMoodle";
          gs_json_object_data.interfaceName = "core.scormmanager.IScormManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void updateResult(Object scorm)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("scorm",new Gson().toJson(scorm));
          gs_json_object_data.method = "updateResult";
          gs_json_object_data.interfaceName = "core.scormmanager.IScormManager";
          String result = transport.send(gs_json_object_data);
     }

}
