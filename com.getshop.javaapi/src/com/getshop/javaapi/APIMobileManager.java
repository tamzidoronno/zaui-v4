package com.getshop.javaapi;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
public class APIMobileManager {

      public Transporter transport;

      public APIMobileManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * Clears the badged number.
     *
     * @param tokenId
     */
     public void clearBadged(java.lang.String tokenId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tokenId",new Gson().toJson(tokenId));
          gs_json_object_data.method = "clearBadged";
          gs_json_object_data.interfaceName = "core.mobilemanager.IMobileManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Register a token to the system.
     * This token is later on used for sending messages
     * back to the unit.
     * s
     * @param token
     */
     public void registerToken(com.thundashop.core.mobilemanager.data.Token token)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("token",new Gson().toJson(token));
          gs_json_object_data.method = "registerToken";
          gs_json_object_data.interfaceName = "core.mobilemanager.IMobileManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Sends the message to all registered units.
     *
     * @param message
     */
     public void sendMessageToAll(java.lang.String message)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.method = "sendMessageToAll";
          gs_json_object_data.interfaceName = "core.mobilemanager.IMobileManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Sends the message to all units that are registered as test units.
     *
     * @param message
     */
     public void sendMessageToAllTestUnits(java.lang.String message)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.method = "sendMessageToAllTestUnits";
          gs_json_object_data.interfaceName = "core.mobilemanager.IMobileManager";
          String result = transport.send(gs_json_object_data);
     }

}
