package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIWebManager {

      public Communicator transport;

      public APIWebManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author hung
     */
     public JsonElement htmlGet(Object url)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("url",new Gson().toJson(url));
          gs_json_object_data.method = "htmlGet";
          gs_json_object_data.interfaceName = "core.webmanager.IWebManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author hung
     */
     public JsonElement htmlGetJson(Object url)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("url",new Gson().toJson(url));
          gs_json_object_data.method = "htmlGetJson";
          gs_json_object_data.interfaceName = "core.webmanager.IWebManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author hung
     */
     public JsonElement htmlPost(Object url, Object data, Object jsonPost, Object encoding)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("url",new Gson().toJson(url));
          gs_json_object_data.args.put("data",new Gson().toJson(data));
          gs_json_object_data.args.put("jsonPost",new Gson().toJson(jsonPost));
          gs_json_object_data.args.put("encoding",new Gson().toJson(encoding));
          gs_json_object_data.method = "htmlPost";
          gs_json_object_data.interfaceName = "core.webmanager.IWebManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author hung
     */
     public JsonElement htmlPostBasicAuth(Object url, Object data, Object jsonPost, Object encoding, Object auth)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("url",new Gson().toJson(url));
          gs_json_object_data.args.put("data",new Gson().toJson(data));
          gs_json_object_data.args.put("jsonPost",new Gson().toJson(jsonPost));
          gs_json_object_data.args.put("encoding",new Gson().toJson(encoding));
          gs_json_object_data.args.put("auth",new Gson().toJson(auth));
          gs_json_object_data.method = "htmlPostBasicAuth";
          gs_json_object_data.interfaceName = "core.webmanager.IWebManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author hung
     */
     public JsonElement htmlPostJson(Object url, Object data, Object encoding)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("url",new Gson().toJson(url));
          gs_json_object_data.args.put("data",new Gson().toJson(data));
          gs_json_object_data.args.put("encoding",new Gson().toJson(encoding));
          gs_json_object_data.method = "htmlPostJson";
          gs_json_object_data.interfaceName = "core.webmanager.IWebManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
