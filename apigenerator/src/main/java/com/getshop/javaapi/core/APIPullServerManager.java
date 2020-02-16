package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIPullServerManager {

      public Communicator transport;

      public APIPullServerManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public JsonElement getPullMessages(Object keyId, Object storeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("keyId",new Gson().toJson(keyId));
          gs_json_object_data.args.put("storeId",new Gson().toJson(storeId));
          gs_json_object_data.method = "getPullMessages";
          gs_json_object_data.interfaceName = "core.pullserver.IPullServerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void markMessageAsReceived(Object messageId, Object storeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("messageId",new Gson().toJson(messageId));
          gs_json_object_data.args.put("storeId",new Gson().toJson(storeId));
          gs_json_object_data.method = "markMessageAsReceived";
          gs_json_object_data.interfaceName = "core.pullserver.IPullServerManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void savePullMessage(Object pullMessage)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pullMessage",new Gson().toJson(pullMessage));
          gs_json_object_data.method = "savePullMessage";
          gs_json_object_data.interfaceName = "core.pullserver.IPullServerManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void triggerCheckForPullMessage()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "triggerCheckForPullMessage";
          gs_json_object_data.interfaceName = "core.pullserver.IPullServerManager";
          String result = transport.send(gs_json_object_data);
     }

}
