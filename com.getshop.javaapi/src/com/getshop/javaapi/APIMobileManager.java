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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("tokenId",new Gson().toJson(tokenId));
          data.method = "clearBadged";
          data.interfaceName = "core.mobilemanager.IMobileManager";
          String result = transport.send(data);
     }

     /**
     * Register a token to the system.
     * This token is later on used for sending messages
     * back to the unit.
     * s
     * @param token
     */
     public void registerToken(com.thundashop.core.mobilemanager.data.Token token)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("token",new Gson().toJson(token));
          data.method = "registerToken";
          data.interfaceName = "core.mobilemanager.IMobileManager";
          String result = transport.send(data);
     }

     /**
     * Sends the message to all registered units.
     *
     * @param message
     */
     public void sendMessageToAll(java.lang.String message)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("message",new Gson().toJson(message));
          data.method = "sendMessageToAll";
          data.interfaceName = "core.mobilemanager.IMobileManager";
          String result = transport.send(data);
     }

     /**
     * Sends the message to all units that are registered as test units.
     *
     * @param message
     */
     public void sendMessageToAllTestUnits(java.lang.String message)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("message",new Gson().toJson(message));
          data.method = "sendMessageToAllTestUnits";
          data.interfaceName = "core.mobilemanager.IMobileManager";
          String result = transport.send(data);
     }

}
