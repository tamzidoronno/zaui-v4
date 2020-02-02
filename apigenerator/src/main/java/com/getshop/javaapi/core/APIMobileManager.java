package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIMobileManager {

      public Communicator transport;

      public APIMobileManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Clears the badged number.
     *
     * @param tokenId
     */
     public void clearBadged(Object tokenId)  throws Exception  {
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
     public void registerToken(Object token)  throws Exception  {
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
     public void sendMessageToAll(Object message)  throws Exception  {
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
     public void sendMessageToAllTestUnits(Object message)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.method = "sendMessageToAllTestUnits";
          gs_json_object_data.interfaceName = "core.mobilemanager.IMobileManager";
          String result = transport.send(gs_json_object_data);
     }

}
