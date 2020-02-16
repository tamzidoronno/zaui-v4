package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIOAuthManager {

      public Communicator transport;

      public APIOAuthManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author boggi
     */
     public JsonElement getCurrentOAuthSession(Object oauthSessionId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("oauthSessionId",new Gson().toJson(oauthSessionId));
          gs_json_object_data.method = "getCurrentOAuthSession";
          gs_json_object_data.interfaceName = "core.oauthmanager.IOAuthManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement startNewOAuthSession(Object authAddress, Object clientId, Object scope, Object clientSecretId, Object tokenAddress)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("authAddress",new Gson().toJson(authAddress));
          gs_json_object_data.args.put("clientId",new Gson().toJson(clientId));
          gs_json_object_data.args.put("scope",new Gson().toJson(scope));
          gs_json_object_data.args.put("clientSecretId",new Gson().toJson(clientSecretId));
          gs_json_object_data.args.put("tokenAddress",new Gson().toJson(tokenAddress));
          gs_json_object_data.method = "startNewOAuthSession";
          gs_json_object_data.interfaceName = "core.oauthmanager.IOAuthManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
