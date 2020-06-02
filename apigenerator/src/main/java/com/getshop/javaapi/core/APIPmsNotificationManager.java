package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIPmsNotificationManager {

      public Communicator transport;

      public APIPmsNotificationManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author boggi
     */
     public JsonElement createPreview(String gs_multiLevelName, Object key, Object message)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("key",new Gson().toJson(key));
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.method = "createPreview";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsNotificationManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public void deleteMessage(String gs_multiLevelName, Object messageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("messageId",new Gson().toJson(messageId));
          gs_json_object_data.method = "deleteMessage";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsNotificationManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author boggi
     */
     public JsonElement doFormationOnMessage(String gs_multiLevelName, Object msg, Object bookingId, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("msg",new Gson().toJson(msg));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "doFormationOnMessage";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsNotificationManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getAllMessages(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "getAllMessages";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsNotificationManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getLanguagesForMessage(String gs_multiLevelName, Object key, Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("key",new Gson().toJson(key));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "getLanguagesForMessage";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsNotificationManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getMessage(String gs_multiLevelName, Object messageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("messageId",new Gson().toJson(messageId));
          gs_json_object_data.method = "getMessage";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsNotificationManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getPrefixesForMessage(String gs_multiLevelName, Object key, Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("key",new Gson().toJson(key));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "getPrefixesForMessage";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsNotificationManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getSpecificMessage(String gs_multiLevelName, Object key, Object booking, Object room, Object type, Object prefix)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("key",new Gson().toJson(key));
          gs_json_object_data.args.put("booking",new Gson().toJson(booking));
          gs_json_object_data.args.put("room",new Gson().toJson(room));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.args.put("prefix",new Gson().toJson(prefix));
          gs_json_object_data.method = "getSpecificMessage";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsNotificationManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public void saveMessage(String gs_multiLevelName, Object msg)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("msg",new Gson().toJson(msg));
          gs_json_object_data.method = "saveMessage";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsNotificationManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author boggi
     */
     public void sendEmail(String gs_multiLevelName, Object msg, Object email, Object bookingId, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("msg",new Gson().toJson(msg));
          gs_json_object_data.args.put("email",new Gson().toJson(email));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "sendEmail";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsNotificationManager";
          String result = transport.send(gs_json_object_data);
     }

}
