package com.getshop.javaapi.pos;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIGetShopCentral {

      public Communicator transport;

      public APIGetShopCentral(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public JsonElement getToken(Object username, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("username",new Gson().toJson(username));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "getToken";
          gs_json_object_data.interfaceName = "core.central.IGetShopCentral";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement login(Object token, Object user)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("token",new Gson().toJson(token));
          gs_json_object_data.args.put("user",new Gson().toJson(user));
          gs_json_object_data.method = "login";
          gs_json_object_data.interfaceName = "core.central.IGetShopCentral";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement validateAccessToken(Object token)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("token",new Gson().toJson(token));
          gs_json_object_data.method = "validateAccessToken";
          gs_json_object_data.interfaceName = "core.central.IGetShopCentral";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
